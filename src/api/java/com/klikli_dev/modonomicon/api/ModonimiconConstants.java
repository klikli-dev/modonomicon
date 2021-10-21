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

public class ModonimiconConstants {

    public static class Data {
        public static final String MODONOMICON_DATA_PATH = ModonomiconAPI.ID + "_data";
    }

    public static class Nbt {
        public static final String PREFIX = ModonomiconAPI.ID + ":";
        public static final String BOOK_OPEN = PREFIX + "book_open";

    }

    public static class I18n {
        public static final String BOOK_PREFIX = "book." +ModonomiconAPI.ID + ".";
        public static final String ITEM_GROUP = "itemGroup." + ModonomiconAPI.ID;

        public static class Test {
            public static final String TESTBOOK_NAME = BOOK_PREFIX + "test";
            public static final String TESTBOOK_PREFIX = BOOK_PREFIX + "test.";
            public static final String DEFAULT_CATEGORY = TESTBOOK_PREFIX + "default";
            public static final String DEFAULT_CATEGORY_PREFIX = TESTBOOK_PREFIX + "default";
            public static final String DEFAULT_ENTRY1 = DEFAULT_CATEGORY_PREFIX + "default1";
            public static final String DEFAULT_ENTRY2 = DEFAULT_CATEGORY_PREFIX + "default2";
        }
    }
}
