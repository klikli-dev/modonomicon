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

        public static class Book {
            public static final String DEFAULT_TEXTURE = new ResourceLocation(ModonomiconAPI.ID, "textures/gui/book.png").toString();
        }

        public static class Category {
            public static final String DEFAULT_BACKGROUND = new ResourceLocation(ModonomiconAPI.ID, "textures/gui/dark_slate_seamless.png").toString();
            public static final String DEFAULT_ENTRY_TEXTURES = new ResourceLocation(ModonomiconAPI.ID, "textures/gui/entry_textures.png").toString();
        }
    }

    public static class Nbt {
        public static final String PREFIX = ModonomiconAPI.ID + ":";
        public static final String BOOK_OPEN = PREFIX + "book_open";

    }

    public static class I18n {
        public static final String BOOK_PREFIX = "book." +ModonomiconAPI.ID + ".";
        public static final String ITEM_GROUP = "itemGroup." + ModonomiconAPI.ID;
    }
}
