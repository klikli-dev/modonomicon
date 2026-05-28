/*
 * SPDX-FileCopyrightText: 2023 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.research.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.Identifier;

import java.util.List;

public record ResearchNodeDefinition(Identifier id, List<Identifier> requiredFacts) {

    public static final Codec<ResearchNodeDefinition> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Identifier.CODEC.fieldOf("id").forGetter(ResearchNodeDefinition::id),
            Identifier.CODEC.listOf().fieldOf("required_facts").forGetter(ResearchNodeDefinition::requiredFacts)
    ).apply(instance, ResearchNodeDefinition::new));
}
