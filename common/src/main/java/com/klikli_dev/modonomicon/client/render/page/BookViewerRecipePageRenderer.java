/*
 * SPDX-FileCopyrightText: 2026 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.client.render.page;

import com.klikli_dev.modonomicon.api.ModonomiconConstants;
import com.klikli_dev.modonomicon.book.BookTextHolder;
import com.klikli_dev.modonomicon.book.page.BookViewerRecipePage;
import com.klikli_dev.modonomicon.book.page.RecipeAttribute;
import com.klikli_dev.modonomicon.client.gui.BookGuiManager;
import com.klikli_dev.modonomicon.client.gui.book.entry.BookEntryScreen;
import com.klikli_dev.modonomicon.integration.recipeviewer.RecipeViewerRecipe;
import com.klikli_dev.modonomicon.integration.recipeviewer.RecipeViewerRecipePreview;
import com.klikli_dev.modonomicon.integration.recipeviewer.RecipeViewerRegistry;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.crafting.Recipe;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;

/**
 * Renders a {@link BookViewerRecipePage} by asking an installed recipe viewer to render the referenced recipes.
 * <p>
 * This renderer does not reference any recipe viewer directly, it only talks to the
 * {@link RecipeViewerRegistry} abstraction, so it works with any supported viewer (e.g. JEI).
 * <p>
 * Each recipe can request a scale via the page. The requested scale is always clamped so that the recipe, together
 * with the page titles, never exceeds the page's usable area.
 */
public class BookViewerRecipePageRenderer extends BookPageRenderer<BookViewerRecipePage> implements PageWithTextRenderer {

    public static int Y = 4;

    private static final int TITLE_HEIGHT = 12;
    private static final int RECIPE_GAP = 4;
    private static final int FALLBACK_RECIPE_HEIGHT = 40;
    private static final long TICK_INTERVAL_MS = 50L;

    /**
     * The computed layout of one recipe block on the page.
     */
    private record RecipeLayout(
            @Nullable RecipeViewerRecipePreview preview,
            RecipeAttribute attribute,
            BookTextHolder title,
            @Nullable ResourceKey<Recipe<?>> recipeKey,
            int titleY,
            int previewX,
            int previewY,
            int previewWidth,
            int previewHeight,
            int endY
    ) {
        boolean hasPreview() {
            return this.preview != null && this.attribute.scale() > 0.0f;
        }

        boolean contains(double mouseX, double mouseY) {
            return mouseX >= this.previewX && mouseX < this.previewX + this.previewWidth
                    && mouseY >= this.previewY && mouseY < this.previewY + this.previewHeight;
        }
    }

    @Nullable
    private RecipeLayout recipe1Layout;
    @Nullable
    private RecipeLayout recipe2Layout;
    private int contentHeight;

    private long lastTickTime;

    public BookViewerRecipePageRenderer(BookViewerRecipePage page) {
        super(page);
    }

    @Override
    public void onBeginDisplayPage(BookEntryScreen parentScreen, int left, int top) {
        super.onBeginDisplayPage(parentScreen, left, top);
        this.lastTickTime = 0;
        var preview1 = this.createPreview(this.page.getRecipeKey1(), this.page.getRecipeType1(), this.page.getRecipe1());
        var preview2 = this.createPreview(this.page.getRecipeKey2(), this.page.getRecipeType2(), this.page.getRecipe2());
        this.updateLayout(preview1, preview2);
    }

    @Nullable
    private RecipeViewerRecipePreview createPreview(@Nullable ResourceKey<Recipe<?>> recipeKey, @Nullable Identifier recipeTypeId, RecipeAttribute attribute) {
        boolean hasId = recipeKey != null && recipeTypeId != null;
        if (!hasId && !attribute.hasQuery()) {
            return null;
        }
        return RecipeViewerRegistry.createRecipePreview(new RecipeViewerRecipe(
                hasId ? recipeKey.identifier() : null,
                recipeTypeId,
                attribute.background(),
                attribute.input(),
                attribute.output(),
                attribute.workstation()
        ));
    }

