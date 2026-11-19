/*
 * SPDX-FileCopyrightText: 2026 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.api.datagen.research;

import com.klikli_dev.modonomicon.api.datagen.book.BookEntryModel;
import com.klikli_dev.modonomicon.data.TriggerType;
import com.klikli_dev.modonomicon.registry.TriggerTypeRegistry;
import com.klikli_dev.modonomicon.research.hook.NoTriggerTarget;
import com.klikli_dev.modonomicon.research.item.ResearchItemMatcher;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.level.ItemLike;

/**
 * Fluent helper for authoring research ingress.
 *
 * Ingress is the boundary where external events such as book entry views or advancement completion
 * are translated into primitive research mutations. Ingress can grant facts or increment values;
 * research nodes then unlock from those granted facts and value thresholds.
 */
public final class ResearchIngressHelper {
    private final ResearchDataBuilder research;

    /**
     * Creates an ingress helper over the given research builder.
     */
    public ResearchIngressHelper(ResearchDataBuilder research) {
        this.research = research;
    }

    /**
     * Starts authoring ingress from the {@code entry_viewed_once} trigger family.
     *
     * @param entryId the full entry resource location, e.g. {@code modonomicon:features/condition_root}.
     *                This must match the ID passed to {@link BookEntryModel#create(Identifier, String)}.
     *                Typically constructed as {@code this.modLoc(CategoryId.ID + "/" + EntryId.ID)},
     *                using the category provider's {@code ID} and entry provider's {@code ID} constants.
     */
    public EntryViewedOnceIngress onEntryViewedOnce(Identifier entryId) {
        return new EntryViewedOnceIngress(this.research, entryId);
    }

    /**
     * Starts authoring ingress from the advancement-earned trigger family.
     */
    public AdvancementEarnedIngress onAdvancementEarned(Identifier advancementId) {
        return new AdvancementEarnedIngress(this.research, advancementId);
    }

    /**
     * Starts authoring ingress from the {@code item_crafted} trigger family for an exact item template.
     */
    public ItemCraftedIngress onItemCrafted(ItemStackTemplate targetItem) {
        return new ItemCraftedIngress(this.research, ResearchItemMatcher.of(targetItem));
    }

    /**
     * Starts authoring ingress from the {@code item_crafted} trigger family for an item or item-like.
     */
    public ItemCraftedIngress onItemCrafted(ItemLike targetItem) {
        return new ItemCraftedIngress(this.research, ResearchItemMatcher.of(targetItem));
    }

    /**
     * Starts authoring ingress from the {@code item_crafted} trigger family for any item in the given tag.
     */
    public ItemCraftedIngress onItemCrafted(TagKey<Item> targetTag) {
        return new ItemCraftedIngress(this.research, ResearchItemMatcher.tag(targetTag));
    }

    /**
     * Starts authoring ingress from the {@code item_crafted} trigger family for the given matcher.
     */
    public ItemCraftedIngress onItemCrafted(ResearchItemMatcher targetItem) {
        return new ItemCraftedIngress(this.research, targetItem);
    }

    /**
     * Starts authoring ingress from the {@code item_acquired} trigger family for an exact item template.
     */
    public ItemAcquiredIngress onItemAcquired(ItemStackTemplate targetItem) {
        return new ItemAcquiredIngress(this.research, ResearchItemMatcher.of(targetItem));
    }

    /**
     * Starts authoring ingress from the {@code item_acquired} trigger family for an item or item-like.
     */
    public ItemAcquiredIngress onItemAcquired(ItemLike targetItem) {
        return new ItemAcquiredIngress(this.research, ResearchItemMatcher.of(targetItem));
    }

    /**
     * Starts authoring ingress from the {@code item_acquired} trigger family for any item in the given tag.
     */
    public ItemAcquiredIngress onItemAcquired(TagKey<Item> targetTag) {
        return new ItemAcquiredIngress(this.research, ResearchItemMatcher.tag(targetTag));
    }

    /**
     * Starts authoring ingress from the {@code item_acquired} trigger family for the given matcher.
     */
    public ItemAcquiredIngress onItemAcquired(ResearchItemMatcher targetItem) {
        return new ItemAcquiredIngress(this.research, targetItem);
    }

    /**
     * Starts authoring ingress from the {@code item_used} trigger family for an exact item template.
     */
    public ItemUsedIngress onItemUsed(ItemStackTemplate targetItem) {
        return new ItemUsedIngress(this.research, ResearchItemMatcher.of(targetItem));
    }

    /**
     * Starts authoring ingress from the {@code item_used} trigger family for an item or item-like.
     */
    public ItemUsedIngress onItemUsed(ItemLike targetItem) {
        return new ItemUsedIngress(this.research, ResearchItemMatcher.of(targetItem));
    }

    /**
     * Starts authoring ingress from the {@code item_used} trigger family for any item in the given tag.
     */
    public ItemUsedIngress onItemUsed(TagKey<Item> targetTag) {
        return new ItemUsedIngress(this.research, ResearchItemMatcher.tag(targetTag));
    }

