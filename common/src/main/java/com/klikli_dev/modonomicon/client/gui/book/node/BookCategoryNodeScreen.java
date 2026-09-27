/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 * SPDX-FileCopyrightText: 2021 Authors of Arcana
 *
 * SPDX-License-Identifier: MIT
 */
package com.klikli_dev.modonomicon.client.gui.book.node;

import com.klikli_dev.modonomicon.api.events.EntryClickedEvent;
import com.klikli_dev.modonomicon.book.BookBackgroundRenderingMode;
import com.klikli_dev.modonomicon.book.BookCategory;
import com.klikli_dev.modonomicon.book.BookCategoryBackgroundParallaxLayer;
import com.klikli_dev.modonomicon.book.conditions.context.BookConditionEntryContext;
import com.klikli_dev.modonomicon.book.entries.BookEntry;
import com.klikli_dev.modonomicon.bookstate.BookServices;
import com.klikli_dev.modonomicon.bookstate.visual.CategoryVisualState;
import com.klikli_dev.modonomicon.client.gui.BookGuiManager;
import com.klikli_dev.modonomicon.client.gui.book.BookAddress;
import com.klikli_dev.modonomicon.client.gui.book.BookCategoryScreen;
import com.klikli_dev.modonomicon.client.gui.book.BookContentRenderer;
import com.klikli_dev.modonomicon.client.gui.book.entry.DirectEntryConnectionRenderer;
import com.klikli_dev.modonomicon.client.gui.book.entry.EntryConnectionRenderer;
import com.klikli_dev.modonomicon.client.gui.book.entry.EntryDisplayState;
import com.klikli_dev.modonomicon.client.gui.book.theme.GuiSprite;
import com.klikli_dev.modonomicon.client.gui.book.theme.NodeConnectionRendererType;
import com.klikli_dev.modonomicon.events.ModonomiconEvents;
import com.klikli_dev.modonomicon.platform.ClientServices;
import com.klikli_dev.modonomicon.util.TextRenderHelper;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;

import java.util.ArrayList;


public class BookCategoryNodeScreen implements BookCategoryScreen {
    public static final int ENTRY_GRID_SCALE = 30;
    public static final int ENTRY_GAP = 2;

    public static final int ENTRY_HEIGHT = 26;
    public static final int ENTRY_WIDTH = 26;

    /**
     * How far the top/bottom entry name background tucks under the entry badge, so the two look connected.
     * The badge is rendered after (and thus on top of) the background.
     * Side bars instead pass fully behind the badge, see {@link #ENTRY_NAME_STICK_THROUGH}.
     */
    public static final int ENTRY_NAME_OVERLAP = 5;
    /**
     * Extra shift of top/bottom bars into the badge beyond {@link #ENTRY_NAME_OVERLAP}:
     * the bottom bar 3px up, the top bar 3px down.
     */
    public static final int ENTRY_NAME_VERTICAL_SHIFT = 3;
    /**
     * How far a left/right entry name bar sticks out past the far edge of the badge.
     */
    public static final int ENTRY_NAME_STICK_THROUGH = 2;
    /**
     * Horizontal padding between the entry name text and the visible edge of its background.
     */
    public static final int ENTRY_NAME_PADDING_X = 5;
    /**
     * Vertical padding between the entry name text and the visible edge of its background.
     */
    public static final int ENTRY_NAME_PADDING_Y = 7;

    /**
     * Fallback entry name background if the theme does not provide one.
     * Matches the default theme texture, see BookNodeSettings.
     */
    private static final GuiSprite FALLBACK_ENTRY_NAME_BACKGROUND = new GuiSprite(
            Identifier.fromNamespaceAndPath("modonomicon", "modonomicon/themes/default/node/entry_backgrounds/name_background"), 200, 26);

    private final BookParentNodeScreen bookParentScreen;
    private final BookCategory category;
    private final DirectEntryConnectionRenderer directConnectionRenderer;
    private final EntryConnectionRenderer spriteConnectionRenderer;
    private float scrollX = 0;
    private float scrollY = 0;
    private boolean isScrolling;
    private float targetZoom;
    private float currentZoom;

    public BookCategoryNodeScreen(BookParentNodeScreen bookOverviewScreen, BookCategory category) {
        this.bookParentScreen = bookOverviewScreen;
        this.category = category;

        this.directConnectionRenderer = new DirectEntryConnectionRenderer(category.getBook().theme().node().settings().directConnections());
        this.spriteConnectionRenderer = new EntryConnectionRenderer(category.getBook().theme().node());

        this.targetZoom = 0.7f;
        this.currentZoom = this.targetZoom;
    }

    @Override
    public BookCategory getCategory() {
        return this.category;
    }

    public float getXOffset() {
        return ((this.bookParentScreen.getInnerWidth() / 2f) * (1 / this.currentZoom)) - this.scrollX / 2;
    }

