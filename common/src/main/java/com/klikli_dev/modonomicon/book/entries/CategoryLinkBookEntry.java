/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.book.entries;

import com.klikli_dev.modonomicon.Modonomicon;
import com.klikli_dev.modonomicon.book.BookCategory;
import com.klikli_dev.modonomicon.book.error.BookErrorManager;
import com.klikli_dev.modonomicon.client.gui.BookGuiManager;
import com.klikli_dev.modonomicon.client.gui.book.BookAddress;
import com.klikli_dev.modonomicon.data.BookEntryType;
import com.klikli_dev.modonomicon.registry.BookEntryTypeRegistry;
import com.klikli_dev.modonomicon.util.Codecs;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.Level;

import java.util.Optional;

public class CategoryLinkBookEntry extends BookEntry {

    public static final Identifier ID = Modonomicon.loc("category_link");

    public static final MapCodec<CategoryLinkBookEntry> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Identifier.CODEC.fieldOf("id").forGetter(BookEntry::getId),
            BookEntry.BookEntryData.CODEC.forGetter(entry -> entry.data),
            Identifier.CODEC.optionalFieldOf("command_to_run_on_first_read").forGetter(entry -> Optional.ofNullable(entry.commandToRunOnFirstReadId)),
            Identifier.CODEC.fieldOf("category_to_open").forGetter(CategoryLinkBookEntry::categoryToOpenId)
    ).apply(instance, (id, data, commandToRunOnFirstReadId, categoryToOpenId) -> new CategoryLinkBookEntry(id, data, commandToRunOnFirstReadId.orElse(null), categoryToOpenId)));

    public static final StreamCodec<RegistryFriendlyByteBuf, CategoryLinkBookEntry> STREAM_CODEC = StreamCodec.composite(
            Identifier.STREAM_CODEC, BookEntry::getId,
            BookEntry.BookEntryData.STREAM_CODEC, entry -> entry.data,
            ByteBufCodecs.optional(Identifier.STREAM_CODEC), entry -> Optional.ofNullable(entry.commandToRunOnFirstReadId),
            Identifier.STREAM_CODEC, CategoryLinkBookEntry::categoryToOpenId,
            (id, data, commandToRunOnFirstReadId, categoryToOpenId) -> new CategoryLinkBookEntry(id, data, commandToRunOnFirstReadId.orElse(null), categoryToOpenId)
    );

    /**
     * The category to open on click
     */
    protected Identifier categoryToOpenId;
    protected BookCategory categoryToOpen;

    public CategoryLinkBookEntry(Identifier id, BookEntryData data, Identifier commandToRunOnFirstReadId, Identifier categoryToOpenId) {
        super(id, data, commandToRunOnFirstReadId);
        this.categoryToOpenId = categoryToOpenId;
    }

    @Override
    public BookEntryType<?> type() {
        return BookEntryTypeRegistry.CATEGORY_LINK;
    }

    @Override
    public void build(Level level, BookCategory category) {
        super.build(level, category);

        if (this.categoryToOpenId != null) {
            this.categoryToOpen = this.getBook().getCategory(this.categoryToOpenId);

            if (this.categoryToOpen == null) {
                BookErrorManager.get().error("Category to open \"" + this.categoryToOpenId + "\" does not exist in this book. Set to null.");
                this.categoryToOpenId = null;
            }
        }
    }

    public BookCategory getCategoryToOpen() {
        return this.categoryToOpen;
    }

    public Identifier categoryToOpenId() {
        return this.categoryToOpenId;
    }

    @Override
    public void openEntry(BookAddress address) {
        //if we jump to a category, we push the current category to history to be able to return
        BookGuiManager.get().pushHistory(BookAddress.defaultFor(this.getCategory()));
        //we don't have any use for the address here
        BookGuiManager.get().openCategoryLinkEntry(this);
    }

}
