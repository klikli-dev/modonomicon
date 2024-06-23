// SPDX-FileCopyrightText: 2024 klikli-dev
//
// SPDX-License-Identifier: MIT

package com.klikli_dev.modonomicon.api.datagen;

import com.klikli_dev.modonomicon.api.datagen.book.BookEntryModel;

import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

public abstract class CategoryProviderBase extends ModonomiconProviderBase {
    protected final ModonomiconProviderBase parent;

    protected CategoryProviderBase(ModonomiconProviderBase parent, String modId, BiConsumer<String, String> lang, Map<String, BiConsumer<String, String>> translations, BookContextHelper context, ConditionHelper conditionHelper) {
        super(modId, lang, translations, context, conditionHelper);
        this.parent = parent;
    }

    public abstract CategoryEntryMap entryMap();

    public abstract BookEntryModel add(BookEntryModel entry);

    public abstract List<BookEntryModel> add(List<BookEntryModel> entries);
}
