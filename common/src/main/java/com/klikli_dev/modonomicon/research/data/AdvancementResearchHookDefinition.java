/*
 * SPDX-FileCopyrightText: 2026 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.research.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.Identifier;

import java.util.Optional;

/**
 * Defines an authored advancement hook that grants a research fact or increments a value
 * when the player earns the specified advancement.
 * A hook either grants a fact OR increments a value, never both.
 *
 * @param id unique id of this hook definition resource
 * @param advancementId the advancement whose completion triggers this hook
 * @param factId the research fact granted when the advancement is earned (null if this hook increments a value)
 * @param valueId the research value incremented when the advancement is earned (null if this hook grants a fact)
 * @param increment the amount to add to the target value when this hook fires (default 1, ignored for fact hooks)
 */
public record AdvancementResearchHookDefinition(
        Identifier id,
        Identifier advancementId,
        Identifier factId,
        Identifier valueId,
        int increment
) {

    public static final Codec<AdvancementResearchHookDefinition> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Identifier.CODEC.fieldOf("id").forGetter(AdvancementResearchHookDefinition::id),
            Identifier.CODEC.fieldOf("advancement_id").forGetter(AdvancementResearchHookDefinition::advancementId),
            Identifier.CODEC.optionalFieldOf("fact_id").forGetter(h -> Optional.ofNullable(h.factId)),
            Identifier.CODEC.optionalFieldOf("value_id").forGetter(h -> Optional.ofNullable(h.valueId)),
            Codec.intRange(1, Integer.MAX_VALUE).optionalFieldOf("increment", 1).forGetter(AdvancementResearchHookDefinition::increment)
    ).apply(instance, (id, advancementId, factIdOpt, valueIdOpt, increment) ->
            new AdvancementResearchHookDefinition(
                    id, advancementId,
                    factIdOpt.orElse(null),
                    valueIdOpt.orElse(null),
                    increment
            )
    ));
}
