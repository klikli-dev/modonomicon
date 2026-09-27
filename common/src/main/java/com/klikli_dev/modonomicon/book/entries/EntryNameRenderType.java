// SPDX-FileCopyrightText: 2026 klikli-dev
//
// SPDX-License-Identifier: MIT

package com.klikli_dev.modonomicon.book.entries;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.NotNull;

import java.util.function.IntFunction;

/**
 * Controls if and where an entry's name is permanently rendered next to its badge
 * on node-based category screens, similar to the vanilla advancement screen.
 */
public enum EntryNameRenderType implements StringRepresentable {
    NONE("none"),
    LEFT("left"),
    RIGHT("right"),
    TOP("top"),
    BOTTOM("bottom");

    public static final StringRepresentable.EnumCodec<EntryNameRenderType> CODEC = StringRepresentable.fromEnum(EntryNameRenderType::values);
    private static final IntFunction<EntryNameRenderType> BY_ID = ByIdMap.continuous(Enum::ordinal, values(), ByIdMap.OutOfBoundsStrategy.WRAP);
    public static final StreamCodec<ByteBuf, EntryNameRenderType> STREAM_CODEC = ByteBufCodecs.idMapper(BY_ID, EntryNameRenderType::ordinal);
    private final String name;

    EntryNameRenderType(String name) {
        this.name = name;
    }

    public static EntryNameRenderType byName(String name) {
        return CODEC.byName(name);
    }

    public static EntryNameRenderType byId(int id) {
        return BY_ID.apply(id);
    }

    @Override
    public @NotNull String getSerializedName() {
        return this.name;
    }
}
