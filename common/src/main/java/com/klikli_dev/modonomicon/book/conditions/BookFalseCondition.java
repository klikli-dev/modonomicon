/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.book.conditions;

import com.google.gson.JsonObject;
import com.klikli_dev.modonomicon.api.ModonomiconConstants.Data.Condition;
import com.klikli_dev.modonomicon.book.conditions.context.BookConditionContext;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

public class BookFalseCondition extends BookCondition {

    public BookFalseCondition(Component component) {
        super(component);
    }

    public static BookFalseCondition fromJson(JsonObject json, HolderLookup.Provider provider) {
        var tooltip = tooltipFromJson(json, provider);
        return new BookFalseCondition(tooltip);
    }

    public static BookFalseCondition fromNetwork(RegistryFriendlyByteBuf buffer) {
        var tooltip = buffer.readBoolean() ? ComponentSerialization.STREAM_CODEC.decode(buffer) : null;
        return new BookFalseCondition(tooltip);
    }

    @Override
    public ResourceLocation getType() {
        return Condition.FALSE;
    }

    @Override
    public void toNetwork(RegistryFriendlyByteBuf buffer) {
        buffer.writeBoolean(this.tooltip != null);
        if (this.tooltip != null) {
            ComponentSerialization.STREAM_CODEC.encode(buffer, this.tooltip);
        }
    }

    @Override
    public boolean test(BookConditionContext context, Player player) {
        return false;
    }
}
