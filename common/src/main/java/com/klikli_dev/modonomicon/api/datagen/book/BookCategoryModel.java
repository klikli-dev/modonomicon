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
import com.klikli_dev.modonomicon.registry.ItemRegistry;
import com.mojang.serialization.JsonOps;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class BookCategoryModel {
    protected BookModel book;

    protected ResourceLocation id;
    protected String name;
    protected BookIconModel icon = BookIconModel.create(ItemRegistry.MODONOMICON_PURPLE.get());
    protected int sortNumber = -1;
    protected ResourceLocation background = new ResourceLocation(Category.DEFAULT_BACKGROUND);
    protected int backgroundWidth = Category.DEFAULT_BACKGROUND_WIDTH;
    protected int backgroundHeight = Category.DEFAULT_BACKGROUND_HEIGHT;
    /**
     * Allows to modify how "zoomed in" the background texture is rendered.
     * A lower value means the texture is zoomed OUT more -> it is sharper / less blurry.
     */
    protected float backgroundTextureZoomMultiplier = Category.DEFAULT_BACKGROUND_TEXTURE_ZOOM_MULTIPLIER;
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
        json.add("icon", this.icon.toJson());
        json.addProperty("sort_number", this.sortNumber);
        json.addProperty("background", this.background.toString());
        json.addProperty("background_width", this.backgroundWidth);
        json.addProperty("background_height", this.backgroundHeight);
        json.addProperty("background_texture_zoom_multiplier", this.backgroundTextureZoomMultiplier);
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

    public BookIconModel getIcon() {
        return this.icon;
    }

    public int getSortNumber() {
        return this.sortNumber;
    }

    public ResourceLocation getBackground() {
        return this.background;
    }

    public int getBackgroundWidth() {
        return this.backgroundWidth;
    }

    public int getBackgroundHeight() {
        return this.backgroundHeight;
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
     */
    public BookCategoryModel withIcon(BookIconModel icon) {
        this.icon = icon;
        return this;
    }

    /**
     * Sets the category's icon as the given texture resource location
     */
    public BookCategoryModel withIcon(ResourceLocation texture) {
        this.icon = BookIconModel.create(texture);
        return this;
    }

    /**
     * Sets the category's icon as the given texture resource location with the given size
     */
    public BookCategoryModel withIcon(ResourceLocation texture, int width, int height) {
        this.icon = BookIconModel.create(texture, width, height);
        return this;
    }

    /**
     * Sets the category's icon to the texture of the given item
     */
    public BookCategoryModel withIcon(ItemLike item) {
        this.icon = BookIconModel.create(item);
        return this;
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
     * The texture needs to be a 512x512 png file, unless withBackgroundSize is called to specify a different size.
     * Default value is {@link Category#DEFAULT_BACKGROUND}.
     */
    public BookCategoryModel withBackground(ResourceLocation background) {
        this.background = background;
        return this;
    }

    /**
     * Sets the category's background texture size.
     * Also used for the parallax layers.
     * Width and height should be identical otherwise undesirable effects may occur.
     */
    public BookCategoryModel withBackgroundSize(int width, int height) {
        this.backgroundWidth = width;
        this.backgroundHeight = height;
        return this;
    }

    /**
     * Sets the category's background texture zoom multiplier.
     * A lower value means the texture is zoomed OUT more -> it is sharper / less blurry.
     * Default value is {@link Category#DEFAULT_BACKGROUND_TEXTURE_ZOOM_MULTIPLIER}.
     */
    public BookCategoryModel withBackgroundTextureZoomMultiplier(float backgroundTextureZoomMultiplier) {
        this.backgroundTextureZoomMultiplier = backgroundTextureZoomMultiplier;
        return this;
    }

    /**
     * Adds a parallax layer to the category's background.
     * If there are any parallax layers, the background texture will be ignored.
     * The texture needs to be a 512x512 png file, unless withBackgroundSize is called to specify a different size.
     */
    public BookCategoryModel withBackgroundParallaxLayers(BookCategoryBackgroundParallaxLayer... layers) {
        this.backgroundParallaxLayers.addAll(List.of(layers));
        return this;
    }

    /**
     * Adds a parallax layer to the category's background.
     * If there are any parallax layers, the background texture will be ignored.
     * The texture needs to be a 512x512 png file, unless withBackgroundSize is called to specify a different size.
     */
    public BookCategoryModel withBackgroundParallaxLayer(BookCategoryBackgroundParallaxLayer layer) {
        this.backgroundParallaxLayers.add(layer);
        return this;
    }

    /**
     * Adds a parallax layer to the category's background.
     * If there are any parallax layers, the background texture will be ignored.
     * The texture needs to be a 512x512 png file, unless withBackgroundSize is called to specify a different size.
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
