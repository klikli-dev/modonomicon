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
            Codec.INT.fieldOf("openPagesIndex").forGetter((state) -> state.openPagesIndex),
            Codec.BOOL.optionalFieldOf("unread", false).forGetter((state) -> state.unread)
    ).apply(instance, EntryVisualState::new));

    public int openPagesIndex;
    public boolean unread;

    public EntryVisualState() {
        this(0, false);
    }

    public EntryVisualState(int openPagesIndex) {
        this(openPagesIndex, false);
    }

    public EntryVisualState(int openPagesIndex, boolean unread) {
        this.openPagesIndex = openPagesIndex;
        this.unread = unread;
    }

    public void setUnread(boolean unread) {
        this.unread = unread;
    }
}
