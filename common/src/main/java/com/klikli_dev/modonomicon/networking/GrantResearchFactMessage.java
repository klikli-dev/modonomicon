/*
 * SPDX-FileCopyrightText: 2026 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.networking;

import com.klikli_dev.modonomicon.Modonomicon;
import com.klikli_dev.modonomicon.research.action.ResearchFactGrants;
import com.klikli_dev.modonomicon.research.data.ResearchDataManager;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.permissions.Permissions;

/**
 * Client request to grant a research fact, sent by players with game master permission (the level of the reload command)
 * when they shift-click a requirement of a research entry. The server checks the permission and the fact again, the client
 * only decides whether to send the request.
 */
public class GrantResearchFactMessage implements Message {

    public static final Type<GrantResearchFactMessage> TYPE = new Type<>(Identifier.fromNamespaceAndPath(Modonomicon.MOD_ID, "grant_research_fact"));

    public static final StreamCodec<RegistryFriendlyByteBuf, GrantResearchFactMessage> STREAM_CODEC = StreamCodec.composite(
            Identifier.STREAM_CODEC,
            (m) -> m.factId,
            GrantResearchFactMessage::new
    );

    public Identifier factId;

    public GrantResearchFactMessage(Identifier factId) {
        this.factId = factId;
    }

    @Override
    public void onServerReceived(MinecraftServer minecraftServer, ServerPlayer player) {
        //the same permission as the grant fact command, checked here because the client request is not trusted
        if (!player.permissions().hasPermission(Permissions.COMMANDS_GAMEMASTER)) {
            return;
        }

        if (!ResearchDataManager.get().data().factIds().contains(this.factId)) {
            return;
        }

        ResearchFactGrants.grant(player, this.factId);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
