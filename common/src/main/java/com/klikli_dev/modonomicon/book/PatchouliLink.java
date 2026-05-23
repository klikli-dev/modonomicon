/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.book;

import com.klikli_dev.modonomicon.book.error.BookErrorManager;
import com.klikli_dev.modonomicon.util.Codecs;
import net.minecraft.resources.Identifier;
import org.apache.commons.lang3.StringUtils;

public class PatchouliLink {
    public static final String PROTOCOL_PATCHOULI = "patchouli://";

    public Identifier bookId;
    public Identifier entryId;
    public int pageNumber;

    private PatchouliLink() {

    }

    private static PatchouliLink fromEntry(String linkText) {

        //Note: patchouli entries have the same namespace as the book, so we can just use the book id as namespace

        //Patchouli link format: [<display text>](patchouli://<namespace>:<book_id>//[<namespace>]:<entry_id>)
        //Patchouli link format: [<display text>](patchouli://<namespace>:<book_id>//[<namespace>]:<entry_id>#page_number)
        //Patchouli link format: [<display text>](patchouli://<namespace>:<book_id>//[<namespace>]:<entry_id>/#page_number)


        //strip protocol
        linkText = linkText.substring(PROTOCOL_PATCHOULI.length());
        var bookLink = new PatchouliLink();
        var bookAndEntry = linkText.split("//", 2);
        bookLink.bookId = parseIdentifier(bookAndEntry[0], "book", linkText);

        //unlike book link we're not verifying patchouli link book ids, as we don't control the loading cycle
        // and may parse this link before patchouli is available


        if (bookAndEntry.length == 1) //we only got a book id
            throw new IllegalArgumentException("Invalid patchouli link, does not contain any entry id: " + linkText);

        var entryId = bookAndEntry[1];


        int lastHashIndex = entryId.lastIndexOf("#");
        if (lastHashIndex >= 0) {
            //handle page index after #
            var postHash = entryId.substring(lastHashIndex);
            var path = StringUtils.removeEnd(entryId.substring(0, lastHashIndex), "/"); //remove trailing /

            bookLink.entryId = parseIdentifier(path, "entry", linkText);

            //We're not verifying patchouli link entry ids either

            try {
                bookLink.pageNumber = Integer.parseInt(postHash);
                //not validating page number either
            } catch (NumberFormatException e) {
                BookErrorManager.get().error("Invalid page number in entry link: " + linkText, e);
            }

            return bookLink;
        }

        //handle no page number/anchor
        //We're not verifying patchouli link entry ids here either
        bookLink.entryId = parseIdentifier(entryId, "entry", linkText);

        return bookLink;
    }

    public static PatchouliLink from(String linkText) {
        if (linkText.toLowerCase().startsWith(PROTOCOL_PATCHOULI)) {
            return fromEntry(linkText);
        } else {
            throw new IllegalArgumentException("Invalid patchouli link, does not start with \"" + PROTOCOL_PATCHOULI + "\": " + linkText);
        }
    }

    public static boolean isPatchouliLink(String linkText) {
        return linkText.toLowerCase().startsWith(PROTOCOL_PATCHOULI);
    }

    private static Identifier parseIdentifier(String value, String kind, String linkText) {
        var id = Codecs.tryParseStrictIdentifier(value);
        if (id == null) {
            throw new IllegalArgumentException("Invalid " + kind + " id in patchouli link, expected fully qualified identifier: " + linkText);
        }
        return id;
    }
}
