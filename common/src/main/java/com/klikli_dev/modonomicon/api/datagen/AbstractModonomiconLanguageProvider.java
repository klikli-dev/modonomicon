// SPDX-FileCopyrightText: 2024 klikli-dev
//
// SPDX-License-Identifier: MIT

package com.klikli_dev.modonomicon.api.datagen;

import com.google.gson.JsonObject;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public abstract class AbstractModonomiconLanguageProvider implements ModonomiconLanguageProvider, DataProvider {
    protected final Map<String, String> data = new Object2ObjectOpenHashMap<>();
    protected final PackOutput output;
    protected final String modId;
    protected final String locale;

    private final ModonomiconLanguageProvider cachedProvider;

    /**
     * Creates a new language provider.
     *
     * @param output         the pack output to write to.
     * @param modId          the mod id.
     * @param locale         the locale this provider should generate.
     * @param cachedProvider the cached provider - its contents will be written into this provider.
     */
    public AbstractModonomiconLanguageProvider(PackOutput output, String modId, String locale, ModonomiconLanguageProvider cachedProvider) {
        this.output = output;
        this.modId = modId;
        this.locale = locale;
        this.cachedProvider = cachedProvider;
    }

    public AbstractModonomiconLanguageProvider(PackOutput output, String modId, String locale) {
        this(output, modId, locale, null);
    }

    protected abstract void addTranslations();

    @Override
    public @NotNull CompletableFuture<?> run(@NotNull CachedOutput cache) {
        this.addTranslations();

        if (this.cachedProvider != null) {
            this.cachedProvider.data(this.data::put);
        }

        if (!this.data.isEmpty())
            return this.save(cache, this.output.getOutputFolder(PackOutput.Target.RESOURCE_PACK).resolve(this.modId).resolve("lang").resolve(this.locale + ".json"));

        return CompletableFuture.allOf();
    }

    @Override
    public @NotNull String getName() {
        return "Languages: " + this.locale;
    }

    private CompletableFuture<?> save(CachedOutput cache, Path target) {
        JsonObject json = new JsonObject();
        this.data.forEach(json::addProperty);

        return DataProvider.saveStable(cache, json, target);
    }

    @Override
    public void accept(String key, String value) {
        if (this.data.put(key, value) != null)
            throw new IllegalStateException("Duplicate translation key " + key);
    }
}
