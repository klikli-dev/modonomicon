/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.client.gui.book.markdown;

import com.klikli_dev.modonomicon.book.error.BookErrorManager;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.HoverEvent.Action;
import net.minecraft.network.chat.HoverEvent.ItemStackInfo;
import net.minecraft.network.chat.TextColor;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;
import org.commonmark.node.Link;
import org.commonmark.node.Node;
import org.commonmark.node.Text;

import java.util.function.Consumer;

public class ItemLinkRenderer implements LinkRenderer {

    public static final String PROTOCOL_ITEM = "item://";
    public static final int PROTOCOL_ITEM_LENGTH = PROTOCOL_ITEM.length();

    public static final TextColor ITEM_LINK_COLOR =  TextColor.fromRgb(0x03fc90); //light green


    @Override
    public boolean visit(Link link, Consumer<Node> visitChildren, ComponentNodeRendererContext context) {

        //[](item://minecraft:diamond)
        //[TestText](item://minecraft:emerald)

        if(link.getDestination().startsWith(PROTOCOL_ITEM)){

            BookErrorManager.get().setContext("Item Link: {}, \n{}",
                    link.getDestination(),
                    BookErrorManager.get().getContextHelper()
            );

            var currentColor = context.getCurrentStyle().getColor();


            var itemId = new ResourceLocation(link.getDestination().substring(PROTOCOL_ITEM_LENGTH));
            var item = ForgeRegistries.ITEMS.getHolder(itemId).get();

            //if we have a color we use it, otherwise we use item link default.
            context.setCurrentStyle(context.getCurrentStyle()
                    .withColor(currentColor == null ? ITEM_LINK_COLOR : currentColor)
                    .withHoverEvent(new HoverEvent(Action.SHOW_ITEM, new ItemStackInfo(new ItemStack(item.value()))))
            );

            if(link.getLastChild() == null){
                //if no children, render item name
                link.appendChild(new Text(new TranslatableComponent(item.value().getDescriptionId()).getString()));
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