    /**
     * Starts authoring ingress from the {@code item_used} trigger family for the given matcher.
     */
    public ItemUsedIngress onItemUsed(ResearchItemMatcher targetItem) {
        return new ItemUsedIngress(this.research, targetItem);
    }

    /**
     * Ingress step for {@code item_crafted}.
     */
    public static final class ItemCraftedIngress extends ResearchIngress<ResearchItemMatcher> {
        /**
         * Creates an item-crafted ingress step bound to one specific item matcher.
         */
        public ItemCraftedIngress(ResearchDataBuilder research, ResearchItemMatcher targetItem) {
            super(research, TriggerTypeRegistry.ITEM_CRAFTED, targetItem);
        }

        /**
         * Creates an item-crafted ingress step bound to one exact item template.
         */
        public ItemCraftedIngress(ResearchDataBuilder research, ItemStackTemplate targetItem) {
            this(research, ResearchItemMatcher.of(targetItem));
        }
    }

    /**
     * Ingress step for {@code item_acquired}.
     */
    public static final class ItemAcquiredIngress extends ResearchIngress<ResearchItemMatcher> {
        /**
         * Creates an item-acquired ingress step bound to one specific item matcher.
         */
        public ItemAcquiredIngress(ResearchDataBuilder research, ResearchItemMatcher targetItem) {
            super(research, TriggerTypeRegistry.ITEM_ACQUIRED, targetItem);
        }

        /**
         * Creates an item-acquired ingress step bound to one exact item template.
         */
        public ItemAcquiredIngress(ResearchDataBuilder research, ItemStackTemplate targetItem) {
            this(research, ResearchItemMatcher.of(targetItem));
        }
    }

    /**
     * Ingress step for {@code item_used}. Use {@link #consume(int)} to consume items of the used stack when the hook
     * changes research state.
     */
    public static final class ItemUsedIngress extends ResearchIngress<ResearchItemMatcher> {
        private final int consume;

        /**
         * Creates an item-used ingress step bound to one specific item matcher.
         */
        public ItemUsedIngress(ResearchDataBuilder research, ResearchItemMatcher targetItem) {
            this(research, targetItem, 0);
        }

        /**
         * Creates an item-used ingress step bound to one exact item template.
         */
        public ItemUsedIngress(ResearchDataBuilder research, ItemStackTemplate targetItem) {
            this(research, ResearchItemMatcher.of(targetItem));
        }

        private ItemUsedIngress(ResearchDataBuilder research, ResearchItemMatcher targetItem, int consume) {
            super(research, TriggerTypeRegistry.ITEM_USED, targetItem);
            this.consume = consume;
        }

        /**
         * Returns a copy of this ingress that consumes the given number of items of the used stack when the use changes
         * research state. A use that changes nothing consumes nothing. The count must not be negative.
         *
         * @throws IllegalArgumentException if consume is negative
         */
        public ItemUsedIngress consume(int consume) {
            if (consume < 0) {
                throw new IllegalArgumentException("consume must not be negative, was " + consume);
            }
            return new ItemUsedIngress(this.research, this.target, consume);
        }

        @Override
        protected int consumeCount() {
            return this.consume;
        }
    }

    /**
     * Starts authoring ingress from a custom trigger type.
     * This is the generic entry point for third-party trigger types.
     */
    public <TTarget> GenericIngress<TTarget> on(TriggerType<TTarget, ?> triggerType, TTarget target) {
        return new GenericIngress<>(this.research, triggerType, target);
    }

    /**
     * Starts authoring ingress from a custom trigger type without authored target data.
     */
    public GenericIngress<NoTriggerTarget> on(TriggerType<NoTriggerTarget, ?> triggerType) {
        return new GenericIngress<>(this.research, triggerType, NoTriggerTarget.INSTANCE);
    }

    /**
     * Ingress step for {@code entry_viewed_once}.
     */
    public static final class EntryViewedOnceIngress extends ResearchIngress<Identifier> {
        /**
         * Creates an entry-viewed ingress step bound to one specific book entry id.
         */
        public EntryViewedOnceIngress(ResearchDataBuilder research, Identifier entryId) {
            super(research, TriggerTypeRegistry.ENTRY_VIEWED_ONCE, entryId);
        }
    }

    /**
     * Ingress step for advancement completion.
     */
    public static final class AdvancementEarnedIngress extends ResearchIngress<Identifier> {
        /**
         * Creates an advancement-earned ingress step bound to one specific advancement id.
         */
        public AdvancementEarnedIngress(ResearchDataBuilder research, Identifier advancementId) {
            super(research, TriggerTypeRegistry.ADVANCEMENT, advancementId);
        }
    }

    /**
     * Generic ingress step for custom trigger types.
     */
    public static final class GenericIngress<TTarget> extends ResearchIngress<TTarget> {
        /**
         * Creates a generic ingress step bound to one specific trigger type and target.
         */
        public GenericIngress(ResearchDataBuilder research, TriggerType<TTarget, ?> triggerType, TTarget target) {
            super(research, triggerType, target);
        }
    }
}
