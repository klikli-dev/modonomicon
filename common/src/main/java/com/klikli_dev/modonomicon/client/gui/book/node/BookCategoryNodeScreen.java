/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 * SPDX-FileCopyrightText: 2021 Authors of Arcana
 *
 * SPDX-License-Identifier: MIT
 */
package com.klikli_dev.modonomicon.client.gui.book.node;

import com.klikli_dev.modonomicon.api.events.EntryClickedEvent;
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
import com.klikli_dev.modonomicon.client.gui.book.theme.NodeConnectionRendererType;
import com.klikli_dev.modonomicon.events.ModonomiconEvents;
import com.klikli_dev.modonomicon.platform.ClientServices;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;

import net.minecraft.network.chat.Component;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;


public class BookCategoryNodeScreen implements BookCategoryScreen {
    public static final int ENTRY_GRID_SCALE = 30;
    public static final int ENTRY_GAP = 2;

    public static final int ENTRY_HEIGHT = 26;
    public static final int ENTRY_WIDTH = 26;

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
        if (event.button() != 0) {
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

            if (this.isEntryHovered(entry, xOffset, yOffset, (int) event.x(), (int) event.y())) {

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
        float parallax1 = parallax / layer.getSpeed();

        if (layer.getVanishZoom() == -1 || layer.getVanishZoom() > zoom) {
            //for some reason on this one blit overload tex width and height are switched. It does correctly call the followup though, so we have to go along
            guiGraphics.blit(RenderPipelines.GUI_TEXTURED, layer.getBackground(), x, y,
                    (scrollX + this.getCategory().getMaxScrollX()) / parallax1 + xOffset,
                    (scrollY + this.getCategory().getMaxScrollY()) / parallax1 + yOffset,
                    width, height, (int) (backgroundHeight * backgroundTextureZoomMultiplier), (int) (backgroundWidth * backgroundTextureZoomMultiplier));
        }

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
            var isHovered = this.isEntryHovered(entry, xOffset, yOffset, mouseX, mouseY);

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

            guiGraphics.pose().popMatrix();
        }
        guiGraphics.pose().popMatrix();
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

    private boolean isEntryHovered(BookEntry entry, float xOffset, float yOffset, int mouseX, int mouseY) {
        int x = (int) ((entry.getX() * ENTRY_GRID_SCALE + xOffset + 2) * this.currentZoom);
        int y = (int) ((entry.getY() * ENTRY_GRID_SCALE + yOffset + 2) * this.currentZoom);
        int innerX = this.bookParentScreen.getInnerX();
        int innerY = this.bookParentScreen.getInnerY();
        int innerWidth = this.bookParentScreen.getInnerWidth();
        int innerHeight = this.bookParentScreen.getInnerHeight();
        return mouseX >= x && mouseX <= x + (ENTRY_WIDTH * this.currentZoom)
                && mouseY >= y && mouseY <= y + (ENTRY_HEIGHT * this.currentZoom)
                && mouseX >= innerX && mouseX <= innerX + innerWidth
                && mouseY >= innerY && mouseY <= innerY + innerHeight;
    }

    private void renderTooltip(GuiGraphicsExtractor guiGraphics, BookEntry entry, EntryDisplayState displayState, float xOffset, float yOffset, int mouseX, int mouseY) {
        //hovered?
        if (this.isEntryHovered(entry, xOffset, yOffset, mouseX, mouseY)) {

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
        if (event.key() == GLFW.GLFW_KEY_ESCAPE) {
            BookGuiManager.get().closeScreenStack(this);
            return true;
        }
        return false;
    }
}
