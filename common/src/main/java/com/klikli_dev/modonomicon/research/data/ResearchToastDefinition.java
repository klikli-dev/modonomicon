/*
 * SPDX-FileCopyrightText: 2026 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.research.data;

import com.klikli_dev.modonomicon.book.BookIcon;
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
 * Optional toast display data for a research element.
 *
 * @param title translatable key for the toast title (second line)
 * @param titleArgs static component arguments for the title (values auto-append current count)
 * @param description translatable key for the toast category/description (first line), null uses default
 * @param icon optional icon to render; null means no icon
 */
public record ResearchToastDefinition(
        Identifier title,
        List<Component> titleArgs,
        Identifier description,
        BookIcon icon
) {
    public static final Codec<ResearchToastDefinition> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Identifier.CODEC.fieldOf("title").forGetter(ResearchToastDefinition::title),
            ComponentSerialization.CODEC.listOf().optionalFieldOf("title_args", List.of()).forGetter(ResearchToastDefinition::titleArgs),
            Identifier.CODEC.optionalFieldOf("description").forGetter(ResearchToastDefinition::descriptionOrEmpty),
            BookIcon.CODEC.optionalFieldOf("icon").forGetter(ResearchToastDefinition::iconOrEmpty)
    ).apply(instance, (title, titleArgs, description, icon) -> new ResearchToastDefinition(title, titleArgs, description.orElse(null), icon.orElse(null))));

    public static final StreamCodec<RegistryFriendlyByteBuf, ResearchToastDefinition> STREAM_CODEC =
            ByteBufCodecs.fromCodecWithRegistries(CODEC);

    public Optional<Identifier> descriptionOrEmpty() {
        return Optional.ofNullable(this.description);
    }

    public Optional<BookIcon> iconOrEmpty() {
        return Optional.ofNullable(this.icon);
    }
}
