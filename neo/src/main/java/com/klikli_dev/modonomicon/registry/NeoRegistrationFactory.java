/*
 * SPDX-FileCopyrightText: 2023 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.registry;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.fml.ModList;
import net.neoforged.fml.javafmlmod.FMLModContainer;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;

public class NeoRegistrationFactory implements RegistrationProvider.Factory {

    @Override
    public <T> RegistrationProvider<T> create(ResourceKey<? extends Registry<T>> resourceKey, String modId) {
        final var containerOpt = ModList.get().getModContainerById(modId);
        if (containerOpt.isEmpty())
            throw new NullPointerException("Cannot find mod container for id " + modId);
        final var cont = containerOpt.get();
        if (cont instanceof FMLModContainer fmlModContainer) {
            final var register = DeferredRegister.create(resourceKey, modId);
            register.register(fmlModContainer.getEventBus());
            return new Provider<>(modId, register);
        } else {
            throw new ClassCastException("The container of the mod " + modId + " is not a FML one!");
        }
    }

    private static class Provider<T> implements RegistrationProvider<T> {
        private final String modId;
        private final DeferredRegister<T> registry;

        private final Set<RegistryObject<T>> entries = new HashSet<>();
        private final Set<RegistryObject<T>> entriesView = Collections.unmodifiableSet(this.entries);

        private Provider(String modId, DeferredRegister<T> registry) {
            this.modId = modId;
            this.registry = registry;
        }

        @Override
        public String getModId() {
            return this.modId;
        }

        @Override
        @SuppressWarnings("unchecked")
        public <I extends T> RegistryObject<I> register(String name, Supplier<? extends I> supplier) {
            final var obj = this.registry.<I>register(name, supplier);
            final var ro = new RegistryObject<I>() {

                @Override
                public ResourceKey<I> getResourceKey() {
                    return (ResourceKey<I>) obj.getKey();
                }

                @Override
                public ResourceLocation getId() {
                    return obj.getId();
                }

                @Override
                public I get() {
                    return obj.get();
                }

                @Override
                public Holder<I> asHolder() {
                    return (Holder<I>)registry.getRegistry().get().getHolder(obj.getKey()).get();
                }
            };
            this.entries.add((RegistryObject<T>) ro);
            return ro;
        }

        @Override
        public Set<RegistryObject<T>> getEntries() {
            return this.entriesView;
        }
    }
}