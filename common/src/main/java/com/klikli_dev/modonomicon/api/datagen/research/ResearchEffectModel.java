/*
 * SPDX-FileCopyrightText: 2026 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.api.datagen.research;

import com.klikli_dev.modonomicon.research.effect.AddValueEffect;
import com.klikli_dev.modonomicon.research.effect.ExperienceEffect;
import com.klikli_dev.modonomicon.research.effect.FunctionEffect;
import com.klikli_dev.modonomicon.research.effect.GiveItemEffect;
import com.klikli_dev.modonomicon.research.effect.GrantFactEffect;
import com.klikli_dev.modonomicon.research.effect.ResearchEffect;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.level.ItemLike;

/**
 * Authoring-time builder for a {@link ResearchEffect}, used as a effect of a stage (see {@link ResearchStageSpec#effect})
 * or as an effect of a {@link ResearchActionModel}. Use {@link #of(ResearchEffect)} to author effects of third-party types.
 */
public final class ResearchEffectModel {
    private final ResearchEffect effect;

    private ResearchEffectModel(ResearchEffect effect) {
        this.effect = effect;
    }

    /**
     * Wraps any {@link ResearchEffect}, e.g. one of a third-party effect type.
     */
    public static ResearchEffectModel of(ResearchEffect effect) {
        return new ResearchEffectModel(effect);
    }

    /**
     * Gives one of the item.
     */
    public static ResearchEffectModel giveItem(ItemLike item) {
        return giveItem(new ItemStackTemplate(item.asItem()));
    }

    /**
     * Gives the stack, including its components.
     */
    public static ResearchEffectModel giveItem(ItemStackTemplate stack) {
        return giveItem(stack, false);
    }

    /**
     * Gives the stack. If requireAbsent is set, the effect is only available while the player does not hold an equal stack,
     * e.g. a research note that can be received once.
     */
    public static ResearchEffectModel giveItem(ItemStackTemplate stack, boolean requireAbsent) {
        return new ResearchEffectModel(new GiveItemEffect(stack, requireAbsent));
    }

    /**
     * Gives experience points.
     */
    public static ResearchEffectModel experience(int points) {
        return new ResearchEffectModel(new ExperienceEffect(points, false));
    }

    /**
     * Gives experience levels.
     */
    public static ResearchEffectModel levels(int levels) {
        return new ResearchEffectModel(new ExperienceEffect(levels, true));
    }

    /**
     * Grants a research fact.
     */
    public static ResearchEffectModel grantFact(ResearchFactRef fact) {
        return new ResearchEffectModel(new GrantFactEffect(fact.id()));
    }

    /**
     * Adds to a research value.
     */
    public static ResearchEffectModel addValue(ResearchValueRef value, int amount) {
        return new ResearchEffectModel(new AddValueEffect(value.id(), amount));
    }

    /**
     * Runs the function as the player, e.g. {@code modonomicon:demo/scholarship_synthesis} for {@code data/modonomicon/function/demo/synthesis_effect.mcfunction}.
     */
    public static ResearchEffectModel function(Identifier function) {
        return new ResearchEffectModel(new FunctionEffect(function));
    }

    public ResearchEffect toResearchEffect() {
        return this.effect;
    }
}
