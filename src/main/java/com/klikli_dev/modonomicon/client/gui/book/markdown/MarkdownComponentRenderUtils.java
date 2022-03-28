/*
 * LGPL-3-0
 *
 * Copyright (C) 2022 klikli-dev
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

package com.klikli_dev.modonomicon.client.gui.book.markdown;

import com.google.common.collect.Lists;
import net.minecraft.client.gui.Font;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.util.FormattedCharSequence;

import java.util.List;

public class MarkdownComponentRenderUtils {
    /**
     * Adopted from {@link net.minecraft.client.gui.components.ComponentRenderUtils#wrapComponents(FormattedText, int, Font)}
     * Wraps translatable components and gracefully handles markdown lists.
     * @param text the text to wrap.
     * @param width the max width of the text to wrap at
     * @param listWidth the alternate width to use for lists. This is a hack to avoid issues
     *                  with indent for wrapped lines causing the lines to exceed the width.
     *                  Could be e.g. width-10
     * @param font The font to use, will usually be Minecraft.getInstance().font;
     * @return a list of wrapped lines ready to render via font.
     */
    public static List<FormattedCharSequence> wrapComponents(TranslatableComponent text, int width, int listWidth, Font font) {
        if(text instanceof ListItemComponent){
            width = listWidth;
        }

        List<FormattedCharSequence> list = Lists.newArrayList();
        font.getSplitter().splitLines(text, width, Style.EMPTY, (lineText, isWrapped) -> {
            FormattedCharSequence formattedcharsequence = Language.getInstance().getVisualOrder(lineText);
            var indent = FormattedCharSequence.EMPTY;
            if(text instanceof ListItemComponent item){
                indent = FormattedCharSequence.forward(item.getListHolder().getIndent() + "   ", Style.EMPTY);
            }
            list.add(isWrapped ? FormattedCharSequence.composite(indent, formattedcharsequence) : formattedcharsequence);
        });
        return (list.isEmpty() ? Lists.newArrayList(FormattedCharSequence.EMPTY) : list);
    }
}
