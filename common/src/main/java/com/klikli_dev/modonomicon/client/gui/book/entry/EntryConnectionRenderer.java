/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 * SPDX-FileCopyrightText: 2021 Authors of Arcana
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.client.gui.book.entry;

import com.klikli_dev.modonomicon.book.BookEntryParent;
import com.klikli_dev.modonomicon.book.entries.BookEntry;
import com.klikli_dev.modonomicon.client.gui.book.node.BookCategoryNodeScreen;
import com.klikli_dev.modonomicon.client.gui.book.theme.BookNodeTheme;
import com.klikli_dev.modonomicon.client.gui.book.theme.GuiSprite;
import com.klikli_dev.modonomicon.util.GuiGraphicsExt;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;

import static java.lang.Math.*;

public class EntryConnectionRenderer {

    public int blitOffset;
    public BookNodeTheme nodeTheme;

    public EntryConnectionRenderer(BookNodeTheme nodeTheme) {
        this.nodeTheme = nodeTheme;
    }

    public void renderLinedUpEntries(GuiGraphicsExtractor guiGraphics, BookEntry entry, BookEntry parentEntry, BookEntryParent parent, boolean isVertical) {
        if (isVertical) {
            this.drawVerticalLine(guiGraphics, parentEntry.getX(), entry.getY(), parentEntry.getY());
            if (parent.drawArrow()) {
                //move the arrow head one grid slot before the target, because it occupies 30x30
                if (parentEntry.getY() > entry.getY())
                    this.drawUpArrow(guiGraphics, entry.getX(), entry.getY() + 1);
                else
                    this.drawDownArrow(guiGraphics, entry.getX(), entry.getY() - 1);
            }

        } else {
            this.drawHorizontalLine(guiGraphics, parentEntry.getY(), entry.getX(), parentEntry.getX());
            if (parent.drawArrow()) {
                //move the arrow head one grid slot before the target, because it occupies 30x30
                if (parentEntry.getX() > entry.getX())
                    this.drawLeftArrow(guiGraphics, entry.getX() + 1, entry.getY());
                else
                    this.drawRightArrow(guiGraphics, entry.getX() - 1, entry.getY());
            }
        }
    }

    public void renderSmallCurves(GuiGraphicsExtractor guiGraphics, BookEntry entry, BookEntry parentEntry, BookEntryParent parent) {
        this.drawVerticalLine(guiGraphics, entry.getX(), parentEntry.getY(), entry.getY());
        this.drawHorizontalLine(guiGraphics, parentEntry.getY(), parentEntry.getX(), entry.getX());
        if (entry.getX() > parentEntry.getX()) {
            if (entry.getY() > parentEntry.getY()) {
                this.drawSmallCurveLeftDown(guiGraphics, entry.getX(), parentEntry.getY());
                if (parent.drawArrow())
                    this.drawDownArrow(guiGraphics, entry.getX(), entry.getY() - 1);
            } else {
                this.drawSmallCurveLeftUp(guiGraphics, entry.getX(), parentEntry.getY());
                if (parent.drawArrow())
                    this.drawUpArrow(guiGraphics, entry.getX(), entry.getY() + 1);
            }
        } else {
            if (entry.getY() > parentEntry.getY()) {
                this.drawSmallCurveRightDown(guiGraphics, entry.getX(), parentEntry.getY());
                if (parent.drawArrow())
                    this.drawDownArrow(guiGraphics, entry.getX(), entry.getY() - 1);
            } else {
                this.drawSmallCurveRightUp(guiGraphics, entry.getX(), parentEntry.getY());
                if (parent.drawArrow())
                    this.drawUpArrow(guiGraphics, entry.getX(), entry.getY() + 1);
            }
        }
    }

    public void renderSmallCurvesReversed(GuiGraphicsExtractor guiGraphics, BookEntry entry, BookEntry parentEntry, BookEntryParent parent) {
        this.drawHorizontalLine(guiGraphics, entry.getY(), entry.getX(), parentEntry.getX());
        this.drawVerticalLine(guiGraphics, parentEntry.getX(), parentEntry.getY(), entry.getY());
        if (entry.getX() < parentEntry.getX()) {
            if (entry.getY() > parentEntry.getY()) {
                this.drawSmallCurveLeftUp(guiGraphics, parentEntry.getX(), entry.getY());
                if (parent.drawArrow())
                    this.drawLeftArrow(guiGraphics, entry.getX() + 1, entry.getY());
            } else {
                this.drawSmallCurveLeftDown(guiGraphics, parentEntry.getX(), entry.getY());
                if (parent.drawArrow())
                    this.drawLeftArrow(guiGraphics, entry.getX() + 1, entry.getY());
            }
        } else {
            if (entry.getY() > parentEntry.getY()) {
                this.drawSmallCurveRightUp(guiGraphics, parentEntry.getX(), entry.getY());
                if (parent.drawArrow())
                    this.drawRightArrow(guiGraphics, entry.getX() - 1, entry.getY());
            } else {
                this.drawSmallCurveRightDown(guiGraphics, parentEntry.getX(), entry.getY());
                if (parent.drawArrow())
                    this.drawRightArrow(guiGraphics, entry.getX() - 1, entry.getY());
            }
        }
    }

