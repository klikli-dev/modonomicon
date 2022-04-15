/*
 * LGPL-3.0
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

import com.klikli_dev.modonomicon.api.ModonimiconConstants.I18n.Gui;
import com.klikli_dev.modonomicon.book.BookLink;
import com.klikli_dev.modonomicon.book.error.BookErrorManager;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.ClickEvent.Action;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.TranslatableComponent;
import org.commonmark.node.Link;
import org.commonmark.node.Node;

import java.util.function.Consumer;

public class BookLinkRenderer implements LinkRenderer {
    @Override
    public boolean visit(Link link, Consumer<Node> visitChildren, ComponentNodeRendererContext context) {
        if (BookLink.isBookLink(link.getDestination())) {
            var currentColor = context.getCurrentStyle().getColor();

            BookErrorManager.get().setContext("Link: {}, \n{}",
                    link.getDestination(),
                    BookErrorManager.get().getContextHelper()
            );

            var bookLink = BookLink.from(link.getDestination());
            var goToText = bookLink.bookId.toString().replace(":", ".");
            if (bookLink.categoryId != null) {
                goToText = bookLink.categoryId.toString().replace(":", ".");
            }
            if (bookLink.entryId != null) {
                goToText = bookLink.entryId.toString().replace(":", ".");
            }
            var hoverComponent = new TranslatableComponent(Gui.HOVER_BOOK_LINK, goToText);


            //if we have a color we use it, otherwise we use link default.
            context.setCurrentStyle(context.getCurrentStyle()
                    .withColor(currentColor == null ? context.getLinkColor() : currentColor)
                    .withClickEvent(new ClickEvent(Action.CHANGE_PAGE, link.getDestination()))
                    .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, hoverComponent))
            );

            visitChildren.accept(link);

            //links are not style instructions, so we reset to our previous color.
            context.setCurrentStyle(context.getCurrentStyle()
                    .withColor(currentColor)
                    .withClickEvent(null)
                    .withHoverEvent(null)
            );

            BookErrorManager.get().setContext(null);
            return true;
        }
        return false;

    }
}
