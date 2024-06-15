// SPDX-FileCopyrightText: 2024 klikli-dev
//
// SPDX-License-Identifier: MIT

package com.klikli_dev.modonomicon.datagen.book.demo.features;

import com.klikli_dev.modonomicon.api.datagen.CategoryProvider;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.*;
import com.mojang.datafixers.util.Pair;
import net.minecraft.world.item.Items;

public class RecipeEntry extends EntryProvider {
    public static final String ID = "recipe";

    public RecipeEntry(CategoryProvider parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("intro", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText())
        );
        this.pageTitle("Recipe Entry");
        this.pageText("""
                Recipe pages allow to show recipes in the book.
                """);

        this.page("crafting", () -> BookCraftingRecipePageModel.create()
                .withRecipeId1("minecraft:crafting_table")
                .withRecipeId2("minecraft:oak_planks")
                .withText(this.context().pageText())
                .withTitle2("test.test.test")
        );
        this.pageText("A sample recipe page.");
        this.add("test.test.test", "Book of Binding: Afrit (Bound)"); //long title to test scaling on recipe 2

        this.page("smelting", () -> BookSmeltingRecipePageModel.create()
                .withRecipeId1("minecraft:charcoal")
                .withRecipeId2("minecraft:cooked_beef")
        );

        this.page("smoking", () -> BookSmokingRecipePageModel.create()
                .withRecipeId1("minecraft:cooked_beef_from_smoking")
                .withText(this.context().pageText())
        );
        this.pageText("A smoking recipe page with one recipe and some text.");

        this.page("blasting", () -> BookBlastingRecipePageModel.create()
                .withRecipeId2("minecraft:iron_ingot_from_blasting_iron_ore")
        );

        this.page("campfire_cooking", () -> BookCampfireCookingRecipePageModel.create()
                .withRecipeId1("minecraft:cooked_beef_from_campfire_cooking")
        );

        this.page("stonecutting", () -> BookStonecuttingRecipePageModel.create()
                .withRecipeId1("minecraft:andesite_slab_from_andesite_stonecutting")
        );

        this.page("smithing", () -> BookSmithingRecipePageModel.create()
                .withRecipeId1("minecraft:netherite_axe_smithing")
                .withTitle1(this.context().pageTitle())
                .withRecipeId2("minecraft:netherite_chestplate_smithing")
        );
        this.pageTitle("1.20+ Smithing Recipe");

        //test the missing recipe visualization
        this.page("smithing_missing", () -> BookSmithingRecipePageModel.create()
                .withRecipeId1("minecraft:netherite_axe_smithing_does_not_exist")
        );
    }

    @Override
    protected String entryName() {
        return "Recipe Entry";
    }

    @Override
    protected String entryDescription() {
        return "An entry showcasing recipe pages.";
    }

    @Override
    protected Pair<Integer, Integer> entryBackground() {
        return EntryBackground.DEFAULT;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(Items.CRAFTING_TABLE);
    }

    @Override
    protected String entryId() {
        return ID;
    }
}
