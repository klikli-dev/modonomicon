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
import com.klikli_dev.modonomicon.book.BookIcon;
import com.klikli_dev.modonomicon.datagen.book.demo.FeaturesCategory;
import com.klikli_dev.modonomicon.datagen.book.demo.ValuesCategory;
import com.klikli_dev.modonomicon.datagen.book.demo.features.ConditionRootEntry;
import com.klikli_dev.modonomicon.datagen.book.demo.values.CollectorAEntry;
import com.klikli_dev.modonomicon.datagen.book.demo.values.CollectorBEntry;
import com.klikli_dev.modonomicon.datagen.book.demo.values.CollectorCEntry;
import com.klikli_dev.modonomicon.research.data.ResearchToastDefinition;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Util;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ItemStackTemplate;

import java.util.List;

import static com.klikli_dev.modonomicon.datagen.research.DemoResearch.toastKey;

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
                .onEntryViewedOnce(this.modLoc(FeaturesCategory.ID + "/" + ConditionRootEntry.ID))
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
                .onEntryViewedOnce(this.modLoc(ValuesCategory.ID + "/" + CollectorAEntry.ID))
                .incrementValue("demo/collector_a_hook", collectorCount, 1);
        this.ingress()
                .onEntryViewedOnce(this.modLoc(ValuesCategory.ID + "/" + CollectorBEntry.ID))
                .incrementValue("demo/collector_b_hook", collectorCount, 1);
        this.ingress()
                .onEntryViewedOnce(this.modLoc(ValuesCategory.ID + "/" + CollectorCEntry.ID))
                .incrementValue("demo/collector_c_hook", collectorCount, 1);

        this.node(COLLECTOR_COMPLETE, List.of(), List.of(
                new ResearchNodeSpec.ValueRequirement(collectorCount, 3)
        ));

        var stickCrafted = this.ingress()
                .onItemCrafted(new ItemStackTemplate(Items.STICK))
                .declareFact("demo/stick_crafted");
        this.node(CRAFTING_STICK, stickCrafted);
        this.research().toast(CRAFTING_STICK, new ResearchToastDefinition(
                toastKey("research_unlocked"),
                List.of(),
                toastKey("stick_crafted.title"),
                new BookIcon(new ItemStackTemplate(Items.STICK))
        ));
        this.add(toastKey("stick_crafted.title"), "Stick Crafted");

        var cobbleAcquired = this.ingress()
                .onItemAcquired(new ItemStackTemplate(Items.COBBLESTONE))
                .declareFact("demo/cobblestone_acquired");
        this.node(ACQUIRE_COBBLESTONE, cobbleAcquired);
        this.research().toast(ACQUIRE_COBBLESTONE, new ResearchToastDefinition(
                toastKey("research_unlocked"),
                List.of(),
                toastKey("cobblestone_acquired.title"),
                new BookIcon(new ItemStackTemplate(Items.COBBLESTONE))
        ));
        this.add(toastKey("research_unlocked"), "Research Unlocked");
        this.add(toastKey("cobblestone_acquired.title"), "Cobblestone Acquired");

        // Stage refs for the demo multi-stage node
        var stagesDemoStage1 = this.stageRef("demo/stages_demo_stage_1");
        var stagesDemoStage2 = this.stageRef("demo/stages_demo_stage_2");
        var stagesDemoStage3 = this.stageRef("demo/stages_demo_stage_3");

        // Value incremented each time a plank is crafted; stages unlock at thresholds
        var planksCrafted = this.value("demo/stages_planks_crafted");
        this.ingress()
                .onItemCrafted(new ItemStackTemplate(Items.OAK_PLANKS))
                .incrementValue("demo/stages_planks_hook", planksCrafted, 1);

        // Multi-stage node: 3 value-based stages unlocked directly by crafting planks
        this.nodeBuilder(STAGES_DEMO)
                .withStage(ResearchStageSpec.valuesOnly(stagesDemoStage1, List.of(
                        new ResearchNodeSpec.ValueRequirement(planksCrafted, 1)
                )).toast(new ResearchToastDefinition(
                        toastKey("stage_progress"),
                        List.of(),
                        toastKey("stages_demo_stage_1.title"),
                        new BookIcon(new ItemStackTemplate(Items.OAK_PLANKS))
                )))
                .withStage(ResearchStageSpec.valuesOnly(stagesDemoStage2, List.of(
                        new ResearchNodeSpec.ValueRequirement(planksCrafted, 3)
                )))
                .withStage(ResearchStageSpec.valuesOnly(stagesDemoStage3, List.of(
                        new ResearchNodeSpec.ValueRequirement(planksCrafted, 5)
                )).toast(new ResearchToastDefinition(
                        toastKey("stage_complete"),
                        List.of(),
                        toastKey("stages_demo_stage_3.title"),
                        new BookIcon(new ItemStackTemplate(Items.DIAMOND))
                )))
                .build();
        this.add(toastKey("stage_progress"), "Stage Progress");
        this.add(toastKey("stage_complete"), "Stage Complete");
        this.add(toastKey("stages_demo_stage_1.title"), "First Planks Crafted");
        this.add(toastKey("stages_demo_stage_3.title"), "All Stages Complete");

        // Node that depends on stages_demo reaching stage 2
        this.nodeBuilder(STAGES_DEPENDENT)
                .withStageDependency(STAGES_DEMO, stagesDemoStage2)
                .build();

        // Research node and stage display names for condition tooltips
        this.researchNodeName(CONDITION_LEVEL_1, "Condition Level 1");
        this.researchNodeName(ADVANCEMENT_MINE_STONE, "Mine Stone Advancement");
        this.researchNodeName(ADVANCEMENT_RIDE_BOAT_WITH_GOAT, "Ride a Boat with a Goat");
        this.researchNodeName(COLLECTOR_COMPLETE, "Collector Complete");
        this.researchNodeName(CRAFTING_STICK, "Stick Crafting");
        this.researchNodeName(ACQUIRE_COBBLESTONE, "Cobblestone Acquisition");
        this.researchNodeName(STAGES_DEMO, "Plank Crafting Progress");
        this.researchNodeName(STAGES_DEPENDENT, "Stages Dependent");
        this.researchStageName(stagesDemoStage1, "First Planks");
        this.researchStageName(stagesDemoStage2, "More Planks");
        this.researchStageName(stagesDemoStage3, "All Planks");
    }

    static ResearchNodeRef node(String path) {
        return ResearchNodeRef.of(Modonomicon.loc(path));
    }

    static String toastKey(String path) {
        return Util.makeDescriptionId("research_toast", Modonomicon.loc(path));
    }
}
