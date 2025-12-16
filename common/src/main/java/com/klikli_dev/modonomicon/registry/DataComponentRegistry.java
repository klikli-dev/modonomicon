/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.registry;

import com.klikli_dev.modonomicon.Modonomicon;
import com.mojang.serialization.Codec;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.resources.Identifier;

import java.util.function.UnaryOperator;

public class DataComponentRegistry {
    public static final RegistrationProvider<DataComponentType<?>> DATA_COMPONENTS = RegistrationProvider.get(Registries.DATA_COMPONENT_TYPE, Modonomicon.MOD_ID);

    public static final RegistryObject<DataComponentType<Identifier>> BOOK_ID = register("book_id", builder ->
            builder.persistent(Identifier.CODEC)
                    .networkSynchronized(Identifier.STREAM_CODEC).cacheEncoding()
    );

    // Data component for storing whether the book item is currently open (true) or closed (false)
    public static final RegistryObject<DataComponentType<Boolean>> BOOK_OPEN = register("book_open", builder ->
            builder.persistent(Codec.BOOL)
                    .networkSynchronized(ByteBufCodecs.BOOL).cacheEncoding()
    );

    // Called in the mod initializer / constructor in order to make sure that items are registered
    public static void load() {
    }

    private static <T> RegistryObject<DataComponentType<T>> register(String name, UnaryOperator<DataComponentType.Builder<T>> unaryOperator) {
        return DATA_COMPONENTS.register(name, () -> unaryOperator.apply(DataComponentType.builder()).build());
    }
}
