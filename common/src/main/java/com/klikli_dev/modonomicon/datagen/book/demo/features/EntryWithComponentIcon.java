// SPDX-FileCopyrightText: 2024 klikli-dev
//
// SPDX-License-Identifier: MIT

package com.klikli_dev.modonomicon.datagen.book.demo.features;

import com.klikli_dev.modonomicon.api.datagen.CategoryProvider;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookEntryModel;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookSpotlightPageModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel;
import com.klikli_dev.modonomicon.platform.Services;
import com.klikli_dev.modonomicon.client.gui.book.theme.GuiTexture;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.DyedItemColor;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.enchantment.ItemEnchantments;

import java.util.List;

public class EntryWithComponentIcon extends EntryProvider {
    public static final String ID = "component_icon";

    public EntryWithComponentIcon(CategoryProvider parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("intro", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText())
        );
        this.pageTitle("Component Item Icon Entry");
        this.pageText("""
                This entry has an icon that is an ItemStack with a data component.
                """
        );
    }

    @Override
    protected String entryName() {
        return "Component Item Icon Entry";
    }

    @Override
    protected String entryDescription() {
        return "An entry showcasing item stack icons with data components";
    }

    @Override
    protected GuiTexture entryBackground() {
        return EntryBackground.DEFAULT;
    }

    @Override
    protected BookIconModel entryIcon() {
        var iconStack = new ItemStackTemplate(Items.LEATHER_HELMET, DataComponentPatch.builder().set(DataComponents.DYED_COLOR, new DyedItemColor(0x169C9C)).build());
        return BookIconModel.create(iconStack);
    }

    @Override
    protected BookEntryModel additionalSetup(BookEntryModel entry) {
        return super.additionalSetup(entry);
    }

    @Override
    protected String entryId() {
        return ID;
    }
}
