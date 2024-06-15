/*
 * SPDX-FileCopyrightText: 2024 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.datagen.book;

import com.klikli_dev.modonomicon.api.datagen.CategoryProvider;
import com.klikli_dev.modonomicon.api.datagen.LeafletEntryProvider;
import com.klikli_dev.modonomicon.api.datagen.LeafletSubProvider;
import com.klikli_dev.modonomicon.api.datagen.ModonomiconLanguageProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel;
import com.klikli_dev.modonomicon.book.PageDisplayMode;

public class DemoLeaflet extends LeafletSubProvider {
    public static final String ID = "demo_leaflet";

    public DemoLeaflet(String modId, ModonomiconLanguageProvider defaultLang, ModonomiconLanguageProvider... translations) {
        super(ID, modId, defaultLang, translations);
    }

    @Override
    protected LeafletEntryProvider createEntryProvider(CategoryProvider parent) {
        //To keep a leaflet neat, you can just use an anonymous entry provider here.
        //alternatively you can of course create a separate class for it and instantiate it here.
        return new LeafletEntryProvider(parent) {
            @Override
            protected void generatePages() {
                this.page("intro", () -> BookTextPageModel.create()
                        .withTitle(this.context().pageTitle())
                        .withText(this.context().pageText())
                );
                this.pageTitle("Demo Leaflet");
                this.pageText("""
                        This is a demo leaflet. It has some features:
                        1. It's a leaflet. Yay.
                        2. All page types modonomicon supports.
                        3. No categories, no books, no mess.
                        4. Fun! (If you like leaflets)
                        """);

                this.page("page2", () -> BookTextPageModel.create()
                        .withTitle(this.context().pageTitle())
                        .withText(this.context().pageText())
                );
                this.pageTitle("A second page");
                this.pageText("""
                        Despite being a leaflet, it can have as many pages as you like!
                        """);

                this.page("page3", () -> BookTextPageModel.create()
                        .withTitle(this.context().pageTitle())
                        .withText(this.context().pageText())
                );
                this.pageTitle("A third page");
                this.pageText("""
                        Now just imagine a few more pages.
                        """);
            }
        };
    }

    @Override
    protected BookModel additionalLeafletSetup(BookModel book) {
        //e.g. set creative tab using .withCreativeTab(<ResourceLocation>)
        return book.withBookTextOffsetWidth(-5)
                .withBookTextOffsetX(5)
                .withPageDisplayMode(PageDisplayMode.SINGLE_PAGE);
    }

    @Override
    protected void registerDefaultMacros() {
        //Currently no macros
    }

    @Override
    protected String bookName() {
        return "Demo Leaflet";
    }

    @Override
    protected String bookTooltip() {
        return "A simple leaflet to show how modonomicon handles those.";
    }
}
