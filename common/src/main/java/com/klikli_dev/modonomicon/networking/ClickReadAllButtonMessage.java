/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.networking;

import com.klikli_dev.modonomicon.Modonomicon;
import com.klikli_dev.modonomicon.bookstate.BookUnlockStateManager;
import com.klikli_dev.modonomicon.data.BookDataManager;
import com.mojang.serialization.Codec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

public class ClickReadAllButtonMessage implements Message {

    public static final Type<ClickReadAllButtonMessage> TYPE = new Type<>(new ResourceLocation(Modonomicon.MOD_ID, "click_read_all_button"));

    public static final StreamCodec<RegistryFriendlyByteBuf, ClickReadAllButtonMessage> STREAM_CODEC = StreamCodec.composite(
            ResourceLocation.STREAM_CODEC,
            ( m) -> m.bookId,
            ByteBufCodecs.BOOL,
            (m) -> m.readAll,
            ClickReadAllButtonMessage::new
    );

    public ResourceLocation bookId;
    public boolean readAll; //true to not only read unlocked but even the locked ones

    public ClickReadAllButtonMessage(ResourceLocation bookId, boolean readAll) {
        this.bookId = bookId;
        this.readAll = readAll;
    }

    @Override
    public void onServerReceived(MinecraftServer minecraftServer, ServerPlayer player) {
        var book = BookDataManager.get().getBook(this.bookId);
        if (book != null) {
            //unlock pages, then update the unlock capability, finally sync.
            var anyRead = false;
            for (var entry : book.getEntries().values()) {
                if ((this.readAll || BookUnlockStateManager.get().isUnlockedFor(player, entry)) && BookUnlockStateManager.get().readFor(player, entry)) {
                    anyRead = true;
                }
            }

            if (anyRead) {
                BookUnlockStateManager.get().updateAndSyncFor(player);
            }
        }
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
