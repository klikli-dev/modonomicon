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
        //The entry map allows to define where entries are in relation to each other.
        //It is recommended to use a single character per entry
        // (if you think you are running out of characters .. any unicode character works.)
        //Alternatively the format used for multiblock below can be used.
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
        var multiblockEntry = this.add(new MultiblockEntry(this).generate("multiblock"));

        var conditionRootEntry = this.add(new ConditionRootEntry(this).generate('r'));
        //the advancement condition is set up in the entry provider
        var conditionAdvancementEntry = this.add(new ConditionAdvancementEntry(this).generate('a'));

        //the condition for the level 1 entry to depend on the root entry is set up here so we can access the entry. We could also do it in the entry provider and either hand over a reference, or use the ID as resource location to reference it
        var conditionLevel1Entry = this.add(new ConditionLevel1Entry(this).generate('1'))
                .withCondition(this.condition().entryRead(conditionRootEntry))
                //here we use this.parent() to get access to the parent settings
                .withParent(this.parent(conditionRootEntry).withLineReversed(true));

        var conditionLevel2Entry = this.add(new ConditionLevel2Entry(this).generate('2'))
                .withCondition(this.condition().entryRead(conditionLevel1Entry))
                //here we want a default parent so we can just hand over the entry
                .withParent(conditionLevel1Entry);

        var twoParentsEntry = this.add(new TwoParentEntry(this).generate('t'))
                .showWhenAnyParentUnlocked(true)
                .withParent(this.parent(conditionRootEntry).withLineReversed(true))
                .withParent(conditionLevel2Entry)
                .withCondition(this.condition().and(
                        this.condition().entryRead(conditionRootEntry),
                        this.condition().entryRead(conditionLevel2Entry)
                ));

        var recipeEntry = this.add(new RecipeEntry(this).generate('c'));

        var spotlightEntry = this.add(new SpotlightEntry(this).generate('s'))
                .withParent(this.parent(recipeEntry).withLineReversed(true));

        var componentIconEntry = this.add(new EntryWithComponentIcon(this).generate('x'))
                .withParent(spotlightEntry);

        var emptyEntry = this.add(new EmptyPageEntry(this).generate('e'))
                .withParent(spotlightEntry);

        var commandEntry = this.add(new CommandEntry(this).generate('f'));

        var entityEntry = this.add(new EntityEntry(this).generate('d'));

        var imageEntry = new ImageEntry(this).generate('i');
        imageEntry.withParent(this.parent(emptyEntry));

        var redirectEntry = this.add(new DemoRedirectEntry(this).generate('5'));

        var customIconEntry = this.add(new CustomIconEntry(this).generate('g'))
                .withParent(imageEntry);
    }

    @Override
    protected BookCategoryModel additionalSetup(BookCategoryModel category) {
        return category.withBackgroundParallaxLayers(
                new BookCategoryBackgroundParallaxLayer(this.modLoc("textures/gui/parallax/flow/base.png"), 0.7f, -1),
                new BookCategoryBackgroundParallaxLayer(this.modLoc("textures/gui/parallax/flow/1.png"), 1f, -1),
                new BookCategoryBackgroundParallaxLayer(this.modLoc("textures/gui/parallax/flow/2.png"), 1.4f, -1)
        ).withCategoryButtonSprite(Identifier.fromNamespaceAndPath("modonomicon", "modonomicon/themes/default/content/buttons/category/category_button_golden"), 44, 20);
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
