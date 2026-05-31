/*
 * SPDX-FileCopyrightText: 2026 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.datagen.research;

import com.klikli_dev.modonomicon.api.datagen.research.SingleResearchSubProvider;
import net.minecraft.resources.Identifier;

/**
 * Demo research bundle used to exercise the research datagen API and runtime.
 */
public class DemoResearch extends SingleResearchSubProvider {
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
        var conditionLevel1Viewed = this.ingress()
                .onEntryViewedOnce(this.modLoc("features/condition_level_1"))
                .declareFact("demo/condition_level_1_viewed");
        var conditionLevel2Viewed = this.ingress()
                .onEntryViewedOnce(this.modLoc("features/condition_level_2"))
                .declareFact("demo/condition_level_2_viewed");
        var formattingBasicViewed = this.ingress()
                .onEntryViewedOnce(this.modLoc("formatting/basic"))
                .declareFact("demo/formatting_basic_viewed");
        var formattingAdvancedViewed = this.ingress()
                .onEntryViewedOnce(this.modLoc("formatting/advanced"))
                .declareFact("demo/formatting_advanced_viewed");
        var featuresRecipeViewed = this.ingress()
                .onEntryViewedOnce(this.modLoc("features/recipe"))
                .declareFact("demo/features_recipe_viewed");
        var featuresSpotlightViewed = this.ingress()
                .onEntryViewedOnce(this.modLoc("features/spotlight"))
                .declareFact("demo/features_spotlight_viewed");
        var featuresComponentIconViewed = this.ingress()
                .onEntryViewedOnce(this.modLoc("features/component_icon"))
                .declareFact("demo/features_component_icon_viewed");
        var featuresEmptyViewed = this.ingress()
                .onEntryViewedOnce(this.modLoc("features/empty"))
                .declareFact("demo/features_empty_viewed");
        var featuresImageViewed = this.ingress()
                .onEntryViewedOnce(this.modLoc("features/image"))
                .declareFact("demo/features_image_viewed");
        var advancementMineStoneCompleted = this.ingress()
                .onAdvancementEarned(Identifier.parse("minecraft:story/mine_stone"))
                .declareFact("demo/advancement_mine_stone_completed");
        var advancementRideBoatWithGoatCompleted = this.ingress()
                .onAdvancementEarned(Identifier.parse("minecraft:husbandry/ride_a_boat_with_a_goat"))
                .declareFact("demo/advancement_ride_boat_with_goat_completed");

        this.node("demo/condition_level_1", conditionRootViewed);
        this.node("demo/condition_level_2", conditionLevel1Viewed);
        this.node("demo/formatting_advanced", formattingBasicViewed);
        this.node("demo/formatting_link", formattingAdvancedViewed);
        this.node("demo/features_spotlight", featuresRecipeViewed);
        this.node("demo/features_component_icon", featuresSpotlightViewed);
        this.node("demo/features_empty", featuresSpotlightViewed);
        this.node("demo/features_image", featuresEmptyViewed);
        this.node("demo/features_custom_icon", featuresImageViewed);
        this.node("demo/features_two_parents_root", conditionRootViewed);
        this.node("demo/features_two_parents_level_2", conditionLevel2Viewed);
        this.node("demo/advancement_mine_stone", advancementMineStoneCompleted);
        this.node("demo/advancement_ride_boat_with_goat", advancementRideBoatWithGoatCompleted);
    }
}
