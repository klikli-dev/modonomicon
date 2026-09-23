// SPDX-FileCopyrightText: 2026 klikli-dev
//
// SPDX-License-Identifier: MIT

package com.klikli_dev.modonomicon.datagen.book.demo.features;

import com.klikli_dev.modonomicon.api.datagen.CategoryProvider;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel;
import com.klikli_dev.modonomicon.client.gui.book.theme.GuiSprite;
import com.klikli_dev.modonomicon.datagen.book.demo.FeaturesCategory;
import net.minecraft.world.item.Items;

/**
 * Demonstrates opt-in client-side page splitting as an alternative to scale-to-fit.
 * Splitting is locale-aware, preserves authored page numbers for links and bookmarks,
 * and renders overflow as text-only continuation fragments.
 */
public class PageSplittingEntry extends EntryProvider {
    public static final String ID = "page_splitting";

    public PageSplittingEntry(CategoryProvider parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("split", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText())
                .withAllowPageSplit(true)
        );
        this.pageTitle("Splitting Enabled");
        this.pageText("""
                This page opts into splitting, so long text flows onto \\
                continuation fragments at full scale instead of shrinking. \\

                The first fragment keeps the title and this separator text, \\
                while overflow renders as text-only continuations. \\

                - Lists keep their prefixes across fragments. \\
                - Explicit newlines are preserved. \\
                - Links keep working everywhere: {0} \\

                Lorem ipsum dolor sit amet, consectetur adipiscing elit, \\
                sed do eiusmod tempor incididunt ut labore et dolore \\
                magna aliqua. Ut enim ad minim veniam, quis nostrud \\
                exercitation ullamco laboris nisi ut aliquip ex ea \\
                commodo consequat. Duis aute irure dolor in \\
                reprehenderit in voluptate velit esse cillum dolore \\
                eu fugiat nulla pariatur. Excepteur sint occaecat \\
                cupidatat non proident, sunt in culpa qui officia \\
                deserunt mollit anim id est laborum. \\

                {1} \\
                Sed ut perspiciatis unde omnis iste natus error sit \\
                voluptatem accusantium doloremque laudantium, totam \\
                rem aperiam, eaque ipsa quae ab illo inventore \\
                veritatis et quasi architecto beatae vitae dicta \\
                sunt explicabo.
                """,
                this.entryLink("Back to this entry", FeaturesCategory.ID, ID),
                this.entryLink("A second link on a continuation", FeaturesCategory.ID, ID)
        );

        this.page("unscaled", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText())
                .withAutoScale(false)
                .withAllowPageSplit(false)
        );
        this.pageTitle("Unscaled Without Split");
        this.pageText("""
                This page disables both scaling and splitting, so it \\
                renders at full scale without continuation fragments. \\
                Lorem ipsum dolor sit amet, consectetur adipiscing elit, \\
                sed do eiusmod tempor incididunt ut labore et dolore \\
                magna aliqua. Ut enim ad minim veniam, quis nostrud \\
                exercitation ullamco laboris nisi ut aliquip ex ea \\
                commodo consequat. Duis aute irure dolor in \\
                reprehenderit in voluptate velit esse cillum dolore \\
                eu fugiat nulla pariatur. Excepteur sint occaecat \\
                cupidatat non proident, sunt in culpa qui officia \\
                deserunt mollit anim id est laborum.
                """
        );
    }

    @Override
    protected String entryName() {
        return "Page Splitting Entry";
    }

    @Override
    protected String entryDescription() {
        return "An entry showcasing opt-in page splitting.";
    }

    @Override
    protected GuiSprite entryBackground() {
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
