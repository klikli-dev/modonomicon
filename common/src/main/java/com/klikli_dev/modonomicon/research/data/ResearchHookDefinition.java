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
 * Defines an authored hook that maps one explicit runtime event target to a research fact grant.
 *
 * @param id unique id of this hook definition resource
 * @param triggerType the supported event family that can fire this hook, such as {@code entry_viewed_once}
 * @param triggerTargetId the specific target observed by the trigger; for {@code entry_viewed_once} this is the entry id being viewed
 * @param factId the research fact granted when the configured trigger fires for the configured target
 */
public record ResearchHookDefinition(Identifier id, TriggerType triggerType, Identifier triggerTargetId, Identifier factId) {

    public static final Codec<ResearchHookDefinition> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Identifier.CODEC.fieldOf("id").forGetter(ResearchHookDefinition::id),
            TriggerType.CODEC.fieldOf("trigger_type").forGetter(ResearchHookDefinition::triggerType),
            Identifier.CODEC.fieldOf("event_target_id").forGetter(ResearchHookDefinition::triggerTargetId),
            Identifier.CODEC.fieldOf("fact_id").forGetter(ResearchHookDefinition::factId)
    ).apply(instance, ResearchHookDefinition::new));

    public enum TriggerType {
        ENTRY_VIEWED_ONCE("entry_viewed_once");

        public static final Codec<TriggerType> CODEC = Codec.STRING.xmap(TriggerType::fromSerializedName, TriggerType::serializedName);

        private final String serializedName;

        TriggerType(String serializedName) {
            this.serializedName = serializedName;
        }

        public String serializedName() {
            return this.serializedName;
        }

        public static TriggerType fromSerializedName(String name) {
            if (ENTRY_VIEWED_ONCE.serializedName.equals(name)) {
                return ENTRY_VIEWED_ONCE;
            }
            throw new IllegalArgumentException("Unsupported research trigger type: " + name);
        }
    }
}
