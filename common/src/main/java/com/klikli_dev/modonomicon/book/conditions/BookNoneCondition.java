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

/**
 * The default condition - equivalent to the True condition, but will be replaced by AddAutoReadConditions
 */
public class BookNoneCondition extends BookCondition {

    public BookNoneCondition() {
        this(null);
    }

    public BookNoneCondition(Component component) {
        super(component);
    }

    public static BookNoneCondition fromJson(JsonObject json, HolderLookup.Provider provider) {
        var tooltip = tooltipFromJson(json, provider);
        return new BookNoneCondition(tooltip);
    }

    public static BookNoneCondition fromNetwork(RegistryFriendlyByteBuf buffer) {
        var tooltip = buffer.readBoolean() ? ComponentSerialization.STREAM_CODEC.decode(buffer) : null;
        return new BookNoneCondition(tooltip);
    }

    @Override
    public ResourceLocation getType() {
        return Condition.NONE;
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
        return true;
    }
}
