// SPDX-FileCopyrightText: 2024 klikli-dev
//
// SPDX-License-Identifier: MIT

package com.klikli_dev.modonomicon.datagen.book.demo.features;

import com.klikli_dev.modonomicon.api.datagen.CategoryProvider;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookEntryModel;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.*;
import com.klikli_dev.modonomicon.client.gui.book.theme.GuiSprite;

import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;

public class RecipeEntry extends EntryProvider {
    public static final String ID = "recipe";

    public RecipeEntry(CategoryProvider parent) {
        super(parent);
    }

    @Override
    protected BookEntryModel additionalSetup(BookEntryModel entry) {
        entry.withGeneratePagesAsFiles(false); //generate pages inline to demo that
        return super.additionalSetup(entry);
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
                .withRecipeId1("minecraft:iron_axe")
                .withRecipeId2("minecraft:oak_planks")
                .withText(this.context().pageText())
                .withTitle2("test.test.test")
        );
        this.pageText("A sample recipe page.");
        this.add("test.test.test", "Book of Binding: Afrit (Bound)"); //long title to test scaling on recipe 2

        this.page("smelting", () -> BookSmeltingRecipePageModel.create()
                .withRecipeId1("minecraft:charcoal")
                .withRecipeId2("minecraft:cooked_beef")
                .withText(this.context().pageText())
        );
        this.pageText("A sample smelting recipe page.");

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

        this.page("smithing_single", () -> BookSmithingRecipePageModel.create()
                .withRecipeId1("minecraft:netherite_axe_smithing")
                .withText(this.context().pageText())
        );
        this.pageText("A smithing recipe page with one recipe and some text.");

        this.page("viewer_recipe", () -> BookViewerRecipePageModel.create()
                .withRecipeId1("minecraft:netherite_axe_smithing")
                .withTitle1("Test title 1")
                .withRecipeId2("minecraft:iron_axe")
                .withTitle2("Test title 2")
                .withRecipe2($ -> $.withScale(0.75F).withBackground(false))
                .withText("Test text")
        );

        this.page(
                "viewer_recipe2",
                () -> BookViewerRecipePageModel.create()
                        .withRecipe1($ -> $
                                .withBackground(false)
                                .withInput(new ItemStackTemplate(Items.SPLASH_POTION, DataComponentPatch.builder()
                                        .set(DataComponents.POTION_CONTENTS, new PotionContents(Potions.STRONG_SLOWNESS))
                                        .build()))
                                .withOutput(new ItemStackTemplate(Items.LINGERING_POTION, DataComponentPatch.builder()
                                        .set(DataComponents.POTION_CONTENTS, new PotionContents(Potions.STRONG_SLOWNESS))
                                        .build())))
                        .withRecipeId2("minecraft:acacia_chest_boat")
        );

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
    protected GuiSprite entryBackground() {
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

