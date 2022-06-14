/*
 *
 *  * SPDX-FileCopyrightText: 2022 klikli-dev
 *  *
 *  * SPDX-License-Identifier: MIT
 *
 */

package com.klikli_dev.modonomicon.book.conditions;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.klikli_dev.modonomicon.api.ModonimiconConstants.Data.Condition;
import com.klikli_dev.modonomicon.book.Book;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.player.Player;

import java.util.ArrayList;

public class BookAndCondition extends BookCondition {

    protected BookCondition[] children;

    public BookAndCondition(BookCondition[] children) {
        if (children == null || children.length == 0)
            throw new IllegalArgumentException("AndCondition must have at least one child.");

        this.children = children;
    }

    public static BookAndCondition fromJson(JsonObject json) {
        var children = new ArrayList<BookCondition>();
        for (var j : GsonHelper.getAsJsonArray(json, "children")) {
            if (!j.isJsonObject())
                throw new JsonSyntaxException("Condition children must be an array of JsonObjects.");
            children.add(BookCondition.fromJson(j.getAsJsonObject()));
        }
        return new BookAndCondition(children.toArray(new BookCondition[children.size()]));
    }

    public static BookAndCondition fromNetwork(FriendlyByteBuf buffer) {
        var childCount = buffer.readVarInt();
        var children = new BookCondition[childCount];
        for (var i = 0; i < childCount; i++) {
            children[i] = BookCondition.fromNetwork(buffer);
        }
        return new BookAndCondition(children);
    }

    @Override
    public ResourceLocation getType() {
        return Condition.AND;
    }

    @Override
    public void toNetwork(FriendlyByteBuf buffer) {
        buffer.writeVarInt(this.children.length);
        for (var child : this.children) {
            child.toNetwork(buffer);
        }
    }

    @Override
    public boolean test(Book book, Player player) {
        for (var child : this.children) {
            if (!child.test(book, player))
                return false;
        }
        return true;
    }
}
