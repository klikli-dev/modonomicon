package com.klikli_dev.modonomicon.book;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.advancements.AdvancementType;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.NotNull;

import java.util.function.IntFunction;

public enum BookDisplayMode implements StringRepresentable {
    NODE("node"),
    INDEX("index");

    public static final Codec<AdvancementType> CODEC = StringRepresentable.fromEnum(AdvancementType::values);
    private static final IntFunction<BookDisplayMode> BY_ID = ByIdMap.continuous(Enum::ordinal, values(), ByIdMap.OutOfBoundsStrategy.WRAP);
    public static final StreamCodec<ByteBuf, BookDisplayMode> STREAM_CODEC = ByteBufCodecs.idMapper(BY_ID, BookDisplayMode::ordinal);
    public final String name;

    BookDisplayMode(String name) {
        this.name = name;
    }

    @Override
    public @NotNull String getSerializedName() {
        return this.name;
    }
}
