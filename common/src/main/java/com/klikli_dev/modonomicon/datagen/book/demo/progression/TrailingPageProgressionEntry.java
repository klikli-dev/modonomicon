// SPDX-FileCopyrightText: 2026 klikli-dev
//
// SPDX-License-Identifier: MIT

package com.klikli_dev.modonomicon.datagen.book.demo.progression;

import com.klikli_dev.modonomicon.api.datagen.CategoryProvider;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel;
import com.klikli_dev.modonomicon.book.RequirementsDisplay;
import com.klikli_dev.modonomicon.client.gui.book.theme.GuiSprite;
import com.klikli_dev.modonomicon.datagen.research.DemoResearch;
import net.minecraft.world.item.Items;

public class TrailingPageProgressionEntry extends EntryProvider {
    public static final String ID = "trailing_page_progression";

    public TrailingPageProgressionEntry(CategoryProvider parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.requirementsDisplay(RequirementsDisplay.TRAILING_PAGE);

        this.page("intro", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Trailing page requirements");
        this.pageText("""
                The requirements of this entry are listed on a trailing page after the last page of the entry. Flip past the end of the entry to see them.
                The first stage counts the dirt you pick up, the progress is shown as a bar. Stages after the first one stay hidden until they are complete.
                """);

        this.stage("dirt", s -> s
                .value(DemoResearch.PROGRESSION_DIRT_COLLECTED, 5)
                .icon(Items.DIRT)
                .title("Gathering dirt")
                .description("Pick up 5 dirt."));

        this.page("dirt_gathered", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Dirt gathered");
        this.pageText("""
                You picked up enough dirt. The trailing page showed the progress of this stage while you were collecting it.
                """);

        this.stage("torch", s -> s
                .craft(Items.TORCH)
                .title("Lighting a torch")
                .description("Craft a torch."));

        this.page("torch_lit", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Torch lit");
        this.pageText("""
                Both stages are complete. Once the last stage is done, the trailing page only lists completed requirements.
                """);
    }

    @Override
    protected String entryName() {
        return "Trailing Page Progression";
    }

    @Override
    protected String entryDescription() {
        return "Staged progression with a value requirement and the requirements on a trailing page.";
    }

    @Override
    protected GuiSprite entryBackground() {
        return EntryBackground.DEFAULT;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(Items.DIRT);
    }

    @Override
    protected String entryId() {
        return ID;
    }
}
