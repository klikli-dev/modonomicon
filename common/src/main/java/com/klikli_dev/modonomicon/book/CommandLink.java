/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.book;

import com.klikli_dev.modonomicon.data.BookDataManager;
import net.minecraft.resources.ResourceLocation;

public class CommandLink {
    public static final String PROTOCOL_COMMAND = "command://";
    public ResourceLocation bookId;
    public ResourceLocation commandId;

    private CommandLink() {

    }

    private static CommandLink fromCommand(Book fromBook, String linkText) {
        //strip protocol
        linkText = linkText.substring(PROTOCOL_COMMAND.length());
        var commandLink = new CommandLink();

        if (linkText.contains(":")) {
            //with book id
            var parts = linkText.split("/", 2);
            commandLink.bookId = ResourceLocation.tryParse(parts[0]);
            var book = BookDataManager.get().getBook(commandLink.bookId);
            if (book == null) {
                throw new IllegalArgumentException("Invalid command link, book not found: " + linkText);
            }

            if (parts.length == 1) //we only got a book id
                throw new IllegalArgumentException("Invalid command link, does not contain any command id: " + linkText);

            commandLink.commandId = ResourceLocation.fromNamespaceAndPath(commandLink.bookId.getNamespace(), parts[1]);
            var command = book.getCommand(commandLink.commandId);
            if (command == null) {
                throw new IllegalArgumentException("Invalid command link, command not found in book: " + linkText);
            }

            return commandLink;
        } else {
            //without book id
            commandLink.bookId = fromBook.getId();
            if (linkText.isEmpty())
                throw new IllegalArgumentException("Invalid command link, does not contain any command id, because it is empty: " + linkText);

            commandLink.commandId = ResourceLocation.fromNamespaceAndPath(commandLink.bookId.getNamespace(), linkText);
            var command = fromBook.getCommand(commandLink.commandId);
            if (command == null) {
                throw new IllegalArgumentException("Invalid command link, command not found in book: " + linkText);
            }

            return commandLink;
        }
    }

    public static CommandLink from(Book fromBook, String linkText) {
        //command://modonomicon:demo/test_command/test_entry
        //command://test_command

        if (linkText.toLowerCase().startsWith(PROTOCOL_COMMAND)) {
            return fromCommand(fromBook, linkText);
        } else {
            throw new IllegalArgumentException("Invalid command link, does not start with \"" + PROTOCOL_COMMAND + "\": " + linkText);
        }
    }

    public static boolean isCommandLink(String linkText) {
        return linkText.toLowerCase().startsWith(PROTOCOL_COMMAND);
    }
}
