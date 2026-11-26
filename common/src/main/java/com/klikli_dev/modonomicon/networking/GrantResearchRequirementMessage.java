/*
 * SPDX-FileCopyrightText: 2026 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.networking;

import com.klikli_dev.modonomicon.Modonomicon;
import com.klikli_dev.modonomicon.bookstate.BookEntryViewing;
import com.klikli_dev.modonomicon.data.BookDataManager;
import com.klikli_dev.modonomicon.research.action.ResearchGrants;
import com.klikli_dev.modonomicon.research.data.ResearchDataManager;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.permissions.Permissions;

import java.util.Objects;

/**
 * Client request by a player with game master permission (the level of the reload command) to satisfy a research requirement
 * by shift-clicking it. The server checks the permission and the ids again, the client only decides whether to send the request.
 * <ul>
 *     <li>{@link Kind#FACT}: grants the fact, see {@link ResearchGrants#grantFact}.</li>
 *     <li>{@link Kind#VALUE}: raises the value to the amount, see {@link ResearchGrants#raiseValue}.</li>
 *     <li>{@link Kind#ENTRY}: opens the entry, like the player would, see {@link BookEntryViewing#view}.</li>
 * </ul>
 */
public class GrantResearchRequirementMessage implements Message {

    public static final Type<GrantResearchRequirementMessage> TYPE = new Type<>(Identifier.fromNamespaceAndPath(Modonomicon.MOD_ID, "grant_research_requirement"));

    public static final StreamCodec<RegistryFriendlyByteBuf, GrantResearchRequirementMessage> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT,
            (m) -> m.kind.ordinal(),
            Identifier.STREAM_CODEC,
            (m) -> m.id,
            ByteBufCodecs.VAR_INT,
            (m) -> m.amount,
            (kind, id, amount) -> new GrantResearchRequirementMessage(Kind.fromOrdinal(kind), id, amount)
    );

    public enum Kind {
        FACT,
        VALUE,
        ENTRY;

        /**
         * Unknown ordinals fall back to {@link #FACT}, so a malformed packet cannot throw while it is decoded.
         */
        public static Kind fromOrdinal(int ordinal) {
            return ordinal >= 0 && ordinal < values().length ? values()[ordinal] : FACT;
        }
    }

    public final Kind kind;
    public final Identifier id;
    /**
     * The threshold of a value, unused by the other kinds.
     */
    public final int amount;

    public GrantResearchRequirementMessage(Kind kind, Identifier id, int amount) {
        this.kind = kind;
        this.id = id;
        this.amount = amount;
    }

    @Override
    public void onServerReceived(MinecraftServer minecraftServer, ServerPlayer player) {
        //the same permission as the grant fact command, checked here because the client request is not trusted
        if (!player.permissions().hasPermission(Permissions.COMMANDS_GAMEMASTER)) {
            return;
        }

        var data = ResearchDataManager.get().data();
        switch (this.kind) {
            case FACT -> {
                if (data.factIds().contains(this.id)) {
                    ResearchGrants.grantFact(player, this.id);
                }
            }
            case VALUE -> {
                if (data.valueIds().contains(this.id)) {
                    ResearchGrants.raiseValue(player, this.id, this.amount);
                }
            }
            case ENTRY -> BookDataManager.get().getBooks().values().stream()
                    .map(book -> book.getEntry(this.id))
                    .filter(Objects::nonNull)
                    .findFirst()
                    .ifPresent(entry -> BookEntryViewing.view(player, entry));
        }
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
