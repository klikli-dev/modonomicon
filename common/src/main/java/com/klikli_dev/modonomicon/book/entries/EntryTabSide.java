/*
 * SPDX-FileCopyrightText: 2026 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.book.entries;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.NotNull;

import java.util.function.IntFunction;

/**
 * The book edge an entry tab is attached to.
 */
public enum EntryTabSide implements StringRepresentable {
    LEFT("left"),
    RIGHT("right");

    public static final StringRepresentable.EnumCodec<EntryTabSide> CODEC = StringRepresentable.fromEnum(EntryTabSide::values);
    private static final IntFunction<EntryTabSide> BY_ID = ByIdMap.continuous(Enum::ordinal, values(), ByIdMap.OutOfBoundsStrategy.WRAP);
    public static final StreamCodec<ByteBuf, EntryTabSide> STREAM_CODEC = ByteBufCodecs.idMapper(BY_ID, EntryTabSide::ordinal);
    private final String name;

    EntryTabSide(String name) {
        this.name = name;
    }

    public static EntryTabSide byName(String pName) {
        return CODEC.byName(pName);
    }

    @Override
    public @NotNull String getSerializedName() {
        return this.name;
    }
}
