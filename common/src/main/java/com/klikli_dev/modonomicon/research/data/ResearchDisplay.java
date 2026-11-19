/*
 * SPDX-FileCopyrightText: 2026 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.research.data;

import com.klikli_dev.modonomicon.book.BookIcon;
import com.klikli_dev.modonomicon.research.action.ResearchAction;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

/**
 * Optional display data of a research element, used to present it in requirement blocks.
 * Every part is optional, missing parts fall back to translation keys and then to hook data.
 *
 * @param icon optional icon representing the element
 * @param title optional title, shown as the first line of a requirement
 * @param description optional description, shown below the title
 * @param category optional requirement category the element is grouped into
 * @param action optional action a player can perform to satisfy a fact, only used on facts
 */
public record ResearchDisplay(
        Optional<BookIcon> icon,
        Optional<Component> title,
        Optional<Component> description,
        Optional<Identifier> category,
        Optional<ResearchAction> action
) {
    public static final ResearchDisplay EMPTY = new ResearchDisplay(Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty());

    /**
     * Creates display data from an optional icon and optional text components. Null leaves the part unset.
     */
    public static ResearchDisplay of(@Nullable BookIcon icon, @Nullable Component title, @Nullable Component description) {
        return new ResearchDisplay(Optional.ofNullable(icon), Optional.ofNullable(title), Optional.ofNullable(description), Optional.empty(), Optional.empty());
    }

    /**
     * Returns a copy of this display that is grouped into the given category.
     */
    public ResearchDisplay withCategory(@Nullable Identifier category) {
        return new ResearchDisplay(this.icon, this.title, this.description, Optional.ofNullable(category), this.action);
    }

    /**
     * Returns a copy of this display that offers the given action on a fact.
     */
    public ResearchDisplay withAction(@Nullable ResearchAction action) {
        return new ResearchDisplay(this.icon, this.title, this.description, this.category, Optional.ofNullable(action));
    }

    public static final Codec<ResearchDisplay> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            BookIcon.CODEC.optionalFieldOf("icon").forGetter(ResearchDisplay::icon),
            ComponentSerialization.CODEC.optionalFieldOf("title").forGetter(ResearchDisplay::title),
            ComponentSerialization.CODEC.optionalFieldOf("description").forGetter(ResearchDisplay::description),
            Identifier.CODEC.optionalFieldOf("category").forGetter(ResearchDisplay::category),
            ResearchAction.CODEC.optionalFieldOf("action").forGetter(ResearchDisplay::action)
    ).apply(instance, ResearchDisplay::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, ResearchDisplay> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.optional(BookIcon.STREAM_CODEC),
                    ResearchDisplay::icon,
                    ByteBufCodecs.optional(ComponentSerialization.TRUSTED_STREAM_CODEC),
                    ResearchDisplay::title,
                    ByteBufCodecs.optional(ComponentSerialization.TRUSTED_STREAM_CODEC),
                    ResearchDisplay::description,
                    ByteBufCodecs.optional(Identifier.STREAM_CODEC),
                    ResearchDisplay::category,
                    ByteBufCodecs.optional(ResearchAction.STREAM_CODEC),
                    ResearchDisplay::action,
                    ResearchDisplay::new
            );
}
