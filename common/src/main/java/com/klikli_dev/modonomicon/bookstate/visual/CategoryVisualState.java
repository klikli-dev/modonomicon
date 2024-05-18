/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.bookstate.visual;

import com.klikli_dev.modonomicon.util.Codecs;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.resources.ResourceLocation;

import java.util.Map;
import java.util.Optional;

public class CategoryVisualState {

    public static final Codec<CategoryVisualState> CODEC = RecordCodecBuilder.create((instance) -> instance.group(
            Codecs.mutableMap(ResourceLocation.CODEC, EntryVisualState.CODEC).fieldOf("entryStates").forGetter((state) -> state.entryStates),
            Codec.FLOAT.fieldOf("scrollX").forGetter((state) -> state.scrollX),
            Codec.FLOAT.fieldOf("scrollY").forGetter((state) -> state.scrollY),
            Codec.FLOAT.fieldOf("targetZoom").forGetter((state) -> state.targetZoom),
            ResourceLocation.CODEC.optionalFieldOf("openEntry").forGetter((state) -> Optional.ofNullable(state.openEntry))
    ).apply(instance, CategoryVisualState::new));

    public Map<ResourceLocation, EntryVisualState> entryStates;

    public float scrollX;
    public float scrollY;
    public float targetZoom;

    public ResourceLocation openEntry;

    public CategoryVisualState() {
        this(new Object2ObjectOpenHashMap<>(), 0, 0, 0.7f, Optional.empty());
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    public CategoryVisualState(Map<ResourceLocation, EntryVisualState> entryStates, float scrollX, float scrollY, float targetZoom, Optional<ResourceLocation> openEntry) {
        this.entryStates = entryStates;
        this.scrollX = scrollX;
        this.scrollY = scrollY;
        this.targetZoom = targetZoom;
        this.openEntry = openEntry.orElse(null);
    }
}
