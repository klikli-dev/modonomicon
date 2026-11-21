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
import com.klikli_dev.modonomicon.datagen.book.demo.ScholarshipCategory;
import com.klikli_dev.modonomicon.datagen.book.demo.research.PlacedPageResearchEntry;
import com.klikli_dev.modonomicon.datagen.book.demo.values.SourcesEntry;
import com.klikli_dev.modonomicon.research.data.ResearchCategories;
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
 * Demo research bundle used to exercise the research datagen API and runtime. All of it is themed as the scholarship
 * storyline: notes, paper, ink and iron, sources, and the insight gained by studying.
 */
public class DemoResearch extends SingleResearchSubProvider {
    public static final ResearchNodeRef ADVANCEMENT_MINE_STONE = node("demo/advancement_mine_stone");
    public static final ResearchNodeRef ADVANCEMENT_RIDE_BOAT_WITH_GOAT = node("demo/advancement_ride_boat_with_goat");
    public static final ResearchNodeRef WRITABLE_BOOK_STAGES = node("demo/writable_books");
    public static final ResearchStageRef WRITABLE_BOOK_STAGE_1 = stage("demo/writable_books_stage_1");
    public static final ResearchStageRef WRITABLE_BOOK_STAGE_2 = stage("demo/writable_books_stage_2");
    public static final ResearchStageRef WRITABLE_BOOK_STAGE_3 = stage("demo/writable_books_stage_3");
    public static final ResearchNodeRef MARKER_EXPLICIT = node("demo/marker_explicit");
    public static final ResearchNodeRef REQUIREMENTS_STAGED = node("demo/requirements_staged");
    public static final ResearchStageRef REQUIREMENTS_STAGE_1 = stage("demo/requirements_stage_1");
    public static final ResearchStageRef REQUIREMENTS_STAGE_2 = stage("demo/requirements_stage_2");
    public static final ResearchValueRef PAPER_COLLECTED = valueRef("demo/scholarship/paper_collected");
    public static final ResearchValueRef SOURCES_CONSULTED = valueRef("demo/scholarship/sources_consulted");
    public static final ResearchValueRef WRITABLE_BOOKS_CRAFTED = valueRef("demo/writable_books_crafted");
    public static final ResearchFactRef INK_SAC_ACQUIRED = factRef("demo/scholarship/ink_sac_acquired");
    public static final ResearchFactRef IRON_INGOT_ACQUIRED = factRef("demo/requirements_iron_ingot_acquired");
    public static final ResearchFactRef ADVANCEMENT_MINE_STONE_COMPLETED = factRef("demo/advancement_mine_stone_completed");
    public static final ResearchFactRef ADVANCEMENT_RIDE_BOAT_WITH_GOAT_COMPLETED = factRef("demo/advancement_ride_boat_with_goat_completed");
    public static final Identifier DEMO_SMITHING = Modonomicon.loc("demo_smithing");
    private static final Identifier DEMO_SMITHING_ICON = Identifier.fromNamespaceAndPath(Modonomicon.MOD_ID, "modonomicon/requirement_categories/demo_smithing");
    private static final String DEMO_SMITHING_TITLE = "modonomicon.demo.category.smithing.title";
    private static final String DEMO_SMITHING_DESCRIPTION = "modonomicon.demo.category.smithing.description";
    private static final String PAPER_COLLECTED_TITLE = "modonomicon.demo.scholarship.paper_collected.title";
    private static final String SOURCES_CONSULTED_TITLE = "modonomicon.demo.scholarship.sources_consulted.title";
    private static final String WRITABLE_BOOKS_CRAFTED_TITLE = "modonomicon.demo.scholarship.writable_books_crafted.title";
    private static final String INK_SAC_ACQUIRED_TITLE = "modonomicon.demo.scholarship.ink_sac_acquired.title";
    private static final String RIDE_BOAT_WITH_GOAT_TITLE = "modonomicon.demo.scholarship.advancement_ride_boat_with_goat.title";
    private static final String MINE_STONE_TITLE = "modonomicon.demo.scholarship.advancement_mine_stone.title";
    /**
     * Granted by the take action of the blank note, so the row ticks once a note was taken while the action stays repeatable.
     */
    public static final ResearchFactRef SCHOLARSHIP_NOTE_TAKEN = factRef("demo/scholarship/note_taken");
    /**
     * Granted by crafting a completed note, the row for making the note from a blank one.
     */
    public static final ResearchFactRef SCHOLARSHIP_NOTE_CRAFTED = factRef("demo/scholarship/note_crafted");
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
    private static final String SCHOLARSHIP_NOTE_TAKEN_TITLE = "modonomicon.demo.scholarship.note_taken.title";
    private static final String SCHOLARSHIP_NOTE_CRAFTED_TITLE = "modonomicon.demo.scholarship.note_crafted.title";
    private static final String SCHOLARSHIP_NOTE_CRAFTED_DESCRIPTION = "modonomicon.demo.scholarship.note_crafted.description";
    private static final String SCHOLARSHIP_NOTE_STUDIED_TITLE = "modonomicon.demo.scholarship.note_studied.title";
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
        var mineStoneCompleted = this.ingress()
                .onAdvancementEarned(this.mcLoc("story/mine_stone"))
                .declareFact(ADVANCEMENT_MINE_STONE_COMPLETED.id().getPath());
        var rideBoatCompleted = this.ingress()
                .onAdvancementEarned(this.mcLoc("husbandry/ride_a_boat_with_a_goat"))
                .declareFact(ADVANCEMENT_RIDE_BOAT_WITH_GOAT_COMPLETED.id().getPath());

