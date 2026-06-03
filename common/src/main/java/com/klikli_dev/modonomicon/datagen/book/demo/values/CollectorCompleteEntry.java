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

public class CollectorCompleteEntry extends EntryProvider {
    public static final String ID = "collector_complete";

    public CollectorCompleteEntry(CategoryProvider parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("info", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Collector Complete");
        this.pageText("""
                Congratulations! You have viewed all three collector entries.
                This entry is unlocked because the collector count reached 3.
                """);
    }

    @Override
    protected String entryName() {
        return "Collector Complete";
    }

    @Override
    protected String entryDescription() {
        return "Unlocked when all collector entries have been viewed.";
    }

    @Override
    protected GuiSprite entryBackground() {
        return EntryBackground.CONDITION;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(Items.GOLD_INGOT);
    }

    @Override
    protected String entryId() {
        return ID;
    }
}
