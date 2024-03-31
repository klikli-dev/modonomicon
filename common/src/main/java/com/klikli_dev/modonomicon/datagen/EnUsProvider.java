/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.datagen;

import com.klikli_dev.modonomicon.Modonomicon;
import com.klikli_dev.modonomicon.api.ModonomiconAPI;
import com.klikli_dev.modonomicon.api.ModonomiconConstants;
import com.klikli_dev.modonomicon.api.ModonomiconConstants.I18n.*;
import com.klikli_dev.modonomicon.api.datagen.BookContextHelper;
import com.klikli_dev.modonomicon.api.datagen.AbstractModonomiconLanguageProvider;
import com.klikli_dev.modonomicon.api.datagen.ModonomiconLanguageProvider;
import com.klikli_dev.modonomicon.registry.ItemRegistry;
import net.minecraft.data.PackOutput;

public class EnUsProvider extends AbstractModonomiconLanguageProvider {
    public EnUsProvider(PackOutput packOutput, ModonomiconLanguageProvider cachedProvider) {
        super(packOutput, Modonomicon.MOD_ID, "en_us", cachedProvider);
    }

    private void addMisc() {
        this.add(ModonomiconConstants.I18n.ITEM_GROUP, "Modonomicon");

        //buttons
        this.add(Gui.BUTTON_PREVIOUS, "Previous Page");
        this.add(Gui.BUTTON_NEXT, "Next Page");
        this.add(Gui.BUTTON_BACK, "Back");
        this.add(Gui.BUTTON_BACK_TOOLTIP, "Go back to the last page you visited");
        this.add(Gui.BUTTON_EXIT, "Exit");
        this.add(Gui.BUTTON_VISUALIZE, "Show Multiblock Preview");
        this.add(Gui.BUTTON_VISUALIZE_TOOLTIP, "Show Multiblock Preview");
        this.add(Gui.BUTTON_READ_ALL, "Mark all entries as read");
        this.add(Gui.BUTTON_READ_ALL_TOOLTIP_READ_UNLOCKED, "Mark all §aunlocked§r entries as read.");
        this.add(Gui.BUTTON_READ_ALL_TOOLTIP_SHIFT_INSTRUCTIONS, "Shift-Click to mark §call§r (even locked) entries as read.");
        this.add(Gui.BUTTON_READ_ALL_TOOLTIP_READ_ALL, "Mark §call§r (even locked) entries as read.");
        this.add(Gui.BUTTON_READ_ALL_TOOLTIP_SHIFT_WARNING, "§l§cWarning:§r This may make it harder to read progress-oriented books.");
        this.add(Gui.BUTTON_READ_ALL_TOOLTIP_NONE, "There are currently §lno unread§r unlocked entries.");

        this.add(Gui.HOVER_BOOK_LINK, "Go to: %s");
        this.add(Gui.HOVER_BOOK_LINK_LOCKED, "%s.\n%s");
        this.add(Gui.HOVER_BOOK_ENTRY_LINK_LOCKED_INFO, "You need to unlock this entry before you can open the link!");
        this.add(Gui.HOVER_BOOK_ENTRY_LINK_LOCKED_INFO_HINT, "Hint: The entry is in the Category: %s");
        this.add(Gui.HOVER_BOOK_PAGE_LINK_LOCKED_INFO, "You need to unlock this page before you can open the link!");
        this.add(Gui.HOVER_BOOK_PAGE_LINK_LOCKED_INFO_HINT, "Hint: The page is in the Entry %s, under the Category %s");
        this.add(Gui.HOVER_HTTP_LINK, "Visit: %s");
        this.add(Gui.HOVER_ITEM_LINK_INFO, "Click to show recipe in JEI, Shift-Click to show usage.");
        this.add(Gui.HOVER_ITEM_LINK_INFO_LINE2, "Will not do anything, if no recipe/usage is found.");
        this.add(Gui.HOVER_ITEM_LINK_INFO_NO_JEI, "Install JEI to show recipe/usage on click.");

        this.add(Gui.HOVER_COMMAND_LINK, "Click to run linked command. Hold down shift to show command.");
        this.add(Gui.HOVER_COMMAND_LINK_UNAVAILABLE, "You already used this command too many times, you cannot use it again.");

        //other gui stuff
        this.add(Gui.PAGE_ENTITY_LOADING_ERROR, "Failed to load entity");
        this.add(Gui.SEARCH_SCREEN_TITLE, "Search in Book");
        this.add(Gui.SEARCH_ENTRY_LOCKED, "(Locked)");
        this.add(Gui.SEARCH_NO_RESULTS, "No Results");
        this.add(Gui.SEARCH_NO_RESULTS_SAD, ":(");
        this.add(Gui.SEARCH_INFO_TEXT, """
                To search for entries, simply start typing what you are looking for.
                """);
        this.add(Gui.SEARCH_ENTRY_LIST_TITLE, "Entries");
        this.add(Gui.OPEN_SEARCH, "Open Search");


        this.add(Gui.RECIPE_PAGE_RECIPE_MISSING, "Recipe %s was not found! This may be an issue with the mod, or the modpack may have disabled it.");

        //Tooltip
        this.add(Tooltips.CONDITION_ADVANCEMENT, "Requires Advancement: %s");
        this.add(Tooltips.CONDITION_ADVANCEMENT_LOADING, "Loading ...");
        this.add(Tooltips.CONDITION_ADVANCEMENT_HIDDEN, "Hidden Advancement");
        this.add(Tooltips.CONDITION_ENTRY_UNLOCKED, "Requires unlocked Entry: %s");
        this.add(Tooltips.CONDITION_ENTRY_READ, "Requires read Entry: %s\nHint: Mark all entries as read with the \"eye\" button at the top right.");
        this.add(Tooltips.ITEM_NO_BOOK_FOUND_FOR_STACK, "No book found for this item in the modonomicon book database! Nbt: %s");
        this.add(Tooltips.RECIPE_CRAFTING_SHAPELESS, "Shapeless");
        this.add(Tooltips.FLUID_AMOUNT, "%s mb");
        this.add(Tooltips.FLUID_AMOUNT_AND_CAPACITY, "%s / %s mb");

        //Commands
        this.add(Command.ERROR_UNKNOWN_BOOK, "Unknown book: %s");
        this.add(Command.SUCCESS_RESET_BOOK, "Successfully reset book: %s");
        this.add(Command.SUCCESS_SAVE_PROGRESS, "Saved progress for book: %s. The unlock code has been copied to your clipboard.");
        this.add(Command.SUCCESS_LOAD_PROGRESS, "Successfully loaded progress for book: %s.");
        this.add(Command.RELOAD_REQUESTED, "Requested reload of resource- and datapacks.");
        this.add(Command.RELOAD_SUCCESS, "Successfully reloaded resource- and datapacks.");
        this.add(Command.ERROR_LOAD_PROGRESS, "Invalid unlock code!");
        this.add(Command.ERROR_LOAD_PROGRESS_CLIENT, "Failed to decode unlock code. Make sure to have a valid unlock code in your clipboard! Current Clipboard content: \"%s\"");

        this.add(Command.DEFAULT_FAILURE_MESSAGE, "Modonomicon tried to run a command for you (e.g. because you read an entry for the first time, or clicked a command button or command link). However, it seems you already reached the maximum use limit for this command.");

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

    private void addDemoBook() {

        var helper = ModonomiconAPI.get().getContextHelper(Modonomicon.MOD_ID);
        helper.book("demo");

        this.addDemoBookFeaturesCategory(helper);
        this.addDemoBookFormattingCategory(helper);
        this.addDemoBookHiddenCategory(helper);


    }

    private void addDemoBookFeaturesCategory(BookContextHelper helper) {
        helper.category("features");

        this.addDemoBookMultiblockEntry(helper);
        this.addDemoBookRecipeEntry(helper);
        this.addDemoBookConditionEntries(helper);
        this.addDemoBookSpotlightEntry(helper);
        this.addDemoBookEmptyPageEntry(helper);
        this.addDemoBookEntityEntry(helper);
        this.addDemoBookImagePageEntry(helper);
        this.addDemoBookImageRedirectEntry(helper);
        this.add(helper.categoryName(), "Features Category");

    }

    private void addDemoBookMultiblockEntry(BookContextHelper helper) {
        helper.entry("multiblock");

        helper.page("intro");
        this.add(helper.pageTitle(), "Multiblock Page");
        this.add(helper.pageText(), "Multiblock pages allow to preview multiblocks both in the book and in the world.");

        helper.page("preview");
        this.add("multiblocks.modonomicon.blockentity", "Blockentity Multiblock."); //TODO: should probably move into another part of langgen
        this.add(helper.pageText(), "A sample multiblock.");

        this.add(helper.entryName(), "Multiblock Entry");
        this.add(helper.entryDescription(), "An entry showcasing a multiblock.");
    }

    private void addDemoBookConditionEntries(BookContextHelper helper) {

        helper.entry("condition_root");
        helper.page("info");
        this.add(helper.pageTitle(), "Condition Root");
        this.add(helper.pageText(), "Root page for our condition / unlock tests.");

        this.add(helper.entryName(), "Condition Root Entry");
        this.add(helper.entryDescription(), "Condition Root Entry");


        helper.entry("condition_level_1");
        helper.page("info");
        this.add(helper.pageTitle(), "Condition Level 1");
        this.add(helper.pageText(), "This entry depends on Condition Root being read.");

        this.add(helper.entryName(), "Condition Level 1 Entry");
        this.add(helper.entryDescription(), "Depends on Condition Root being read.");

        helper.entry("condition_level_2");
        helper.page("info");
        this.add(helper.pageTitle(), "Condition Level 2");
        this.add(helper.pageText(), "This entry depends on Condition Level 1 being unlocked.");

        this.add(helper.entryName(), "Condition Level 2 Entry");
        this.add(helper.entryDescription(), "Depends on Condition Level 1 being unlocked.");
    }

    private void addDemoBookRecipeEntry(BookContextHelper helper) {
        helper.entry("recipe");

        helper.page("intro");
        this.add(helper.pageTitle(), "Recipe Entry");
        this.add(helper.pageText(), "Recipe pages allow to show recipes in the book.");

        helper.page("crafting");
        this.add(helper.pageText(), "A sample recipe page.");
        this.add("test.test.test", "Book of Binding: Afrit (Bound)"); //long title to test scaling on recipe 2


        helper.page("smoking");
        this.add(helper.pageText(), "A smoking recipe page with one recipe and some text.");

        this.add(helper.entryName(), "Recipe Entry");
        this.add(helper.entryDescription(), "An entry showcasing recipe pages.");
    }

    private void addDemoBookSpotlightEntry(BookContextHelper helper) {
        helper.entry("spotlight");

        helper.page("intro");
        this.add(helper.pageTitle(), "Spotlight Entry");
        this.add(helper.pageText(), "Spotlight pages allow to show items (actually, ingredients).");

        helper.page("spotlight1");
        this.add(helper.pageTitle(), "Custom Title");
        this.add(helper.pageText(), "A sample spotlight page with custom title.");

        helper.page("spotlight2");
        this.add(helper.pageText(), "A sample spotlight page with automatic title.");

        this.add(helper.entryName(), "Spotlight Entry");
        this.add(helper.entryDescription(), "An entry showcasing spotlight pages.");
    }

    private void addDemoBookEmptyPageEntry(BookContextHelper helper) {
        helper.entry("empty");

        helper.page("intro");
        this.add(helper.pageTitle(), "Empty Page Entry");
        this.add(helper.pageText(), "Empty pages allow to add .. empty pages.");

        this.add(helper.entryName(), "Empty Page Entry");
        this.add(helper.entryDescription(), "An entry showcasing empty pages.");
    }

    private void addDemoBookEntityEntry(BookContextHelper helper) {
        helper.entry("entity");

        helper.page("intro");
        this.add(helper.pageTitle(), "Entity Entry");
        this.add(helper.pageText(), "Entity pages allow to show entities.");

        helper.page("entity1");
        this.add(helper.pageTitle(), "Custom Name");

        helper.page("entity2");
        this.add(helper.pageText(), "A sample entity page with automatic title.");

        this.add(helper.entryName(), "Entity Entry");
        this.add(helper.entryDescription(), "An entry showcasing entity pages.");
    }

    private void addDemoBookImagePageEntry(BookContextHelper helper) {
        helper.entry("image");

        helper.page("intro");
        this.add(helper.pageTitle(), "Image Page Entry");
        this.add(helper.pageText(), "Image pages allow to show images.");

        helper.page("image");
        this.add(helper.pageTitle(), "Sample image!");
        this.add(helper.pageText(), "A sample text for the sample image.");


        this.add(helper.entryName(), "Image Page Entry");
        this.add(helper.entryDescription(), "An entry showcasing image pages.");
    }

    private void addDemoBookImageRedirectEntry(BookContextHelper helper) {
        helper.entry("redirect");
        this.add(helper.entryName(), "Category Redirect Entry");
        this.add(helper.entryDescription(), "Redirects to another category.");
    }


    private void addDemoBookFormattingCategory(BookContextHelper helper) {
        helper.category("formatting");

        this.addDemoBookBasicFormattingEntry(helper);
        this.addDemoBookAdvancedFormattingEntry(helper);
        this.addDemoBookLinkFormattingEntry(helper);

        helper.entry("always_locked");
        this.add(helper.entryName(), "Always Locked Entry");
        this.add(helper.entryDescription(), "Used to demonstrate linking to locked entries");

        this.add(helper.categoryName(), "Formatting Category");
    }

    private void addDemoBookBasicFormattingEntry(BookContextHelper helper) {
        helper.entry("basic");

        helper.page("page1"); //bold, italics, underlines,
        this.add(helper.pageTitle(), "Basic Formatting");
        this.add(helper.pageText(),
                """
                        **This is bold**    \s
                        *This is italics*    \s
                        ++This is underlined++
                             """);

        helper.page("page2"); ////strikethrough, color

        //this.add(helper.pageTitle(), "");
        this.add(helper.pageText(),
                """
                        ~~This is stricken through~~   \s
                        [#](55FF55)Colorful Text![#]()
                             """);

        this.add(helper.entryName(), "Basic Formatting Entry");
        this.add(helper.entryDescription(), "An entry showcasing basic formatting.");
    }

    private void addDemoBookAdvancedFormattingEntry(BookContextHelper helper) {
        helper.entry("advanced");

        helper.page("page1");  //translatable texts, mixed formatting
        this.add(helper.pageTitle(), "Advanced Formatting");
        this.add(helper.pageText(),
                """
                        <t>this.could.be.a.translation.key<t>    \s
                        ***This is bold italics***    \s
                        *++This is italics underlined++*
                        [](item://minecraft:diamond)
                        [TestText](item://minecraft:emerald)
                             """);

        helper.page("page2"); //lists

        //this.add(helper.pageTitle(), "");
        this.add(helper.pageText(),
                """
                        Unordered List:
                        - List item 
                        - List item 2
                        - List item 3

                        Ordered List:
                        1. Entry 1
                        2. Entry 2
                             """);

        helper.page("page3"); //lists
        this.add(helper.pageTitle(), "Ridiculously superlong title that should be scaled!");
        this.add(helper.pageText(),
                """
                        This page is to test title scaling!""");

        this.add(helper.entryName(), "Advanced Formatting Entry");
        this.add(helper.entryDescription(), "An entry showcasing advanced formatting.");
    }

    private void addDemoBookLinkFormattingEntry(BookContextHelper helper) {
        helper.entry("link");

        helper.page("page1"); //http links
        this.add(helper.pageTitle(), "Http Links");
        this.add(helper.pageText(),
                """
                        [Click me!](https://klikli-dev.github.io/modonomicon/) \\
                        [Or me!](https://github.com/klikli-dev/modonomicon)
                             """);

        helper.page("page2"); //book entry links
        this.add(helper.pageTitle(), "Book Links");
        this.add(helper.pageText(),
                """
                        [View a Multiblock](entry://modonomicon:demo/features/multiblock) \\
                        [Link to a Condition](entry://modonomicon:demo/features/condition_level_1) \\
                        [Link to basic formatting](entry://modonomicon:demo/formatting/basic) \\
                        [Link without book id](entry://formatting/basic) \\
                        [Always locked](entry://modonomicon:demo/formatting/always_locked) \\
                        [Category Link without book id](category://formatting/)
                             """);

        helper.page("page3"); //patchouli links
        this.add(helper.pageTitle(), "Patchouli Links");
        this.add(helper.pageText(),
                """
                        [Link to a Patchouli Entry](patchouli://occultism:dictionary_of_spirits//misc/books_of_calling)
                             """);
        this.add("patchouli.occultism.dictionary_of_spirits.misc.books_of_calling.name", "Books of Calling"); //patchouli entry name

        this.add(helper.entryName(), "Link Formatting Entry");
        this.add(helper.entryDescription(), "An entry showcasing link formatting.");
    }

    private void addDemoBookHiddenCategory(BookContextHelper helper) {
        helper.category("hidden");

        helper.entry("always_locked");
        this.add(helper.entryName(), "Always Locked Entry");
        this.add(helper.entryDescription(), "Placeholder because I could not be bothered to create sample content here");

        this.add(helper.categoryName(), "Hidden Category");
    }

    private void addBooks() {
        this.addDemoBook();

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
        this.addBooks();
    }
}
