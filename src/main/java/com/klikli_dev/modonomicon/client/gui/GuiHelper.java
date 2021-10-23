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
import com.klikli_dev.modonomicon.api.ModonimiconConstants.I18n;
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
        var cat1 = new BookCategory(
                Modonomicon.loc("cat1"),
                I18n.BOOK_PREFIX + "cat1",
                0,
                new HashMap<>(),
                new BookIcon(new ItemStack(Items.EMERALD)),
                Modonomicon.loc("textures/gui/default_background.png"),
                Modonomicon.loc("textures/gui/entry_textures.png")
        );

        var cat2 = new BookCategory(
                Modonomicon.loc("cat2"),
                I18n.BOOK_PREFIX + "cat2",
                0,
                new HashMap<>(),
                new BookIcon(new ItemStack(Items.IRON_AXE)),
                Modonomicon.loc("textures/gui/default_background.png"),
                Modonomicon.loc("textures/gui/entry_textures.png")
        );

        var cat1Entry1 = new BookEntry(
                Modonomicon.loc("cat1.entry1"),
                cat1,
                new ArrayList<>(),
                I18n.BOOK_PREFIX + "cat1.entry1",
                new BookIcon(new ItemStack(Items.APPLE)),
                -5, -5
        );
        cat1.addEntry(cat1Entry1);

        var cat1Entry2 = new BookEntry(
                Modonomicon.loc("cat1.entry2"),
                cat1,
                new ArrayList<>(),
                I18n.BOOK_PREFIX + "cat1.entry2",
                new BookIcon(new ItemStack(Items.DIAMOND)),
                0, 0
        );
        cat1.addEntry(cat1Entry2);
        cat1Entry2.getParents().add(new BookEntryParent(cat1Entry1));

        var cat2Entry1 = new BookEntry(
                Modonomicon.loc("cat2.entry1"),
                cat1,
                new ArrayList<>(),
                I18n.BOOK_PREFIX + "cat2.entry1",
                new BookIcon(new ItemStack(Items.ACACIA_SAPLING)),
                1, 2
        );
        cat2.addEntry(cat2Entry1);

        var cat2Entry2 = new BookEntry(
                Modonomicon.loc("cat2.entry2"),
                cat1,
                new ArrayList<>(),
                I18n.BOOK_PREFIX + "cat2.entry2",
                new BookIcon(new ItemStack(Items.ANVIL)),
                0, 0
        );
        cat2.addEntry(cat2Entry2);
        cat2Entry2.getParents().add(new BookEntryParent(cat2Entry1));

        Minecraft.getInstance().setScreen(new BookScreen(new Book(
                Modonomicon.loc("test"),
                I18n.BOOK_PREFIX + "test",
                Modonomicon.loc("textures/gui/book.png"),
                new ImmutableMap.Builder<ResourceLocation, BookCategory>()
                        .put(cat1.getId(), cat1)
                        .put(cat2.getId(), cat2)
                        .build()
        ), stack));
    }
}
