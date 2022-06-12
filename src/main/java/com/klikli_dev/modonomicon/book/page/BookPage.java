/*
 * LGPL-3.0
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

package com.klikli_dev.modonomicon.book.page;

import com.klikli_dev.modonomicon.book.Book;
import com.klikli_dev.modonomicon.book.BookEntry;
import com.klikli_dev.modonomicon.client.gui.book.markdown.BookTextRenderer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

public abstract class BookPage {

    protected Book book;
    protected BookEntry parentEntry;
    protected int pageNumber;

    protected String anchor;

    public BookPage(String anchor) {
        this.anchor = anchor;
    }

    public String getAnchor() {
        return this.anchor;
    }

    public abstract ResourceLocation getType();

    /**
     * call after loading the book jsons to finalize.
     */
    public void build(BookEntry parentEntry, int pageNum) {
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

    public abstract void toNetwork(FriendlyByteBuf buffer);


    public BookEntry getParentEntry() {
        return this.parentEntry;
    }

    public void setParentEntry(BookEntry parentEntry) {
        this.parentEntry = parentEntry;
    }

    public int getPageNumber() {
        return this.pageNumber;
    }

    public void setPageNumber(int pageNumber) {
        this.pageNumber = pageNumber;
    }


}
