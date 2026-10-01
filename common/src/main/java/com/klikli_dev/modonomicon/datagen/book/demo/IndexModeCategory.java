// SPDX-FileCopyrightText: 2024 klikli-dev
//
// SPDX-License-Identifier: MIT

package com.klikli_dev.modonomicon.datagen.book.demo;

import com.klikli_dev.modonomicon.api.datagen.CategoryProvider;
import com.klikli_dev.modonomicon.api.datagen.SingleBookSubProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookCategoryModel;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.book.BookDisplayMode;
import com.klikli_dev.modonomicon.datagen.book.demo.indexmode.Demo1IndexEntry;
import com.klikli_dev.modonomicon.datagen.book.demo.indexmode.Demo2IndexEntry;
import com.klikli_dev.modonomicon.datagen.book.demo.indexmode.DemoCountingIndexEntry;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;

public class IndexModeCategory extends CategoryProvider {
    public static final String ID = "index_mode";

    public IndexModeCategory(SingleBookSubProvider parent) {
        super(parent);
    }

    @Override
    protected void generateEntries() {
        this.add(new Demo1IndexEntry(this).generate());
        this.add(new Demo2IndexEntry(this).generate());

        ItemLike[] icons = {
                Items.DIAMOND_SWORD,
                Items.APPLE,
                Items.BOOK,
                Items.COMPASS,
                Items.CRAFTING_TABLE,
                Items.FURNACE,
                Items.OAK_LOG,
                Items.IRON_PICKAXE,
                Items.GOLDEN_APPLE,
                Items.ENDER_PEARL,
                Items.REDSTONE,
                Items.LAPIS_LAZULI,
                Items.EMERALD,
                Items.CAKE,
                Items.TNT,
                Items.AMETHYST_SHARD,
                Items.BOW,
                Items.FISHING_ROD,
                Items.SHIELD,
                Items.CLOCK,
                Items.MAP,
                Items.LANTERN,
                Items.CAMPFIRE,
                Items.JUKEBOX,
                Items.NOTE_BLOCK,
                Items.OBSIDIAN,
                Items.DIAMOND,
                Items.NETHERITE_INGOT,
                Items.BLAZE_ROD,
                Items.GHAST_TEAR,
                Items.SPYGLASS,
                Items.BRUSH,
                Items.RECOVERY_COMPASS,
                Items.ELYTRA,
                Items.TOTEM_OF_UNDYING
        };

        for (int i = 0; i < icons.length; i++) {
            int number = i + 3;
            this.add(new DemoCountingIndexEntry(this, "demo" + number, "Demo Entry " + number, icons[i]).generate());
        }
    }

    @Override
    protected BookCategoryModel additionalSetup(BookCategoryModel category) {
        //This makes this one category display in index mode.
        //If you want the whole book in index mode, do the same thing in the (single)book provider
        return category.withDisplayMode(BookDisplayMode.INDEX);
    }

    @Override
    protected String categoryName() {
        return "Index Mode Category";
    }

    @Override
    protected String categoryDescription() {
        //This is currently only shown for index-mode books
        //Links here cover all supported link types to test category description click & hover handling (see #267).
        //The features link is fully qualified on purpose: this category is shared between the demo
        //and demo_index books, and only the demo book has a features category.
        return """
                A category showcasing how Modonomicon works in index mode.\\
                [Open Demo Entry 1](entry://index_mode/demo1)\\
                [Open Features Category](category://modonomicon:demo/features)\\
                [Modonomicon on GitHub](https://github.com/klikli-dev/modonomicon)\\
                [Diamond](item://minecraft:diamond)\\
                [Get an apple](command://modonomicon:demo/test_command)""";
    }

    @Override
    protected BookIconModel categoryIcon() {
        return BookIconModel.create(Items.PAPER);
    }

    @Override
    public String categoryId() {
        return ID;
    }
}
