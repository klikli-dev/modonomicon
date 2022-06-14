/*
 *
 *  * SPDX-FileCopyrightText: 2022 klikli-dev
 *  *
 *  * SPDX-License-Identifier: MIT
 *
 */

package com.klikli_dev.modonomicon.book.conditions;

import com.google.gson.JsonObject;
import com.klikli_dev.modonomicon.book.Book;
import com.klikli_dev.modonomicon.data.LoaderRegistry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.player.Player;

public abstract class BookCondition {

    public abstract ResourceLocation getType();

    public abstract void toNetwork(FriendlyByteBuf buffer);

    public abstract boolean test(Book book, Player player);

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
}
