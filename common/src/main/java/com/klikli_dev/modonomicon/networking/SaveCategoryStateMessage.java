/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.networking;

import com.klikli_dev.modonomicon.Modonomicon;
import com.klikli_dev.modonomicon.book.BookCategory;
import com.klikli_dev.modonomicon.bookstate.BookVisualStateManager;
import com.klikli_dev.modonomicon.bookstate.visual.CategoryVisualState;
import com.klikli_dev.modonomicon.data.BookDataManager;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

public class SaveCategoryStateMessage implements Message {

    public static final Type<SaveCategoryStateMessage> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(Modonomicon.MOD_ID, "save_category_state"));
    public static final StreamCodec<RegistryFriendlyByteBuf, SaveCategoryStateMessage> STREAM_CODEC = CustomPacketPayload.codec(SaveCategoryStateMessage::encode, SaveCategoryStateMessage::new);

    public BookCategory category;

    public float scrollX = 0;
    public float scrollY = 0;
    public float targetZoom;

    public ResourceLocation openEntry = null;

    /**
     * Ids kept for safe (re-)resolution on the server: the book may be unknown on the server
     * (loading failed or client/server content mismatch) or not yet built when the packet arrives.
     * See https://github.com/klikli-dev/modonomicon/issues/368
     */
    private ResourceLocation bookId;
    private ResourceLocation categoryId;

    public SaveCategoryStateMessage(BookCategory category, CategoryVisualState state) {
        this(category, state.scrollX, state.scrollY, state.targetZoom, state.openEntry);
    }

    public SaveCategoryStateMessage(BookCategory category, float scrollX, float scrollY, float targetZoom, ResourceLocation openEntry) {
        this.category = category;
        this.scrollX = scrollX;
        this.scrollY = scrollY;
        this.targetZoom = targetZoom;
        this.openEntry = openEntry;
        this.bookId = category.getBook().getId();
        this.categoryId = category.getId();
    }

    public SaveCategoryStateMessage(RegistryFriendlyByteBuf buf) {
        this.decode(buf);
    }

    private void encode(RegistryFriendlyByteBuf buf) {
        buf.writeResourceLocation(this.category.getBook().getId());
        buf.writeResourceLocation(this.category.getId());
        buf.writeFloat(this.scrollX);
        buf.writeFloat(this.scrollY);
        buf.writeFloat(this.targetZoom);
        buf.writeBoolean(this.openEntry != null);
        if (this.openEntry != null) {
            buf.writeResourceLocation(this.openEntry);
        }
    }

    private void decode(RegistryFriendlyByteBuf buf) {
        this.bookId = buf.readResourceLocation();
        this.categoryId = buf.readResourceLocation();
        this.category = this.resolveCategory();
        this.scrollX = buf.readFloat();
        this.scrollY = buf.readFloat();
        this.targetZoom = buf.readFloat();
        if (buf.readBoolean()) {
            this.openEntry = buf.readResourceLocation();
        }
    }

    private BookCategory resolveCategory() {
        var book = BookDataManager.get().getBook(this.bookId);
        if (book == null) {
            return null;
        }
        return book.getCategory(this.categoryId);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    @Override
    public void onServerReceived(MinecraftServer minecraftServer, ServerPlayer player) {
        if (this.category == null || this.category.getBook() == null) {
            //The book may not be built yet (e.g. right after /reload) - build it lazily and try again.
            BookDataManager.get().tryBuildBooks(player.level());
            this.category = this.resolveCategory();
        }

        if (this.category == null) {
            //The client knows a book/category the server does not (book failed to load on the server
            //or client/server content mismatch, e.g. in hybrid modpacks). Never crash the server
            //tick over this, ignore the state save and log.
            Modonomicon.LOG.warn("Received SaveCategoryStateMessage for unknown category '{}' in book '{}' from player '{}'. The book is not loaded on the server. Ignoring to avoid a crash (see #368).", this.categoryId, this.bookId, player.getName().getString());
            return;
        }

        if (this.category.getBook() == null) {
            Modonomicon.LOG.warn("Received SaveCategoryStateMessage for category '{}' in book '{}' from player '{}', but the category is not linked to a book (book not built). Ignoring to avoid a crash (see #368).", this.categoryId, this.bookId, player.getName().getString());
            return;
        }

        var currentState = BookVisualStateManager.get().getCategoryStateFor(player, this.category);
        currentState.scrollX = this.scrollX;
        currentState.scrollY = this.scrollY;
        currentState.targetZoom = this.targetZoom;
        currentState.openEntry = this.openEntry;
        BookVisualStateManager.get().setCategoryStateFor(player, this.category, currentState);
        BookVisualStateManager.get().syncFor(player);
    }
}
