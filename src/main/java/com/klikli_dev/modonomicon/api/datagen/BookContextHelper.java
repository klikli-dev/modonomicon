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

    public Stack<String> stack = new Stack<>();

    public String mod;
    public String book;
    public String category;
    public String entry;
    public String page;

    public BookContextHelper(String mod) {
        this.mod = mod;
    }

    public BookContextHelper book(String book){
        this.book = book;
        return this;
    }

    public BookContextHelper category(String category){
        this.category = category;
        return this;
    }

    public BookContextHelper entry(String entry){
        this.entry = entry;
        return this;
    }

    public BookContextHelper page(String page){
        this.page = page;
        return this;
    }

    public String book(){
        return BOOK_PREFIX + this.mod + "." + this.book;
    }

    public String bookName(){
        return this.book() + ".name";
    }
    public String bookTooltip(){
        return this.book() + ".tooltip";
    }

    public String category(){
        return this.book() + "." + this.category;
    }

    public String categoryName(){
        return this.category() + ".name";
    }

    public String categoryCondition(String conditionName){
        return this.category() + ".condition." + conditionName;
    }

    public String entry(){
        return this.category() + "." + this.entry;
    }

    public String entryName(){
        return this.entry() + ".name";
    }

    public String entryDescription(){
        return this.entry() + ".description";
    }

    public String entryCondition(String conditionName){
        return this.entry() + ".condition." + conditionName;
    }


    public String page(){
        return this.entry() + "." + this.page;
    }

    public String pageTitle(){
        return this.page() + ".title";
    }

    public String pageText(){
        return this.page() + ".text";
    }

    //TODO: should we cache the key everytime we get a string so we can compare in lang gen?
}