    public float getYOffset() {
        return ((this.bookParentScreen.getInnerHeight() / 2f) * (1 / this.currentZoom)) - this.scrollY / 2;
    }

    public float getCurrentZoom() {
        return this.currentZoom;
    }

    public int getInnerX() {
        return this.bookParentScreen.getInnerX();
    }

    public int getInnerY() {
        return this.bookParentScreen.getInnerY();
    }

    public int getInnerWidth() {
        return this.bookParentScreen.getInnerWidth();
    }

    public int getInnerHeight() {
        return this.bookParentScreen.getInnerHeight();
    }

    public void render(GuiGraphicsExtractor guiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        if (ClientServices.CLIENT_CONFIG.enableSmoothZoom()) {
            float diff = this.targetZoom - this.currentZoom;
            this.currentZoom = this.currentZoom + Math.min(pPartialTick * (2 / 3f), 1) * diff;
        } else
            this.currentZoom = this.targetZoom;

        //GL Scissors to the inner frame area so entries do not stick out
        int innerX = this.bookParentScreen.getInnerX();
        int innerY = this.bookParentScreen.getInnerY();
        int innerWidth = this.bookParentScreen.getInnerWidth();
        int innerHeight = this.bookParentScreen.getInnerHeight();
        //the -1 are magic numbers to avoid an overflow of 1px.
        guiGraphics.enableScissor(innerX, innerY, innerX + innerWidth - 1, innerY + innerHeight - 1);
        this.renderEntries(guiGraphics, pMouseX, pMouseY);
        guiGraphics.disableScissor();
    }

    public void zoom(double delta) {
        float step = 1.2f;
        if ((delta < 0 && this.targetZoom > 0.5) || (delta > 0 && this.targetZoom < 1))
            this.targetZoom *= delta > 0 ? step : 1 / step;
        if (this.targetZoom > 1f)
            this.targetZoom = 1f;
    }

    public boolean mouseDragged(MouseButtonEvent event, double mouseX, double mouseY) {
        //Based on advancementsscreen
        //NB: since 26.3 mouse buttons are SDL 1-indexed (left = 1), so compare against
        //InputConstants instead of a raw button index (see the 26.3 migration primer).
        if (event.button() != InputConstants.MOUSE_BUTTON_LEFT) {
            this.isScrolling = false;
            return false;
        } else {
            if (!this.isScrolling) {
                this.isScrolling = true;
            } else {
                this.scroll(mouseX * 1.5, mouseY * 1.5);
            }
            return true;
        }
    }

    public boolean mouseClicked(MouseButtonEvent event, boolean isDoubleClick) {

        float xOffset = this.getXOffset();
        float yOffset = this.getYOffset();
        for (var entry : this.category.getEntries().values()) {
            var displayStyle = this.getEntryDisplayState(entry);

            if (this.isEntryHovered(entry, displayStyle, xOffset, yOffset, (int) event.x(), (int) event.y())) {

                var entryClickedEvent = new EntryClickedEvent(this.category.getBook().getId(), entry.getId(), event, displayStyle);
                //if event is canceled -> click was handled and we do not open the entry.
                if (ModonomiconEvents.client().entryClicked(entryClickedEvent)) {
                    return true;
                }

                //only if the entry is unlocked we open it
                if (displayStyle == EntryDisplayState.UNLOCKED) {
                    BookGuiManager.get().openEntry(entry, BookAddress.defaultFor(entry));
                    return true;
                }
            }
        }

        return false;
    }

