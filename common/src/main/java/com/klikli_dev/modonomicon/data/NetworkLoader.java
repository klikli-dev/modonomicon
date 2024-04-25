/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.data;

import net.minecraft.network.RegistryFriendlyByteBuf;

public interface NetworkLoader<T> {
    T fromNetwork(RegistryFriendlyByteBuf buff);
}
