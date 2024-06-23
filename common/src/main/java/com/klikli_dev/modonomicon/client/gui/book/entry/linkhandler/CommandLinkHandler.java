// SPDX-FileCopyrightText: 2024 klikli-dev
//
// SPDX-License-Identifier: MIT

package com.klikli_dev.modonomicon.client.gui.book.entry.linkhandler;

import com.klikli_dev.modonomicon.book.CommandLink;
import com.klikli_dev.modonomicon.bookstate.BookUnlockStateManager;
import com.klikli_dev.modonomicon.client.gui.book.entry.BookEntryScreen;
import com.klikli_dev.modonomicon.data.BookDataManager;
import com.klikli_dev.modonomicon.networking.ClickCommandLinkMessage;
import com.klikli_dev.modonomicon.platform.Services;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Style;
import org.jetbrains.annotations.NotNull;

public class CommandLinkHandler extends LinkHandler {
    public CommandLinkHandler(BookEntryScreen screen) {
        super(screen);
    }

    @Override
    public ClickResult handleClick(@NotNull Style pStyle) {
        var event = pStyle.getClickEvent();
        if (event == null)
            return ClickResult.UNHANDLED;

        if (event.getAction() != ClickEvent.Action.RUN_COMMAND)
            return ClickResult.UNHANDLED;

        if (!CommandLink.isCommandLink(event.getValue()))
            return ClickResult.UNHANDLED;

        var link = CommandLink.from(this.book(), event.getValue());
        var book = BookDataManager.get().getBook(link.bookId);
        if (link.commandId == null)
            return ClickResult.FAILURE;

        var command = book.getCommand(link.commandId);

        if (BookUnlockStateManager.get().canRunFor(this.player(), command)) {
            Services.NETWORK.sendToServer(new ClickCommandLinkMessage(link.bookId, link.commandId));

            //we immediately count up the usage client side -> to avoid spamming the server
            //if the server ends up not counting up the usage, it will sync the correct info back down to us
            //We should only do that on the client connected to a dedicated server, because on the integrated server we would count usage twice
            //that means, for singleplayer clients OR clients that share to lan we dont call the setRunFor
            if (Minecraft.getInstance().getSingleplayerServer() == null)
                BookUnlockStateManager.get().setRunFor(this.player(), command);
        }

        return ClickResult.SUCCESS;
    }
}
