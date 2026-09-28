// SPDX-FileCopyrightText: 2024 klikli-dev
//
// SPDX-License-Identifier: MIT

package com.klikli_dev.modonomicon.datagen.book.demo.indexmode;

import com.klikli_dev.modonomicon.api.datagen.CategoryProvider;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel;
import com.klikli_dev.modonomicon.client.gui.book.theme.GuiSprite;
import net.minecraft.world.level.ItemLike;

public class DemoCountingIndexEntry extends EntryProvider {
    private final String id;
    private final String name;
    private final ItemLike icon;

    public DemoCountingIndexEntry(CategoryProvider parent, String id, String name, ItemLike icon) {
        super(parent);
        this.id = id;
        this.name = name;
        this.icon = icon;
    }

    @Override
    protected void generatePages() {
        this.page("intro", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText())
        );
        this.pageTitle("Demo");
        this.pageText("""
                Demo
                """);
    }

    @Override
    protected String entryName() {
        return this.name;
    }

    @Override
    protected GuiSprite entryBackground() {
        return EntryBackground.DEFAULT;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(this.icon);
    }

    @Override
    protected String entryId() {
        return this.id;
    }
}
