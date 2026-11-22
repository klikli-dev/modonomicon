/*
 * SPDX-FileCopyrightText: 2026 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.gametest;

import com.klikli_dev.modonomicon.Modonomicon;
import com.klikli_dev.modonomicon.datagen.research.DemoResearch;
import com.klikli_dev.modonomicon.research.ResearchServices;
import com.klikli_dev.modonomicon.research.action.ResearchActions;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import static com.klikli_dev.modonomicon.gametest.ResearchGameTestData.freshPlayer;

/**
 * Runs the scholarship demo research of {@code DemoResearch} and {@code NotesEntry} with its generated research data.
 */
public class ResearchDemoGameTests {

    private static final Identifier NOTES_NODE = Modonomicon.loc("generated/demo/nodes/entry_research/scholarship_notes");
    private static final Identifier ENTRY_VIEWED_FACT = Modonomicon.loc("generated/demo/facts/entry_viewed_once/scholarship_notes");
    private static final Identifier OBSERVATION_FACT = Modonomicon.loc("generated/demo/nodes/entry_research/scholarship_notes/stages/observation/observation");
    /**
     * The 0-based index of the next stage in progress, see {@code PlayerResearchState#currentStageIndex}: 0 observation,
     * 1 transcription, 2 synthesis.
     */
    private static final int TRANSCRIPTION_STAGE_INDEX = 1;
    private static final int SYNTHESIS_STAGE_INDEX = 2;

    /**
     * A player who has read the entry and completed the observation stage, so transcription is the active stage.
     */
    private static ServerPlayer observedPlayer(GameTestHelper helper) {
        var player = freshPlayer(helper);
        player.getInventory().add(new ItemStack(Items.PAPER, 5));
        player.getInventory().add(new ItemStack(Items.SHEARS));
        ResearchServices.hooks().applyStateChange(player, () -> ResearchServices.state().grantFact(player, ENTRY_VIEWED_FACT));
        var result = ResearchActions.perform(player, NOTES_NODE, OBSERVATION_FACT);
        helper.assertTrue(result == ResearchActions.Result.PERFORMED, "Observation should be performed, was " + result);
        return player;
    }

    /**
     * Taking a blank note grants its fact, and the take action stays performable afterwards.
     */
    public static void takingNoteGrantsFactAndStaysRepeatable(GameTestHelper helper) {
        var player = observedPlayer(helper);

        var first = ResearchActions.perform(player, NOTES_NODE, DemoResearch.NOTE_TAKEN.id());
        helper.assertTrue(first == ResearchActions.Result.PERFORMED, "First take should be performed, was " + first);
        helper.assertTrue(ResearchServices.state().getStateFor(player).hasFact(DemoResearch.NOTE_TAKEN.id()),
                "The take row should be met after taking a note");

        var second = ResearchActions.perform(player, NOTES_NODE, DemoResearch.NOTE_TAKEN.id());
        helper.assertTrue(second == ResearchActions.Result.PERFORMED, "The take action should stay repeatable, was " + second);
        helper.succeed();
    }

    /**
     * Taking a note alone does not complete transcription. Making and studying the note do, and the cost-only synthesis
     * stage then waits for its manual completion instead of completing by itself.
     */
    public static void transcriptionNeedsMakeAndStudy(GameTestHelper helper) {
        var player = observedPlayer(helper);
        ResearchActions.perform(player, NOTES_NODE, DemoResearch.NOTE_TAKEN.id());
        helper.assertTrue(ResearchServices.state().getStateFor(player).currentStageIndex(NOTES_NODE) == TRANSCRIPTION_STAGE_INDEX,
                "Taking a note alone should keep transcription active");

        ResearchServices.hooks().applyStateChange(player, () -> {
            var state = ResearchServices.state();
            boolean crafted = state.grantFact(player, DemoResearch.NOTE_CRAFTED.id());
            boolean studied = state.grantFact(player, DemoResearch.NOTE_STUDIED.id());
            return crafted || studied;
        });
        helper.assertTrue(ResearchServices.state().getStateFor(player).currentStageIndex(NOTES_NODE) == SYNTHESIS_STAGE_INDEX,
                "Making and studying the note should complete transcription");

        ResearchServices.hooks().applyStateChange(player, () -> false);
        helper.assertTrue(ResearchServices.state().getStateFor(player).currentStageIndex(NOTES_NODE) == SYNTHESIS_STAGE_INDEX,
                "The synthesis stage must not complete without its manual completion");
        helper.succeed();
    }
}
