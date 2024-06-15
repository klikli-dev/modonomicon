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

public enum PageDisplayMode implements StringRepresentable {
    DOUBLE_PAGE("double_page"),
    SINGLE_PAGE("single_page");

    public static final EnumCodec<PageDisplayMode> CODEC = StringRepresentable.fromEnum(PageDisplayMode::values);
    private static final IntFunction<PageDisplayMode> BY_ID = ByIdMap.continuous(Enum::ordinal, values(), ByIdMap.OutOfBoundsStrategy.WRAP);
    public static final StreamCodec<ByteBuf, PageDisplayMode> STREAM_CODEC = ByteBufCodecs.idMapper(BY_ID, PageDisplayMode::ordinal);
    private final String name;

    PageDisplayMode(String name) {
        this.name = name;
    }

    public static PageDisplayMode byName(String pName) {
        return CODEC.byName(pName);
    }

    public static PageDisplayMode byId(int pId) {
        return BY_ID.apply(pId);
    }

    @Override
    public @NotNull String getSerializedName() {
        return this.name;
    }
}
