/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.registry;

import com.klikli_dev.modonomicon.Modonomicon;
import com.klikli_dev.modonomicon.item.ModonomiconItem;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

import java.util.function.UnaryOperator;

public class DataComponentRegistry {
    public static final RegistrationProvider<DataComponentType<?>> DATA_COMPONENTS = RegistrationProvider.get(Registries.DATA_COMPONENT_TYPE, Modonomicon.MOD_ID);

    public static final RegistryObject<DataComponentType<ResourceLocation>> BOOK_ID = register("book_id", builder ->
            builder.persistent(ResourceLocation.CODEC)
                    .networkSynchronized(ResourceLocation.STREAM_CODEC).cacheEncoding()
    );

    // Called in the mod initializer / constructor in order to make sure that items are registered
    public static void load() {
    }

    private static <T> RegistryObject<DataComponentType<T>> register(String name, UnaryOperator<DataComponentType.Builder<T>> unaryOperator) {
        return DATA_COMPONENTS.register(name, () -> unaryOperator.apply(DataComponentType.builder()).build());
    }
}
