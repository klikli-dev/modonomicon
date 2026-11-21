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
 * Ids and built-in definitions of the requirement groups shipped with modonomicon.
 * <p>
 * The built-in groups are defined in code, so they are always present. A group declared in a research bundle
 * ({@code groups.json}, or via {@code ResearchGroupModel} in datagen) with the same id replaces the built-in one.
 * Titles and descriptions are translation keys, see {@link #translationKey(Identifier)}.
 */
public final class ResearchGroups {

    public static final Identifier RESEARCH = Modonomicon.loc("research");
    public static final Identifier ITEMS = Modonomicon.loc("items");
    public static final Identifier KNOWLEDGE = Modonomicon.loc("knowledge");
    public static final Identifier PROGRESS = Modonomicon.loc("progress");
    public static final Identifier EFFECTS = Modonomicon.loc("effects");

    /**
     * The sprite of the research group, also the fallback icon of custom groups.
     */
    public static final Identifier RESEARCH_ICON = icon("research");

    private static final String TRANSLATION_PREFIX = "research_group";

    private ResearchGroups() {
    }

    /**
     * The translation key of a group title, e.g. {@code research_group.modonomicon.items}. The description uses the same key with a {@code .description} suffix.
     */
    public static String translationKey(Identifier groupId) {
        return Util.makeDescriptionId(TRANSLATION_PREFIX, groupId);
    }

    /**
     * The built-in groups, in declaration order.
     */
    public static List<ResearchGroupDefinition> builtins() {
        return List.of(
                builtin(RESEARCH, "research", 0),
                builtin(ITEMS, "items", 10, TriggerTypeRegistry.ITEM_CRAFTED.id(), TriggerTypeRegistry.ITEM_ACQUIRED.id()),
                builtin(KNOWLEDGE, "knowledge", 20, TriggerTypeRegistry.ENTRY_VIEWED_ONCE.id(), TriggerTypeRegistry.ADVANCEMENT.id(), TriggerTypeRegistry.ITEM_USED.id()),
                builtin(PROGRESS, "progress", 30),
                builtin(EFFECTS, "effects", 100)
        );
    }

    private static ResearchGroupDefinition builtin(Identifier id, String iconName, int sortOrder, Identifier... triggerTypes) {
        return new ResearchGroupDefinition(
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