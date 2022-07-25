/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.book;

import com.google.gson.JsonObject;
import com.klikli_dev.modonomicon.api.ModonimiconConstants.Data;
import com.klikli_dev.modonomicon.book.error.BookErrorManager;
import com.klikli_dev.modonomicon.client.gui.book.markdown.BookTextRenderer;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeGenerationSettings;
import net.minecraft.world.level.biome.BiomeSpecialEffects;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Book {
    protected ResourceLocation id;
    protected String name;
    protected String tooltip;
    protected String creativeTab;

    protected ResourceLocation model;
    protected ResourceLocation bookOverviewTexture;
    protected ResourceLocation bookContentTexture;

    protected ResourceLocation craftingTexture;
    protected ResourceLocation turnPageSound;
    protected Map<ResourceLocation, BookCategory> categories;
    protected Map<ResourceLocation, BookEntry> entries;

    protected int defaultTitleColor;

    public boolean autoAddReadConditions() {
        return this.autoAddReadConditions;
    }

    protected boolean autoAddReadConditions;


    public Book(ResourceLocation id, String name, String tooltip, ResourceLocation model, String creativeTab, ResourceLocation bookOverviewTexture, ResourceLocation bookContentTexture, ResourceLocation craftingTexture, ResourceLocation turnPageSound, int defaultTitleColor, boolean autoAddReadConditions) {
        this.id = id;
        this.name = name;
        this.tooltip = tooltip;
        this.model = model;
        this.creativeTab = creativeTab;
        this.bookOverviewTexture = bookOverviewTexture;
        this.bookContentTexture = bookContentTexture;
        this.craftingTexture = craftingTexture;
        this.turnPageSound = turnPageSound;
        this.defaultTitleColor = defaultTitleColor;
        this.autoAddReadConditions = autoAddReadConditions;
        this.categories = new HashMap<>();
        this.entries = new HashMap<>();
    }

    public static Book fromJson(ResourceLocation id, JsonObject json) {
        var name = GsonHelper.getAsString(json, "name");
        var tooltip = GsonHelper.getAsString(json, "tooltip", "");
        var model = new ResourceLocation(GsonHelper.getAsString(json, "model", Data.Book.DEFAULT_MODEL));
        var creativeTab = GsonHelper.getAsString(json, "creative_tab", "misc");
        var bookOverviewTexture = new ResourceLocation(GsonHelper.getAsString(json, "book_overview_texture", Data.Book.DEFAULT_OVERVIEW_TEXTURE));
        var bookContentTexture = new ResourceLocation(GsonHelper.getAsString(json, "book_content_texture", Data.Book.DEFAULT_CONTENT_TEXTURE));
        var craftingTexture = new ResourceLocation(GsonHelper.getAsString(json, "crafting_texture", Data.Book.DEFAULT_CRAFTING_TEXTURE));
        var turnPageSound = new ResourceLocation(GsonHelper.getAsString(json, "turn_page_sound", Data.Book.DEFAULT_PAGE_TURN_SOUND));
        var defaultTitleColor = GsonHelper.getAsInt(json, "default_title_color", 0x00000);
        var autoAddReadConditions = GsonHelper.getAsBoolean(json, "auto_add_read_conditions", false);
        return new Book(id, name, tooltip, model, creativeTab, bookOverviewTexture, bookContentTexture, craftingTexture, turnPageSound, defaultTitleColor, autoAddReadConditions);
    }

    //TODO: further properties for customization, such as book item, title color...

    public static Book fromNetwork(ResourceLocation id, FriendlyByteBuf buffer) {
        var name = buffer.readUtf();
        var tooltip = buffer.readUtf();
        var model = buffer.readResourceLocation();
        var creativeTab = buffer.readUtf();
        var bookOverviewTexture = buffer.readResourceLocation();
        var bookContentTexture = buffer.readResourceLocation();
        var craftingTexture = buffer.readResourceLocation();
        var turnPageSound = buffer.readResourceLocation();
        var defaultTitleColor = buffer.readInt();
        var autoAddReadConditions = buffer.readBoolean();
        return new Book(id, name, tooltip, model, creativeTab, bookOverviewTexture, bookContentTexture, craftingTexture, turnPageSound, defaultTitleColor, autoAddReadConditions);
    }

    public ResourceLocation getTurnPageSound() {
        return this.turnPageSound;
    }

    /**
     * call after loading the book jsons to finalize.
     */
    public void build() {
        //first "backlink" all our entries directly into the book
        for (var category : this.categories.values()) {
            for (var entry : category.getEntries().values()) {
                this.addEntry(entry);
            }
        }

        //then build categories, which will in turn build entries (which need the above backlinks to resolve parents)
        for (var category : this.categories.values()) {
            BookErrorManager.get().getContextHelper().categoryId = category.getId();
            category.build(this);
            BookErrorManager.get().getContextHelper().categoryId = null;
        }
    }

    /**
     * Called after build() (after loading the book jsons) to render markdown and store any errors
     */
    public void prerenderMarkdown(BookTextRenderer textRenderer) {
        for (var category : this.categories.values()) {
            BookErrorManager.get().getContextHelper().categoryId = category.getId();
            category.prerenderMarkdown(textRenderer);
            BookErrorManager.get().getContextHelper().categoryId = null;
        }
    }

    public int getDefaultTitleColor() {
        return this.defaultTitleColor;
    }

    public ResourceLocation getId() {
        return this.id;
    }

    public void addCategory(BookCategory category) {
        this.categories.putIfAbsent(category.id, category);
    }

    public BookCategory getCategory(ResourceLocation id) {
        return this.categories.get(id);
    }

    public Map<ResourceLocation, BookCategory> getCategories() {
        return this.categories;
    }

    public List<BookCategory> getCategoriesSorted() {
        return this.categories.values().stream().sorted(Comparator.comparingInt(BookCategory::getSortNumber)).toList();
    }

    public void addEntry(BookEntry entry) {
        this.entries.putIfAbsent(entry.id, entry);
    }

    public BookEntry getEntry(ResourceLocation id) {
        return this.entries.get(id);
    }

    public Map<ResourceLocation, BookEntry> getEntries() {
        return this.entries;
    }

    public String getName() {
        return this.name;
    }

    public String getTooltip() {
        return this.tooltip;
    }

    public String getCreativeTab() {
        return this.creativeTab;
    }

    public ResourceLocation getBookOverviewTexture() {
        return this.bookOverviewTexture;
    }

    public void toNetwork(FriendlyByteBuf buffer) {
        buffer.writeUtf(this.name);
        buffer.writeUtf(this.tooltip);
        buffer.writeResourceLocation(this.model);
        buffer.writeUtf(this.creativeTab);
        buffer.writeResourceLocation(this.bookOverviewTexture);
        buffer.writeResourceLocation(this.bookContentTexture);
        buffer.writeResourceLocation(this.craftingTexture);
        buffer.writeResourceLocation(this.turnPageSound);
        buffer.writeInt(this.defaultTitleColor);
        buffer.writeBoolean(this.autoAddReadConditions);
    }

    public ResourceLocation getCraftingTexture() {
        return this.craftingTexture;
    }

    public ResourceLocation getBookContentTexture() {
        return this.bookContentTexture;
    }

    public ResourceLocation getModel() {
        return this.model;
    }
}
