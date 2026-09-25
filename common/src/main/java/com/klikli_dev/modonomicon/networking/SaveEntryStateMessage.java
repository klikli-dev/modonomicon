/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.networking;

import com.klikli_dev.modonomicon.Modonomicon;
import com.klikli_dev.modonomicon.book.entries.BookEntry;
import com.klikli_dev.modonomicon.bookstate.BookVisualStateManager;
import com.klikli_dev.modonomicon.bookstate.visual.EntryVisualState;
import com.klikli_dev.modonomicon.data.BookDataManager;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

public class SaveEntryStateMessage implements Message {

    public static final Type<SaveEntryStateMessage> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(Modonomicon.MOD_ID, "save_entry_state"));

    public static final StreamCodec<RegistryFriendlyByteBuf, SaveEntryStateMessage> STREAM_CODEC = CustomPacketPayload.codec(SaveEntryStateMessage::encode, SaveEntryStateMessage::new);


    public BookEntry entry;
    public int openPagesIndex;

    /**
     * Ids kept for safe (re-)resolution on the server: the book may be unknown on the server
     * (loading failed or client/server content mismatch) or not yet built when the packet arrives.
     * See https://github.com/klikli-dev/modonomicon/issues/368
     */
    private ResourceLocation bookId;
    private ResourceLocation entryId;

    public SaveEntryStateMessage(BookEntry entry, EntryVisualState state) {
        this(entry, state.openPagesIndex);
    }

    public SaveEntryStateMessage(BookEntry entry, int openPagesIndex) {
        this.entry = entry;
        this.openPagesIndex = openPagesIndex;
        this.bookId = entry.getBook().getId();
        this.entryId = entry.getId();
    }

    public SaveEntryStateMessage(RegistryFriendlyByteBuf buf) {
        this.decode(buf);
    }

    private void encode(RegistryFriendlyByteBuf buf) {
        buf.writeResourceLocation(this.entry.getBook().getId());
        buf.writeResourceLocation(this.entry.getId());
        buf.writeVarInt(this.openPagesIndex);
    }

    private void decode(RegistryFriendlyByteBuf buf) {
        this.bookId = buf.readResourceLocation();
        this.entryId = buf.readResourceLocation();
        this.openPagesIndex = buf.readVarInt();
        this.entry = this.resolveEntry();
    }

    private BookEntry resolveEntry() {
        var book = BookDataManager.get().getBook(this.bookId);
        if (book == null) {
            return null;
        }
        return book.getEntry(this.entryId);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    @Override
    public void onServerReceived(MinecraftServer minecraftServer, ServerPlayer player) {
        if (this.entry == null || this.entry.getCategory() == null || this.entry.getBook() == null) {
            //The book may not be built yet (e.g. right after /reload) - build it lazily and try again.
            BookDataManager.get().tryBuildBooks(player.level());
            this.entry = this.resolveEntry();
        }

        if (this.entry == null) {
            //The client knows a book/entry the server does not (book failed to load on the server
            //or client/server content mismatch, e.g. in hybrid modpacks). Never crash the server
            //tick over this, ignore the state save and log.
            Modonomicon.LOG.warn("Received SaveEntryStateMessage for unknown entry '{}' in book '{}' from player '{}'. The book is not loaded on the server. Ignoring to avoid a crash (see #368).", this.entryId, this.bookId, player.getName().getString());
            return;
        }

        if (this.entry.getCategory() == null || this.entry.getBook() == null) {
            Modonomicon.LOG.warn("Received SaveEntryStateMessage for entry '{}' in book '{}' from player '{}', but the entry is not linked to a book (book not built). Ignoring to avoid a crash (see #368).", this.entryId, this.bookId, player.getName().getString());
            return;
        }

        var currentState = BookVisualStateManager.get().getEntryStateFor(player, this.entry);
        currentState.openPagesIndex = this.openPagesIndex;
        BookVisualStateManager.get().setEntryStateFor(player, this.entry, currentState);
        BookVisualStateManager.get().syncFor(player);
    }
}
