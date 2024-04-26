/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.client.gui.book.markdown;

import com.klikli_dev.modonomicon.book.error.BookErrorManager;
import com.mojang.brigadier.StringReader;
import net.minecraft.commands.arguments.item.ItemInput;
import net.minecraft.commands.arguments.item.ItemParser;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.HoverEvent.Action;
import net.minecraft.network.chat.HoverEvent.ItemStackInfo;
import net.minecraft.network.chat.TextColor;
import net.minecraft.world.item.ItemStack;
import org.commonmark.node.Link;
import org.commonmark.node.Node;
import org.commonmark.node.Text;

import java.util.function.Consumer;

public class ItemLinkRenderer implements LinkRenderer {

    public static final String PROTOCOL_ITEM = "item://";
    public static final int PROTOCOL_ITEM_LENGTH = PROTOCOL_ITEM.length();

    public static final TextColor ITEM_LINK_COLOR = TextColor.fromRgb(0x029e5a); //light green

    public static boolean isItemLink(String linkText) {
        return linkText.toLowerCase().startsWith(PROTOCOL_ITEM);
    }

    @Override
    public boolean visit(Link link, Consumer<Node> visitChildren, ComponentNodeRendererContext context) {

        //[](item://minecraft:diamond)
        //[TestText](item://minecraft:emerald)

        if (link.getDestination().startsWith(PROTOCOL_ITEM)) {

            BookErrorManager.get().setContext("Item Link: {}, \n{}",
                    link.getDestination(),
                    BookErrorManager.get().getContextHelper()
            );

            var currentColor = context.getCurrentStyle().getColor();


            var itemParser = new ItemParser(context.getProvider());
            var itemStack = ItemStack.EMPTY;
            try {
                var itemId = link.getDestination().substring(PROTOCOL_ITEM_LENGTH);
                var reader = new StringReader(itemId);
                var itemResult = itemParser.parse(reader);
                var itemInput = new ItemInput(itemResult.item(), itemResult.components());
                itemStack = itemInput.createItemStack(1, false);
            } catch (Exception e) {
                BookErrorManager.get().error("Failed to parse item link.", e);
            }

            //if we have a color we use it, otherwise we use item link default.
            context.setCurrentStyle(context.getCurrentStyle()
                    .withColor(currentColor == null ? ITEM_LINK_COLOR : currentColor)
                    .withHoverEvent(new HoverEvent(Action.SHOW_ITEM, new ItemStackInfo(itemStack)))
                    .withClickEvent(new ClickEvent(ClickEvent.Action.CHANGE_PAGE, link.getDestination()))
            );

            //TODO: show usage infos -> shift to show usage, click to show recipe

            if (link.getLastChild() == null) {
                //if no children, render item name
                link.appendChild(new Text(Component.translatable(itemStack.getItem().getDescriptionId()).getString()));
            }

            visitChildren.accept(link);


            //links are not style instructions, so we reset to our previous color.
            context.setCurrentStyle(context.getCurrentStyle()
                    .withColor(currentColor)
                    .withHoverEvent(null)
            );

            BookErrorManager.get().setContext(null);

            return true;
        }
        return false;
    }
}
