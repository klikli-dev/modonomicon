// SPDX-FileCopyrightText: 2024 klikli-dev
//
// SPDX-License-Identifier: MIT

package com.klikli_dev.modonomicon.datagen.book.demo.features;

import com.klikli_dev.modonomicon.api.datagen.CategoryProvider;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookMultiblockPageModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel;
import com.mojang.datafixers.util.Pair;
import net.minecraft.world.item.Items;

public class MultiblockEntry extends EntryProvider {
    public static final String ID = "multiblock";

    public MultiblockEntry(CategoryProvider parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("intro", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText())
        );
        this.pageTitle("Multiblock Entry");
        this.pageText("""
                Multiblock pages allow to preview multiblocks both in the book and in the world.
                """);

        this.page("preview", () -> BookMultiblockPageModel.create()
                .withMultiblockId(this.modLoc("blockentity"))
                .withMultiblockName("multiblocks.modonomicon.blockentity")
                .withText(this.context().pageText())
        );
        this.pageText("A sample multiblock.");
        this.add("multiblocks.modonomicon.blockentity", "Blockentity Multiblock.");

        this.page("preview2", () -> BookMultiblockPageModel.create()
                .withMultiblockId(this.modLoc("tag"))
                .withText(this.context().pageText())
        );
        this.pageText("A multiblock with tag!");

        this.page("demo_predicate", () -> BookMultiblockPageModel.create()
                .withMultiblockId(this.modLoc("demo_predicate"))
        );

        this.page("demo_fluid", () -> BookMultiblockPageModel.create()
                .withMultiblockId(this.modLoc("demo_fluid"))
        );
    }

    @Override
    protected String entryName() {
        return "Multiblock Entry";
    }

    @Override
    protected String entryDescription() {
        return "An entry showcasing a multiblock.";
    }

    @Override
    protected Pair<Integer, Integer> entryBackground() {
        return EntryBackground.DEFAULT;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(Items.FURNACE);
    }

    @Override
    protected String entryId() {
        return ID;
    }
}
