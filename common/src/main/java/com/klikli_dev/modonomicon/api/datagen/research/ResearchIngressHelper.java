/*
 * SPDX-FileCopyrightText: 2026 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.api.datagen.research;

import com.klikli_dev.modonomicon.data.TriggerType;
import com.klikli_dev.modonomicon.research.hook.NoTriggerTarget;
import com.klikli_dev.modonomicon.research.item.ResearchItemMatcher;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.level.ItemLike;

/**
 * Fluent helper for authoring research hooks under its old name.
 *
 * @deprecated Use {@link ResearchHookHelper} instead. Will be removed in Minecraft 26.3.
 */
@Deprecated(forRemoval = true)
public final class ResearchIngressHelper {
    private final ResearchDataBuilder research;

    /**
     * @deprecated Use {@link ResearchHookHelper} instead. Will be removed in Minecraft 26.3.
     */
    @Deprecated(forRemoval = true)
    public ResearchIngressHelper(ResearchDataBuilder research) {
        this.research = research;
    }

    /**
     * @deprecated Use {@link ResearchHookHelper#onEntryViewedOnce(Identifier)} instead. Will be removed in Minecraft 26.3.
     */
    @Deprecated(forRemoval = true)
    public EntryViewedOnceIngress onEntryViewedOnce(Identifier entryId) {
        return new EntryViewedOnceIngress(this.research, entryId);
    }

    /**
     * @deprecated Use {@link ResearchHookHelper#onAdvancementEarned(Identifier)} instead. Will be removed in Minecraft 26.3.
     */
    @Deprecated(forRemoval = true)
    public AdvancementEarnedIngress onAdvancementEarned(Identifier advancementId) {
        return new AdvancementEarnedIngress(this.research, advancementId);
    }

    /**
     * @deprecated Use {@link ResearchHookHelper#onItemCrafted(ItemStackTemplate)} instead. Will be removed in Minecraft 26.3.
     */
    @Deprecated(forRemoval = true)
    public ItemCraftedIngress onItemCrafted(ItemStackTemplate targetItem) {
        return new ItemCraftedIngress(this.research, targetItem);
    }

    /**
     * @deprecated Use {@link ResearchHookHelper#onItemCrafted(ItemLike)} instead. Will be removed in Minecraft 26.3.
     */
    @Deprecated(forRemoval = true)
    public ItemCraftedIngress onItemCrafted(ItemLike targetItem) {
        return new ItemCraftedIngress(this.research, ResearchItemMatcher.of(targetItem));
    }

    /**
     * @deprecated Use {@link ResearchHookHelper#onItemCrafted(TagKey)} instead. Will be removed in Minecraft 26.3.
     */
    @Deprecated(forRemoval = true)
    public ItemCraftedIngress onItemCrafted(TagKey<Item> targetTag) {
        return new ItemCraftedIngress(this.research, ResearchItemMatcher.tag(targetTag));
    }

    /**
     * @deprecated Use {@link ResearchHookHelper#onItemCrafted(ResearchItemMatcher)} instead. Will be removed in Minecraft 26.3.
     */
    @Deprecated(forRemoval = true)
    public ItemCraftedIngress onItemCrafted(ResearchItemMatcher targetItem) {
        return new ItemCraftedIngress(this.research, targetItem);
    }

    /**
     * @deprecated Use {@link ResearchHookHelper#onItemAcquired(ItemStackTemplate)} instead. Will be removed in Minecraft 26.3.
     */
    @Deprecated(forRemoval = true)
    public ItemAcquiredIngress onItemAcquired(ItemStackTemplate targetItem) {
        return new ItemAcquiredIngress(this.research, targetItem);
    }

    /**
     * @deprecated Use {@link ResearchHookHelper#onItemAcquired(ItemLike)} instead. Will be removed in Minecraft 26.3.
     */
    @Deprecated(forRemoval = true)
    public ItemAcquiredIngress onItemAcquired(ItemLike targetItem) {
        return new ItemAcquiredIngress(this.research, ResearchItemMatcher.of(targetItem));
    }

    /**
     * @deprecated Use {@link ResearchHookHelper#onItemAcquired(TagKey)} instead. Will be removed in Minecraft 26.3.
     */
    @Deprecated(forRemoval = true)
    public ItemAcquiredIngress onItemAcquired(TagKey<Item> targetTag) {
        return new ItemAcquiredIngress(this.research, ResearchItemMatcher.tag(targetTag));
    }

    /**
     * @deprecated Use {@link ResearchHookHelper#onItemAcquired(ResearchItemMatcher)} instead. Will be removed in Minecraft 26.3.
     */
    @Deprecated(forRemoval = true)
    public ItemAcquiredIngress onItemAcquired(ResearchItemMatcher targetItem) {
        return new ItemAcquiredIngress(this.research, targetItem);
    }

    /**
     * @deprecated Use {@link ResearchHookHelper#onItemUsed(ItemStackTemplate)} instead. Will be removed in Minecraft 26.3.
     */
    @Deprecated(forRemoval = true)
    public ItemUsedIngress onItemUsed(ItemStackTemplate targetItem) {
        return new ItemUsedIngress(this.research, targetItem);
    }

