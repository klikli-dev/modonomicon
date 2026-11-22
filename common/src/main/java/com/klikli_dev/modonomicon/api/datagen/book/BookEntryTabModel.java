/*
 * SPDX-FileCopyrightText: 2026 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.api.datagen.book;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.klikli_dev.modonomicon.api.datagen.book.condition.BookConditionModel;
import com.klikli_dev.modonomicon.api.datagen.book.condition.BookNoneConditionModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookPageModel;
import com.klikli_dev.modonomicon.book.entries.BookEntryTab;
import com.klikli_dev.modonomicon.book.entries.EntryTabSide;
import com.klikli_dev.modonomicon.data.load.LoadConditionPreprocessor;
import com.mojang.serialization.JsonOps;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.Nullable;

/**
 * A side bookmark of an entry that opens a single page as an overlay.
 * Tabs are written as files named after their id in the entry's tabs directory, unless the entry generates its tabs inline.
 */
public class BookEntryTabModel {

    @Nullable
    protected String id;
    protected BookPageModel<?> page;
    @Nullable
    protected BookIconModel icon;
    @Nullable
    protected String title;
    protected EntryTabSide side = EntryTabSide.RIGHT;
    @Nullable
    protected String anchor;
    protected BookConditionModel<?> condition = BookNoneConditionModel.create();
    @Nullable
    protected BookConditionModel<?> loadCondition;
    protected int sortNumber = -1;

    protected BookEntryTabModel() {
    }

    /**
     * Creates a tab without id. Only usable for inline tabs.
     */
    public static BookEntryTabModel create() {
        return new BookEntryTabModel();
    }

    /**
     * Creates a tab with the given id. The id is the file name of the tab and is required for file tabs.
     */
    public static BookEntryTabModel create(String id) {
        var model = new BookEntryTabModel();
        model.id = id;
        return model;
    }

    public String getId() {
        return this.id;
    }

    public BookEntryTabModel withId(String id) {
        this.id = id;
        return this;
    }

    public BookEntryTabModel withPage(BookPageModel<?> page) {
        this.page = page;
        return this;
    }

    /**
     * Explicit icon. If not set, the icon is derived from the page (e.g. the recipe output).
     */
    public BookEntryTabModel withIcon(BookIconModel icon) {
        this.icon = icon;
        return this;
    }

    public BookEntryTabModel withIcon(ItemLike item) {
        return this.withIcon(BookIconModel.create(item));
    }

    /**
     * @param title translation key shown as tooltip
     */
    public BookEntryTabModel withTitle(String title) {
        this.title = title;
        return this;
    }

    /**
     * Sets the book edge the tab is attached to. Default is {@link EntryTabSide#RIGHT}.
     */
    public BookEntryTabModel withSide(EntryTabSide side) {
        this.side = side;
        return this;
    }

    /**
     * Pins the tab to a page: the tab is only visible while the page with this id is on the open spread.
     * {@code EntryProvider.tab(...)} sets this automatically to the preceding page, unless an anchor was set explicitly.
     * Tabs without an anchor ({@code EntryProvider.entryTab(...)}) are visible for the whole entry.
     *
     * @param pageId the id of the page of this entry, e.g. the id set on {@link BookPageModel#withId(String)}
     */
    public BookEntryTabModel withAnchor(String pageId) {
        this.anchor = pageId;
        return this;
    }

    @Nullable
    public String getAnchor() {
        return this.anchor;
    }

    /**
     * The tab is only visible if this condition is met.
     */
    public BookEntryTabModel withCondition(BookConditionModel<?> condition) {
        this.condition = condition;
        return this;
    }

    public BookConditionModel<?> getCondition() {
        return this.condition;
    }

    /**
     * The tab is removed at load time if this condition is not met. Only conditions that support load-time evaluation are allowed.
     */
    public BookEntryTabModel withLoadCondition(BookConditionModel<?> loadCondition) {
        this.loadCondition = loadCondition;
        return this;
    }

    /**
     * Controls the order of file tabs within the entry. Tabs with a negative sort number are sorted in the order they are added.
     */
    public BookEntryTabModel withSortNumber(int sortNumber) {
        this.sortNumber = sortNumber;
        return this;
    }

    public int getSortNumber() {
        return this.sortNumber;
    }

    /**
     * Serializes the tab to a file json. The page of a tab without page id gets the tab id.
     */
    public JsonObject toJson(Identifier entryId, HolderLookup.Provider provider) {
        var json = BookEntryTab.CODEC.encodeStart(provider.createSerializationContext(JsonOps.INSTANCE), this.toBookEntryTab(provider))
                .getOrThrow(JsonParseException::new)
                .getAsJsonObject();
        this.addLoadConditionToJson(json, entryId, provider);
        return json;
    }

    /**
     * Adds the load condition (if any) to the given tab json.
     */
    public void addLoadConditionToJson(JsonObject json, Identifier entryId, HolderLookup.Provider provider) {
        if (this.loadCondition != null) {
            json.add(LoadConditionPreprocessor.KEY, this.loadCondition.toJson(entryId, provider));
        }
    }

    public BookEntryTab toBookEntryTab(HolderLookup.Provider provider) {
        if (this.page == null) {
            throw new IllegalStateException("Entry tab" + (this.id != null ? " '" + this.id + "'" : "") + " needs a page.");
        }

        if (this.id != null && (this.page.getId() == null || this.page.getId().isEmpty())) {
            this.page.withId(this.id);
        }

        return new BookEntryTab(
                this.id,
                this.page.toBookPage(provider),
                this.icon != null ? this.icon.toBookIcon() : null,
                this.title != null ? Component.translatable(this.title) : null,
                this.side,
                this.anchor,
                this.condition.toBookCondition(provider),
                this.sortNumber
        );
    }
}
