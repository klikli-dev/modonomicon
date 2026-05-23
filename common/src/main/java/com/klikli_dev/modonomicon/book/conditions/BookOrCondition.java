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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class BookOrCondition extends BookCondition {

    public static final Identifier ID = Modonomicon.loc("or");
    public static final MapCodec<BookOrCondition> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            ComponentSerialization.CODEC.optionalFieldOf("tooltip").forGetter(condition -> Optional.ofNullable(condition.tooltip())),
            BookCondition.CODEC.listOf().fieldOf("children").forGetter(condition -> Arrays.asList(condition.children))
    ).apply(instance, (tooltip, children) -> new BookOrCondition(tooltip.orElse(null), children.toArray(BookCondition[]::new))));
    public static final StreamCodec<RegistryFriendlyByteBuf, BookOrCondition> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.optional(ComponentSerialization.TRUSTED_STREAM_CODEC), condition -> Optional.ofNullable(condition.tooltip()),
            BookCondition.STREAM_CODEC.apply(ByteBufCodecs.list()), condition -> Arrays.asList(condition.children),
            (tooltip, children) -> new BookOrCondition(tooltip.orElse(null), children.toArray(BookCondition[]::new))
    );

    protected BookCondition[] children;

    protected List<Component> tooltips;

    public BookOrCondition(Component component, BookCondition[] children) {
        super(component);
        if (children == null || children.length == 0)
            throw new IllegalArgumentException("OrCondition must have at least one child.");

        this.children = children;
    }

    @Override
    public BookConditionType<?> type() {
        return BookConditionTypeRegistry.OR;
    }

    @Override
    public boolean requiresMultiPassUnlockTest() {
        return Arrays.stream(this.children).anyMatch(BookCondition::requiresMultiPassUnlockTest);
    }

    public BookCondition[] children() {
        return this.children;
    }

    @Override
    public boolean test(BookConditionContext context, Player player) {
        for (var child : this.children) {
            if (child.test(context, player))
                return true;
        }
        return false;
    }

    @Override
    public boolean testOnLoad() {
        for (var child : this.children) {
            if (child.testOnLoad())
                return true;
        }
        return false;
    }

    @Override
    public List<Component> getTooltip(Player player, BookConditionContext context) {
        if (this.tooltips == null) {
            this.tooltips = new ArrayList<>();
        }

        this.tooltips.clear(); //should not cache because e.g. advancement condition tooltips can change
        if (this.tooltip != null)
            this.tooltips.add(this.tooltip);
        for (var child : this.children) {
            this.tooltips.addAll(child.getTooltip(player, context));
        }

        return this.tooltips;
    }
}
