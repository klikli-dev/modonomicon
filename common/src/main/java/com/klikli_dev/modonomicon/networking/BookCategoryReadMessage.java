/*
 * SPDX-FileCopyrightText: 2024 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.networking;

import com.klikli_dev.modonomicon.Modonomicon;
import com.klikli_dev.modonomicon.bookstate.BookServices;
import com.klikli_dev.modonomicon.data.BookDataManager;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

public class BookCategoryReadMessage implements Message {

    public static final Type<BookCategoryReadMessage> TYPE = new Type<>(Identifier.fromNamespaceAndPath(Modonomicon.MOD_ID, "book_category_read"));

    public static final StreamCodec<RegistryFriendlyByteBuf, BookCategoryReadMessage> STREAM_CODEC = StreamCodec.composite(
            Identifier.STREAM_CODEC,
            (m) -> m.bookId,
            Identifier.STREAM_CODEC,
            (m) -> m.categoryId,
            BookCategoryReadMessage::new
    );

    public Identifier bookId;
    public Identifier categoryId;

    public BookCategoryReadMessage(Identifier bookId, Identifier categoryId) {
        this.bookId = bookId;
        this.categoryId = categoryId;
    }

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    @Override
    public void onServerReceived(MinecraftServer minecraftServer, ServerPlayer player) {
        var book = BookDataManager.get().getBook(this.bookId);
        if (book != null) {
            var category = book.getCategory(this.categoryId);
            if (category != null) {
                var wasUnread = BookServices.interaction().isCategoryUnread(player, category);
                var firstRead = BookServices.interaction().markCategoryRead(player, category);
                if (firstRead || wasUnread) {
                    com.klikli_dev.modonomicon.bookstate.BookVisualStateManager.get().syncFor(player);
                }
            }
        }
    }
}
