/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.network.messages;

import com.klikli_dev.modonomicon.api.ModonimiconConstants.I18n.Command;
import com.klikli_dev.modonomicon.capability.BookUnlockCapability;
import com.klikli_dev.modonomicon.network.Message;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent.Context;

public class SendUnlockCodeToServerMessage implements Message {

    public String unlockCode;

    public SendUnlockCodeToServerMessage(String unlockCode) {
        this.unlockCode = unlockCode;
    }

    public SendUnlockCodeToServerMessage(FriendlyByteBuf buf) {
        this.decode(buf);
    }

    @Override
    public void encode(FriendlyByteBuf buf) {
        buf.writeUtf(this.unlockCode);
    }

    @Override
    public void decode(FriendlyByteBuf buf) {
        this.unlockCode = buf.readUtf();
    }

    @Override
    public void onServerReceived(MinecraftServer minecraftServer, ServerPlayer player, Context context) {
        var book = BookUnlockCapability.applyUnlockCodeFor(player, this.unlockCode);
        if (book != null) {
            player.sendMessage(new TranslatableComponent(Command.SUCCESS_LOAD_PROGRESS, new TranslatableComponent(book.getName())), Util.NIL_UUID);
        } else {
            player.sendMessage(new TranslatableComponent(Command.ERROR_LOAD_PROGRESS).withStyle(ChatFormatting.RED), Util.NIL_UUID);
        }

    }
}
