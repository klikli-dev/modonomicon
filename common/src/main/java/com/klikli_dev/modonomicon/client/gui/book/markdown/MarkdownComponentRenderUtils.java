/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.client.gui.book.markdown;

import com.google.common.collect.Lists;
import com.klikli_dev.modonomicon.client.gui.TextWrapper;
import net.minecraft.client.gui.Font;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.util.FormattedCharSequence;

import java.util.List;

public class MarkdownComponentRenderUtils {
    /**
     * Adopted from
     * {@link net.minecraft.client.gui.components.ComponentRenderUtils#wrapComponents(FormattedText, int, Font)} Wraps
     * translatable components and gracefully handles markdown lists.
     *
     * @param text      the text to wrap.
     * @param width     the max width of the text to wrap at
     * @param listWidth the alternate width to use for lists. This is a hack to avoid issues with indent for wrapped
     *                  lines causing the lines to exceed the width. Could be e.g. width-10
     * @param font      The font to use, will usually be Minecraft.getInstance().font;
     * @return a list of wrapped lines ready to render via font.
     */
    public static List<FormattedCharSequence> wrapComponents(MutableComponent text, int width, int listWidth, Font font) {
        ListItemContents listItem = text.getContents() instanceof ListItemContents contents ? contents : null;
        if (listItem != null) {
            width = listWidth;
        }

        //the list number/bullet is not part of the wrapped text, so it is prepended to the first line instead. This
        //keeps it from consuming the first line's text width, matching the indent that wrapped lines get.
        Component prefix = listItem != null ? listItem.getPrefix() : null;
        FormattedCharSequence indent = listItem != null
                ? FormattedCharSequence.forward(listItem.getListHolder().getIndent() + "   ", Style.EMPTY)
                : FormattedCharSequence.EMPTY;

        List<FormattedCharSequence> list = Lists.newArrayList();
        boolean[] firstLine = {true};
        TextWrapper.splitLines(text, width, Style.EMPTY, font, (lineText, isWrapped) -> {
            FormattedCharSequence formattedcharsequence = Language.getInstance().getVisualOrder(lineText);
            FormattedCharSequence leading = FormattedCharSequence.EMPTY;
            if (firstLine[0] && prefix != null) {
                leading = Language.getInstance().getVisualOrder(prefix);
            } else if (isWrapped) {
                leading = indent;
            }
            firstLine[0] = false;

            list.add(leading == FormattedCharSequence.EMPTY
                    ? formattedcharsequence
                    : FormattedCharSequence.composite(leading, formattedcharsequence));
        });

        if (list.isEmpty()) {
            list.add(prefix != null ? Language.getInstance().getVisualOrder(prefix) : FormattedCharSequence.EMPTY);
        }

        return list;
    }
}
