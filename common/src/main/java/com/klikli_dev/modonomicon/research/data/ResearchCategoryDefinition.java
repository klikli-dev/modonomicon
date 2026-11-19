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
 * Groups requirement rows in requirement blocks. Each category is one row, rows are ordered by sort order, then by id.
 *
 * @param id           unique identifier of the category
 * @param icon         texture identifier of the GUI sprite, drawn faint at the start of the row
 * @param title        title of the category, shown in the tooltip of the icon
 * @param description  optional description, shown in the tooltip of the icon
 * @param sortOrder    position of the row, ascending
 * @param triggerTypes trigger type ids whose hooks are grouped into this category by default
 */
public record ResearchCategoryDefinition(
        Identifier id,
        Identifier icon,
        Component title,
        Optional<Component> description,
        int sortOrder,
        List<Identifier> triggerTypes
) {
    public static final Codec<ResearchCategoryDefinition> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Identifier.CODEC.fieldOf("id").forGetter(ResearchCategoryDefinition::id),
            Identifier.CODEC.fieldOf("icon").forGetter(ResearchCategoryDefinition::icon),
            ComponentSerialization.CODEC.fieldOf("title").forGetter(ResearchCategoryDefinition::title),
            ComponentSerialization.CODEC.optionalFieldOf("description").forGetter(ResearchCategoryDefinition::description),
            Codec.INT.optionalFieldOf("sort_order", 0).forGetter(ResearchCategoryDefinition::sortOrder),
            Identifier.CODEC.listOf().optionalFieldOf("trigger_types", List.of()).forGetter(ResearchCategoryDefinition::triggerTypes)
    ).apply(instance, ResearchCategoryDefinition::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, ResearchCategoryDefinition> STREAM_CODEC =
            StreamCodec.composite(
                    Identifier.STREAM_CODEC,
                    ResearchCategoryDefinition::id,
                    Identifier.STREAM_CODEC,
                    ResearchCategoryDefinition::icon,
                    ComponentSerialization.TRUSTED_STREAM_CODEC,
                    ResearchCategoryDefinition::title,
                    ByteBufCodecs.optional(ComponentSerialization.TRUSTED_STREAM_CODEC),
                    ResearchCategoryDefinition::description,
                    ByteBufCodecs.VAR_INT,
                    ResearchCategoryDefinition::sortOrder,
                    Identifier.STREAM_CODEC.apply(ByteBufCodecs.list()),
                    ResearchCategoryDefinition::triggerTypes,
                    ResearchCategoryDefinition::new
            );
}
