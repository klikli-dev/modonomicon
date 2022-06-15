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
import com.klikli_dev.modonomicon.book.conditions.context.BookConditionContext;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.player.Player;

import java.util.ArrayList;
import java.util.List;

public class BookAndCondition extends BookCondition {

    protected BookCondition[] children;

    protected List<Component> tooltips;

    public BookAndCondition(Component component, BookCondition[] children) {
        super(component);
        if (children == null || children.length == 0)
            throw new IllegalArgumentException("AndCondition must have at least one child.");
        this.children = children;

        this.tooltips = new ArrayList<>();
        this.tooltips.add(component);
        for (var child : children) {
            this.tooltips.addAll(child.getTooltip());
        }
    }

    public static BookAndCondition fromJson(JsonObject json) {
        var children = new ArrayList<BookCondition>();
        for (var j : GsonHelper.getAsJsonArray(json, "children")) {
            if (!j.isJsonObject())
                throw new JsonSyntaxException("Condition children must be an array of JsonObjects.");
            children.add(BookCondition.fromJson(j.getAsJsonObject()));
        }
        var tooltip = tooltipFromJson(json);
        return new BookAndCondition(tooltip, children.toArray(new BookCondition[children.size()]));
    }

    public static BookAndCondition fromNetwork(FriendlyByteBuf buffer) {
        var tooltip = buffer.readComponent();
        var childCount = buffer.readVarInt();
        var children = new BookCondition[childCount];
        for (var i = 0; i < childCount; i++) {
            children[i] = BookCondition.fromNetwork(buffer);
        }
        return new BookAndCondition(tooltip, children);
    }

    @Override
    public ResourceLocation getType() {
        return Condition.AND;
    }

    @Override
    public void toNetwork(FriendlyByteBuf buffer) {
        buffer.writeComponent(this.tooltip);
        buffer.writeVarInt(this.children.length);
        for (var child : this.children) {
            child.toNetwork(buffer);
        }
    }

    @Override
    public boolean test(BookConditionContext context, Player player) {
        for (var child : this.children) {
            if (!child.test(context, player))
                return false;
        }
        return true;
    }

    @Override
    public List<Component> getTooltip() {
        return this.tooltips;
    }
}
