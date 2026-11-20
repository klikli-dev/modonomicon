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

public class PlacedPageProgressionEntry extends EntryProvider {
    public static final String ID = "placed_page_progression";

    public PlacedPageProgressionEntry(CategoryProvider parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("intro", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Placed requirements");
        this.pageText("""
                The next page is a requirements page. It lists what the first stage needs, and it is placed here, not at the bottom of a page.
                Requirements pages are never hidden, so they keep showing your progress after a stage is complete.
                """);

        this.requirementsPage("requirements_start");

        this.stage("lighting", s -> s
                .craft(Items.TORCH)
                .title("Lighting the way")
                .description("Craft a torch."));

        this.page("lit", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Lit");
        this.pageText("""
                You crafted a torch, so this page is unlocked. The requirements page before it now shows the first stage with check marks.
                The requirements page below shows the stage that comes next.
                """);

        this.requirementsPage("requirements_next");

        this.stage("tools", s -> s
                .craft(Items.STONE_PICKAXE)
                .title("Carving tools")
                .description("Craft a stone pickaxe to carve your seals."));

        this.page("equipped", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Equipped");
        this.pageText("""
                You crafted a stone pickaxe. Both stages are complete, and every requirements page of this entry shows its requirements as done.
                """);
    }

    @Override
    protected String entryName() {
        return "Placed Page Progression";
    }

    @Override
    protected String entryDescription() {
        return "Staged progression with requirements pages placed between the content.";
    }

    @Override
    protected GuiSprite entryBackground() {
        return EntryBackground.DEFAULT;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(Items.TORCH);
    }

    @Override
    protected String entryId() {
        return ID;
    }
}