    /**
     * Computes the effective scale of both recipes so that they fit the page's usable area, and stores the
     * resulting layout.
     * <p>
     * A recipe is first clamped to the page width, then both recipes are scaled down together (proportionally)
     * until the total content height fits the page. This means a recipe never exceeds the page, no matter the
     * requested scale.
     */
    private void updateLayout(@Nullable RecipeViewerRecipePreview preview1, @Nullable RecipeViewerRecipePreview preview2) {
        boolean has1 = this.page.getRecipeKey1() != null || this.page.getRecipe1().hasQuery();
        boolean has2 = this.page.getRecipeKey2() != null || this.page.getRecipe2().hasQuery();

        float scale1 = has1 ? Math.max(0.0f, this.page.getRecipe1().scale()) : 0.0f;
        float scale2 = has2 ? Math.max(0.0f, this.page.getRecipe2().scale()) : 0.0f;

        //clamp each recipe to the page width
        if (has1 && preview1 != null && preview1.getWidth() > 0) {
            scale1 = Math.min(scale1, (float) BookEntryScreen.PAGE_WIDTH / preview1.getWidth());
        }
        if (has2 && preview2 != null && preview2.getWidth() > 0) {
            scale2 = Math.min(scale2, (float) BookEntryScreen.PAGE_WIDTH / preview2.getWidth());
        }

        //heights that are not affected by the preview scale (titles, gaps and fallback messages)
        double fixedHeight = 0;
        if (has1) {
            fixedHeight += this.titleHeight(this.page.getTitle1());
            if (preview1 == null) {
                fixedHeight += FALLBACK_RECIPE_HEIGHT;
            }
        }
        if (has2) {
            fixedHeight += RECIPE_GAP + this.titleHeight(this.page.getTitle2());
            if (preview2 == null) {
                fixedHeight += FALLBACK_RECIPE_HEIGHT;
            }
        }

        //scaled preview heights
        double previewHeight = 0;
        if (has1 && preview1 != null) {
            previewHeight += preview1.getHeight() * scale1;
        }
        if (has2 && preview2 != null) {
            previewHeight += preview2.getHeight() * scale2;
        }

        //clamp to the page height
        double maxContentHeight = BookEntryScreen.PAGE_HEIGHT - Y;
        if (fixedHeight + previewHeight > maxContentHeight) {
            double available = maxContentHeight - fixedHeight;
            if (available <= 0 || previewHeight <= 0) {
                scale1 = 0.0f;
                scale2 = 0.0f;
            } else {
                double factor = available / previewHeight;
                scale1 *= (float) factor;
                scale2 *= (float) factor;
            }
        }

        int y = Y;
        if (has1) {
            this.recipe1Layout = this.buildLayout(preview1, this.page.getRecipe1().withScale(scale1), this.page.getTitle1(), this.page.getRecipeKey1(), y);
            y = this.recipe1Layout.endY();
        } else {
            this.recipe1Layout = null;
        }

        if (has2) {
            y += RECIPE_GAP;
            this.recipe2Layout = this.buildLayout(preview2, this.page.getRecipe2().withScale(scale2), this.page.getTitle2(), this.page.getRecipeKey2(), y);
            y = this.recipe2Layout.endY();
        } else {
            this.recipe2Layout = null;
        }

        this.contentHeight = (int) Math.min(y - Y, maxContentHeight);
    }

    private RecipeLayout buildLayout(@Nullable RecipeViewerRecipePreview preview, RecipeAttribute attribute, BookTextHolder title, @Nullable ResourceKey<Recipe<?>> recipeKey, int y) {
        int titleY = y;
        if (!title.isEmpty()) {
            y += TITLE_HEIGHT;
        }

        int previewY = y;
        int previewX = 0;
        int previewWidth = 0;
        int previewHeight = 0;

        if (preview != null && attribute.scale() > 0.0f) {
            previewWidth = scaledWidth(preview, attribute.scale());
            previewHeight = scaledHeight(preview, attribute.scale());
            previewX = (BookEntryScreen.PAGE_WIDTH - previewWidth) / 2;
            y += previewHeight;
        } else if (preview == null) {
            y += FALLBACK_RECIPE_HEIGHT;
        }

        return new RecipeLayout(preview, attribute, title, recipeKey, titleY, previewX, previewY, previewWidth, previewHeight, y);
    }

