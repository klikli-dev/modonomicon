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

public enum ImageDisplayMode implements StringRepresentable {
    DEFAULT("default"),
    WIDE("wide"),
    SMALL("small");

    public static final StringRepresentable.EnumCodec<ImageDisplayMode> CODEC = StringRepresentable.fromEnum(ImageDisplayMode::values);
    private static final IntFunction<ImageDisplayMode> BY_ID = ByIdMap.continuous(Enum::ordinal, values(), ByIdMap.OutOfBoundsStrategy.WRAP);
    public static final StreamCodec<ByteBuf, ImageDisplayMode> STREAM_CODEC = ByteBufCodecs.idMapper(BY_ID, ImageDisplayMode::ordinal);
    private final String name;

    ImageDisplayMode(String name) {
        this.name = name;
    }

    public static ImageDisplayMode byName(String pName) {
        return CODEC.byName(pName);
    }

    public static ImageDisplayMode byId(int pId) {
        return BY_ID.apply(pId);
    }

    @Override
    public @NotNull String getSerializedName() {
        return this.name;
    }
}
