/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.api.datagen.book;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.klikli_dev.modonomicon.api.ModonomiconConstants.Data.Category;
import com.klikli_dev.modonomicon.api.datagen.book.condition.BookConditionModel;
import com.klikli_dev.modonomicon.book.BookCategoryBackgroundParallaxLayer;
import com.mojang.serialization.JsonOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class BookCategoryModel {
    protected BookModel book;

    protected ResourceLocation id;
    protected String name;
    protected ResourceLocation icon = new ResourceLocation(Category.DEFAULT_ICON);
    protected int sortNumber = -1;
    protected ResourceLocation background = new ResourceLocation(Category.DEFAULT_BACKGROUND);
    protected List<BookCategoryBackgroundParallaxLayer> backgroundParallaxLayers = new ArrayList<>();
    protected ResourceLocation entryTextures = new ResourceLocation(Category.DEFAULT_ENTRY_TEXTURES);
    protected List<BookEntryModel> entries = new ArrayList<>();

    @Nullable
    protected BookConditionModel condition = null;
    protected boolean showCategoryButton = true;

    protected BookCategoryModel(ResourceLocation id, String name) {
        this.id = id;
        this.name = name;
    }

    /**
     * @param id   The category ID, e.g. "modonomicon:features". The ID must be unique within the book.
     * @param name Should be a translation key.
     */
    public static BookCategoryModel create(ResourceLocation id, String name) {
        return new BookCategoryModel(id, name);
    }

    @Nullable
    public BookConditionModel getCondition() {
        return this.condition;
    }

    public boolean showCategoryButton() {
        return this.showCategoryButton;
    }

    public List<BookEntryModel> getEntries() {
        return this.entries;
    }

    public BookModel getBook() {
        return this.book;
    }

    public JsonObject toJson() {
        JsonObject json = new JsonObject();
        json.addProperty("name", this.name);
        json.addProperty("icon", this.icon.toString());
        json.addProperty("sort_number", this.sortNumber);
        json.addProperty("background", this.background.toString());
        json.add("background_parallax_layers",
                this.backgroundParallaxLayers.stream()
                        .map(layer -> BookCategoryBackgroundParallaxLayer.CODEC.encodeStart(JsonOps.INSTANCE, layer).get().orThrow())
                        .collect(JsonArray::new, JsonArray::add, JsonArray::addAll));
        json.addProperty("entry_textures", this.entryTextures.toString());
        if (this.condition != null) {
            json.add("condition", this.condition.toJson());
        }
        json.addProperty("show_category_button", this.showCategoryButton);
        return json;
    }

    public ResourceLocation getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public ResourceLocation getIcon() {
        return this.icon;
    }

    public int getSortNumber() {
        return this.sortNumber;
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

    /**
     * Sets the category's icon.
     *
     * @param icon Either an item ResourceLocation (e.g.: "minecraft:stone") or a texture ResourceLocation (e.g. "minecraft:textures/block/stone.png" - note the ".png" at the end)
     */
    public BookCategoryModel withIcon(ResourceLocation icon) {
        this.icon = icon;
        return this;
    }

    /**
     * Sets the category's icon.
     *
     * @param icon Either an item ResourceLocation (e.g.: "minecraft:stone") or a texture ResourceLocation (e.g. "minecraft:textures/block/stone.png" - note the ".png" at the end)
     */
    public BookCategoryModel withIcon(String icon) {
        return this.withIcon(new ResourceLocation(icon));
    }

    /**
     * Sets the category's icon to the texture of the given item
     */
    public BookCategoryModel withIcon(ItemLike item) {
        return this.withIcon(ForgeRegistries.ITEMS.getKey(item.asItem()));
    }


    /**
     * Sets the category's sort number.
     * Categories with a lower sort number will be displayed first.
     */
    public BookCategoryModel withSortNumber(int sortNumber) {
        this.sortNumber = sortNumber;
        return this;
    }

    /**
     * Sets the category's background texture.
     * The texture must be a 512x512 png file.
     * Default value is {@link Category#DEFAULT_BACKGROUND}
     */
    public BookCategoryModel withBackground(ResourceLocation background) {
        this.background = background;
        return this;
    }

    /**
     * Adds a parallax layer to the category's background.
     * If there are any parallax layers, the background texture will be ignored.
     * The texture must be a 512x512 png file.
     */
    public BookCategoryModel withBackgroundParallaxLayers(BookCategoryBackgroundParallaxLayer ... layers) {
        this.backgroundParallaxLayers.addAll(List.of(layers));
        return this;
    }

    /**
     * Adds a parallax layer to the category's background.
     * If there are any parallax layers, the background texture will be ignored.
     * The texture must be a 512x512 png file.
     */
    public BookCategoryModel withBackgroundParallaxLayer(BookCategoryBackgroundParallaxLayer layer) {
        this.backgroundParallaxLayers.add(layer);
        return this;
    }

    /**
     * Adds a parallax layer to the category's background.
     * If there are any parallax layers, the background texture will be ignored.
     * The texture must be a 512x512 png file.
     */
    public BookCategoryModel withBackgroundParallaxLayer(ResourceLocation layerTexture) {
        this.backgroundParallaxLayers.add(new BookCategoryBackgroundParallaxLayer(layerTexture));
        return this;
    }

    /**
     * Sets the category's entry textures.
     * The texture must be a 256x256 png file.
     * This texture is used to display the entry background icons as well as the arrows connecting entries.
     * Default value is {@link Category#DEFAULT_ENTRY_TEXTURES}
     */
    public BookCategoryModel withEntryTextures(ResourceLocation entryTextures) {
        this.entryTextures = entryTextures;
        return this;
    }

    /**
     * Replaces the current list of entries with the given list.
     */
    public BookCategoryModel withEntries(List<BookEntryModel> entries) {
        entries.forEach(this::linkEntry);
        this.entries.addAll(entries);
        return this;
    }

    protected BookEntryModel linkEntry(BookEntryModel entry) {
        entry.category = this;
        if (!entry.id.getPath().startsWith(this.id.getPath())) {
            entry.id = new ResourceLocation(entry.id.getNamespace(), this.id.getPath() + "/" + entry.id.getPath());
        }
        return entry;
    }

    /**
     * Adds the given entries to the list of entries.
     */
    public BookCategoryModel withEntries(BookEntryModel... entries) {
        return this.withEntries(List.of(entries));
    }

    /**
     * Adds the given entry to the list of entries.
     */
    public BookCategoryModel withEntry(BookEntryModel entry) {
        this.entries.add(this.linkEntry(entry));
        return this;
    }

    /**
     * Sets the condition that needs to be met for this category to be shown.
     * If no condition is set, the category will be unlocked by default.
     * Use {@link com.klikli_dev.modonomicon.api.datagen.book.condition.BookAndConditionModel} or {@link com.klikli_dev.modonomicon.api.datagen.book.condition.BookOrConditionModel} to combine multiple conditions.
     */
    public BookCategoryModel withCondition(BookConditionModel condition) {
        this.condition = condition;
        return this;
    }

    /**
     * Sets whether the category button should be shown in the book.
     * Default value is true.
     * If false, the category will not have a navigation button, but can still be accessed by clicking on an entry or link that links to it.
     */
    public BookCategoryModel withShowCategoryButton(boolean showCategoryButton) {
        this.showCategoryButton = showCategoryButton;
        return this;
    }
}
