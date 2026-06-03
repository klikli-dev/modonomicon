/*
 * SPDX-FileCopyrightText: 2023 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.research.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.Identifier;

import java.util.Optional;

/**
 * Canonical definition for one research value.
 *
 * @param id unique identifier for this value
 * @param toast optional toast display data
 */
public record ResearchValueDefinition(
        Identifier id,
        Optional<ResearchToastDefinition> toast
) {
    public static final Codec<ResearchValueDefinition> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Identifier.CODEC.fieldOf("id").forGetter(ResearchValueDefinition::id),
            ResearchToastDefinition.CODEC.optionalFieldOf("toast").forGetter(ResearchValueDefinition::toastOrEmpty)
    ).apply(instance, (id, toast) -> new ResearchValueDefinition(id, toast)));

    public Optional<ResearchToastDefinition> toastOrEmpty() {
        return this.toast;
    }
}
