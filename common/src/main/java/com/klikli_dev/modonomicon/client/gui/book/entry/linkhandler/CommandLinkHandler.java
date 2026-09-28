// SPDX-FileCopyrightText: 2024 klikli-dev
//
// SPDX-License-Identifier: MIT

package com.klikli_dev.modonomicon.client.gui.book.entry.linkhandler;

import com.klikli_dev.modonomicon.book.CommandLink;
import com.klikli_dev.modonomicon.bookstate.BookServices;
import com.klikli_dev.modonomicon.data.BookDataManager;
import com.klikli_dev.modonomicon.networking.ClickCommandLinkMessage;
import com.klikli_dev.modonomicon.platform.Services;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Style;
import org.jetbrains.annotations.NotNull;

public class CommandLinkHandler extends LinkHandler {
    public CommandLinkHandler(LinkClickContext context) {
        super(context);
    }

    @Override
    public ClickResult handleClick(@NotNull Style pStyle) {
        var event = pStyle.getClickEvent();
        if (event == null)
            return ClickResult.UNHANDLED;

        //Command links use RUN_COMMAND action, but the command string is not a vanilla string, instead it is a custom protocol string.
        //the isCommandLink below will check for a protocol prefix
        if (event.action() != ClickEvent.Action.RUN_COMMAND || !(event instanceof ClickEvent.RunCommand(
                String command1
        )))
            return ClickResult.UNHANDLED;

        if (!CommandLink.isCommandLink(command1))
            return ClickResult.UNHANDLED;

        var link = CommandLink.from(this.book(), command1);
        var book = BookDataManager.get().getBook(link.bookId);
        if (link.commandId == null)
            return ClickResult.FAILURE;

        var command = book.getCommand(link.commandId);
        // Get the current entry's Identifier - null when the link is clicked outside of an entry (e.g. in a description).
        var entryId = this.context.entryId();
        // Check if the command is allowed for this entry
        if (entryId != null) {
            if (!command.isEntryAllowed(entryId)) {
                com.klikli_dev.modonomicon.Modonomicon.LOG.warn("Blocked command link: Command '{}' is not allowed on entry '{}' (book '{}')", command.getId(), entryId, book.getId());
                return ClickResult.FAILURE;
            }
        } else if (!command.getAllowedEntries().isEmpty()) {
            //without an entry context only commands that are allowed everywhere can run.
            //the server enforces the same rule by skipping the entry check only for a missing entry id.
            com.klikli_dev.modonomicon.Modonomicon.LOG.warn("Blocked command link: Command '{}' (book '{}') is restricted to entries, but was clicked outside of an entry", command.getId(), book.getId());
            return ClickResult.FAILURE;
        }

        if (BookServices.stateAccess().canRun(this.player(), command)) {
            Services.NETWORK.sendToServer(new ClickCommandLinkMessage(link.bookId, link.commandId, entryId));

            //we immediately count up the usage client side -> to avoid spamming the server
            //if the server ends up not counting up the usage, it will sync the correct info back down to us
            //We should only do that on the client connected to a dedicated server, because on the integrated server we would count usage twice
            //that means, for singleplayer clients OR clients that share to lan we dont call the setRunFor
            if (Minecraft.getInstance().getSingleplayerServer() == null)
                BookServices.stateAccess().setRun(this.player(), command);

            return ClickResult.SUCCESS;
        }
        return ClickResult.FAILURE;
    }
}
