/*
 * SPDX-FileCopyrightText: 2023 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.data;

import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class ReloadListenerWrapper implements IdentifiableResourceReloadListener {

    private final ResourceLocation id;
    private final LegacySimpleJsonResourceReloadListener listener;

    public ReloadListenerWrapper(ResourceLocation id, LegacySimpleJsonResourceReloadListener listener) {
        this.id = id;
        this.listener = listener;
    }

    @Override
    public ResourceLocation getFabricId() {
        return this.id;
    }

    @Override
    public CompletableFuture<Void> reload(PreparationBarrier preparationBarrier, ResourceManager resourceManager, Executor backgroundExecutor, Executor gameExecutor) {
        return this.listener.reload(preparationBarrier, resourceManager, backgroundExecutor, gameExecutor);
    }
}
