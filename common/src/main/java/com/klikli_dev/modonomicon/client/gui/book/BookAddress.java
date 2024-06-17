/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.client.gui.book;

import com.klikli_dev.modonomicon.book.Book;
import com.klikli_dev.modonomicon.book.BookCategory;
import com.klikli_dev.modonomicon.book.entries.BookEntry;
import com.klikli_dev.modonomicon.util.StreamCodecs;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

/**
 * Represents an address in a book, consisting of the book, category, entry and page.
 * Used to navigate to a specific page in a book and to store such a state
 */
@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
public record BookAddress(@NotNull ResourceLocation bookId,
                          ResourceLocation categoryId, boolean ignoreSavedCategory,
                          ResourceLocation entryId, boolean ignoreSavedEntry,
                          int page, boolean ignoreSavedPage
) {
    public static final Codec<BookAddress> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ResourceLocation.CODEC.fieldOf("bookId").forGetter(BookAddress::bookId),
            ResourceLocation.CODEC.optionalFieldOf("categoryId").forGetter((address) -> Optional.ofNullable(address.categoryId)),
            Codec.BOOL.fieldOf("ignoreSavedCategory").forGetter(BookAddress::ignoreSavedCategory),
            ResourceLocation.CODEC.optionalFieldOf("entryId").forGetter((address) -> Optional.ofNullable(address.entryId)),
            Codec.BOOL.fieldOf("ignoreSavedEntry").forGetter(BookAddress::ignoreSavedEntry),
            Codec.INT.fieldOf("page").forGetter(BookAddress::page),
            Codec.BOOL.fieldOf("ignoreSavedPage").forGetter(BookAddress::ignoreSavedPage)
    ).apply(instance, BookAddress::new));

    public static final StreamCodec<FriendlyByteBuf, BookAddress> STREAM_CODEC = StreamCodecs.composite(
            ResourceLocation.STREAM_CODEC,
            BookAddress::bookId,
            ByteBufCodecs.optional(ResourceLocation.STREAM_CODEC),
            (address) -> Optional.ofNullable(address.categoryId),
            ByteBufCodecs.BOOL,
            BookAddress::ignoreSavedCategory,
            ByteBufCodecs.optional(ResourceLocation.STREAM_CODEC),
            (address) -> Optional.ofNullable(address.entryId),
            ByteBufCodecs.BOOL,
            BookAddress::ignoreSavedEntry,
            ByteBufCodecs.INT,
            BookAddress::page,
            ByteBufCodecs.BOOL,
            BookAddress::ignoreSavedPage,
            BookAddress::new
    );

    private BookAddress(@NotNull ResourceLocation bookId,
                        Optional<ResourceLocation> categoryId, boolean ignoreSavedCategory,
                        Optional<ResourceLocation> entryId, boolean ignoreSavedEntry,
                        int page, boolean ignoreSavedPage
    ) {
        this(bookId, categoryId.orElse(null), ignoreSavedCategory, entryId.orElse(null), ignoreSavedEntry, page, ignoreSavedPage);
    }

    public static BookAddress ignoreSaved(@NotNull BookEntry entry, int page) {
        return ignoreSaved(entry.getBook().getId(), entry.getCategory().getId(), entry.getId(), page);
    }


    public static BookAddress ignoreSaved(@NotNull BookEntry entry) {
        return ignoreSaved(entry, -1);
    }

    public static BookAddress defaultFor(@NotNull BookCategory category) {
        return of(category.getBook().getId(), category.getId(), null, -1);
    }

    public static BookAddress defaultFor(@NotNull BookEntry entry) {
        return of(entry.getBook().getId(), entry.getCategory().getId(), entry.getId(), -1);
    }

    public static BookAddress defaultFor(@NotNull Book book) {
        return defaultFor(book.getId());
    }

    public static BookAddress defaultFor(@NotNull ResourceLocation bookId) {
        return of(bookId, null, null, -1);
    }

    public static BookAddress of(@NotNull ResourceLocation bookId,
                                 ResourceLocation categoryId,
                                 ResourceLocation entryId,
                                 int page) {
        return new BookAddress(bookId, categoryId, false, entryId, false, page, false);
    }

    public static BookAddress ignoreSaved(@NotNull ResourceLocation bookId,
                                          ResourceLocation categoryId,
                                          ResourceLocation entryId,
                                          int page) {
        return new BookAddress(bookId, categoryId, true, entryId, true, page, true);
    }

    public BookAddress withPage(int page) {
        return new BookAddress(this.bookId, this.categoryId, this.ignoreSavedCategory, this.entryId, this.ignoreSavedEntry, page, this.ignoreSavedPage);
    }
}