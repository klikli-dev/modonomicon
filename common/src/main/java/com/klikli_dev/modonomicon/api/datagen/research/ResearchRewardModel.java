/*
 * SPDX-FileCopyrightText: 2026 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.api.datagen.research;

import com.klikli_dev.modonomicon.research.reward.AddValueReward;
import com.klikli_dev.modonomicon.research.reward.ExperienceReward;
import com.klikli_dev.modonomicon.research.reward.FunctionReward;
import com.klikli_dev.modonomicon.research.reward.GrantFactReward;
import com.klikli_dev.modonomicon.research.reward.ItemReward;
import com.klikli_dev.modonomicon.research.reward.ResearchReward;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.level.ItemLike;

/**
 * Authoring-time builder for a {@link ResearchReward} of a stage, see {@link ResearchStageSpec#reward}.
 * Use {@link #of(ResearchReward)} to author rewards of third-party reward types.
 */
public final class ResearchRewardModel {
    private final ResearchReward reward;

    private ResearchRewardModel(ResearchReward reward) {
        this.reward = reward;
    }

    /**
     * Wraps any {@link ResearchReward}, e.g. one of a third-party reward type.
     */
    public static ResearchRewardModel of(ResearchReward reward) {
        return new ResearchRewardModel(reward);
    }

    /**
     * Gives one of the item.
     */
    public static ResearchRewardModel item(ItemLike item) {
        return item(new ItemStackTemplate(item.asItem()));
    }

    /**
     * Gives the stack, including its components.
     */
    public static ResearchRewardModel item(ItemStackTemplate stack) {
        return new ResearchRewardModel(new ItemReward(stack));
    }

    /**
     * Gives experience points.
     */
    public static ResearchRewardModel experience(int points) {
        return new ResearchRewardModel(new ExperienceReward(points, false));
    }

    /**
     * Gives experience levels.
     */
    public static ResearchRewardModel levels(int levels) {
        return new ResearchRewardModel(new ExperienceReward(levels, true));
    }

    /**
     * Grants a research fact.
     */
    public static ResearchRewardModel grantFact(ResearchFactRef fact) {
        return new ResearchRewardModel(new GrantFactReward(fact.id()));
    }

    /**
     * Adds to a research value.
     */
    public static ResearchRewardModel addValue(ResearchValueRef value, int amount) {
        return new ResearchRewardModel(new AddValueReward(value.id(), amount));
    }

    /**
     * Runs the function as the player, e.g. {@code modonomicon:demo/synthesis_reward} for {@code data/modonomicon/function/demo/synthesis_reward.mcfunction}.
     */
    public static ResearchRewardModel function(Identifier function) {
        return new ResearchRewardModel(new FunctionReward(function));
    }

    public ResearchReward toResearchReward() {
        return this.reward;
    }
}
