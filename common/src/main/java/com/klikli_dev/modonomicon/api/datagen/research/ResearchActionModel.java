/*
 * SPDX-FileCopyrightText: 2026 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.api.datagen.research;

import com.klikli_dev.modonomicon.research.action.GiveItemAction;
import com.klikli_dev.modonomicon.research.action.GrantFactAction;
import com.klikli_dev.modonomicon.research.action.ResearchAction;
import net.minecraft.world.item.ItemStackTemplate;

/**
 * Authoring-time builder for a {@link ResearchAction} attached to a fact via {@link ResearchDisplayModel#withAction}.
 * Use {@link #of(ResearchAction)} to author actions of third-party action types.
 */
public final class ResearchActionModel {
    private final ResearchAction action;

    private ResearchActionModel(ResearchAction action) {
        this.action = action;
    }

    /**
     * Wraps any {@link ResearchAction}, e.g. one of a third-party action type.
     */
    public static ResearchActionModel of(ResearchAction action) {
        return new ResearchActionModel(action);
    }

    /**
     * Pays the cost and grants the fact the action is attached to.
     */
    public static ResearchActionModel grantFact(ResearchCostModel cost) {
        return new ResearchActionModel(new GrantFactAction(cost.toResearchCost()));
    }

    /**
     * Pays the cost and gives the player the stack. Only available while the player does not hold an equal stack,
     * see {@link #requireAbsent(boolean)}.
     */
    public static ResearchActionModel giveItem(ItemStackTemplate stack, ResearchCostModel cost) {
        return new ResearchActionModel(new GiveItemAction(stack, true, cost.toResearchCost()));
    }

    /**
     * For {@link #giveItem} actions: whether the player must not hold an equal stack for the action to be available. Defaults to true.
     */
    public ResearchActionModel requireAbsent(boolean requireAbsent) {
        if (this.action instanceof GiveItemAction give) {
            return new ResearchActionModel(new GiveItemAction(give.stack(), requireAbsent, give.cost(), give.repeatable()));
        }
        return this;
    }

    /**
     * Makes the action repeatable: it stays available after its fact is granted and after its stage is completed, and every
     * performance pays the cost again. Only {@link #giveItem} actions can repeat, for other actions this returns the model unchanged.
     */
    public ResearchActionModel repeatable() {
        if (this.action instanceof GiveItemAction give) {
            return new ResearchActionModel(new GiveItemAction(give.stack(), give.requireAbsent(), give.cost(), true));
        }
        return this;
    }

    public ResearchAction toResearchAction() {
        return this.action;
    }
}
