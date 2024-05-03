/*
 * SPDX-FileCopyrightText: 2024 DaFuqs
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.book.conditions;

import com.google.gson.*;
import com.klikli_dev.modonomicon.api.*;
import com.klikli_dev.modonomicon.book.*;
import com.klikli_dev.modonomicon.book.conditions.context.*;
import net.minecraft.network.*;
import net.minecraft.network.chat.*;
import net.minecraft.resources.*;
import net.minecraft.util.*;
import net.minecraft.world.entity.player.*;

import java.util.*;

public class BookCategoryHasEntriesCondition extends BookCondition {
    
    protected ResourceLocation categoryId;
    
    public BookCategoryHasEntriesCondition(Component tooltip, ResourceLocation categoryId) {
        super(tooltip);
        this.categoryId = categoryId;
    }
    
    public static BookCategoryHasEntriesCondition fromJson(JsonObject json) {
        ResourceLocation categoryId = new ResourceLocation(GsonHelper.getAsString(json, "category_id"));
        Component tooltip = Component.translatable(ModonomiconConstants.I18n.Tooltips.CONDITION_CATEGORY_HAS_ENTRIES, categoryId);
        return new BookCategoryHasEntriesCondition(tooltip, categoryId);
    }
    @Override
    public void toNetwork(FriendlyByteBuf buffer) {
        buffer.writeBoolean(this.tooltip != null);
        if (this.tooltip != null) {
            buffer.writeComponent(this.tooltip);
        }
        buffer.writeResourceLocation(this.categoryId);
    }
    
    public static BookCategoryHasEntriesCondition fromNetwork(FriendlyByteBuf buffer) {
        var tooltip = buffer.readBoolean() ? buffer.readComponent() : null;
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
