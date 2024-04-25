/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.networking;

import com.klikli_dev.modonomicon.Modonomicon;
import com.klikli_dev.modonomicon.bookstate.BookStatesSaveData;
import com.klikli_dev.modonomicon.bookstate.BookUnlockStateManager;
import com.klikli_dev.modonomicon.bookstate.BookUnlockStates;
import com.klikli_dev.modonomicon.client.gui.BookGuiManager;
import net.minecraft.client.Minecraft;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SyncBookUnlockStatesMessage implements Message {

    public static final Type<SyncBookUnlockStatesMessage> TYPE = new Type<>(new ResourceLocation(Modonomicon.MOD_ID, "sync_book_unlock_states"));

    public static final StreamCodec<RegistryFriendlyByteBuf, SyncBookUnlockStatesMessage> STREAM_CODEC = StreamCodec.composite(
            BookUnlockStates.STREAM_CODEC,
            (m) -> m.states,
            SyncBookUnlockStatesMessage::new
    );

    public BookUnlockStates states;

    public SyncBookUnlockStatesMessage(BookUnlockStates states) {
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
            BookUnlockStateManager.get().saveData = new BookStatesSaveData(
                    new ConcurrentHashMap<>(Map.of(player.getUUID(), this.states)),
                    new ConcurrentHashMap<>()
            );
        }

        //but firing the update event is fine :)
        if (BookGuiManager.get().openOverviewScreen != null) {
            BookGuiManager.get().openOverviewScreen.onSyncBookUnlockCapabilityMessage(this);
        }
    }
}
