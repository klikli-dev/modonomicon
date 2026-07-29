/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.book.conditions;

import com.klikli_dev.modonomicon.Modonomicon;
import com.klikli_dev.modonomicon.book.conditions.context.BookConditionContext;
import com.klikli_dev.modonomicon.data.BookConditionType;
import com.klikli_dev.modonomicon.registry.BookConditionTypeRegistry;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;

import java.util.Optional;

/**
 * The default condition - equivalent to the True condition, but will be replaced by AddAutoReadConditions
 */
public class BookNoneCondition extends BookCondition {

    public static final Identifier ID = Modonomicon.loc("none");
    public static final MapCodec<BookNoneCondition> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            ComponentSerialization.CODEC.optionalFieldOf("tooltip").forGetter(condition -> Optional.ofNullable(condition.tooltip()))
    ).apply(instance, tooltip -> new BookNoneCondition(tooltip.orElse(null))));
    public static final StreamCodec<RegistryFriendlyByteBuf, BookNoneCondition> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.optional(ComponentSerialization.TRUSTED_STREAM_CODEC), condition -> Optional.ofNullable(condition.tooltip()),
            tooltip -> new BookNoneCondition(tooltip.orElse(null))
    );

    public BookNoneCondition() {
        this(null);
    }

    public BookNoneCondition(Component component) {
        super(component);
    }

    @Override
    public BookConditionType<?> type() {
        return BookConditionTypeRegistry.NONE;
    }

    @Override
    public boolean test(BookConditionContext context, Player player) {
        return true;
    }
}
