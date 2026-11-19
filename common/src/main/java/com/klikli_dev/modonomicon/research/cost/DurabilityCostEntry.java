/*
 * SPDX-FileCopyrightText: 2026 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.research.cost;

import com.klikli_dev.modonomicon.api.ModonomiconConstants.I18n.Gui;
import com.klikli_dev.modonomicon.registry.ResearchCostTypeRegistry;
import com.klikli_dev.modonomicon.research.item.ResearchItemMatcher;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
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

import java.util.ArrayList;
import java.util.List;

/**
 * Takes durability from one damageable stack accepted by the matcher, e.g. shears. The stack is damaged, not consumed,
 * and may break. Any usable stack is enough, the first one in the order of {@link #durabilitySlots} is chosen.
 *
 * @param item the item or tag of the damageable stack
 * @param amount the durability points taken
 */
public record DurabilityCostEntry(ResearchItemMatcher item, int amount) implements ResearchCostEntry {

    public static final MapCodec<DurabilityCostEntry> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            ResearchItemMatcher.CODEC.fieldOf("item").forGetter(DurabilityCostEntry::item),
            Codec.intRange(1, Integer.MAX_VALUE).fieldOf("amount").forGetter(DurabilityCostEntry::amount)
    ).apply(instance, DurabilityCostEntry::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, DurabilityCostEntry> STREAM_CODEC = StreamCodec.composite(
            ResearchItemMatcher.STREAM_CODEC,
            DurabilityCostEntry::item,
            ByteBufCodecs.VAR_INT,
            DurabilityCostEntry::amount,
            DurabilityCostEntry::new
    );

    @Override
    public ResearchCostType<?> type() {
        return ResearchCostTypeRegistry.DURABILITY;
    }

    @Override
    public int planRank() {
        return 0;
    }

    /**
     * The number of usable stacks the player holds that the matcher accepts. Only stacks count, not items.
     */
    @Override
    public int heldCount(Player player) {
        var inventory = player.getInventory();
        int held = 0;
        for (int slot = 0; slot < inventory.getContainerSize(); slot++) {
            if (this.isUsable(inventory.getItem(slot))) {
                held++;
            }
        }
        return held;
    }

    @Override
    public boolean plan(ResearchCostPlan plan) {
        for (int slot : durabilitySlots(plan.player().getInventory())) {
            if (slot >= plan.size() || plan.free(slot) <= 0 || !this.isUsable(plan.stack(slot))) {
                continue;
            }
            plan.claim(this, slot, this.amount);
            return true;
        }
        return false;
    }

    @Override
    public void apply(ServerPlayer player, ResearchCostPlan plan) {
        for (var reservation : plan.reservationsOf(this)) {
            damage(player, reservation.slot(), reservation.amount());
        }
    }

    @Override
    public Component describe() {
        return Component.translatable(Gui.REQUIREMENTS_COST_DURABILITY, this.amount, this.item.displayName());
    }

    private boolean isUsable(ItemStack stack) {
        return this.item.matches(stack) && stack.isDamageableItem() && !stack.isBroken();
    }

    /**
     * Damages the stack in the slot by the amount, the way vanilla tools wear down: Unbreaking applies and the stack breaks
     * with its break effects when depleted. Stacks in equipment slots use the equipment overload (break event for the slot),
     * other inventory stacks play the break sound and particles themselves.
     */
    private static void damage(ServerPlayer player, int slot, int amount) {
        var inventory = player.getInventory();
        var stack = inventory.getItem(slot);
        var level = player.level();

        var equipmentSlot = equipmentSlot(inventory, slot);
        if (equipmentSlot != null) {
            stack.hurtAndBreak(amount, player, equipmentSlot);
            return;
        }

        var broken = stack.copy();
        stack.hurtAndBreak(amount, (ServerLevel) level, player, item -> {
            var pos = player.position();
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
}
