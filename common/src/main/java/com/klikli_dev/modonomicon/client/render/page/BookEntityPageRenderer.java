/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.client.render.page;

import com.klikli_dev.modonomicon.Modonomicon;
import com.klikli_dev.modonomicon.api.ModonomiconConstants.I18n.Gui;
import com.klikli_dev.modonomicon.book.page.BookEntityPage;
import com.klikli_dev.modonomicon.client.ClientTicks;
import com.klikli_dev.modonomicon.client.gui.book.BookContentRenderer;
import com.klikli_dev.modonomicon.client.gui.book.entry.BookEntryScreen;
import com.klikli_dev.modonomicon.util.EntityUtil;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

public class BookEntityPageRenderer extends BookPageRenderer<BookEntityPage> implements PageWithTextRenderer {
    private Entity entity;
    private boolean errored;
    private float renderScale;
    private float renderOffset;

    public BookEntityPageRenderer(BookEntityPage page) {
        super(page);
    }

    public static void renderEntity(GuiGraphics guiGraphics, Entity entity, Level world, float x, float y, float rotation, float renderScale, float offset) {
        //TODO: fix entity rendering
//        Vector3f vector3f = new Vector3f(0.0F, p_275689_.getBbHeight() / 2.0F + p_275604_ * f9, 0.0F);
//
//
//        if(entity instanceof LivingEntity livingEntity) {
//            EntityRenderDispatcher erd = Minecraft.getInstance().getEntityRenderDispatcher();
//            EntityRenderer<? super LivingEntity, ?> entityrenderer = erd.getRenderer(livingEntity);
//            EntityRenderState entityrenderstate = entityrenderer.createRenderState(livingEntity, 1.0F);
//            entityrenderstate.hitboxesRenderState = null;
//            guiGraphics.submitEntityRenderState(entityrenderstate, renderScale, translation, rotation, overrideCameraAngle, x1, y1, x2, y2);
//        }
    }

    private void loadEntity(Level world) {
        if (!this.errored && (this.entity == null || !this.entity.isAlive())) {
            try {
                var entityLoader = EntityUtil.getEntityLoader(this.page.getEntityId());
                this.entity = entityLoader.apply(world);

                float width = this.entity.getBbWidth();
                float height = this.entity.getBbHeight();

                float entitySize = Math.max(1F, Math.max(width, height));

                this.renderScale = 100F / entitySize * 0.8F * this.getPage().getScale();
                this.renderOffset = Math.max(height, entitySize) * 0.5F + this.getPage().getOffset();
            } catch (Exception e) {
                this.errored = true;
                Modonomicon.LOG.error("Failed to load entity", e);
            }
        }
    }

    @Override
    public void onBeginDisplayPage(BookEntryScreen parentScreen, int left, int top) {
        super.onBeginDisplayPage(parentScreen, left, top);

        this.loadEntity(Minecraft.getInstance().level);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float ticks) {
        if (!this.page.getEntityName().isEmpty()) {
            this.renderTitle(guiGraphics, this.page.getEntityName(), false, BookEntryScreen.PAGE_WIDTH / 2, 0);
        }

        int textY = this.getTextY();
        this.renderBookTextHolder(guiGraphics, this.getPage().getText(), 0, textY, BookEntryScreen.PAGE_WIDTH, BookEntryScreen.PAGE_HEIGHT - textY);

        int x = BookEntryScreen.PAGE_WIDTH / 2 - 53;
        int y = 7;
       BookContentRenderer.drawFromContentTexture(RenderPipelines.GUI_TEXTURED, guiGraphics, this.getPage().getBook(), x, y, 405, 149, 106, 106);

        if (this.errored) {
            guiGraphics.drawString(this.font, Component.translatable(Gui.PAGE_ENTITY_LOADING_ERROR), 58, 60, 0xFF0000, true);
        }

        if (this.entity != null) {
            float rotation = this.page.doesRotate() ? ClientTicks.total : this.page.getDefaultRotation();
            renderEntity(guiGraphics, this.entity, Minecraft.getInstance().level, 58, 60, rotation, this.renderScale, this.renderOffset);
        }

        var style = this.getClickedComponentStyleAt(mouseX, mouseY);
        if (style != null)
            this.parentScreen.renderComponentHoverEffect(guiGraphics, style, mouseX, mouseY);
    }

    @Nullable
    @Override
    public Style getClickedComponentStyleAt(double pMouseX, double pMouseY) {
        if (pMouseX > 0 && pMouseY > 0) {
            if (!this.page.getEntityName().isEmpty()) {
                var titleStyle = this.getClickedComponentStyleAtForTitle(this.page.getEntityName(), BookEntryScreen.PAGE_WIDTH / 2, 0, pMouseX, pMouseY);
                if (titleStyle != null) {
                    return titleStyle;
                }
            }

            var x = this.parentScreen.getBook().getBookTextOffsetX();
            var y = this.getTextY() + this.parentScreen.getBook().getBookTextOffsetY();
            var width = BookEntryScreen.PAGE_WIDTH + this.parentScreen.getBook().getBookTextOffsetWidth() - x; //always remove the offset x from the width to avoid overflow
            var height = BookEntryScreen.PAGE_HEIGHT + this.parentScreen.getBook().getBookTextOffsetHeight() - y; //always remove the offset y from the height to avoid overflow

            var textStyle = this.getClickedComponentStyleAtForTextHolder(this.page.getText(), x, y, width, height, pMouseX, pMouseY);
            if (textStyle != null) {
                return textStyle;
            }
        }
        return super.getClickedComponentStyleAt(pMouseX, pMouseY);
    }

    @Override
    public int getTextY() {
        return 115;
    }


}
