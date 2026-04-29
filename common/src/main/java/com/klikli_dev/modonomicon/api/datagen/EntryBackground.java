// SPDX-FileCopyrightText: 2023 klikli-dev
//
// SPDX-License-Identifier: MIT

package com.klikli_dev.modonomicon.api.datagen;

import com.klikli_dev.modonomicon.Modonomicon;
import com.klikli_dev.modonomicon.client.gui.book.theme.GuiSprite;
import net.minecraft.resources.Identifier;

public class EntryBackground {
    private static final String DEFAULT_THEME_ENTRY_BACKGROUND_PATH = Modonomicon.MOD_ID + "/themes/default/node/entry_backgrounds/";

    public static final GuiSprite SQUARE_GOLD = entryBackground("square_gold");
    public static final GuiSprite SQUARE_GRAY = entryBackground("square_gray");
    public static final GuiSprite STAR_GOLD = entryBackground("star_gold");
    public static final GuiSprite STAR_GRAY = entryBackground("star_gray");
    public static final GuiSprite CIRCLE_GOLD = entryBackground("circle_gold");
    public static final GuiSprite CIRCLE_GRAY = entryBackground("circle_gray");

    public static final GuiSprite DEFAULT = SQUARE_GOLD;
    public static final GuiSprite CONDITION = SQUARE_GRAY;
    public static final GuiSprite CATEGORY_START = STAR_GOLD;
    public static final GuiSprite LINK = CIRCLE_GOLD;
    public static final GuiSprite LINK_TO_CATEGORY = STAR_GRAY;

    private static GuiSprite entryBackground(String name) {
        return new GuiSprite(Modonomicon.loc( DEFAULT_THEME_ENTRY_BACKGROUND_PATH + name), 26, 26);
    }
}

