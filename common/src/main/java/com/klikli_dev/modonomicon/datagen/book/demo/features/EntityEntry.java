// SPDX-FileCopyrightText: 2024 klikli-dev
//
// SPDX-License-Identifier: MIT

package com.klikli_dev.modonomicon.datagen.book.demo.features;

import com.klikli_dev.modonomicon.api.datagen.CategoryProvider;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookEntityPageModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel;
import com.mojang.datafixers.util.Pair;
import net.minecraft.world.item.Items;

public class EntityEntry extends EntryProvider {
    public static final String ID = "entity";

    public EntityEntry(CategoryProvider parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("entity", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText())
        );
        this.pageTitle("Entity Entry");
        this.pageText("""
                This entry showcases a spotlight page with an entity.
                """);

        this.page("entity1", () -> BookEntityPageModel.create()
                .withEntityName(this.context().pageTitle())
                .withEntityId("minecraft:ender_dragon")
                .withScale(0.5f)
        );
        this.pageTitle("Custom Name");

        this.page("entity2", () -> BookEntityPageModel.create()
                .withText(this.context().pageText())
                .withEntityId("minecraft:spider")
                .withScale(1f)
        );
        this.pageText("A sample entity page with automatic title.");
    }

    @Override
    protected String entryName() {
        return "Entity Entry";
    }

    @Override
    protected String entryDescription() {
        return "An entry showcasing entity pages.";
    }

    @Override
    protected Pair<Integer, Integer> entryBackground() {
        return EntryBackground.DEFAULT;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(Items.SPIDER_EYE);
    }

    @Override
    protected String entryId() {
        return ID;
    }
}