        this.research().display(mineStoneCompleted, ResearchDisplayModel.create().withIcon(Items.STONE).withTitle(MINE_STONE_TITLE));
        this.add(MINE_STONE_TITLE, "Earn the advancement Mine Stone");
        this.research().display(rideBoatCompleted, ResearchDisplayModel.create().withIcon(Items.OAK_BOAT).withTitle(RIDE_BOAT_WITH_GOAT_TITLE));
        this.add(RIDE_BOAT_WITH_GOAT_TITLE, "Earn the advancement Ride a Boat with a Goat");

        this.node(ADVANCEMENT_MINE_STONE, mineStoneCompleted);
        this.node(ADVANCEMENT_RIDE_BOAT_WITH_GOAT, rideBoatCompleted);

        // Collector: every view of the sources shelf counts one source consulted
        var sourcesConsulted = this.value(SOURCES_CONSULTED.id().getPath());
        this.ingress()
                .onEntryViewedOnce(this.modLoc(ScholarshipCategory.ID + "/" + SourcesEntry.ID))
                .incrementValue("demo/scholarship/source_hook", sourcesConsulted, 1);
        this.research().display(sourcesConsulted, ResearchDisplayModel.create().withIcon(Items.BOOKSHELF).withTitle(SOURCES_CONSULTED_TITLE));
        this.add(SOURCES_CONSULTED_TITLE, "Sources consulted");

        // Explicit marker: a manually authored node, cleared once the index entry has been viewed
        var indexViewed = this.ingress()
                .onEntryViewedOnce(this.modLoc(ScholarshipCategory.ID + "/" + PlacedPageResearchEntry.ID))
                .declareFact("demo/marker_index_viewed");
        this.node(MARKER_EXPLICIT, indexViewed);

