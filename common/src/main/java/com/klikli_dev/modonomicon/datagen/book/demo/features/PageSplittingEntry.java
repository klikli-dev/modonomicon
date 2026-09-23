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
                This page opts into splitting, so long text flows onto
                continuation fragments at full scale instead of shrinking.
                The text below is deliberately long: once it overflows the
                first fragment, the remainder renders as text-only
                continuations with no title or separator.

                Splitting is recalculated for the active locale and font,
                and links keep working everywhere: {0}

                - Bulleted lists keep their prefixes across fragments.
                - Wrapped list lines stay indented under their item.
                - Numbered lists behave the same, as shown below.

                1. Read the title on the first fragment, then turn the page
                to find this list continuing without any repeated header.
                2. Each step keeps its number, even when it wraps onto extra
                rendered lines or lands on a continuation fragment.
                3. Lorem ipsum dolor sit amet, consectetur adipiscing elit,
                sed do eiusmod tempor incididunt ut labore et dolore.
                4. Ut enim ad minim veniam, quis nostrud exercitation ullamco
                laboris nisi ut aliquip ex ea commodo consequat.
                5. Duis aute irure dolor in reprehenderit in voluptate velit
                esse cillum dolore eu fugiat nulla pariatur.
                6. Excepteur sint occaecat cupidatat non proident, sunt in
                culpa qui officia deserunt mollit anim id est laborum.
                7. Sed ut perspiciatis unde omnis iste natus error sit
                voluptatem accusantium doloremque laudantium.
                8. Nemo enim ipsam voluptatem quia voluptas sit aspernatur
                aut odit aut fugit, sed quia consequuntur magni dolores.
                9. Neque porro quisquam est, qui dolorem ipsum quia dolor sit
                amet, consectetur, adipisci velit.
                10. Ut enim ad minima veniam, quis nostrum exercitationem
                ullam corporis suscipit laboriosam, nisi ut aliquid ex ea.
                11. Quis autem vel eum iure reprehenderit qui in ea voluptate
                velit esse quam nihil molestiae consequatur.
                12. At vero eos et accusamus et iusto odio dignissimos
                ducimus qui blanditiis praesentium voluptatum.

                {1}
                Links on continuations navigate exactly like links on the
                first fragment, and bookmarks still record the authored page.
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
                This page disables both scaling and splitting, so it
                renders at full scale without continuation fragments.
                Lorem ipsum dolor sit amet, consectetur adipiscing elit,
                sed do eiusmod tempor incididunt ut labore et dolore
                magna aliqua. Ut enim ad minim veniam, quis nostrud
                exercitation ullamco laboris nisi ut aliquip ex ea
                commodo consequat. Duis aute irure dolor in
                reprehenderit in voluptate velit esse cillum dolore
                eu fugiat nulla pariatur. Excepteur sint occaecat
                cupidatat non proident, sunt in culpa qui officia
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
