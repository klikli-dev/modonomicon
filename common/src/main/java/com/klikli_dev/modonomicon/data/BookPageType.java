/*
 * SPDX-FileCopyrightText: 2026 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.data;

import com.klikli_dev.modonomicon.book.page.BookPage;
import com.mojang.serialization.MapCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;

public record BookPageType<T extends BookPage>(
        Identifier id,
        MapCodec<T> codec,
        StreamCodec<RegistryFriendlyByteBuf, T> streamCodec
) {
}
