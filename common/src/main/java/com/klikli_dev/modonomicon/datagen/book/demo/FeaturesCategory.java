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
    protected void generateEntries() {
        var multiblockEntry = this.add(new MultiblockEntry(this).generate());
        this.layout().entry(multiblockEntry).at(-8, -3);

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

        var pageSplittingEntry = this.add(new PageSplittingEntry(this).generate())
                .withParent(spotlightEntry);
        this.layout().entry(pageSplittingEntry).below(commandEntry, 2);

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
