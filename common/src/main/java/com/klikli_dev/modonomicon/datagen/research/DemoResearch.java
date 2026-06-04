/*
 * SPDX-FileCopyrightText: 2026 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.datagen.research;

import com.klikli_dev.modonomicon.Modonomicon;
import com.klikli_dev.modonomicon.api.datagen.research.ResearchNodeRef;
import com.klikli_dev.modonomicon.api.datagen.research.ResearchNodeSpec;
import com.klikli_dev.modonomicon.api.datagen.research.ResearchStageRef;
import com.klikli_dev.modonomicon.api.datagen.research.ResearchStageSpec;
import com.klikli_dev.modonomicon.api.datagen.research.ResearchValueRef;
import com.klikli_dev.modonomicon.api.datagen.research.SingleResearchSubProvider;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ItemStackTemplate;

import java.util.List;

/**
 * Demo research bundle used to exercise the research datagen API and runtime.
 */
public class DemoResearch extends SingleResearchSubProvider {
    public static final ResearchNodeRef CONDITION_LEVEL_1 = node("demo/condition_level_1");
    public static final ResearchNodeRef ADVANCEMENT_MINE_STONE = node("demo/advancement_mine_stone");
    public static final ResearchNodeRef ADVANCEMENT_RIDE_BOAT_WITH_GOAT = node("demo/advancement_ride_boat_with_goat");
    public static final ResearchNodeRef COLLECTOR_COMPLETE = node("demo/collector_complete");
    public static final ResearchNodeRef CRAFTING_STICK = node("demo/crafting_stick");
    public static final ResearchNodeRef ACQUIRE_COBBLESTONE = node("demo/acquire_cobblestone");
    public static final ResearchNodeRef STAGES_DEMO = node("demo/stages_demo");
    public static final ResearchNodeRef STAGES_DEPENDENT = node("demo/stages_dependent");

    /**
     * Creates the demo research subprovider under the {@code modonomicon:demo} bundle id.
     */
    public DemoResearch(String modId) {
        super("demo", modId);
    }

    /**
     * Authors the current demo research facts, nodes, and ingress mappings.
     */
    @Override
    protected void generateResearch() {
        var conditionRootViewed = this.ingress()
                .onEntryViewedOnce(this.modLoc("features/condition_root"))
                .declareFact("demo/condition_root_viewed");
        var advancementMineStoneCompleted = this.ingress()
                .onAdvancementEarned(this.mcLoc("story/mine_stone"))
                .declareFact("demo/advancement_mine_stone_completed");
        var advancementRideBoatWithGoatCompleted = this.ingress()
                .onAdvancementEarned(this.mcLoc("husbandry/ride_a_boat_with_a_goat"))
                .declareFact("demo/advancement_ride_boat_with_goat_completed");

        this.node(CONDITION_LEVEL_1, conditionRootViewed);
        this.node(ADVANCEMENT_MINE_STONE, advancementMineStoneCompleted);
        this.node(ADVANCEMENT_RIDE_BOAT_WITH_GOAT, advancementRideBoatWithGoatCompleted);

        var collectorCount = this.value("demo/collector_count");

        this.ingress()
                .onEntryViewedOnce(this.modLoc("values/collector_a"))
                .incrementValue("demo/collector_a_hook", collectorCount, 1);
        this.ingress()
                .onEntryViewedOnce(this.modLoc("values/collector_b"))
                .incrementValue("demo/collector_b_hook", collectorCount, 1);
        this.ingress()
                .onEntryViewedOnce(this.modLoc("values/collector_c"))
                .incrementValue("demo/collector_c_hook", collectorCount, 1);

        this.node(COLLECTOR_COMPLETE, List.of(), List.of(
                new ResearchNodeSpec.ValueRequirement(collectorCount, 3)
        ));

        var stickCrafted = this.ingress()
                .onItemCrafted(new ItemStackTemplate(Items.STICK))
                .declareFact("demo/stick_crafted");
        this.node(CRAFTING_STICK, stickCrafted);

        var cobbleAcquired = this.ingress()
                .onItemAcquired(new ItemStackTemplate(Items.COBBLESTONE))
                .declareFact("demo/cobblestone_acquired");
        this.node(ACQUIRE_COBBLESTONE, cobbleAcquired);

        // Stage refs for the demo multi-stage node
        var stagesDemoStage1 = this.stageRef("demo/stages_demo_stage_1");
        var stagesDemoStage2 = this.stageRef("demo/stages_demo_stage_2");
        var stagesDemoStage3 = this.stageRef("demo/stages_demo_stage_3");

        // Facts granted by viewing stage content entries
        var stagesFact1 = this.ingress()
                .onEntryViewedOnce(this.modLoc("stages/stage_1_content"))
                .declareFact("demo/stages_stage_1_viewed");
        var stagesFact2 = this.ingress()
                .onEntryViewedOnce(this.modLoc("stages/stage_2_content"))
                .declareFact("demo/stages_stage_2_viewed");

        // Value incremented by viewing collector entries
        var stagesCollector = this.value("demo/stages_collector");
        this.ingress()
                .onEntryViewedOnce(this.modLoc("stages/collector_entry"))
                .incrementValue("demo/stages_collector_hook", stagesCollector, 1);

        // Multi-stage node: requires conditionRootViewed to start, then 3 stages
        this.node(STAGES_DEMO, List.of(conditionRootViewed), List.of(), List.of(
                ResearchStageSpec.factsOnly(stagesDemoStage1, List.of(stagesFact1)),
                ResearchStageSpec.factsOnly(stagesDemoStage2, List.of(stagesFact2)),
                ResearchStageSpec.valuesOnly(stagesDemoStage3, List.of(
                        new ResearchNodeSpec.ValueRequirement(stagesCollector, 2)
                ))
        ), List.of());

        // Node that depends on stages_demo reaching stage 2
        this.node(STAGES_DEPENDENT, List.of(), List.of(), List.of(), List.of(
                ResearchNodeSpec.StageDependencySpec.of(STAGES_DEMO, stagesDemoStage2)
        ));
    }

    static ResearchNodeRef node(String path) {
        return ResearchNodeRef.of(Modonomicon.loc(path));
    }
}
