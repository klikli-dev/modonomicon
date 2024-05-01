/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.book.entries;

import com.google.gson.JsonObject;
import com.klikli_dev.modonomicon.api.ModonomiconConstants;
import com.klikli_dev.modonomicon.book.BookCategory;
import com.klikli_dev.modonomicon.book.error.BookErrorManager;
import com.klikli_dev.modonomicon.client.gui.book.BookCategoryScreen;
import com.klikli_dev.modonomicon.client.gui.book.BookContentScreen;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.Level;

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

    public static CategoryLinkBookEntry fromJson(ResourceLocation id, JsonObject json, boolean autoAddReadConditions, HolderLookup.Provider provider) {
        BookEntryData data = BookEntryData.fromJson(json, autoAddReadConditions, provider);

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

    public static CategoryLinkBookEntry fromNetwork(RegistryFriendlyByteBuf buffer) {
        var id = buffer.readResourceLocation();
        BookEntryData data = BookEntryData.fromNetwork(buffer);
        ResourceLocation commandToRunOnFirstReadId = buffer.readNullable(FriendlyByteBuf::readResourceLocation);
        ResourceLocation categoryToOpen = buffer.readResourceLocation();

        return new CategoryLinkBookEntry(id, data, commandToRunOnFirstReadId, categoryToOpen);
    }

    @Override
    public ResourceLocation getType() {
        return ModonomiconConstants.Data.EntryType.CATEGORY_LINK;
    }

    @Override
    public void toNetwork(RegistryFriendlyByteBuf buffer) {
        buffer.writeResourceLocation(this.id);
        this.data.toNetwork(buffer);
        buffer.writeNullable(this.commandToRunOnFirstReadId, FriendlyByteBuf::writeResourceLocation);
        buffer.writeResourceLocation(this.categoryToOpenId);
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
        categoryScreen.getBookOverviewScreen().changeCategory(this.getCategoryToOpen());
        return null;
    }

}
