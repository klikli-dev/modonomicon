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

public class IndexMarkerExplicitEntry extends EntryProvider {
    public static final String ID = "index_marker_explicit";

    public IndexMarkerExplicitEntry(CategoryProvider parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("info", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Explicit Index Marker");
        this.pageText("This entry shows a marker until explicitly authored research unlocks. Research nodes are shared across books, so this reuses the stick-crafting node from the demo book research.");
    }

    @Override
    protected String entryName() {
        return "Explicit Index Marker";
    }

    @Override
    protected String entryDescription() {
        return "Marker with an explicit research condition.";
    }

    @Override
    protected GuiSprite entryBackground() {
        return EntryBackground.CONDITION;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(Items.LEVER);
    }

    @Override
    protected String entryId() {
        return ID;
    }
}
