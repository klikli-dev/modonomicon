// SPDX-FileCopyrightText: 2024 klikli-dev
//
// SPDX-License-Identifier: MIT

package com.klikli_dev.modonomicon.datagen.book.demo.features;

import com.klikli_dev.modonomicon.api.datagen.CategoryProvider;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel;
import com.mojang.datafixers.util.Pair;

public class CustomIconEntry extends EntryProvider {
    public static final String ID = "custom_icon";

    public CustomIconEntry(CategoryProvider parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("intro", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText())
        );
        this.pageTitle("Custom Icon");
        this.pageText("""
                This entry has a custom texture with size 32x32 as icon!
                """);
    }

    @Override
    protected String entryName() {
        return "Custom Icon";
    }

    @Override
    protected String entryDescription() {
        return "This entry has a custom texture with size 32x32 as icon!";
    }

    @Override
    protected Pair<Integer, Integer> entryBackground() {
        return EntryBackground.DEFAULT;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(this.modLoc("textures/gui/big_test_icon.png"), 32, 32);
    }

    @Override
    protected String entryId() {
        return ID;
    }
}
