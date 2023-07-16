/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.book.conditions;

import com.google.gson.JsonObject;
import com.klikli_dev.modonomicon.api.ModonomiconConstants.Data.Condition;
import com.klikli_dev.modonomicon.book.conditions.context.BookConditionContext;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

public class BookFalseCondition extends BookCondition {

    public BookFalseCondition(Component component) {
        super(component);
    }

    public static BookFalseCondition fromJson(JsonObject json) {
        var tooltip = tooltipFromJson(json);
        return new BookFalseCondition(tooltip);
    }

    public static BookFalseCondition fromNetwork(FriendlyByteBuf buffer) {
        var tooltip = buffer.readBoolean() ? buffer.readComponent() : null;
        return new BookFalseCondition(tooltip);
    }

    @Override
    public ResourceLocation getType() {
        return Condition.FALSE;
    }

    @Override
    public void toNetwork(FriendlyByteBuf buffer) {
        buffer.writeBoolean(this.tooltip != null);
        if (this.tooltip != null) {
            buffer.writeComponent(this.tooltip);
        }
    }

    @Override
    public boolean test(BookConditionContext context, Player player) {
        return false;
    }
}
