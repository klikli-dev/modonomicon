// SPDX-FileCopyrightText: 2023 klikli-dev
//
// SPDX-License-Identifier: MIT

package com.klikli_dev.modonomicon.api.datagen;

import com.klikli_dev.modonomicon.client.gui.book.theme.GuiTexture;
import net.minecraft.resources.Identifier;

public class EntryBackground {
    public static final GuiTexture DEFAULT = new GuiTexture(Identifier.fromNamespaceAndPath("modonomicon", "entry_background_0_0"), 26, 26);
    public static final GuiTexture CONDITION = new GuiTexture(Identifier.fromNamespaceAndPath("modonomicon", "entry_background_1_0"), 26, 26);
    public static final GuiTexture CATEGORY_START = new GuiTexture(Identifier.fromNamespaceAndPath("modonomicon", "entry_background_0_1"), 26, 26);
    public static final GuiTexture LINK = new GuiTexture(Identifier.fromNamespaceAndPath("modonomicon", "entry_background_0_2"), 26, 26);
    public static final GuiTexture LINK_TO_CATEGORY = new GuiTexture(Identifier.fromNamespaceAndPath("modonomicon", "entry_background_1_1"), 26, 26);
}
