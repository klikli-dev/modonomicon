/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.networking;

import com.klikli_dev.modonomicon.Modonomicon;
import com.klikli_dev.modonomicon.data.BookDataManager;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

public class ClickCommandLinkMessage implements Message {

    public static final CustomPacketPayload.Type<ClickCommandLinkMessage> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(Modonomicon.MOD_ID, "click_command_link"));


    public static final StreamCodec<RegistryFriendlyByteBuf, ClickCommandLinkMessage> STREAM_CODEC = StreamCodec.composite(
            ResourceLocation.STREAM_CODEC,
            (m) -> m.bookId,
            ResourceLocation.STREAM_CODEC,
            (m) -> m.commandId,
            ClickCommandLinkMessage::new
    );

    public ResourceLocation bookId;
    public ResourceLocation commandId;

    public ClickCommandLinkMessage(ResourceLocation bookId, ResourceLocation commandId) {
        this.bookId = bookId;
        this.commandId = commandId;
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    @Override
    public void onServerReceived(MinecraftServer minecraftServer, ServerPlayer player) {
        var book = BookDataManager.get().getBook(this.bookId);
        if (book != null) {
            var command = book.getCommand(this.commandId);
            if (command != null) {
                command.execute(player);
            }
        }
    }
}