    public void renderBackground(GuiGraphicsExtractor guiGraphics) {
        //based on the frame's total width and its thickness, calculate where the inner area starts
        int innerX = this.bookParentScreen.getInnerX();
        int innerY = this.bookParentScreen.getInnerY();

        //then calculate the corresponding inner area width/height so we don't draw out of the frame
        int innerWidth = this.bookParentScreen.getInnerWidth();
        int innerHeight = this.bookParentScreen.getInnerHeight();

        //non-tiling rendering modes map (a part of) the texture onto the background area with normalized uvs.
        //because of that the gui scale applies uniformly to the whole area, so unlike tiling these modes
        //look the same at any gui scale. by default (background_overscan > 1) the texture is rendered
        //larger than the area and pans with scrolling, creating a parallax effect.
        if (this.category.getBackgroundParallaxLayers().isEmpty()) {
            if (this.category.getBackgroundRenderingMode() == BookBackgroundRenderingMode.SCALE) {
                this.renderBackgroundStretched(guiGraphics, innerX, innerY, innerWidth, innerHeight);
                return;
            }
            if (this.category.getBackgroundRenderingMode() == BookBackgroundRenderingMode.FIT) {
                this.renderBackgroundCover(guiGraphics, innerX, innerY, innerWidth, innerHeight);
                return;
            }
        }

        //we do not use our static max_scroll here because it makes some issues, so we use the tex instead.
        int backgroundWidth = this.category.getBackgroundWidth();
        int backgroundHeight = this.category.getBackgroundHeight();
        final int MAX_SCROLL = Math.max(backgroundWidth, backgroundHeight);
        float backgroundTextureZoomMultiplier = this.category.getBackgroundTextureZoomMultiplier();


        // Adjust scale calculations to take into account actual texture size
        float xScale = MAX_SCROLL * 2.0f / ((float) MAX_SCROLL + this.bookParentScreen.getFrameThicknessW() - this.bookParentScreen.getFrameWidth());
        float yScale = MAX_SCROLL * 2.0f / ((float) MAX_SCROLL + this.bookParentScreen.getFrameThicknessH() - this.bookParentScreen.getFrameHeight());
        float scale = Math.max(xScale, yScale);
        float xOffset = xScale == scale ? 0 : (MAX_SCROLL - (innerWidth + MAX_SCROLL * 2.0f / scale)) / 2;
        float yOffset = yScale == scale ? 0 : (MAX_SCROLL - (innerHeight + MAX_SCROLL * 2.0f / scale)) / 2;

        //note we cannot translate -z here because even -1 immediately pushes us behind the scene -> not visible
        if (!this.category.getBackgroundParallaxLayers().isEmpty()) {
            this.category.getBackgroundParallaxLayers().forEach(layer -> {
                this.renderBackgroundParallaxLayer(guiGraphics, layer, innerX, innerY, innerWidth, innerHeight, this.scrollX, this.scrollY, scale, xOffset, yOffset, this.currentZoom, backgroundWidth, backgroundHeight, backgroundTextureZoomMultiplier);
            });
        } else {
            //for some reason on this one blit overload tex width and height are switched. It does correctly call the followup though, so we have to go along
            //force offset to int here to reduce difference to entry rendering which is pos based and thus int precision only
            guiGraphics.blit(RenderPipelines.GUI_TEXTURED, this.category.getBackground(), innerX, innerY,
                    (this.scrollX + MAX_SCROLL) / scale + xOffset,
                    (this.scrollY + MAX_SCROLL) / scale + yOffset,
                    innerWidth, innerHeight, (int) (backgroundHeight * backgroundTextureZoomMultiplier), (int) (backgroundWidth * backgroundTextureZoomMultiplier));

        }
    }

    public void renderBackgroundParallaxLayer(GuiGraphicsExtractor guiGraphics, BookCategoryBackgroundParallaxLayer layer, int x, int y, int width, int height, float scrollX, float scrollY, float parallax, float xOffset, float yOffset, float zoom, int backgroundWidth, int backgroundHeight, float backgroundTextureZoomMultiplier) {
        if (layer.getVanishZoom() == -1 || layer.getVanishZoom() > zoom) {
            if (layer.getRenderingMode() != BookBackgroundRenderingMode.REPEAT) {
                this.renderNonTilingParallaxLayer(guiGraphics, layer, x, y, width, height, scrollX, scrollY, backgroundWidth, backgroundHeight);
                return;
            }

            float parallax1 = parallax / layer.getSpeed();

            //for some reason on this one blit overload tex width and height are switched. It does correctly call the followup though, so we have to go along
            guiGraphics.blit(RenderPipelines.GUI_TEXTURED, layer.getBackground(), x, y,
                    (scrollX + this.getCategory().getMaxScrollX()) / parallax1 + xOffset,
                    (scrollY + this.getCategory().getMaxScrollY()) / parallax1 + yOffset,
                    width, height, (int) (backgroundHeight * backgroundTextureZoomMultiplier), (int) (backgroundWidth * backgroundTextureZoomMultiplier));
        }

    }

