/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.networking;

import com.klikli_dev.modonomicon.Modonomicon;
import com.klikli_dev.modonomicon.bookstate.BookStatesSaveData;
import com.klikli_dev.modonomicon.bookstate.BookVisualStateManager;
import com.klikli_dev.modonomicon.bookstate.BookVisualStates;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SyncBookVisualStatesMessage implements Message {

    public static final ResourceLocation ID = new ResourceLocation(Modonomicon.MOD_ID, "sync_book_visual_states");

    public BookVisualStates states;

    public SyncBookVisualStatesMessage(BookVisualStates states) {
        this.states = states;
    }

    public SyncBookVisualStatesMessage(FriendlyByteBuf buf) {
        this.decode(buf);
    }

    @Override
    public void encode(FriendlyByteBuf buf) {
        buf.writeJsonWithCodec(BookVisualStates.CODEC, this.states);
    }

    @Override
    public void decode(FriendlyByteBuf buf) {
        this.states = buf.readJsonWithCodec(BookVisualStates.CODEC);
    }

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    public void onClientReceived(Minecraft minecraft, Player player) {
        BookVisualStateManager.get().saveData = new BookStatesSaveData(
                new ConcurrentHashMap<>(),
                new ConcurrentHashMap<>(Map.of(player.getUUID(), this.states))
        );
    }
}
