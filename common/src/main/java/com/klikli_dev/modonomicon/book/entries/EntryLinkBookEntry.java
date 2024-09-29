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

public class EntryLinkBookEntry extends BookEntry {

    /**
     * The entry to open on click
     */
    protected ResourceLocation entryToOpenId;
    protected BookEntry entryToOpen;

    public EntryLinkBookEntry(ResourceLocation id, BookEntryData data, ResourceLocation commandToRunOnFirstReadId, ResourceLocation entryToOpenId) {
        super(id, data, commandToRunOnFirstReadId);
        this.entryToOpenId = entryToOpenId;
    }

    public static EntryLinkBookEntry fromJson(ResourceLocation id, JsonObject json, boolean autoAddReadConditions, HolderLookup.Provider provider) {
        BookEntryData data = BookEntryData.fromJson(id, json, autoAddReadConditions, provider);

        ResourceLocation commandToRunOnFirstReadId = null;
        if (json.has("command_to_run_on_first_read")) {
            commandToRunOnFirstReadId = ResourceLocation.parse(GsonHelper.getAsString(json, "command_to_run_on_first_read"));
        }

        var entryToOpenIdPath = GsonHelper.getAsString(json, "entry_to_open");
        var entryToOpenId = entryToOpenIdPath.contains(":") ?
                ResourceLocation.parse(entryToOpenIdPath) :
                ResourceLocation.fromNamespaceAndPath(id.getNamespace(), entryToOpenIdPath);

        return new EntryLinkBookEntry(id, data, commandToRunOnFirstReadId, entryToOpenId);
    }

    public static EntryLinkBookEntry fromNetwork(RegistryFriendlyByteBuf buffer) {
        var id = buffer.readResourceLocation();
        BookEntryData data = BookEntryData.fromNetwork(buffer);
        ResourceLocation commandToRunOnFirstReadId = buffer.readNullable(FriendlyByteBuf::readResourceLocation);
        ResourceLocation entryToOpen = buffer.readResourceLocation();

        return new EntryLinkBookEntry(id, data, commandToRunOnFirstReadId, entryToOpen);
    }

    @Override
    public ResourceLocation getType() {
        return ModonomiconConstants.Data.EntryType.ENTRY_LINK;
    }

    @Override
    public void toNetwork(RegistryFriendlyByteBuf buffer) {
        buffer.writeResourceLocation(this.id);
        this.data.toNetwork(buffer);
        buffer.writeNullable(this.commandToRunOnFirstReadId, FriendlyByteBuf::writeResourceLocation);
        buffer.writeResourceLocation(this.entryToOpenId);
    }

    @Override
    public void build(Level level, BookCategory entry) {
        super.build(level, entry);

        if (this.entryToOpenId != null) {
            this.entryToOpen = this.getBook().getEntry(this.entryToOpenId);

            if (this.entryToOpen == null) {
                BookErrorManager.get().error("Entry to open \"" + this.entryToOpenId + "\" does not exist in this book. Set to null.");
                this.entryToOpenId = null;
            }
        }
    }

    public BookEntry getEntryToOpen() {
        return this.entryToOpen;
    }

    @Override
    public void openEntry(BookAddress address) {
        //if we jump to an entry, we push the current category to history to be able to return
        BookGuiManager.get().pushHistory(BookAddress.defaultFor(this.getCategory()));

        BookGuiManager.get().openEntry(this.entryToOpen, BookAddress.defaultFor(this.entryToOpen));
    }

}
