// SPDX-FileCopyrightText: 2026 klikli-dev
//
// SPDX-License-Identifier: MIT

package com.klikli_dev.modonomicon.datagen.book.demo.indexmode;

import com.klikli_dev.modonomicon.api.datagen.CategoryProvider;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel;
import com.klikli_dev.modonomicon.client.gui.book.theme.GuiSprite;
import net.minecraft.world.item.Items;

public class IndexMarkerCraftedEntry extends EntryProvider {
    public static final String ID = "index_marker_crafted";

    public IndexMarkerCraftedEntry(CategoryProvider parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("info", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Crafted Index Marker");
        this.pageText("This entry shows a marker until a diamond sword has been crafted. The backing research is generated automatically via markerUntilCrafted into this book's own generated research bundle.");
    }

    @Override
    protected String entryName() {
        return "Crafted Index Marker";
    }

    @Override
    protected String entryDescription() {
        return "Marker cleared by crafting a diamond sword.";
    }

    @Override
    protected GuiSprite entryBackground() {
        return EntryBackground.CONDITION;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(Items.DIAMOND_SWORD);
    }

    @Override
    protected String entryId() {
        return ID;
    }
}
