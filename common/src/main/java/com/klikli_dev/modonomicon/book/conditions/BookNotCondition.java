/*
 * SPDX-FileCopyrightText: 2026 klikli-dev
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

import java.util.List;
import java.util.Optional;

public class BookNotCondition extends BookCondition {

    public static final Identifier ID = Modonomicon.loc("not");
    public static final MapCodec<BookNotCondition> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            ComponentSerialization.CODEC.optionalFieldOf("tooltip").forGetter(condition -> Optional.ofNullable(condition.tooltip())),
            BookCondition.CODEC.fieldOf("child").forGetter(condition -> condition.child)
    ).apply(instance, (tooltip, child) -> new BookNotCondition(tooltip.orElse(null), child)));
    public static final StreamCodec<RegistryFriendlyByteBuf, BookNotCondition> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.optional(ComponentSerialization.TRUSTED_STREAM_CODEC), condition -> Optional.ofNullable(condition.tooltip()),
            BookCondition.STREAM_CODEC, condition -> condition.child,
            (tooltip, child) -> new BookNotCondition(tooltip.orElse(null), child)
    );

    protected BookCondition child;

    public BookNotCondition(Component tooltip, BookCondition child) {
        super(tooltip);
        if (child == null)
            throw new IllegalArgumentException("NotCondition must have a child.");
        this.child = child;
    }

    @Override
    public BookConditionType<?> type() {
        return BookConditionTypeRegistry.NOT;
    }

    @Override
    public boolean requiresMultiPassUnlockTest() {
        return this.child.requiresMultiPassUnlockTest();
    }

    public BookCondition child() {
        return this.child;
    }

    @Override
    public boolean test(BookConditionContext context, Player player) {
        return !this.child.test(context, player);
    }

    @Override
    public boolean testOnLoad() {
        // Always load and decide at runtime. Naively returning !child.testOnLoad() would
        // prevent loading for the main use case: most conditions (e.g. research) return
        // true on load to defer the decision to runtime, and inverting that to false
        // would drop the content entirely. Runtime test() still hides it correctly.
        return true;
    }

    @Override
    public List<Component> getTooltip(Player player, BookConditionContext context) {
        // Do not forward the child's tooltip: it describes the non-negated requirement
        // (e.g. "Requires X") which is misleading once the logic is inverted.
        // Authors can set an explicit tooltip instead.
        return super.getTooltip(player, context);
    }
}
