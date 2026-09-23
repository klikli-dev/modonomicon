// SPDX-FileCopyrightText: 2026 klikli-dev
//
// SPDX-License-Identifier: MIT

package com.klikli_dev.modonomicon.integration.recipeviewer;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.util.List;

/**
 * Base type for an ingredient condition, used to search for a recipe when no recipe id is given.
 * <p>
 * Subclasses represent a specific ingredient type, e.g. {@link ItemRVIngredient} for {@code item_stack}. The
 * {@code type} field in the serialized form selects the subclass.
 */
public abstract class RVIngredient {

    public static final Codec<RVIngredient> CODEC = Codec.STRING.dispatch(
            "type",
            RVIngredient::type,
            RVIngredient::codecForType
    );

    public static final StreamCodec<RegistryFriendlyByteBuf, RVIngredient> STREAM_CODEC =
            ByteBufCodecs.fromCodecWithRegistries(CODEC);

    private static MapCodec<? extends RVIngredient> codecForType(String type) {
        if (ItemRVIngredient.TYPE.equals(type)) {
            return ItemRVIngredient.MAP_CODEC;
        }
        throw new IllegalArgumentException("Unsupported ingredient type: " + type);
    }

    /**
     * @return the ingredient type id
     */
    public abstract String type();

    /**
     * @return a short human-readable description of this ingredient, used in the missing recipe message
     */
    public String describe() {
        return this.type();
    }

    /**
     * @return true if any of the given ingredient condition lists is non-empty
     */
    public static boolean hasAny(List<RVIngredient> input, List<RVIngredient> output, List<RVIngredient> workstation) {
        return !input.isEmpty() || !output.isEmpty() || !workstation.isEmpty();
    }
}
