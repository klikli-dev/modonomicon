/*
 * SPDX-FileCopyrightText: 2026 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.book;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.NotNull;

import java.util.function.IntFunction;

/**
 * Governs where the research requirements of an entry progression are shown.
 */
public enum RequirementsDisplay implements StringRepresentable {
    /**
     * Requirements are shown at the bottom of the first page of the entry.
     */
    FIRST_PAGE("first_page"),
    /**
     * Requirements are shown on a virtual page after the last unlocked page of the entry.
     */
    TRAILING_PAGE("trailing_page"),
    /**
     * Requirements are shown by stage requirement pages placed in the entry.
     */
    PAGES("pages"),
    /**
     * No requirements are shown.
     */
    NONE("none");

    public static final StringRepresentable.EnumCodec<RequirementsDisplay> CODEC = StringRepresentable.fromEnum(RequirementsDisplay::values);
    private static final IntFunction<RequirementsDisplay> BY_ID = ByIdMap.continuous(Enum::ordinal, values(), ByIdMap.OutOfBoundsStrategy.WRAP);
    public static final StreamCodec<ByteBuf, RequirementsDisplay> STREAM_CODEC = ByteBufCodecs.idMapper(BY_ID, RequirementsDisplay::ordinal);
    private final String name;

    RequirementsDisplay(String name) {
        this.name = name;
    }

    public static RequirementsDisplay byName(String pName) {
        return CODEC.byName(pName);
    }

    public static RequirementsDisplay byId(int pId) {
        return BY_ID.apply(pId);
    }

    @Override
    public @NotNull String getSerializedName() {
        return this.name;
    }
}
