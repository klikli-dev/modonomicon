/*
 * SPDX-FileCopyrightText: 2026 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.book.entries;

import com.klikli_dev.modonomicon.book.ScholarshipDisplay;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;

import java.util.Optional;

/**
 * Links an entry to the research node that tracks its research.
 * The requirements of the node's next stage are shown in the entry, the entry is complete once the node is complete.
 *
 * @param node                the research node that tracks the research of this entry
 * @param scholarshipDisplay optional override of the book's default requirements display
 */
public record BookEntryResearch(
        Identifier node,
        Optional<ScholarshipDisplay> scholarshipDisplay
) {
    public static final Codec<BookEntryResearch> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Identifier.CODEC.fieldOf("node").forGetter(BookEntryResearch::node),
            ScholarshipDisplay.CODEC.optionalFieldOf("scholarship_display").forGetter(BookEntryResearch::scholarshipDisplay)
    ).apply(instance, BookEntryResearch::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, BookEntryResearch> STREAM_CODEC = StreamCodec.composite(
            Identifier.STREAM_CODEC, BookEntryResearch::node,
            ByteBufCodecs.optional(ScholarshipDisplay.STREAM_CODEC), BookEntryResearch::scholarshipDisplay,
            BookEntryResearch::new
    );
}
