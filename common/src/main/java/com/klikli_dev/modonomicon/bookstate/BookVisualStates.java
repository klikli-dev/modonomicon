/*
 * SPDX-FileCopyrightText: 2023 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.bookstate;

import com.klikli_dev.modonomicon.Modonomicon;
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
        if (book == null) {
            //Can happen if the client knows a book the server does not (book failed to load on the
            //server or client/server content mismatch), or if books are not built yet. Never crash
            //a server tick over this, return a temporary state (see #368).
            Modonomicon.LOG.warn("Tried to get visual state for null book. Returning a temporary state to avoid a crash (see #368).");
            return new BookVisualState();
        }
        return this.bookStates.computeIfAbsent(book.getId(), (id) -> new BookVisualState());
    }

    public CategoryVisualState getCategoryState(BookCategory category) {
        if (category == null || category.getBook() == null) {
            //Category unknown or not linked to a book (yet): book not built after /reload, loading
            //failed, or client/server content mismatch. Never crash a server tick over this (see #368).
            Modonomicon.LOG.warn("Tried to get visual state for category '{}' with null book. Returning a temporary state to avoid a crash (see #368).", category == null ? null : category.getId());
            return new CategoryVisualState();
        }
        return this.getBookState(category.getBook()).categoryStates.computeIfAbsent(category.getId(), (id) -> new CategoryVisualState());
    }

    public EntryVisualState getEntryState(BookEntry entry) {
        if (entry == null || entry.getCategory() == null) {
            //Entry unknown or not linked to a category (yet): book not built after /reload, loading
            //failed, or client/server content mismatch. Never crash a server tick over this (see #368).
            Modonomicon.LOG.warn("Tried to get visual state for entry '{}' with null category. Returning a temporary state to avoid a crash (see #368).", entry == null ? null : entry.getId());
            return new EntryVisualState();
        }
        return this.getCategoryState(entry.getCategory()).entryStates.computeIfAbsent(entry.getId(), (id) -> new EntryVisualState());
    }

    public List<BookAddress> getBookmarks(Book book) {
        if (book == null) {
            Modonomicon.LOG.warn("Tried to get bookmarks for null book. Returning a temporary list to avoid a crash (see #368).");
            return new ArrayList<>();
        }
        return this.bookBookmarks.computeIfAbsent(book.getId(), (id) -> new ArrayList<>());
    }

    public void setBookState(Book book, BookVisualState state) {
        if (book == null) {
            Modonomicon.LOG.warn("Tried to set visual state for null book. Ignoring to avoid a crash (see #368).");
            return;
        }
        this.bookStates.put(book.getId(), state);
    }

    public void setEntryState(BookEntry entry, EntryVisualState state) {
        if (entry == null || entry.getCategory() == null) {
            Modonomicon.LOG.warn("Tried to set visual state for entry '{}' with null category. Ignoring to avoid a crash (see #368).", entry == null ? null : entry.getId());
            return;
        }
        this.getCategoryState(entry.getCategory()).entryStates.put(entry.getId(), state);
    }

    public void setCategoryState(BookCategory category, CategoryVisualState state) {
        if (category == null || category.getBook() == null) {
            Modonomicon.LOG.warn("Tried to set visual state for category '{}' with null book. Ignoring to avoid a crash (see #368).", category == null ? null : category.getId());
            return;
        }
        this.getBookState(category.getBook()).categoryStates.put(category.getId(), state);
    }

    public void setBookmarks(Book book, List<BookAddress> bookmarks) {
        if (book == null) {
            Modonomicon.LOG.warn("Tried to set bookmarks for null book. Ignoring to avoid a crash (see #368).");
            return;
        }
        this.bookBookmarks.put(book.getId(), bookmarks);
    }

    public void addBookmark(Book book, BookAddress bookmark) {
        if (book == null) {
            Modonomicon.LOG.warn("Tried to add a bookmark for null book. Ignoring to avoid a crash (see #368).");
            return;
        }
        this.getBookmarks(book).add(bookmark);
    }

    public boolean removeBookmark(Book book, BookAddress bookmark) {
        if (book == null) {
            Modonomicon.LOG.warn("Tried to remove a bookmark for null book. Ignoring to avoid a crash (see #368).");
            return false;
        }
        return this.getBookmarks(book).remove(bookmark);
    }
}
