/*
 * SPDX-FileCopyrightText: 2024 DaFuqs
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.book.conditions;

import com.google.gson.JsonObject;
import com.klikli_dev.modonomicon.api.ModonomiconConstants;
import com.klikli_dev.modonomicon.Modonomicon;
import com.klikli_dev.modonomicon.book.conditions.context.BookConditionContext;
import com.klikli_dev.modonomicon.book.conditions.context.BookConditionEntryContext;
import com.klikli_dev.modonomicon.book.error.BookErrorManager;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.player.Player;

import java.util.List;

/**
 * This BookCondition evaluates to true
 * if a category has at least
 */
public class BookCategoryHasVisibleEntriesCondition extends BookCondition {
    
    protected ResourceLocation categoryId;
    
    public BookCategoryHasVisibleEntriesCondition(Component tooltip, ResourceLocation categoryId) {
        super(tooltip);
        this.categoryId = categoryId;
    }
    
    public static BookCategoryHasVisibleEntriesCondition fromJson(ResourceLocation conditionParentId, JsonObject json, HolderLookup.Provider provider) {
        var categoryPath = GsonHelper.getAsString(json, "category_id");
        var categoryId = categoryPath.contains(":") ?
                ResourceLocation.parse(categoryPath) :
                ResourceLocation.fromNamespaceAndPath(conditionParentId.getNamespace(), categoryPath);

        Component tooltip = Component.translatable(ModonomiconConstants.I18n.Tooltips.CONDITION_CATEGORY_HAS_VISIBLE_ENTRIES, categoryId.toLanguageKey());
        return new BookCategoryHasVisibleEntriesCondition(tooltip, categoryId);
    }

    @Override
    public void toNetwork(RegistryFriendlyByteBuf buffer) {
        buffer.writeBoolean(this.tooltip != null);
        if (this.tooltip != null) {
            ComponentSerialization.STREAM_CODEC.encode(buffer, this.tooltip);
        }
        buffer.writeResourceLocation(this.categoryId);
    }
    
    public static BookCategoryHasVisibleEntriesCondition fromNetwork(RegistryFriendlyByteBuf buffer) {
        var tooltip = buffer.readBoolean() ? ComponentSerialization.STREAM_CODEC.decode(buffer) : null;
        var entryId = buffer.readResourceLocation();
        return new BookCategoryHasVisibleEntriesCondition(tooltip, entryId);
    }
    
    @Override
    public ResourceLocation getType() {
        return ModonomiconConstants.Data.Condition.CATEGORY_HAS_VISIBLE_ENTRIES;
    }
    
    @Override
    public boolean test(BookConditionContext context, Player player) {
        if (context == null || context.book == null) {
            //Can happen if the book was not (yet) built, e.g. after /reload before books are rebuilt,
            //or if book loading failed. Never crash a server tick over this, fail closed and log.
            Modonomicon.LOG.error("BookCategoryHasVisibleEntriesCondition tested with null book for category '{}'. The book was likely not built yet (e.g. after /reload) or failed to load. Returning false to avoid a crash.", this.categoryId);
            return false;
        }

        var category = context.book.getCategory(this.categoryId);
        if (category == null) {
            //Do not throw here: this runs during server ticks and would crash the server.
            //Instead record a book error so the book shows an error screen, log, and fail closed.
            var message = "Category with id " + this.categoryId + " not found in book " + context.getBook().getId() + " for BookCategoryHasVisibleEntriesCondition. This happened while trying to unlock " + context;
            Modonomicon.LOG.error(message);
            BookErrorManager.get().error(context.getBook().getId(), message);
            return false;
        }

        if(category.getEntries().isEmpty()) {
            return false;
        }

        for(var entry : category.getEntries().values()) {
            if(entry.getEntryDisplayState(player).isVisible()) {
                return true;
            }
        }

        return false;
    }
    
    @Override
    public List<Component> getTooltip(Player player, BookConditionContext context) {
        if (this.tooltip == null && context instanceof BookConditionEntryContext entryContext
                && entryContext.getBook() != null && entryContext.getBook().getEntry(this.categoryId) != null) {
            this.tooltip = Component.translatable(ModonomiconConstants.I18n.Tooltips.CONDITION_CATEGORY_HAS_VISIBLE_ENTRIES, Component.translatable(entryContext.getBook().getEntry(this.categoryId).getName()));
        }
        return super.getTooltip(player, context);
    }
}
