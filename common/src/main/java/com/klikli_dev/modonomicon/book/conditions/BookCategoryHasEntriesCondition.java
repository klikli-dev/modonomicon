/*
 * SPDX-FileCopyrightText: 2024 DaFuqs
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.book.conditions;

import com.google.gson.JsonObject;
import com.klikli_dev.modonomicon.api.ModonomiconConstants;
import com.klikli_dev.modonomicon.book.BookCategory;
import com.klikli_dev.modonomicon.book.conditions.context.BookConditionContext;
import com.klikli_dev.modonomicon.book.conditions.context.BookConditionEntryContext;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.player.Player;

import java.util.List;

public class BookCategoryHasEntriesCondition extends BookCondition {
    
    protected ResourceLocation categoryId;
    
    public BookCategoryHasEntriesCondition(Component tooltip, ResourceLocation categoryId) {
        super(tooltip);
        this.categoryId = categoryId;
    }
    
    public static BookCategoryHasEntriesCondition fromJson(JsonObject json, HolderLookup.Provider provider) {
        ResourceLocation categoryId = new ResourceLocation(GsonHelper.getAsString(json, "category_id"));
        Component tooltip = Component.translatable(ModonomiconConstants.I18n.Tooltips.CONDITION_CATEGORY_HAS_ENTRIES, categoryId);
        return new BookCategoryHasEntriesCondition(tooltip, categoryId);
    }
    @Override
    public void toNetwork(RegistryFriendlyByteBuf buffer) {
        buffer.writeBoolean(this.tooltip != null);
        if (this.tooltip != null) {
            ComponentSerialization.STREAM_CODEC.encode(buffer, this.tooltip);
        }
        buffer.writeResourceLocation(this.categoryId);
    }
    
    public static BookCategoryHasEntriesCondition fromNetwork(RegistryFriendlyByteBuf buffer) {
        var tooltip = buffer.readBoolean() ? ComponentSerialization.STREAM_CODEC.decode(buffer) : null;
        var entryId = buffer.readResourceLocation();
        return new BookCategoryHasEntriesCondition(tooltip, entryId);
    }
    
    @Override
    public ResourceLocation getType() {
        return ModonomiconConstants.Data.Condition.CATEGORY_HAS_ENTRIES;
    }
    
    @Override
    public boolean test(BookConditionContext context, Player player) {
        BookCategory category = context.book.getCategory(this.categoryId);
        if (category == null) {
            throw new IllegalArgumentException("Category with id " + this.categoryId + " not found in book " + context.getBook().getId() + "for BookCategoryHasEntriesCondition. This happened while trying to unlock " + context);
        }
        return category.getEntries().size() > 0;
    }
    
    @Override
    public List<Component> getTooltip(Player player, BookConditionContext context) {
        if (this.tooltip == null && context instanceof BookConditionEntryContext entryContext) {
            this.tooltip = Component.translatable(ModonomiconConstants.I18n.Tooltips.CONDITION_ENTRY_READ, Component.translatable(entryContext.getBook().getEntry(this.categoryId).getName()));
        }
        return super.getTooltip(player, context);
    }
}
