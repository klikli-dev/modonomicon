/*
 * SPDX-FileCopyrightText: 2026 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.data;

import com.mojang.serialization.Codec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;

public final class DispatchCodecRegistry<T> {

    private final Map<Identifier, T> byId = new LinkedHashMap<>();
    private final Function<T, Identifier> idGetter;
    private final String family;

    public DispatchCodecRegistry(Function<T, Identifier> idGetter, String family) {
        this.idGetter = idGetter;
        this.family = family;
    }

    public <V extends T> V register(Identifier id, V value) {
        if (this.byId.putIfAbsent(id, value) != null) {
            throw new IllegalArgumentException("Duplicate registration for " + this.family + " " + id);
        }
        return value;
    }

    public T get(Identifier id) {
        T value = this.byId.get(id);
        if (value == null) {
            throw new IllegalArgumentException("Unknown " + this.family + " " + id);
        }
        return value;
    }

    public Codec<T> byNameCodec() {
        return Identifier.CODEC.xmap(this::get, this.idGetter);
    }

    public StreamCodec<RegistryFriendlyByteBuf, T> byNameStreamCodec() {
        return Identifier.STREAM_CODEC.map(this::get, this.idGetter).cast();
    }

    public Collection<T> values() {
        return this.byId.values();
    }
}
