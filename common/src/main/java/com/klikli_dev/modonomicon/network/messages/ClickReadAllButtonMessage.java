/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.network.messages;

import com.klikli_dev.modonomicon.data.BookDataManager;
import com.klikli_dev.modonomicon.network.Message;
import com.klikli_dev.modonomicon.registry.CapabilityRegistry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent.Context;

public class ClickReadAllButtonMessage implements Message {

    public ResourceLocation bookId;
    public boolean readAll; //true to not only read unlocked but even the locked ones

    public ClickReadAllButtonMessage(ResourceLocation bookId, boolean readAll) {
        this.bookId = bookId;
        this.readAll = readAll;
    }

    public ClickReadAllButtonMessage(FriendlyByteBuf buf) {
        this.decode(buf);
    }

    @Override
    public void encode(FriendlyByteBuf buf) {
        buf.writeResourceLocation(this.bookId);
        buf.writeBoolean(this.readAll);
    }

    @Override
    public void decode(FriendlyByteBuf buf) {
        this.bookId = buf.readResourceLocation();
        this.readAll = buf.readBoolean();
    }

    @Override
    public void onServerReceived(MinecraftServer minecraftServer, ServerPlayer player, Context context) {
        player.getCapability(CapabilityRegistry.BOOK_UNLOCK).ifPresent(capability -> {
            var book = BookDataManager.get().getBook(this.bookId);
            if(book != null){
                //unlock pages, then update the unlock capability, finally sync.
                var anyRead = false;
                for(var entry : book.getEntries().values()){
                    if ((this.readAll || capability.isUnlocked(entry)) && capability.read(entry)) {
                        anyRead = true;
                    }
                }

                if(anyRead){
                    capability.update(player);
                    capability.sync(player);
                }
            }
        });
    }
}
