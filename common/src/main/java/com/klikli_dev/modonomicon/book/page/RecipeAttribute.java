/*
 * SPDX-FileCopyrightText: 2026 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.book.page;

import com.klikli_dev.modonomicon.integration.recipeviewer.RVIngredient;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.util.List;

/**
 * Per-recipe attributes for a {@link BookViewerRecipePage}.
 * <ul>
 *     <li>{@code scale} - the scale the recipe is rendered at. The renderer always clamps the scale so the recipe
 *     never exceeds the page's usable area, no matter the value given here.</li>
 *     <li>{@code background} - whether the recipe viewer should draw the recipe's own background (and border).
 *     Defaults to {@code true}. This does not affect the book page background.</li>
 *     <li>{@code input}/{@code output}/{@code workstation} - ingredient conditions used to search for a recipe
 *     when no (valid) recipe id is given. All filled fields must match for a recipe to be used.</li>
 * </ul>
 */
public record RecipeAttribute(
        float scale,
        boolean background,
        List<RVIngredient> input,
        List<RVIngredient> output,
        List<RVIngredient> workstation
) {

    public static final RecipeAttribute DEFAULT = new RecipeAttribute(1.0f, true, List.of(), List.of(), List.of());

    public static final Codec<RecipeAttribute> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.FLOAT.optionalFieldOf("scale", 1.0f).forGetter(RecipeAttribute::scale),
            Codec.BOOL.optionalFieldOf("background", true).forGetter(RecipeAttribute::background),
            RVIngredient.CODEC.listOf().optionalFieldOf("input", List.of()).forGetter(RecipeAttribute::input),
            RVIngredient.CODEC.listOf().optionalFieldOf("output", List.of()).forGetter(RecipeAttribute::output),
            RVIngredient.CODEC.listOf().optionalFieldOf("workstation", List.of()).forGetter(RecipeAttribute::workstation)
    ).apply(instance, RecipeAttribute::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, RecipeAttribute> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.FLOAT, RecipeAttribute::scale,
            ByteBufCodecs.BOOL, RecipeAttribute::background,
            RVIngredient.STREAM_CODEC.apply(ByteBufCodecs.list()), RecipeAttribute::input,
            RVIngredient.STREAM_CODEC.apply(ByteBufCodecs.list()), RecipeAttribute::output,
            RVIngredient.STREAM_CODEC.apply(ByteBufCodecs.list()), RecipeAttribute::workstation,
            RecipeAttribute::new
    );

    /**
     * @return true if any of the ingredient conditions is set
     */
    public boolean hasQuery() {
        return RVIngredient.hasAny(this.input, this.output, this.workstation);
    }

    public RecipeAttribute withScale(float scale) {
        return new RecipeAttribute(scale, this.background, this.input, this.output, this.workstation);
    }

    public RecipeAttribute withBackground(boolean background) {
        return new RecipeAttribute(this.scale, background, this.input, this.output, this.workstation);
    }

    public RecipeAttribute withInput(RVIngredient... input) {
        return this.withInput(List.of(input));
    }

    public RecipeAttribute withInput(List<RVIngredient> input) {
        return new RecipeAttribute(this.scale, this.background, List.copyOf(input), this.output, this.workstation);
    }

    public RecipeAttribute withOutput(RVIngredient... output) {
        return this.withOutput(List.of(output));
    }

    public RecipeAttribute withOutput(List<RVIngredient> output) {
        return new RecipeAttribute(this.scale, this.background, this.input, List.copyOf(output), this.workstation);
    }

    public RecipeAttribute withWorkstation(RVIngredient... workstation) {
        return this.withWorkstation(List.of(workstation));
    }

    public RecipeAttribute withWorkstation(List<RVIngredient> workstation) {
        return new RecipeAttribute(this.scale, this.background, this.input, this.output, List.copyOf(workstation));
    }
}
