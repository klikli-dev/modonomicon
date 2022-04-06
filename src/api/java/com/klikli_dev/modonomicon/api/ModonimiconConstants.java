/*
 * LGPL-3-0
 *
 * Copyright (C) 2021 klikli-dev
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

package com.klikli_dev.modonomicon.api;

import net.minecraft.resources.ResourceLocation;

public class ModonimiconConstants {

    public static class Data {
        public static final String MODONOMICON_DATA_PATH = ModonomiconAPI.ID + "s";
        public static final String MODONOMICON_ASSET_PATH = ModonomiconAPI.ID + "s";

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
        }
    }

    public static class Nbt {
        public static final String PREFIX = ModonomiconAPI.ID + ":";
        public static final String BOOK_OPEN = PREFIX + "book_open";

    }

    public static class I18n {
        public static final String BOOK_PREFIX = "book." +ModonomiconAPI.ID + ".";
        public static final String ITEM_GROUP = "itemGroup." + ModonomiconAPI.ID;

        public static class Gui {
            public static final String PREFIX = ModonomiconAPI.ID + ".gui.";
            public static final String BUTTON_NEXT = PREFIX + "button.next_page";
            public static final String BUTTON_PREVIOUS = PREFIX + "button.previous_page";
            public static final String BUTTON_EXIT = PREFIX + "button.exit";

            public static final String HOVER_BOOK_LINK = PREFIX + "hover.link";
        }

        public static class Subtitles {
            public static final String PREFIX = ModonomiconAPI.ID + ".subtitles.";
            public static final String TURN_PAGE = PREFIX + "turn_page";
        }
    }
}
