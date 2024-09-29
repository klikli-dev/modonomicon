// SPDX-FileCopyrightText: 2024 klikli-dev
//
// SPDX-License-Identifier: MIT

package com.klikli_dev.modonomicon.client.gui.book.entry.linkhandler;

import com.klikli_dev.modonomicon.book.BookLink;
import com.klikli_dev.modonomicon.bookstate.BookUnlockStateManager;
import com.klikli_dev.modonomicon.client.gui.BookGuiManager;
import com.klikli_dev.modonomicon.client.gui.book.entry.BookEntryScreen;
import com.klikli_dev.modonomicon.data.BookDataManager;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Style;
import org.jetbrains.annotations.NotNull;

public class BookLinkHandler extends LinkHandler {
    public BookLinkHandler(BookEntryScreen screen) {
        super(screen);
    }

    @Override
    public ClickResult handleClick(@NotNull Style pStyle) {
        var event = pStyle.getClickEvent();
        if (event == null)
            return ClickResult.UNHANDLED;

        if (event.getAction() != ClickEvent.Action.CHANGE_PAGE)
            return ClickResult.UNHANDLED;

        if (!BookLink.isBookLink(event.getValue()))
            return ClickResult.UNHANDLED;

        var link = BookLink.from(this.book(), event.getValue());
        var book = BookDataManager.get().getBook(link.bookId);
        if (link.entryId != null) {
            var entry = book.getEntry(link.entryId);

            if (!BookUnlockStateManager.get().isUnlockedFor(this.player(), entry)) {
                //renderComponentHoverEffect will render a warning that it is locked so it is fine to exit here
                return ClickResult.FAILURE;
            }

            Integer page = link.pageNumber;
            if (link.pageAnchor != null) {
                page = entry.getPageNumberForAnchor(link.pageAnchor);
            }

            if (page != null && !BookUnlockStateManager.get().isUnlockedFor(this.player(), entry.getPages().get(page))) {
                return ClickResult.UNHANDLED;
            } else if (page == null) {
                page = 0;
            }

            //we push the page we are currently on to the history
            var currentPageNumber = this.screen().getCurrentPageNumber();
            BookGuiManager.get().pushHistory(this.book().getId(), this.category().getId(), this.entry().getId(), currentPageNumber);
            BookGuiManager.get().openEntry(link.bookId, link.entryId, page);
        } else if (link.categoryId != null) {
            BookGuiManager.get().openEntry(link.bookId, link.categoryId, null, 0);
            //Currently we do not push categories to history
        } else {
            BookGuiManager.get().openEntry(link.bookId, null, null, 0);
            //Currently we do not push categories to history
        }
        return ClickResult.SUCCESS;
    }
}
