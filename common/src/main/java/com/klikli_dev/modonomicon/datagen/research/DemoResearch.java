/*
 * SPDX-FileCopyrightText: 2026 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.datagen.research;

import com.klikli_dev.modonomicon.Modonomicon;
import com.klikli_dev.modonomicon.api.datagen.research.ResearchActionModel;
import com.klikli_dev.modonomicon.api.datagen.research.ResearchCategoryModel;
import com.klikli_dev.modonomicon.api.datagen.research.ResearchCostModel;
import com.klikli_dev.modonomicon.api.datagen.research.ResearchDisplayModel;
import com.klikli_dev.modonomicon.api.datagen.research.ResearchEffectModel;
import com.klikli_dev.modonomicon.api.datagen.research.ResearchFactRef;
import com.klikli_dev.modonomicon.api.datagen.research.ResearchNodeRef;
import com.klikli_dev.modonomicon.api.datagen.research.ResearchNodeSpec;
import com.klikli_dev.modonomicon.api.datagen.research.ResearchStageRef;
import com.klikli_dev.modonomicon.api.datagen.research.ResearchStageSpec;
import com.klikli_dev.modonomicon.api.datagen.research.ResearchValueRef;
import com.klikli_dev.modonomicon.api.datagen.research.SingleResearchSubProvider;
import com.klikli_dev.modonomicon.book.BookIcon;
import com.klikli_dev.modonomicon.datagen.book.demo.ResearchCategory;
import com.klikli_dev.modonomicon.datagen.book.demo.features.ConditionRootEntry;
import com.klikli_dev.modonomicon.datagen.book.demo.markers.MarkerIntroEntry;
import com.klikli_dev.modonomicon.datagen.book.demo.values.CollectorAEntry;
import com.klikli_dev.modonomicon.datagen.book.demo.values.CollectorBEntry;
import com.klikli_dev.modonomicon.datagen.book.demo.values.CollectorCEntry;
import com.klikli_dev.modonomicon.research.data.ResearchStageCompletion;
import com.klikli_dev.modonomicon.research.data.ResearchToastDefinition;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Util;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.component.CustomData;

import java.util.List;
import java.util.Optional;

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
    public static final ResearchNodeRef MARKER_EXPLICIT = node("demo/marker_explicit");
    public static final ResearchNodeRef REQUIREMENTS_STAGED = node("demo/requirements_staged");
    public static final ResearchStageRef REQUIREMENTS_STAGE_1 = stage("demo/requirements_stage_1");
    public static final ResearchStageRef REQUIREMENTS_STAGE_2 = stage("demo/requirements_stage_2");
    public static final ResearchValueRef PROGRESSION_DIRT_COLLECTED = valueRef("demo/progression_dirt_collected");
    private static final String REQUIREMENTS_OAK_LOG_TITLE = "modonomicon.demo.requirements.oak_log.title";
    private static final String REQUIREMENTS_COBBLESTONE_TITLE = "modonomicon.demo.requirements.cobblestone.title";
    public static final Identifier DEMO_RITUALS = Modonomicon.loc("demo_rituals");
    private static final Identifier DEMO_RITUALS_ICON = Identifier.fromNamespaceAndPath(Modonomicon.MOD_ID, "modonomicon/requirement_categories/demo_rituals");
    private static final String DEMO_RITUALS_TITLE = "modonomicon.demo.category.rituals.title";
    private static final String DEMO_RITUALS_DESCRIPTION = "modonomicon.demo.category.rituals.description";
    private static final String PROGRESSION_DIRT_TITLE = "modonomicon.demo.progression.dirt.title";
    public static final ResearchFactRef SCHOLARSHIP_NOTE_STUDIED = factRef("demo/scholarship/note_studied");
    public static final ResearchFactRef SCHOLARSHIP_NOTES_READ = factRef("demo/scholarship/notes_read");
    public static final ResearchValueRef SCHOLARSHIP_INSIGHT = valueRef("demo/insight");
    /**
     * A blank paper marked with custom data, handed out by the note action. Stacks of this marker match the blank note only.
     */
    public static final ItemStackTemplate BLANK_RESEARCH_NOTE = researchNote("Blank Research Note", "blank_research_note");
    /**
     * A completed research note, made by crafting a blank note with an ink sac. Using it in the air studies it and consumes it.
     */
    public static final ItemStackTemplate RESEARCH_NOTE = researchNote("Research Note", "research_note");
    private static final String SCHOLARSHIP_NOTE_TITLE = "modonomicon.demo.scholarship.research_note.title";
    private static final String SCHOLARSHIP_INSIGHT_TITLE = "modonomicon.demo.scholarship.insight.title";
    private static final String SCHOLARSHIP_NOTES_READ_TITLE = "modonomicon.demo.scholarship.notes_read.title";

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
                .onEntryViewedOnce(this.modLoc(ResearchCategory.ID + "/" + ConditionRootEntry.ID))
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
                .onEntryViewedOnce(this.modLoc(ResearchCategory.ID + "/" + CollectorAEntry.ID))
                .incrementValue("demo/collector_a_hook", collectorCount, 1);
        this.ingress()
                .onEntryViewedOnce(this.modLoc(ResearchCategory.ID + "/" + CollectorBEntry.ID))
                .incrementValue("demo/collector_b_hook", collectorCount, 1);
        this.ingress()
                .onEntryViewedOnce(this.modLoc(ResearchCategory.ID + "/" + CollectorCEntry.ID))
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

        // Explicit marker demo: clears once the markers intro entry has been viewed (manual pattern)
        var markerIntroViewed = this.ingress()
                .onEntryViewedOnce(this.modLoc(ResearchCategory.ID + "/" + MarkerIntroEntry.ID))
                .declareFact("demo/marker_intro_viewed");
        this.node(MARKER_EXPLICIT, markerIntroViewed);

        // Staged node for the requirements demo: stage 1 needs oak logs, stage 2 needs iron ingots and 5 cobblestone
        var requirementsOakLog = this.ingress()
                .onItemAcquired(new ItemStackTemplate(Items.OAK_LOG))
                .declareFact("demo/requirements_oak_log_acquired");
        var requirementsIronIngot = this.ingress()
                .onItemAcquired(new ItemStackTemplate(Items.IRON_INGOT))
                .declareFact("demo/requirements_iron_ingot_acquired");
        var requirementsCobblestone = this.value("demo/requirements_cobblestone_collected");
        this.ingress()
                .onItemAcquired(new ItemStackTemplate(Items.COBBLESTONE))
                .incrementValue("demo/requirements_cobblestone_hook", requirementsCobblestone, 1);

        var requirementsStage1 = REQUIREMENTS_STAGE_1;
        var requirementsStage2 = REQUIREMENTS_STAGE_2;

        this.nodeBuilder(REQUIREMENTS_STAGED)
                .withStage(ResearchStageSpec.factsOnly(requirementsStage1, List.of(requirementsOakLog)))
                .withStage(new ResearchStageSpec(requirementsStage2, List.of(requirementsIronIngot), List.of(
                        new ResearchNodeSpec.ValueRequirement(requirementsCobblestone, 5)
                ), Optional.empty(), Optional.empty(), ResearchStageCompletion.AUTO, List.of()))
                .build();
        //the oak log fact overrides the title and icon, the iron ingot fact uses the item acquired hook for its row
        this.research().display(requirementsOakLog, ResearchDisplayModel.create().withIcon(Items.OAK_LOG).withTitle(REQUIREMENTS_OAK_LOG_TITLE));
        this.add(REQUIREMENTS_OAK_LOG_TITLE, "Collect oak logs");
        //custom category: the iron ingot requirement is grouped into its own row instead of the item row
        this.research().category(ResearchCategoryModel.create(DEMO_RITUALS)
                .withIcon(DEMO_RITUALS_ICON)
                .withTitle(DEMO_RITUALS_TITLE)
                .withDescription(DEMO_RITUALS_DESCRIPTION)
                .withSortOrder(15));
        this.research().display(requirementsIronIngot, ResearchDisplayModel.create().withCategory(DEMO_RITUALS));
        this.add(DEMO_RITUALS_TITLE, "Rituals");
        this.add(DEMO_RITUALS_DESCRIPTION, "Demo category declared in DemoResearch, grouping the iron ingot of the staged requirements.");
        this.research().display(requirementsCobblestone, ResearchDisplayModel.create().withTitle(REQUIREMENTS_COBBLESTONE_TITLE));
        this.add(REQUIREMENTS_COBBLESTONE_TITLE, "Cobblestone collected");
        this.researchNodeName(REQUIREMENTS_STAGED, "Staged requirements");
        this.researchStageName(requirementsStage1, "Oak logs");
        this.researchStageName(requirementsStage2, "Iron and cobblestone");

        // Value of the progression demos: incremented by each dirt acquired, the display overrides the generic value name
        var progressionDirt = this.value("demo/progression_dirt_collected");
        this.ingress()
                .onItemAcquired(new ItemStackTemplate(Items.DIRT))
                .incrementValue("demo/progression_dirt_hook", progressionDirt, 1);
        this.research().display(progressionDirt, ResearchDisplayModel.create().withIcon(Items.DIRT).withTitle(PROGRESSION_DIRT_TITLE));
        this.add(PROGRESSION_DIRT_TITLE, "Dirt collected");

        // Scholarship chain: take a blank note from an action, process it by crafting, then submit the completed note by using it
        this.research().fact(SCHOLARSHIP_NOTES_READ.id().getPath());
        var noteStudied = this.ingress()
                .onItemUsed(RESEARCH_NOTE)
                .consume(1)
                .declareFact(SCHOLARSHIP_NOTE_STUDIED.id().getPath());
        this.research().display(noteStudied, ResearchDisplayModel.create()
                .withTitle(SCHOLARSHIP_NOTE_TITLE)
                .withAction(ResearchActionModel.create()
                        .cost(ResearchCostModel.create().item(Items.PAPER).durability(Items.SHEARS, 1))
                        .effect(ResearchEffectModel.giveItem(BLANK_RESEARCH_NOTE, false))
                        .repeatable()));
        this.add(SCHOLARSHIP_NOTE_TITLE, "Take a blank research note");
        this.research().display(SCHOLARSHIP_NOTES_READ, ResearchDisplayModel.create().withIcon(Items.WRITABLE_BOOK).withTitle(SCHOLARSHIP_NOTES_READ_TITLE));
        this.add(SCHOLARSHIP_NOTES_READ_TITLE, "Notes transcribed");
        var insight = this.value(SCHOLARSHIP_INSIGHT.id().getPath());
        this.research().display(insight, ResearchDisplayModel.create().withIcon(Items.ENCHANTED_BOOK).withTitle(SCHOLARSHIP_INSIGHT_TITLE));
        this.add(SCHOLARSHIP_INSIGHT_TITLE, "Insight");

        // Research node and stage display names for condition tooltips
        this.researchNodeName(CONDITION_LEVEL_1, "Condition Level 1");
        this.researchNodeName(ADVANCEMENT_MINE_STONE, "Mine Stone Advancement");
        this.researchNodeName(ADVANCEMENT_RIDE_BOAT_WITH_GOAT, "Ride a Boat with a Goat");
        this.researchNodeName(COLLECTOR_COMPLETE, "Collector Complete");
        this.researchNodeName(CRAFTING_STICK, "Stick Crafting");
        this.researchNodeName(ACQUIRE_COBBLESTONE, "Cobblestone Acquisition");
        this.researchNodeName(STAGES_DEMO, "Plank Crafting Progress");
        this.researchNodeName(STAGES_DEPENDENT, "Stages Dependent");
        this.researchNodeName(MARKER_EXPLICIT, "Explicit Marker");
        this.researchStageName(stagesDemoStage1, "First Planks");
        this.researchStageName(stagesDemoStage2, "More Planks");
        this.researchStageName(stagesDemoStage3, "All Planks");
    }

    static ResearchNodeRef node(String path) {
        return ResearchNodeRef.of(Modonomicon.loc(path));
    }

    static ResearchStageRef stage(String path) {
        return ResearchStageRef.of(Modonomicon.loc(path));
    }

    static ResearchValueRef valueRef(String path) {
        return ResearchValueRef.of(Modonomicon.loc(path));
    }

    static ResearchFactRef factRef(String path) {
        return ResearchFactRef.of(Modonomicon.loc(path));
    }

    static ItemStackTemplate researchNote(String name, String marker) {
        var markerData = new CompoundTag();
        markerData.putString("modonomicon_demo", marker);
        var components = DataComponentPatch.builder()
                .set(DataComponents.CUSTOM_NAME, Component.literal(name))
                .set(DataComponents.CUSTOM_DATA, CustomData.of(markerData))
                .build();
        return new ItemStackTemplate(Items.PAPER.builtInRegistryHolder(), 1, components);
    }

    static String toastKey(String path) {
        return Util.makeDescriptionId("research_toast", Modonomicon.loc(path));
    }
}
