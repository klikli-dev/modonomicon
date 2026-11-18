/*
 * SPDX-FileCopyrightText: 2026 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.api.datagen.research;

import com.klikli_dev.modonomicon.research.cost.DurabilityCostEntry;
import com.klikli_dev.modonomicon.research.cost.ItemCostEntry;
import com.klikli_dev.modonomicon.research.cost.LevelsCostEntry;
import com.klikli_dev.modonomicon.research.cost.ResearchCostEntry;
import com.klikli_dev.modonomicon.research.data.ResearchCost;
import com.klikli_dev.modonomicon.research.item.ResearchItemMatcher;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.level.ItemLike;

import java.util.ArrayList;
import java.util.List;

/**
 * Authoring-time builder for a {@link ResearchCost}, e.g. the cost of a paid action or of a manual stage completion.
 * Every call adds a typed entry, in call order.
 * <pre>{@code
 * ResearchCostModel.create().item(Items.PAPER).durability(Items.SHEARS, 1).levels(2)
 * }</pre>
 */
public final class ResearchCostModel {
    private final List<ResearchCostEntry> entries = new ArrayList<>();

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
        return this.item(ResearchItemMatcher.of(item), count);
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
        return this.entry(new ItemCostEntry(matcher, count));
    }

    /**
     * Pays the given count of any item of the tag.
     */
    public ResearchCostModel tag(TagKey<Item> tag, int count) {
        return this.item(ResearchItemMatcher.tag(tag), count);
    }

    /**
     * Takes durability from one matching damageable stack, e.g. 1 durability of shears. The stack is damaged, not consumed.
     * Any stack with remaining durability is enough, see {@link DurabilityCostEntry}.
     */
    public ResearchCostModel durability(ItemLike item, int durability) {
        return this.durability(ResearchItemMatcher.of(item), durability);
    }

    /**
     * Takes durability from one matching damageable stack of the tag.
     */
    public ResearchCostModel durabilityTag(TagKey<Item> tag, int durability) {
        return this.durability(ResearchItemMatcher.tag(tag), durability);
    }

    /**
     * Takes durability from one damageable stack the matcher accepts.
     */
    public ResearchCostModel durability(ResearchItemMatcher matcher, int durability) {
        return this.entry(new DurabilityCostEntry(matcher, durability));
    }

    /**
     * Pays experience levels. Replaces levels set before, so the last call wins.
     */
    public ResearchCostModel levels(int levels) {
        this.entries.removeIf(entry -> entry instanceof LevelsCostEntry);
        if (levels > 0) {
            this.entries.add(new LevelsCostEntry(levels));
        }
        return this;
    }

    /**
     * Adds an entry of any cost type, e.g. one of a third-party type registered with {@code ResearchCostTypeRegistry.register}.
     */
    public ResearchCostModel entry(ResearchCostEntry entry) {
        this.entries.add(entry);
        return this;
    }

    /**
     * Adds the entries of the other cost to this cost. Returns this model.
     */
    public ResearchCostModel merge(ResearchCostModel other) {
        this.entries.addAll(other.entries);
        return this;
    }

    public ResearchCost toResearchCost() {
        return new ResearchCost(List.copyOf(this.entries));
    }
}
