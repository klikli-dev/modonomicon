/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.book.page;

import com.google.gson.JsonObject;
import com.klikli_dev.modonomicon.book.Book;
import com.klikli_dev.modonomicon.book.conditions.BookCondition;
import com.klikli_dev.modonomicon.book.entries.BookContentEntry;
import com.klikli_dev.modonomicon.client.gui.book.markdown.BookTextRenderer;
import com.klikli_dev.modonomicon.data.BookPageType;
import com.klikli_dev.modonomicon.registry.BookPageTypeRegistry;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.Level;

public abstract class BookPage {

    public static final Codec<BookPage> CODEC = Codec.lazyInitialized(() -> BookPageTypeRegistry.codec().dispatch(
            "type",
            BookPage::type,
            BookPageType::codec
    ));

    public static final StreamCodec<RegistryFriendlyByteBuf, BookPage> STREAM_CODEC = StreamCodec.recursive(codec -> BookPageTypeRegistry.streamCodec()
                    .dispatch(
                            BookPage::type,
                            BookPageType::streamCodec
                    ));

    protected Book book;
    protected BookContentEntry parentEntry;
    protected int pageNumber;

    protected String anchor;
    protected BookCondition condition;

    public BookPage(String anchor, BookCondition condition) {
        this.anchor = anchor;
        this.condition = condition;
    }

    public static BookPage fromJson(Identifier pageParentId, JsonObject json, HolderLookup.Provider provider) {
        return CODEC.parse(provider.createSerializationContext(JsonOps.INSTANCE), json)
                .getOrThrow(error -> new IllegalArgumentException("Failed to decode page for " + pageParentId + ": " + error));
    }

    public static BookPage fromNetwork(RegistryFriendlyByteBuf buffer) {
        return STREAM_CODEC.decode(buffer);
    }

    public static void toNetwork(BookPage page, RegistryFriendlyByteBuf buffer) {
        STREAM_CODEC.encode(buffer, page);
    }

    public String getAnchor() {
        return this.anchor;
    }

    public BookCondition getCondition() {
        return this.condition;
    }

    public abstract BookPageType<?> type();

    public Identifier getType() {
        return this.type().id();
    }

    /**
     * call after loading the book jsons to finalize.
     */
    public void build(Level level, BookContentEntry parentEntry, int pageNum) {
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

    @SuppressWarnings("unchecked")
    public void toNetwork(RegistryFriendlyByteBuf buffer) {
        ((StreamCodec<RegistryFriendlyByteBuf, BookPage>) this.type().streamCodec().cast()).encode(buffer, this);
    }

    public BookContentEntry getParentEntry() {
        return this.parentEntry;
    }

    public void setParentEntry(BookContentEntry parentEntry) {
        this.parentEntry = parentEntry;
    }

    public int getPageNumber() {
        return this.pageNumber;
    }

    public void setPageNumber(int pageNumber) {
        this.pageNumber = pageNumber;
    }

    /**
     * Returns true if this page matches the given query
     *
     * @param query The query text the player entered.
     * @param level The level as context for e.g. resolving ingredient displays.
     * @return true if the page matches the query and should be shown to the searching player, false otherwise.
     */
    public abstract boolean matchesQuery(String query, Level level);
}
