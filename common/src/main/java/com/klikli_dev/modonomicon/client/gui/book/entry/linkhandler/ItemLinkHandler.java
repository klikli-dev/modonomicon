// SPDX-FileCopyrightText: 2024 klikli-dev
//
// SPDX-License-Identifier: MIT

package com.klikli_dev.modonomicon.client.gui.book.entry.linkhandler;

import com.klikli_dev.modonomicon.Modonomicon;
import com.klikli_dev.modonomicon.client.gui.BookGuiManager;
import com.klikli_dev.modonomicon.client.gui.book.entry.BookEntryScreen;
import com.klikli_dev.modonomicon.client.gui.book.markdown.ItemLinkRenderer;
import com.klikli_dev.modonomicon.integration.recipeviewer.RecipeViewerRegistry;
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

        //Item links use OPEN_FILE action as it allows us to hand over a string.
        //the isItemLink below will check for a protocol prefix
        if (event.action() != ClickEvent.Action.OPEN_FILE || !(event instanceof ClickEvent.OpenFile(String path)))
            return ClickResult.UNHANDLED;

        if (!ItemLinkRenderer.isItemLink(path))
            return ClickResult.UNHANDLED;

        if (!RecipeViewerRegistry.isAnyAvailable())
            return ClickResult.FAILURE;


        var itemStack = ItemStack.EMPTY;
        try {
            var itemId = path.substring(ItemLinkRenderer.PROTOCOL_ITEM_LENGTH);
            var reader = new StringReader(itemId);
            var itemResult = this.itemParser.parse(reader);
            var itemInput = new ItemInput(itemResult.item(), itemResult.components());
            itemStack = itemInput.createItemStack(1);
        } catch (Exception e) {
            Modonomicon.LOG.error("Failed to parse item link: {}", path, e);
            return ClickResult.FAILURE;
        }

        final var finalItemStack = itemStack;
        //We deliberately do NOT close the book here:
        //- The recipe viewer opens on top of the current screen and remembers it as its parent, so closing
        //  the viewer (e.g. pressing ESC) returns to the book exactly where the player left off.
        //- If the viewer finds no recipe/usage for the stack it simply does not open, leaving the book open
        //  instead of closing all GUIs.
        BookGuiManager.get().keepMousePosition(() ->
                RecipeViewerRegistry.show(finalItemStack, Minecraft.getInstance().hasShiftDown()));

        return ClickResult.SUCCESS;
    }
}
