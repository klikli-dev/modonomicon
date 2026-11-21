/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.api;

import net.minecraft.resources.Identifier;
import net.minecraft.util.Util;

public class ModonomiconConstants {

    public static class Data {
        public static final String MODONOMICON_DATA_PATH = ModonomiconAPI.ID + "/books";
        public static final String MULTIBLOCK_DATA_PATH = ModonomiconAPI.ID + "/multiblocks";
        public static final String RESEARCH_DATA_PATH = ModonomiconAPI.ID + "/research";

        public static class Book {
            public static final String DEFAULT_FONT = Identifier.fromNamespaceAndPath(ModonomiconAPI.ID, "default").toString();
            public static final String DEFAULT_PAGE_TURN_SOUND = Identifier.fromNamespaceAndPath(ModonomiconAPI.ID, "turn_page").toString();
            public static final String DEFAULT_MODEL = Identifier.fromNamespaceAndPath(ModonomiconAPI.ID, "modonomicon_purple").toString();
            public static final int DEFAULT_TITLE_COLOR = 0xFF000000;
            public static final int DEFAULT_TEXT_COLOR = 0xFF000000;
        }

        public static class Category {
            public static final String DEFAULT_ICON = Identifier.fromNamespaceAndPath(ModonomiconAPI.ID, "modonomicon_purple").toString();
            public static final String DEFAULT_BACKGROUND = Identifier.fromNamespaceAndPath(ModonomiconAPI.ID, "textures/gui/dark_slate_seamless.png").toString();
            public static final int DEFAULT_BACKGROUND_WIDTH = 512;
            public static final int DEFAULT_BACKGROUND_HEIGHT = 512;
            public static final int DEFAULT_MAX_SCROLL_X = 512;
            public static final int DEFAULT_MAX_SCROLL_Y = 512;
            public static final float DEFAULT_BACKGROUND_TEXTURE_ZOOM_MULTIPLIER = 1f;
            public static final float DEFAULT_BACKGROUND_OVERSCAN = 1.1f;
        }

        public static class Icon {
            public static final int DEFAULT_WIDTH = 16;
            public static final int DEFAULT_HEIGHT = 16;
        }

        public static class Command {
            public static final int DEFAULT_MAX_USES = 1;
        }

    }

    public static class I18n {
        public static final String BOOK_PREFIX = "book." + ModonomiconAPI.ID + ".";
        public static final String ITEM_GROUP = "itemGroup." + ModonomiconAPI.ID;

        public static class Gui {
            public static final String PREFIX = ModonomiconAPI.ID + ".gui.";
            public static final String BUTTON_NEXT = PREFIX + "button.next_page";
            public static final String BUTTON_PREVIOUS = PREFIX + "button.previous_page";
            public static final String BUTTON_BACK = PREFIX + "button.back";
            public static final String BUTTON_BACK_TOOLTIP = PREFIX + "button.back.tooltip";
            public static final String BUTTON_EXIT = PREFIX + "button.exit";
            public static final String BUTTON_CLOSE_TAB = PREFIX + "button.close_tab";

