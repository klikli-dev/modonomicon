/*
 * SPDX-FileCopyrightText: 2022 Authors of Patchouli
 * SPDX-FileCopyrightText: 2022 klikli-dev
 * SPDX-FileCopyrightText: 2026 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.client.render.preview;

import com.klikli_dev.modonomicon.api.ModonomiconConstants;
import com.klikli_dev.modonomicon.util.TextRenderHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.util.Mth;

import java.awt.Color;

/**
 * HUD overlay for the in-world multiblock preview: title, animated build progress bar,
 * completion banner, anchor/looking status lines and layer-by-layer indicator.
 * <p>
 * Rendering moved verbatim from the former {@code MultiblockPreviewRenderer} monolith and split
 * into one small draw method per visual element. Pixel positions, colors and timings are
 * unchanged (see {@link PreviewConstants}).
 * </p>
 */
public final class PreviewHudRenderer {

    private PreviewHudRenderer() {
    }

    /**
     * Renders the preview HUD.
     *
     * @return {@code true} when the completion animation finished and the preview should be cleared.
     */
    public static boolean render(PreviewSession session, PreviewProgress progress, GuiGraphicsExtractor guiGraphics, float partialTicks) {
        int waitTime = PreviewConstants.COMPLETE_WAIT_TICKS;
        int fadeOutSpeed = PreviewConstants.COMPLETE_FADE_PX_PER_TICK;
        int fullAnimTime = waitTime + PreviewConstants.COMPLETE_ANIM_TAIL_TICKS;
        int timeComplete = session.timeComplete();
        float animTime = timeComplete + (timeComplete == 0 ? 0 : partialTicks);

        if (animTime > fullAnimTime) {
            return true;
        }

        guiGraphics.pose().pushMatrix();
        guiGraphics.pose().translate(0, -Math.max(0, animTime - waitTime) * fadeOutSpeed);

        Minecraft mc = Minecraft.getInstance();
        int x = mc.getWindow().getGuiScaledWidth() / 2;
        int y = PreviewConstants.HUD_TITLE_Y;

        TextRenderHelper.drawString(guiGraphics, mc.font, session.name(), x - mc.font.width(session.name()) / 2.0F, y, -1, false);

        int width = PreviewConstants.HUD_BAR_WIDTH;
        int height = PreviewConstants.HUD_BAR_HEIGHT;
        int left = x - width / 2;
        int top = y + 10;

        if (timeComplete > 0) {
            drawCompleteBanner(guiGraphics, mc, x, top, height, animTime);
        }

        drawProgressBar(guiGraphics, progress, left, top, width, height);

        if (!session.isAnchored()) {
            drawNotAnchored(guiGraphics, mc, x, top, height);
        } else {
            drawLookingStack(guiGraphics, mc, session, left, top, height);
            if (timeComplete == 0) {
                drawBuildCount(guiGraphics, mc, progress, left, top, width, height, x);
            }
            drawLayerInfo(guiGraphics, mc, session, progress, x, top, height);
        }

        guiGraphics.pose().popMatrix();
        return false;
    }

    private static void drawCompleteBanner(GuiGraphicsExtractor guiGraphics, Minecraft mc, int x, int top, int height, float animTime) {
        String s = I18n.get(ModonomiconConstants.I18n.Multiblock.COMPLETE);
        guiGraphics.pose().pushMatrix();
        guiGraphics.pose().translate(0, Math.min(height + 5, animTime));
        guiGraphics.text(mc.font, s, (int) (x - mc.font.width(s) / 2.0F), top + height - 10, 0xFF00FF00, false);
        guiGraphics.pose().popMatrix();
    }

    private static void drawProgressBar(GuiGraphicsExtractor guiGraphics, PreviewProgress progress, int left, int top, int width, int height) {
        //render a black square at the "bottom", 1px larger than the actual progress bar, so it acts as a border
        guiGraphics.fill(left - 1, top - 1, left + width + 1, top + height + 1, 0xFF000000);

        //then, on top of that, render a gray gradient as "empty progress"
        guiGraphics.fillGradient(left, top, left + width, top + height, 0xFF666666, 0xFF666666);

        float fract = (float) progress.blocksDone() / Math.max(1, progress.blocks());
        int progressWidth = (int) ((float) width * fract);
        int color = Mth.hsvToRgb(fract / 3.0F, 1.0F, 1.0F) | 0xFF000000;
        int color2 = new Color(color).darker().getRGB();

        //finally, on top of that, render a colored gradient as "filled progress"
        guiGraphics.fillGradient(left, top, left + progressWidth, top + height, color, color2);
    }

    private static void drawNotAnchored(GuiGraphicsExtractor guiGraphics, Minecraft mc, int x, int top, int height) {
        String s = I18n.get(ModonomiconConstants.I18n.Multiblock.NOT_ANCHORED);
        guiGraphics.text(mc.font, s, (int) (x - mc.font.width(s) / 2.0F), top + height + 8, 0xFFFFFFFF, false);
    }

    private static void drawLookingStack(GuiGraphicsExtractor guiGraphics, Minecraft mc, PreviewSession session, int left, int top, int height) {
        if (session.lookingState() == null) {
            return;
        }
        // try-catch around here because the state isn't necessarily present in the world in this instance,
        // which isn't really expected behavior for getPickBlock
        try {
            var stack = session.lookingState().getCloneItemStack(mc.level, session.lookingPos(), true);
            if (!stack.isEmpty()) {
                guiGraphics.text(mc.font, stack.getHoverName().copy(), left + 20, top + height + 8, 0xFFFFFFFF, false);

                guiGraphics.item(stack, left, top + height + 2);
            }
        } catch (Exception ignored) {
        }
    }

    private static void drawBuildCount(GuiGraphicsExtractor guiGraphics, Minecraft mc, PreviewProgress progress, int left, int top, int width, int height, int x) {
        int color = 0xFFFFFFFF;
        int posx = left + width;
        int posy = top + height + 2;
        float mult = 1;
        String count = progress.blocksDone() + "/" + progress.blocks();

        if (progress.blocksDone() == progress.blocks() && progress.airFilled() > 0) {
            count = I18n.get(ModonomiconConstants.I18n.Multiblock.REMOVE_BLOCKS);
            color = 0xFFDA4E3F;
            mult *= 2;
            posx -= width / 2;
            posy += 2;
        }

        guiGraphics.text(mc.font, count, (int) (posx - mc.font.width(count) / mult), posy, color, true);
    }

    private static void drawLayerInfo(GuiGraphicsExtractor guiGraphics, Minecraft mc, PreviewSession session, PreviewProgress progress, int x, int top, int height) {
        if (session.isLayerByLayer() && progress.totalLayers() > 0) {
            String layerInfo = I18n.get(ModonomiconConstants.I18n.Multiblock.LAYER_BY_LAYER, progress.currentLayer(), progress.totalLayers());
            guiGraphics.text(mc.font, layerInfo, (int) (x - mc.font.width(layerInfo) / 2.0F), top + height + 18, 0xFFFFFFFF, false);
        }
    }
}
