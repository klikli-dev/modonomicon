/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.api;

import net.minecraft.resources.ResourceLocation;

public class ModonimiconConstants {

    public static class Data {
        public static final String MODONOMICON_DATA_PATH = ModonomiconAPI.ID + "s";
        public static final String MULTIBLOCK_DATA_PATH = ModonomiconAPI.ID + "_multiblocks";

        public static class Book {
            public static final String DEFAULT_OVERVIEW_TEXTURE = new ResourceLocation(ModonomiconAPI.ID, "textures/gui/book_overview.png").toString();
            public static final String DEFAULT_CONTENT_TEXTURE = new ResourceLocation(ModonomiconAPI.ID, "textures/gui/book_content.png").toString();
            public static final String DEFAULT_PAGE_TURN_SOUND = new ResourceLocation(ModonomiconAPI.ID, "turn_page").toString();
        }

        public static class Category {
            public static final String DEFAULT_BACKGROUND = new ResourceLocation(ModonomiconAPI.ID, "textures/gui/dark_slate_seamless.png").toString();
            public static final String DEFAULT_ENTRY_TEXTURES = new ResourceLocation(ModonomiconAPI.ID, "textures/gui/entry_textures.png").toString();
        }

        public static class Page {
            public static final ResourceLocation TEXT = new ResourceLocation(ModonomiconAPI.ID, "text");
            public static final ResourceLocation MULTIBLOCK = new ResourceLocation(ModonomiconAPI.ID, "multiblock");
        }

        public static class Condition {
            public static final ResourceLocation ADVANCEMENT = new ResourceLocation(ModonomiconAPI.ID, "advancement");
            public static final ResourceLocation OR = new ResourceLocation(ModonomiconAPI.ID, "or");
            public static final ResourceLocation AND = new ResourceLocation(ModonomiconAPI.ID, "and");

            public static final ResourceLocation TRUE = new ResourceLocation(ModonomiconAPI.ID, "true");

            public static final ResourceLocation FALSE = new ResourceLocation(ModonomiconAPI.ID, "false");

            public static final ResourceLocation ENTRY_UNLOCKED = new ResourceLocation(ModonomiconAPI.ID, "entry_unlocked");

            public static final ResourceLocation ENTRY_READ = new ResourceLocation(ModonomiconAPI.ID, "entry_read");
        }
    }

    public static class Nbt {
        public static final String PREFIX = ModonomiconAPI.ID + ":";
        public static final String BOOK_OPEN = PREFIX + "book_open";

    }

    public static class I18n {
        public static final String BOOK_PREFIX = "book." + ModonomiconAPI.ID + ".";
        public static final String ITEM_GROUP = "itemGroup." + ModonomiconAPI.ID;

        public static class Gui {
            public static final String PREFIX = ModonomiconAPI.ID + ".gui.";
            public static final String BUTTON_NEXT = PREFIX + "button.next_page";
            public static final String BUTTON_PREVIOUS = PREFIX + "button.previous_page";
            public static final String BUTTON_EXIT = PREFIX + "button.exit";

            public static final String BUTTON_VISUALIZE = PREFIX + "button.visualize";
            public static final String BUTTON_VISUALIZE_TOOLTIP = PREFIX + "button.visualize.tooltip";


            public static final String HOVER_BOOK_LINK = PREFIX + "hover.book_link";
            public static final String HOVER_HTTP_LINK = PREFIX + "hover.http_link";

            public static final String NO_ERRORS_FOUND = PREFIX + "no_errors_found";
        }

        public static class Multiblock {
            public static final String PREFIX = ModonomiconAPI.ID + ".multiblock.";
            public static final String COMPLETE = PREFIX + "complete";
            public static final String NOT_ANCHORED = PREFIX + "not_anchored";
            public static final String REMOVE_BLOCKS = PREFIX + "remove_blocks";
        }

        public static class Subtitles {
            public static final String PREFIX = ModonomiconAPI.ID + ".subtitle.";
            public static final String TURN_PAGE = PREFIX + "turn_page";
        }

        public static class Tooltips {
            public static final String PREFIX = ModonomiconAPI.ID + ".tooltip.";
            public static final String CONDITION_PREFIX =PREFIX + ".condition.";
            public static final String CONDITION_ADVANCEMENT = CONDITION_PREFIX + "advancement";
            public static final String CONDITION_ENTRY_UNLOCKED = CONDITION_PREFIX + "entry_unlocked";
            public static final String CONDITION_ENTRY_READ = CONDITION_PREFIX + "entry_read";
        }

        public static class Command {
            public static final String PREFIX = ModonomiconAPI.ID + ".command.";

            public static final String ERROR_PREFIX = PREFIX + ".error.";
            public static final String SUCCESS_PREFIX = PREFIX + ".error.";
            public static final String ERROR_UNKNOWN_BOOK = ERROR_PREFIX + "unknown_book";
            public static final String SUCCESS_RESET_BOOK = SUCCESS_PREFIX + "reset_Book";
        }
    }
}
