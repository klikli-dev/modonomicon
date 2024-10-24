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
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

public interface ModonomiconLanguageProvider extends BiConsumer<String, String> {

    default void add(String key, String name){
        this.accept(key, name);
    }

    default void addBlock(Supplier<? extends Block> key, String name) {
        this.add(key.get(), name);
    }

    default void add(Block key, String name) {
        this.add(key.getDescriptionId(), name);
    }

    default void addItem(Supplier<? extends Item> key, String name) {
        this.add(key.get(), name);
    }

    default void add(Item key, String name) {
        this.add(key.getDescriptionId(), name);
    }

    default void addItemStack(Supplier<ItemStack> key, String name) {
        this.add(key.get(), name);
    }

    default void add(ItemStack key, String name) {
        this.add(key.getItem().getDescriptionId(), name);
    }

    default void addEffect(Supplier<? extends MobEffect> key, String name) {
        this.add(key.get(), name);
    }

    default void add(MobEffect key, String name) {
        this.add(key.getDescriptionId(), name);
    }

    default void addEntityType(Supplier<? extends EntityType<?>> key, String name) {
        this.add(key.get(), name);
    }

    default void add(EntityType<?> key, String name) {
        this.add(key.getDescriptionId(), name);
    }

    /**
     * Return a map containing all translation keys and their values.
     * Alternatively override the second overload to provide the data one-by-one to a bi-consumer.
     */
    default @NotNull Map<String, String> data(){
       return Map.of();
    }

    default void data(BiConsumer<String, String> consumer){
        this.data().forEach(consumer);
    }
}
