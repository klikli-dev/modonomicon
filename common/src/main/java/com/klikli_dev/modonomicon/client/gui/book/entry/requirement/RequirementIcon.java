/*
 * SPDX-FileCopyrightText: 2026 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.client.gui.book.entry.requirement;

import com.klikli_dev.modonomicon.book.BookIcon;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

/**
 * A single icon of a requirement row, either an item stack or a book icon.
 *
 * @param stack    the item stack to render, or null
 * @param bookIcon the book icon to render, or null
 */
public record RequirementIcon(@Nullable ItemStack stack, @Nullable BookIcon bookIcon) {

    public static RequirementIcon of(ItemStack stack) {
        return new RequirementIcon(stack, null);
    }

    public static RequirementIcon of(BookIcon bookIcon) {
        return new RequirementIcon(null, bookIcon);
    }

    public void render(GuiGraphicsExtractor guiGraphics, Font font, int x, int y) {
        if (this.stack != null) {
            guiGraphics.item(this.stack, x, y);
            guiGraphics.itemDecorations(font, this.stack, x, y);
        } else if (this.bookIcon != null) {
            this.bookIcon.render(guiGraphics, x, y);
        }
    }
}
