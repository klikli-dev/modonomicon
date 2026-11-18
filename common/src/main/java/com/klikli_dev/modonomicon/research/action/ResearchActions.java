/*
 * SPDX-FileCopyrightText: 2026 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.research.action;

import com.klikli_dev.modonomicon.research.ResearchServices;
import com.klikli_dev.modonomicon.research.data.ResearchDataManager;
import com.klikli_dev.modonomicon.research.data.ResearchDisplay;
import com.klikli_dev.modonomicon.research.data.ResearchNodeDefinition;
import com.klikli_dev.modonomicon.research.effect.ResearchEffectContext;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;

import java.util.Optional;

public final class ResearchActions {

    public enum Result {
        PERFORMED,
        /** The node, stage or fact does not allow the action, or the fact has no action. */
        INVALID,
        /** The action's own checks failed, e.g. the player already holds the item. */
        UNAVAILABLE,
        /** The player cannot pay the action's cost. */
        UNAFFORDABLE
    }

    private ResearchActions() {
    }

    /**
     * The action attached to a fact, if any. Used to describe the cost of a fact's action to the player.
     */
    public static Optional<ResearchAction> actionFor(Identifier factId) {
        var fact = ResearchDataManager.get().data().factDefinitions().get(factId);
        return fact == null ? Optional.empty() : fact.displayOrEmpty().flatMap(ResearchDisplay::action);
    }

    /**
     * The 1-based index of the first stage that requires the fact, or 0 if no stage does.
     */
    private static int stageOf(ResearchNodeDefinition node, Identifier factId) {
        for (int i = 0; i < node.stages().size(); i++) {
            if (node.stages().get(i).requiredFacts().contains(factId)) {
                return i + 1;
            }
        }
        return 0;
    }

    /**
     * Validates and performs the action attached to a requirement fact of a node.
     * This is the single server entry point for player actions, network packets must call it.
     * <p>
     * Validation: the node must be unlocked (a node is started as soon as it is unlocked), the fact must be a
     * requirement of the node's current stage, the fact must not be granted yet, the action must be available
     * and the cost must be affordable. Only then the action's effects run inside the state change pipeline (granting the fact
     * first if the action grants it), and the cost is paid once they succeeded. If an effect throws, nothing is paid.
     * <p>
     * A repeatable action replaces the current stage and not-granted checks: the node must have reached the stage of the
     * fact (a complete node has reached all stages), and the cost is paid on every performance.
     * <p>
     * Entry visibility is not checked here; the node must be unlocked, which is what makes its entries show requirements.
     */
    public static Result perform(ServerPlayer player, Identifier nodeId, Identifier factId) {
        var data = ResearchDataManager.get().data();
        var node = data.nodeDefinitions().get(nodeId);
        var fact = data.factDefinitions().get(factId);
        if (node == null || fact == null) {
            return Result.INVALID;
        }

        var state = ResearchServices.state().getStateFor(player);
        if (!state.isNodeUnlocked(nodeId)) {
            return Result.INVALID;
        }

        int stageIndex = state.getNodeStageIndex(nodeId);
        var action = fact.displayOrEmpty().flatMap(ResearchDisplay::action).orElse(null);
        if (action == null) {
            return Result.INVALID;
        }
        if (action.repeatable()) {
            //a repeatable action only needs the node to have reached the stage of the fact, a complete node has reached all stages
            int factStage = stageOf(node, factId);
            if (factStage < 1 || stageIndex < factStage) {
                return Result.INVALID;
            }
        } else {
            if (stageIndex < 1 || stageIndex > node.stages().size()) {
                return Result.INVALID;
            }
            var currentStage = node.stages().get(stageIndex - 1);
            if (!currentStage.requiredFacts().contains(factId) || state.hasFact(factId)) {
                return Result.INVALID;
            }
        }

        var context = new ResearchEffectContext(factId);
        if (!action.isAvailable(player, context)) {
            return Result.UNAVAILABLE;
        }
        if (!action.cost().canAfford(player)) {
            return Result.UNAFFORDABLE;
        }

        ResearchServices.hooks().applyStateChange(player, () -> {
            if (action.grantsFact()) {
                ResearchServices.state().grantFact(player, factId);
            }
            for (var effect : action.effects()) {
                effect.apply(player, context);
            }
            return true;
        });
        //paid last, so a throwing mutation costs nothing. Effects normally only add items, so the checked cost stays payable.
        action.cost().consume(player);
        ResearchServices.state().syncFor(player);
        return Result.PERFORMED;
    }
}
