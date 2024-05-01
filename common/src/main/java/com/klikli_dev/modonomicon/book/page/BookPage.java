/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.book.page;

import com.klikli_dev.modonomicon.book.Book;
import com.klikli_dev.modonomicon.book.entries.ContentBookEntry;
import com.klikli_dev.modonomicon.book.conditions.BookCondition;
import com.klikli_dev.modonomicon.client.gui.book.markdown.BookTextRenderer;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;

public abstract class BookPage {

    protected Book book;
    protected ContentBookEntry parentEntry;
    protected int pageNumber;

    protected String anchor;
    protected BookCondition condition;

    public BookPage(String anchor, BookCondition condition) {
        this.anchor = anchor;
        this.condition = condition;
    }

    public String getAnchor() {
        return this.anchor;
    }

    public BookCondition getCondition() {
        return this.condition;
    }

    public abstract ResourceLocation getType();

    /**
     * call after loading the book jsons to finalize.
     */
    public void build(Level level, ContentBookEntry parentEntry, int pageNum) {
        this.parentEntry = parentEntry;
        this.pageNumber = pageNum;
        this.book = this.parentEntry.getBook();
    }

    /**
     * Called after build() (after loading the book jsons) to render markdown and store any errors
     */
    public void prerenderMarkdown(BookTextRenderer textRenderer) {
    }


    public Book getBook() {
        return this.book;
    }

    public void toNetwork(RegistryFriendlyByteBuf buffer) {
        buffer.writeUtf(this.anchor);
        BookCondition.toNetwork(this.condition, buffer);
    }

    public ContentBookEntry getParentEntry() {
        return this.parentEntry;
    }

    public void setParentEntry(ContentBookEntry parentEntry) {
        this.parentEntry = parentEntry;
    }

    public int getPageNumber() {
        return this.pageNumber;
    }

    public void setPageNumber(int pageNumber) {
        this.pageNumber = pageNumber;
    }

    public abstract boolean matchesQuery(String query);
}
