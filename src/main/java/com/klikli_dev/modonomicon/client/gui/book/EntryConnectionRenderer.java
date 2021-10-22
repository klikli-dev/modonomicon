/*
 * LGPL-3-0
 *
 * Copyright (C) 2021 klikli-dev
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

package com.klikli_dev.modonomicon.client.gui.book;

import com.klikli_dev.modonomicon.data.book.BookEntry;
import com.klikli_dev.modonomicon.data.book.BookEntryParent;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.GuiComponent;

import static java.lang.Math.*;

public class EntryConnectionRenderer {

    public float xOffset;
    public float yOffset;
    public int blitOffset;

    public EntryConnectionRenderer() {
    }

    public void render(PoseStack stack, BookEntry entry, BookEntryParent parent) {
        BookEntry parentEntry = parent.getEntry();

        //only render if line is enabled and if we are in the same category (other category -> other page!)
        if (parent.isLineEnabled() && parentEntry.getCategory().equals(entry.getCategory())) {
            RenderSystem.setShaderTexture(0, BookScreen.ENTRY_TEXTURES);

            int deltaX = abs(entry.getX() - parentEntry.getX());
            int deltaY = abs(entry.getY() - parentEntry.getY());

            //if the entries are in a line, just draw a line
            if (deltaX == 0 || deltaY == 0) {
                if (deltaX == 0) {
                    this.drawVerticalLine(stack, parentEntry.getX(), entry.getY(), parentEntry.getY());
                    if (parent.drawArrow()){
                        //move the arrow head one grid slot before the target, because it occupies 30x30
                        if (parentEntry.getY() > entry.getY())
                            this.drawUpArrow(stack, entry.getX(), entry.getY() + 1);
                        else
                            this.drawDownArrow(stack, entry.getX(), entry.getY() - 1);
                    }

                } else {
                    this.drawHorizontalLine(stack, parentEntry.getY(), entry.getX(), parentEntry.getX());
                    if (parent.drawArrow()){
                        //move the arrow head one grid slot before the target, because it occupies 30x30
                        if (parentEntry.getX() > entry.getX())
                            this.drawLeftArrow(stack, entry.getX() + 1, entry.getY());
                        else
                            this.drawRightArrow(stack, entry.getX() - 1, entry.getY());
                    }
                }
            }
        }
    }

    protected void setBlitOffset(int blitOffset) {
        this.blitOffset = blitOffset;
    }

    //TODO: don't we need to take the gap into account?

    protected void setOffset(float x, float y) {
        this.xOffset = x;
        this.yOffset = y;
    }

    /**
     * Scales from grid coordinates (1, 2, 3, ... ) to screen coordinates (30, 60, 90)
     */
    protected int screenX(int x) {
        return (int) (x * BookScreen.ENTRY_GRID_SCALE + this.xOffset);
    }

    /**
     * Scales from grid coordinates (1, 2, 3, ... ) to screen coordinates (30, 60, 90)
     */
    protected int screenY(int y) {
        return (int) (y * BookScreen.ENTRY_GRID_SCALE + this.yOffset);
    }

    protected void blit(PoseStack stack, int pX, int pY, float pUOffset, float pVOffset, int pUWidth, int pVHeight) {
        GuiComponent.blit(stack, pX, pY, this.blitOffset, pUOffset, pVOffset, pUWidth, pVHeight, 256, 256);
    }

    protected void smallCurveLeftDown(PoseStack stack, int x, int y) {
        //TODO: find correct uv offset for left down curve
        this.blit(stack, this.screenX(x), this.screenY(y), 0, 0, 30, 30);
    }

    void drawVerticalLineAt(PoseStack stack, int x, int y) {
        this.blit(stack, this.screenX(x), this.screenY(y), 180, 196, 30, 30);
    }

    void drawHorizontalLineAt(PoseStack stack, int x, int y) {
        this.blit(stack, this.screenX(x), this.screenY(y), 180, 227, 30, 30);
    }

    void drawVerticalLine(PoseStack stack, int x, int startY, int endY) {
        int temp = startY;

        //swap them if endY > startY
        startY = min(startY, endY);
        endY = max(endY, temp);

        for (int j = startY + 1; j < endY; j++)
            this.drawVerticalLineAt(stack, x, j);
    }

    void drawHorizontalLine(PoseStack stack, int y, int startX, int endX){
        int temp = startX;

        //swap them if endX > startX
        startX = min(startX, endX);
        endX = max(endX, temp);
        // *exclusive*
        for(int j = startX + 1; j < endX; j++){
            this.drawHorizontalLineAt(stack, j, y);
        }
    }

    void drawUpArrow(PoseStack stack, int x, int y){
        this.blit(stack, this.screenX(x), this.screenY(y) - 1, 0, 136, 30, 30);
    }

    void drawDownArrow(PoseStack stack, int x, int y){
        this.blit(stack, this.screenX(x), this.screenY(y) + 1, 0, 166, 30, 30);
    }

    void drawRightArrow(PoseStack stack, int x, int y){
        this.blit(stack, this.screenX(x) + 1, this.screenY(y), 30, 136, 30, 30);
    }

    void drawLeftArrow(PoseStack stack, int x, int y){
        this.blit(stack, this.screenX(x) - 1, this.screenY(y), 30, 166, 30, 30);
    }
}
