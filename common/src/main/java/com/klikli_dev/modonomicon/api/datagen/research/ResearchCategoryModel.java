/*
 * SPDX-FileCopyrightText: 2026 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.api.datagen.research;

import com.klikli_dev.modonomicon.data.TriggerType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.Nullable;

/**
 * Research group builder under its old name.
 *
 * @deprecated Use {@link ResearchGroupModel} instead. Will be removed in Minecraft 26.3.
 */
@Deprecated(forRemoval = true)
public class ResearchCategoryModel extends ResearchGroupModel {
    protected ResearchCategoryModel(Identifier id) {
        super(id);
    }

    /**
     * @deprecated Use {@link ResearchGroupModel#create(Identifier)} instead. Will be removed in Minecraft 26.3.
     */
    @Deprecated(forRemoval = true)
    public static ResearchCategoryModel create(Identifier id) {
        return new ResearchCategoryModel(id);
    }

    /**
     * @deprecated Use {@link ResearchGroupModel#withIcon(Identifier)} instead. Will be removed in Minecraft 26.3.
     */
    @Deprecated(forRemoval = true)
    @Override
    public ResearchCategoryModel withIcon(Identifier icon) {
        super.withIcon(icon);
        return this;
    }

    /**
     * @deprecated Use {@link ResearchGroupModel#withTitle(String)} instead. Will be removed in Minecraft 26.3.
     */
    @Deprecated(forRemoval = true)
    @Override
    public ResearchCategoryModel withTitle(String titleKey) {
        super.withTitle(titleKey);
        return this;
    }

    /**
     * @deprecated Use {@link ResearchGroupModel#withTitle(Component)} instead. Will be removed in Minecraft 26.3.
     */
    @Deprecated(forRemoval = true)
    @Override
    public ResearchCategoryModel withTitle(Component title) {
        super.withTitle(title);
        return this;
    }

    /**
     * @deprecated Use {@link ResearchGroupModel#withDescription(String)} instead. Will be removed in Minecraft 26.3.
     */
    @Deprecated(forRemoval = true)
    @Override
    public ResearchCategoryModel withDescription(@Nullable String descriptionKey) {
        super.withDescription(descriptionKey);
        return this;
    }

    /**
     * @deprecated Use {@link ResearchGroupModel#withDescription(Component)} instead. Will be removed in Minecraft 26.3.
     */
    @Deprecated(forRemoval = true)
    @Override
    public ResearchCategoryModel withDescription(@Nullable Component description) {
        super.withDescription(description);
        return this;
    }

    /**
     * @deprecated Use {@link ResearchGroupModel#withSortOrder(int)} instead. Will be removed in Minecraft 26.3.
     */
    @Deprecated(forRemoval = true)
    @Override
    public ResearchCategoryModel withSortOrder(int sortOrder) {
        super.withSortOrder(sortOrder);
        return this;
    }

    /**
     * @deprecated Use {@link ResearchGroupModel#withTriggerTypes(TriggerType...)} instead. Will be removed in Minecraft 26.3.
     */
    @Deprecated(forRemoval = true)
    @Override
    public ResearchCategoryModel withTriggerTypes(TriggerType<?, ?>... triggerTypes) {
        super.withTriggerTypes(triggerTypes);
        return this;
    }

    /**
     * @deprecated Use {@link ResearchGroupModel#withTriggerTypeIds(Identifier...)} instead. Will be removed in Minecraft 26.3.
     */
    @Deprecated(forRemoval = true)
    @Override
    public ResearchCategoryModel withTriggerTypeIds(Identifier... triggerTypeIds) {
        super.withTriggerTypeIds(triggerTypeIds);
        return this;
    }
}