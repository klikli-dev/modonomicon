/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.api.datagen;

import java.util.Stack;

/**
 * A helper class to keep track where in the book we are.
 * This allows to generate correct IDs, DescriptionIds, and so on.
 */
public class BookContextHelper {
    public static final String BOOK_PREFIX = "book.";

    protected Stack<String> stack = new Stack<>();

    protected String modId;
    protected String bookId;
    protected String categoryId;
    protected String entryId;
    protected String pageId;

    public BookContextHelper(String modId) {
        this.modId = modId;
    }

    public BookContextHelper book(String book) {
        this.bookId = book;
        return this;
    }

    public BookContextHelper category(String category) {
        this.categoryId = category;
        return this;
    }

    public BookContextHelper entry(String entry) {
        this.entryId = entry;
        return this;
    }

    public BookContextHelper page(String page) {
        this.pageId = page;
        return this;
    }

    public String book() {
        return BOOK_PREFIX + this.modId + "." + this.bookId;
    }

    public String bookName() {
        return this.book() + ".name";
    }

    public String bookDescription() {
        return this.book() + ".description";
    }

    public String bookTooltip() {
        return this.book() + ".tooltip";
    }

    public String modId() {
        return this.modId;
    }

    public String bookId() {
        return this.bookId;
    }

    public String categoryId() {
        return this.categoryId;
    }

    public String entryId() {
        return this.entryId;
    }

    public String pageId() {
        return this.pageId;
    }

    public String category() {
        return this.book() + "." + this.categoryId;
    }

    public String categoryName() {
        return this.category() + ".name";
    }

    public String categoryDescription() {
        return this.category() + ".description";
    }

    public String categoryCondition(String conditionName) {
        return this.category() + ".condition." + conditionName;
    }

    public String entry() {
        return this.category() + "." + this.entryId;
    }

    public String entryName() {
        return this.entry() + ".name";
    }

    public String entryDescription() {
        return this.entry() + ".description";
    }

    public String entryCondition(String conditionName) {
        return this.entry() + ".condition." + conditionName;
    }


    public String page() {
        return this.entry() + "." + this.pageId;
    }

    public String pageTitle() {
        return this.page() + ".title";
    }

    public String pageText() {
        return this.page() + ".text";
    }

    //TODO: should we cache the key everytime we get a string so we can compare in lang gen?
}
