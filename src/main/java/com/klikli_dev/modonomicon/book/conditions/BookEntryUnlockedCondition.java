/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.book.conditions;

import com.google.gson.JsonObject;
import com.klikli_dev.modonomicon.api.ModonimiconConstants.Data.Condition;
import com.klikli_dev.modonomicon.api.ModonimiconConstants.I18n.Tooltips;
import com.klikli_dev.modonomicon.book.conditions.context.BookConditionContext;
import com.klikli_dev.modonomicon.book.conditions.context.BookConditionEntryContext;
import com.klikli_dev.modonomicon.capability.BookUnlockCapability;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.player.Player;

import java.util.List;

public class BookEntryUnlockedCondition extends BookCondition {

    protected ResourceLocation entryId;

    public BookEntryUnlockedCondition(Component tooltip, ResourceLocation entryId) {
        super(tooltip);
        this.entryId = entryId;
    }

    public static BookEntryUnlockedCondition fromJson(JsonObject json) {
        var entryId = new ResourceLocation(GsonHelper.getAsString(json, "entry_id"));

        var tooltip = tooltipFromJson(json);

        return new BookEntryUnlockedCondition(tooltip, entryId);
    }

    public static BookEntryUnlockedCondition fromNetwork(FriendlyByteBuf buffer) {
        var tooltip = buffer.readBoolean() ? buffer.readComponent() : null;
        var entryId = buffer.readResourceLocation();
        return new BookEntryUnlockedCondition(tooltip, entryId);
    }

    @Override
    public ResourceLocation getType() {
        return Condition.ENTRY_UNLOCKED;
    }

    @Override
    public void toNetwork(FriendlyByteBuf buffer) {
        buffer.writeBoolean(this.tooltip != null);
        if (this.tooltip != null) {
            buffer.writeComponent(this.tooltip);
        }
        buffer.writeResourceLocation(this.entryId);
    }

    @Override
    public boolean test(BookConditionContext context, Player player) {
        return BookUnlockCapability.isUnlockedFor(player, context.getBook().getEntry(this.entryId));
    }

    @Override
    public List<Component> getTooltip(BookConditionContext context) {
        if (this.tooltip == null && context instanceof BookConditionEntryContext entryContext) {
            this.tooltip = Component.translatable(Tooltips.CONDITION_ENTRY_UNLOCKED, Component.translatable(entryContext.getBook().getEntry(this.entryId).getName()));
        }
        return super.getTooltip(context);
    }
}
