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
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

public class SendUnlockCodeToServerMessage implements Message {

    public static final ResourceLocation ID = new ResourceLocation(Modonomicon.MOD_ID, "send_unlock_code_to_server");
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
    public ResourceLocation getId() {
        return ID;
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
