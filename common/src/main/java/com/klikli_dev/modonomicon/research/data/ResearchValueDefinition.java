/*
 * SPDX-FileCopyrightText: 2023 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.research.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.Identifier;

/**
 * Canonical definition for one research value.
 *
 * @param id unique identifier for this value
 */
public record ResearchValueDefinition(Identifier id) {

    public static final Codec<ResearchValueDefinition> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Identifier.CODEC.fieldOf("id").forGetter(ResearchValueDefinition::id)
    ).apply(instance, ResearchValueDefinition::new));
}
