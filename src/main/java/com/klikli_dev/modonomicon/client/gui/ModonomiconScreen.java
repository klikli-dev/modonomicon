/*
 * MIT License
 *
 * Copyright 2021 klikli-dev
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
 * of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following
 * conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial
 * portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 * PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT
 * OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 * OTHER DEALINGS IN THE SOFTWARE.
 */

package com.klikli_dev.modonomicon.client.gui;

import com.klikli_dev.modonomicon.Modonomicon;
import com.klikli_dev.modonomicon.api.ModonimiconConstants;
import com.klikli_dev.modonomicon.data.BookData;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fmlclient.gui.GuiUtils;

public class ModonomiconScreen extends Screen {

    protected ItemStack bookStack;
    protected BookData bookData;
    protected ResourceLocation frameTexture;


    public ModonomiconScreen(BookData bookData, ItemStack bookStack) {
        super(new TextComponent(""));

        //somehow there are render calls before init(), leaving minecraft null
        this.minecraft = Minecraft.getInstance();

        this.bookStack = bookStack;
        this.bookData = bookData;
        //TODO: frameTexture load from book data
        this.frameTexture = new ResourceLocation(Modonomicon.MODID, "textures/gui/book.png");
    }

    protected void renderFrame(PoseStack poseStack) {
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, this.frameTexture);

        int width = this.getFrameWidth();
        int height = this.getFrameHeight();
        int x = (this.width - width) / 2;
        int y = (this.height - height) / 2;

        int blitOffset = this.getBlitOffset();

        //draw a resizeable border. Center parts of each side will be stretched
        //the exact border size mostly does not matter because the center is empty anyway, but 50 gives a lot of flexiblity
        GuiUtils.drawContinuousTexturedBox(poseStack, x, y, 0, 0, width, height,
                140, 140, 50, 50, 50, 50, blitOffset);

        //now draw center parts of each side. This allows to add non-repeat features here or just generally bridge the repeated pixesl
        //top, bottom, left, right
        //these parts are to the left of the repeatable frame in the texture.
        GuiUtils.drawTexturedModalRect(poseStack, (x + (width / 2)) - 36, y, 140, 0, 72, 17, blitOffset);
        GuiUtils.drawTexturedModalRect(poseStack, (x + (width / 2)) - 36, (y + height) - 18, 140, 17, 72, 18, blitOffset);
        GuiUtils.drawTexturedModalRect(poseStack, x, (y + (height / 2)) - 35, 140, 35, 17, 70, blitOffset);
        GuiUtils.drawTexturedModalRect(poseStack, x + width - 17, (y + (height / 2)) - 35, 157, 35, 17, 70, blitOffset);
    }

    protected int getFrameWidth() {
        //TODO: enable config frame width
        return this.width - 60;
    }

    protected int getFrameHeight() {
        //TODO: enable config frame height
        return this.height - 20;
    }

    @Override
    public void render(PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTick) {
        this.renderBackground(pPoseStack);
        super.render(pPoseStack, pMouseX, pMouseY, pPartialTick);

        this.renderFrame(pPoseStack);
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return true;
    }

    @Override
    public void onClose() {
        if (!this.bookStack.isEmpty())
            this.bookStack.getOrCreateTag().putBoolean(ModonimiconConstants.Nbt.BOOK_OPEN, false);
        super.onClose();
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
