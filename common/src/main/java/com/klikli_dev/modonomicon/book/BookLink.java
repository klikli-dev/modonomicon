/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.book;

import com.klikli_dev.modonomicon.book.error.BookErrorManager;
import com.klikli_dev.modonomicon.data.BookDataManager;
import com.klikli_dev.modonomicon.util.Codecs;
import net.minecraft.resources.Identifier;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;

public class BookLink {
    public static final String PROTOCOL_BOOK = "book://";
    public static final String PROTOCOL_CATEGORY = "category://";
    public static final String PROTOCOL_ENTRY = "entry://";

    public Identifier bookId;
    public Identifier categoryId;
    public Identifier entryId;
    public Integer pageNumber;
    public String pageAnchor;

    private BookLink() {

    }


    private static BookLink fromBook(Book fromBook, String linkText) {
        //strip protocol
        linkText = linkText.substring(PROTOCOL_BOOK.length());
        var bookLink = new BookLink();
        var parts = linkText.split("/", 2); //discard everything after /
        bookLink.bookId = parseIdentifier(parts[0], "book", linkText);
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

        var path = StringUtils.removeEnd(linkText, "/");
        var parts = path.split("/");
        if (parts.length == 0 || StringUtils.isBlank(parts[0])) {
            throw new IllegalArgumentException("Invalid category link, expected category path: " + linkText);
        }

        int categoryStartIndex;
        if (parts[0].contains(":")) {
            if (parts.length < 2) {
                throw new IllegalArgumentException("Invalid category link, expected fully qualified book id and category path: " + linkText);
            }

            bookLink.bookId = parseIdentifier(parts[0], "book", linkText);
            categoryStartIndex = 1;
        } else {
            if (fromBook == null) {
                throw new IllegalArgumentException("Invalid category link, missing book context: " + linkText);
            }

            bookLink.bookId = fromBook.getId();
            categoryStartIndex = 0;
        }

        var book = BookDataManager.get().getBook(bookLink.bookId);
        if (book == null) {
            throw new IllegalArgumentException("Invalid category link, book not found: " + linkText);
        }

        var categoryPath = String.join("/", Arrays.copyOfRange(parts, categoryStartIndex, parts.length));
        bookLink.categoryId = parsePathIdentifier(bookLink.bookId.getNamespace(), categoryPath, "category", linkText);
        var category = book.getCategory(bookLink.categoryId);
        if (category == null) {
            throw new IllegalArgumentException("Invalid category link, category not found in book: " + linkText);
        }

        return bookLink;
    }

    private static BookLink fromEntry(Book fromBook, String linkText) {
        //strip protocol
        linkText = linkText.substring(PROTOCOL_ENTRY.length());
        var bookLink = new BookLink();

        var entryTarget = linkText;

        //anchors are indicated by @
        int lastAtIndex = entryTarget.lastIndexOf("@");
        if (lastAtIndex >= 0) {
            bookLink.pageAnchor = entryTarget.substring(lastAtIndex + 1);
            entryTarget = StringUtils.removeEnd(entryTarget.substring(0, lastAtIndex), "/");
        }

        int lastHashIndex = entryTarget.lastIndexOf("#");
        if (lastHashIndex >= 0) {
            var postHash = entryTarget.substring(lastHashIndex + 1);
            entryTarget = StringUtils.removeEnd(entryTarget.substring(0, lastHashIndex), "/");
            try {
                bookLink.pageNumber = Integer.parseInt(postHash);
            } catch (NumberFormatException e) {
                BookErrorManager.get().error("Invalid page number in entry link: " + linkText, e);
            }
        }

        var parts = entryTarget.split("/");
        if (parts.length == 0 || StringUtils.isBlank(parts[0])) {
            throw new IllegalArgumentException("Invalid entry link, expected entry path: " + linkText);
        }

        int entryStartIndex;
        if (parts[0].contains(":")) {
            if (parts.length < 3) {
                throw new IllegalArgumentException("Invalid entry link, expected fully qualified book id, category path and entry path: " + linkText);
            }

            bookLink.bookId = parseIdentifier(parts[0], "book", linkText);
            entryStartIndex = 1;
        } else {
            if (fromBook == null) {
                throw new IllegalArgumentException("Invalid entry link, missing book context: " + linkText);
            }

            if (parts.length < 2) {
                throw new IllegalArgumentException("Invalid entry link, expected category path and entry path: " + linkText);
            }

            bookLink.bookId = fromBook.getId();
            entryStartIndex = 0;
        }

        Book book = BookDataManager.get().getBook(bookLink.bookId);
        if (book == null) {
            throw new IllegalArgumentException("Invalid entry link, book not found: " + linkText);
        }

       var pathParts = Arrays.copyOfRange(parts, entryStartIndex, parts.length);
        if (pathParts.length < 2) {
            throw new IllegalArgumentException("Invalid entry link, expected category path and entry path: " + linkText);
        }

        var categoryPath = String.join("/", Arrays.copyOfRange(pathParts, 0, pathParts.length - 1));
        var entryPath = pathParts[pathParts.length - 1];

        bookLink.categoryId = parsePathIdentifier(bookLink.bookId.getNamespace(), categoryPath, "category", linkText);
        if (book.getCategory(bookLink.categoryId) == null) {
            throw new IllegalArgumentException("Invalid entry link, category not found in book: " + linkText);
        }

        bookLink.entryId = parsePathIdentifier(bookLink.bookId.getNamespace(), categoryPath + "/" + entryPath, "entry", linkText);
        var entry = book.getEntry(bookLink.entryId);
        if (entry == null) {
            throw new IllegalArgumentException("Invalid entry link, entry not found in book: " + linkText);
        }

        if (bookLink.pageAnchor != null && entry.getPageNumberForId(bookLink.pageAnchor) == -1) {
            throw new IllegalArgumentException("Invalid entry link, anchor not found in entry: " + linkText);
        }

        if (bookLink.pageNumber != null && (bookLink.pageNumber < 0 || bookLink.pageNumber >= entry.getPages().size())) {
            throw new IllegalArgumentException("Invalid entry link, page number not found in entry: " + linkText);
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
        //category://nested/test_category

        //entry://modonomicon:test/test_category/test_entry
        //entry://modonomicon:test/test_category/test_entry#1
        //entry://modonomicon:test/test_category/test_entry/#1
        //entry://modonomicon:test/test_category/test_entry@anchor
        //entry://modonomicon:test/nested/test_category/test_entry

        //without book prefix (will use fromBook):
        //entry://test_category/test_entry
        //entry://test_category/test_entry#1
        //entry://test_category/test_entry/#1
        //entry://test_category/test_entry@anchor
        //entry://nested/test_category/test_entry

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

    private static Identifier parseIdentifier(String value, String kind, String linkText) {
        var id = Codecs.tryParseStrictIdentifier(value);
        if (id == null) {
            throw new IllegalArgumentException("Invalid " + kind + " id in link, expected fully qualified identifier: " + linkText);
        }
        return id;
    }

    private static Identifier parsePathIdentifier(String namespace, String path, String kind, String linkText) {
        try {
            return Identifier.fromNamespaceAndPath(namespace, path);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid " + kind + " path in link: " + linkText, e);
        }
    }
}
