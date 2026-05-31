/*
 * SPDX-FileCopyrightText: 2023 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.networking;

import com.klikli_dev.modonomicon.Modonomicon;
import com.klikli_dev.modonomicon.research.state.PlayerResearchState;
import com.klikli_dev.modonomicon.research.state.ResearchStateManager;
import net.minecraft.client.Minecraft;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;

public class SyncResearchStateMessage implements Message {

    public static final Type<SyncResearchStateMessage> TYPE = new Type<>(Identifier.fromNamespaceAndPath(Modonomicon.MOD_ID, "sync_research_state"));
    public static final StreamCodec<RegistryFriendlyByteBuf, SyncResearchStateMessage> STREAM_CODEC = StreamCodec.composite(
            PlayerResearchState.STREAM_CODEC,
            message -> message.state,
            SyncResearchStateMessage::new
    );

    public final PlayerResearchState state;

    public SyncResearchStateMessage(PlayerResearchState state) {
        this.state = state;
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    @Override
    public void onClientReceived(Minecraft minecraft, Player player) {
        if (minecraft.getSingleplayerServer() == null) {
            ResearchStateManager.get().installClientState(player, this.state);
        }
    }
}
