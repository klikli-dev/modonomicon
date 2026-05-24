/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.book;

import com.klikli_dev.modonomicon.data.BookDataManager;
import com.klikli_dev.modonomicon.util.Codecs;
import net.minecraft.resources.Identifier;

public class CommandLink {
    public static final String PROTOCOL_COMMAND = "command://";
    public Identifier bookId;
    public Identifier commandId;

    private CommandLink() {

    }

    private static CommandLink fromCommand(Book fromBook, String linkText) {
        //strip protocol
        linkText = linkText.substring(PROTOCOL_COMMAND.length());
        var commandLink = new CommandLink();
        var parts = linkText.split("/", 2);
        if (parts.length < 2) {
            throw new IllegalArgumentException("Invalid command link, expected fully qualified book and command ids: " + linkText);
        }

        commandLink.bookId = parseIdentifier(parts[0], "book", linkText);
        var book = BookDataManager.get().getBook(commandLink.bookId);
        if (book == null) {
            throw new IllegalArgumentException("Invalid command link, book not found: " + linkText);
        }

        commandLink.commandId = parseIdentifier(parts[1], "command", linkText);
        var command = book.getCommand(commandLink.commandId);
        if (command == null) {
            throw new IllegalArgumentException("Invalid command link, command not found in book: " + linkText);
        }

        return commandLink;
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

    private static Identifier parseIdentifier(String value, String kind, String linkText) {
        var id = Codecs.tryParseStrictIdentifier(value);
        if (id == null) {
            throw new IllegalArgumentException("Invalid " + kind + " id in link, expected fully qualified identifier: " + linkText);
        }
        return id;
    }
}