    public void renderLargeCurves(GuiGraphicsExtractor guiGraphics, BookEntry entry, BookEntry parentEntry, BookEntryParent parent) {
        this.drawHorizontalLineShortened(guiGraphics, parentEntry.getY(), parentEntry.getX(), entry.getX());
        this.drawVerticalLineShortened(guiGraphics, entry.getX(), entry.getY(), parentEntry.getY());
        if (entry.getX() > parentEntry.getX()) {
            if (entry.getY() > parentEntry.getY()) {
                this.drawLargeCurveLeftDown(guiGraphics, entry.getX() - 1, parentEntry.getY());
                if (parent.drawArrow())
                    this.drawDownArrow(guiGraphics, entry.getX(), entry.getY() - 1);
            } else {
                this.drawLargeCurveLeftUp(guiGraphics, entry.getX() - 1, parentEntry.getY() - 1);
                if (parent.drawArrow())
                    this.drawUpArrow(guiGraphics, entry.getX(), entry.getY() + 1);
            }
        } else {
            if (entry.getY() > parentEntry.getY()) {
                this.drawLargeCurveRightDown(guiGraphics, entry.getX(), parentEntry.getY());
                if (parent.drawArrow())
                    this.drawDownArrow(guiGraphics, entry.getX(), entry.getY() - 1);
            } else {
                this.drawLargeCurveRightUp(guiGraphics, entry.getX(), parentEntry.getY() - 1);
                if (parent.drawArrow())
                    this.drawUpArrow(guiGraphics, entry.getX(), entry.getY() + 1);
            }
        }
    }

    public void renderLargeCurvesReversed(GuiGraphicsExtractor guiGraphics, BookEntry entry, BookEntry parentEntry, BookEntryParent parent) {
        this.drawHorizontalLineShortened(guiGraphics, entry.getY(), entry.getX(), parentEntry.getX());
        this.drawVerticalLineShortened(guiGraphics, parentEntry.getX(), parentEntry.getY(), entry.getY());
        if (entry.getX() > parentEntry.getX()) {
            if (entry.getY() > parentEntry.getY())
                this.drawLargeCurveRightUp(guiGraphics, parentEntry.getX(), entry.getY() - 1);
            else
                this.drawLargeCurveRightDown(guiGraphics, parentEntry.getX(), entry.getY());
            if (parent.drawArrow())
                this.drawRightArrow(guiGraphics, entry.getX() - 1, entry.getY());
        } else {
            if (entry.getY() > parentEntry.getY())
                this.drawLargeCurveLeftUp(guiGraphics, parentEntry.getX() - 1, parentEntry.getY() + 1);
            else
                this.drawLargeCurveLeftDown(guiGraphics, parentEntry.getX() - 1, entry.getY());
            if (parent.drawArrow())
                this.drawLeftArrow(guiGraphics, entry.getX() + 1, entry.getY());
        }
    }

    public void render(GuiGraphicsExtractor guiGraphics, BookEntry entry, BookEntryParent parent) {
        BookEntry parentEntry = parent.getEntry();

        //only render if line is enabled and if we are in the same category (other category -> other page!)
        if (parent.isLineEnabled() && parentEntry.getCategory().equals(entry.getCategory())) {
            int deltaX = abs(entry.getX() - parentEntry.getX());
            int deltaY = abs(entry.getY() - parentEntry.getY());

            if (deltaX == 0 || deltaY == 0) {
                //if the entries are in a line, just draw a line
                this.renderLinedUpEntries(guiGraphics, entry, parentEntry, parent, deltaX == 0);
            } else {
                if (deltaX < 2 || deltaY < 2) {
                    if (!parent.isLineReversed()) {
                        this.renderSmallCurves(guiGraphics, entry, parentEntry, parent);
                    } else {
                        this.renderSmallCurvesReversed(guiGraphics, entry, parentEntry, parent);
                    }
                } else {
                    if (!parent.isLineReversed()) {
                        this.renderLargeCurves(guiGraphics, entry, parentEntry, parent);
                    } else {
                        this.renderLargeCurvesReversed(guiGraphics, entry, parentEntry, parent);
                    }
                }
            }
        }
    }

    /**
     * Scales from grid coordinates (1, 2, 3, ... ) to screen coordinates (30, 60, 90)
     */
    protected int screenX(int x) {
        return x * BookCategoryNodeScreen.ENTRY_GRID_SCALE;
    }

    /**
     * Scales from grid coordinates (1, 2, 3, ... ) to screen coordinates (30, 60, 90)
     */
    protected int screenY(int y) {
        return y * BookCategoryNodeScreen.ENTRY_GRID_SCALE;
    }

    protected void blit(GuiGraphicsExtractor guiGraphics, GuiSprite sprite, int pX, int pY) {
        GuiGraphicsExt.blitSprite(guiGraphics, RenderPipelines.GUI_TEXTURED, sprite, pX, pY);
    }

