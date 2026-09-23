// SPDX-FileCopyrightText: 2026 klikli-dev
//
// SPDX-License-Identifier: MIT

package com.klikli_dev.modonomicon.integration.recipeviewer;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.level.ItemLike;

import java.util.Objects;

/**
 * An {@link RVIngredient} of type {@link #TYPE}, holding an {@link ItemStackTemplate}.
 * <p>
 * The value is an {@link ItemStackTemplate} rather than an {@link ItemStack}, so it can be created in datagen
 * before the item components are bound (same approach as the spotlight page).
 */
public final class ItemRVIngredient extends RVIngredient {

    public static final String TYPE = "item_stack";

    /**
     * Codec for the ingredient value only. The {@code type} field is added by {@link RVIngredient#CODEC}.
     */
    public static final MapCodec<ItemRVIngredient> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            ItemStackTemplate.CODEC.fieldOf("value").forGetter(ItemRVIngredient::value)
    ).apply(instance, ItemRVIngredient::new));
    public static final Codec<ItemRVIngredient> CODEC = MAP_CODEC.codec();

    private final ItemStackTemplate value;

    public ItemRVIngredient(ItemStackTemplate value) {
        this.value = value;
    }

    @Override
    public String type() {
        return TYPE;
    }

    @Override
    public String describe() {
        return BuiltInRegistries.ITEM.getKey(this.value.item().value()).toString();
    }

    public ItemStackTemplate value() {
        return this.value;
    }

    /**
     * Creates the actual item stack. Must only be called once the item components are bound (i.e. not in datagen).
     */
    public ItemStack toItemStack() {
        return this.value.create();
    }

    public static ItemRVIngredient of(ItemStackTemplate value) {
        return new ItemRVIngredient(value);
    }

    public static ItemRVIngredient of(ItemLike value) {
        return new ItemRVIngredient(new ItemStackTemplate(value.asItem()));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ItemRVIngredient other)) {
            return false;
        }
        return this.value.equals(other.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.value);
    }

    @Override
    public String toString() {
        return "ItemRVIngredient[" + this.value + "]";
    }
}
