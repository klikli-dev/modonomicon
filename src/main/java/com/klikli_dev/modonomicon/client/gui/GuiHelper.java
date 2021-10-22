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

package com.klikli_dev.modonomicon.client.gui;

import com.google.common.collect.ImmutableMap;
import com.klikli_dev.modonomicon.Modonomicon;
import com.klikli_dev.modonomicon.api.ModonimiconConstants;
import com.klikli_dev.modonomicon.client.gui.book.BookScreen;
import com.klikli_dev.modonomicon.data.book.*;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.ArrayList;
import java.util.HashMap;

public class GuiHelper {
    public static void openBook(ItemStack stack) {
        var defaultCat = new BookCategory(
                Modonomicon.loc("default"),
                ModonimiconConstants.I18n.Test.DEFAULT_CATEGORY,
                0,
                new HashMap<>(),
                Modonomicon.loc("textures/gui/default_background.png")
        );

        var defaultEntry1 = new BookEntry(
                Modonomicon.loc("default1"),
                defaultCat,
                new ArrayList<>(),
                ModonimiconConstants.I18n.Test.DEFAULT_ENTRY1,
                new BookIcon(new ItemStack(Items.APPLE)),
                -5, -5
        );
        defaultCat.addEntry(defaultEntry1);

        var defaultEntry2 = new BookEntry(
                Modonomicon.loc("default2"),
                defaultCat,
                new ArrayList<>(),
                ModonimiconConstants.I18n.Test.DEFAULT_ENTRY2,
                new BookIcon(new ItemStack(Items.DIAMOND)),
                0, 0
        );
        defaultCat.addEntry(defaultEntry2);

        defaultEntry2.getParents().add(new BookEntryParent(defaultEntry1));

        Minecraft.getInstance().setScreen(new BookScreen(new Book(
                Modonomicon.loc("test"),
                ModonimiconConstants.I18n.Test.TESTBOOK_NAME,
                Modonomicon.loc("textures/gui/book.png"),
                new ImmutableMap.Builder<ResourceLocation, BookCategory>().put(defaultCat.getId(), defaultCat).build()
        ), stack));
    }
}
