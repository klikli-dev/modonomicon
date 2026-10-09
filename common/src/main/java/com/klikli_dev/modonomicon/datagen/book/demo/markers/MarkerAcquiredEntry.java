/*
 * SPDX-FileCopyrightText: 2026 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.datagen.book.demo.markers;

import com.klikli_dev.modonomicon.api.datagen.CategoryProvider;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel;
import com.klikli_dev.modonomicon.client.gui.book.theme.GuiSprite;
import net.minecraft.world.item.Items;

public class MarkerAcquiredEntry extends EntryProvider {
    public static final String ID = "marker_acquired";

    public MarkerAcquiredEntry(CategoryProvider parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("info", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Acquired Marker");
        this.pageText("This entry shows a marker until cobblestone has been acquired. The backing research is generated automatically via the ConditionHelper markerUntilAcquired variant.");
    }

    @Override
    protected String entryName() {
        return "Acquired Marker";
    }

    @Override
    protected String entryDescription() {
        return "Marker cleared by acquiring cobblestone.";
    }

    @Override
    protected GuiSprite entryBackground() {
        return EntryBackground.CONDITION;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(Items.COBBLESTONE);
    }

    @Override
    protected String entryId() {
        return ID;
    }
}
