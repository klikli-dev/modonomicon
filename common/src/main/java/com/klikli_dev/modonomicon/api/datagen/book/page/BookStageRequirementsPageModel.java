/*
 * SPDX-FileCopyrightText: 2026 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.api.datagen.book.page;

import com.klikli_dev.modonomicon.api.datagen.book.BookTextHolderModel;
import com.klikli_dev.modonomicon.api.datagen.research.ResearchStageRef;
import com.klikli_dev.modonomicon.book.page.BookPage;
import com.klikli_dev.modonomicon.book.page.BookStageRequirementsPage;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * A page showing the research requirements of a stage. Without a stage, the next stage of the entry progression is shown.
 */
public class BookStageRequirementsPageModel extends BookPageModel<BookStageRequirementsPageModel> {
    @Nullable
    protected BookTextHolderModel title;
    @Nullable
    protected BookTextHolderModel text;
    @Nullable
    protected String stage;

    protected BookStageRequirementsPageModel() {
        super(BookStageRequirementsPage.ID);
    }

    public static BookStageRequirementsPageModel create() {
        return new BookStageRequirementsPageModel();
    }

    @Override
    public BookPage toBookPage(HolderLookup.Provider provider) {
        var page = new BookStageRequirementsPage(
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

    public BookStageRequirementsPageModel withTitle(String title) {
        this.title = new BookTextHolderModel(title);
        return this;
    }

    public BookStageRequirementsPageModel withTitle(Component title) {
        this.title = new BookTextHolderModel(title);
        return this;
    }

    public BookStageRequirementsPageModel withText(String text) {
        this.text = new BookTextHolderModel(text);
        return this;
    }

    public BookStageRequirementsPageModel withText(Component text) {
        this.text = new BookTextHolderModel(text);
        return this;
    }

    /**
     * Shows the requirements of the given research stage id, e.g. "modonomicon:demo/some_stage".
     * Without a stage, the next stage of the entry progression is shown.
     */
    public BookStageRequirementsPageModel withStage(String stage) {
        this.stage = stage;
        return this;
    }

    public BookStageRequirementsPageModel withStage(ResearchStageRef stage) {
        return this.withStage(stage.id().toString());
    }
}