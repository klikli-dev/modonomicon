/*
 * SPDX-FileCopyrightText: 2026 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.datagen.book.demo.acquiring;

import com.klikli_dev.modonomicon.api.datagen.CategoryProvider;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel;
import com.klikli_dev.modonomicon.client.gui.book.theme.GuiSprite;
import net.minecraft.world.item.Items;

public class AcquiringCobblestoneEntry extends EntryProvider {
    public static final String ID = "acquiring_cobblestone";

    public AcquiringCobblestoneEntry(CategoryProvider parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("info", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Cobblestone Acquired");
        this.pageText("You acquired cobblestone! This entry unlocked because the item_acquired trigger fired.");
    }

    @Override
    protected String entryName() {
        return "Cobblestone Acquired";
    }

    @Override
    protected String entryDescription() {
        return "Unlocked when you acquire cobblestone.";
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
