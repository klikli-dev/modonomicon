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

import net.minecraft.network.chat.TextColor;
import org.commonmark.node.Link;
import org.commonmark.node.Node;
import org.commonmark.node.Text;

import java.util.function.Consumer;

public class ColorLinkRenderer implements LinkRenderer {
    @Override
    public boolean visit(Link link, Consumer<Node> visitChildren, ComponentNodeRendererContext context) {
        var child = link.getFirstChild();
        if (child instanceof Text t && t.getLiteral().equals("#")) {
            if (link.getDestination().isEmpty()) {
                context.setCurrentStyle(context.getCurrentStyle().withColor((TextColor) null));
            } else {
                //we use TextColor.parseColor because it fails gracefully as a color reset.
                context.setCurrentStyle(context.getCurrentStyle()
                        .withColor(TextColor.parseColor("#" + link.getDestination())));
            }
            //we do not call visit children, because color "links" should not be rendered
            return true;
        }
        return false;
    }
}
