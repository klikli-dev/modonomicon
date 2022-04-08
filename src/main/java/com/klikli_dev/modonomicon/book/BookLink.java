/*
 * LGPL-3-0
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

package com.klikli_dev.modonomicon.book;

import net.minecraft.resources.ResourceLocation;
import org.apache.commons.lang3.StringUtils;

public class BookLink {
    public static final String PROTOCOL_BOOK = "book://";
    public static final String PROTOCOL_CATEGORY = "category://";
    public static final String PROTOCOL_ENTRY = "entry://";

    public ResourceLocation bookId;
    public ResourceLocation categoryId;
    public ResourceLocation entryId;
    public int pageNumber;
    public String pageAnchor;

    private BookLink() {

    }


    private static BookLink fromBook(String linkText) {
        //strip protocol
        linkText = linkText.substring(PROTOCOL_BOOK.length());
        var bookLink = new BookLink();
        var parts = linkText.split("/", 2); //discard everything after /
        bookLink.bookId = ResourceLocation.tryParse(parts[0]);
        bookLink.bookId = ResourceLocation.tryParse(parts[0]);
        var book = BookDataManager.get().getBook(bookLink.bookId);
        if (book == null) {
            throw new IllegalArgumentException("Invalid book link, book not found: " + linkText);
        }
        return bookLink;
    }

    private static BookLink fromCategory(String linkText) {
        //strip protocol
        linkText = linkText.substring(PROTOCOL_CATEGORY.length());
        var bookLink = new BookLink();
        var parts = linkText.split("/", 2);
        bookLink.bookId = ResourceLocation.tryParse(parts[0]);
        var book = BookDataManager.get().getBook(bookLink.bookId);
        if (book == null) {
            throw new IllegalArgumentException("Invalid category link, book not found: " + linkText);
        }

        if (parts.length == 1) //we only got a book id
            throw new IllegalArgumentException("Invalid category link, does not contain any category id: " + linkText);

        parts = parts[1].split("/", 2); //discard everything after /, our category id ends at the end of string or at the first /
        bookLink.categoryId = new ResourceLocation(bookLink.bookId.getNamespace(), parts[0]);
        var category = book.getCategory(bookLink.categoryId);
        if (category == null) {
            throw new IllegalArgumentException("Invalid category link, category not found in book: " + linkText);
        }

        return bookLink;
    }

    private static BookLink fromEntry(String linkText) {
        //strip protocol
        linkText = linkText.substring(PROTOCOL_ENTRY.length());
        var bookLink = new BookLink();
        var parts = linkText.split("/", 2);
        bookLink.bookId = ResourceLocation.tryParse(parts[0]);
        var book = BookDataManager.get().getBook(bookLink.bookId);
        if (book == null) {
            throw new IllegalArgumentException("Invalid entry link, book not found: " + linkText);
        }

        if (parts.length == 1) //we only got a book id
            throw new IllegalArgumentException("Invalid entry link, does not contain any entry id: " + linkText);

        var entryId = parts[1];

        //anchors and pages are indicated by #
        int lastAtIndex = entryId.lastIndexOf("@");
        if(lastAtIndex >= 0){
            var postAt = entryId.substring(lastAtIndex);
            var path = StringUtils.removeEnd(entryId.substring(0, lastAtIndex), "/"); //remove trailing /
            bookLink.entryId = new ResourceLocation(book.getId().getNamespace(), path);
            bookLink.pageAnchor = postAt;
        }

        int lastHashIndex = entryId.lastIndexOf("#");
        if (lastHashIndex >= 0) {
            //handle page anchor after #
            var postHash = entryId.substring(lastHashIndex);
            var path = StringUtils.removeEnd(entryId.substring(0, lastHashIndex), "/"); //remove trailing /
            bookLink.entryId = new ResourceLocation(book.getId().getNamespace(), path);
            try{
                bookLink.pageNumber = Integer.parseInt(postHash);
            } catch (NumberFormatException e) {
                //TODO: book error handling
            }

            return bookLink;
        }

        //handle no page number/anchor
        bookLink.entryId = new ResourceLocation(book.getId().getNamespace(), entryId);
        return bookLink;
    }

    public static BookLink from(String linkText) {
        //book://modonomicon:test/
        //book://modonomicon:test
        //category://modonomicon:test/test_category/
        //category://modonomicon:test/test_category
        //entry://modonomicon:test/test_category/test_entry
        //entry://modonomicon:test/test_category/test_entry#1
        //entry://modonomicon:test/test_category/test_entry/#1
        //entry://modonomicon:test/test_category/test_entry@anchor

        if (linkText.toLowerCase().startsWith(PROTOCOL_BOOK)) {
            return fromBook(linkText);
        } else if (linkText.toLowerCase().startsWith(PROTOCOL_CATEGORY)) {
            return fromCategory(linkText);
        } else if (linkText.toLowerCase().startsWith(PROTOCOL_ENTRY)) {
            return fromEntry(linkText);
        } else {
            throw new IllegalArgumentException("Invalid book link, does not start with \"book://\", \"category://\" or \"entry://\": " + linkText);
        }
    }

    public static boolean isBookLink(String linkText) {
        return linkText.toLowerCase().startsWith(PROTOCOL_BOOK) ||
                linkText.toLowerCase().startsWith(PROTOCOL_CATEGORY) ||
                linkText.toLowerCase().startsWith(PROTOCOL_ENTRY);
    }
}
