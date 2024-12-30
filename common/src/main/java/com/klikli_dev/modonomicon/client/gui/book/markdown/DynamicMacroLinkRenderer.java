/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.client.gui.book.markdown;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextColor;
import org.commonmark.node.Link;
import org.commonmark.node.Node;
import org.commonmark.node.Text;

import java.util.function.Consumer;

public class DynamicMacroLinkRenderer implements LinkRenderer {
    @Override
    public boolean visit(Link link, Consumer<Node> visitChildren, ComponentNodeRendererContext context) {
        //[{}](my.example.key)
        var child = link.getFirstChild();
        if (child instanceof Text t && t.getLiteral().equals("{}")) {
            if (!link.getDestination().isEmpty() && context.getBook().textMacros().containsKey(link.getDestination())) {
                var replacement = context.getBook().textMacros().get(link.getDestination());

                context.getCurrentComponent().append(Component.translatable(replacement));

                //we do not call visit children, because macro "links" should not be rendered
                return true;
            }
        }
        return false;
    }
}
