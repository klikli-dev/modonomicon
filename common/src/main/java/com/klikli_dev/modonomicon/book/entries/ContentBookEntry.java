/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.book.entries;

import com.google.gson.*;
import com.klikli_dev.modonomicon.api.*;
import com.klikli_dev.modonomicon.book.*;
import com.klikli_dev.modonomicon.book.conditions.BookCondition;
import com.klikli_dev.modonomicon.book.error.BookErrorManager;
import com.klikli_dev.modonomicon.book.page.BookPage;
import com.klikli_dev.modonomicon.bookstate.BookUnlockStateManager;
import com.klikli_dev.modonomicon.client.gui.book.*;
import com.klikli_dev.modonomicon.client.gui.book.markdown.BookTextRenderer;
import com.klikli_dev.modonomicon.data.LoaderRegistry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import java.util.*;

public class ContentBookEntry extends BookEntry {

    protected List<BookPage> pages;

    public ContentBookEntry(ResourceLocation id, BookEntryData data, ResourceLocation commandToRunOnFirstReadId, List<BookPage> pages) {
        super(id, data, commandToRunOnFirstReadId);
        this.pages = pages;
    }
    
    @Override
    public ResourceLocation getType() {
        return ModonomiconConstants.Data.EntryType.CONTENT;
    }

    public static ContentBookEntry fromJson(ResourceLocation id, JsonObject json, boolean autoAddReadConditions) {
        BookEntryData data = BookEntryData.fromJson(json, autoAddReadConditions);

        ResourceLocation commandToRunOnFirstReadId = null;
        if (json.has("command_to_run_on_first_read")) {
            commandToRunOnFirstReadId = new ResourceLocation(GsonHelper.getAsString(json, "command_to_run_on_first_read"));
        }

        var pages = new ArrayList<BookPage>();
        if (json.has("pages")) {
            var jsonPages = GsonHelper.getAsJsonArray(json, "pages");
            for (var pageElem : jsonPages) {
                BookErrorManager.get().setContext("Page Index: {}", pages.size());
                var pageJson = GsonHelper.convertToJsonObject(pageElem, "page");
                var type = new ResourceLocation(GsonHelper.getAsString(pageJson, "type"));
                var loader = LoaderRegistry.getPageJsonLoader(type);
                var page = loader.fromJson(pageJson);
                pages.add(page);
            }
        }
        
        return new ContentBookEntry(id, data, commandToRunOnFirstReadId, pages);
    }

    @Override
    public void toNetwork(FriendlyByteBuf buffer) {
        buffer.writeResourceLocation(this.id);
        this.data.toNetwork(buffer);
        buffer.writeNullable(this.commandToRunOnFirstReadId, FriendlyByteBuf::writeResourceLocation);
        
        buffer.writeVarInt(this.pages.size());
        for (var page : this.pages) {
            buffer.writeResourceLocation(page.getType());
            page.toNetwork(buffer);
        }
    }
    
    public static ContentBookEntry fromNetwork(FriendlyByteBuf buffer) {
        var id = buffer.readResourceLocation();
        BookEntryData data = BookEntryData.fromNetwork(buffer);
        ResourceLocation commandToRunOnFirstReadId = buffer.readNullable(FriendlyByteBuf::readResourceLocation);

        var pages = new ArrayList<BookPage>();
        var pageCount = buffer.readVarInt();
        for (var i = 0; i < pageCount; i++) {
            var type = buffer.readResourceLocation();
            var loader = LoaderRegistry.getPageNetworkLoader(type);
            var page = loader.fromNetwork(buffer);
            pages.add(page);
        }
        
        return new ContentBookEntry(id, data, commandToRunOnFirstReadId, pages);
    }
    
    /**
     * call after loading the book jsons to finalize.
     */
    @Override
    public void build(Level level, BookCategory category) {
        super.build(level, category);

        //build pages
        int pageNum = 0;
        for (var page : this.pages) {
            BookErrorManager.get().getContextHelper().pageNumber = pageNum;
            page.build(level, this, pageNum);
            BookErrorManager.get().getContextHelper().pageNumber = -1;
            pageNum++;
        }
    }
    
    @Override
    public void prerenderMarkdown(BookTextRenderer textRenderer) {
        for (var page : this.pages) {
            BookErrorManager.get().getContextHelper().pageNumber = page.getPageNumber();
            page.prerenderMarkdown(textRenderer);
            BookErrorManager.get().getContextHelper().pageNumber = -1;
        }
    }
    
    @Override
    public List<BookPage> getPages() {
        return this.pages;
    }
    
    @Override
    public List<BookPage> getUnlockedPagesFor(Player player) {
        BookUnlockStateManager unlockManager = BookUnlockStateManager.get();
        return unlockManager.getUnlockedPagesFor(player, this);
    }
    
    @Override
    public int getPageNumberForAnchor(String anchor) {
        var pages = this.getPages();
        for (int i = 0; i < pages.size(); i++) {
            var page = pages.get(i);
            if (anchor.equals(page.getAnchor())) {
                return i;
            }
        }

        return -1;
    }
    
    @Override
    public boolean matchesQuery(String query) {
        if(super.matchesQuery(query)) {
            return true;
        }

        for (var page : this.getPages()) {
            if (page.matchesQuery(query)) {
                return true;
            }
        }

        return false;
    }
    
    public BookContentScreen openEntry(BookCategoryScreen categoryScreen) {
        return categoryScreen.openContentEntry(this);
    }
}
