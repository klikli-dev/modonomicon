// SPDX-FileCopyrightText: 2024 klikli-dev
//
// SPDX-License-Identifier: MIT

package com.klikli_dev.modonomicon.api.datagen;

import com.google.gson.JsonObject;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.block.Block;

import java.nio.file.Path;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

public abstract class AbstractModonomiconLanguageProvider implements ModonomiconLanguageProvider, DataProvider {
    private final Map<String, String> data = new TreeMap<>();
    private final PackOutput output;
    private final String modid;
    private final String locale;

    private final ModonomiconLanguageProvider cachedProvider;

    /**
     * Creates a new language provider.
     *
     * @param output         the pack output to write to.
     * @param modid          the mod id.
     * @param locale         the locale this provider should generate.
     * @param cachedProvider the cached provider - its contents will be written into this provider.
     */
    public AbstractModonomiconLanguageProvider(PackOutput output, String modid, String locale, ModonomiconLanguageProvider cachedProvider) {
        this.output = output;
        this.modid = modid;
        this.locale = locale;
        this.cachedProvider = cachedProvider;
    }

    public AbstractModonomiconLanguageProvider(PackOutput output, String modid, String locale) {
        this(output, modid, locale, null);
    }

    @Override
    public String locale() {
        return this.locale;
    }

    protected abstract void addTranslations();

    @Override
    public Map<String, String> data() {
        return this.data;
    }

    @Override
    public CompletableFuture<?> run(CachedOutput cache) {
        this.addTranslations();

        if (this.cachedProvider != null && !this.cachedProvider.data().isEmpty())
            this.data.putAll(this.cachedProvider.data());

        if (!this.data.isEmpty())
            return this.save(cache, this.output.getOutputFolder(PackOutput.Target.RESOURCE_PACK).resolve(this.modid).resolve("lang").resolve(this.locale + ".json"));

        return CompletableFuture.allOf();
    }

    @Override
    public String getName() {
        return "Languages: " + this.locale;
    }

    private CompletableFuture<?> save(CachedOutput cache, Path target) {
        JsonObject json = new JsonObject();
        this.data.forEach(json::addProperty);

        return DataProvider.saveStable(cache, json, target);
    }

    public void addBlock(Supplier<? extends Block> key, String name) {
        this.add(key.get(), name);
    }

    public void add(Block key, String name) {
        this.add(key.getDescriptionId(), name);
    }

    public void addItem(Supplier<? extends Item> key, String name) {
        this.add(key.get(), name);
    }

    public void add(Item key, String name) {
        this.add(key.getDescriptionId(), name);
    }

    public void addItemStack(Supplier<ItemStack> key, String name) {
        this.add(key.get(), name);
    }

    public void add(ItemStack key, String name) {
        this.add(key.getDescriptionId(), name);
    }

    public void addEnchantment(Supplier<? extends Enchantment> key, String name) {
        this.add(key.get(), name);
    }

    public void add(Enchantment key, String name) {
        if (key.description() != null && key.description().getContents() instanceof TranslatableContents translatableContents) {
            this.add(translatableContents.getKey(), name);
        } else {
            throw new IllegalArgumentException("Enchantment " + key + " does not have a description that is a component with translatable contents - cannot get translation key.");
        }
    }

    public void addEffect(Supplier<? extends MobEffect> key, String name) {
        this.add(key.get(), name);
    }

    public void add(MobEffect key, String name) {
        this.add(key.getDescriptionId(), name);
    }

    public void addEntityType(Supplier<? extends EntityType<?>> key, String name) {
        this.add(key.get(), name);
    }

    public void add(EntityType<?> key, String name) {
        this.add(key.getDescriptionId(), name);
    }

    public void add(String key, String value) {
        if (this.data.put(key, value) != null)
            throw new IllegalStateException("Duplicate translation key " + key);
    }
}