        // Scholarship chain: take a blank note from an action, make the completed note by crafting, then study it by using it.
        // The take action grants its own fact, so its row is met once a note was taken and the action stays repeatable.
        this.research().fact(SCHOLARSHIP_NOTES_READ.id().getPath());
        this.research().fact(SCHOLARSHIP_NOTE_TAKEN.id().getPath());
        this.research().display(SCHOLARSHIP_NOTE_TAKEN, ResearchDisplayModel.create()
                .withIcon(Items.PAPER)
                .withTitle(SCHOLARSHIP_NOTE_TAKEN_TITLE)
                .withCategory(ResearchCategories.KNOWLEDGE)
                .withAction(ResearchActionModel.create()
                        .cost(ResearchCostModel.create().item(Items.PAPER).durability(Items.SHEARS, 1))
                        .effect(ResearchEffectModel.giveItem(BLANK_RESEARCH_NOTE, false))
                        .grantsFact()
                        .repeatable()));
        this.add(SCHOLARSHIP_NOTE_TAKEN_TITLE, "Take a blank research note");
        var noteCrafted = this.ingress()
                .onItemCrafted(RESEARCH_NOTE)
                .declareFact(SCHOLARSHIP_NOTE_CRAFTED.id().getPath());
        this.research().display(noteCrafted, ResearchDisplayModel.create()
                .withIcon(RESEARCH_NOTE)
                .withTitle(SCHOLARSHIP_NOTE_CRAFTED_TITLE)
                .withCategory(ResearchCategories.KNOWLEDGE)
                .withDescription(SCHOLARSHIP_NOTE_CRAFTED_DESCRIPTION));
        this.add(SCHOLARSHIP_NOTE_CRAFTED_TITLE, "Make a completed research note");
        this.add(SCHOLARSHIP_NOTE_CRAFTED_DESCRIPTION, "Craft a blank note with an ink sac.");
        var noteStudied = this.ingress()
                .onItemUsed(RESEARCH_NOTE)
                .consume(1)
                .declareFact(SCHOLARSHIP_NOTE_STUDIED.id().getPath());
        this.research().display(noteStudied, ResearchDisplayModel.create()
                .withIcon(RESEARCH_NOTE)
                .withTitle(SCHOLARSHIP_NOTE_STUDIED_TITLE)
                .withCategory(ResearchCategories.KNOWLEDGE));
        this.add(SCHOLARSHIP_NOTE_STUDIED_TITLE, "Study a completed research note");
        this.research().display(SCHOLARSHIP_NOTES_READ, ResearchDisplayModel.create().withIcon(Items.WRITABLE_BOOK).withTitle(SCHOLARSHIP_NOTES_READ_TITLE));
        this.add(SCHOLARSHIP_NOTES_READ_TITLE, "Notes transcribed");
        var insight = this.value(SCHOLARSHIP_INSIGHT.id().getPath());
        this.research().display(insight, ResearchDisplayModel.create().withIcon(Items.ENCHANTED_BOOK).withTitle(SCHOLARSHIP_INSIGHT_TITLE));
        this.add(SCHOLARSHIP_INSIGHT_TITLE, "Insight");

        // Value stages: the bookbinding stage research counts writable books crafted
        var writableBooksCrafted = this.value(WRITABLE_BOOKS_CRAFTED.id().getPath());
        this.ingress()
                .onItemCrafted(new ItemStackTemplate(Items.WRITABLE_BOOK))
                .incrementValue("demo/writable_books_hook", writableBooksCrafted, 1);
        this.research().display(writableBooksCrafted, ResearchDisplayModel.create().withIcon(Items.WRITABLE_BOOK).withTitle(WRITABLE_BOOKS_CRAFTED_TITLE));
        this.add(WRITABLE_BOOKS_CRAFTED_TITLE, "Writable books crafted");

        // Multi-stage node: 3 value-based stages unlocked directly by crafting writable books
        this.nodeBuilder(WRITABLE_BOOK_STAGES)
                .withStage(ResearchStageSpec.valuesOnly(WRITABLE_BOOK_STAGE_1, List.of(
                        new ResearchNodeSpec.ValueRequirement(writableBooksCrafted, 1)
                )).withToast(new ResearchToastDefinition(
                        toastKey("stage_progress"),
                        List.of(),
                        toastKey("writable_books_stage_1.title"),
                        new BookIcon(new ItemStackTemplate(Items.WRITABLE_BOOK))
                )))
                .withStage(ResearchStageSpec.valuesOnly(WRITABLE_BOOK_STAGE_2, List.of(
                        new ResearchNodeSpec.ValueRequirement(writableBooksCrafted, 3)
                )))
                .withStage(ResearchStageSpec.valuesOnly(WRITABLE_BOOK_STAGE_3, List.of(
                        new ResearchNodeSpec.ValueRequirement(writableBooksCrafted, 5)
                )).withToast(new ResearchToastDefinition(
                        toastKey("stage_complete"),
                        List.of(),
                        toastKey("writable_books_stage_3.title"),
                        new BookIcon(new ItemStackTemplate(Items.ENCHANTED_BOOK))
                )))
                .build();
        this.add(toastKey("stage_progress"), "Stage Progress");
        this.add(toastKey("stage_complete"), "Stage Complete");
        this.add(toastKey("writable_books_stage_1.title"), "First Book Bound");
        this.add(toastKey("writable_books_stage_3.title"), "Bound Volume Complete");
        this.researchNodeName(WRITABLE_BOOK_STAGES, "Bookbinding Progress");
        this.researchStageName(WRITABLE_BOOK_STAGE_1, "First book");
        this.researchStageName(WRITABLE_BOOK_STAGE_2, "Three books");
        this.researchStageName(WRITABLE_BOOK_STAGE_3, "Five books");

