/*
 * SPDX-FileCopyrightText: 2026 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.api.datagen.book.page;

import com.klikli_dev.modonomicon.api.datagen.book.BookTextHolderModel;
import com.klikli_dev.modonomicon.api.datagen.research.ResearchStageRef;
import com.klikli_dev.modonomicon.book.page.BookPage;
import com.klikli_dev.modonomicon.book.page.BookScholarshipPage;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * A page showing the research requirements of a stage. Without a stage, the next stage of the entry research is shown.
 */
public class BookScholarshipPageModel extends BookPageModel<BookScholarshipPageModel> {
    @Nullable
    protected BookTextHolderModel title;
    @Nullable
    protected BookTextHolderModel text;
    @Nullable
    protected String stage;

    protected BookScholarshipPageModel() {
        super(BookScholarshipPage.ID);
    }

    public static BookScholarshipPageModel create() {
        return new BookScholarshipPageModel();
    }

    @Override
    public BookPage toBookPage(HolderLookup.Provider provider) {
        var page = new BookScholarshipPage(
                this.title != null ? this.title.toBookTextHolder() : null,
                this.text != null ? this.text.toBookTextHolder() : null,
                this.stage,
                this.id,
                this.condition(provider),
                List.of()
        );
        this.applyAssociatedItems(page);
        return page;
    }

    public BookScholarshipPageModel withTitle(String title) {
        this.title = new BookTextHolderModel(title);
        return this;
    }

    public BookScholarshipPageModel withTitle(Component title) {
        this.title = new BookTextHolderModel(title);
        return this;
    }

    public BookScholarshipPageModel withText(String text) {
        this.text = new BookTextHolderModel(text);
        return this;
    }

    public BookScholarshipPageModel withText(Component text) {
        this.text = new BookTextHolderModel(text);
        return this;
    }

    /**
     * Shows the requirements of the given research stage id, e.g. "modonomicon:demo/some_stage".
     * Without a stage, the next stage of the entry research is shown.
     */
    public BookScholarshipPageModel withStage(String stage) {
        this.stage = stage;
        return this;
    }

    public BookScholarshipPageModel withStage(ResearchStageRef stage) {
        return this.withStage(stage.id().toString());
    }
}