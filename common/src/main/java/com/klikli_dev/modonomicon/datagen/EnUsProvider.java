/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.datagen;

import com.klikli_dev.modonomicon.Modonomicon;
import com.klikli_dev.modonomicon.api.ModonomiconConstants;
import com.klikli_dev.modonomicon.api.ModonomiconConstants.I18n.*;
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


        this.add(Gui.BOOK_INDEX_LIST_TITLE, "Categories");
        this.add(Gui.CATEGORY_INDEX_LIST_TITLE, "Entries");

        this.add(Gui.BOOKMARKS_SCREEN_TITLE, "Bookmarks");
        this.add(Gui.BOOKMARKS_ENTRY_LIST_TITLE, "Bookmark Entries");
        this.add(Gui.BOOKMARKS_NO_RESULTS, "No entries yet.");
        this.add(Gui.BOOKMARKS_INFO_TEXT, """
                To navigate to a bookmark, click on it in this list.
                \\
                \\
                To add a bookmark, open an entry and click the "Add Bookmark" button on the bottom right.
                \\
                \\
                To remove a bookmark, navigate to it and click the "Remove Bookmark" button on the bottom right.
                """);

        this.add(Gui.OPEN_BOOKMARKS, "Open Bookmarks");
        this.add(Gui.ADD_BOOKMARK, "Add Bookmark");
        this.add(Gui.REMOVE_BOOKMARK, "Remove Bookmark");

        this.add(Gui.RECIPE_PAGE_RECIPE_MISSING, "Recipe %s was not found! This may be an issue with the mod, or the modpack may have disabled it.");

        //Tooltip
        this.add(Tooltips.CONDITION_ADVANCEMENT, "Requires Advancement: %s");
        this.add(Tooltips.CONDITION_ADVANCEMENT_LOADING, "Loading ...");
        this.add(Tooltips.CONDITION_ADVANCEMENT_HIDDEN, "Hidden Advancement");
        this.add(Tooltips.CONDITION_ENTRY_UNLOCKED, "Requires unlocked Entry: %s");
        this.add(Tooltips.CONDITION_CATEGORY_HAS_ENTRIES, "Requires that category %s has loaded entries");
        this.add(Tooltips.CONDITION_MOD_LOADED, "Requires loaded mod: %s");
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

    protected void addTranslations() {
        this.addMisc();
        this.addItems();
    }
}