            public static final String SCHOLARSHIP_TITLE = PREFIX + "scholarship.title";
            public static final String SCHOLARSHIP_NEXT_STEPS = PREFIX + "scholarship.next_steps";
            public static final String SCHOLARSHIP_NEXT = PREFIX + "scholarship.next";
            public static final String SCHOLARSHIP_COMPLETE = PREFIX + "scholarship.complete";
            public static final String SCHOLARSHIP_STAGE_LINE = PREFIX + "scholarship.stage_line";
            public static final String SCHOLARSHIP_CRAFT_ITEM = PREFIX + "scholarship.craft_item";
            public static final String SCHOLARSHIP_OBTAIN_ITEM = PREFIX + "scholarship.obtain_item";
            public static final String SCHOLARSHIP_READ_ENTRY = PREFIX + "scholarship.read_entry";
            public static final String SCHOLARSHIP_STAGE_TITLE = PREFIX + "scholarship.stage_title";
            public static final String SCHOLARSHIP_PROGRESS = PREFIX + "scholarship.progress";
            public static final String SCHOLARSHIP_MET = PREFIX + "scholarship.met";
            public static final String SCHOLARSHIP_NOT_MET = PREFIX + "scholarship.not_met";
            public static final String SCHOLARSHIP_NOT_MET_CRAFT = PREFIX + "scholarship.not_met.craft";
            public static final String SCHOLARSHIP_NOT_MET_OBTAIN = PREFIX + "scholarship.not_met.obtain";
            public static final String SCHOLARSHIP_NOT_MET_PROGRESS = PREFIX + "scholarship.not_met.progress";
            public static final String SCHOLARSHIP_NOT_MET_HINT = PREFIX + "scholarship.not_met.hint";
            public static final String SCHOLARSHIP_CLICK_TO_OPEN = PREFIX + "scholarship.click_to_open";
            public static final String SCHOLARSHIP_COSTS = PREFIX + "scholarship.costs";
            public static final String SCHOLARSHIP_COST_ITEM = PREFIX + "scholarship.cost.item";
            public static final String SCHOLARSHIP_COST_LEVELS = PREFIX + "scholarship.cost.levels";
            public static final String SCHOLARSHIP_COST_DURABILITY = PREFIX + "scholarship.cost.durability";
            public static final String SCHOLARSHIP_COST_NOT_HELD = PREFIX + "scholarship.cost.not_held";
            public static final String SCHOLARSHIP_COST_SPECIAL = PREFIX + "scholarship.cost.special";
            public static final String SCHOLARSHIP_ACTION_RECORD = PREFIX + "scholarship.action.record";
            public static final String SCHOLARSHIP_ACTION_RECEIVE = PREFIX + "scholarship.action.receive";
            public static final String SCHOLARSHIP_ACTION_RECEIVE_AGAIN = PREFIX + "scholarship.action.receive_again";
            public static final String SCHOLARSHIP_ACTION_PERFORM = PREFIX + "scholarship.action.perform";
            public static final String SCHOLARSHIP_ACTION_ALREADY_HELD = PREFIX + "scholarship.action.already_held";
            public static final String SCHOLARSHIP_CONSUMED_ON_COMPLETION = PREFIX + "scholarship.consumed_on_completion";
            public static final String SCHOLARSHIP_USE_ITEM = PREFIX + "scholarship.use_item";
            public static final String SCHOLARSHIP_NOT_USED = PREFIX + "scholarship.not_used";
            public static final String SCHOLARSHIP_STAGE_COMPLETE = PREFIX + "scholarship.stage.complete";
            public static final String SCHOLARSHIP_STAGE_COMPLETING = PREFIX + "scholarship.stage.completing";
            public static final String SCHOLARSHIP_STAGE_COMPLETE_HINT = PREFIX + "scholarship.stage.complete.hint";
            public static final String SCHOLARSHIP_STAGE_MANUAL = PREFIX + "scholarship.stage.manual";
            public static final String SCHOLARSHIP_EFFECT = PREFIX + "scholarship.effect";
            public static final String SCHOLARSHIP_EFFECT_EXPERIENCE = PREFIX + "scholarship.effect.experience";
            public static final String SCHOLARSHIP_EFFECT_LEVELS = PREFIX + "scholarship.effect.levels";
            public static final String SCHOLARSHIP_EFFECT_VALUE = PREFIX + "scholarship.effect.value";
            public static final String SCHOLARSHIP_EFFECT_SPECIAL = PREFIX + "scholarship.effect.special";
            public static final String SCHOLARSHIP_TRIGGER_SPECIAL = PREFIX + "scholarship.trigger.special";
            public static final String SCHOLARSHIP_EFFECT_PENDING = PREFIX + "scholarship.effect.pending";
            public static final String SCHOLARSHIP_PAGE_TEXT = PREFIX + "scholarship.page.text";
            public static final String FEEDBACK_MISSING = PREFIX + "feedback.missing";
            public static final String FEEDBACK_UNAVAILABLE = PREFIX + "feedback.unavailable";
            public static final String FEEDBACK_INVALID = PREFIX + "feedback.invalid";

