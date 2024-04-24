// SPDX-FileCopyrightText: 2024 klikli-dev
//
// SPDX-License-Identifier: MIT

package com.klikli_dev.modonomicon.api.datagen;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.block.Block;

import java.util.Map;
import java.util.function.Supplier;

public interface ModonomiconLanguageProvider {

    String locale();

    void add(String key, String name);

    void addBlock(Supplier<? extends Block> key, String name);

    void add(Block key, String name);

    void addItem(Supplier<? extends Item> key, String name);

    void add(Item key, String name);

    void addItemStack(Supplier<ItemStack> key, String name);

    void add(ItemStack key, String name);

    void addEnchantment(Supplier<? extends Enchantment> key, String name);

    void add(Enchantment key, String name);

    void addEffect(Supplier<? extends MobEffect> key, String name);

    void add(MobEffect key, String name);

    void addEntityType(Supplier<? extends EntityType<?>> key, String name);

    void add(EntityType<?> key, String name);

    Map<String, String> data();
}
