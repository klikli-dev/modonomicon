// SPDX-FileCopyrightText: 2024 klikli-dev
//
// SPDX-License-Identifier: MIT

package com.klikli_dev.modonomicon.datagen.book.demo.features;

import com.klikli_dev.modonomicon.api.datagen.CategoryProvider;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookEntryModel;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel;
import com.mojang.datafixers.util.Pair;
import net.minecraft.world.item.Items;

public class CommandEntry extends EntryProvider {
    public static final String ID = "command";

    public CommandEntry(CategoryProvider parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("intro", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText())
        );
        this.pageTitle("Entry Read Commands");
        this.pageText("""
                This entry just ran a command when you first read it. Look into your chat!
                """);

        this.page("command_link", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText())
        );
        this.pageTitle("Command Link");
        //See DemoBookProvider#additionalSetup for how the command is added to the book
        this.pageText(
                """
                        {0}
                        """,
                this.commandLink("Click me to run the command!", "test_command2")
        );
    }

    @Override
    protected BookEntryModel additionalSetup(BookEntryModel entry) {
        //See DemoBookProvider#additionalSetup for how the command is added to the book
        return entry.withCommandToRunOnFirstRead(this.modLoc("test_command"));
    }

    @Override
    protected String entryName() {
        return "Entry Read Commands";
    }

    @Override
    protected String entryDescription() {
        return "This entry runs a command when you first read it.";
    }

    @Override
    protected Pair<Integer, Integer> entryBackground() {
        return EntryBackground.DEFAULT;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(Items.OAK_SIGN);
    }

    @Override
    protected String entryId() {
        return ID;
    }
}
