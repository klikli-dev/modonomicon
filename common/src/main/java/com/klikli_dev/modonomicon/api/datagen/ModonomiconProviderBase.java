/*
 * SPDX-FileCopyrightText: 2024 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */


package com.klikli_dev.modonomicon.api.datagen;

import com.klikli_dev.modonomicon.api.datagen.book.condition.BookAndConditionModel;
import com.klikli_dev.modonomicon.api.datagen.book.condition.BookConditionModel;
import com.klikli_dev.modonomicon.api.datagen.book.condition.BookOrConditionModel;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.ChatFormatting;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.ItemLike;

import java.text.MessageFormat;
import java.util.Map;
import java.util.function.BiConsumer;

public abstract class ModonomiconProviderBase {
    protected final String modId;
    protected final ModonomiconLanguageProvider lang;
    protected final Map<String, ModonomiconLanguageProvider> langs;
    protected final Map<String, BiConsumer<String, String>> langsAsBiConsumers;
    protected final BookContextHelper context;
    protected final ConditionHelper conditionHelper;
    private final Map<String, String> macros = new Object2ObjectOpenHashMap<>();

    private HolderLookup.Provider registries;

    protected ModonomiconProviderBase(String modId, BiConsumer<String, String> lang, Map<String, BiConsumer<String, String>> langs, BookContextHelper context, ConditionHelper conditionHelper) {
        this.modId = modId;
        //Convert biconsumers to language provider to keep backwards compatibility
        this.lang = toLanguageProvider(lang);
        this.langs = toLanguageProvider(langs);
        this.langsAsBiConsumers = langs;
        this.context = context;
        this.conditionHelper = conditionHelper;
    }

    public static Map<String, ModonomiconLanguageProvider> toLanguageProvider(Map<String, BiConsumer<String, String>> translations) {
        var result = new Object2ObjectOpenHashMap<String, ModonomiconLanguageProvider>();
        translations.forEach((locale, consumer) -> result.put(locale, toLanguageProvider(consumer)));
        return result;
    }

    public static ModonomiconLanguageProvider toLanguageProvider(BiConsumer<String, String> consumer) {
        return new ModonomiconLanguageProvider() {
            @Override
            public void accept(String s, String s2) {
                consumer.accept(s, s2);
            }
        };
    }

    protected String modId() {
        return this.modId;
    }

    protected HolderLookup.Provider registries() {
        return this.registries;
    }

    protected void registries(HolderLookup.Provider registries) {
        this.registries = registries;
    }

    protected ModonomiconLanguageProvider lang() {
        return this.lang;
    }

    protected ModonomiconLanguageProvider lang(String locale) {
        return this.langs.get(locale);
    }

    protected Map<String, ModonomiconLanguageProvider> langs() {
        return this.langs;
    }

    protected Map<String, BiConsumer<String, String>> langsAsMapOfBiConsumers() {
        return this.langsAsBiConsumers;
    }

    protected ConditionHelper condition() {
        return this.conditionHelper;
    }

    protected BookContextHelper context() {
        return this.context;
    }

    protected String path(String... parts) {
        return String.join("/", parts);
    }

    protected Identifier modLoc(String... idParts) {
        return Identifier.fromNamespaceAndPath(this.modId, this.path(idParts));
    }

    protected Identifier modLoc(String name) {
        return Identifier.fromNamespaceAndPath(this.modId, name);
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
        for (var entry : this.macros().entrySet()) {
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
        return this.format("[{0}](entry://{1}/{2}/{3})", text, this.modLoc(this.context().bookId()), category, entry);
    }

    /**
     * Create a link to a category in the same book.
     */
    protected String categoryLink(String text, String category) {
        return this.format("[{0}](category://{1}/{2})", text, this.modLoc(this.context().bookId()), category);
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
        var rl = BuiltInRegistries.ITEM.getKey(item.asItem());
        return this.format("[{0}](item://{1})", text, rl);
    }

    /**
     * Create a command link for the command with the given id.
     */
    protected String commandLink(String text, String commandId) {
        return this.format("[{0}](command://{1}/{2})", text, this.modLoc(this.context().bookId()), this.modLoc(commandId));
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

    protected String color(String text, ChatFormatting color) {
        return this.color(text, color.getColor());
    }

    protected String color(String text, int rgb) {
        return this.color(text, String.format("%06X", rgb));
    }

    protected String color(String text, String color) {
        return this.format("[#]({0}){1}[#]()", color, text);
    }

    public BookAndConditionModel and(BookConditionModel<?>... children) {
        return this.condition().and(children);
    }

    public BookOrConditionModel or(BookConditionModel<?>... children) {
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
    protected void add(ModonomiconLanguageProvider translation, String key, String value) {
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
    protected void add(ModonomiconLanguageProvider translation, String key, String pattern, Object... args) {
        this.add(translation, key, this.format(pattern, args));
    }
}