    private static int scaledWidth(RecipeViewerRecipePreview preview, float scale) {
        return Math.max(1, Math.round(preview.getWidth() * scale));
    }

    private static int scaledHeight(RecipeViewerRecipePreview preview, float scale) {
        if (scale <= 0.0f) {
            return 0;
        }
        return Math.max(1, Math.round(preview.getHeight() * scale));
    }

    @Override
    public void render(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float ticks) {
        this.tick();

        this.drawRecipe(guiGraphics, this.recipe1Layout, mouseX, mouseY);
        this.drawRecipe(guiGraphics, this.recipe2Layout, mouseX, mouseY);

        var textY = this.getTextY();
        this.renderBookTextHolder(guiGraphics, this.getPage().getText(), 0, textY, BookEntryScreen.PAGE_WIDTH, BookEntryScreen.PAGE_HEIGHT - textY);

        var style = this.getClickedComponentStyleAt(mouseX, mouseY);
        if (style != null) {
            //mouseX/mouseY are page-local (pMouseX - bookLeft - page.left), tooltips need screen coordinates
            this.parentScreen.renderComponentHoverEffect(guiGraphics, style, mouseX + this.parentScreen.getBookLeft() + this.left, mouseY + this.parentScreen.getBookTop() + this.top);
        }

        //draw viewer overlays (e.g. non-item tooltips) last so they end up on top
        this.renderOverlays(guiGraphics, mouseX, mouseY);
    }

    private void renderOverlays(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY) {
        int screenMouseX = this.parentScreen.getBookLeft() + this.left + mouseX;
        int screenMouseY = this.parentScreen.getBookTop() + this.top + mouseY;
        this.renderOverlay(guiGraphics, this.recipe1Layout, mouseX, mouseY, screenMouseX, screenMouseY);
        this.renderOverlay(guiGraphics, this.recipe2Layout, mouseX, mouseY, screenMouseX, screenMouseY);
    }

    private void renderOverlay(GuiGraphicsExtractor guiGraphics, @Nullable RecipeLayout layout, int mouseX, int mouseY, int screenMouseX, int screenMouseY) {
        if (layout == null || !layout.hasPreview()) {
            return;
        }

        var preview = layout.preview();
        if (preview == null) {
            return;
        }

        //only draw overlays while the mouse is over this preview
        if (!layout.contains(mouseX, mouseY)) {
            return;
        }

        preview.renderOverlays(guiGraphics, layout.previewX(), layout.previewY(), mouseX, mouseY, layout.attribute().scale(), screenMouseX, screenMouseY);
    }

    private void drawRecipe(GuiGraphicsExtractor guiGraphics, @Nullable RecipeLayout layout, int mouseX, int mouseY) {
        if (layout == null) {
            return;
        }

        if (!layout.title().isEmpty()) {
            this.renderTitle(guiGraphics, layout.title(), false, BookEntryScreen.PAGE_WIDTH / 2, layout.titleY());
        }

        if (layout.preview() == null) {
            if (!RecipeViewerRegistry.isAnyAvailable()) {
                //no recipe viewer is installed, so the recipe simply cannot be rendered here. It is not missing.
                this.drawWrappedStringNoShadow(guiGraphics,
                        Component.translatable(ModonomiconConstants.I18n.Gui.RECIPE_PAGE_NO_RECIPE_VIEWER).withStyle(ChatFormatting.RED),
                        0, layout.previewY(), -1, BookEntryScreen.PAGE_WIDTH);
                return;
            }

            String description = layout.recipeKey() != null ? layout.recipeKey().identifier().toString() : describeQuery(layout.attribute());
            this.drawWrappedStringNoShadow(guiGraphics,
                    Component.translatable(ModonomiconConstants.I18n.Gui.RECIPE_PAGE_RECIPE_MISSING, description).withStyle(ChatFormatting.RED),
                    0, layout.previewY(), -1, BookEntryScreen.PAGE_WIDTH);
            return;
        }

        if (!layout.hasPreview()) {
            //recipe does not fit the page at all, skip it rather than overflowing
            return;
        }

        float scale = layout.attribute().scale();
        layout.preview().render(guiGraphics, layout.previewX(), layout.previewY(), mouseX, mouseY, scale);

        //register the hovered ingredient with the screen, so it shows a tooltip and can be clicked to
        //look up its recipes/usages in the recipe viewer
        var hoveredStack = layout.preview().getItemStackUnderMouse(layout.previewX(), layout.previewY(), mouseX, mouseY, scale);
        if (!hoveredStack.isEmpty()) {
            this.parentScreen.setTooltipStack(hoveredStack);
        }
    }

