/*
 * SPDX-FileCopyrightText: 2026 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.datagen.research;

import com.klikli_dev.modonomicon.Modonomicon;
import com.klikli_dev.modonomicon.api.datagen.research.ResearchNodeRef;
import com.klikli_dev.modonomicon.api.datagen.research.ResearchNodeSpec;
import com.klikli_dev.modonomicon.api.datagen.research.ResearchValueRef;
import com.klikli_dev.modonomicon.api.datagen.research.SingleResearchSubProvider;
import net.minecraft.resources.Identifier;

import java.util.List;

/**
 * Demo research bundle used to exercise the research datagen API and runtime.
 */
public class DemoResearch extends SingleResearchSubProvider {
    public static final ResearchNodeRef CONDITION_LEVEL_1 = node("demo/condition_level_1");
    public static final ResearchNodeRef ADVANCEMENT_MINE_STONE = node("demo/advancement_mine_stone");
    public static final ResearchNodeRef ADVANCEMENT_RIDE_BOAT_WITH_GOAT = node("demo/advancement_ride_boat_with_goat");
    public static final ResearchNodeRef COLLECTOR_COMPLETE = node("demo/collector_complete");

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
    }

    static ResearchNodeRef node(String path) {
        return ResearchNodeRef.of(Modonomicon.loc(path));
    }
}
