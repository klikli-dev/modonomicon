/*
 * SPDX-FileCopyrightText: 2026 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.client.gui.book.entry.requirement;

import com.klikli_dev.modonomicon.book.BookIcon;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

/**
 * A single icon of a requirement row, either an item stack or a book icon, optionally decorated by a badge.
 * <p>
 * A badge is a texture of {@link ResearchIcons#BADGE_SIZE} pixels, drawn at the bottom right corner of the icon above
 * the item decorations. Addons can supply badges via {@link #withBadge(Identifier)}.
 *
 * @param stack    the item stack to render, or null
 * @param bookIcon the book icon to render, or null
 * @param badge    the texture of the badge to draw over the icon, or null
 */
public record RequirementIcon(@Nullable ItemStack stack, @Nullable BookIcon bookIcon, @Nullable Identifier badge) {

    /**
     * An icon showing the item of the stack. The stack count is not shown: the requirement renderer draws the target
     * count itself and replaces it with a checkmark once the requirement is met.
     */
    public static RequirementIcon of(ItemStack stack) {
        return new RequirementIcon(stack.getCount() > 1 ? stack.copyWithCount(1) : stack, null, null);
    }

    public static RequirementIcon of(BookIcon bookIcon) {
        return new RequirementIcon(null, bookIcon, null);
    }

    /**
     * A copy of this icon that shows the given badge, replacing any badge it had.
     *
     * @param badge the texture of the badge, see {@link ResearchIcons#texture(String)}, or null to remove the badge
     */
    public RequirementIcon withBadge(@Nullable Identifier badge) {
        return new RequirementIcon(this.stack, this.bookIcon, badge);
    }

    public void render(GuiGraphicsExtractor guiGraphics, Font font, int x, int y) {
        if (this.stack != null) {
            guiGraphics.item(this.stack, x, y);
            guiGraphics.itemDecorations(font, this.stack, x, y);
        } else if (this.bookIcon != null) {
            this.bookIcon.render(guiGraphics, x, y);
        }

        if (this.badge != null) {
            var offset = ResearchIcons.BADGE_OFFSET;
            var size = ResearchIcons.BADGE_SIZE;
            guiGraphics.blit(RenderPipelines.GUI_TEXTURED, this.badge, x + offset, y + offset, 0, 0, size, size, size, size, size, size);
        }
    }
}
