// SPDX-FileCopyrightText: 2026 klikli-dev
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

/**
 * Configures how the background texture of a category is rendered in node-based category screens.
 */
public enum BookBackgroundRenderingMode implements StringRepresentable {
    /**
     * The background texture is tiled to fill the background area.
     * This is the default and works best for seamless textures.
     * Because tiling happens in texture pixels, the visible result reacts to the gui scale.
     */
    REPEAT("repeat"),
    /**
     * The background texture is stretched to exactly fill the background area,
     * ignoring the original aspect ratio.
     */
    SCALE("scale"),
    /**
     * The background texture is uniformly scaled so that the entire background area is covered,
     * without distorting the texture. Parts of the texture may be cropped.
     */
    FIT("fit");

    public static final StringRepresentable.EnumCodec<BookBackgroundRenderingMode> CODEC = StringRepresentable.fromEnum(BookBackgroundRenderingMode::values);
    private static final IntFunction<BookBackgroundRenderingMode> BY_ID = ByIdMap.continuous(Enum::ordinal, values(), ByIdMap.OutOfBoundsStrategy.WRAP);
    public static final StreamCodec<ByteBuf, BookBackgroundRenderingMode> STREAM_CODEC = ByteBufCodecs.idMapper(BY_ID, BookBackgroundRenderingMode::ordinal);
    private final String name;

    BookBackgroundRenderingMode(String name) {
        this.name = name;
    }

    public static BookBackgroundRenderingMode byName(String pName) {
        return CODEC.byName(pName);
    }

    public static BookBackgroundRenderingMode byId(int pId) {
        return BY_ID.apply(pId);
    }

    @Override
    public @NotNull String getSerializedName() {
        return this.name;
    }
}
