// SPDX-FileCopyrightText: 2024 klikli-dev
//
// SPDX-License-Identifier: MIT

package com.klikli_dev.modonomicon.client.gui.book.entry.linkhandler;

import com.klikli_dev.modonomicon.book.PatchouliLink;
import com.klikli_dev.modonomicon.client.gui.BookGuiManager;
import com.klikli_dev.modonomicon.client.gui.book.entry.BookEntryScreen;
import com.klikli_dev.modonomicon.platform.Services;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Style;
import org.jetbrains.annotations.NotNull;

public class PatchouliLinkHandler extends LinkHandler {
    public PatchouliLinkHandler(BookEntryScreen screen) {
        super(screen);
    }

    @Override
    public ClickResult handleClick(@NotNull Style pStyle) {
        var event = pStyle.getClickEvent();
        if (event == null)
            return ClickResult.UNHANDLED;

        //Patchouli links use OPEN_FILE action as it allows us to hand over a string.
        //the isPatchouliLink below will check for a protocol prefix
        if (event.action() != ClickEvent.Action.OPEN_FILE || !(event instanceof ClickEvent.OpenFile openFile))
            return ClickResult.UNHANDLED;

        if (!PatchouliLink.isPatchouliLink(openFile.path()))
            return ClickResult.UNHANDLED;

        var link = PatchouliLink.from(openFile.path());
        if (link.bookId == null)
            return ClickResult.FAILURE;

        BookGuiManager.get().keepMousePosition(() -> {
            BookGuiManager.get().closeScreenStack(this.screen()); //will cause the book to close entirely, and save the open page
            //the integration class handles class loading guards if patchouli is not present
            Services.PATCHOULI.openEntry(link.bookId, link.entryId, link.pageNumber);
        });


        return ClickResult.SUCCESS;
    }
}
