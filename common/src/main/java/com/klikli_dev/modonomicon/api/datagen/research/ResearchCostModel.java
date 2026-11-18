/*
 * SPDX-FileCopyrightText: 2026 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.api.datagen.research;

import com.klikli_dev.modonomicon.research.data.ResearchCost;
import com.klikli_dev.modonomicon.research.item.ResearchItemMatcher;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Authoring-time builder for a {@link ResearchCost}, e.g. the cost of a paid action or of a manual stage completion.
 * <pre>{@code
 * ResearchCostModel.create().item(Items.PAPER).item(Items.INK_SAC).levels(2)
 * }</pre>
 */
public final class ResearchCostModel {
    private final List<ResearchCost.ItemCost> items = new ArrayList<>();
    private int levels;

    private ResearchCostModel() {
    }

    public static ResearchCostModel create() {
        return new ResearchCostModel();
    }

    /**
     * Pays one of the item.
     */
    public ResearchCostModel item(ItemLike item) {
        return this.item(item, 1);
    }

    /**
     * Pays the given count of the item.
     */
    public ResearchCostModel item(ItemLike item, int count) {
        this.items.add(new ResearchCost.ItemCost(Ingredient.of(item), count, Optional.empty()));
        return this;
    }

    /**
     * Pays the item of the stack, in the stack's count. If the stack has components, only stacks carrying them match,
     * e.g. a research note marked with custom data.
     */
    public ResearchCostModel item(ItemStackTemplate stack) {
        return this.item(ResearchItemMatcher.of(stack), stack.count());
    }

    /**
     * Pays the given count of the items the matcher accepts: a stack template (its item, and components if any) or a tag.
     */
    public ResearchCostModel item(ResearchItemMatcher matcher, int count) {
        this.items.add(matcher.value().map(
                stack -> new ResearchCost.ItemCost(
                        Ingredient.of(stack.item().value()),
                        count,
                        stack.components().isEmpty() ? Optional.<DataComponentPatch>empty() : Optional.of(stack.components())
                ),
                tag -> new ResearchCost.ItemCost(tagIngredient(tag), count, Optional.empty())
        ));
        return this;
    }

    /**
     * Takes durability from one matching damageable stack, e.g. 1 durability of shears. The stack is damaged, not consumed.
     * Any stack with remaining durability is enough, see {@code ResearchCost.ItemCost}.
     */
    public ResearchCostModel durability(ItemLike item, int durability) {
        this.items.add(new ResearchCost.ItemCost(Ingredient.of(item), 1, Optional.empty(), Optional.of(durability)));
        return this;
    }

    /**
     * Takes durability from one matching damageable stack of the tag.
     */
    public ResearchCostModel durabilityTag(TagKey<Item> tag, int durability) {
        this.items.add(new ResearchCost.ItemCost(tagIngredient(tag), 1, Optional.empty(), Optional.of(durability)));
        return this;
    }

    /**
     * Pays the given count of any item of the tag.
     */
    public ResearchCostModel tag(TagKey<Item> tag, int count) {
        return this.item(ResearchItemMatcher.tag(tag), count);
    }

    /**
     * Pays experience levels.
     */
    public ResearchCostModel levels(int levels) {
        this.levels = levels;
        return this;
    }

    /**
     * Adds the items and levels of the other cost to this cost. Returns this model.
     */
    public ResearchCostModel merge(ResearchCostModel other) {
        this.items.addAll(other.items);
        this.levels += other.levels;
        return this;
    }

    public ResearchCost toResearchCost() {
        return new ResearchCost(List.copyOf(this.items), this.levels);
    }

    private static Ingredient tagIngredient(TagKey<Item> tag) {
        return Ingredient.of(BuiltInRegistries.ITEM.get(tag).orElseThrow(
                () -> new IllegalArgumentException("Unknown item tag " + tag.location())));
    }
}
