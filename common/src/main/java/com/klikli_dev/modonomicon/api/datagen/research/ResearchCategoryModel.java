/*
 * SPDX-FileCopyrightText: 2026 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.api.datagen.research;

import com.klikli_dev.modonomicon.data.TriggerType;
import com.klikli_dev.modonomicon.research.data.ResearchCategories;
import com.klikli_dev.modonomicon.research.data.ResearchCategoryDefinition;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Authoring-time model of a requirement category. Converts to {@link ResearchCategoryDefinition} via {@link #toDefinition()}.
 * <p>
 * Use {@link ResearchCategories} ids to change the grouping of built-in categories, or declare your own category with a new id.
 * Titles and descriptions can be given as translation keys or as components. Null leaves the description unset.
 */
public class ResearchCategoryModel {
    private final Identifier id;
    @Nullable
    private Identifier icon;
    @Nullable
    private Component title;
    @Nullable
    private Component description;
    private int sortOrder;
    private final List<Identifier> triggerTypes = new ArrayList<>();

    private ResearchCategoryModel(Identifier id) {
        this.id = id;
    }

    /**
     * Creates a category model with the given id. The icon defaults to the research icon, the title to the translation key {@code research_category.<id>}.
     */
    public static ResearchCategoryModel create(Identifier id) {
        var model = new ResearchCategoryModel(id);
        model.title = Component.translatable(ResearchCategories.translationKey(id));
        model.description = Component.translatable(ResearchCategories.translationKey(id) + ".description");
        model.icon = ResearchCategories.RESEARCH_ICON;
        return model;
    }

    /**
     * Sets the icon, a GUI sprite id such as {@code modonomicon:modonomicon/requirement_categories/research}.
     */
    public ResearchCategoryModel withIcon(Identifier icon) {
        this.icon = icon;
        return this;
    }

    /**
     * Sets the title from a translation key.
     */
    public ResearchCategoryModel withTitle(String titleKey) {
        this.title = Component.translatable(titleKey);
        return this;
    }

    public ResearchCategoryModel withTitle(Component title) {
        this.title = title;
        return this;
    }

    /**
     * Sets the description from a translation key, null leaves it unset.
     */
    public ResearchCategoryModel withDescription(@Nullable String descriptionKey) {
        this.description = descriptionKey != null ? Component.translatable(descriptionKey) : null;
        return this;
    }

    public ResearchCategoryModel withDescription(@Nullable Component description) {
        this.description = description;
        return this;
    }

    /**
     * Sets the position of the row, rows are ordered ascending by sort order, then by id.
     */
    public ResearchCategoryModel withSortOrder(int sortOrder) {
        this.sortOrder = sortOrder;
        return this;
    }

    /**
     * Makes this category the default for the hooks of the given trigger types, e.g. item crafts.
     */
    public ResearchCategoryModel withTriggerTypes(TriggerType<?, ?>... triggerTypes) {
        for (var triggerType : triggerTypes) {
            this.triggerTypes.add(triggerType.id());
        }
        return this;
    }

    /**
     * Makes this category the default for the hooks of the given trigger type ids.
     */
    public ResearchCategoryModel withTriggerTypeIds(Identifier... triggerTypeIds) {
        this.triggerTypes.addAll(List.of(triggerTypeIds));
        return this;
    }

    public Identifier id() {
        return this.id;
    }

    /**
     * Converts this model into the runtime category definition.
     */
    public ResearchCategoryDefinition toDefinition() {
        return new ResearchCategoryDefinition(
                this.id,
                this.icon,
                this.title,
                Optional.ofNullable(this.description),
                this.sortOrder,
                List.copyOf(this.triggerTypes)
        );
    }
}