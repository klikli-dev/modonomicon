// SPDX-FileCopyrightText: 2024 klikli-dev
//
// SPDX-License-Identifier: MIT

package com.klikli_dev.modonomicon.datagen.book.addtodemo.newcat;

import com.klikli_dev.modonomicon.api.datagen.CategoryProvider;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel;
import com.mojang.datafixers.util.Pair;
import net.minecraft.world.item.Items;

public class IntroEntry extends EntryProvider {

    public static final String ENTRY_ID = "intro";

    public IntroEntry(CategoryProvider parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("intro", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("About this Work");
        this.pageText(
                """
                        The following pages will lead the novice alchemist on their journey through the noble art of the transformation of matter and mind. This humble author will share their experiences, thoughts and research notes to guide the valued reader in as safe a manner as the subject matter allows.
                        """
        );

        this.page("help", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Seeking Counsel");
        this.pageText(
                """
                        If the reader finds themselves in trouble of any kind, prompt assistance will be provided at the Council of Alchemists, known also as Kli Kli's Discord Server.
                        \\
                        \\
                        [To get help, join us at https://discord.gg/trE4SHRXvb](https://discord.gg/trE4SHRXvb)
                        """
        );
    }

    @Override
    protected String entryName() {
        return "About this Work";
    }

    @Override
    protected String entryDescription() {
        return "About using The Hermetica";
    }

    @Override
    protected Pair<Integer, Integer> entryBackground() {
        return EntryBackground.CATEGORY_START;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(Items.APPLE);
    }

    @Override
    protected String entryId() {
        return ENTRY_ID;
    }
}