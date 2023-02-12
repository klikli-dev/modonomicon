/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.book;

import com.google.common.base.Suppliers;
import com.google.gson.JsonObject;
import com.klikli_dev.modonomicon.api.ModonomiconConstants.Data;
import com.klikli_dev.modonomicon.api.ModonomiconConstants.Nbt;
import com.klikli_dev.modonomicon.book.error.BookErrorManager;
import com.klikli_dev.modonomicon.client.gui.book.BookContentScreen;
import com.klikli_dev.modonomicon.client.gui.book.markdown.BookTextRenderer;
import com.klikli_dev.modonomicon.registry.ItemRegistry;
import com.klikli_dev.modonomicon.util.ItemStackUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Supplier;

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
    protected ConcurrentMap<ResourceLocation, BookCategory> categories;
    protected ConcurrentMap<ResourceLocation, BookEntry> entries;

    protected int defaultTitleColor;
    protected boolean autoAddReadConditions;
    protected boolean generateBookItem;
    protected ResourceLocation customBookItem;

    /**
     * When rendering book text holders, add this offset to the x position (basically, create a left margin).
     * Will be automatically subtracted from the width to avoid overflow.
     */
    protected int bookTextOffsetX;

    /**
     * When rendering book text holders, add this offset to the y position (basically, create a top margin).
     */
    protected int bookTextOffsetY;

    /**
     * When rendering book text holders, add this offset to the width (allows to create a right margin)
     * To make the line end move to the left (as it would for a margin setting in eg css), use a negative value.
     */
    protected int bookTextOffsetWidth;

    protected Supplier<ItemStack> bookItem = Suppliers.memoize(() -> {
        if (this.customBookItem != null) {
            var parsed = ItemStackUtil.parseItemStackString(this.customBookItem.toString());
            return ItemStackUtil.loadFromParsed(parsed);
        }
        var stack = new ItemStack(ItemRegistry.MODONOMICON.get());
        var tag = new CompoundTag();
        tag.putString(Nbt.ITEM_BOOK_ID_TAG, this.id.toString());
        stack.setTag(tag);
        return stack;
    });

    public Book(ResourceLocation id, String name, String tooltip, ResourceLocation model, boolean generateBookItem, ResourceLocation customBookItem, String creativeTab, ResourceLocation bookOverviewTexture, ResourceLocation bookContentTexture, ResourceLocation craftingTexture, ResourceLocation turnPageSound, int defaultTitleColor, boolean autoAddReadConditions, int bookTextOffsetX, int bookTextOffsetY, int bookTextOffsetWidth) {
        this.id = id;
        this.name = name;
        this.tooltip = tooltip;
        this.model = model;
        this.generateBookItem = generateBookItem;
        this.customBookItem = customBookItem;
        this.creativeTab = creativeTab;
        this.bookOverviewTexture = bookOverviewTexture;
        this.bookContentTexture = bookContentTexture;
        this.craftingTexture = craftingTexture;
        this.turnPageSound = turnPageSound;
        this.defaultTitleColor = defaultTitleColor;
        this.autoAddReadConditions = autoAddReadConditions;
        this.categories = new ConcurrentHashMap<>();
        this.entries = new ConcurrentHashMap<>();
        this.bookTextOffsetX = bookTextOffsetX;
        this.bookTextOffsetY = bookTextOffsetY;
        this.bookTextOffsetWidth = bookTextOffsetWidth;
    }

    public static Book fromJson(ResourceLocation id, JsonObject json) {
        var name = GsonHelper.getAsString(json, "name");
        var tooltip = GsonHelper.getAsString(json, "tooltip", "");
        var model = new ResourceLocation(GsonHelper.getAsString(json, "model", Data.Book.DEFAULT_MODEL));
        var generateBookItem = GsonHelper.getAsBoolean(json, "generate_book_item", true);
        var customBookItem = json.has("custom_book_item") ?
                new ResourceLocation(GsonHelper.getAsString(json, "custom_book_item")) :
                null;
        var creativeTab = GsonHelper.getAsString(json, "creative_tab", "misc");
        var bookOverviewTexture = new ResourceLocation(GsonHelper.getAsString(json, "book_overview_texture", Data.Book.DEFAULT_OVERVIEW_TEXTURE));
        var bookContentTexture = new ResourceLocation(GsonHelper.getAsString(json, "book_content_texture", Data.Book.DEFAULT_CONTENT_TEXTURE));
        var craftingTexture = new ResourceLocation(GsonHelper.getAsString(json, "crafting_texture", Data.Book.DEFAULT_CRAFTING_TEXTURE));
        var turnPageSound = new ResourceLocation(GsonHelper.getAsString(json, "turn_page_sound", Data.Book.DEFAULT_PAGE_TURN_SOUND));
        var defaultTitleColor = GsonHelper.getAsInt(json, "default_title_color", 0x00000);
        var autoAddReadConditions = GsonHelper.getAsBoolean(json, "auto_add_read_conditions", false);

        var bookTextOffsetX = GsonHelper.getAsInt(json, "book_text_offset_x", 0);
        var bookTextOffsetY = GsonHelper.getAsInt(json, "book_text_offset_y", 0);
        var bookTextOffsetWidth = GsonHelper.getAsInt(json, "book_text_offset_width", 0);

        return new Book(id, name, tooltip, model, generateBookItem, customBookItem, creativeTab, bookOverviewTexture, bookContentTexture, craftingTexture, turnPageSound, defaultTitleColor, autoAddReadConditions, bookTextOffsetX, bookTextOffsetY, bookTextOffsetWidth);
    }

    public static Book fromNetwork(ResourceLocation id, FriendlyByteBuf buffer) {
        var name = buffer.readUtf();
        var tooltip = buffer.readUtf();
        var model = buffer.readResourceLocation();
        var generateBookItem = buffer.readBoolean();
        var customBookItem = buffer.readBoolean() ? buffer.readResourceLocation() : null;
        var creativeTab = buffer.readUtf();
        var bookOverviewTexture = buffer.readResourceLocation();
        var bookContentTexture = buffer.readResourceLocation();
        var craftingTexture = buffer.readResourceLocation();
        var turnPageSound = buffer.readResourceLocation();
        var defaultTitleColor = buffer.readInt();
        var autoAddReadConditions = buffer.readBoolean();
        var bookTextOffsetX = (int) buffer.readShort();
        var bookTextOffsetY =  (int) buffer.readShort();
        var bookTextOffsetWidth =  (int) buffer.readShort();
        return new Book(id, name, tooltip, model, generateBookItem, customBookItem, creativeTab, bookOverviewTexture, bookContentTexture, craftingTexture, turnPageSound, defaultTitleColor, autoAddReadConditions, bookTextOffsetX, bookTextOffsetY, bookTextOffsetWidth);
    }

    public ItemStack getBookItem() {
        return this.bookItem.get();
    }

    public boolean autoAddReadConditions() {
        return this.autoAddReadConditions;
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
        buffer.writeBoolean(this.generateBookItem);
        buffer.writeBoolean(this.customBookItem != null);
        if (this.customBookItem != null) {
            buffer.writeResourceLocation(this.customBookItem);
        }
        buffer.writeUtf(this.creativeTab);
        buffer.writeResourceLocation(this.bookOverviewTexture);
        buffer.writeResourceLocation(this.bookContentTexture);
        buffer.writeResourceLocation(this.craftingTexture);
        buffer.writeResourceLocation(this.turnPageSound);
        buffer.writeInt(this.defaultTitleColor);
        buffer.writeBoolean(this.autoAddReadConditions);

        buffer.writeShort(this.bookTextOffsetX);
        buffer.writeShort(this.bookTextOffsetY);
        buffer.writeShort(this.bookTextOffsetWidth);
    }

    public ResourceLocation getCustomBookItem() {
        return this.customBookItem;
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

    public boolean generateBookItem() {
        return this.generateBookItem;
    }

    public int getBookTextOffsetX() {
        return this.bookTextOffsetX;
    }

    public int getBookTextOffsetY() {
        return this.bookTextOffsetY;
    }

    public int getBookTextOffsetWidth() {
        return this.bookTextOffsetWidth;
    }
}
