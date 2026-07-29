/*
 * SPDX-FileCopyrightText: 2026 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.book.runtime;

import com.klikli_dev.modonomicon.Modonomicon;
import com.klikli_dev.modonomicon.api.ModonomiconAPI;
import com.klikli_dev.modonomicon.api.ModonomiconConstants;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.book.BookCategory;
import com.klikli_dev.modonomicon.book.BookDisplayMode;
import com.klikli_dev.modonomicon.book.BookIcon;
import com.klikli_dev.modonomicon.book.BookTextHolder;
import com.klikli_dev.modonomicon.book.conditions.BookNoneCondition;
import com.klikli_dev.modonomicon.book.entries.BookContentEntry;
import com.klikli_dev.modonomicon.book.entries.BookEntry;
import com.klikli_dev.modonomicon.book.page.BookTextPage;
import com.klikli_dev.modonomicon.datagen.book.DemoBook;
import com.klikli_dev.modonomicon.datagen.book.demo.FeaturesCategory;
import com.klikli_dev.modonomicon.datagen.book.demo.features.CommandEntry;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.Items;

import java.util.List;

public final class DemoRuntimeBookContent {

    private static final Identifier DEMO_BOOK_ID = Modonomicon.loc(DemoBook.ID);
    private static final Identifier RUNTIME_CATEGORY_ID = Modonomicon.loc("runtime_demo");
    private static final Identifier RUNTIME_ENTRY_ID = Modonomicon.loc("runtime_demo/runtime_entry");
    private static final Identifier COMMAND_ENTRY_ID = Modonomicon.loc(FeaturesCategory.ID + "/" + CommandEntry.ID);

    private DemoRuntimeBookContent() {
    }

    public static void register() {
        try (var batch = ModonomiconAPI.get().openRuntimeContentBatch()) {
            batch.book(DEMO_BOOK_ID).addCategory(createRuntimeCategory());
            batch.book(DEMO_BOOK_ID)
                    .category(Modonomicon.loc(FeaturesCategory.ID))
                    .entry(COMMAND_ENTRY_ID)
                    .addPage(new BookTextPage(
                            new BookTextHolder("Runtime API"),
                            new BookTextHolder("This page was appended at runtime through Modonomicon's new batch API."),
                            false,
                            true,
                            "runtime_api_demo",
                            new BookNoneCondition()
                    ));
        }
    }

    private static BookCategory createRuntimeCategory() {
        var category = new BookCategory(
                RUNTIME_CATEGORY_ID,
                "Runtime API Demo",
                new BookTextHolder("This category was added on common setup using the runtime content API."),
                500,
                new BookNoneCondition(),
                true,
                new BookIcon(new ItemStackTemplate(Items.LIGHTNING_ROD.weathering().unaffected())),
                BookDisplayMode.NODE,
                Identifier.parse(ModonomiconConstants.Data.Category.DEFAULT_BACKGROUND),
                ModonomiconConstants.Data.Category.DEFAULT_BACKGROUND_WIDTH,
                ModonomiconConstants.Data.Category.DEFAULT_BACKGROUND_HEIGHT,
                ModonomiconConstants.Data.Category.DEFAULT_MAX_SCROLL_X,
                ModonomiconConstants.Data.Category.DEFAULT_MAX_SCROLL_Y,
                ModonomiconConstants.Data.Category.DEFAULT_BACKGROUND_TEXTURE_ZOOM_MULTIPLIER,
                List.of(),
                null,
                null,
                true
        );

        category.addEntry(new BookContentEntry(
                RUNTIME_ENTRY_ID,
                new BookEntry.BookEntryData(
                        RUNTIME_CATEGORY_ID,
                        List.of(),
                        0,
                        0,
                        "Runtime Entry",
                        "A demo entry registered through the runtime API.",
                        new BookIcon(new ItemStackTemplate(Items.WRITABLE_BOOK)),
                        EntryBackground.DEFAULT,
                        new BookNoneCondition(),
                        false,
                        false,
                        0
                ),
                null,
                List.of(
                        new BookTextPage(
                                new BookTextHolder("Added at runtime"),
                                new BookTextHolder("This category and entry were added during common setup. Modonomicon queued them until the demo book was available."),
                                false,
                                true,
                                "runtime_category_intro",
                                new BookNoneCondition()
                        )
                )
        ));

        return category;
    }
}
