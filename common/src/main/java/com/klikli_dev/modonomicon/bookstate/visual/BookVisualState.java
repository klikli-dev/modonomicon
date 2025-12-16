/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.bookstate.visual;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.Object2ObjectMaps;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Optional;

public class BookVisualState {
    public static final Codec<BookVisualState> CODEC = RecordCodecBuilder.create((instance) -> instance.group(
            Codec.unboundedMap(Identifier.CODEC, CategoryVisualState.CODEC).fieldOf("categoryStates").forGetter((state) -> state.categoryStates),
            Identifier.CODEC.optionalFieldOf("openCategory").forGetter((state) -> Optional.ofNullable(state.openCategory)),
            Codec.INT.fieldOf("openPagesIndex").forGetter((state) -> state.openPagesIndex)
    ).apply(instance, BookVisualState::new));

    public Map<Identifier, CategoryVisualState> categoryStates;

    @Nullable
    public Identifier openCategory;

    /**
     * For books in index mode
     */
    public int openPagesIndex;

    public BookVisualState() {
        this(Object2ObjectMaps.emptyMap(), (Identifier) null, 0);
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    public BookVisualState(Map<Identifier, CategoryVisualState> categoryStates, Optional<Identifier> openCategory, int openPagesIndex) {
        this(categoryStates, openCategory.orElse(null), openPagesIndex);
    }

    public BookVisualState(Map<Identifier, CategoryVisualState> categoryStates, @Nullable Identifier openCategory, int openPagesIndex) {
        this.categoryStates = new Object2ObjectOpenHashMap<>(categoryStates);
        this.openCategory = openCategory;
        this.openPagesIndex = openPagesIndex;
    }
}