            public static final String BUTTON_VISUALIZE = PREFIX + "button.visualize";
            public static final String BUTTON_VISUALIZE_TOOLTIP = PREFIX + "button.visualize.tooltip";
            public static final String BUTTON_VISUALIZE_LAYERS = PREFIX + "button.visualize_layers";
            public static final String BUTTON_VISUALIZE_LAYERS_TOOLTIP = PREFIX + "button.visualize_layers.tooltip";

            public static final String BUTTON_VIEWED_ONCE_RESEARCH = PREFIX + "button.viewed_once_research";
            public static final String BUTTON_VIEWED_ONCE_RESEARCH_TOOLTIP_VISIBLE = PREFIX + "button.viewed_once_research.tooltip.visible";
            public static final String BUTTON_VIEWED_ONCE_RESEARCH_TOOLTIP_ALL = PREFIX + "button.viewed_once_research.tooltip.all";
            public static final String BUTTON_VIEWED_ONCE_RESEARCH_TOOLTIP_NONE = PREFIX + "button.viewed_once_research.tooltip.none";
            public static final String BUTTON_VIEWED_ONCE_RESEARCH_TOOLTIP_SHIFT_INSTRUCTIONS = PREFIX + "button.viewed_once_research.tooltip.shift";
            public static final String BUTTON_VIEWED_ONCE_RESEARCH_TOOLTIP_SHIFT_WARNING = PREFIX + "button.viewed_once_research.tooltip.shift_warning";

            public static final String HOVER_BOOK_LINK = PREFIX + "hover.book_link";
            public static final String HOVER_BOOK_LINK_ERROR = PREFIX + "hover.book_link.error";
            public static final String HOVER_BOOK_LINK_LOCKED = PREFIX + "hover.book_link_locked";
            public static final String HOVER_LINK_ERROR = PREFIX + "hover.link.error";
            public static final String HOVER_BOOK_ENTRY_LINK_LOCKED_INFO = PREFIX + "hover.book_entry_link_locked_info";
            public static final String HOVER_BOOK_ENTRY_LINK_LOCKED_INFO_HINT = PREFIX + "hover.book_entry_link_locked_info.hint";
            public static final String HOVER_BOOK_PAGE_LINK_LOCKED_INFO = PREFIX + "hover.book_page_link_locked_info";
            public static final String HOVER_BOOK_PAGE_LINK_LOCKED_INFO_HINT = PREFIX + "hover.book_page_link_locked_info.hint";
            public static final String HOVER_HTTP_LINK = PREFIX + "hover.http_link";
            public static final String HOVER_ITEM_LINK_INFO = PREFIX + "hover.item_link_info";
            public static final String HOVER_ITEM_LINK_INFO_NO_RECIPE_VIEWER = PREFIX + "hover.item_link_info.no_recipe_viewer";
            public static final String HOVER_COMMAND_LINK = PREFIX + "hover.command_link";
            public static final String HOVER_COMMAND_LINK_UNAVAILABLE = PREFIX + "hover.command_link.unavailable";
            public static final String NO_ERRORS_FOUND = PREFIX + "no_errors_found";
            public static final String BOOK_BUILD_FAILED = PREFIX + "book_build_failed";
            public static final String BOOK_NOT_FOUND = PREFIX + "book_not_found";
            public static final String PAGE_ENTITY_LOADING_ERROR = PREFIX + "page.entity.loading_error";

