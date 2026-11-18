/*
 * SPDX-FileCopyrightText: 2026 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.book.entries;

import com.klikli_dev.modonomicon.book.RequirementsDisplay;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;

import java.util.Optional;

/**
 * Links an entry to the research node that tracks its progression.
 * The requirements of the node's next stage are shown in the entry, the entry is complete once the node is complete.
 *
 * @param node                the research node that tracks the progression of this entry
 * @param requirementsDisplay optional override of the book's default requirements display
 */
public record BookEntryProgression(
        Identifier node,
        Optional<RequirementsDisplay> requirementsDisplay
) {
    public static final Codec<BookEntryProgression> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Identifier.CODEC.fieldOf("node").forGetter(BookEntryProgression::node),
            RequirementsDisplay.CODEC.optionalFieldOf("requirements_display").forGetter(BookEntryProgression::requirementsDisplay)
    ).apply(instance, BookEntryProgression::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, BookEntryProgression> STREAM_CODEC = StreamCodec.composite(
            Identifier.STREAM_CODEC, BookEntryProgression::node,
            ByteBufCodecs.optional(RequirementsDisplay.STREAM_CODEC), BookEntryProgression::requirementsDisplay,
            BookEntryProgression::new
    );
}
