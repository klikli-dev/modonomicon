// SPDX-FileCopyrightText: 2026 klikli-dev
//
// SPDX-License-Identifier: MIT

package com.klikli_dev.modonomicon.datagen.book.demo.progression;

import com.klikli_dev.modonomicon.api.datagen.CategoryProvider;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel;
import com.klikli_dev.modonomicon.client.gui.book.theme.GuiSprite;
import net.minecraft.world.item.Items;

public class ChildProgressionEntry extends EntryProvider {
    public static final String ID = "child_progression";

    public ChildProgressionEntry(CategoryProvider parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("intro", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Unlocked by completion");
        this.pageText("""
                This entry is a child of First Page Progression. Its parent has a progression, so this entry unlocks when the parent is complete, not when it is viewed.
                Complete all stages of First Page Progression to unlock this entry.
                """);
    }

    @Override
    protected String entryName() {
        return "Child Progression";
    }

    @Override
    protected String entryDescription() {
        return "Unlocked once First Page Progression is complete.";
    }

    @Override
    protected GuiSprite entryBackground() {
        return EntryBackground.DEFAULT;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(Items.EMERALD);
    }

    @Override
    protected String entryId() {
        return ID;
    }
}
