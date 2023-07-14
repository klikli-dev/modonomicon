/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.networking;

import net.minecraft.network.FriendlyByteBuf;

public interface Message {
    void encode(FriendlyByteBuf buf);

    void decode(FriendlyByteBuf buf);
}
