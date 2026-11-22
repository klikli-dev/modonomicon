/*
 * SPDX-FileCopyrightText: 2026 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.api.datagen.research;

import com.klikli_dev.modonomicon.data.TriggerType;
import com.klikli_dev.modonomicon.research.data.ResearchGroups;
import com.klikli_dev.modonomicon.research.data.ResearchGroupDefinition;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Authoring-time model of a requirement group. Converts to {@link ResearchGroupDefinition} via {@link #toDefinition()}.
 * <p>
 * Use {@link ResearchGroups} ids to change the grouping of built-in groups, or declare your own group with a new id.
 * Titles and descriptions can be given as translation keys or as components. Null leaves the description unset.
 */
public class ResearchGroupModel {
    private final Identifier id;
    @Nullable
    private Identifier icon;
    @Nullable
    private Component title;
    @Nullable
    private Component description;
    private int sortOrder;
    private final List<Identifier> triggerTypes = new ArrayList<>();

    protected ResearchGroupModel(Identifier id) {
        this.id = id;
        this.title = Component.translatable(ResearchGroups.translationKey(id));
        this.description = Component.translatable(ResearchGroups.translationKey(id) + ".description");
        this.icon = ResearchGroups.RESEARCH_ICON;
    }

    /**
     * Creates a group model with the given id. The icon defaults to the research icon, the title to the translation key {@code research_group.<id>}.
     */
    public static ResearchGroupModel create(Identifier id) {
        return new ResearchGroupModel(id);
    }

    /**
     * Sets the icon, a GUI sprite id such as {@code modonomicon:modonomicon/scholarship/groups/research}.
     */
    public ResearchGroupModel withIcon(Identifier icon) {
        this.icon = icon;
        return this;
    }

    /**
     * Sets the title from a translation key.
     */
    public ResearchGroupModel withTitle(String titleKey) {
        this.title = Component.translatable(titleKey);
        return this;
    }

    public ResearchGroupModel withTitle(Component title) {
        this.title = title;
        return this;
    }

    /**
     * Sets the description from a translation key, null leaves it unset.
     */
    public ResearchGroupModel withDescription(@Nullable String descriptionKey) {
        this.description = descriptionKey != null ? Component.translatable(descriptionKey) : null;
        return this;
    }

    public ResearchGroupModel withDescription(@Nullable Component description) {
        this.description = description;
        return this;
    }

    /**
     * Sets the position of the row, rows are ordered ascending by sort order, then by id.
     */
    public ResearchGroupModel withSortOrder(int sortOrder) {
        this.sortOrder = sortOrder;
        return this;
    }

    /**
     * Makes this group the default for the hooks of the given trigger types, e.g. item crafts.
     */
    public ResearchGroupModel withTriggerTypes(TriggerType<?, ?>... triggerTypes) {
        for (var triggerType : triggerTypes) {
            this.triggerTypes.add(triggerType.id());
        }
        return this;
    }

    /**
     * Makes this group the default for the hooks of the given trigger type ids.
     */
    public ResearchGroupModel withTriggerTypeIds(Identifier... triggerTypeIds) {
        this.triggerTypes.addAll(List.of(triggerTypeIds));
        return this;
    }

    public Identifier id() {
        return this.id;
    }

    /**
     * Converts this model into the runtime group definition.
     */
    public ResearchGroupDefinition toDefinition() {
        return new ResearchGroupDefinition(
                this.id,
                this.icon,
                this.title,
                Optional.ofNullable(this.description),
                this.sortOrder,
                List.copyOf(this.triggerTypes)
        );
    }
}