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
 * Governs how categories whose condition is not met are presented in the parent screens.
 */
public enum BookLockedCategoryDisplay implements StringRepresentable {
    /**
     * Locked categories are hidden entirely (no button / list entry).
     * This is the default for node mode books.
     */
    HIDDEN("hidden"),
    /**
     * Locked categories are shown with a lock indicator, but can still be opened.
     * This is the default for index mode books.
     */
    SHOW_AS_LOCKED("show_as_locked");

    public static final StringRepresentable.EnumCodec<BookLockedCategoryDisplay> CODEC = StringRepresentable.fromEnum(BookLockedCategoryDisplay::values);
    private static final IntFunction<BookLockedCategoryDisplay> BY_ID = ByIdMap.continuous(Enum::ordinal, values(), ByIdMap.OutOfBoundsStrategy.WRAP);
    public static final StreamCodec<ByteBuf, BookLockedCategoryDisplay> STREAM_CODEC = ByteBufCodecs.idMapper(BY_ID, BookLockedCategoryDisplay::ordinal);
    private final String name;

    BookLockedCategoryDisplay(String name) {
        this.name = name;
    }

    public static BookLockedCategoryDisplay byName(String pName) {
        return CODEC.byName(pName);
    }

    public static BookLockedCategoryDisplay byId(int pId) {
        return BY_ID.apply(pId);
    }

    @Override
    public @NotNull String getSerializedName() {
        return this.name;
    }
}
