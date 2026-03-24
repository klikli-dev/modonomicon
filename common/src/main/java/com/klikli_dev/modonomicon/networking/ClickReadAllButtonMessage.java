/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.networking;

import com.klikli_dev.modonomicon.Modonomicon;
import com.klikli_dev.modonomicon.bookstate.BookUnlockStateManager;
import com.klikli_dev.modonomicon.data.BookDataManager;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

public class ClickReadAllButtonMessage implements Message {

    public static final Type<ClickReadAllButtonMessage> TYPE = new Type<>(Identifier.fromNamespaceAndPath(Modonomicon.MOD_ID, "click_read_all_button"));

    public static final StreamCodec<RegistryFriendlyByteBuf, ClickReadAllButtonMessage> STREAM_CODEC = StreamCodec.composite(
            Identifier.STREAM_CODEC,
            (m) -> m.bookId,
            ByteBufCodecs.BOOL,
            (m) -> m.readAll,
            ClickReadAllButtonMessage::new
    );

    public Identifier bookId;
    public boolean readAll; //true to not only read unlocked but even the locked ones

    public ClickReadAllButtonMessage(Identifier bookId, boolean readAll) {
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

            for (var category : book.getCategories().values()) {
                if ((this.readAll || BookUnlockStateManager.get().isUnlockedFor(player, category)) && BookUnlockStateManager.get().readCategoryFor(player, category)) {
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