    /**
     * Renders a non-tiling parallax layer, sampling with normalized uvs so no tiling occurs at any gui scale.
     * Works like the tiled layers (scroll pans, speed differentiates layers, vanish zoom hides),
     * but the pan is clamped to an overdraw margin created by the layer overscan instead of wrapping.
     * Relative layer speeds are preserved by panning each layer proportionally to the fastest layer,
     * so the fastest layer traverses the full margin and nothing saturates.
     * The tiling layout corrections (x/y offset, texture zoom multiplier) do not apply here,
     * centering is handled by construction instead.
     */
    private void renderNonTilingParallaxLayer(GuiGraphicsExtractor guiGraphics, BookCategoryBackgroundParallaxLayer layer, int x, int y, int width, int height, float scrollX, float scrollY, int backgroundWidth, int backgroundHeight) {
        if (width <= 0 || height <= 0)
            return;

        int textureWidth = Math.max(1, backgroundWidth);
        int textureHeight = Math.max(1, backgroundHeight);

        //base section: full texture for scale, covering section for fit
        int baseU = 0;
        int baseV = 0;
        int baseSrcWidth = textureWidth;
        int baseSrcHeight = textureHeight;
        if (layer.getRenderingMode() == BookBackgroundRenderingMode.FIT) {
            int[] cover = coverSourceRect(textureWidth, textureHeight, width, height);
            baseU = cover[0];
            baseV = cover[1];
            baseSrcWidth = cover[2];
            baseSrcHeight = cover[3];
        }

        //shrink the sampled section around its center to create the overdraw margin (1.0 = no margin = fixed)
        float overscan = Math.max(1, layer.getOverscan());
        int srcWidth = Math.min(textureWidth, Math.max(1, Math.round(baseSrcWidth / overscan)));
        int srcHeight = Math.min(textureHeight, Math.max(1, Math.round(baseSrcHeight / overscan)));

        float scrollFractionX = 0;
        float scrollFractionY = 0;
        if (overscan > 1) {
            float maxSpeed = 0;
            for (var other : this.getCategory().getBackgroundParallaxLayers())
                maxSpeed = Math.max(maxSpeed, Math.abs(other.getSpeed()));
            if (maxSpeed == 0)
                maxSpeed = 1;
            if (this.getCategory().getMaxScrollX() > 0)
                scrollFractionX = Mth.clamp(scrollX / this.getCategory().getMaxScrollX() * layer.getSpeed() / maxSpeed, -1, 1);
            if (this.getCategory().getMaxScrollY() > 0)
                scrollFractionY = Mth.clamp(scrollY / this.getCategory().getMaxScrollY() * layer.getSpeed() / maxSpeed, -1, 1);
        }

        int uOffset = Mth.clamp(Math.round(baseU + (baseSrcWidth - srcWidth) / 2f + scrollFractionX * (textureWidth - srcWidth) / 2f), 0, textureWidth - srcWidth);
        int vOffset = Mth.clamp(Math.round(baseV + (baseSrcHeight - srcHeight) / 2f + scrollFractionY * (textureHeight - srcHeight) / 2f), 0, textureHeight - srcHeight);

        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, layer.getBackground(), x, y,
                uOffset, vOffset, width, height,
                srcWidth, srcHeight, textureWidth, textureHeight);
    }

    /**
     * Renders the category background texture stretched to exactly fill the background area,
     * ignoring the original aspect ratio.
     * The full texture is sampled with normalized uvs, so no tiling occurs at any gui scale.
     */
    private void renderBackgroundStretched(GuiGraphicsExtractor guiGraphics, int x, int y, int width, int height) {
        if (width <= 0 || height <= 0)
            return;

        int textureWidth = Math.max(1, this.category.getBackgroundWidth());
        int textureHeight = Math.max(1, this.category.getBackgroundHeight());

        this.renderBackgroundMapped(guiGraphics, x, y, width, height, 0, 0, textureWidth, textureHeight);
    }

    /**
     * Renders the category background texture uniformly scaled so that the entire background area is covered,
     * without distorting the texture. Parts of the texture may be cropped.
     * Only the covering part of the texture is sampled with normalized uvs, so no tiling occurs at any gui scale.
     */
    private void renderBackgroundCover(GuiGraphicsExtractor guiGraphics, int x, int y, int width, int height) {
        if (width <= 0 || height <= 0)
            return;

        int textureWidth = Math.max(1, this.category.getBackgroundWidth());
        int textureHeight = Math.max(1, this.category.getBackgroundHeight());

        int[] cover = coverSourceRect(textureWidth, textureHeight, width, height);

        this.renderBackgroundMapped(guiGraphics, x, y, width, height, cover[0], cover[1], cover[2], cover[3]);
    }

    /**
     * Calculates the centered section of a texture with the given size that covers an area of the given
     * size when uniformly scaled, without distorting the texture. Returns {u, v, width, height}.
     */
    private static int[] coverSourceRect(int textureWidth, int textureHeight, int areaWidth, int areaHeight) {
        //uniform scale so the texture covers the whole area
        float coverScale = Math.max(areaWidth / (float) textureWidth, areaHeight / (float) textureHeight);
        //the corresponding (centered) section of the texture, clamped to the texture bounds
        int srcWidth = Math.min(textureWidth, Math.max(1, Math.round(areaWidth / coverScale)));
        int srcHeight = Math.min(textureHeight, Math.max(1, Math.round(areaHeight / coverScale)));
        int uOffset = (textureWidth - srcWidth) / 2;
        int vOffset = (textureHeight - srcHeight) / 2;
        return new int[]{uOffset, vOffset, srcWidth, srcHeight};
    }

    /**
     * Renders the given section of the category background texture into the background area.
     * Applies the category background overscan (uniform extra zoom creating a pan margin) and pans the
     * sampled section with the current scroll position, clamped to the texture bounds.
     * Sampling uses normalized uvs, so no tiling occurs at any gui scale.
     */
    private void renderBackgroundMapped(GuiGraphicsExtractor guiGraphics, int x, int y, int width, int height, int baseU, int baseV, int baseSrcWidth, int baseSrcHeight) {
        int textureWidth = Math.max(1, this.category.getBackgroundWidth());
        int textureHeight = Math.max(1, this.category.getBackgroundHeight());

        //shrink the sampled section around its center to create the pan margin (1.0 = no margin = fixed)
        float overscan = Math.max(1, this.category.getBackgroundOverscan());
        int srcWidth = Math.min(textureWidth, Math.max(1, Math.round(baseSrcWidth / overscan)));
        int srcHeight = Math.min(textureHeight, Math.max(1, Math.round(baseSrcHeight / overscan)));

        //pan across the full scroll range, in the same direction as the tiled backgrounds.
        //only pans if there is an overscan margin (overscan > 1), otherwise the backdrop stays fixed.
        float scrollFractionX = 0;
        float scrollFractionY = 0;
        if (overscan > 1) {
            scrollFractionX = this.category.getMaxScrollX() > 0 ? Mth.clamp(this.scrollX / this.category.getMaxScrollX(), -1, 1) : 0;
            scrollFractionY = this.category.getMaxScrollY() > 0 ? Mth.clamp(this.scrollY / this.category.getMaxScrollY(), -1, 1) : 0;
        }
        int uOffset = Mth.clamp(Math.round(baseU + (baseSrcWidth - srcWidth) / 2f + scrollFractionX * (textureWidth - srcWidth) / 2f), 0, textureWidth - srcWidth);
        int vOffset = Mth.clamp(Math.round(baseV + (baseSrcHeight - srcHeight) / 2f + scrollFractionY * (textureHeight - srcHeight) / 2f), 0, textureHeight - srcHeight);

        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, this.category.getBackground(), x, y,
                uOffset, vOffset, width, height,
                srcWidth, srcHeight, textureWidth, textureHeight);
    }

    private void renderEntries(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY) {

        //calculate the render offset
        float xOffset = this.getXOffset();
        float yOffset = this.getYOffset();

        this.renderConnections(guiGraphics, xOffset, yOffset);

        guiGraphics.pose().pushMatrix();
        guiGraphics.pose().scale(this.currentZoom, this.currentZoom);

        for (var entry : this.category.getEntries().values()) {
            var displayState = this.getEntryDisplayState(entry);
            var isHovered = this.isEntryHovered(entry, displayState, xOffset, yOffset, mouseX, mouseY);

            if (displayState == EntryDisplayState.HIDDEN)
                continue;

            var entryBackground = entry.getEntryBackground();

            guiGraphics.pose().pushMatrix();
            //we translate instead of applying the offset to the entry x/y to avoid jittering when moving
            guiGraphics.pose().translate(xOffset, yOffset);


            //we apply a z offset to push the entries before the connection arrows
            //TODO here we had a translate +10z

            //As of 1.20 this is not necessary, in fact it causes the entry to render behind the bg
            //guiGraphics.pose().translate(0, 0, -10); //push the whole entry behind the frame


            int color = ARGB.colorFromFloat(1.0f, 1.0F, 1.0F, 1.0F);
            if (displayState == EntryDisplayState.LOCKED) {
                //Draw locked entries greyed out
                //TODO shader color needs to be handed as last parameter to blit
                color = ARGB.colorFromFloat(1f, 0.2F, 0.2F, 0.2F);
            } else if (isHovered) {
                //Draw hovered entries slightly greyed out
                color = ARGB.colorFromFloat(1f, 0.8F, 0.8F, 0.8F);
            }

            //render the entry name background first, so the badge overlaps the joint
            //like the vanilla advancement screen draws its title box behind the icon frame.
            //the theme default background, tint and text color can each be overridden per entry.
            //both tints combine with the entry color so locked names render under the same
            //grey overlay as the badge.
            var nameBox = this.getEntryNameBox(entry, displayState);
            if (nameBox != null) {
                var settings = this.category.getBook().theme().node().settings();
                var background = entry.getNameBackground().isEmpty() ? settings.entryNameStyle().background() : entry.getNameBackground();
                if (background.isEmpty()) {
                    background = FALLBACK_ENTRY_NAME_BACKGROUND;
                }
                background.extractRenderState(guiGraphics,
                        nameBox.x(), nameBox.y(), nameBox.width(), nameBox.height(),
                        ARGB.multiply(ARGB.multiply(settings.entryNameStyle().backgroundTint(), entry.getNameBackgroundColor()), color));
            }

            //render entry background
            entryBackground.extractRenderState(guiGraphics,
                    entry.getX() * ENTRY_GRID_SCALE + ENTRY_GAP, entry.getY() * ENTRY_GRID_SCALE + ENTRY_GAP,
                    ENTRY_WIDTH, ENTRY_HEIGHT, color);

            guiGraphics.pose().pushMatrix();

            //render icon
            entry.getIcon().render(guiGraphics, entry.getX() * ENTRY_GRID_SCALE + ENTRY_GAP + 5, entry.getY() * ENTRY_GRID_SCALE + ENTRY_GAP + 5);

            guiGraphics.pose().popMatrix();

            //render unread icon
            if (displayState == EntryDisplayState.UNLOCKED && BookServices.stateAccess().isEntryUnread(Minecraft.getInstance().player, entry)) {
                BookContentRenderer.drawUnreadIndicator(guiGraphics, this.bookParentScreen.getBook(),
                        entry.getX() * ENTRY_GRID_SCALE + ENTRY_GAP + 16 + 2,
                        entry.getY() * ENTRY_GRID_SCALE + ENTRY_GAP - 2, isHovered);
            }

            //render the entry name text on top of its background
            //unlike the vanilla advancement screen, which only shows name and description on hover,
            //this is permanently rendered as part of the entry
            if (nameBox != null) {
                int nameColor;
                if (displayState == EntryDisplayState.LOCKED) {
                    nameColor = 0xFFAAAAAA;
                } else {
                    var settings = this.category.getBook().theme().node().settings();
                    nameColor = ARGB.multiply(settings.entryNameStyle().textColor(), entry.getNameTextColor());
                }
                //float coordinates to allow sub-pixel vertical centering of the text in the bar
                TextRenderHelper.drawString(guiGraphics, Minecraft.getInstance().font, Component.translatable(entry.getName()),
                        nameBox.textX(), nameBox.textY(), nameColor, true);
            }

            guiGraphics.pose().popMatrix();
        }
        guiGraphics.pose().popMatrix();
    }

    /**
     * Background box and text position of the permanently rendered entry name.
     * The box tucks under (or, for side bars, passes behind) the badge so the two look connected.
     * Text coordinates are floats to allow sub-pixel vertical centering.
     */
    private record EntryNameBox(int x, int y, int width, int height, float textX, float textY) {
    }

    /**
     * Computes the entry name background box and text position, or null if the entry does not render its name.
     * Locked entries only render their name if they opt into {@code show_name_before_unlock}.
     * The bar has the same height for all placements.
     * Each direction has its own method so the styling can be adjusted independently.
     * Coordinates are in entry space, i.e. the caller must have applied the entry offset to the pose stack.
     */
    private EntryNameBox getEntryNameBox(BookEntry entry, EntryDisplayState displayState) {
        if (displayState == EntryDisplayState.LOCKED && !entry.showNameBeforeUnlock())
            return null;

        return switch (entry.getRenderName()) {
            case LEFT -> this.nameBoxLeft(entry);
            case RIGHT -> this.nameBoxRight(entry);
            case TOP -> this.nameBoxAbove(entry);
            case BOTTOM -> this.nameBoxBelow(entry);
            default -> null;
        };
    }

    /**
     * Uniform height of the entry name bar for all placements: text line plus vertical
     * padding, with one extra pixel at the bottom.
     */
    private int entryNameBarHeight() {
        return Minecraft.getInstance().font.lineHeight + ENTRY_NAME_PADDING_Y * 2 + 1;
    }

    /**
     * Name bar to the right of the badge, styled like the vanilla advancement title bar:
     * passing behind the full badge and sticking out {@link #ENTRY_NAME_STICK_THROUGH} pixels
     * on its far side, vertically centered next to it, text centered in the bar.
     */
    private EntryNameBox nameBoxRight(BookEntry entry) {
        var font = Minecraft.getInstance().font;
        int nameWidth = font.width(Component.translatable(entry.getName()));
        int barHeight = this.entryNameBarHeight();

        int entryX = entry.getX() * ENTRY_GRID_SCALE + ENTRY_GAP;
        int entryY = entry.getY() * ENTRY_GRID_SCALE + ENTRY_GAP;

        int width = ENTRY_NAME_STICK_THROUGH + ENTRY_WIDTH + ENTRY_NAME_PADDING_X + nameWidth + ENTRY_NAME_PADDING_X;
        int x = entryX - ENTRY_NAME_STICK_THROUGH;
        int y = entryY + (ENTRY_HEIGHT - barHeight) / 2;

        return new EntryNameBox(x, y, width, barHeight,
                entryX + ENTRY_WIDTH + ENTRY_NAME_PADDING_X, y + (barHeight - font.lineHeight) / 2f);
    }

    /**
     * Name bar to the left of the badge, mirrored version of {@link #nameBoxRight(BookEntry)}.
     */
    private EntryNameBox nameBoxLeft(BookEntry entry) {
        var font = Minecraft.getInstance().font;
        int nameWidth = font.width(Component.translatable(entry.getName()));
        int barHeight = this.entryNameBarHeight();

        int entryX = entry.getX() * ENTRY_GRID_SCALE + ENTRY_GAP;
        int entryY = entry.getY() * ENTRY_GRID_SCALE + ENTRY_GAP;

        int width = ENTRY_NAME_PADDING_X + nameWidth + ENTRY_NAME_PADDING_X + ENTRY_WIDTH + ENTRY_NAME_STICK_THROUGH;
        int x = entryX + ENTRY_WIDTH + ENTRY_NAME_STICK_THROUGH - width;
        int y = entryY + (ENTRY_HEIGHT - barHeight) / 2;

        return new EntryNameBox(x, y, width, barHeight,
                (float) x + ENTRY_NAME_PADDING_X, y + (barHeight - font.lineHeight) / 2f);
    }

    /**
     * Sample styling for a name bar above the badge: fitted bar with the same height as the side bars,
     * tucked under the badge, text centered on the background like for the side bars.
     * Adjust freely, it is intentionally decoupled from the side bar styling above.
     */
    private EntryNameBox nameBoxAbove(BookEntry entry) {
        var font = Minecraft.getInstance().font;
        int nameWidth = font.width(Component.translatable(entry.getName()));
        int barHeight = this.entryNameBarHeight();
        int tuck = ENTRY_NAME_OVERLAP + ENTRY_NAME_VERTICAL_SHIFT;

        int entryX = entry.getX() * ENTRY_GRID_SCALE + ENTRY_GAP;
        int entryY = entry.getY() * ENTRY_GRID_SCALE + ENTRY_GAP;

        int width = nameWidth + ENTRY_NAME_PADDING_X * 2;
        int x = entryX + (ENTRY_WIDTH - width) / 2;
        int y = entryY - barHeight + tuck;

        return new EntryNameBox(x, y, width, barHeight,
                x + ENTRY_NAME_PADDING_X, y + (barHeight - font.lineHeight) / 2f);
    }

    /**
     * Sample styling for a name bar below the badge: fitted bar with the same height as the side bars,
     * tucked under the badge, text centered on the background like for the side bars.
     * Adjust freely, it is intentionally decoupled from the side bar styling above.
     */
    private EntryNameBox nameBoxBelow(BookEntry entry) {
        var font = Minecraft.getInstance().font;
        int nameWidth = font.width(Component.translatable(entry.getName()));
        int barHeight = this.entryNameBarHeight();
        int tuck = ENTRY_NAME_OVERLAP + ENTRY_NAME_VERTICAL_SHIFT;

        int entryX = entry.getX() * ENTRY_GRID_SCALE + ENTRY_GAP;
        int entryY = entry.getY() * ENTRY_GRID_SCALE + ENTRY_GAP;

        int width = nameWidth + ENTRY_NAME_PADDING_X * 2;
        int x = entryX + (ENTRY_WIDTH - width) / 2;
        int y = entryY + ENTRY_HEIGHT - tuck;

        return new EntryNameBox(x, y, width, barHeight,
                x + ENTRY_NAME_PADDING_X, y + (barHeight - font.lineHeight) / 2f);
    }

    public void renderEntryTooltips(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTicks) {
        //calculate the render offset
        float xOffset = this.getXOffset();
        float yOffset = this.getYOffset();

        for (var entry : this.category.getEntries().values()) {
            var displayState = this.getEntryDisplayState(entry);
            if (displayState == EntryDisplayState.HIDDEN)
                continue;

            this.renderTooltip(guiGraphics, entry, displayState, xOffset, yOffset, mouseX, mouseY);
        }
    }

    private boolean isEntryHovered(BookEntry entry, EntryDisplayState displayState, float xOffset, float yOffset, int mouseX, int mouseY) {
        int x = (int) ((entry.getX() * ENTRY_GRID_SCALE + xOffset + 2) * this.currentZoom);
        int y = (int) ((entry.getY() * ENTRY_GRID_SCALE + yOffset + 2) * this.currentZoom);
        int innerX = this.bookParentScreen.getInnerX();
        int innerY = this.bookParentScreen.getInnerY();
        int innerWidth = this.bookParentScreen.getInnerWidth();
        int innerHeight = this.bookParentScreen.getInnerHeight();
        if (mouseX < innerX || mouseX > innerX + innerWidth
                || mouseY < innerY || mouseY > innerY + innerHeight) {
            return false;
        }
        if (mouseX >= x && mouseX <= x + (ENTRY_WIDTH * this.currentZoom)
                && mouseY >= y && mouseY <= y + (ENTRY_HEIGHT * this.currentZoom)) {
            return true;
        }
        //the name badge counts as part of the entry
        var nameBox = this.getEntryNameBox(entry, displayState);
        if (nameBox == null) {
            return false;
        }
        int boxX = (int) ((nameBox.x() + xOffset) * this.currentZoom);
        int boxY = (int) ((nameBox.y() + yOffset) * this.currentZoom);
        return mouseX >= boxX && mouseX <= boxX + (nameBox.width() * this.currentZoom)
                && mouseY >= boxY && mouseY <= boxY + (nameBox.height() * this.currentZoom);
    }

    private void renderTooltip(GuiGraphicsExtractor guiGraphics, BookEntry entry, EntryDisplayState displayState, float xOffset, float yOffset, int mouseX, int mouseY) {
        //hovered?
        if (this.isEntryHovered(entry, displayState, xOffset, yOffset, mouseX, mouseY)) {

            var tooltip = new ArrayList<ClientTooltipComponent>();
            var tooltipComponents = new ArrayList<Component>();

            if (displayState == EntryDisplayState.LOCKED) {
                tooltip.addAll(
                        entry.getCondition().getTooltip(Minecraft.getInstance().player, BookConditionEntryContext.of(this.bookParentScreen.getBook(), entry)).stream().map(Component::getVisualOrderText).map(ClientTooltipComponent::create).toList());
                tooltipComponents.addAll(
                        entry.getCondition().getTooltip(Minecraft.getInstance().player, BookConditionEntryContext.of(this.bookParentScreen.getBook(), entry)));
            } else if (displayState == EntryDisplayState.UNLOCKED) {
                //add name in bold
                tooltip.add(ClientTooltipComponent.create(Component.translatable(entry.getName()).withStyle(ChatFormatting.BOLD).getVisualOrderText()));
                tooltipComponents.add(Component.translatable(entry.getName()).withStyle(ChatFormatting.BOLD));
                //add description
                if (!entry.getDescription().isEmpty()) {
                    tooltip.add(ClientTooltipComponent.create(Component.translatable(entry.getDescription()).getVisualOrderText()));
                    tooltipComponents.add(Component.translatable(entry.getDescription()));
                }
            }

            //draw description
            guiGraphics.setComponentTooltipForNextFrame(Minecraft.getInstance().font, tooltipComponents, mouseX, mouseY);
//            guiGraphics.renderTooltip(Minecraft.getInstance().font, tooltip, mouseX, mouseY, DefaultTooltipPositioner.INSTANCE, null);
        }
    }

    private void renderConnections(GuiGraphicsExtractor guiGraphics, float xOffset, float yOffset) {
        if (this.category.getBook().theme().node().settings().connectionRenderer() == NodeConnectionRendererType.DIRECT) {
            this.directConnectionRenderer.render(guiGraphics, this);
            return;
        }

        guiGraphics.pose().pushMatrix();
        guiGraphics.pose().scale(this.currentZoom, this.currentZoom);

        for (var entry : this.category.getEntries().values()) {
            var entryDisplayState = this.getEntryDisplayState(entry);
            if (entryDisplayState == EntryDisplayState.HIDDEN) {
                continue;
            }

            for (var parent : entry.getParents()) {
                var parentDisplayState = this.getEntryDisplayState(parent.getEntry());
                if (parentDisplayState == EntryDisplayState.HIDDEN) {
                    continue;
                }

                guiGraphics.pose().pushMatrix();
                guiGraphics.pose().translate(xOffset, yOffset);
                this.spriteConnectionRenderer.render(guiGraphics, entry, parent);
                guiGraphics.pose().popMatrix();
            }
        }

        guiGraphics.pose().popMatrix();
    }

    private void scroll(double pDragX, double pDragY) {
        this.scrollX = (float) Mth.clamp(this.scrollX - pDragX, -this.getCategory().getMaxScrollX(), this.getCategory().getMaxScrollX());
        this.scrollY = (float) Mth.clamp(this.scrollY - pDragY, -this.getCategory().getMaxScrollY(), this.getCategory().getMaxScrollY());
    }

    /**
     * Sets the visual elements of the state, but not the open entry (handled by Gui Manager)
     */
    @Override
    public void loadState(CategoryVisualState state) {
        this.scrollX = state.scrollX;
        this.scrollY = state.scrollY;
        this.targetZoom = state.targetZoom;
        this.currentZoom = state.targetZoom;
    }

    @Override
    public void saveState(CategoryVisualState state) {
        state.scrollX = this.scrollX;
        state.scrollY = this.scrollY;
        state.targetZoom = this.targetZoom;
    }

    @Override
    public void onDisplay() {

    }

    @Override
    public void onClose() {
        //do not call super - gui manager should handle gui removal
        //Note: As this is not a vanilla screen child we don't even have a super :)
    }

    public boolean keyPressed(KeyEvent event) {
        if (event.isEscape()) {
            BookGuiManager.get().closeScreenStack(this);
            return true;
        }
        return false;
    }
}