        // Requirement facts and values used by the research entries
        var inkSacAcquired = this.ingress()
                .onItemAcquired(new ItemStackTemplate(Items.INK_SAC))
                .declareFact(INK_SAC_ACQUIRED.id().getPath());
        this.research().display(inkSacAcquired, ResearchDisplayModel.create().withIcon(Items.INK_SAC).withTitle(INK_SAC_ACQUIRED_TITLE));
        this.add(INK_SAC_ACQUIRED_TITLE, "Gather ink");

        var ironIngotAcquired = this.ingress()
                .onItemAcquired(new ItemStackTemplate(Items.IRON_INGOT))
                .declareFact(IRON_INGOT_ACQUIRED.id().getPath());
        var paperCollected = this.value(PAPER_COLLECTED.id().getPath());
        this.ingress()
                .onItemAcquired(new ItemStackTemplate(Items.PAPER))
                .incrementValue("demo/scholarship/paper_hook", paperCollected, 1);
        this.research().display(paperCollected, ResearchDisplayModel.create().withIcon(Items.PAPER).withTitle(PAPER_COLLECTED_TITLE));
        this.add(PAPER_COLLECTED_TITLE, "Paper collected");

        // Hand-authored staged requirements: stage 1 needs ink, stage 2 needs iron and 5 paper
        this.nodeBuilder(REQUIREMENTS_STAGED)
                .withStage(ResearchStageSpec.factsOnly(REQUIREMENTS_STAGE_1, List.of(inkSacAcquired)))
                .withStage(new ResearchStageSpec(REQUIREMENTS_STAGE_2, List.of(ironIngotAcquired), List.of(
                        new ResearchNodeSpec.ValueRequirement(paperCollected, 5)
                ), Optional.empty(), Optional.empty(), ResearchStageCompletion.AUTO, List.of()))
                .build();
        //custom category: the iron ingot requirement is grouped into its own row instead of the item row
        this.research().category(ResearchCategoryModel.create(DEMO_SMITHING)
                .withIcon(DEMO_SMITHING_ICON)
                .withTitle(DEMO_SMITHING_TITLE)
                .withDescription(DEMO_SMITHING_DESCRIPTION)
                .withSortOrder(15));
        this.research().display(ironIngotAcquired, ResearchDisplayModel.create().withCategory(DEMO_SMITHING));
        this.add(DEMO_SMITHING_TITLE, "Smithing");
        this.add(DEMO_SMITHING_DESCRIPTION, "Demo category declared in DemoResearch, grouping the iron ingot of the staged requirements.");
        this.researchNodeName(REQUIREMENTS_STAGED, "Staged requirements");
        this.researchStageName(REQUIREMENTS_STAGE_1, "Ink");
        this.researchStageName(REQUIREMENTS_STAGE_2, "Iron and paper");

        // Research node names for condition tooltips
        this.researchNodeName(ADVANCEMENT_MINE_STONE, "Mine Stone Advancement");
        this.researchNodeName(ADVANCEMENT_RIDE_BOAT_WITH_GOAT, "Ride a Boat with a Goat");
        this.researchNodeName(MARKER_EXPLICIT, "Explicit Marker");
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
