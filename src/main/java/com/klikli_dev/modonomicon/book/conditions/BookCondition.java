/*
 *
 *  * SPDX-FileCopyrightText: 2022 klikli-dev
 *  *
 *  * SPDX-License-Identifier: MIT
 *
 */

package com.klikli_dev.modonomicon.book.conditions;

import com.google.gson.JsonObject;
import com.klikli_dev.modonomicon.book.conditions.context.BookConditionContext;
import com.klikli_dev.modonomicon.data.LoaderRegistry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.player.Player;

import java.util.List;

public abstract class BookCondition {

    protected Component tooltip;

    public BookCondition(Component tooltip) {
        this.tooltip = tooltip;
    }

    public static MutableComponent tooltipFromJson(JsonObject json) {
        var tooltipElement = json.get("tooltip");
        if (tooltipElement.isJsonPrimitive())
            return Component.translatable(tooltipElement.getAsString());

        return Component.Serializer.fromJson(tooltipElement);
    }

    public static BookCondition fromJson(JsonObject json) {
        var type = new ResourceLocation(GsonHelper.getAsString(json, "type"));
        var loader = LoaderRegistry.getConditionJsonLoader(type);
        return loader.fromJson(json);
    }

    public static BookCondition fromNetwork(FriendlyByteBuf buf) {
        var type = buf.readResourceLocation();
        var loader = LoaderRegistry.getConditionNetworkLoader(type);
        return loader.fromNetwork(buf);
    }

    public abstract ResourceLocation getType();

    public abstract void toNetwork(FriendlyByteBuf buffer);

    public abstract boolean test(BookConditionContext context, Player player);

    public List<Component> getTooltip() {
        return List.of(this.tooltip);
    }
}
