package com.klikli_dev.modonomicon.book.conditions.context;

import com.klikli_dev.modonomicon.book.Book;
import com.klikli_dev.modonomicon.book.BookEntry;
import com.klikli_dev.modonomicon.book.page.BookPage;

public class BookConditionPageContext extends BookConditionContext {
    public final BookPage page;

    public BookConditionPageContext(Book book, BookPage page) {
        super(book);
        this.page = page;
    }

    @Override
    public String toString() {
        return "BookConditionEntryContext{" +
                "book=" + this.book +
                ", entry=" + this.page.getParentEntry() +
                ", page=" + this.page +
                '}';
    }

    public BookEntry getEntry() {
        return this.page.getParentEntry();
    }

    public BookPage getPage() {
        return this.page;
    }
}
