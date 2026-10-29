// SPDX-FileCopyrightText: 2024 klikli-dev
//
// SPDX-License-Identifier: MIT

package com.klikli_dev.modonomicon.datagen.book.demo.features;

import com.klikli_dev.modonomicon.api.datagen.CategoryProvider;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.condition.BookModLoadedConditionModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel;
import com.mojang.datafixers.util.Pair;
import net.minecraft.world.item.Items;

public class FilePagesEntry extends EntryProvider {
    public static final String ID = "file_pages";

    public FilePagesEntry(CategoryProvider parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        //This page is replaced by a file-based page, see the override files in
        //common/src/main/resources/data/modonomicon/modonomicon/books/demo/pages/features/file_pages/
        this.page("replaced", () -> BookTextPageModel.create()
                .withId("replaced")
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText())
        );
        this.pageTitle("To Be Replaced");
        this.pageText("""
                This page is replaced by the file-based page `40_override_by_id.json`.
                If file-based pages work, you are reading the replacement instead of this text.
                """);

        //This page is removed by a file-based page targeting its anchor
        this.page("anchored", () -> BookTextPageModel.create()
                .withAnchor("remove_me")
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText())
        );
        this.pageTitle("To Be Removed (Anchor)");
        this.pageText("""
                This page is removed by the file-based page `20_remove_by_anchor.json`.
                If file-based pages work, you will never see this text.
                """);

        //This page is removed by a file-based page targeting its index (2)
        this.page("indexed", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText())
        );
        this.pageTitle("To Be Removed (Index)");
        this.pageText("""
                This page is removed by the file-based page `10_remove_by_index.json`.
                If file-based pages work, you will never see this text.
                """);

        //This page is removed by a file-based page targeting its id
        this.page("removed", () -> BookTextPageModel.create()
                .withId("removed")
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText())
        );
        this.pageTitle("To Be Removed (Id)");
        this.pageText("""
                This page is removed by the file-based page `30_remove_by_id.json`.
                If file-based pages work, you will never see this text.
                """);

        //This page stays. It uses a (passing) load condition to showcase the field in the generated json.
        this.page("staying", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText())
                .withLoadCondition(BookModLoadedConditionModel.create().withModId("modonomicon"))
        );
        this.pageTitle("Staying Page");
        this.pageText("""
                This page is not targeted by any file-based page, so it stays.
                It has a passing `load_condition` (mod loaded: modonomicon).
                """);

        //Translations for the replacement page content in 40_override_by_id.json.
        //No page model is added here, this only registers the language keys.
        this.context().page("replacement");
        this.pageTitle("Replacement Page");
        this.pageText("""
                This text comes from the file-based page `40_override_by_id.json`, \\
                which replaced the original page with the id `replaced`.
                """);

        //Translations for the appended page content in 50_append_extra.json.
        this.context().page("appended");
        this.pageTitle("Appended Page");
        this.pageText("""
                This page was added by the file-based page `50_append_extra.json`. \\
                It has no target, so it is appended at the end of the entry.
                """);
    }

    @Override
    protected String entryName() {
        return "File-based Pages";
    }

    @Override
    protected String entryDescription() {
        return "An entry showcasing file-based pages: overriding, adding and removing single pages.";
    }

    @Override
    protected Pair<Integer, Integer> entryBackground() {
        return EntryBackground.DEFAULT;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(Items.PAPER);
    }

    @Override
    protected String entryId() {
        return ID;
    }
}
