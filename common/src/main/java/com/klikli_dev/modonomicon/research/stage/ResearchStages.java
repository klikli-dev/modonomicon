/*
 * SPDX-FileCopyrightText: 2026 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.research.stage;

import com.klikli_dev.modonomicon.research.ResearchServices;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;

public final class ResearchStages {

    private ResearchStages() {
    }

    /**
     * Completes the current stage of a node if it is a manual stage whose requirements are met and the player can afford
     * its cost. This is the single server entry point for manual stage completion, network packets must call it.
     * <p>
     * Order: validate, plan the stage's cost, complete the stage inside the state change pipeline (its rewards are granted once
     * and automatic stages that follow cascade), then pay the planned cost. If the completion throws, nothing is paid.
     * If the cost cannot be paid anymore after the completion, the completion is kept and a warning is logged, see
     * {@link ResearchCost#pay}. Child nodes that depend on the stage unlock through the usual re-evaluation.
     * Returns false without any change if validation fails.
     */
    public static boolean completeManual(ServerPlayer player, Identifier nodeId) {
        var rule = ResearchServices.state().readyManualStage(player, nodeId).orElse(null);
        if (rule == null) {
            return false;
        }
        var cost = rule.completion().cost();
        var reserved = cost.plan(player);
        if (reserved.isEmpty()) {
            return false;
        }

        ResearchServices.hooks().applyStateChange(player, () -> {
            ResearchServices.state().completeManualStage(player, rule);
            return true;
        });
        cost.pay(player, reserved.get());
        ResearchServices.state().syncFor(player);
        return true;
    }
}
