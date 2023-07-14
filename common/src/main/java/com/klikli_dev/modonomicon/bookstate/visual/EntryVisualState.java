/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.bookstate.visual;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public class EntryVisualState {

    public static final Codec<EntryVisualState> CODEC = RecordCodecBuilder.create((instance) -> instance.group(
            Codec.INT.fieldOf("openPagesIndex").forGetter((state) -> state.openPagesIndex)
    ).apply(instance, EntryVisualState::new));

    public int openPagesIndex;

    public EntryVisualState() {
        this(0);
    }

    public EntryVisualState(int openPagesIndex) {
        this.openPagesIndex = openPagesIndex;
    }
}
