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

public enum ImageScaleMode implements StringRepresentable {
    SCALE_TO_FIT("scale_to_fit"),
    ACTUAL_SIZE("actual_size");

    public static final StringRepresentable.EnumCodec<ImageScaleMode> CODEC = StringRepresentable.fromEnum(ImageScaleMode::values);
    private static final IntFunction<ImageScaleMode> BY_ID = ByIdMap.continuous(Enum::ordinal, values(), ByIdMap.OutOfBoundsStrategy.WRAP);
    public static final StreamCodec<ByteBuf, ImageScaleMode> STREAM_CODEC = ByteBufCodecs.idMapper(BY_ID, ImageScaleMode::ordinal);
    private final String name;

    ImageScaleMode(String name) {
        this.name = name;
    }

    public static ImageScaleMode byName(String pName) {
        return CODEC.byName(pName);
    }

    public static ImageScaleMode byId(int pId) {
        return BY_ID.apply(pId);
    }

    @Override
    public @NotNull String getSerializedName() {
        return this.name;
    }
}
