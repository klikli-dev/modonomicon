/*
 * SPDX-FileCopyrightText: 2026 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.datagen.book;

import com.klikli_dev.modonomicon.Modonomicon;
import com.klikli_dev.modonomicon.api.datagen.SingleBookSubProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookModel;
import com.klikli_dev.modonomicon.book.BookDisplayMode;
import com.klikli_dev.modonomicon.datagen.book.demo.ConditionalCategory;
import com.klikli_dev.modonomicon.datagen.book.demo.IndexModeCategory;
import net.minecraft.resources.Identifier;

public class DemoIndexBook extends SingleBookSubProvider {

    public static final String ID = "demo_index";

    public DemoIndexBook() {
        super(ID, Modonomicon.MOD_ID);
    }

    @Override
    protected BookModel additionalSetup(BookModel book) {
        return book.withDisplayMode(BookDisplayMode.INDEX)
                .withModel(Identifier.parse("modonomicon:modonomicon_green"))
                .withTheme(theme -> theme.withLayout(layout -> layout
                        .withBookTextOffsetX(5)
                        .withBookTextOffsetY(0)
                        .withBookTextOffsetWidth(-5)));
    }

    @Override
    protected void registerDefaultMacros() {
        //currently no macros
    }

    @Override
    protected void generateCategories() {
        this.add(new IndexModeCategory(this).generate());
        this.add(new ConditionalCategory(this).generate());
    }

    @Override
    protected String bookName() {
        return "Demo Index Book";
    }

    @Override
    protected String bookTooltip() {
        return "A minimal book to showcase Modonomicon index mode.";
    }
}