            public static final String SEARCH_SCREEN_TITLE = PREFIX + "search.screen.title";
            public static final String SEARCH_ENTRY_LOCKED = PREFIX + "search.entry.locked";
            public static final String SEARCH_NO_RESULTS = PREFIX + "search.no_results";
            public static final String SEARCH_NO_RESULTS_SAD = PREFIX + "search.sad";
            public static final String SEARCH_INFO_TEXT = PREFIX + "search.info";
            public static final String SEARCH_ENTRY_LIST_TITLE = PREFIX + "search.entry_list_title";

            public static final String BOOK_INDEX_LIST_TITLE = PREFIX + "book.index_list_title";
            public static final String CATEGORY_INDEX_LIST_TITLE = PREFIX + "category.index_list_title";

            public static final String BOOKMARKS_SCREEN_TITLE = PREFIX + "bookmarks.screen.title";
            public static final String BOOKMARKS_INFO_TEXT = PREFIX + "bookmarks.info";
            public static final String BOOKMARKS_ENTRY_LIST_TITLE = PREFIX + "bookmarks.entry_list_title";
            public static final String BOOKMARKS_NO_RESULTS = PREFIX + "bookmarks.no_results";

            public static final String RECENTLY_UNLOCKED_SCREEN_TITLE = PREFIX + "recently_unlocked.screen.title";
            public static final String RECENTLY_UNLOCKED_INFO_TEXT = PREFIX + "recently_unlocked.info";
            public static final String RECENTLY_UNLOCKED_ENTRY_LIST_TITLE = PREFIX + "recently_unlocked.entry_list_title";
            public static final String RECENTLY_UNLOCKED_NO_RESULTS = PREFIX + "recently_unlocked.no_results";

            public static final String OPEN_SEARCH = PREFIX + "open_search";
            public static final String OPEN_BOOKMARKS = PREFIX + "open_bookmarks";
            public static final String OPEN_RECENTLY_UNLOCKED = PREFIX + "open_recently_unlocked";
            public static final String ADD_BOOKMARK = PREFIX + "add_bookmark";
            public static final String REMOVE_BOOKMARK = PREFIX + "remove_bookmark";

            public static final String RECIPE_PAGE_RECIPE_MISSING = PREFIX + "recipe_page.recipe_missing";
            public static final String RECIPE_PAGE_NO_RECIPE_VIEWER = PREFIX + "recipe_page.no_recipe_viewer";
            public static final String RECIPE_LOOKUP_NO_RESULT = PREFIX + "recipe_lookup.no_result";
        }

        public static class Multiblock {
            public static final String PREFIX = ModonomiconAPI.ID + ".multiblock.";
            public static final String COMPLETE = PREFIX + "complete";
            public static final String NOT_ANCHORED = PREFIX + "not_anchored";
            public static final String REMOVE_BLOCKS = PREFIX + "remove_blocks";
            public static final String LAYER_BY_LAYER = PREFIX + "layer_by_layer";
        }

        public static class Subtitles {
            public static final String PREFIX = ModonomiconAPI.ID + ".subtitle.";
            public static final String TURN_PAGE = PREFIX + "turn_page";
        }

        public static class Tooltips {
            public static final String PREFIX = "tooltip." + ModonomiconAPI.ID + ".";
            public static final String CONDITION_PREFIX = PREFIX + "condition.";
            public static final String CONDITION_MOD_LOADED = CONDITION_PREFIX + "mod_loaded";
            public static final String CONDITION_RESEARCH_NODE_UNLOCKED = CONDITION_PREFIX + "research_node_unlocked";
            public static final String CONDITION_RESEARCH_STAGE_COMPLETED = CONDITION_PREFIX + "research_stage_completed";
            public static final String CONDITION_CATEGORY_HAS_VISIBLE_ENTRIES = CONDITION_PREFIX + "has_visible_entries";
            public static final String CONDITION_ENTRY_UNLOCKED = CONDITION_PREFIX + "entry_unlocked";
            public static final String CONDITION_ITEM_CRAFTED = CONDITION_PREFIX + "item_crafted";
            public static final String CONDITION_ITEM_ACQUIRED = CONDITION_PREFIX + "item_acquired";
            public static final String RECIPE_PREFIX = PREFIX + "recipe.";
            public static final String RECIPE_CRAFTING_SHAPELESS = RECIPE_PREFIX + "crafting_shapeless";
            public static final String ITEM_NO_BOOK_FOUND_FOR_STACK = PREFIX + "no_book_found_for_stack";
            public static final String FLUID_AMOUNT = PREFIX + "fluid.amount";
            public static final String FLUID_AMOUNT_AND_CAPACITY = PREFIX + "fluid.amount_and_capacity";
            public static final String ASSOCIATED_HOLD_TO_OPEN = PREFIX + "associated.hold_to_open";
            public static final String ASSOCIATED_LOCKED = PREFIX + "associated.locked";

        }

