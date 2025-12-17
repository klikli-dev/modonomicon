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

        //Command links use RUN_COMMAND action, but the command string is not a vanilla string, instead it is a custom protocol string.
        //the isCommandLink below will check for a protocol prefix
        if (event.action() != ClickEvent.Action.RUN_COMMAND || !(event instanceof ClickEvent.RunCommand runCommand))
            return ClickResult.UNHANDLED;

        if (!CommandLink.isCommandLink(runCommand.command()))
            return ClickResult.UNHANDLED;

        var link = CommandLink.from(this.book(), runCommand.command());
        var book = BookDataManager.get().getBook(link.bookId);
        if (link.commandId == null)
            return ClickResult.FAILURE;

        var command = book.getCommand(link.commandId);
        // Get the current entry's Identifier
        var entryId = this.screen.getEntry().getId();
        // Check if the command is allowed for this entry
        if (!command.isEntryAllowed(entryId)) {
            com.klikli_dev.modonomicon.Modonomicon.LOG.warn("Blocked command link: Command '{}' is not allowed on entry '{}' (book '{}')", command.getId(), entryId, book.getId());
            return ClickResult.FAILURE;
        }

        if (BookUnlockStateManager.get().canRunFor(this.player(), command)) {
            Services.NETWORK.sendToServer(new ClickCommandLinkMessage(link.bookId, link.commandId, entryId));

            //we immediately count up the usage client side -> to avoid spamming the server
            //if the server ends up not counting up the usage, it will sync the correct info back down to us
            //We should only do that on the client connected to a dedicated server, because on the integrated server we would count usage twice
            //that means, for singleplayer clients OR clients that share to lan we dont call the setRunFor
            if (Minecraft.getInstance().getSingleplayerServer() == null)
                BookUnlockStateManager.get().setRunFor(this.player(), command);

            return ClickResult.SUCCESS;
        }
        return ClickResult.FAILURE;
    }
}