    @Override
    public boolean mouseClickedIngredient(MouseButtonEvent event) {
        int button = event.button();
        if (button != GLFW.GLFW_MOUSE_BUTTON_LEFT && button != GLFW.GLFW_MOUSE_BUTTON_RIGHT) {
            return false;
        }

        boolean uses = button == GLFW.GLFW_MOUSE_BUTTON_RIGHT || this.mc.hasShiftDown();

        //the screen already handles item stack clicks (via the hovered tooltip stack), so reaching this point
        //means the hovered ingredient is not an item stack. Let the viewer handle it so non-item ingredients
        //can still be looked up.
        if (this.handleIngredientClick(this.recipe1Layout, event.x(), event.y(), uses)) {
            return true;
        }
        return this.handleIngredientClick(this.recipe2Layout, event.x(), event.y(), uses);
    }

    private boolean handleIngredientClick(@Nullable RecipeLayout layout, double mouseX, double mouseY, boolean uses) {
        if (layout == null || !layout.hasPreview()) {
            return false;
        }

        //only handle clicks within the preview bounds
        if (!layout.contains(mouseX, mouseY)) {
            return false;
        }

        var preview = layout.preview();
        if (preview == null) {
            return false;
        }

        boolean[] handled = {false};
        BookGuiManager.get().keepMousePosition(() ->
                handled[0] = preview.handleIngredientClick(layout.previewX(), layout.previewY(), (int) mouseX, (int) mouseY, layout.attribute().scale(), uses));
        return handled[0];
    }

    private void tick() {
        long now = System.currentTimeMillis();
        if (now - this.lastTickTime < TICK_INTERVAL_MS) {
            return;
        }
        this.lastTickTime = now;

        if (this.recipe1Layout != null && this.recipe1Layout.preview() != null) {
            this.recipe1Layout.preview().tick();
        }
        if (this.recipe2Layout != null && this.recipe2Layout.preview() != null) {
            this.recipe2Layout.preview().tick();
        }
    }

    @Override
    public int getTextY() {
        return Y + this.contentHeight;
    }

    private int titleHeight(BookTextHolder title) {
        return title.isEmpty() ? 0 : TITLE_HEIGHT;
    }

    /**
     * Builds a short description of the ingredient conditions, used in the missing recipe message.
     */
    private static String describeQuery(RecipeAttribute attribute) {
        var parts = new ArrayList<String>();
        for (var ingredient : attribute.input()) {
            parts.add("input=" + ingredient.describe());
        }
        for (var ingredient : attribute.output()) {
            parts.add("output=" + ingredient.describe());
        }
        for (var ingredient : attribute.workstation()) {
            parts.add("workstation=" + ingredient.describe());
        }
        return String.join(", ", parts);
    }

    @Nullable
    @Override
    public Style getClickedComponentStyleAt(double pMouseX, double pMouseY) {
        if (pMouseX > 0 && pMouseY > 0) {
            var textY = this.getTextY();
            var bounds = this.getBookTextHolderBounds(0, textY, BookEntryScreen.PAGE_WIDTH, BookEntryScreen.PAGE_HEIGHT - textY);
            var textStyle = this.getClickedComponentStyleAtForTextHolder(this.page.getText(), bounds.x, bounds.y, bounds.width, bounds.height, pMouseX, pMouseY);
            if (textStyle != null) {
                return textStyle;
            }
        }
        return super.getClickedComponentStyleAt(pMouseX, pMouseY);
    }
}
