/*
 * SPDX-FileCopyrightText: 2026 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.research.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.Identifier;

public record AdvancementResearchHookDefinition(Identifier id, Identifier advancementId, Identifier factId) {

    public static final Codec<AdvancementResearchHookDefinition> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Identifier.CODEC.fieldOf("id").forGetter(AdvancementResearchHookDefinition::id),
            Identifier.CODEC.fieldOf("advancement_id").forGetter(AdvancementResearchHookDefinition::advancementId),
            Identifier.CODEC.fieldOf("fact_id").forGetter(AdvancementResearchHookDefinition::factId)
    ).apply(instance, AdvancementResearchHookDefinition::new));
}
