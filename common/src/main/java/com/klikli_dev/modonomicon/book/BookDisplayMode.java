// SPDX-FileCopyrightText: 2024 klikli-dev
//
// SPDX-License-Identifier: MIT


package com.klikli_dev.modonomicon.book;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.NotNull;

import java.util.function.IntFunction;

public enum BookDisplayMode implements StringRepresentable {
    NODE("node"),
    INDEX("index");

    public static final StringRepresentable.EnumCodec<BookDisplayMode> CODEC = StringRepresentable.fromEnum(BookDisplayMode::values);
    private static final IntFunction<BookDisplayMode> BY_ID = ByIdMap.continuous(Enum::ordinal, values(), ByIdMap.OutOfBoundsStrategy.WRAP);
    public static final StreamCodec<ByteBuf, BookDisplayMode> STREAM_CODEC = ByteBufCodecs.idMapper(BY_ID, BookDisplayMode::ordinal);
    private final String name;

    BookDisplayMode(String name) {
        this.name = name;
    }

    public static BookDisplayMode byName(String pName) {
        return CODEC.byName(pName);
    }

    public static BookDisplayMode byId(int pId) {
        return BY_ID.apply(pId);
    }

    @Override
    public @NotNull String getSerializedName() {
        return this.name;
    }
}
