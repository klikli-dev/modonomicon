/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.book.conditions;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.klikli_dev.modonomicon.api.ModonomiconConstants.Data.Condition;
import com.klikli_dev.modonomicon.book.conditions.context.BookConditionContext;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.player.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BookOrCondition extends BookCondition {

    protected BookCondition[] children;

    protected List<Component> tooltips;

    public BookOrCondition(Component component, BookCondition[] children) {
        super(component);
        if (children == null || children.length == 0)
            throw new IllegalArgumentException("OrCondition must have at least one child.");

        this.children = children;
    }

    public static BookOrCondition fromJson(JsonObject json) {
        var children = new ArrayList<BookCondition>();
        for (var j : GsonHelper.getAsJsonArray(json, "children")) {
            if (!j.isJsonObject())
                throw new JsonSyntaxException("Condition children must be an array of JsonObjects.");
            children.add(BookCondition.fromJson(j.getAsJsonObject()));
        }
        var tooltip = tooltipFromJson(json);
        return new BookOrCondition(tooltip, children.toArray(new BookCondition[children.size()]));
    }

    public static BookOrCondition fromNetwork(FriendlyByteBuf buffer) {
        var tooltip = buffer.readBoolean() ? buffer.readComponent() : null;
        var childCount = buffer.readVarInt();
        var children = new BookCondition[childCount];
        for (var i = 0; i < childCount; i++) {
            children[i] = BookCondition.fromNetwork(buffer);
        }
        return new BookOrCondition(tooltip, children);
    }

    @Override
    public ResourceLocation getType() {
        return Condition.OR;
    }

    @Override
    public boolean requiresMultiPassUnlockTest() {
        return Arrays.stream(this.children).anyMatch(BookCondition::requiresMultiPassUnlockTest);
    }

    public BookCondition[] children() {
        return this.children;
    }

    @Override
    public void toNetwork(FriendlyByteBuf buffer) {
        buffer.writeBoolean(this.tooltip != null);
        if (this.tooltip != null) {
            buffer.writeComponent(this.tooltip);
        }
        buffer.writeVarInt(this.children.length);
        for (var child : this.children) {
            BookCondition.toNetwork(child, buffer);
        }
    }

    @Override
    public boolean test(BookConditionContext context, Player player) {
        for (var child : this.children) {
            if (child.test(context, player))
                return true;
        }
        return false;
    }

    @Override
    public List<Component> getTooltip(BookConditionContext context) {
        if (this.tooltips == null) {
            this.tooltips = new ArrayList<>();
            if (this.tooltip != null)
                this.tooltips.add(this.tooltip);
            for (var child : this.children) {
                this.tooltips.addAll(child.getTooltip(context));
            }
        }


        return this.tooltips != null ? this.tooltips : List.of();
    }
}
