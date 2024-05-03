/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.book.entries;

import com.google.gson.*;
import com.klikli_dev.modonomicon.api.*;
import com.klikli_dev.modonomicon.book.*;
import com.klikli_dev.modonomicon.book.conditions.*;
import com.klikli_dev.modonomicon.book.error.*;
import com.klikli_dev.modonomicon.client.gui.book.*;
import net.minecraft.network.*;
import net.minecraft.resources.*;
import net.minecraft.util.*;
import net.minecraft.world.level.*;

import java.util.*;

public class CategoryLinkBookEntry extends BookEntry {
    
    /**
     * The category to open on click
     */
    protected ResourceLocation categoryToOpenId;
    protected BookCategory categoryToOpen;
    
    public CategoryLinkBookEntry(ResourceLocation id, BookEntryData data, ResourceLocation commandToRunOnFirstReadId, ResourceLocation categoryToOpenId) {
        super(id, data, commandToRunOnFirstReadId);
        this.categoryToOpenId = categoryToOpenId;
    }
    
    @Override
    public ResourceLocation getType() {
        return ModonomiconConstants.Data.EntryType.CATEGORY_LINK;
    }
    
    public static CategoryLinkBookEntry fromJson(ResourceLocation id, JsonObject json, boolean autoAddReadConditions) {
        BookEntryData data = BookEntryData.fromJson(json, autoAddReadConditions);
        
        ResourceLocation commandToRunOnFirstReadId = null;
        if (json.has("command_to_run_on_first_read")) {
            commandToRunOnFirstReadId = new ResourceLocation(GsonHelper.getAsString(json, "command_to_run_on_first_read"));
        }

        ResourceLocation categoryToOpenId = null;
        if (json.has("category_to_open")) {
            categoryToOpenId = new ResourceLocation(GsonHelper.getAsString(json, "category_to_open"));
        }

        return new CategoryLinkBookEntry(id, data, commandToRunOnFirstReadId, categoryToOpenId);
    }

    @Override
    public void toNetwork(FriendlyByteBuf buffer) {
        buffer.writeResourceLocation(this.id);
        this.data.toNetwork(buffer);
        buffer.writeNullable(this.commandToRunOnFirstReadId, FriendlyByteBuf::writeResourceLocation);
        buffer.writeResourceLocation(this.categoryToOpenId);
    }
    
    public static CategoryLinkBookEntry fromNetwork(FriendlyByteBuf buffer) {
        var id = buffer.readResourceLocation();
        BookEntryData data = BookEntryData.fromNetwork(buffer);
        ResourceLocation commandToRunOnFirstReadId = buffer.readNullable(FriendlyByteBuf::readResourceLocation);
        ResourceLocation categoryToOpen = buffer.readResourceLocation();

        return new CategoryLinkBookEntry(id, data, commandToRunOnFirstReadId, categoryToOpen);
    }
    
    @Override
    public void build(Level level, BookCategory category) {
        super.build(level, category);
        
        if (this.categoryToOpenId != null) {
            this.categoryToOpen = this.getBook().getCategory(this.categoryToOpenId);
            
            if (this.categoryToOpen == null) {
                BookErrorManager.get().error("Category to open \"" + this.categoryToOpenId + "\" does not exist in this book. Set to null.");
                this.categoryToOpenId = null;
            }
        }
    }

    public BookCategory getCategoryToOpen() {
        return this.categoryToOpen;
    }
    
    public BookContentScreen openEntry(BookCategoryScreen categoryScreen) {
        categoryScreen.getBookOverviewScreen().changeCategory(getCategoryToOpen());
        return null;
    }
    
}
