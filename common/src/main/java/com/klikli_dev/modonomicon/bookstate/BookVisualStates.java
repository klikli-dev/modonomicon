/*
 * SPDX-FileCopyrightText: 2023 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.bookstate;

import com.klikli_dev.modonomicon.book.Book;
import com.klikli_dev.modonomicon.book.BookCategory;
import com.klikli_dev.modonomicon.book.entries.BookEntry;
import com.klikli_dev.modonomicon.bookstate.visual.BookVisualState;
import com.klikli_dev.modonomicon.bookstate.visual.CategoryVisualState;
import com.klikli_dev.modonomicon.bookstate.visual.EntryVisualState;
import com.klikli_dev.modonomicon.client.gui.book.BookAddress;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import it.unimi.dsi.fastutil.objects.Object2ObjectMaps;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BookVisualStates {
    public static final Codec<BookVisualStates> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.unboundedMap(ResourceLocation.CODEC, BookVisualState.CODEC).fieldOf("bookStates").forGetter((s) -> s.bookStates),
            Codec.unboundedMap(ResourceLocation.CODEC, BookAddress.CODEC.listOf()).fieldOf("bookBookmarks").forGetter((s) -> s.bookBookmarks)
    ).apply(instance, BookVisualStates::new));

    public static final StreamCodec<ByteBuf, BookVisualStates> STREAM_CODEC = ByteBufCodecs.fromCodec(CODEC);

    public Map<ResourceLocation, BookVisualState> bookStates;

    public Map<ResourceLocation, List<BookAddress>> bookBookmarks;

    public BookVisualStates() {
        this(Object2ObjectMaps.emptyMap(), Object2ObjectMaps.emptyMap());
    }

    public BookVisualStates(Map<ResourceLocation, BookVisualState> bookStates, Map<ResourceLocation, List<BookAddress>> bookBookmarks) {
        this.bookStates = Object2ObjectMaps.synchronize(new Object2ObjectOpenHashMap<>(bookStates));
        this.bookBookmarks = Object2ObjectMaps.synchronize(new Object2ObjectOpenHashMap<>());
        bookBookmarks.forEach((bookId, entries) -> this.bookBookmarks.put(bookId, new ArrayList<>(entries)));
    }

    public BookVisualState getBookState(Book book) {
        return this.bookStates.computeIfAbsent(book.getId(), (id) -> new BookVisualState());
    }

    public CategoryVisualState getCategoryState(BookCategory category) {
        return this.getBookState(category.getBook()).categoryStates.computeIfAbsent(category.getId(), (id) -> new CategoryVisualState());
    }

    public EntryVisualState getEntryState(BookEntry entry) {
        return this.getCategoryState(entry.getCategory()).entryStates.computeIfAbsent(entry.getId(), (id) -> new EntryVisualState());
    }

    public List<BookAddress> getBookmarks(Book book) {
        return this.bookBookmarks.computeIfAbsent(book.getId(), (id) -> new ArrayList<>());
    }

    public void setBookState(Book book, BookVisualState state) {
        this.bookStates.put(book.getId(), state);
    }

    public void setEntryState(BookEntry entry, EntryVisualState state) {
        this.getCategoryState(entry.getCategory()).entryStates.put(entry.getId(), state);
    }

    public void setCategoryState(BookCategory category, CategoryVisualState state) {
        this.getBookState(category.getBook()).categoryStates.put(category.getId(), state);
    }

    public void setBookmarks(Book book, List<BookAddress> bookmarks) {
        this.bookBookmarks.put(book.getId(), bookmarks);
    }

    public void addBookmark(Book book, BookAddress bookmark) {
        this.getBookmarks(book).add(bookmark);
    }

    public boolean removeBookmark(Book book, BookAddress bookmark) {
        return this.getBookmarks(book).remove(bookmark);
    }
}
