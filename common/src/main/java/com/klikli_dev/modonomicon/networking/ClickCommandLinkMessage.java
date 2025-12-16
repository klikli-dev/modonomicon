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
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

public class ClickCommandLinkMessage implements Message {

    public static final CustomPacketPayload.Type<ClickCommandLinkMessage> TYPE = new CustomPacketPayload.Type<>(Identifier.fromNamespaceAndPath(Modonomicon.MOD_ID, "click_command_link"));


    public static final StreamCodec<RegistryFriendlyByteBuf, ClickCommandLinkMessage> STREAM_CODEC = StreamCodec.composite(
            Identifier.STREAM_CODEC,
            (m) -> m.bookId,
            Identifier.STREAM_CODEC,
            (m) -> m.commandId,
            Identifier.STREAM_CODEC,
            (m) -> m.entryId,
            ClickCommandLinkMessage::new
    );

    public Identifier bookId;
    public Identifier commandId;
    public Identifier entryId; // The entry the command is being run from

    public ClickCommandLinkMessage(Identifier bookId, Identifier commandId, Identifier entryId) {
        this.bookId = bookId;
        this.commandId = commandId;
        this.entryId = entryId;
    }

    // Backwards compatibility
    public ClickCommandLinkMessage(Identifier bookId, Identifier commandId) {
        this(bookId, commandId, null);
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
                // Check if the entry is allowed for this command
                if (this.entryId != null && !command.isEntryAllowed(this.entryId)) {
                    // Always use the modonomicon-defined failure message
                    player.sendSystemMessage(net.minecraft.network.chat.Component.translatable(
                        com.klikli_dev.modonomicon.api.ModonomiconConstants.I18n.Command.FAILURE_NOT_ALLOWED_HERE
                    ).withStyle(net.minecraft.ChatFormatting.RED));
                    return;
                }
                command.execute(player);
            }
        }
    }
}
