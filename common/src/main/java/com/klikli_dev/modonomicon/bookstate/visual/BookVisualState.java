/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.bookstate.visual;

import com.klikli_dev.modonomicon.util.Codecs;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class BookVisualState {
    public static final Codec<BookVisualState> CODEC = RecordCodecBuilder.create((instance) -> instance.group(
            Codecs.mutableMap(ResourceLocation.CODEC, CategoryVisualState.CODEC).fieldOf("categoryStates").forGetter((state) -> state.categoryStates),
            ResourceLocation.CODEC.optionalFieldOf("openCategory").forGetter((state) -> Optional.ofNullable(state.openCategory))).apply(instance, BookVisualState::new));

    public Map<ResourceLocation, CategoryVisualState> categoryStates;

    public ResourceLocation openCategory;

    public BookVisualState() {
        this(new HashMap<>(), Optional.empty());
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    public BookVisualState(Map<ResourceLocation, CategoryVisualState> categoryStates, Optional<ResourceLocation> openCategory) {
        this.categoryStates = new HashMap<>(categoryStates);
        this.openCategory = openCategory.orElse(null);
    }
}
