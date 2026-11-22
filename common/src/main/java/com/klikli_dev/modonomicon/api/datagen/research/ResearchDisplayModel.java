/*
 * SPDX-FileCopyrightText: 2026 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.api.datagen.research;

import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.research.data.ResearchDisplay;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

/**
 * Authoring-time display data for research elements. Converts to {@link ResearchDisplay} via {@link #toResearchDisplay()}.
 *
 * <p>Every part is either set or unset (null). Unset parts keep their previous value when a display is merged
 * into another, see {@link #merge(ResearchDisplayModel)}.
 *
 * <p>Titles and descriptions can be given as translation keys ({@code withTitle(String)}), which are wrapped in
 * translatable components, or as ready-made components ({@code withTitle(Component)}). Null leaves a part unset.
 */
public class ResearchDisplayModel {
    @Nullable
    private BookIconModel icon;
    @Nullable
    private Component title;
    @Nullable
    private Component description;
    @Nullable
    private Identifier group;
    @Nullable
    private ResearchActionModel action;

    private ResearchDisplayModel() {
    }

    /**
     * Creates an empty display model, all parts unset.
     */
    public static ResearchDisplayModel create() {
        return new ResearchDisplayModel();
    }

    /**
     * Returns the copy of the existing display, or an empty display if there is none.
     */
    static ResearchDisplayModel copyOrCreate(Optional<ResearchDisplayModel> existing) {
        return existing.map(ResearchDisplayModel::copy).orElseGet(ResearchDisplayModel::create);
    }

    /**
     * Returns an independent copy of this display model.
     */
    public ResearchDisplayModel copy() {
        var copy = new ResearchDisplayModel();
        copy.icon = this.icon;
        copy.title = this.title;
        copy.description = this.description;
        copy.group = this.group;
        copy.action = this.action;
        return copy;
    }

    /**
     * Overrides the parts of this display with every part set in the other display. Parts unset in the other display
     * keep their value. Returns this model.
     */
    public ResearchDisplayModel merge(ResearchDisplayModel other) {
        if (other.icon != null) {
            this.icon = other.icon;
        }
        if (other.title != null) {
            this.title = other.title;
        }
        if (other.description != null) {
            this.description = other.description;
        }
        if (other.group != null) {
            this.group = other.group;
        }
        if (other.action != null) {
            this.action = other.action;
        }
        return this;
    }

    public ResearchDisplayModel withIcon(BookIconModel icon) {
        this.icon = icon;
        return this;
    }

    public ResearchDisplayModel withIcon(ItemLike item) {
        return this.withIcon(BookIconModel.create(item));
    }

    public ResearchDisplayModel withIcon(ItemStackTemplate stack) {
        return this.withIcon(BookIconModel.create(stack));
    }

    public ResearchDisplayModel withIcon(Identifier texture) {
        return this.withIcon(BookIconModel.create(texture));
    }

    public ResearchDisplayModel withIcon(Identifier texture, int width, int height) {
        return this.withIcon(BookIconModel.create(texture, width, height));
    }

    /**
     * Sets the title from a translation key.
     */
    public ResearchDisplayModel withTitle(@Nullable String titleKey) {
        this.title = titleKey != null ? Component.translatable(titleKey) : null;
        return this;
    }

    /**
     * Sets the title from a component.
     */
    public ResearchDisplayModel withTitle(@Nullable Component title) {
        this.title = title;
        return this;
    }

    /**
     * Sets the description from a translation key.
     */
    public ResearchDisplayModel withDescription(@Nullable String descriptionKey) {
        this.description = descriptionKey != null ? Component.translatable(descriptionKey) : null;
        return this;
    }

    /**
     * Sets the description from a component.
     */
    public ResearchDisplayModel withDescription(@Nullable Component description) {
        this.description = description;
        return this;
    }

    /**
     * Groups the element into the requirement group with the given id, e.g. {@code ResearchGroups.ITEMS}.
     */
    public ResearchDisplayModel withGroup(@Nullable Identifier group) {
        this.group = group;
        return this;
    }

    /**
     * Offers an action on the fact this display belongs to, e.g. paying to learn it. Only facts use actions.
     */
    public ResearchDisplayModel withAction(@Nullable ResearchActionModel action) {
        this.action = action;
        return this;
    }

    /**
     * Converts this model into the runtime display data. This is the only place where datagen display data is converted.
     */
    public ResearchDisplay toResearchDisplay() {
        return ResearchDisplay.of(this.icon != null ? this.icon.toBookIcon() : null, this.title, this.description)
                .withGroup(this.group)
                .withAction(this.action != null ? this.action.toResearchAction() : null);
    }

    /**
     * @deprecated Use {@link #withGroup(Identifier)} instead. Will be removed in Minecraft 26.3.
     */
    @Deprecated(forRemoval = true)
    public ResearchDisplayModel withCategory(@Nullable Identifier category) {
        return this.withGroup(category);
    }}
