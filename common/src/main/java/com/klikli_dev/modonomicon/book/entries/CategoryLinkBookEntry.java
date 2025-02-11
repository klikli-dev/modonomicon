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
import com.klikli_dev.modonomicon.client.gui.BookGuiManager;
import com.klikli_dev.modonomicon.client.gui.book.BookAddress;
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
        BookEntryData data = BookEntryData.fromJson(id, json, autoAddReadConditions, provider);

        ResourceLocation commandToRunOnFirstReadId = null;
        if (json.has("command_to_run_on_first_read")) {
            commandToRunOnFirstReadId = ResourceLocation.parse(GsonHelper.getAsString(json, "command_to_run_on_first_read"));
        }

        var categoryToOpenIdPath = GsonHelper.getAsString(json, "category_to_open");
        var categoryToOpenId = categoryToOpenIdPath.contains(":") ?
                ResourceLocation.parse(categoryToOpenIdPath) :
                ResourceLocation.fromNamespaceAndPath(id.getNamespace(), categoryToOpenIdPath);

        return new CategoryLinkBookEntry(id, data, commandToRunOnFirstReadId, categoryToOpenId);
    }

    public static CategoryLinkBookEntry fromNetwork(RegistryFriendlyByteBuf buffer) {
        var id = buffer.readResourceLocation();
        BookEntryData data = BookEntryData.fromNetwork(buffer);
        ResourceLocation commandToRunOnFirstReadId = buffer.readNullable(FriendlyByteBuf::readResourceLocation);
        ResourceLocation categoryToOpen = buffer.readNullable(FriendlyByteBuf::readResourceLocation); //can be set to null in #build, if the category was not found

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
        buffer.writeNullable(this.categoryToOpenId, FriendlyByteBuf::writeResourceLocation); //can be set to null in #build, if the category was not found
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

    @Override
    public void openEntry(BookAddress address) {
        //if we jump to a category, we push the current category to history to be able to return
        BookGuiManager.get().pushHistory(BookAddress.defaultFor(this.getCategory()));
        //we don't have any use for the address here
        BookGuiManager.get().openCategoryLinkEntry(this);
    }

}
