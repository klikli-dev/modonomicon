/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.book;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.klikli_dev.modonomicon.book.conditions.BookCondition;
import com.klikli_dev.modonomicon.book.conditions.BookTrueCondition;
import com.klikli_dev.modonomicon.book.error.BookErrorManager;
import com.klikli_dev.modonomicon.book.page.BookPage;
import com.klikli_dev.modonomicon.client.gui.book.markdown.BookTextRenderer;
import com.klikli_dev.modonomicon.data.LoaderRegistry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;

import java.util.ArrayList;
import java.util.List;

public class BookEntry {
    protected ResourceLocation id;
    protected ResourceLocation categoryId;
    protected BookCategory category;
    protected Book book;
    protected List<BookEntryParent> parents;
    protected String name;
    protected String description;
    protected BookIcon icon;
    protected int x;
    protected int y;
    protected boolean hideWhileLocked;
    protected List<BookPage> pages;
    protected BookCondition condition;

    //TODO: entry type for background/border texture

    public BookEntry(ResourceLocation id, ResourceLocation categoryId, String name, String description, BookIcon icon, int x, int y, boolean hideWhileLocked, BookCondition condition, List<BookEntryParent> parents, List<BookPage> pages) {
        this.id = id;
        this.categoryId = categoryId;
        this.name = name;
        this.description = description;
        this.icon = icon;
        this.x = x;
        this.y = y;
        this.parents = parents;
        this.pages = pages;
        this.condition = condition;
        this.hideWhileLocked = hideWhileLocked;
    }

    public static BookEntry fromJson(ResourceLocation id, JsonObject json) {
        var categoryId = new ResourceLocation(GsonHelper.getAsString(json, "category"));
        var name = GsonHelper.getAsString(json, "name");
        var description = GsonHelper.getAsString(json, "description", "");
        var icon = BookIcon.fromString(GsonHelper.getAsString(json, "icon"));
        var x = GsonHelper.getAsInt(json, "x");
        var y = GsonHelper.getAsInt(json, "y");
        var hideWhileLocked = GsonHelper.getAsBoolean(json, "hide_while_locked");

        var parentEntries = new ArrayList<BookEntryParent>();

        if (json.has("parents")) {
            JsonArray parents = GsonHelper.getAsJsonArray(json, "parents");
            for (var parent : parents) {
                parentEntries.add(BookEntryParent.fromJson(parent.getAsJsonObject()));
            }
        }

        var pages = new ArrayList<BookPage>();
        if (json.has("pages")) {
            var jsonPages = GsonHelper.getAsJsonArray(json, "pages");
            for (var pageElem : jsonPages) {
                var pageJson = GsonHelper.convertToJsonObject(pageElem, "page");
                var type = new ResourceLocation(GsonHelper.getAsString(pageJson, "type"));
                var loader = LoaderRegistry.getPageJsonLoader(type);
                var page = loader.fromJson(pageJson);
                pages.add(page);
            }
        }

        BookCondition condition = new BookTrueCondition(); //default to unlocked
        if (json.has("condition")) {
            condition = BookCondition.fromJson(json.getAsJsonObject("condition"));
        }

        return new BookEntry(id, categoryId, name, description, icon, x, y, hideWhileLocked, condition, parentEntries, pages);
    }

    public static BookEntry fromNetwork(ResourceLocation id, FriendlyByteBuf buffer) {
        var categoryId = buffer.readResourceLocation();
        var name = buffer.readUtf();
        var description = buffer.readUtf();
        var icon = BookIcon.fromNetwork(buffer);
        var x = buffer.readVarInt();
        var y = buffer.readVarInt();
        var hideWhileLocked = buffer.readBoolean();

        var parentEntries = new ArrayList<BookEntryParent>();

        var parentCount = buffer.readVarInt();
        for (var i = 0; i < parentCount; i++) {
            parentEntries.add(BookEntryParent.fromNetwork(buffer));
        }

        var pages = new ArrayList<BookPage>();
        var pageCount = buffer.readVarInt();
        for (var i = 0; i < pageCount; i++) {
            var type = buffer.readResourceLocation();
            var loader = LoaderRegistry.getPageNetworkLoader(type);
            var page = loader.fromNetwork(buffer);
            pages.add(page);
        }

        var condition = BookCondition.fromNetwork(buffer);

        return new BookEntry(id, categoryId, name, description, icon, x, y, hideWhileLocked, condition, parentEntries, pages);
    }

    /**
     * call after loading the book jsons to finalize.
     */
    public void build(BookCategory category) {
        this.category = category;
        this.book = category.getBook();

        //resolve parents
        var newParents = new ArrayList<BookEntryParent>();
        for (var parent : this.getParents()) {
            var parentEntry = this.book.getEntry(parent.getEntryId());
            newParents.add(new ResolvedBookEntryParent(parentEntry));
        }
        this.parents = newParents;

        //build pages
        int pageNum = 0;
        for (var page : this.pages) {
            BookErrorManager.get().getContextHelper().pageNumber = pageNum;
            page.build(this, pageNum);
            BookErrorManager.get().getContextHelper().pageNumber = -1;
            pageNum++;
        }
    }

    /**
     * Called after build() (after loading the book jsons) to render markdown and store any errors
     */
    public void prerenderMarkdown(BookTextRenderer textRenderer) {
        for (var page : this.pages) {
            BookErrorManager.get().getContextHelper().pageNumber = page.getPageNumber();
            page.prerenderMarkdown(textRenderer);
            BookErrorManager.get().getContextHelper().pageNumber = -1;
        }
    }

    public void toNetwork(FriendlyByteBuf buffer) {
        buffer.writeResourceLocation(this.categoryId);
        buffer.writeUtf(this.name);
        buffer.writeUtf(this.description);
        this.icon.toNetwork(buffer);
        buffer.writeVarInt(this.x);
        buffer.writeVarInt(this.y);
        buffer.writeBoolean(this.hideWhileLocked);

        buffer.writeVarInt(this.parents.size());
        for (var parent : this.parents) {
            parent.toNetwork(buffer);
        }

        buffer.writeVarInt(this.pages.size());
        for (var page : this.pages) {
            buffer.writeResourceLocation(page.getType());
            page.toNetwork(buffer);
        }

        BookCondition.toNetwork(this.condition, buffer);
    }

    public int getY() {
        return this.y;
    }

    public int getX() {
        return this.x;
    }

    public boolean hideWhileLocked() {
        return this.hideWhileLocked;
    }

    public ResourceLocation getId() {
        return this.id;
    }

    public ResourceLocation getCategoryId() {
        return this.categoryId;
    }

    public BookCategory getCategory() {
        return this.category;
    }

    public List<BookEntryParent> getParents() {
        return this.parents;
    }

    public String getName() {
        return this.name;
    }

    public BookIcon getIcon() {
        return this.icon;
    }

    public String getDescription() {
        return this.description;
    }

    public List<BookPage> getPages() {
        return this.pages;
    }

    public Book getBook() {
        return this.book;
    }

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

    public BookCondition getCondition() {
        return this.condition;
    }
}
