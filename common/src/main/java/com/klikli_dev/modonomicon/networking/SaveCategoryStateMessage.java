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

    public SaveCategoryStateMessage(BookCategory category, CategoryVisualState state) {
        this(category, state.scrollX, state.scrollY, state.targetZoom, state.openEntry);
    }

    public SaveCategoryStateMessage(BookCategory category, float scrollX, float scrollY, float targetZoom, ResourceLocation openEntry) {
        this.category = category;
        this.scrollX = scrollX;
        this.scrollY = scrollY;
        this.targetZoom = targetZoom;
        this.openEntry = openEntry;
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
        this.category = BookDataManager.get().getBook(buf.readResourceLocation()).getCategory(buf.readResourceLocation());
        this.scrollX = buf.readFloat();
        this.scrollY = buf.readFloat();
        this.targetZoom = buf.readFloat();
        if (buf.readBoolean()) {
            this.openEntry = buf.readResourceLocation();
        }
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    @Override
    public void onServerReceived(MinecraftServer minecraftServer, ServerPlayer player) {
        var currentState = BookVisualStateManager.get().getCategoryStateFor(player, this.category);
        currentState.scrollX = this.scrollX;
        currentState.scrollY = this.scrollY;
        currentState.targetZoom = this.targetZoom;
        currentState.openEntry = this.openEntry;
        BookVisualStateManager.get().setCategoryStateFor(player, this.category, currentState);
        BookVisualStateManager.get().syncFor(player);
    }
}