    /**
     * @deprecated Use {@link ResearchHookHelper#onItemUsed(ItemLike)} instead. Will be removed in Minecraft 26.3.
     */
    @Deprecated(forRemoval = true)
    public ItemUsedIngress onItemUsed(ItemLike targetItem) {
        return new ItemUsedIngress(this.research, ResearchItemMatcher.of(targetItem));
    }

    /**
     * @deprecated Use {@link ResearchHookHelper#onItemUsed(TagKey)} instead. Will be removed in Minecraft 26.3.
     */
    @Deprecated(forRemoval = true)
    public ItemUsedIngress onItemUsed(TagKey<Item> targetTag) {
        return new ItemUsedIngress(this.research, ResearchItemMatcher.tag(targetTag));
    }

    /**
     * @deprecated Use {@link ResearchHookHelper#onItemUsed(ResearchItemMatcher)} instead. Will be removed in Minecraft 26.3.
     */
    @Deprecated(forRemoval = true)
    public ItemUsedIngress onItemUsed(ResearchItemMatcher targetItem) {
        return new ItemUsedIngress(this.research, targetItem);
    }

    /**
     * @deprecated Use {@link ResearchHookHelper#on(TriggerType, Object)} instead. Will be removed in Minecraft 26.3.
     */
    @Deprecated(forRemoval = true)
    public <TTarget> GenericIngress<TTarget> on(TriggerType<TTarget, ?> triggerType, TTarget target) {
        return new GenericIngress<>(this.research, triggerType, target);
    }

    /**
     * @deprecated Use {@link ResearchHookHelper#on(TriggerType)} instead. Will be removed in Minecraft 26.3.
     */
    @Deprecated(forRemoval = true)
    public GenericIngress<NoTriggerTarget> on(TriggerType<NoTriggerTarget, ?> triggerType) {
        return new GenericIngress<>(this.research, triggerType, NoTriggerTarget.INSTANCE);
    }

    /**
     * @deprecated Use {@link ResearchHookHelper.EntryViewedOnceHookBuilder} instead. Will be removed in Minecraft 26.3.
     */
    @Deprecated(forRemoval = true)
    public static final class EntryViewedOnceIngress extends ResearchHookHelper.EntryViewedOnceHookBuilder {
        public EntryViewedOnceIngress(ResearchDataBuilder research, Identifier entryId) {
            super(research, entryId);
        }
    }

    /**
     * @deprecated Use {@link ResearchHookHelper.AdvancementEarnedHookBuilder} instead. Will be removed in Minecraft 26.3.
     */
    @Deprecated(forRemoval = true)
    public static final class AdvancementEarnedIngress extends ResearchHookHelper.AdvancementEarnedHookBuilder {
        public AdvancementEarnedIngress(ResearchDataBuilder research, Identifier advancementId) {
            super(research, advancementId);
        }
    }

    /**
     * @deprecated Use {@link ResearchHookHelper.ItemCraftedHookBuilder} instead. Will be removed in Minecraft 26.3.
     */
    @Deprecated(forRemoval = true)
    public static final class ItemCraftedIngress extends ResearchHookHelper.ItemCraftedHookBuilder {
        public ItemCraftedIngress(ResearchDataBuilder research, ResearchItemMatcher targetItem) {
            super(research, targetItem);
        }

        public ItemCraftedIngress(ResearchDataBuilder research, ItemStackTemplate targetItem) {
            super(research, targetItem);
        }
    }

    /**
     * @deprecated Use {@link ResearchHookHelper.ItemAcquiredHookBuilder} instead. Will be removed in Minecraft 26.3.
     */
    @Deprecated(forRemoval = true)
    public static final class ItemAcquiredIngress extends ResearchHookHelper.ItemAcquiredHookBuilder {
        public ItemAcquiredIngress(ResearchDataBuilder research, ResearchItemMatcher targetItem) {
            super(research, targetItem);
        }

        public ItemAcquiredIngress(ResearchDataBuilder research, ItemStackTemplate targetItem) {
            super(research, targetItem);
        }
    }

    /**
     * @deprecated Use {@link ResearchHookHelper.ItemUsedHookBuilder} instead. Will be removed in Minecraft 26.3.
     */
    @Deprecated(forRemoval = true)
    public static final class ItemUsedIngress extends ResearchHookHelper.ItemUsedHookBuilder {
        public ItemUsedIngress(ResearchDataBuilder research, ResearchItemMatcher targetItem) {
            super(research, targetItem);
        }

        public ItemUsedIngress(ResearchDataBuilder research, ItemStackTemplate targetItem) {
            super(research, targetItem);
        }

        private ItemUsedIngress(ResearchDataBuilder research, ResearchItemMatcher targetItem, int consume) {
            super(research, targetItem, consume);
        }

        @Override
        public ItemUsedIngress consume(int consume) {
            super.consume(consume);
            return new ItemUsedIngress(this.research, this.target, consume);
        }
    }

    /**
     * @deprecated Use {@link ResearchHookHelper.GenericHookBuilder} instead. Will be removed in Minecraft 26.3.
     */
    @Deprecated(forRemoval = true)
    public static final class GenericIngress<TTarget> extends ResearchHookHelper.GenericHookBuilder<TTarget> {
        public GenericIngress(ResearchDataBuilder research, TriggerType<TTarget, ?> triggerType, TTarget target) {
            super(research, triggerType, target);
        }
    }
}