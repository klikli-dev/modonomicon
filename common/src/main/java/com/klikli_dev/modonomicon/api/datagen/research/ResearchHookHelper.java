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
 * Fluent helper for authoring research hooks.
 *
 * HookBuilder is the boundary where external events such as book entry views or advancement completion
 * are translated into primitive research mutations. HookBuilder can grant facts or increment values;
 * research nodes then unlock from those granted facts and value thresholds.
 */
public final class ResearchHookHelper {
    private final ResearchDataBuilder research;

    /**
     * Creates a hook helper over the given research builder.
     */
    public ResearchHookHelper(ResearchDataBuilder research) {
        this.research = research;
    }

    /**
     * Starts authoring hookss for the {@code entry_viewed_once} trigger family.
     *
     * @param entryId the full entry resource location, e.g. {@code modonomicon:features/condition_root}.
     *                This must match the ID passed to {@link BookEntryModel#create(Identifier, String)}.
     *                Typically constructed as {@code this.modLoc(CategoryId.ID + "/" + EntryId.ID)},
     *                using the category provider's {@code ID} and entry provider's {@code ID} constants.
     */
    public EntryViewedOnceHookBuilder onEntryViewedOnce(Identifier entryId) {
        return new EntryViewedOnceHookBuilder(this.research, entryId);
    }

    /**
     * Starts authoring hookss for the advancement-earned trigger family.
     */
    public AdvancementEarnedHookBuilder onAdvancementEarned(Identifier advancementId) {
        return new AdvancementEarnedHookBuilder(this.research, advancementId);
    }

    /**
     * Starts authoring hookss for the {@code item_crafted} trigger family for an exact item template.
     */
    public ItemCraftedHookBuilder onItemCrafted(ItemStackTemplate targetItem) {
        return new ItemCraftedHookBuilder(this.research, ResearchItemMatcher.of(targetItem));
    }

    /**
     * Starts authoring hookss for the {@code item_crafted} trigger family for an item or item-like.
     */
    public ItemCraftedHookBuilder onItemCrafted(ItemLike targetItem) {
        return new ItemCraftedHookBuilder(this.research, ResearchItemMatcher.of(targetItem));
    }

    /**
     * Starts authoring hookss for the {@code item_crafted} trigger family for any item in the given tag.
     */
    public ItemCraftedHookBuilder onItemCrafted(TagKey<Item> targetTag) {
        return new ItemCraftedHookBuilder(this.research, ResearchItemMatcher.tag(targetTag));
    }

    /**
     * Starts authoring hookss for the {@code item_crafted} trigger family for the given matcher.
     */
    public ItemCraftedHookBuilder onItemCrafted(ResearchItemMatcher targetItem) {
        return new ItemCraftedHookBuilder(this.research, targetItem);
    }

    /**
     * Starts authoring hookss for the {@code item_acquired} trigger family for an exact item template.
     */
    public ItemAcquiredHookBuilder onItemAcquired(ItemStackTemplate targetItem) {
        return new ItemAcquiredHookBuilder(this.research, ResearchItemMatcher.of(targetItem));
    }

    /**
     * Starts authoring hookss for the {@code item_acquired} trigger family for an item or item-like.
     */
    public ItemAcquiredHookBuilder onItemAcquired(ItemLike targetItem) {
        return new ItemAcquiredHookBuilder(this.research, ResearchItemMatcher.of(targetItem));
    }

    /**
     * Starts authoring hookss for the {@code item_acquired} trigger family for any item in the given tag.
     */
    public ItemAcquiredHookBuilder onItemAcquired(TagKey<Item> targetTag) {
        return new ItemAcquiredHookBuilder(this.research, ResearchItemMatcher.tag(targetTag));
    }

    /**
     * Starts authoring hookss for the {@code item_acquired} trigger family for the given matcher.
     */
    public ItemAcquiredHookBuilder onItemAcquired(ResearchItemMatcher targetItem) {
        return new ItemAcquiredHookBuilder(this.research, targetItem);
    }

    /**
     * Starts authoring hookss for the {@code item_used} trigger family for an exact item template.
     */
    public ItemUsedHookBuilder onItemUsed(ItemStackTemplate targetItem) {
        return new ItemUsedHookBuilder(this.research, ResearchItemMatcher.of(targetItem));
    }

    /**
     * Starts authoring hookss for the {@code item_used} trigger family for an item or item-like.
     */
    public ItemUsedHookBuilder onItemUsed(ItemLike targetItem) {
        return new ItemUsedHookBuilder(this.research, ResearchItemMatcher.of(targetItem));
    }

    /**
     * Starts authoring hookss for the {@code item_used} trigger family for any item in the given tag.
     */
    public ItemUsedHookBuilder onItemUsed(TagKey<Item> targetTag) {
        return new ItemUsedHookBuilder(this.research, ResearchItemMatcher.tag(targetTag));
    }

    /**
     * Starts authoring hookss for the {@code item_used} trigger family for the given matcher.
     */
    public ItemUsedHookBuilder onItemUsed(ResearchItemMatcher targetItem) {
        return new ItemUsedHookBuilder(this.research, targetItem);
    }

