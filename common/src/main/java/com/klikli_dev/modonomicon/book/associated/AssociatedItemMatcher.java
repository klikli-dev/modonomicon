/*
 * SPDX-FileCopyrightText: 2026 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.book.associated;

import net.minecraft.core.component.DataComponentType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import java.util.Objects;

/**
 * Matches an {@link ItemStackTemplate} from book json against a hovered {@link ItemStack}.
 * <p>
 * The item must be equal and every component defined in the template must be present
 * on the stack with an equal value. Additional components on the stack are allowed.
 * A template without components matches any stack of the item.
 */
public class AssociatedItemMatcher {

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

        for (var entry : patch.entrySet()) {
            DataComponentType<?> type = entry.getKey();
            var expected = entry.getValue();
            if (expected.isEmpty()) {
                // template requires the component to be absent
                if (stack.has(type)) {
                    return false;
                }
            } else {
                if (!matchesComponent(stack, type, expected.get())) {
                    return false;
                }
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
