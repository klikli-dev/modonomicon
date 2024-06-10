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
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SyncBookVisualStatesMessage implements Message {

    public static final Type<SyncBookVisualStatesMessage> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(Modonomicon.MOD_ID, "sync_book_visual_states"));

    public static final StreamCodec<RegistryFriendlyByteBuf, SyncBookVisualStatesMessage> STREAM_CODEC = StreamCodec.composite(
            BookVisualStates.STREAM_CODEC,
            (m) -> m.states,
            SyncBookVisualStatesMessage::new
    );

    public BookVisualStates states;

    public SyncBookVisualStatesMessage(BookVisualStates states) {
        this.states = states;
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    @Override
    public void onClientReceived(Minecraft minecraft, Player player) {
        //we are not allowed to overwrite the save data if we are in singleplayer or if we are the lan host, otherwise we would overwrite the server side save data!
        if (minecraft.getSingleplayerServer() == null) {
            BookVisualStateManager.get().saveData = new BookStatesSaveData(
                    new ConcurrentHashMap<>(),
                    new ConcurrentHashMap<>(Map.of(player.getUUID(), this.states))
            );
        }
    }
}
