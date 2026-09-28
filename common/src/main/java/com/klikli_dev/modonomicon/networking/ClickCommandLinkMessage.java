/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.networking;

import com.klikli_dev.modonomicon.Modonomicon;
import com.klikli_dev.modonomicon.data.BookDataManager;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class ClickCommandLinkMessage implements Message {

    public static final CustomPacketPayload.Type<ClickCommandLinkMessage> TYPE = new CustomPacketPayload.Type<>(Identifier.fromNamespaceAndPath(Modonomicon.MOD_ID, "click_command_link"));


    public static final StreamCodec<RegistryFriendlyByteBuf, ClickCommandLinkMessage> STREAM_CODEC = StreamCodec.composite(
            Identifier.STREAM_CODEC,
            (m) -> m.bookId,
            Identifier.STREAM_CODEC,
            (m) -> m.commandId,
            //the entry id is absent when the link is clicked outside of an entry, e.g. in a category description
            ByteBufCodecs.optional(Identifier.STREAM_CODEC).map((id) -> id.orElse(null), Optional::ofNullable),
            (m) -> m.entryId,
            ClickCommandLinkMessage::new
    );

    public Identifier bookId;
    public Identifier commandId;
    /**
     * The entry the command is being run from. Must be non-null: commands clicked
     * outside of an entry (e.g. in a category description) are rejected by the server,
     * as the entry restriction cannot be verified without an entry context.
     */
    @Nullable
    public Identifier entryId;

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
                // Commands require an entry context: without an entry id (e.g. from a category
                // description) the entry restriction cannot be verified, so the command is rejected.
                // This also guards against modified clients sending packets with no entry id to
                // bypass the entry restriction check.
                if (this.entryId == null || !command.isEntryAllowed(this.entryId)) {
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