    /**
     * HookBuilder step for {@code item_crafted}.
     */
    public static final class ItemCraftedHookBuilder extends ResearchHookBuilder<ResearchItemMatcher> {
        /**
         * Creates an item-crafted hook step bound to one specific item matcher.
         */
        public ItemCraftedHookBuilder(ResearchDataBuilder research, ResearchItemMatcher targetItem) {
            super(research, TriggerTypeRegistry.ITEM_CRAFTED, targetItem);
        }

        /**
         * Creates an item-crafted hook step bound to one exact item template.
         */
        public ItemCraftedHookBuilder(ResearchDataBuilder research, ItemStackTemplate targetItem) {
            this(research, ResearchItemMatcher.of(targetItem));
        }
    }

    /**
     * HookBuilder step for {@code item_acquired}.
     */
    public static final class ItemAcquiredHookBuilder extends ResearchHookBuilder<ResearchItemMatcher> {
        /**
         * Creates an item-acquired hook step bound to one specific item matcher.
         */
        public ItemAcquiredHookBuilder(ResearchDataBuilder research, ResearchItemMatcher targetItem) {
            super(research, TriggerTypeRegistry.ITEM_ACQUIRED, targetItem);
        }

        /**
         * Creates an item-acquired hook step bound to one exact item template.
         */
        public ItemAcquiredHookBuilder(ResearchDataBuilder research, ItemStackTemplate targetItem) {
            this(research, ResearchItemMatcher.of(targetItem));
        }
    }

    /**
     * HookBuilder step for {@code item_used}. Use {@link #consume(int)} to consume items of the used stack when the hook
     * changes research state.
     */
    public static final class ItemUsedHookBuilder extends ResearchHookBuilder<ResearchItemMatcher> {
        private final int consume;

        /**
         * Creates an item-used hook step bound to one specific item matcher.
         */
        public ItemUsedHookBuilder(ResearchDataBuilder research, ResearchItemMatcher targetItem) {
            this(research, targetItem, 0);
        }

        /**
         * Creates an item-used hook step bound to one exact item template.
         */
        public ItemUsedHookBuilder(ResearchDataBuilder research, ItemStackTemplate targetItem) {
            this(research, ResearchItemMatcher.of(targetItem));
        }

        private ItemUsedHookBuilder(ResearchDataBuilder research, ResearchItemMatcher targetItem, int consume) {
            super(research, TriggerTypeRegistry.ITEM_USED, targetItem);
            this.consume = consume;
        }

        /**
         * Returns a copy of this hook that consumes the given number of items of the used stack when the use changes
         * research state. A use that changes nothing consumes nothing. The count must not be negative.
         *
         * @throws IllegalArgumentException if consume is negative
         */
        public ItemUsedHookBuilder consume(int consume) {
            if (consume < 0) {
                throw new IllegalArgumentException("consume must not be negative, was " + consume);
            }
            return new ItemUsedHookBuilder(this.research, this.target, consume);
        }

        @Override
        protected int consumeCount() {
            return this.consume;
        }
    }

    /**
     * Starts authoring hooks from a custom trigger type.
     * This is the generic entry point for third-party trigger types.
     */
    public <TTarget> GenericHookBuilder<TTarget> on(TriggerType<TTarget, ?> triggerType, TTarget target) {
        return new GenericHookBuilder<>(this.research, triggerType, target);
    }

    /**
     * Starts authoring hooks from a custom trigger type without authored target data.
     */
    public GenericHookBuilder<NoTriggerTarget> on(TriggerType<NoTriggerTarget, ?> triggerType) {
        return new GenericHookBuilder<>(this.research, triggerType, NoTriggerTarget.INSTANCE);
    }

    /**
     * HookBuilder step for {@code entry_viewed_once}.
     */
    public static final class EntryViewedOnceHookBuilder extends ResearchHookBuilder<Identifier> {
        /**
         * Creates an entry-viewed hook step bound to one specific book entry id.
         */
        public EntryViewedOnceHookBuilder(ResearchDataBuilder research, Identifier entryId) {
            super(research, TriggerTypeRegistry.ENTRY_VIEWED_ONCE, entryId);
        }
    }

    /**
     * HookBuilder step for advancement completion.
     */
    public static final class AdvancementEarnedHookBuilder extends ResearchHookBuilder<Identifier> {
        /**
         * Creates an advancement-earned hook step bound to one specific advancement id.
         */
        public AdvancementEarnedHookBuilder(ResearchDataBuilder research, Identifier advancementId) {
            super(research, TriggerTypeRegistry.ADVANCEMENT, advancementId);
        }
    }

    /**
     * Generic hook step for custom trigger types.
     */
    public static final class GenericHookBuilder<TTarget> extends ResearchHookBuilder<TTarget> {
        /**
         * Creates a generic hook step bound to one specific trigger type and target.
         */
        public GenericHookBuilder(ResearchDataBuilder research, TriggerType<TTarget, ?> triggerType, TTarget target) {
            super(research, triggerType, target);
        }
    }
}
