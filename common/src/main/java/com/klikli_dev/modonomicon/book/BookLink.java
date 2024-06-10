/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.book;

import com.klikli_dev.modonomicon.book.error.BookErrorManager;
import com.klikli_dev.modonomicon.data.BookDataManager;
import net.minecraft.resources.ResourceLocation;
import org.apache.commons.lang3.StringUtils;

public class BookLink {
    public static final String PROTOCOL_BOOK = "book://";
    public static final String PROTOCOL_CATEGORY = "category://";
    public static final String PROTOCOL_ENTRY = "entry://";

    public ResourceLocation bookId;
    public ResourceLocation categoryId;
    public ResourceLocation entryId;
    public Integer pageNumber;
    public String pageAnchor;

    private BookLink() {

    }


    private static BookLink fromBook(Book fromBook, String linkText) {
        //strip protocol
        linkText = linkText.substring(PROTOCOL_BOOK.length());
        var bookLink = new BookLink();
        var parts = linkText.split("/", 2); //discard everything after /
        bookLink.bookId = ResourceLocation.tryParse(parts[0]);
        var book = BookDataManager.get().getBook(bookLink.bookId);
        if (book == null) {
            throw new IllegalArgumentException("Invalid book link, book not found: " + linkText);
        }
        return bookLink;
    }

    private static BookLink fromCategory(Book fromBook, String linkText) {
        //strip protocol
        linkText = linkText.substring(PROTOCOL_CATEGORY.length());
        var bookLink = new BookLink();

        if (linkText.contains(":")) {
            //with book id
            var parts = linkText.split("/", 2);
            bookLink.bookId = ResourceLocation.tryParse(parts[0]);
            var book = BookDataManager.get().getBook(bookLink.bookId);
            if (book == null) {
                throw new IllegalArgumentException("Invalid category link, book not found: " + linkText);
            }

            if (parts.length == 1) //we only got a book id
                throw new IllegalArgumentException("Invalid category link, does not contain any category id: " + linkText);

            parts = parts[1].split("/", 2); //discard everything after /, our category id ends at the end of string or at the first /
            bookLink.categoryId = ResourceLocation.fromNamespaceAndPath(bookLink.bookId.getNamespace(), parts[0]);
            var category = book.getCategory(bookLink.categoryId);
            if (category == null) {
                throw new IllegalArgumentException("Invalid category link, category not found in book: " + linkText);
            }

            return bookLink;
        } else {
            //without book id
            bookLink.bookId = fromBook.getId();
            if (linkText.isEmpty())
                throw new IllegalArgumentException("Invalid category link, does not contain any category id, because it is empty: " + linkText);

            var parts = linkText.split("/", 2); //discard everything after /, our category id ends at the end of string or at the first /
            bookLink.categoryId = ResourceLocation.fromNamespaceAndPath(bookLink.bookId.getNamespace(), parts[0]);
            var category = fromBook.getCategory(bookLink.categoryId);
            if (category == null) {
                throw new IllegalArgumentException("Invalid category link, category not found in book: " + linkText);
            }

            return bookLink;
        }
    }

    private static BookLink fromEntry(Book fromBook, String linkText) {
        //strip protocol
        linkText = linkText.substring(PROTOCOL_ENTRY.length());
        var bookLink = new BookLink();

        Book book = null;
        String[] parts = null;

        if (linkText.contains(":")) {
            parts = linkText.split("/", 2);
            bookLink.bookId = ResourceLocation.tryParse(parts[0]);
            book = BookDataManager.get().getBook(bookLink.bookId);
            if (book == null) {
                throw new IllegalArgumentException("Invalid entry link, book not found: " + linkText);
            }

            if (parts.length == 1) //we only got a book id
                throw new IllegalArgumentException("Invalid entry link, does not contain any entry id: " + linkText);
        } else {
            bookLink.bookId = fromBook.getId();
            book = fromBook;
            parts = new String[]{"", linkText};
        }


        var entryId = parts[1];

        //anchors are indicated by @
        int lastAtIndex = entryId.lastIndexOf("@");
        if (lastAtIndex >= 0) {
            var postAt = entryId.substring(lastAtIndex + 1);
            var path = StringUtils.removeEnd(entryId.substring(0, lastAtIndex), "/"); //remove trailing /
            bookLink.entryId = ResourceLocation.fromNamespaceAndPath(book.getId().getNamespace(), path);
            var entry = book.getEntry(bookLink.entryId);
            if (entry == null) {
                throw new IllegalArgumentException("Invalid entry link, entry not found in book: " + linkText);
            }

            bookLink.pageAnchor = postAt;
            if (entry.getPageNumberForAnchor(bookLink.pageAnchor) == -1) {
                throw new IllegalArgumentException("Invalid entry link, anchor not found in entry: " + linkText);
            }

            return bookLink;
        }

        int lastHashIndex = entryId.lastIndexOf("#");
        if (lastHashIndex >= 0) {
            //handle page index after #
            var postHash = entryId.substring(lastHashIndex + 1);
            var path = StringUtils.removeEnd(entryId.substring(0, lastHashIndex), "/"); //remove trailing /
            bookLink.entryId = ResourceLocation.fromNamespaceAndPath(book.getId().getNamespace(), path);
            if (book.getEntry(bookLink.entryId) == null) {
                throw new IllegalArgumentException("Invalid entry link, entry not found in book: " + linkText);
            }
            try {
                bookLink.pageNumber = Integer.parseInt(postHash);
                //check if page number is valid
                if (bookLink.pageNumber < 0 || bookLink.pageNumber >= book.getEntry(bookLink.entryId).getPages().size()) {
                    throw new IllegalArgumentException("Invalid entry link, page number not found in entry: " + linkText);
                }
            } catch (NumberFormatException e) {
                BookErrorManager.get().error("Invalid page number in entry link: " + linkText, e);
            }

            return bookLink;
        }

        //handle no page number/anchor
        bookLink.entryId = ResourceLocation.fromNamespaceAndPath(book.getId().getNamespace(), entryId);
        if (book.getEntry(bookLink.entryId) == null) {
            throw new IllegalArgumentException("Invalid entry link, entry not found in book: " + linkText);
        }
        return bookLink;

    }

    public static BookLink from(Book fromBook, String linkText) {
        //book://modonomicon:test/
        //book://modonomicon:test

        //category://modonomicon:test/test_category/
        //category://modonomicon:test/test_category

        //without book prefix (will use fromBook):
        //category://test_category/
        //category://test_category

        //entry://modonomicon:test/test_category/test_entry
        //entry://modonomicon:test/test_category/test_entry#1
        //entry://modonomicon:test/test_category/test_entry/#1
        //entry://modonomicon:test/test_category/test_entry@anchor

        //without book prefix (will use fromBook):
        //entry://test_category/test_entry
        //entry://test_category/test_entry#1
        //entry://test_category/test_entry/#1
        //entry://test_category/test_entry@anchor

        if (linkText.toLowerCase().startsWith(PROTOCOL_BOOK)) {
            return fromBook(fromBook, linkText);
        } else if (linkText.toLowerCase().startsWith(PROTOCOL_CATEGORY)) {
            return fromCategory(fromBook, linkText);
        } else if (linkText.toLowerCase().startsWith(PROTOCOL_ENTRY)) {
            return fromEntry(fromBook, linkText);
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
