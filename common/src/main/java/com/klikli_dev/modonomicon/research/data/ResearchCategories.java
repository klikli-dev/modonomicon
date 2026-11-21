/*
 * SPDX-FileCopyrightText: 2026 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.research.data;

import com.klikli_dev.modonomicon.Modonomicon;
import com.klikli_dev.modonomicon.registry.TriggerTypeRegistry;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Util;

import java.util.List;
import java.util.Optional;

/**
 * Ids and built-in definitions of the requirement categories shipped with modonomicon.
 * <p>
 * The built-in categories are defined in code, so they are always present. A category declared in a research bundle
 * ({@code categories.json}, or via {@code ResearchCategoryModel} in datagen) with the same id replaces the built-in one.
 * Titles and descriptions are translation keys, see {@link #translationKey(Identifier)}.
 */
public final class ResearchCategories {

    public static final Identifier RESEARCH = Modonomicon.loc("research");
    public static final Identifier ITEMS = Modonomicon.loc("items");
    public static final Identifier KNOWLEDGE = Modonomicon.loc("knowledge");
    public static final Identifier PROGRESS = Modonomicon.loc("progress");
    public static final Identifier EFFECTS = Modonomicon.loc("effects");

    /**
     * The sprite of the research category, also the fallback icon of custom categories.
     */
    public static final Identifier RESEARCH_ICON = icon("research");

    private static final String TRANSLATION_PREFIX = "research_category";

    private ResearchCategories() {
    }

    /**
     * The translation key of a category title, e.g. {@code research_category.modonomicon.items}. The description uses the same key with a {@code .description} suffix.
     */
    public static String translationKey(Identifier categoryId) {
        return Util.makeDescriptionId(TRANSLATION_PREFIX, categoryId);
    }

    /**
     * The built-in categories, in declaration order.
     */
    public static List<ResearchCategoryDefinition> builtins() {
        return List.of(
                builtin(RESEARCH, "research", 0),
                builtin(ITEMS, "items", 10, TriggerTypeRegistry.ITEM_CRAFTED.id(), TriggerTypeRegistry.ITEM_ACQUIRED.id()),
                builtin(KNOWLEDGE, "knowledge", 20, TriggerTypeRegistry.ENTRY_VIEWED_ONCE.id(), TriggerTypeRegistry.ADVANCEMENT.id(), TriggerTypeRegistry.ITEM_USED.id()),
                builtin(PROGRESS, "progress", 30),
                builtin(EFFECTS, "effects", 100)
        );
    }

    private static ResearchCategoryDefinition builtin(Identifier id, String iconName, int sortOrder, Identifier... triggerTypes) {
        return new ResearchCategoryDefinition(
                id,
                icon(iconName),
                Component.translatable(translationKey(id)),
                Optional.of(Component.translatable(translationKey(id) + ".description")),
                sortOrder,
                List.of(triggerTypes)
        );
    }

    private static Identifier icon(String name) {
        return Identifier.fromNamespaceAndPath(Modonomicon.MOD_ID, "modonomicon/scholarship/groups/" + name);
    }
}