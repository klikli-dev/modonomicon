package com.klikli_dev.modonomicon.bookstate;

import com.klikli_dev.modonomicon.book.Book;
import com.klikli_dev.modonomicon.book.BookCategory;
import com.klikli_dev.modonomicon.book.BookEntry;
import com.klikli_dev.modonomicon.bookstate.visual.BookVisualState;
import com.klikli_dev.modonomicon.bookstate.visual.CategoryVisualState;
import com.klikli_dev.modonomicon.bookstate.visual.EntryVisualState;
import com.klikli_dev.modonomicon.util.Codecs;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class BookVisualStates {
    public static final Codec<BookVisualStates> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codecs.concurrentMap(ResourceLocation.CODEC, BookVisualState.CODEC).fieldOf("bookStates").forGetter((s) -> s.bookStates)
    ).apply(instance, BookVisualStates::new));


    public ConcurrentMap<ResourceLocation, BookVisualState> bookStates;

    public BookVisualStates() {
        this(new ConcurrentHashMap<>());
    }

    public BookVisualStates(ConcurrentMap<ResourceLocation, BookVisualState> bookStates) {
        this.bookStates = bookStates;
    }

    public BookVisualState getBookState(Book book){
        return this.bookStates.computeIfAbsent(book.getId(), (id) -> new BookVisualState());
    }

    public CategoryVisualState getCategoryState(BookCategory category){
        return this.getBookState(category.getBook()).categoryStates.computeIfAbsent(category.getId(), (id) -> new CategoryVisualState());
    }

    public EntryVisualState getEntryState(BookEntry entry){
        return this.getCategoryState(entry.getCategory()).entryStates.computeIfAbsent(entry.getId(), (id) -> new EntryVisualState());
    }

    public void setEntryState(BookEntry entry, EntryVisualState state){
        this.getCategoryState(entry.getCategory()).entryStates.put(entry.getId(), state);
    }

    public void setCategoryState(BookCategory category, CategoryVisualState state){
        this.getBookState(category.getBook()).categoryStates.put(category.getId(), state);
    }
}
