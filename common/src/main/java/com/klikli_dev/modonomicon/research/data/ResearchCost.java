/*
 * SPDX-FileCopyrightText: 2026 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.research.data;

import com.klikli_dev.modonomicon.book.associated.AssociatedItemMatcher;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Items, durability and experience levels a player pays, e.g. for a paid research action or a manually completed stage.
 * Creative players (infinite materials) can afford every cost and pay nothing.
 *
 * @param items item costs, each one paid from any matching stacks in the player's inventory or as durability of a tool
 * @param levels experience levels to pay
 */
public record ResearchCost(List<ItemCost> items, int levels) {

    public static final ResearchCost EMPTY = new ResearchCost(List.of(), 0);

    /**
     * Pays {@code count} items matching {@code ingredient}, or {@code durability} of one matching tool.
     * <p>
     * If components are set, a stack must also carry every one of them with an equal value, like an item_acquired hook.
     * Stacks with additional components still match, so a plain paper does not match a marked research note.
     * <p>
     * A durability entry targets exactly one damageable stack (its count must stay 1), it is not consumed but damaged.
     * Any stack with remaining durability is enough, the tool may break.
     *
     * @param ingredient the item or tag accepted
     * @param count the number of items consumed, always 1 for durability entries
     * @param components optional component patch the stack must carry
     * @param durability optional durability points to take from one matching stack, instead of items
     */
    public record ItemCost(Ingredient ingredient, int count, Optional<DataComponentPatch> components, Optional<Integer> durability) {

        public ItemCost(Ingredient ingredient, int count, Optional<DataComponentPatch> components) {
            this(ingredient, count, components, Optional.empty());
        }

        public ItemCost(Ingredient ingredient, int count) {
            this(ingredient, count, Optional.empty(), Optional.empty());
        }

        public static final Codec<ItemCost> CODEC = RecordCodecBuilder.<ItemCost>create(instance -> instance.group(
                Ingredient.CODEC.fieldOf("item").forGetter(ItemCost::ingredient),
                Codec.intRange(1, 6400).optionalFieldOf("count", 1).forGetter(ItemCost::count),
                DataComponentPatch.CODEC.optionalFieldOf("components").forGetter(ItemCost::components),
                Codec.intRange(1, Integer.MAX_VALUE).optionalFieldOf("durability").forGetter(ItemCost::durability)
        ).apply(instance, ItemCost::new)).validate(cost ->
                cost.durability().isPresent() && cost.count() != 1
                        ? DataResult.error(() -> "A durability cost takes one stack, its count must be 1 or omitted")
                        : DataResult.success(cost)
        );

        public static final StreamCodec<RegistryFriendlyByteBuf, ItemCost> STREAM_CODEC =
                StreamCodec.composite(
                        Ingredient.CONTENTS_STREAM_CODEC,
                        ItemCost::ingredient,
                        ByteBufCodecs.VAR_INT,
                        ItemCost::count,
                        ByteBufCodecs.optional(DataComponentPatch.STREAM_CODEC),
                        ItemCost::components,
                        ByteBufCodecs.optional(ByteBufCodecs.VAR_INT),
                        ItemCost::durability,
                        ItemCost::new
                );

        /**
         * True if the stack is accepted by this cost entry: the ingredient accepts it and it carries the required components.
         */
        public boolean matches(ItemStack stack) {
            if (!this.ingredient.test(stack)) {
                return false;
            }
            return this.components.map(patch -> AssociatedItemMatcher.matchesComponents(stack, patch)).orElse(true);
        }

        /**
         * True if the stack can pay this durability entry: it matches, is damageable and not broken yet.
         */
        public boolean matchesDurability(ItemStack stack) {
            return this.matches(stack) && stack.isDamageableItem() && !stack.isBroken();
        }

        /**
         * The number of items the player holds that this entry accepts. Always 0 for durability entries.
         */
        public int held(Player player) {
            if (this.durability.isPresent()) {
                return 0;
            }
            var inventory = player.getInventory();
            int held = 0;
            for (int slot = 0; slot < inventory.getContainerSize(); slot++) {
                var stack = inventory.getItem(slot);
                if (this.matches(stack)) {
                    held += stack.getCount();
                }
            }
            return held;
        }

