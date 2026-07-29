// SPDX-FileCopyrightText: 2024 klikli-dev
//
// SPDX-License-Identifier: MIT

package com.klikli_dev.modonomicon.datagen.book.demo.values;

import com.klikli_dev.modonomicon.api.datagen.CategoryProvider;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel;
import com.klikli_dev.modonomicon.client.gui.book.theme.GuiSprite;
import net.minecraft.world.item.Items;

public class CollectorAEntry extends EntryProvider {
    public static final String ID = "collector_a";

    public CollectorAEntry(CategoryProvider parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("info", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Collector A");
        this.pageText("""
                First collector entry. Viewing this increments the collector count.
                """);
    }

    @Override
    protected String entryName() {
        return "Collector A";
    }

    @Override
    protected String entryDescription() {
        return "First of three collector entries.";
    }

    @Override
    protected GuiSprite entryBackground() {
        return EntryBackground.CONDITION;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(Items.REDSTONE);
    }

    @Override
    protected String entryId() {
        return ID;
    }
}
