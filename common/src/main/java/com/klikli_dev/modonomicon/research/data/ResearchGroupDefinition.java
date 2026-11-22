/*
 * SPDX-FileCopyrightText: 2026 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.research.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;

import java.util.List;
import java.util.Optional;

/**
 * Groups requirement rows in requirement blocks. Each group is one row, rows are ordered by sort order, then by id.
 *
 * @param id           unique identifier of the group
 * @param icon         texture identifier of the GUI sprite, drawn faint at the start of the row
 * @param title        title of the group, shown in the tooltip of the icon
 * @param description  optional description, shown in the tooltip of the icon
 * @param sortOrder    position of the row, ascending
 * @param triggerTypes trigger type ids whose hooks are grouped into this group by default
 */
public record ResearchGroupDefinition(
        Identifier id,
        Identifier icon,
        Component title,
        Optional<Component> description,
        int sortOrder,
        List<Identifier> triggerTypes
) {
    public static final Codec<ResearchGroupDefinition> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Identifier.CODEC.fieldOf("id").forGetter(ResearchGroupDefinition::id),
            Identifier.CODEC.fieldOf("icon").forGetter(ResearchGroupDefinition::icon),
            ComponentSerialization.CODEC.fieldOf("title").forGetter(ResearchGroupDefinition::title),
            ComponentSerialization.CODEC.optionalFieldOf("description").forGetter(ResearchGroupDefinition::description),
            Codec.INT.optionalFieldOf("sort_order", 0).forGetter(ResearchGroupDefinition::sortOrder),
            Identifier.CODEC.listOf().optionalFieldOf("trigger_types", List.of()).forGetter(ResearchGroupDefinition::triggerTypes)
    ).apply(instance, ResearchGroupDefinition::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, ResearchGroupDefinition> STREAM_CODEC =
            StreamCodec.composite(
                    Identifier.STREAM_CODEC,
                    ResearchGroupDefinition::id,
                    Identifier.STREAM_CODEC,
                    ResearchGroupDefinition::icon,
                    ComponentSerialization.TRUSTED_STREAM_CODEC,
                    ResearchGroupDefinition::title,
                    ByteBufCodecs.optional(ComponentSerialization.TRUSTED_STREAM_CODEC),
                    ResearchGroupDefinition::description,
                    ByteBufCodecs.VAR_INT,
                    ResearchGroupDefinition::sortOrder,
                    Identifier.STREAM_CODEC.apply(ByteBufCodecs.list()),
                    ResearchGroupDefinition::triggerTypes,
                    ResearchGroupDefinition::new
            );
}
