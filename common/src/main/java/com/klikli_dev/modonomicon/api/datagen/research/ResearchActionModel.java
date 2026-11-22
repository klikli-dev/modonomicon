/*
 * SPDX-FileCopyrightText: 2026 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.api.datagen.research;

import com.klikli_dev.modonomicon.research.action.ResearchAction;
import com.klikli_dev.modonomicon.research.effect.ResearchEffect;
import net.minecraft.world.item.ItemStackTemplate;

import java.util.ArrayList;
import java.util.List;

/**
 * Authoring-time builder for a {@link ResearchAction} attached to a fact via {@link ResearchDisplayModel#withAction}.
 * An action is a cost, the effects it applies, and the flags {@link #grantsFact()} and {@link #repeatable()}.
 * <pre>{@code
 * ResearchActionModel.create()
 *         .cost(ResearchCostModel.create().item(Items.PAPER))
 *         .effect(ResearchEffectModel.giveItem(Items.BOOK))
 *         .repeatable()
 * }</pre>
 */
public final class ResearchActionModel {
    private ResearchCostModel cost = ResearchCostModel.create();
    private final List<ResearchEffectModel> effects = new ArrayList<>();
    private boolean grantsFact;
    private boolean repeatable;

    private ResearchActionModel() {
    }

    public static ResearchActionModel create() {
        return new ResearchActionModel();
    }

    /**
     * Pays to learn the fact the action is attached to: the cost is paid and the fact is granted.
     */
    public static ResearchActionModel learn(ResearchCostModel cost) {
        return create().cost(cost).grantsFact();
    }

    /**
     * Pays the cost and gives the player the stack. Repeatable, and only available while the player does not hold an equal stack,
     * so the player can take it again whenever they have none, e.g. a blank research note. Use
     * {@link ResearchEffectModel#giveItem(ItemStackTemplate, boolean)} for other variants.
     */
    public static ResearchActionModel giveItem(ItemStackTemplate stack, ResearchCostModel cost) {
        return create().cost(cost).effect(ResearchEffectModel.giveItem(stack, true)).repeatable();
    }

    /**
     * Sets the cost the player pays for every performance, replacing any cost set before.
     */
    public ResearchActionModel cost(ResearchCostModel cost) {
        this.cost = cost;
        return this;
    }

    /**
     * Adds an effect applied on performance, in the order added. The action is only available if all its effects are.
     */
    public ResearchActionModel effect(ResearchEffectModel effect) {
        this.effects.add(effect);
        return this;
    }

    /**
     * Performing the action grants the fact it is attached to.
     */
    public ResearchActionModel grantsFact() {
        this.grantsFact = true;
        return this;
    }

    /**
     * The action stays available after its fact is granted and after its stage is completed, and every performance pays the cost again.
     */
    public ResearchActionModel repeatable() {
        this.repeatable = true;
        return this;
    }

    public ResearchAction toResearchAction() {
        var reason = ResearchAction.invalidReason(this.grantsFact, this.repeatable);
        if (reason != null) {
            throw new IllegalStateException(reason);
        }
        List<ResearchEffect> runtimeEffects = this.effects.stream().map(ResearchEffectModel::toResearchEffect).toList();
        return new ResearchAction(this.cost.toResearchCost(), runtimeEffects, this.grantsFact, this.repeatable);
    }
}
