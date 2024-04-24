/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.book.conditions;

import com.google.gson.JsonObject;
import com.klikli_dev.modonomicon.api.ModonomiconConstants.Data.Condition;
import com.klikli_dev.modonomicon.api.ModonomiconConstants.I18n.Tooltips;
import com.klikli_dev.modonomicon.book.conditions.context.BookConditionContext;
import com.klikli_dev.modonomicon.book.conditions.context.BookConditionEntryContext;
import com.klikli_dev.modonomicon.bookstate.BookUnlockStateManager;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.player.Player;

import java.util.List;

public class BookEntryReadCondition extends BookCondition {

    protected ResourceLocation entryId;

    public BookEntryReadCondition(Component tooltip, ResourceLocation entryId) {
        super(tooltip);
        this.entryId = entryId;
    }

    public static BookEntryReadCondition fromJson(JsonObject json, HolderLookup.Provider provider) {
        var entryId = new ResourceLocation(GsonHelper.getAsString(json, "entry_id"));

        var tooltip = tooltipFromJson(json, provider);

        return new BookEntryReadCondition(tooltip, entryId);
    }

    public static BookEntryReadCondition fromNetwork(RegistryFriendlyByteBuf buffer) {
        var tooltip = buffer.readBoolean() ? ComponentSerialization.STREAM_CODEC.decode(buffer) : null;
        var entryId = buffer.readResourceLocation();
        return new BookEntryReadCondition(tooltip, entryId);
    }

    @Override
    public ResourceLocation getType() {
        return Condition.ENTRY_READ;
    }

    @Override
    public void toNetwork(RegistryFriendlyByteBuf buffer) {
        buffer.writeBoolean(this.tooltip != null);
        if (this.tooltip != null) {
            ComponentSerialization.STREAM_CODEC.encode(buffer, this.tooltip);
        }
        buffer.writeResourceLocation(this.entryId);
    }

    @Override
    public boolean test(BookConditionContext context, Player player) {
        var entry = context.getBook().getEntry(this.entryId);
        if (entry == null) {
            throw new IllegalArgumentException("Entry with id " + this.entryId + " not found in book " + context.getBook().getId() + "for BookEntryReadCondition. This happened while trying to unlock " + context);
        }
        return BookUnlockStateManager.get().isReadFor(player, entry);
    }

    @Override
    public List<Component> getTooltip(Player player, BookConditionContext context) {
        if (this.tooltip == null && context instanceof BookConditionEntryContext entryContext) {
            this.tooltip = Component.translatable(Tooltips.CONDITION_ENTRY_READ, Component.translatable(entryContext.getBook().getEntry(this.entryId).getName()));
        }
        return super.getTooltip(player, context);
    }
}
