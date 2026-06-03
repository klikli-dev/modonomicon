/*
 * SPDX-FileCopyrightText: 2026 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.api.datagen.research;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.klikli_dev.modonomicon.api.ModonomiconConstants;
import com.klikli_dev.modonomicon.api.datagen.LanguageProviderCache;
import com.klikli_dev.modonomicon.research.data.AdvancementResearchHookDefinition;
import com.klikli_dev.modonomicon.research.data.ResearchFactDefinition;
import com.klikli_dev.modonomicon.research.data.ResearchHookDefinition;
import com.klikli_dev.modonomicon.research.data.ResearchNodeDefinition;
import com.klikli_dev.modonomicon.research.data.ResearchValueDefinition;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * Top-level research datagen orchestrator.
 *
 * This class is the research-side equivalent of the book provider. It collects authored research
 * bundles from {@link ResearchSubProvider}s, rejects duplicate bundle ids, and writes the canonical
 * explicit research resource files for each bundle.
 */
public class ResearchProvider implements DataProvider {
    private final String modId;
    private final PackOutput packOutput;
    private final CompletableFuture<HolderLookup.Provider> registries;
    private final List<ResearchSubProvider> subProviders;
    private final Map<Identifier, ResearchBundle> bundles = new Object2ObjectOpenHashMap<>();
    private final ResearchCache researchCache;
    private final LanguageProviderCache langCache;

    /**
     * Creates a research provider for the given mod id and research subproviders.
     */
    public ResearchProvider(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> registries, String modId, List<ResearchSubProvider> subProviders) {
        this(packOutput, registries, modId, subProviders, null, null);
    }

    /**
     * Creates a research provider wired to the given research cache.
     */
    public ResearchProvider(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> registries, String modId, List<ResearchSubProvider> subProviders, ResearchCache researchCache) {
        this(packOutput, registries, modId, subProviders, researchCache, null);
    }

    /**
     * Creates a research provider wired to the given research cache and language cache.
     */
    public ResearchProvider(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> registries, String modId, List<ResearchSubProvider> subProviders, ResearchCache researchCache, LanguageProviderCache langCache) {
        this.packOutput = packOutput;
        this.registries = registries;
        this.modId = modId;
        this.subProviders = subProviders;
        this.researchCache = researchCache;
        this.langCache = langCache;
    }

    /**
     * Runs all registered research subproviders and writes their canonical research resources.
     */
    @Override
    public @NotNull CompletableFuture<?> run(@NotNull CachedOutput cache) {
        return this.registries.thenCompose(registries -> {
            this.bundles.clear();

            if (this.researchCache != null) {
                for (var entry : this.researchCache.build().entrySet()) {
                    this.bundles.put(entry.getKey(), new ResearchBundle(entry.getValue()));
                }
            }

            if (this.langCache != null) {
                for (var subProvider : this.subProviders) {
                    if (subProvider instanceof ResearchProviderBase base) {
                        base.injectLang(this.langCache);
                    }
                }
            }

            this.subProviders.forEach(subProvider -> subProvider.generate(this::add, registries));

            var futures = new ArrayList<CompletableFuture<?>>();
            Path dataFolder = this.packOutput.getOutputFolder(PackOutput.Target.DATA_PACK);

            for (var entry : this.bundles.entrySet()) {
                var id = entry.getKey();
                var data = entry.getValue().data();
                var base = dataFolder.resolve(id.getNamespace()).resolve(ModonomiconConstants.Data.RESEARCH_DATA_PATH).resolve(id.getPath());

                futures.add(this.save(cache, ResearchFactDefinition.CODEC, data.factDefinitions(), base.resolve("facts.json")));
                futures.add(this.save(cache, ResearchValueDefinition.CODEC, data.valueDefinitions(), base.resolve("values.json")));
                futures.add(this.save(cache, ResearchNodeDefinition.CODEC, data.nodeDefinitions(), base.resolve("nodes.json")));
                futures.add(this.save(cache, ResearchHookDefinition.CODEC, data.hookDefinitions(), base.resolve("hooks.json")));
                futures.add(this.save(cache, AdvancementResearchHookDefinition.CODEC, data.advancementHookDefinitions(), base.resolve("advancement_hooks.json")));
            }

            return CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new));
        });
    }

    /**
     * Adds one authored research bundle and rejects duplicate bundle ids.
     */
    private void add(Identifier id, ResearchBundle bundle) {
        if (this.bundles.containsKey(id)) {
            throw new IllegalStateException("Duplicate research bundle " + id);
        }
        this.bundles.put(id, bundle);
    }

    /**
     * Saves one canonical research resource list to disk.
     */
    private <T> CompletableFuture<?> save(CachedOutput cache, Codec<T> codec, List<T> values, Path path) {
        return DataProvider.saveStable(cache, list(codec, values), path);
    }

    /**
     * Encodes one list of canonical research definitions into a JSON array.
     */
    private static <T> JsonElement list(Codec<T> codec, List<T> values) {
        JsonArray array = new JsonArray();
        for (T value : values) {
            array.add(codec.encodeStart(JsonOps.INSTANCE, value).getOrThrow());
        }
        return array;
    }

    /**
     * Returns the display name used by the datagen runtime.
     */
    @Override
    public @NotNull String getName() {
        return "Research Data: " + this.modId;
    }
}
