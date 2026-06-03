/*
 * SPDX-FileCopyrightText: 2023 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.research.data;

import com.klikli_dev.modonomicon.data.TriggerType;
import com.klikli_dev.modonomicon.registry.TriggerTypeRegistry;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStackTemplate;

import javax.annotation.Nullable;
import java.util.Optional;

/**
 * Defines an authored hook that maps one explicit runtime event target to a research mutation.
 * A hook either grants a fact OR increments a value, never both.
 *
 * @param id unique id of this hook definition resource
 * @param triggerType the supported event family that can fire this hook, such as {@code entry_viewed_once}
 * @param triggerTargetId the specific target observed by the trigger; for {@code entry_viewed_once} this is the entry id being viewed
 * @param factId the research fact granted when the configured trigger fires (null if this hook increments a value)
 * @param valueId the research value incremented when the configured trigger fires (null if this hook grants a fact)
 * @param increment the amount to add to the target value when this hook fires (default 1, ignored for fact hooks)
 * @param targetItem optional item stack template with components for partial matching (null means match by item ID only)
 * @param matchComponents whether to check components when targetItem is set (default false)
 */
public record ResearchHookDefinition(
        Identifier id,
        TriggerType triggerType,
        Identifier triggerTargetId,
        Identifier factId,
        Identifier valueId,
        int increment,
        @Nullable ItemStackTemplate targetItem,
        boolean matchComponents
) {

    public static final Codec<ResearchHookDefinition> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Identifier.CODEC.fieldOf("id").forGetter(ResearchHookDefinition::id),
            TriggerTypeRegistry.codec().fieldOf("trigger_type").forGetter(ResearchHookDefinition::triggerType),
            Identifier.CODEC.fieldOf("event_target_id").forGetter(ResearchHookDefinition::triggerTargetId),
            Identifier.CODEC.optionalFieldOf("fact_id").forGetter(h -> Optional.ofNullable(h.factId)),
            Identifier.CODEC.optionalFieldOf("value_id").forGetter(h -> Optional.ofNullable(h.valueId)),
            Codec.intRange(1, Integer.MAX_VALUE).optionalFieldOf("increment", 1).forGetter(ResearchHookDefinition::increment),
            ItemStackTemplate.CODEC.optionalFieldOf("event_target_item").forGetter(h -> Optional.ofNullable(h.targetItem)),
            Codec.BOOL.optionalFieldOf("match_components", false).forGetter(ResearchHookDefinition::matchComponents)
    ).apply(instance, (id, triggerType, triggerTargetId, factIdOpt, valueIdOpt, increment, targetItemOpt, matchComponents) ->
            new ResearchHookDefinition(
                    id, triggerType, triggerTargetId,
                    factIdOpt.orElse(null),
                    valueIdOpt.orElse(null),
                    increment,
                    targetItemOpt.orElse(null),
                    matchComponents
            )
    ));
}
