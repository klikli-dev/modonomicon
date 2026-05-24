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

public class EntryLinkBookEntry extends BookEntry {

    public static final Identifier ID = Modonomicon.loc("entry_link");

    public static final MapCodec<EntryLinkBookEntry> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Identifier.CODEC.fieldOf("id").forGetter(BookEntry::getId),
            BookEntry.BookEntryData.CODEC.forGetter(entry -> entry.data),
            Identifier.CODEC.optionalFieldOf("command_to_run_on_first_read").forGetter(entry -> Optional.ofNullable(entry.commandToRunOnFirstReadId)),
            Identifier.CODEC.fieldOf("entry_to_open").forGetter(EntryLinkBookEntry::entryToOpenId)
    ).apply(instance, (id, data, commandToRunOnFirstReadId, entryToOpenId) -> new EntryLinkBookEntry(id, data, commandToRunOnFirstReadId.orElse(null), entryToOpenId)));

    public static final StreamCodec<RegistryFriendlyByteBuf, EntryLinkBookEntry> STREAM_CODEC = StreamCodec.composite(
            Identifier.STREAM_CODEC, BookEntry::getId,
            BookEntry.BookEntryData.STREAM_CODEC, entry -> entry.data,
            ByteBufCodecs.optional(Identifier.STREAM_CODEC), entry -> Optional.ofNullable(entry.commandToRunOnFirstReadId),
            Identifier.STREAM_CODEC, EntryLinkBookEntry::entryToOpenId,
            (id, data, commandToRunOnFirstReadId, entryToOpenId) -> new EntryLinkBookEntry(id, data, commandToRunOnFirstReadId.orElse(null), entryToOpenId)
    );

    /**
     * The entry to open on click
     */
    protected Identifier entryToOpenId;
    protected BookEntry entryToOpen;

    public EntryLinkBookEntry(Identifier id, BookEntryData data, Identifier commandToRunOnFirstReadId, Identifier entryToOpenId) {
        super(id, data, commandToRunOnFirstReadId);
        this.entryToOpenId = entryToOpenId;
    }

    @Override
    public BookEntryType<?> type() {
        return BookEntryTypeRegistry.ENTRY_LINK;
    }

    @Override
    public void build(Level level, BookCategory entry) {
        super.build(level, entry);

        if (this.entryToOpenId != null) {
            this.entryToOpen = this.getBook().getEntry(this.entryToOpenId);

            if (this.entryToOpen == null) {
                BookErrorManager.get().error("Entry to open \"" + this.entryToOpenId + "\" does not exist in this book. Set to null.");
                this.entryToOpenId = null;
            }
        }
    }

    public BookEntry getEntryToOpen() {
        return this.entryToOpen;
    }

    public Identifier entryToOpenId() {
        return this.entryToOpenId;
    }

    @Override
    public void openEntry(BookAddress address) {
        //if we jump to an entry, we push the current category to history to be able to return
        BookGuiManager.get().pushHistory(BookAddress.defaultFor(this.getCategory()));

        BookGuiManager.get().openEntry(this.entryToOpen, BookAddress.defaultFor(this.entryToOpen));
    }

}
