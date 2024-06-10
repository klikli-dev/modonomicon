/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.networking;

import com.klikli_dev.modonomicon.Modonomicon;
import com.klikli_dev.modonomicon.api.ModonomiconConstants.I18n.Command;
import com.klikli_dev.modonomicon.bookstate.BookUnlockStateManager;
import net.minecraft.ChatFormatting;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

public class SendUnlockCodeToServerMessage implements Message {

    public static final Type<SendUnlockCodeToServerMessage> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(Modonomicon.MOD_ID, "send_unlock_code_to_server"));

    public static final StreamCodec<RegistryFriendlyByteBuf, SendUnlockCodeToServerMessage> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8,
            (m) -> m.unlockCode,
            SendUnlockCodeToServerMessage::new
    );

    public String unlockCode;

    public SendUnlockCodeToServerMessage(String unlockCode) {
        this.unlockCode = unlockCode;
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    @Override
    public void onServerReceived(MinecraftServer minecraftServer, ServerPlayer player) {
        var book = BookUnlockStateManager.get().applyUnlockCodeFor(player, this.unlockCode);
        if (book != null) {
            player.sendSystemMessage(Component.translatable(Command.SUCCESS_LOAD_PROGRESS, Component.translatable(book.getName())));
        } else {
            player.sendSystemMessage(Component.translatable(Command.ERROR_LOAD_PROGRESS).withStyle(ChatFormatting.RED));
        }

    }
}
