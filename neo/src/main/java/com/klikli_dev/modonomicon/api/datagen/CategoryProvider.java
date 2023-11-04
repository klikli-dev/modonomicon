/*
 * SPDX-FileCopyrightText: 2023 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.api.datagen;

import com.klikli_dev.modonomicon.api.datagen.book.BookCategoryModel;
import com.klikli_dev.modonomicon.api.datagen.book.BookEntryModel;
import com.klikli_dev.modonomicon.api.datagen.book.BookEntryParentModel;
import com.klikli_dev.modonomicon.api.datagen.book.condition.BookAndConditionModel;
import com.klikli_dev.modonomicon.api.datagen.book.condition.BookConditionModel;
import com.klikli_dev.modonomicon.api.datagen.book.condition.BookOrConditionModel;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.common.data.LanguageProvider;
import net.minecraftforge.registries.ForgeRegistries;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class CategoryProvider {

    protected BookProvider parent;
    protected String categoryId;
    protected CategoryEntryMap entryMap;
    protected Map<String, String> macros;

    protected ConditionHelper conditionHelper;

    protected BookCategoryModel category;

    public CategoryProvider(BookProvider parent, String categoryId) {
        this.parent = parent;
        this.categoryId = categoryId;
        this.entryMap = new CategoryEntryMap();
        this.macros = new HashMap<>();
        this.conditionHelper = new ConditionHelper();
        this.category = null;
    }

    public String categoryId() {
        return this.categoryId;
    }

    protected LanguageProvider lang() {
        return this.parent.lang();
    }

    protected LanguageProvider lang(String locale) {
        return this.parent.lang();
    }

    protected ResourceLocation modLoc(String name) {
        return this.parent.modLoc(name);
    }

    protected BookContextHelper context() {
        return this.parent.context();
    }

    protected ConditionHelper condition() {
        return this.parent.condition();
    }

    protected CategoryEntryMap entryMap() {
        return this.entryMap;
    }

    /**
     * Register a macro (= simple string.replace() of macro -> value) to be used in this category providers.
     */
    protected void registerMacro(String macro, String value) {
        this.macros.put(macro, value);
    }


    /**
     * Get the macros (= simple string.replace() of macro -> value) to be used in this category provider.
     */
    protected Map<String, String> macros() {
        return this.macros;
    }

    /**
     * Apply all macros of this category provider and its book provider to the input string.
     */
    protected String macro(String input) {
        for (var entry : this.macros.entrySet()) {
            input = input.replace(entry.getKey(), entry.getValue());
        }
        for (var entry : this.parent.defaultMacros().entrySet()) {
            input = input.replace(entry.getKey(), entry.getValue());
        }
        return input;
    }

    /**
     * Format a string with the given arguments using MessageFormat.format()
     */
    protected String format(String pattern, Object... arguments) {
        return MessageFormat.format(pattern, arguments);
    }

    /**
     * Create a link to an entry in the same book.
     */
    protected String entryLink(String text, String category, String entry) {
        return this.format("[{0}](entry://{1}/{2})", text, category, entry);
    }

    /**
     * Create a link to a category in the same book.
     */
    protected String categoryLink(String text, String category) {
        return this.format("[{0}](category://{1})", text, category);
    }

    /**
     * Create an item link with no text (will use item name)
     */
    protected String itemLink(ItemLike item) {
        return this.itemLink("", item);
    }

    /**
     * Create an item link with a custom text (instead of item name)
     */
    protected String itemLink(String text, ItemLike item) {
        var rl = ForgeRegistries.ITEMS.getKey(item.asItem());
        return this.format("[{0}](item://{1})", text, rl);
    }

    /**
     * Create a command link for the command with the given id.
     */
    protected String commandLink(String text, String commandId) {
        return this.format("[{0}](command://{1})", text, commandId);
    }

    /**
     * Dummy entry link for use in the book provider, as the linked entry is not available at that point.
     * Replace with identical call to entryLink once the entry is available.
     */
    protected String entryLinkDummy(String text, String category, String entry) {
        return this.format("[{0}]()", text, category, entry);
    }

    /**
     * Dummy category link for use in the book provider, as the linked category is not available at that point.
     * Replace with identical call to categoryLink once the entry is available.
     */
    protected String categoryLinkDummy(String text, String category) {
        return this.format("[{0}]()", text, category);
    }

    protected BookEntryModel entry(String location, String icon) {
        return this.entry(location).withIcon(icon);
    }

    protected BookEntryModel entry(String location, ItemLike icon) {
        return this.entry(location).withIcon(icon);
    }

    protected BookEntryModel entry(char location, String icon) {
        return this.entry(location).withIcon(icon);
    }

    protected BookEntryModel entry(char location, ItemLike icon) {
        return this.entry(location).withIcon(icon);
    }

    protected BookEntryModel entry(char location) {
        return this.entry().withLocation(this.entryMap().get(location));
    }

    protected BookEntryModel entry(String location) {
        return this.entry().withLocation(this.entryMap().get(location));
    }

    protected BookEntryModel entry() {
        return BookEntryModel.create(
                        this.modLoc(this.context().categoryId() + "/" + this.context().entryId()),
                        this.context().entryName()
                )
                .withDescription(this.context().entryDescription());
    }

    protected BookEntryParentModel parent(BookEntryModel parentEntry) {
        return BookEntryParentModel.create(parentEntry.getId());
    }

    public BookAndConditionModel and(BookConditionModel... children) {
        return this.condition().and(children);
    }

    public BookOrConditionModel or(BookConditionModel... children) {
        return this.condition().or(children);
    }

    /**
     * Add translation to the default translation provider.
     * This will apply all macros registered in this category provider and its parent book provider.
     */
    protected void add(String key, String value) {
        this.lang().add(key, this.macro(value));
    }

    /**
     * Add translation to the given translation provider.
     * This will apply all macros registered in this category provider and its parent book provider.
     * <p>
     * Sample usage: this.add(this.lang("ru_ru"), "category", "Text");
     */
    protected void add(LanguageProvider translation, String key, String value) {
        translation.add(key, this.macro(value));
    }

    /**
     * Adds translation to the default translation provider with a pattern and arguments, internally using MessageFormat to format the pattern.
     * This will apply all macros registered in this category provider and its parent book provider.
     */
    protected void add(String key, String pattern, Object... args) {
        this.add(key, this.format(pattern, args));
    }

    /**
     * Adds translation to the given translation provider with a pattern and arguments, internally using MessageFormat to format the pattern.
     * This will apply all macros registered in this category provider and its parent book provider.
     * <p>
     * Sample usage: this.add(this.lang("ru_ru"), "category", "pattern", "arg1");
     */
    protected void add(LanguageProvider translation, String key, String pattern, Object... args) {
        this.add(translation, key, this.format(pattern, args));
    }

    protected BookEntryModel add(BookEntryModel entry) {
        this.category.withEntry(entry);
        return entry;
    }

    protected List<BookEntryModel> add(List<BookEntryModel> entries) {
        this.category.withEntries(entries);
        return entries;
    }

    /**
     * Call this in your BookProvider to get the category.
     */
    public BookCategoryModel generate() {
        this.context().category(this.categoryId);
        this.entryMap().setMap(this.generateEntryMap());
        this.category = this.generateCategory();
        this.generateEntries();
        return this.category;
    }

    /**
     * Implement this and return your entry map String to be used in the CategoryEntryMap
     */
    protected abstract String[] generateEntryMap();

    /**
     * Implement this and in it generate, link (= set parents and conditions) and .add() your entries.
     * Context already is set to this category.
     */
    protected abstract void generateEntries();

    /**
     * Implement this and return your category.
     * Entries should not be added here, instead call .add() in generateEntries().
     * Context already is set to this category.
     */
    protected abstract BookCategoryModel generateCategory();
}