        /**
         * True if the player holds a stack that can pay this durability entry.
         */
        public boolean heldDurability(Player player) {
            var inventory = player.getInventory();
            for (int slot = 0; slot < inventory.getContainerSize(); slot++) {
                if (this.matchesDurability(inventory.getItem(slot))) {
                    return true;
                }
            }
            return false;
        }

        /**
         * A stack of the first item the entry accepts with its components, used as icon. Empty if the entry accepts no item.
         */
        @SuppressWarnings("deprecation")
        public ItemStack iconStack() {
            var patch = this.components.orElse(DataComponentPatch.EMPTY);
            return this.ingredient.items().findFirst()
                    .map(holder -> new ItemStack(holder, 1, patch))
                    .orElse(ItemStack.EMPTY);
        }

        /**
         * True if both entries target the same ingredient, components and durability, regardless of count.
         */
        public boolean sameEntry(ItemCost other) {
            return this.ingredient.equals(other.ingredient)
                    && this.components.equals(other.components)
                    && this.durability.equals(other.durability);
        }

        /**
         * The name of the first item the entry accepts, with its components applied, e.g. the custom name of a research note.
         */
        @SuppressWarnings("deprecation")
        public Component displayName() {
            var patch = this.components.orElse(DataComponentPatch.EMPTY);
            return this.ingredient.items().findFirst()
                    .map(holder -> new ItemStack(holder, 1, patch).getHoverName())
                    .orElseGet(() -> Component.literal("?"));
        }
    }

    public ResearchCost {
        items = List.copyOf(items);
        if (levels < 0) {
            throw new IllegalArgumentException("Research cost levels must not be negative, got " + levels);
        }
    }

