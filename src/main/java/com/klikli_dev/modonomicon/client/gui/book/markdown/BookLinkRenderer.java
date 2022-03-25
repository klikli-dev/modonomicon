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

import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.ClickEvent.Action;
import net.minecraft.network.chat.TextColor;
import org.commonmark.node.Link;
import org.commonmark.node.Node;
import org.commonmark.node.Text;

import java.util.function.Consumer;

public class BookLinkRenderer implements LinkRenderer {
    @Override
    public boolean visit(Link link, Consumer<Node> visitChildren, ComponentNodeRendererContext context) {
        if (link.getDestination().startsWith("book://")) {
            var currentColor = context.getCurrentStyle().getColor();

            //if we have a color we use it, otherwise we use link default.
            context.setCurrentStyle(context.getCurrentStyle()
                    .withColor(currentColor == null ? context.getLinkColor() : currentColor)
                    .withClickEvent(new ClickEvent(Action.CHANGE_PAGE, link.getDestination()))
            );

            visitChildren.accept(link);

            //links are not style instructions, so we reset to our previous color.
            context.setCurrentStyle(context.getCurrentStyle()
                    .withColor(currentColor)
                    .withClickEvent(null)
            );
            return true;
        }
        return false;

    }
}
