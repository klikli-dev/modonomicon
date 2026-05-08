/*
 * SPDX-FileCopyrightText: 2026 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.client.gui.book.entry;

import com.klikli_dev.modonomicon.book.entries.BookEntry;
import com.klikli_dev.modonomicon.client.ClientTicks;
import com.klikli_dev.modonomicon.client.gui.book.node.BookCategoryNodeScreen;
import com.klikli_dev.modonomicon.client.render.state.pip.GuiDirectEntryConnectionRenderState;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.util.ARGB;

import java.util.ArrayList;
import java.util.List;

public class DirectEntryConnectionRenderer {
    private final float lineWidth;
    private final float visibilityMultiplier;
    private static final int CONNECTED_COLOR = ARGB.colorFromFloat(1.0F, 1.0F, 1.0F, 1.0F);
    private static final int AVAILABLE_COLOR = ARGB.colorFromFloat(1.0F, 0.0F, 1.0F, 0.0F);
    private static final int DISCOVERED_COLOR = ARGB.colorFromFloat(1.0F, 0.0F, 0.0F, 1.0F);
    private static final float ENTRY_CENTER_OFFSET = BookCategoryNodeScreen.ENTRY_GAP + BookCategoryNodeScreen.ENTRY_WIDTH / 2.0F;

    public DirectEntryConnectionRenderer(float lineWidth, float visibilityMultiplier) {
        this.lineWidth = lineWidth;
        this.visibilityMultiplier = visibilityMultiplier;
    }

    public void render(GuiGraphicsExtractor guiGraphics, BookCategoryNodeScreen screen) {
        int innerX = screen.getInnerX();
        int innerY = screen.getInnerY();
        int innerWidth = screen.getInnerWidth();
        int innerHeight = screen.getInnerHeight();

        if (innerWidth <= 0 || innerHeight <= 0) {
            return;
        }

        float xOffset = screen.getXOffset();
        float yOffset = screen.getYOffset();
        float zoom = screen.getCurrentZoom();
        float centerX = innerX + innerWidth / 2.0F;
        float centerY = innerY + innerHeight / 2.0F;

        List<GuiDirectEntryConnectionRenderState.Connection> connections = new ArrayList<>();
        for (var entry : screen.getCategory().getEntries().values()) {
            EntryDisplayState entryDisplayState = screen.getEntryDisplayState(entry);
            if (entryDisplayState == EntryDisplayState.HIDDEN) {
                continue;
            }

            for (var parent : entry.getParents()) {
                if (!parent.isLineEnabled()) {
                    continue;
                }

                BookEntry parentEntry = parent.getEntry();
                if (!parentEntry.getCategory().equals(entry.getCategory())) {
                    continue;
                }

                EntryDisplayState parentDisplayState = screen.getEntryDisplayState(parentEntry);
                if (parentDisplayState == EntryDisplayState.HIDDEN) {
                    continue;
                }

                float startX = this.getScreenCenterX(entry, xOffset, zoom) - centerX;
                float startY = this.getScreenCenterY(entry, yOffset, zoom) - centerY;
                float endX = this.getScreenCenterX(parentEntry, xOffset, zoom) - centerX;
                float endY = this.getScreenCenterY(parentEntry, yOffset, zoom) - centerY;

                if (Math.abs(startX - endX) < 0.001F && Math.abs(startY - endY) < 0.001F) {
                    continue;
                }

                connections.add(new GuiDirectEntryConnectionRenderState.Connection(
                        startX,
                        startY,
                        endX,
                        endY,
                        this.getConnectionColor(entryDisplayState, parentDisplayState),
                        entryDisplayState != EntryDisplayState.UNLOCKED
                ));
            }
        }

        if (connections.isEmpty()) {
            return;
        }

        guiGraphics.guiRenderState.addPicturesInPictureState(new GuiDirectEntryConnectionRenderState(
                List.copyOf(connections),
                ClientTicks.total,
                this.lineWidth,
                this.visibilityMultiplier,
                innerX,
                innerY,
                innerX + innerWidth,
                innerY + innerHeight,
                1.0F,
                guiGraphics.scissorStack.peek()
        ));
    }

    private int getConnectionColor(EntryDisplayState entryDisplayState, EntryDisplayState parentDisplayState) {
        if (entryDisplayState == EntryDisplayState.UNLOCKED) {
            return CONNECTED_COLOR;
        }
        if (parentDisplayState == EntryDisplayState.UNLOCKED) {
            return AVAILABLE_COLOR;
        }
        return DISCOVERED_COLOR;
    }

    private float getScreenCenterX(BookEntry entry, float xOffset, float zoom) {
        return (xOffset + entry.getX() * BookCategoryNodeScreen.ENTRY_GRID_SCALE + ENTRY_CENTER_OFFSET) * zoom;
    }

    private float getScreenCenterY(BookEntry entry, float yOffset, float zoom) {
        return (yOffset + entry.getY() * BookCategoryNodeScreen.ENTRY_GRID_SCALE + ENTRY_CENTER_OFFSET) * zoom;
    }
}
