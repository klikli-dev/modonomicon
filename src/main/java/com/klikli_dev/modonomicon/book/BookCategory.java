/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.book;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.klikli_dev.modonomicon.api.ModonomiconConstants.Data.Category;
import com.klikli_dev.modonomicon.book.conditions.BookCondition;
import com.klikli_dev.modonomicon.book.conditions.BookNoneCondition;
import com.klikli_dev.modonomicon.book.error.BookErrorManager;
import com.klikli_dev.modonomicon.client.gui.book.markdown.BookTextRenderer;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class BookCategory {

    protected ResourceLocation id;
    protected Book book;
    protected String name;
    protected BookIcon icon;
    protected int sortNumber;
    protected ResourceLocation background;
    protected List<BookCategoryBackgroundParallaxLayer> backgroundParallaxLayers;
    protected ResourceLocation entryTextures;
    protected ConcurrentMap<ResourceLocation, BookEntry> entries;

    protected BookCondition condition;
    protected boolean showCategoryButton;

    public BookCategory(ResourceLocation id, String name, int sortNumber, BookCondition condition, boolean showCategoryButton, BookIcon icon, ResourceLocation background, List<BookCategoryBackgroundParallaxLayer> backgroundParallaxLayers, ResourceLocation entryTextures) {
        this.id = id;
        this.name = name;
        this.sortNumber = sortNumber;
        this.condition = condition;
        this.showCategoryButton = showCategoryButton;
        this.icon = icon;
        this.background = background;
        this.backgroundParallaxLayers = backgroundParallaxLayers;
        this.entryTextures = entryTextures;
        this.entries = new ConcurrentHashMap<>();
    }

    public static BookCategory fromJson(ResourceLocation id, JsonObject json) {
        var name = GsonHelper.getAsString(json, "name");
        var sortNumber = GsonHelper.getAsInt(json, "sort_number", -1);
        var icon = BookIcon.fromString(new ResourceLocation(GsonHelper.getAsString(json, "icon")));
        var background = new ResourceLocation(GsonHelper.getAsString(json, "background", Category.DEFAULT_BACKGROUND));
        var entryTextures = new ResourceLocation(GsonHelper.getAsString(json, "entry_textures", Category.DEFAULT_ENTRY_TEXTURES));
        var showCategoryButton = GsonHelper.getAsBoolean(json, "show_category_button", true);

        BookCondition condition = new BookNoneCondition(); //default to unlocked
        if (json.has("condition")) {
            condition = BookCondition.fromJson(json.getAsJsonObject("condition"));
        }

        List<BookCategoryBackgroundParallaxLayer> backgroundParallaxLayers = List.of();
        if (json.has("background_parallax_layers"))
            backgroundParallaxLayers = BookCategoryBackgroundParallaxLayer.fromJson(json.getAsJsonArray("background_parallax_layers"));

        return new BookCategory(id, name, sortNumber, condition, showCategoryButton, icon, background, backgroundParallaxLayers, entryTextures);
    }

    public static BookCategory fromNetwork(ResourceLocation id, FriendlyByteBuf buffer) {
        var name = buffer.readUtf();
        var sortNumber = buffer.readInt();
        var icon = BookIcon.fromNetwork(buffer);
        var background = buffer.readResourceLocation();
        var backgroundParallaxLayers = buffer.readList(BookCategoryBackgroundParallaxLayer::fromNetwork);
        var entryTextures = buffer.readResourceLocation();
        var condition = BookCondition.fromNetwork(buffer);
        var showCategoryButton = buffer.readBoolean();
        return new BookCategory(id, name, sortNumber, condition, showCategoryButton, icon, background, backgroundParallaxLayers, entryTextures);
    }

    public void toNetwork(FriendlyByteBuf buffer) {
        buffer.writeUtf(this.name);
        buffer.writeInt(this.sortNumber);
        this.icon.toNetwork(buffer);
        buffer.writeResourceLocation(this.background);
        buffer.writeCollection(this.backgroundParallaxLayers, (buf, layer) -> layer.toNetwork(buf));
        buffer.writeResourceLocation(this.entryTextures);
        BookCondition.toNetwork(this.condition, buffer);
        buffer.writeBoolean(this.showCategoryButton);
    }

    public boolean showCategoryButton() {
        return this.showCategoryButton;
    }

    /**
     * call after loading the book jsons to finalize.
     */
    public void build(Book book) {
        this.book = book;

        for (var entry : this.entries.values()) {
            BookErrorManager.get().getContextHelper().entryId = entry.getId();
            entry.build(this);
            BookErrorManager.get().getContextHelper().entryId = null;
        }
    }

    /**
     * Called after build() (after loading the book jsons) to render markdown and store any errors
     */
    public void prerenderMarkdown(BookTextRenderer textRenderer) {
        for (var entry : this.entries.values()) {
            BookErrorManager.get().getContextHelper().entryId = entry.getId();
            entry.prerenderMarkdown(textRenderer);
            BookErrorManager.get().getContextHelper().entryId = null;
        }
    }

    public ResourceLocation getId() {
        return this.id;
    }

    public Book getBook() {
        return this.book;
    }

    public String getName() {
        return this.name;
    }

    public int getSortNumber() {
        return this.sortNumber;
    }

    public BookIcon getIcon() {
        return this.icon;
    }

    public ResourceLocation getBackground() {
        return this.background;
    }

    public List<BookCategoryBackgroundParallaxLayer> getBackgroundParallaxLayers() {
        return this.backgroundParallaxLayers;
    }

    public ResourceLocation getEntryTextures() {
        return this.entryTextures;
    }

    public Map<ResourceLocation, BookEntry> getEntries() {
        return this.entries;
    }

    public void addEntry(BookEntry entry) {
        this.entries.putIfAbsent(entry.id, entry);
    }

    public BookEntry getEntry(ResourceLocation id) {
        return this.entries.get(id);
    }

    public BookCondition getCondition() {
        return this.condition;
    }
}
