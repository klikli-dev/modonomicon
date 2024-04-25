/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.networking;

import com.klikli_dev.modonomicon.Modonomicon;
import com.klikli_dev.modonomicon.book.BookEntry;
import com.klikli_dev.modonomicon.bookstate.BookVisualStateManager;
import com.klikli_dev.modonomicon.data.BookDataManager;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

public class SaveEntryStateMessage implements Message {

    public static final Type<SaveEntryStateMessage> TYPE = new Type<>(new ResourceLocation(Modonomicon.MOD_ID, "save_entry_state"));

    public static final StreamCodec<RegistryFriendlyByteBuf, SaveEntryStateMessage> STREAM_CODEC = CustomPacketPayload.codec(SaveEntryStateMessage::encode, SaveEntryStateMessage::new);


    public BookEntry entry;
    public int openPagesIndex;

    public SaveEntryStateMessage(BookEntry entry, int openPagesIndex) {
        this.entry = entry;
        this.openPagesIndex = openPagesIndex;
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
        this.entry = BookDataManager.get().getBook(buf.readResourceLocation()).getEntry(buf.readResourceLocation());
        this.openPagesIndex = buf.readVarInt();
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    @Override
    public void onServerReceived(MinecraftServer minecraftServer, ServerPlayer player) {
        var currentState = BookVisualStateManager.get().getEntryStateFor(player, this.entry);
        currentState.openPagesIndex = this.openPagesIndex;
        BookVisualStateManager.get().setEntryStateFor(player, this.entry, currentState);
        BookVisualStateManager.get().syncFor(player);
    }
}
