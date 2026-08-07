/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.datagen;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ModonomiconItemModelProvider implements DataProvider {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private final PackOutput output;
    private final List<ItemModelEntry> entries = new ArrayList<>();

    public ModonomiconItemModelProvider(PackOutput output) {
        this.output = output;
    }

    public void addFlatItem(Item item, Identifier textureLocation) {
        this.entries.add(new ItemModelEntry(item, textureLocation));
    }

    @Override
    public CompletableFuture<?> run(CachedOutput cache) {
        List<CompletableFuture<?>> futures = new ArrayList<>();
        for (ItemModelEntry entry : this.entries) {
            String namespace = entry.item.builtInRegistryHolder().key().identifier().getNamespace();
            String path = entry.item.builtInRegistryHolder().key().identifier().getPath();

            // Write item model JSON (models/item/<id>.json)
            JsonObject model = new JsonObject();
            model.addProperty("parent", "minecraft:item/generated");
            JsonObject textures = new JsonObject();
            textures.addProperty("layer0", entry.textureLocation.toString());
            model.add("textures", textures);

            Path modelPath = this.output.getOutputFolder()
                    .resolve("assets/" + namespace + "/models/item/" + path + ".json");
            futures.add(DataProvider.saveStable(cache, (JsonElement) model, modelPath));

            // Write item info JSON (items/<id>.json)
            JsonObject itemInfo = new JsonObject();
            JsonObject modelRef = new JsonObject();
            modelRef.addProperty("type", "minecraft:model");
            modelRef.addProperty("model", namespace + ":item/" + path);
            itemInfo.add("model", modelRef);

            Path itemInfoPath = this.output.getOutputFolder()
                    .resolve("assets/" + namespace + "/items/" + path + ".json");
            futures.add(DataProvider.saveStable(cache, (JsonElement) itemInfo, itemInfoPath));
        }
        return CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new));
    }

    @Override
    public String getName() {
        return "Modonomicon Item Models";
    }

    private record ItemModelEntry(Item item, Identifier textureLocation) {}
}