    public static final Codec<ResearchCost> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ItemCost.CODEC.listOf().optionalFieldOf("items", List.of()).forGetter(ResearchCost::items),
            Codec.intRange(0, Integer.MAX_VALUE).optionalFieldOf("levels", 0).forGetter(ResearchCost::levels)
    ).apply(instance, ResearchCost::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, ResearchCost> STREAM_CODEC =
            StreamCodec.composite(
                    ItemCost.STREAM_CODEC.apply(ByteBufCodecs.list()),
                    ResearchCost::items,
                    ByteBufCodecs.VAR_INT,
                    ResearchCost::levels,
                    ResearchCost::new
            );

    public boolean isEmpty() {
        return this.items.isEmpty() && this.levels == 0;
    }

    /**
     * Returns the part of this cost the player cannot pay. Empty if the player can afford the whole cost.
     * Durability entries are planned first, then item entries in declaration order. A stack is used at most once.
     */
    public ResearchCost missing(Player player) {
        return this.plan(player).missing();
    }

    public boolean canAfford(Player player) {
        return this.missing(player).isEmpty();
    }

    /**
     * Takes the cost from the player if they can afford it. Does nothing and returns false otherwise.
     * Creative players are never charged. Durability is applied to the chosen stacks the vanilla way, see {@link #damage}.
     */
    public boolean consume(Player player) {
        if (player.hasInfiniteMaterials()) {
            return true;
        }

        var plan = this.plan(player);
        if (!plan.missing().isEmpty()) {
            return false;
        }

        var inventory = player.getInventory();
        for (var take : plan.takes()) {
            inventory.removeItem(take.slot(), take.count());
        }
        for (var damage : plan.damages()) {
            damage(player, damage.slot(), damage.amount());
        }
        inventory.setChanged();

        if (this.levels > 0) {
            player.giveExperienceLevels(-this.levels);
        }
        return true;
    }

    /**
     * Damages the stack in the slot by the amount, the way vanilla tools wear down: Unbreaking applies and the stack breaks
     * with its break effects when depleted. Stacks in equipment slots use the equipment overload (break event for the slot),
     * other inventory stacks play the break sound and particles themselves. Only applied on the server.
     */
    private static void damage(Player player, int slot, int amount) {
        if (!(player instanceof ServerPlayer serverPlayer)) {
            return;
        }
        var inventory = serverPlayer.getInventory();
        var stack = inventory.getItem(slot);
        var level = serverPlayer.level();

        var equipmentSlot = equipmentSlot(inventory, slot);
        if (equipmentSlot != null) {
            stack.hurtAndBreak(amount, serverPlayer, equipmentSlot);
            return;
        }

        var broken = stack.copy();
        stack.hurtAndBreak(amount, (ServerLevel) level, serverPlayer, item -> {
            var pos = serverPlayer.position();
            level.playSound(null, pos.x, pos.y, pos.z, SoundEvents.ITEM_BREAK, SoundSource.PLAYERS, 0.8F, 0.8F + level.getRandom().nextFloat() * 0.4F);
            ((ServerLevel) level).sendParticles(new ItemParticleOption(ParticleTypes.ITEM, broken.getItem()),
                    pos.x, pos.y + 1.0, pos.z, 8, 0.1, 0.1, 0.1, 0.05);
        });
    }

    /**
     * The equipment slot a player inventory slot belongs to, null for the main inventory. The selected hotbar slot is the main hand.
     */
    private static EquipmentSlot equipmentSlot(Inventory inventory, int slot) {
        if (slot == inventory.getSelectedSlot()) {
            return EquipmentSlot.MAINHAND;
        }
        return Inventory.EQUIPMENT_SLOT_MAPPING.get(slot);
    }

    /**
     * The slots durability entries choose from, in order: the selected hotbar slot (main hand), the offhand, the main inventory
     * in slot order (hotbar 0 to 8, then 9 to 35), then the armor slots.
     */
    private static List<Integer> durabilitySlots(Inventory inventory) {
        var slots = new ArrayList<Integer>();
        int selected = inventory.getSelectedSlot();
        slots.add(selected);
        slots.add(Inventory.SLOT_OFFHAND);
        for (int slot = 0; slot < Inventory.INVENTORY_SIZE; slot++) {
            if (slot != selected) {
                slots.add(slot);
            }
        }
        for (int slot = Inventory.INVENTORY_SIZE; slot < Inventory.INVENTORY_SIZE + 4; slot++) {
            slots.add(slot);
        }
        return slots;
    }

    private record Take(int slot, int count) {
    }

    private record Damage(int slot, int amount) {
    }

    private record Plan(List<Take> takes, List<Damage> damages, ResearchCost missing) {
    }

    private Plan plan(Player player) {
        if (player.hasInfiniteMaterials()) {
            return new Plan(List.of(), List.of(), EMPTY);
        }

        var inventory = player.getInventory();
        var remaining = new int[inventory.getContainerSize()];
        for (int slot = 0; slot < remaining.length; slot++) {
            remaining[slot] = inventory.getItem(slot).getCount();
        }

        var takes = new ArrayList<Take>();
        var damages = new ArrayList<Damage>();
        var missingItems = new ArrayList<ItemCost>();

        // durability first: a damageable stack that pays durability is reserved and cannot also be consumed as items
        for (var cost : this.items) {
            if (cost.durability().isEmpty()) {
                continue;
            }
            int chosen = -1;
            for (int slot : durabilitySlots(inventory)) {
                if (slot >= remaining.length || remaining[slot] <= 0) {
                    continue;
                }
                if (cost.matchesDurability(inventory.getItem(slot))) {
                    chosen = slot;
                    break;
                }
            }
            if (chosen < 0) {
                missingItems.add(cost);
            } else {
                remaining[chosen] = 0;
                damages.add(new Damage(chosen, cost.durability().get()));
            }
        }

        for (var cost : this.items) {
            if (cost.durability().isPresent()) {
                continue;
            }
            int needed = cost.count();
            for (int slot = 0; slot < remaining.length && needed > 0; slot++) {
                ItemStack stack = inventory.getItem(slot);
                if (remaining[slot] <= 0 || !cost.matches(stack)) {
                    continue;
                }
                int take = Math.min(needed, remaining[slot]);
                remaining[slot] -= take;
                needed -= take;
                takes.add(new Take(slot, take));
            }
            if (needed > 0) {
                missingItems.add(new ItemCost(cost.ingredient(), needed, cost.components(), Optional.empty()));
            }
        }

        int missingLevels = Math.max(0, this.levels - player.experienceLevel);
        return new Plan(takes, damages, new ResearchCost(missingItems, missingLevels));
    }
}
