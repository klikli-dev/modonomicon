/*
 * SPDX-FileCopyrightText: 2023 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.api.datagen;

import com.klikli_dev.modonomicon.api.datagen.book.BookEntryModel;
import com.klikli_dev.modonomicon.api.datagen.book.condition.*;
import net.minecraft.resources.ResourceLocation;

public class ConditionHelper {
    public BookAdvancementConditionModel advancement(ResourceLocation advancementId) {
        return BookAdvancementConditionModel.create().withAdvancementId(advancementId);
    }

    public BookAdvancementConditionModel advancementBuilder(ResourceLocation advancementId) {
        return BookAdvancementConditionModel.create().withAdvancementId(advancementId);
    }

    public BookEntryReadConditionModel entryRead(ResourceLocation entryId) {
        return BookEntryReadConditionModel.create().withEntry(entryId);
    }

    public BookEntryReadConditionModel entryReadBuilder(ResourceLocation entryId) {
        return BookEntryReadConditionModel.create().withEntry(entryId);
    }

    public BookEntryReadConditionModel entryRead(BookEntryModel entry) {
        return BookEntryReadConditionModel.create().withEntry(entry.getId());
    }
    public BookEntryReadConditionModel entryReadBuilder(BookEntryModel entry) {
        return BookEntryReadConditionModel.create().withEntry(entry.getId());
    }

    public BookAndConditionModel and(BookConditionModel... children) {
        return BookAndConditionModel.create().withChildren(children);
    }

    public BookAndConditionModel andBuilder(BookConditionModel... children) {
        return BookAndConditionModel.create().withChildren(children);
    }

    public BookOrConditionModel or(BookConditionModel... children) {
        return BookOrConditionModel.create().withChildren(children);
    }

    public BookOrConditionModel orBuilder(BookConditionModel... children) {
        return BookOrConditionModel.create().withChildren(children);
    }
}