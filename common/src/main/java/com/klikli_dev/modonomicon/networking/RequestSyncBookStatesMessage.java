/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.networking;

import com.klikli_dev.modonomicon.Modonomicon;
import com.klikli_dev.modonomicon.bookstate.BookUnlockStateManager;
import com.klikli_dev.modonomicon.bookstate.BookVisualStateManager;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

public class RequestSyncBookStatesMessage implements Message {

    public static final ResourceLocation ID = new ResourceLocation(Modonomicon.MOD_ID, "request_sync_book_states");

    public RequestSyncBookStatesMessage() {
    }

    public RequestSyncBookStatesMessage(FriendlyByteBuf buf) {
        this.decode(buf);
    }

    @Override
    public void encode(FriendlyByteBuf buf) {
    }

    @Override
    public void decode(FriendlyByteBuf buf) {
    }

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    public void onServerReceived(MinecraftServer minecraftServer, ServerPlayer player) {
        BookUnlockStateManager.get().syncFor(player);
        BookVisualStateManager.get().syncFor(player);
    }
}
