// SPDX-FileCopyrightText: 2024 klikli-dev
//
// SPDX-License-Identifier: MIT

package com.klikli_dev.modonomicon.datagen.book.demo.features;

import com.klikli_dev.modonomicon.api.datagen.CategoryProvider;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookSpotlightPageModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel;
import com.mojang.datafixers.util.Pair;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;

public class SpotlightEntry extends EntryProvider {
    public static final String ID = "spotlight";

    public SpotlightEntry(CategoryProvider parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("intro", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText())
        );
        this.pageTitle("Spotlight Entry");
        this.pageText("""
                Spotlight pages allow to show items (actually, ingredients).
                """);

        this.page("spotlight1", () -> BookSpotlightPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText())
                .withItem(Ingredient.of(Items.APPLE))
        );
        this.pageTitle("Custom Title");
        this.pageText("A sample spotlight page with custom title.");

        this.page("spotlight2", () -> BookSpotlightPageModel.create()
                .withText(this.context().pageText())
                .withItem(Ingredient.of(Items.DIAMOND))
        );
        this.pageText("A sample spotlight page with automatic title.");
    }

    @Override
    protected String entryName() {
        return "Spotlight Entry";
    }

    @Override
    protected String entryDescription() {
        return "An entry showcasing spotlight pages.";
    }

    @Override
    protected Pair<Integer, Integer> entryBackground() {
        return EntryBackground.DEFAULT;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(Items.BEACON);
    }

    @Override
    protected String entryId() {
        return ID;
    }
}
