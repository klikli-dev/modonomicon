// SPDX-FileCopyrightText: 2023 klikli-dev
//
// SPDX-License-Identifier: MIT

package com.klikli_dev.modonomicon.api.datagen;

import com.klikli_dev.modonomicon.client.gui.book.theme.GuiTexture;
import net.minecraft.resources.Identifier;

public class EntryBackground {
    private static final String DEFAULT_THEME_ENTRY_BACKGROUND_PATH = "modonomicon/themes/default/node/entry_backgrounds/";

    public static final GuiTexture SQUARE_GOLD = entryBackground("square_gold");
    public static final GuiTexture SQUARE_GRAY = entryBackground("square_gray");
    public static final GuiTexture STAR_GOLD = entryBackground("star_gold");
    public static final GuiTexture STAR_GRAY = entryBackground("star_gray");
    public static final GuiTexture CIRCLE_GOLD = entryBackground("circle_gold");
    public static final GuiTexture CIRCLE_GRAY = entryBackground("circle_gray");

    public static final GuiTexture DEFAULT = SQUARE_GOLD;
    public static final GuiTexture CONDITION = SQUARE_GRAY;
    public static final GuiTexture CATEGORY_START = STAR_GOLD;
    public static final GuiTexture LINK = CIRCLE_GOLD;
    public static final GuiTexture LINK_TO_CATEGORY = STAR_GRAY;

    private static GuiTexture entryBackground(String name) {
        return new GuiTexture(Identifier.fromNamespaceAndPath("modonomicon", DEFAULT_THEME_ENTRY_BACKGROUND_PATH + name), 26, 26);
    }
}
