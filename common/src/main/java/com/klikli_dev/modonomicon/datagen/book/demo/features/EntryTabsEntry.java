// SPDX-FileCopyrightText: 2026 klikli-dev
//
// SPDX-License-Identifier: MIT

package com.klikli_dev.modonomicon.datagen.book.demo.features;

import com.klikli_dev.modonomicon.api.datagen.CategoryProvider;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookEntryModel;
import com.klikli_dev.modonomicon.api.datagen.book.BookEntryTabModel;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.*;
import com.klikli_dev.modonomicon.book.entries.EntryTabSide;
import com.klikli_dev.modonomicon.client.gui.book.theme.GuiSprite;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;

public class EntryTabsEntry extends EntryProvider {
    public static final String ID = "entry_tabs";

    public EntryTabsEntry(CategoryProvider parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("intro", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText())
        );
        this.pageTitle("Entry Tabs");
        this.pageText("""
                Tabs are bookmarks on the edges of the book. Clicking one opens its page as a single page over the book, e.g. a recipe.
                Click the tab again, the close button, or click outside the page to close it.
                """);

        this.page("tabs", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText())
        );
        this.pageTitle("Pinned Tabs");
        this.pageText("""
                Tabs declared after a page are pinned to it: the apple tab below is only visible while this page is open.
                """);

        this.tab("anchored", () -> BookEntryTabModel.create()
                .withPage(BookSpotlightPageModel.create().withItem(Ingredient.of(Items.APPLE)).withAssociatedItem(Items.APPLE))
                .withIcon(Items.APPLE)
                .withTitle(this.tabTitleKey())
        );
        this.tabTitle("Anchored spotlight");

        this.page("second_spread", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText())
        );
        this.pageTitle("Second Spread");
        this.pageText("""
                This page is on the second spread. The anchored tab of the Pinned Tabs page is hidden here, because that page is not open.
                """);

        this.page("recipes", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText())
        );
        this.pageTitle("Recipes");
        this.pageText("""
                The two recipe tabs on the right edge are pinned to this page: they only show up while this page is open.
                """);

        this.tab("crafting", () -> BookEntryTabModel.create()
                .withPage(BookCraftingRecipePageModel.create().withRecipeId1("minecraft:iron_axe"))
                .withTitle(this.tabTitleKey())
        );
        this.tabTitle("Iron axe recipe");

        this.tab("smelting", () -> BookEntryTabModel.create()
                .withPage(BookSmeltingRecipePageModel.create().withRecipeId1("minecraft:charcoal"))
                .withTitle(this.tabTitleKey())
        );
        this.tabTitle("Charcoal smelting");

        this.entryTab("field_notes", () -> BookEntryTabModel.create()
                .withSide(EntryTabSide.LEFT)
                .withIcon(Items.WRITABLE_BOOK)
                .withPage(BookTextPageModel.create()
                        .withTitle(this.tabTitleKey())
                        .withText(this.tabTextKey()))
                .withTitle(this.tabTitleKey())
        );
        this.tabTitle("Field notes");
        this.tabText("""
                Tabs on the left edge work the same way as tabs on the right edge. This tab is entry-wide, so it is visible on every page of this entry.
                """);
    }

    @Override
    protected String entryName() {
        return "Entry Tabs";
    }

    @Override
    protected String entryDescription() {
        return "An entry showcasing entry tabs on both edges of the book, pinned to pages or visible for the whole entry.";
    }

    @Override
    protected GuiSprite entryBackground() {
        return EntryBackground.DEFAULT;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(Items.WRITABLE_BOOK);
    }

    @Override
    protected String entryId() {
        return ID;
    }
}
