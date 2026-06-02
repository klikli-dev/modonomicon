// SPDX-FileCopyrightText: 2024 klikli-dev
//
// SPDX-License-Identifier: MIT

package com.klikli_dev.modonomicon.datagen.book.demo;

import com.klikli_dev.modonomicon.api.datagen.CategoryProvider;
import com.klikli_dev.modonomicon.api.datagen.SingleBookSubProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookCategoryModel;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.book.BookCategoryBackgroundParallaxLayer;
import com.klikli_dev.modonomicon.datagen.book.demo.features.*;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Items;

public class FeaturesCategory extends CategoryProvider {
    public static final String ID = "features";

    public FeaturesCategory(SingleBookSubProvider parent) {
        super(parent);
    }

    @Override
    protected String[] generateEntryMap() {
        // Deprecated in favour of the layout() system below.
        // This legacy string-grid remains here only as a reference for existing datagen setups.
        return new String[]{
                "___           ____5_____a_______",
                "__(multiblock)______t_______d___",
                "___           _______r__________",
                "__c           __________________",
                "___           _______2____1__i__",
                "__s           _____e____________",
                "___           _______________g__",
                "__x           _____f____________"
        };
    }

    @Override
    protected void generateEntries() {
        var multiblockEntry = this.add(new MultiblockEntry(this).generate());
        this.layout().entry(multiblockEntry).at(-8, -3);

        var conditionRootEntry = this.add(new ConditionRootEntry(this).generate());
        this.layout().entry(conditionRootEntry).at(0, -2);

        //the advancement condition is set up in the entry provider
        var conditionAdvancementEntry = this.add(new ConditionAdvancementEntry(this).generate());
        this.layout().entry(conditionAdvancementEntry).rightOf(conditionRootEntry, 3).above(2);

        //the condition for the level 1 entry to depend on the root entry is set up here so we can access the entry. We could also do it in the entry provider and either hand over a reference, or use the ID as resource location to reference it
        var conditionLevel1Entry = this.add(new ConditionLevel1Entry(this).generate())
                //here we use this.parent() to get access to the parent settings
                .withParent(this.parent(conditionRootEntry).withLineReversed(true));
        this.layout().entry(conditionLevel1Entry).rightOf(conditionRootEntry, 5).below(2);

        var conditionLevel2Entry = this.add(new ConditionLevel2Entry(this).generate())
                //here we want a default parent so we can just hand over the entry
                .withParent(conditionLevel1Entry);
        this.layout().entry(conditionLevel2Entry).below(conditionRootEntry, 2);

        var twoParentsEntry = this.add(new TwoParentEntry(this).generate())
                .showWhenAnyParentUnlocked(true)
                .withParent(this.parent(conditionRootEntry).withLineReversed(true))
                .withParent(conditionLevel2Entry);
        this.layout().entry(twoParentsEntry).above(conditionRootEntry, 1).leftOf(1);

        var recipeEntry = this.add(new RecipeEntry(this).generate());
        this.layout().entry(recipeEntry).at(-8, -1);

        var spotlightEntry = this.add(new SpotlightEntry(this).generate())
                .withParent(this.parent(recipeEntry).withLineReversed(true));
        this.layout().entry(spotlightEntry).below(recipeEntry, 2);

        var componentIconEntry = this.add(new EntryWithComponentIcon(this).generate())
                .withParent(spotlightEntry);
        this.layout().entry(componentIconEntry).below(spotlightEntry, 2);

        var emptyEntry = this.add(new EmptyPageEntry(this).generate())
                .withParent(spotlightEntry);
        this.layout().entry(emptyEntry).rightOf(spotlightEntry, 6);

        var commandEntry = this.add(new CommandEntry(this).generate());
        this.layout().entry(commandEntry).below(emptyEntry, 2);

        var entityEntry = this.add(new EntityEntry(this).generate());
        this.layout().entry(entityEntry).at(7, -3);

        var imageEntry = new ImageEntry(this).generate();
        imageEntry.withParent(this.parent(emptyEntry));
        this.layout().entry(imageEntry).at(8, 0);

        var redirectEntry = this.add(new DemoRedirectEntry(this).generate());
        this.layout().entry(redirectEntry).at(-3, -4);

        var customIconEntry = this.add(new CustomIconEntry(this).generate())
                .withParent(imageEntry);
        this.layout().entry(customIconEntry).below(imageEntry, 2);
    }

    @Override
    protected BookCategoryModel additionalSetup(BookCategoryModel category) {
        return category.withBackgroundParallaxLayers(
                new BookCategoryBackgroundParallaxLayer(this.modLoc("textures/gui/parallax/flow/base.png"), 0.7f, -1),
                new BookCategoryBackgroundParallaxLayer(this.modLoc("textures/gui/parallax/flow/1.png"), 1f, -1),
                new BookCategoryBackgroundParallaxLayer(this.modLoc("textures/gui/parallax/flow/2.png"), 1.4f, -1)
        ).withCategoryButtonSprites(
                Identifier.fromNamespaceAndPath("modonomicon", "modonomicon/themes/default/content/buttons/category/category_button_golden"),
                Identifier.fromNamespaceAndPath("modonomicon", "modonomicon/themes/default/content/buttons/category/category_button_golden"),
                44, 20
        );
    }

    @Override
    protected String categoryName() {
        return "Features Category";
    }

    @Override
    protected BookIconModel categoryIcon() {
        return BookIconModel.create(Items.NETHER_STAR);
    }

    @Override
    public String categoryId() {
        return ID;
    }
}
