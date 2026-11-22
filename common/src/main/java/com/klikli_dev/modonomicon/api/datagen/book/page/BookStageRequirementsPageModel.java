/*
 * SPDX-FileCopyrightText: 2026 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.api.datagen.book.page;

import com.klikli_dev.modonomicon.api.datagen.research.ResearchStageRef;
import net.minecraft.network.chat.Component;

/**
 * Scholarship page builder under its old name. It emits the same JSON as {@link BookScholarshipPageModel}.
 *
 * @deprecated Use {@link BookScholarshipPageModel} instead. Will be removed in Minecraft 26.3.
 */
@Deprecated(forRemoval = true)
public class BookStageRequirementsPageModel extends BookScholarshipPageModel {
    protected BookStageRequirementsPageModel() {
    }

    /**
     * @deprecated Use {@link BookScholarshipPageModel#create()} instead. Will be removed in Minecraft 26.3.
     */
    @Deprecated(forRemoval = true)
    public static BookStageRequirementsPageModel create() {
        return new BookStageRequirementsPageModel();
    }

    /**
     * @deprecated Use {@link BookScholarshipPageModel#withTitle(String)} instead. Will be removed in Minecraft 26.3.
     */
    @Deprecated(forRemoval = true)
    @Override
    public BookStageRequirementsPageModel withTitle(String title) {
        super.withTitle(title);
        return this;
    }

    /**
     * @deprecated Use {@link BookScholarshipPageModel#withTitle(Component)} instead. Will be removed in Minecraft 26.3.
     */
    @Deprecated(forRemoval = true)
    @Override
    public BookStageRequirementsPageModel withTitle(Component title) {
        super.withTitle(title);
        return this;
    }

    /**
     * @deprecated Use {@link BookScholarshipPageModel#withText(String)} instead. Will be removed in Minecraft 26.3.
     */
    @Deprecated(forRemoval = true)
    @Override
    public BookStageRequirementsPageModel withText(String text) {
        super.withText(text);
        return this;
    }

    /**
     * @deprecated Use {@link BookScholarshipPageModel#withText(Component)} instead. Will be removed in Minecraft 26.3.
     */
    @Deprecated(forRemoval = true)
    @Override
    public BookStageRequirementsPageModel withText(Component text) {
        super.withText(text);
        return this;
    }

    /**
     * @deprecated Use {@link BookScholarshipPageModel#withStage(String)} instead. Will be removed in Minecraft 26.3.
     */
    @Deprecated(forRemoval = true)
    @Override
    public BookStageRequirementsPageModel withStage(String stage) {
        super.withStage(stage);
        return this;
    }

    /**
     * @deprecated Use {@link BookScholarshipPageModel#withStage(ResearchStageRef)} instead. Will be removed in Minecraft 26.3.
     */
    @Deprecated(forRemoval = true)
    @Override
    public BookStageRequirementsPageModel withStage(ResearchStageRef stage) {
        super.withStage(stage);
        return this;
    }
}