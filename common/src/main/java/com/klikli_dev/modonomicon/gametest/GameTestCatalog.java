/*
 * SPDX-FileCopyrightText: 2026 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.gametest;

import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.Identifier;

import java.util.List;
import java.util.function.Consumer;

/**
 * All gametests of Modonomicon, grouped by environment. Loaders register these with their own test function registry
 * and environment mechanism, see the loader specific GameTestRegistry classes.
 * <p>
 * Test functions are registered under the mod namespace, the research data and the empty template live in
 * {@link #DATA_NAMESPACE}, which is excluded from the mod jar.
 */
public final class GameTestCatalog {

    public static final String DATA_NAMESPACE = "modonomicon_gametest";

    public static final Identifier STRUCTURE = Identifier.fromNamespaceAndPath(DATA_NAMESPACE, "empty");

    public record GameTest(String name, Consumer<GameTestHelper> function) {
    }

    public record Group(String name, List<GameTest> tests) {
    }

    public static final List<Group> GROUPS = List.of(
            new Group("research_state", List.of(
                    new GameTest("stage_progress_not_started", ResearchStateGameTests::stageProgressNotStarted),
                    new GameTest("stage_progress_advances", ResearchStateGameTests::stageProgressAdvances),
                    new GameTest("stage_index_is_one_based", ResearchStateGameTests::stageIndexIsOneBased),
                    new GameTest("legacy_rewarded_stages_are_read", ResearchStateGameTests::legacyRewardedStagesAreRead)
            )),
            new Group("research_cost", List.of(
                    new GameTest("overlapping_items_share_stacks", ResearchCostGameTests::overlappingItemsShareStacks),
                    new GameTest("insufficient_overlapping_items_fail", ResearchCostGameTests::insufficientOverlappingItemsFail),
                    new GameTest("durability_planned_before_items", ResearchCostGameTests::durabilityPlannedBeforeItems),
                    new GameTest("levels_must_be_covered", ResearchCostGameTests::levelsMustBeCovered),
                    new GameTest("creative_players_pay_nothing", ResearchCostGameTests::creativePlayersPayNothing),
                    new GameTest("stale_plan_is_detected", ResearchCostGameTests::stalePlanIsDetected)
            )),
            new Group("research_action", List.of(
                    new GameTest("action_grants_fact_and_charges", ResearchActionGameTests::actionGrantsFactAndCharges),
                    new GameTest("unchanged_action_is_unavailable_and_free", ResearchActionGameTests::unchangedActionIsUnavailableAndFree),
                    new GameTest("unaffordable_action_is_not_performed", ResearchActionGameTests::unaffordableActionIsNotPerformed),
                    new GameTest("fact_not_required_by_stage_is_invalid", ResearchActionGameTests::factNotRequiredByStageIsInvalid),
                    new GameTest("granted_fact_cannot_be_granted_again", ResearchActionGameTests::grantedFactCannotBeGrantedAgain)
            )),
            new Group("research_hook", List.of(
                    new GameTest("item_used_with_too_few_items_is_not_handled", ResearchHookGameTests::itemUsedWithTooFewItemsIsNotHandled),
                    new GameTest("item_used_with_enough_items_is_handled", ResearchHookGameTests::itemUsedWithEnoughItemsIsHandled)
            )),
            new Group("research_condition", List.of(
                    new GameTest("multi_stage_node_needs_all_stages", ResearchConditionGameTests::multiStageNodeNeedsAllStages)
            )),
            new Group("research_demo", List.of(
                    new GameTest("taking_note_grants_fact_and_stays_repeatable", ResearchDemoGameTests::takingNoteGrantsFactAndStaysRepeatable),
                    new GameTest("transcription_needs_make_and_study", ResearchDemoGameTests::transcriptionNeedsMakeAndStudy)
            ))
    );

    private GameTestCatalog() {
    }
}
