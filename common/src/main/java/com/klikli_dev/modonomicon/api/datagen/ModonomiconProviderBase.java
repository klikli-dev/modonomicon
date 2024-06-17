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
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.ApiStatus;

import java.text.MessageFormat;
import java.util.Map;
import java.util.stream.Collector;
import java.util.stream.Stream;

@ApiStatus.Internal
public abstract class ModonomiconProviderBase {

    protected static final Collector<ModonomiconLanguageProvider, ?, Object2ObjectOpenHashMap<String, ModonomiconLanguageProvider>> mapMaker
            = Collector.<ModonomiconLanguageProvider, Object2ObjectOpenHashMap<String, ModonomiconLanguageProvider>, Object2ObjectOpenHashMap<String, ModonomiconLanguageProvider>>of(
            Object2ObjectOpenHashMap::new,
            (map, l) -> map.put(l.locale(), l),
            (m1, m2) -> {
                m1.putAll(m2);
                return m1;
            },
            (map) -> {
                map.trim();
                return map;
            },
            Collector.Characteristics.UNORDERED);
    protected final String modId;
    protected final ModonomiconLanguageProvider lang;
    protected final Map<String, ModonomiconLanguageProvider> translations;
    protected final BookContextHelper context;
    protected final ConditionHelper conditionHelper;
    private final Map<String, String> macros = new Object2ObjectOpenHashMap<>();

    protected ModonomiconProviderBase(String modId, ModonomiconLanguageProvider lang, Map<String, ModonomiconLanguageProvider> translations, BookContextHelper context, ConditionHelper conditionHelper) {
        this.modId = modId;
        this.lang = lang;
        this.translations = translations;
        this.context = context;
        this.conditionHelper = conditionHelper;
    }

    public static Map<String, ModonomiconLanguageProvider> makeLangMap(ModonomiconLanguageProvider defaultLang, ModonomiconLanguageProvider... translations) {
        return Stream.concat(Stream.of(defaultLang), Stream.of(translations))
                .collect(mapMaker);
    }

    protected String modId() {
        return this.modId;
    }

    protected ModonomiconLanguageProvider lang() {
        return this.lang;
    }

    protected ModonomiconLanguageProvider lang(String locale) {
        return this.translations.get(locale);
    }

    protected Map<String, ModonomiconLanguageProvider> langs() {
        return this.translations;
    }

    protected ConditionHelper condition() {
        return this.conditionHelper;
    }

    protected BookContextHelper context() {
        return this.context;
    }

    protected ResourceLocation modLoc(String... idParts) {
        return ResourceLocation.fromNamespaceAndPath(this.modId, String.join("/", idParts));
    }

    protected ResourceLocation modLoc(String name) {
        return ResourceLocation.fromNamespaceAndPath(this.modId, name);
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
        var rl = BuiltInRegistries.ITEM.getKey(item.asItem());
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
