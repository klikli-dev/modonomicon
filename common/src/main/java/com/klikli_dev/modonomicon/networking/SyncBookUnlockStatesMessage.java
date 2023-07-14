/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.networking;

import com.klikli_dev.modonomicon.bookstate.BookUnlockStates;
import net.minecraft.network.FriendlyByteBuf;

public class SyncBookUnlockStatesMessage implements Message {

    public BookUnlockStates states;

    public SyncBookUnlockStatesMessage(BookUnlockStates states) {
        this.states = states;
    }

    public SyncBookUnlockStatesMessage(FriendlyByteBuf buf) {
        this.decode(buf);
    }

    @Override
    public void encode(FriendlyByteBuf buf) {
        buf.writeJsonWithCodec(BookUnlockStates.CODEC, this.states);
    }

    @Override
    public void decode(FriendlyByteBuf buf) {
        buf.readJsonWithCodec(BookUnlockStates.CODEC);
    }
}
