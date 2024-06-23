// SPDX-FileCopyrightText: 2024 klikli-dev
//
// SPDX-License-Identifier: MIT

package com.klikli_dev.modonomicon.client.gui.book.entry.linkhandler;

import com.klikli_dev.modonomicon.Modonomicon;
import com.klikli_dev.modonomicon.client.gui.BookGuiManager;
import com.klikli_dev.modonomicon.client.gui.book.entry.BookEntryScreen;
import com.klikli_dev.modonomicon.client.gui.book.markdown.ItemLinkRenderer;
import com.klikli_dev.modonomicon.integration.ModonomiconJeiIntegration;
import com.mojang.brigadier.StringReader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.commands.arguments.item.ItemInput;
import net.minecraft.commands.arguments.item.ItemParser;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Style;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class ItemLinkHandler extends LinkHandler {

    private final ItemParser itemParser;

    public ItemLinkHandler(BookEntryScreen screen) {
        super(screen);
        this.itemParser = new ItemParser(Minecraft.getInstance().level.registryAccess());
    }

    @Override
    public ClickResult handleClick(@NotNull Style pStyle) {
        var event = pStyle.getClickEvent();
        if (event == null)
            return ClickResult.UNHANDLED;

        if (event.getAction() != ClickEvent.Action.CHANGE_PAGE)
            return ClickResult.UNHANDLED;

        if (!ItemLinkRenderer.isItemLink(event.getValue()))
            return ClickResult.UNHANDLED;

        if (!ModonomiconJeiIntegration.get().isJeiLoaded())
            return ClickResult.FAILURE;


        var itemStack = ItemStack.EMPTY;
        try {
            var itemId = event.getValue().substring(ItemLinkRenderer.PROTOCOL_ITEM_LENGTH);
            var reader = new StringReader(itemId);
            var itemResult = this.itemParser.parse(reader);
            var itemInput = new ItemInput(itemResult.item(), itemResult.components());
            itemStack = itemInput.createItemStack(1, false);
        } catch (Exception e) {
            Modonomicon.LOG.error("Failed to parse item link: {}", event.getValue(), e);
            return ClickResult.FAILURE;
        }

        BookGuiManager.get().closeScreenStack(this.screen()); //will cause the book to close entirely, and save the open page

        if (Screen.hasShiftDown()) {
            ModonomiconJeiIntegration.get().showUses(itemStack);
        } else {
            ModonomiconJeiIntegration.get().showRecipe(itemStack);
        }

        //TODO: Consider adding logic to restore content screen after JEI gui close
        //      currently only the overview screen is restored (because JEI does not use Forges Gui Stack, only vanilla screen, thus only saves one parent screen)
        //      we could fix that by listening to the Closing event from forge, and in that set the closing time
        //      -> then on init of overview screen, if closing time is < delta, push last content screen from gui manager

        return ClickResult.SUCCESS;
    }
}
