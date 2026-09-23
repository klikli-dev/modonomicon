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
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

/**
 * Represents an address in a book, consisting of the book, category, entry and page.
 * Used to navigate to a specific page in a book and to store such a state.
 * <p>
 * {@code page} is always the authored page number. {@code displayPage} optionally records
 * the transient virtual display index (split fragment) for back-history navigation.
 * {@code -1} means unspecified, resolving to the first fragment of the authored page.
 */
@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
public record BookAddress(@NotNull Identifier bookId,
                          Identifier categoryId, boolean ignoreSavedCategory,
                          Identifier entryId, boolean ignoreSavedEntry,
                          int page, boolean ignoreSavedPage,
                          int displayPage
) {
    public static final Codec<BookAddress> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Identifier.CODEC.fieldOf("bookId").forGetter(BookAddress::bookId),
            Identifier.CODEC.optionalFieldOf("categoryId").forGetter((address) -> Optional.ofNullable(address.categoryId)),
            Codec.BOOL.fieldOf("ignoreSavedCategory").forGetter(BookAddress::ignoreSavedCategory),
            Identifier.CODEC.optionalFieldOf("entryId").forGetter((address) -> Optional.ofNullable(address.entryId)),
            Codec.BOOL.fieldOf("ignoreSavedEntry").forGetter(BookAddress::ignoreSavedEntry),
            Codec.INT.fieldOf("page").forGetter(BookAddress::page),
            Codec.BOOL.fieldOf("ignoreSavedPage").forGetter(BookAddress::ignoreSavedPage),
            Codec.INT.optionalFieldOf("displayPage", -1).forGetter(BookAddress::displayPage)
    ).apply(instance, BookAddress::new));

    public static final StreamCodec<FriendlyByteBuf, BookAddress> STREAM_CODEC = StreamCodecs.composite(
            Identifier.STREAM_CODEC,
            BookAddress::bookId,
            ByteBufCodecs.optional(Identifier.STREAM_CODEC),
            (address) -> Optional.ofNullable(address.categoryId),
            ByteBufCodecs.BOOL,
            BookAddress::ignoreSavedCategory,
            ByteBufCodecs.optional(Identifier.STREAM_CODEC),
            (address) -> Optional.ofNullable(address.entryId),
            ByteBufCodecs.BOOL,
            BookAddress::ignoreSavedEntry,
            ByteBufCodecs.INT,
            BookAddress::page,
            ByteBufCodecs.BOOL,
            BookAddress::ignoreSavedPage,
            ByteBufCodecs.INT,
            BookAddress::displayPage,
            BookAddress::new
    );

    private BookAddress(@NotNull Identifier bookId,
                        Optional<Identifier> categoryId, boolean ignoreSavedCategory,
                        Optional<Identifier> entryId, boolean ignoreSavedEntry,
                        int page, boolean ignoreSavedPage,
                        int displayPage
    ) {
        this(bookId, categoryId.orElse(null), ignoreSavedCategory, entryId.orElse(null), ignoreSavedEntry, page, ignoreSavedPage, displayPage);
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

    public static BookAddress defaultFor(@NotNull Identifier bookId) {
        return of(bookId, null, null, -1);
    }

    public static BookAddress of(@NotNull Identifier bookId,
                                 Identifier categoryId,
                                 Identifier entryId,
                                 int page) {
        return new BookAddress(bookId, categoryId, false, entryId, false, page, false, -1);
    }

    /**
     * @param displayPage transient virtual display index for back-history navigation,
     *                    {@code -1} resolves to the first fragment of the authored page.
     */
    public static BookAddress of(@NotNull Identifier bookId,
                                 Identifier categoryId,
                                 Identifier entryId,
                                 int page,
                                 int displayPage) {
        return new BookAddress(bookId, categoryId, false, entryId, false, page, false, displayPage);
    }

    public static BookAddress ignoreSaved(@NotNull Identifier bookId,
                                          Identifier categoryId,
                                          Identifier entryId,
                                          int page) {
        return new BookAddress(bookId, categoryId, true, entryId, true, page, true, -1);
    }

    /**
     * @param displayPage transient virtual display index for back-history navigation,
     *                    {@code -1} resolves to the first fragment of the authored page.
     */
    public static BookAddress ignoreSaved(@NotNull Identifier bookId,
                                          Identifier categoryId,
                                          Identifier entryId,
                                          int page,
                                          int displayPage) {
        return new BookAddress(bookId, categoryId, true, entryId, true, page, true, displayPage);
    }

    public BookAddress withPage(int page) {
        return new BookAddress(this.bookId, this.categoryId, this.ignoreSavedCategory, this.entryId, this.ignoreSavedEntry, page, this.ignoreSavedPage, this.displayPage);
    }

    /**
     * @return true if a virtual display index was recorded (back-history navigation).
     */
    public boolean hasDisplayPage() {
        return this.displayPage >= 0;
    }
}