        /**
         * Translation keys of research definitions. Datagen and runtime share these, see the key helpers.
         */
        public static class Research {
            public static final String FACT_PREFIX = "research_fact";
            public static final String VALUE_PREFIX = "research_value";
            public static final String NODE_PREFIX = "research_node";
            public static final String STAGE_PREFIX = "research_stage";

            public static String factKey(Identifier factId) {
                return Util.makeDescriptionId(FACT_PREFIX, factId);
            }

            public static String valueKey(Identifier valueId) {
                return Util.makeDescriptionId(VALUE_PREFIX, valueId);
            }

            public static String nodeKey(Identifier nodeId) {
                return Util.makeDescriptionId(NODE_PREFIX, nodeId);
            }

            public static String stageKey(Identifier stageId) {
                return Util.makeDescriptionId(STAGE_PREFIX, stageId);
            }
        }

        public static class Command {
            public static final String PREFIX = ModonomiconAPI.ID + ".command.";

            public static final String ERROR_PREFIX = PREFIX + "error.";
            public static final String ERROR_UNKNOWN_BOOK = ERROR_PREFIX + "unknown_book";
            public static final String ERROR_UNKNOWN_FACT = ERROR_PREFIX + "unknown_fact";
            public static final String ERROR_UNKNOWN_NODE = ERROR_PREFIX + "unknown_node";
            public static final String ERROR_UNKNOWN_VALUE = ERROR_PREFIX + "unknown_value";
            public static final String ERROR_UNKNOWN_STAGE = ERROR_PREFIX + "unknown_stage";
            public static final String ERROR_LOAD_PROGRESS = ERROR_PREFIX + "load_progress";
            public static final String ERROR_LOAD_PROGRESS_CLIENT = ERROR_PREFIX + "load_progress_client";
            public static final String SUCCESS_PREFIX = PREFIX + "success.";
            public static final String SUCCESS_RESET_BOOK = SUCCESS_PREFIX + "reset_book";
            public static final String SUCCESS_RESET_RESEARCH = SUCCESS_PREFIX + "reset_research";
            public static final String SUCCESS_GRANT_FACT = SUCCESS_PREFIX + "grant_fact";
            public static final String SUCCESS_REVOKE_FACT = SUCCESS_PREFIX + "revoke_fact";
            public static final String SUCCESS_UNLOCK_NODE = SUCCESS_PREFIX + "unlock_node";
            public static final String SUCCESS_LOCK_NODE = SUCCESS_PREFIX + "lock_node";
            public static final String SUCCESS_SET_VALUE = SUCCESS_PREFIX + "set_value";
            public static final String SUCCESS_SAVE_PROGRESS = SUCCESS_PREFIX + "save_progress";
            public static final String SUCCESS_LOAD_PROGRESS = SUCCESS_PREFIX + "load_progress";
            public static final String RELOAD_SUCCESS = SUCCESS_PREFIX + "reload_requested";
            public static final String RELOAD_REQUESTED = PREFIX + "reload_requested";
            public static final String DEFAULT_FAILURE_MESSAGE = PREFIX + "failure";
            public static final String FAILURE_NOT_ALLOWED_HERE = PREFIX + "failure_not_allowed_here";
        }
    }
}
