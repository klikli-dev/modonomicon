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

public class AcquiringInkSacEntry extends EntryProvider {
    public static final String ID = "acquiring_ink_sac";

    public AcquiringInkSacEntry(CategoryProvider parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("info", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Ink Gathered");
        this.pageText("You gathered an ink sac! This entry unlocked because the item_acquired trigger fired. Ink is what turns a blank note into a completed one.");
    }

    @Override
    protected String entryName() {
        return "Ink Gathered";
    }

    @Override
    protected String entryDescription() {
        return "Unlocked when you acquire an ink sac.";
    }

    @Override
    protected GuiSprite entryBackground() {
        return EntryBackground.CONDITION;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(Items.INK_SAC);
    }

    @Override
    protected String entryId() {
        return ID;
    }
}
