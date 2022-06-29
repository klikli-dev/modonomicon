/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.datagen;

import com.klikli_dev.modonomicon.Modonomicon;
import com.klikli_dev.modonomicon.api.ModonimiconConstants;
import com.klikli_dev.modonomicon.api.ModonimiconConstants.I18n.*;
import com.klikli_dev.modonomicon.registry.ItemRegistry;
import net.minecraft.data.DataGenerator;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.data.LanguageProvider;

import java.util.function.Supplier;

public abstract class LangGenerator extends LanguageProvider {
    public LangGenerator(DataGenerator generator, String locale) {
        super(generator, Modonomicon.MODID, locale);
    }

    public void addItemSuffix(Supplier<? extends Item> key, String suffix, String name) {
        this.add(key.get().getDescriptionId() + suffix, name);
    }

    protected String itemKey(String id) {
        return "item." + Modonomicon.MODID + "." + id;
    }

    protected void advancementTitle(String name, String s) {
        this.add(((TranslatableContents)AdvancementsGenerator.title(name).getContents()).getKey(), s);
    }

    protected void advancementDescr(String name, String s) {
        this.add(((TranslatableContents)AdvancementsGenerator.descr(name).getContents()).getKey(), s);
    }


    public static final class English extends LangGenerator {

        public English(DataGenerator generator) {
            super(generator, "en_us");
        }

        private void addMisc() {
            this.add(ModonimiconConstants.I18n.ITEM_GROUP, "Modonomicon");

            //buttons
            this.add(Gui.BUTTON_PREVIOUS, "Previous Page");
            this.add(Gui.BUTTON_NEXT, "Next Page");
            this.add(Gui.BUTTON_EXIT, "Exit");
            this.add(Gui.BUTTON_VISUALIZE, "Show Multiblock Preview");
            this.add(Gui.BUTTON_VISUALIZE_TOOLTIP, "Show Multiblock Preview");


            this.add(Gui.HOVER_BOOK_LINK, "Go to: %s");
            this.add(Gui.HOVER_HTTP_LINK, "Visit: %s");

            //Tooltip
            this.add(Tooltips.CONDITION_ADVANCEMENT, "Requires Advancement: %s");
            this.add(Tooltips.CONDITION_ENTRY_UNLOCKED, "Requires unlocked Entry: %s");
            this.add(Tooltips.CONDITION_ENTRY_READ, "Requires read Entry: %s");

            //Commands
            this.add(Command.ERROR_UNKNOWN_BOOK, "Unknown book: %s");
            this.add(Command.SUCCESS_RESET_BOOK, "Successfuly reset book: %s");

            //sounds
            this.add(Subtitles.TURN_PAGE, "Turn Page");

            //Others
            this.add(Gui.NO_ERRORS_FOUND, "No errors found. You should not see this page!");

            //Multiblock Preview
            this.add(Multiblock.COMPLETE, "Complete!");
            this.add(Multiblock.NOT_ANCHORED, "Right-Click a Block to anchor the Structure.");
            this.add(Multiblock.REMOVE_BLOCKS, " (Clear blocks marked in red)");
        }

        private void addItems() {
            this.addItem(ItemRegistry.MODONOMICON, "Modonomicon");
        }

        private void addAdvancements() {
            this.advancementTitle("root", "Modonomicon");
            this.advancementDescr("root", "The book of all books!");
        }

        private void addBooks() {
            //TODO: convert this into a real data gen for books
            this.add("modonomicon.test_book.title", "Test Book");

            this.add("modonomicon.test.entries.test_category.test_entry.description", "Test Description");
            this.add("modonomicon.test.sections.test_category.test_entry.page0.title", "[**Bold Link**](book://modonomicon:test)");
            this.add("modonomicon.test.sections.test_category.test_entry.page0.text",
                    """
                            [This is a **link** text](https://www.google.com).  \s
                            We have a newline here.
                            - List item 
                            - List item 2
                            - List item 3
                                                
                            And this is a super long line where we hope it will be automatically wrapped into a new line otherwise that is super-bad.      
                            """);
            this.add("modonomicon.test.sections.test_category.test_entry.page1.title", "*[#](55FF55)Colorful Italics*[#]()");
            this.add("modonomicon.test.sections.test_category.test_entry.page1.text",
                    """
                            And this is our page two.
                             """);
            this.add("modonomicon.test.sections.test_category.test_entry.page2.title", "Page 3");
            this.add("modonomicon.test.sections.test_category.test_entry.page2.text",
                    """
                            And this is our page three.
                             """);


            this.add("modonomicon.test.sections.test_category.test_entry_child.page2.text",
                    """
                            And this is our page three.  \s
                            [With link](entry://modonomicon:test/test_category/test_entry@test_anchor)
                             """);

            this.add("modonomicon.test.sections.test_category.test_entry_child.page_with_error.text",
                    """
                            Page with invalid link!  \s
                            [With link](entry://modonomicon:test/test_category/test_entry2@test_anchor)
                             """);

            this.add("modonomicon.test.sections.test_category.test_entry_child.condition.tooltip",
                    "This page cannot be unlocked!");

            this.add("modonomicon.test.sections.test_category.multiblock.page0.text",
                    """
                            This is a sample multiblock.  \s
                            We have a **second** line too.
                            """);
            this.add("modonomicon.test.sections.test_category.multiblock.page0.multiblock_name", "Sample Multiblock");
        }

        protected void addTranslations() {
            this.addMisc();
            this.addItems();
            this.addAdvancements();
            this.addBooks();
        }
    }
}
