/*
 * SPDX-FileCopyrightText: 2023 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.research.state;

import com.klikli_dev.modonomicon.Modonomicon;
import com.klikli_dev.modonomicon.util.Codecs;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.Object2ObjectMaps;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.resources.Identifier;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;

import java.util.Map;
import java.util.UUID;

public class ResearchStatesSaveData extends SavedData {

    public static final Identifier ID = Modonomicon.loc("research_states");

    public static final Codec<ResearchStatesSaveData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.unboundedMap(Codecs.UUID, PlayerResearchState.CODEC).fieldOf("playerResearchStates").forGetter(data -> data.playerResearchStates)
    ).apply(instance, ResearchStatesSaveData::new));

    public static final SavedDataType<ResearchStatesSaveData> TYPE = new SavedDataType<>(ID, ResearchStatesSaveData::new, CODEC, DataFixTypes.PLAYER);

    public final Map<UUID, PlayerResearchState> playerResearchStates;

    public ResearchStatesSaveData() {
        this(Object2ObjectMaps.emptyMap());
    }

    public ResearchStatesSaveData(Map<UUID, PlayerResearchState> playerResearchStates) {
        this.playerResearchStates = Object2ObjectMaps.synchronize(new Object2ObjectOpenHashMap<>(playerResearchStates));
        this.setDirty();
    }

    public PlayerResearchState getFor(UUID playerId) {
        return this.playerResearchStates.computeIfAbsent(playerId, ignored -> {
            this.setDirty();
            return new PlayerResearchState();
        });
    }
}
