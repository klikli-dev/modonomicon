/*
 * LGPL-3.0
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

import com.klikli_dev.modonomicon.book.BookEntry;
import com.klikli_dev.modonomicon.book.BookEntryParent;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.GuiComponent;

import static java.lang.Math.*;

public class EntryConnectionRenderer {

    public int blitOffset;

    public EntryConnectionRenderer() {
    }

    public void renderLinedUpEntries(PoseStack stack, BookEntry entry, BookEntry parentEntry, BookEntryParent parent, boolean isVertical) {
        if (isVertical) {
            this.drawVerticalLine(stack, parentEntry.getX(), entry.getY(), parentEntry.getY());
            if (parent.drawArrow()) {
                //move the arrow head one grid slot before the target, because it occupies 30x30
                if (parentEntry.getY() > entry.getY())
                    this.drawUpArrow(stack, entry.getX(), entry.getY() + 1);
                else
                    this.drawDownArrow(stack, entry.getX(), entry.getY() - 1);
            }

        } else {
            this.drawHorizontalLine(stack, parentEntry.getY(), entry.getX(), parentEntry.getX());
            if (parent.drawArrow()) {
                //move the arrow head one grid slot before the target, because it occupies 30x30
                if (parentEntry.getX() > entry.getX())
                    this.drawLeftArrow(stack, entry.getX() + 1, entry.getY());
                else
                    this.drawRightArrow(stack, entry.getX() - 1, entry.getY());
            }
        }
    }

    public void renderSmallCurves(PoseStack stack, BookEntry entry, BookEntry parentEntry, BookEntryParent parent) {
        this.drawVerticalLine(stack, entry.getX(), parentEntry.getY(), entry.getY());
        this.drawHorizontalLine(stack, parentEntry.getY(), parentEntry.getX(), entry.getX());
        if (entry.getX() > parentEntry.getX()) {
            if (entry.getY() > parentEntry.getY()) {
                this.drawSmallCurveLeftDown(stack, entry.getX(), parentEntry.getY());
                if (parent.drawArrow())
                    this.drawDownArrow(stack, entry.getX(), entry.getY() - 1);
            } else {
                this.drawSmallCurveLeftUp(stack, entry.getX(), parentEntry.getY());
                if (parent.drawArrow())
                    this.drawUpArrow(stack, entry.getX(), entry.getY() + 1);
            }
        } else {
            if (entry.getY() > parentEntry.getY()) {
                this.drawSmallCurveRightDown(stack, entry.getX(), parentEntry.getY());
                if (parent.drawArrow())
                    this.drawDownArrow(stack, entry.getX(), entry.getY() - 1);
            } else {
                this.drawSmallCurveRightUp(stack, entry.getX(), parentEntry.getY());
                if (parent.drawArrow())
                    this.drawUpArrow(stack, entry.getX(), entry.getY() + 1);
            }
        }
    }

    public void renderSmallCurvesReversed(PoseStack stack, BookEntry entry, BookEntry parentEntry, BookEntryParent parent) {
        this.drawHorizontalLine(stack, entry.getY(), entry.getX(), parentEntry.getX());
        this.drawVerticalLine(stack, parentEntry.getX(), parentEntry.getY(), entry.getY());
        if (entry.getX() > parentEntry.getX()) {
            if (entry.getY() > parentEntry.getY()) {
                this.drawSmallCurveRightUp(stack, parentEntry.getX(), entry.getY());
                if (parent.drawArrow())
                    this.drawRightArrow(stack, entry.getX() - 1, entry.getY());
            } else {
                this.drawSmallCurveRightDown(stack, entry.getX() - 1, parentEntry.getY() - 1);
                if (parent.drawArrow())
                    this.drawRightArrow(stack, entry.getX() - 1, entry.getY());
            }
        } else {
            if (entry.getY() > parentEntry.getY()) {
                this.drawSmallCurveLeftUp(stack, entry.getX() + 1, entry.getY());
                if (parent.drawArrow())
                    this.drawLeftArrow(stack, entry.getX() + 1, entry.getY());
            } else {
                this.drawSmallCurveLeftDown(stack, entry.getX() + 1, parentEntry.getY() - 1);
                if (parent.drawArrow())
                    this.drawLeftArrow(stack, entry.getX() + 1, entry.getY());
            }
        }
    }

    public void renderLargeCurves(PoseStack stack, BookEntry entry, BookEntry parentEntry, BookEntryParent parent) {
        this.drawHorizontalLineShortened(stack, parentEntry.getY(), parentEntry.getX(), entry.getX());
        this.drawVerticalLineShortened(stack, entry.getX(), entry.getY(), parentEntry.getY());
        if (entry.getX() > parentEntry.getX()) {
            if (entry.getY() > parentEntry.getY()) {
                this.drawLargeCurveLeftDown(stack, entry.getX() - 1, parentEntry.getY());
                if (parent.drawArrow())
                    this.drawDownArrow(stack, entry.getX(), entry.getY() - 1);
            } else {
                this.drawLargeCurveLeftUp(stack, entry.getX() - 1, parentEntry.getY() - 1);
                if (parent.drawArrow())
                    this.drawUpArrow(stack, entry.getX(), entry.getY() + 1);
            }
        } else {
            if (entry.getY() > parentEntry.getY()) {
                this.drawLargeCurveRightDown(stack, entry.getX(), parentEntry.getY());
                if (parent.drawArrow())
                    this.drawDownArrow(stack, entry.getX(), entry.getY() - 1);
            } else {
                this.drawLargeCurveRightUp(stack, entry.getX(), parentEntry.getY() - 1);
                if (parent.drawArrow())
                    this.drawUpArrow(stack, entry.getX(), entry.getY() + 1);
            }
        }
    }

    public void renderLargeCurvesReversed(PoseStack stack, BookEntry entry, BookEntry parentEntry, BookEntryParent parent) {
        this.drawHorizontalLineShortened(stack, entry.getY(), entry.getX(), parentEntry.getX());
        this.drawVerticalLineShortened(stack, parentEntry.getX(), parentEntry.getY(), entry.getY());
        if (entry.getX() > parentEntry.getX()) {
            if (entry.getY() > parentEntry.getY())
                this.drawLargeCurveRightUp(stack, parentEntry.getX(), entry.getY() - 1);
            else
                this.drawLargeCurveRightDown(stack, parentEntry.getX(), entry.getY());
            if (parent.drawArrow())
                this.drawRightArrow(stack, entry.getX() - 1, entry.getY());
        } else {
            if (entry.getY() > parentEntry.getY())
                this.drawLargeCurveLeftUp(stack, entry.getX() + 1, entry.getY() - 1);
            else
                this.drawLargeCurveLeftDown(stack, entry.getX() + 1, entry.getY());
            if (parent.drawArrow())
                this.drawLeftArrow(stack, entry.getX() + 1, entry.getY());
        }
    }

    public void render(PoseStack stack, BookEntry entry, BookEntryParent parent) {
        BookEntry parentEntry = parent.getEntry();

        //only render if line is enabled and if we are in the same category (other category -> other page!)
        if (parent.isLineEnabled() && parentEntry.getCategory().equals(entry.getCategory())) {
            int deltaX = abs(entry.getX() - parentEntry.getX());
            int deltaY = abs(entry.getY() - parentEntry.getY());

            if (deltaX == 0 || deltaY == 0) {
                //if the entries are in a line, just draw a line
                this.renderLinedUpEntries(stack, entry, parentEntry, parent, deltaX == 0);
            } else {
                if (deltaX < 2 || deltaY < 2) {
                    if(!parent.isLineReversed()){
                        this.renderSmallCurves(stack, entry, parentEntry, parent);
                    }
                    else {
                        this.renderSmallCurvesReversed(stack, entry, parentEntry, parent);
                    }
                } else {
                    if(!parent.isLineReversed()){
                        this.renderLargeCurves(stack, entry, parentEntry, parent);
                    }
                    else {
                        this.renderLargeCurvesReversed(stack, entry, parentEntry, parent);
                    }
                }
            }
        }
    }

    protected void setBlitOffset(int blitOffset) {
        this.blitOffset = blitOffset;
    }

    /**
     * Scales from grid coordinates (1, 2, 3, ... ) to screen coordinates (30, 60, 90)
     */
    protected int screenX(int x) {
        return x * BookCategoryScreen.ENTRY_GRID_SCALE;
    }

    /**
     * Scales from grid coordinates (1, 2, 3, ... ) to screen coordinates (30, 60, 90)
     */
    protected int screenY(int y) {
        return y * BookCategoryScreen.ENTRY_GRID_SCALE;
    }

    protected void blit(PoseStack stack, int pX, int pY, float pUOffset, float pVOffset, int pUWidth, int pVHeight) {
        GuiComponent.blit(stack, pX, pY, this.blitOffset, pUOffset, pVOffset, pUWidth, pVHeight, 256, 256);
    }

    protected void drawSmallCurveLeftDown(PoseStack stack, int x, int y) {
        this.blit(stack, this.screenX(x), this.screenY(y), 0, 226, 30, 30);
    }

    protected void drawSmallCurveRightDown(PoseStack stack, int x, int y) {
        this.blit(stack, this.screenX(x), this.screenY(y), 30, 226, 30, 30);
    }

    protected void drawSmallCurveLeftUp(PoseStack stack, int x, int y) {
        this.blit(stack, this.screenX(x), this.screenY(y), 0, 196, 30, 30);
    }

    protected void drawSmallCurveRightUp(PoseStack stack, int x, int y) {
        this.blit(stack, this.screenX(x), this.screenY(y), 30, 196, 30, 30);
    }

    protected void drawLargeCurveLeftDown(PoseStack stack, int x, int y) {
        this.blit(stack, this.screenX(x), this.screenY(y), 62, 196, 60, 60);
    }

    protected void drawLargeCurveRightDown(PoseStack stack, int x, int y) {
        this.blit(stack, this.screenX(x), this.screenY(y), 122, 196, 60, 60);
    }

    protected void drawLargeCurveLeftUp(PoseStack stack, int x, int y) {
        this.blit(stack, this.screenX(x), this.screenY(y), 62, 134, 60, 60);
    }

    protected void drawLargeCurveRightUp(PoseStack stack, int x, int y) {
        this.blit(stack, this.screenX(x), this.screenY(y), 122, 134, 60, 60);
    }

    void drawVerticalLineAt(PoseStack stack, int x, int y) {
        this.blit(stack, this.screenX(x), this.screenY(y), 184, 164, 30, 31);
    }

    void drawHorizontalLineAt(PoseStack stack, int x, int y) {
        this.blit(stack, this.screenX(x), this.screenY(y), 184, 226, 31, 30);
    }

    void drawVerticalLine(PoseStack stack, int x, int startY, int endY) {
        int temp = startY;

        //swap them if endY > startY
        startY = min(startY, endY);
        endY = max(endY, temp);

        for (int j = startY + 1; j < endY; j++)
            this.drawVerticalLineAt(stack, x, j);
    }

    void drawHorizontalLine(PoseStack stack, int y, int startX, int endX) {
        int temp = startX;

        //swap them if endX > startX
        startX = min(startX, endX);
        endX = max(endX, temp);
        // *exclusive*
        for (int j = startX + 1; j < endX; j++) {
            this.drawHorizontalLineAt(stack, j, y);
        }
    }

    void drawHorizontalLineShortened(PoseStack stack, int y, int startX, int endX){
        int temp = startX;

        // reduce length by one
        if(startX > endX)
            endX++;
        else
            endX--;

        //swap them if endX > startX
        startX = min(startX, endX);
        endX = max(endX, temp);

        for(int j = startX + 1; j < endX; j++)
            this.drawHorizontalLineAt(stack, j, y);
    }

    void drawVerticalLineShortened(PoseStack stack, int x, int startY, int endY){
        int temp = startY;

        // reduce length by one
        if(startY > endY)
            endY++;
        else
            endY--;

        //swap them if endY > startY
        startY = min(startY, endY);
        endY = max(endY, temp);

        for(int j = startY + 1; j < endY; j++)
            this.drawVerticalLineAt(stack, x, j);
    }


    void drawUpArrow(PoseStack stack, int x, int y) {
        this.blit(stack, this.screenX(x), this.screenY(y) - 1, 0, 134, 30, 30);
    }

    void drawDownArrow(PoseStack stack, int x, int y) {
        this.blit(stack, this.screenX(x), this.screenY(y) + 1, 0, 164, 30, 30);
    }

    void drawRightArrow(PoseStack stack, int x, int y) {
        this.blit(stack, this.screenX(x) + 1, this.screenY(y), 30, 134, 30, 30);
    }

    void drawLeftArrow(PoseStack stack, int x, int y) {
        this.blit(stack, this.screenX(x) - 1, this.screenY(y), 30, 164, 30, 30);
    }
}
