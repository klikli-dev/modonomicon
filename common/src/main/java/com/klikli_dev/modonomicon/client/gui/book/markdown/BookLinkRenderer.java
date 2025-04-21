/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.client.gui.book.markdown;

import com.klikli_dev.modonomicon.Modonomicon;
import com.klikli_dev.modonomicon.api.ModonomiconConstants.I18n.Gui;
import com.klikli_dev.modonomicon.book.BookLink;
import com.klikli_dev.modonomicon.book.error.BookErrorManager;
import com.klikli_dev.modonomicon.data.BookDataManager;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.ClickEvent.Action;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
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

            try {
                var bookLink = BookLink.from(context.getBook(), link.getDestination());
                var book = BookDataManager.get().getBook(bookLink.bookId);
                var goToText = Component.translatable(book.getName());
                if (bookLink.categoryId != null) {
                    var category = book.getCategory(bookLink.categoryId);
                    goToText = Component.translatable(category.getName());
                }
                if (bookLink.entryId != null) {
                    var entry = book.getEntry(bookLink.entryId);
                    goToText = Component.translatable(entry.getName());
                }
                //Note: if we ever change this we need to adjust renderComponentHoverEffect
                var hoverComponent = Component.translatable(Gui.HOVER_BOOK_LINK, goToText);


                //if we have a color we use it, otherwise we use link default.
                context.setCurrentStyle(context.getCurrentStyle()
                        .withColor(currentColor == null ? context.getLinkColor() : currentColor)
                        .withClickEvent(new ClickEvent.OpenFile(link.getDestination()))
                        .withHoverEvent(new HoverEvent.ShowText(hoverComponent))
                );

                visitChildren.accept(link);

                //links are not style instructions, so we reset to our previous color.
                context.setCurrentStyle(context.getCurrentStyle()
                        .withColor(currentColor)
                        .withClickEvent(null)
                        .withHoverEvent(null)
                );
            } catch (Exception e) {
                if (context.getBook().allowOpenBooksWithInvalidLinks()) {
                    Modonomicon.LOG.error("Failed to parse book link. allowOpenBooksWithInvalidLinks = true, so book parsing continues. Original error:", e);

                    //Render error message as tooltip in red
                    var hoverComponent = Component.translatable(Gui.HOVER_BOOK_LINK_ERROR, link.getDestination()).withStyle(ChatFormatting.RED);

                    //Render link in red
                    context.setCurrentStyle(context.getCurrentStyle()
                            .withColor(ChatFormatting.RED)
                            .withHoverEvent(new HoverEvent.ShowText(hoverComponent))
                    );

                    visitChildren.accept(link);

                    //links are not style instructions, so we reset to our previous color.
                    context.setCurrentStyle(context.getCurrentStyle()
                            .withColor(currentColor)
                            .withClickEvent(null)
                            .withHoverEvent(null)
                    );
                } else {
                    throw e;
                }
            }

            BookErrorManager.get().setContext(null);
            return true;
        }
        return false;

    }
}
