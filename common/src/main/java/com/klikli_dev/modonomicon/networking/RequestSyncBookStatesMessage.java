/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.networking;

import com.klikli_dev.modonomicon.Modonomicon;
import com.klikli_dev.modonomicon.bookstate.BookUnlockStateManager;
import com.klikli_dev.modonomicon.bookstate.BookVisualStateManager;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

public class RequestSyncBookStatesMessage implements Message {

    public static final RequestSyncBookStatesMessage INSTANCE = new RequestSyncBookStatesMessage();

    public static final Type<RequestSyncBookStatesMessage> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(Modonomicon.MOD_ID, "request_sync_book_states"));

    public static final StreamCodec<RegistryFriendlyByteBuf, RequestSyncBookStatesMessage> STREAM_CODEC = StreamCodec.unit(INSTANCE);


    private RequestSyncBookStatesMessage() {
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    @Override
    public void onServerReceived(MinecraftServer minecraftServer, ServerPlayer player) {
        BookUnlockStateManager.get().syncFor(player);
        BookVisualStateManager.get().syncFor(player);
    }
}
