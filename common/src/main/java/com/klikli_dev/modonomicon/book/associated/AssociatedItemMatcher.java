/*
 * SPDX-FileCopyrightText: 2026 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.book.associated;

import com.mojang.datafixers.util.Either;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.Ingredient;
import java.util.Objects;

/**
 * Matches an associated item from book json against a hovered {@link ItemStack}.
 * <p>
 * The associated item is either an {@link ItemStackTemplate} or an {@link Ingredient}
 * (like spotlight pages support). For templates the item must be equal and every
 * component defined in the template must be present on the stack with an equal value.
 * Additional components on the stack are allowed. A template without components
 * matches any stack of the item. For ingredients {@link Ingredient#test(ItemStack)}
 * decides.
 */
public class AssociatedItemMatcher {

    public static boolean matches(Either<ItemStackTemplate, Ingredient> associated, ItemStack stack) {
        return associated.map(
                template -> matches(template, stack),
                ingredient -> matches(ingredient, stack)
        );
    }

    public static boolean matches(Ingredient ingredient, ItemStack stack) {
        if (stack.isEmpty()) {
            return false;
        }
        return ingredient.test(stack);
    }

    public static boolean matches(ItemStackTemplate template, ItemStack stack) {
        if (stack.isEmpty()) {
            return false;
        }

        if (stack.getItem() != template.item().value()) {
            return false;
        }

        var patch = template.components();
        if (patch.isEmpty()) {
            return true;
        }

        //NB: 26.3 DataComponentPatch no longer exposes entrySet() with Optional values;
        //split() separates required-absent (removed) from required-present (added) components
        var split = patch.split();
        for (var type : split.removed()) {
            // template requires the component to be absent
            if (stack.has(type)) {
                return false;
            }
        }
        for (var typed : split.added()) {
            if (!matchesComponent(stack, typed.type(), typed.value())) {
                return false;
            }
        }

        return true;
    }

    private static <T> boolean matchesComponent(ItemStack stack, DataComponentType<?> type, Object expected) {
        @SuppressWarnings("unchecked")
        var typed = (DataComponentType<T>) type;
        T actual = stack.get(typed);
        return Objects.equals(actual, expected);
    }

    /**
     * Compares two stacks for hold-tracking purposes: same item and same component patch.
     */
    public static boolean isSameStack(ItemStack a, ItemStack b) {
        if (a.isEmpty() || b.isEmpty()) {
            return a.isEmpty() && b.isEmpty();
        }
        if (a.getItem() != b.getItem()) {
            return false;
        }
        return Objects.equals(a.getComponentsPatch(), b.getComponentsPatch());
    }

    /**
     * Stable key fragment for hold-tracking without keeping full stacks alive.
     */
    public static String describe(ItemStack stack) {
        if (stack.isEmpty()) {
            return "empty";
        }
        return stack.getItem().toString() + stack.getComponentsPatch().toString();
    }
}
