/*
 * SPDX-FileCopyrightText: 2026 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.research.action;

import com.klikli_dev.modonomicon.registry.ResearchEffectTypeRegistry;
import com.klikli_dev.modonomicon.research.data.ResearchCost;
import com.klikli_dev.modonomicon.research.effect.ResearchEffect;
import com.klikli_dev.modonomicon.research.effect.ResearchEffectContext;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;

import java.util.List;

/**
 * An action a player performs on a requirement fact to satisfy it, e.g. paying to learn the fact or paying for a research note.
 * Attached to a fact via the fact's display data. Use {@link ResearchActions#perform} to run actions, it validates everything on the server first.
 * <p>
 * An action is a cost, the effects it applies, and two flags:
 * <ul>
 *     <li>{@code cost}: what the player pays, see {@link ResearchCost}. Checked before the effects run and paid after they succeeded.</li>
 *     <li>{@code effects}: the effects applied when the action is performed, in order, e.g. giving an item.
 *     The action is available only if all of its effects are available.</li>
 *     <li>{@code grantsFact}: performing the action grants the fact it is attached to, i.e. "pay to learn".</li>
 *     <li>{@code repeatable}: the action stays available after its fact is granted and after its stage is completed, so it can be
 *     performed again. Every performance still pays the cost. Non-repeatable actions are only offered while their stage is active.</li>
 * </ul>
 *
 * @param cost the cost the player pays for every performance
 * @param effects the effects applied on performance, in order
 * @param grantsFact whether performing the action grants the fact it is attached to
 * @param repeatable whether the action can be performed again after its fact is granted and its stage is completed
 */
public record ResearchAction(ResearchCost cost, List<ResearchEffect> effects, boolean grantsFact, boolean repeatable) {

    public static final Codec<ResearchAction> CODEC = RecordCodecBuilder.<ResearchAction>create(instance -> instance.group(
            ResearchCost.CODEC.optionalFieldOf("cost", ResearchCost.EMPTY).forGetter(ResearchAction::cost),
            ResearchEffectTypeRegistry.CODEC.listOf().optionalFieldOf("effects", List.of()).forGetter(ResearchAction::effects),
            Codec.BOOL.optionalFieldOf("grants_fact", false).forGetter(ResearchAction::grantsFact),
            Codec.BOOL.optionalFieldOf("repeatable", false).forGetter(ResearchAction::repeatable)
    ).apply(instance, ResearchAction::new)).validate(ResearchAction::validate);

    public static final StreamCodec<RegistryFriendlyByteBuf, ResearchAction> STREAM_CODEC =
            StreamCodec.composite(
                    ResearchCost.STREAM_CODEC,
                    ResearchAction::cost,
                    ResearchEffectTypeRegistry.STREAM_CODEC.apply(ByteBufCodecs.list()),
                    ResearchAction::effects,
                    ByteBufCodecs.BOOL,
                    ResearchAction::grantsFact,
                    ByteBufCodecs.BOOL,
                    ResearchAction::repeatable,
                    ResearchAction::new
            );

    public ResearchAction {
        effects = List.copyOf(effects);
    }

    /**
     * An action must either grant its fact or be repeatable. A one-time action that grants nothing would be offered again
     * without anything recording that it was done, so such actions are rejected.
     *
     * @return the error message, or null if the action is valid
     */
    public static String invalidReason(boolean grantsFact, boolean repeatable) {
        return !grantsFact && !repeatable
                ? "A research action must grant its fact or be repeatable, a one-time action without grants_fact is not recorded"
                : null;
    }

    private static DataResult<ResearchAction> validate(ResearchAction action) {
        var reason = invalidReason(action.grantsFact(), action.repeatable());
        return reason == null ? DataResult.success(action) : DataResult.error(() -> reason);
    }

    /**
     * True if every effect of the action is available. Costs are checked separately.
     */
    public boolean isAvailable(ServerPlayer player, ResearchEffectContext context) {
        return this.effects.stream().allMatch(effect -> effect.isAvailable(player, context));
    }
}