    protected void drawSmallCurveLeftDown(GuiGraphicsExtractor guiGraphics, int x, int y) {
        this.blit(guiGraphics, this.nodeTheme.smallCurveLeftDown(), this.screenX(x), this.screenY(y));
    }

    protected void drawSmallCurveRightDown(GuiGraphicsExtractor guiGraphics, int x, int y) {
        this.blit(guiGraphics, this.nodeTheme.smallCurveRightDown(), this.screenX(x), this.screenY(y));
    }

    protected void drawSmallCurveLeftUp(GuiGraphicsExtractor guiGraphics, int x, int y) {
        this.blit(guiGraphics, this.nodeTheme.smallCurveLeftUp(), this.screenX(x), this.screenY(y));
    }

    protected void drawSmallCurveRightUp(GuiGraphicsExtractor guiGraphics, int x, int y) {
        this.blit(guiGraphics, this.nodeTheme.smallCurveRightUp(), this.screenX(x), this.screenY(y));
    }

    protected void drawLargeCurveLeftDown(GuiGraphicsExtractor guiGraphics, int x, int y) {
        this.blit(guiGraphics, this.nodeTheme.largeCurveLeftDown(), this.screenX(x), this.screenY(y));
    }

    protected void drawLargeCurveRightDown(GuiGraphicsExtractor guiGraphics, int x, int y) {
        this.blit(guiGraphics, this.nodeTheme.largeCurveRightDown(), this.screenX(x), this.screenY(y));
    }

    protected void drawLargeCurveLeftUp(GuiGraphicsExtractor guiGraphics, int x, int y) {
        this.blit(guiGraphics, this.nodeTheme.largeCurveLeftUp(), this.screenX(x), this.screenY(y));
    }

    protected void drawLargeCurveRightUp(GuiGraphicsExtractor guiGraphics, int x, int y) {
        this.blit(guiGraphics, this.nodeTheme.largeCurveRightUp(), this.screenX(x), this.screenY(y));
    }

    void drawVerticalLineAt(GuiGraphicsExtractor guiGraphics, int x, int y) {
        this.blit(guiGraphics, this.nodeTheme.verticalLine(), this.screenX(x), this.screenY(y));
    }

    void drawHorizontalLineAt(GuiGraphicsExtractor guiGraphics, int x, int y) {
        this.blit(guiGraphics, this.nodeTheme.horizontalLine(), this.screenX(x), this.screenY(y));
    }

    void drawVerticalLine(GuiGraphicsExtractor guiGraphics, int x, int startY, int endY) {
        int temp = startY;

        //swap them if endY > startY
        startY = min(startY, endY);
        endY = max(endY, temp);

        for (int j = startY + 1; j < endY; j++)
            this.drawVerticalLineAt(guiGraphics, x, j);
    }

    void drawHorizontalLine(GuiGraphicsExtractor guiGraphics, int y, int startX, int endX) {
        int temp = startX;

        //swap them if endX > startX
        startX = min(startX, endX);
        endX = max(endX, temp);
        // *exclusive*
        for (int j = startX + 1; j < endX; j++) {
            this.drawHorizontalLineAt(guiGraphics, j, y);
        }
    }

    void drawHorizontalLineShortened(GuiGraphicsExtractor guiGraphics, int y, int startX, int endX) {
        int temp = startX;

        // reduce length by one
        if (startX > endX)
            endX++;
        else
            endX--;

        //swap them if endX > startX
        startX = min(startX, endX);
        endX = max(endX, temp);

        for (int j = startX + 1; j < endX; j++)
            this.drawHorizontalLineAt(guiGraphics, j, y);
    }

    void drawVerticalLineShortened(GuiGraphicsExtractor guiGraphics, int x, int startY, int endY) {
        int temp = startY;

        // reduce length by one
        if (startY > endY)
            endY++;
        else
            endY--;

        //swap them if endY > startY
        startY = min(startY, endY);
        endY = max(endY, temp);

        for (int j = startY + 1; j < endY; j++)
            this.drawVerticalLineAt(guiGraphics, x, j);
    }


    void drawUpArrow(GuiGraphicsExtractor guiGraphics, int x, int y) {
        this.blit(guiGraphics, this.nodeTheme.upArrow(), this.screenX(x), this.screenY(y) - 1);
    }

    void drawDownArrow(GuiGraphicsExtractor guiGraphics, int x, int y) {
        this.blit(guiGraphics, this.nodeTheme.downArrow(), this.screenX(x), this.screenY(y) + 1);
    }

    void drawRightArrow(GuiGraphicsExtractor guiGraphics, int x, int y) {
        this.blit(guiGraphics, this.nodeTheme.rightArrow(), this.screenX(x) + 1, this.screenY(y));
    }

    void drawLeftArrow(GuiGraphicsExtractor guiGraphics, int x, int y) {
        this.blit(guiGraphics, this.nodeTheme.leftArrow(), this.screenX(x) - 1, this.screenY(y));
    }
}
