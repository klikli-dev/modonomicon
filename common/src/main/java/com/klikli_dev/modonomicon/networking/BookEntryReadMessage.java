/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.networking;

import com.klikli_dev.modonomicon.Modonomicon;
import com.klikli_dev.modonomicon.api.events.EntryFirstReadEvent;
import com.klikli_dev.modonomicon.bookstate.BookServices;
import com.klikli_dev.modonomicon.data.BookDataManager;
import com.klikli_dev.modonomicon.events.ModonomiconEvents;
import com.klikli_dev.modonomicon.research.ResearchServices;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

public class BookEntryReadMessage implements Message {

    public static final Type<BookEntryReadMessage> TYPE = new Type<>(Identifier.fromNamespaceAndPath(Modonomicon.MOD_ID, "book_entry_read"));

    public static final StreamCodec<RegistryFriendlyByteBuf, BookEntryReadMessage> STREAM_CODEC = StreamCodec.composite(
            Identifier.STREAM_CODEC,
            (m) -> m.bookId,
            Identifier.STREAM_CODEC,
            (m) -> m.entryId,
            BookEntryReadMessage::new
    );

    public Identifier bookId;
    public Identifier entryId;

    public BookEntryReadMessage(Identifier bookId, Identifier entryId) {
        this.bookId = bookId;
        this.entryId = entryId;
    }

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    @Override
    public void onServerReceived(MinecraftServer minecraftServer, ServerPlayer player) {
        var entry = BookDataManager.get().getBook(this.bookId).getEntry(this.entryId);
        //unlock page, then update the unlock capability, finally sync.
        if (BookServices.interaction().markEntryRead(player, entry)) {
            ResearchServices.hooks().onEntryViewedOnce(player, entry.getId());
            BookServices.stateAccess().updateAndSync(player);
            ModonomiconEvents.server().entryFirstRead(new EntryFirstReadEvent(entry.getBook().getId(), entry.getId()));
        }
    }
}
