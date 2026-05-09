/*
 * SPDX-FileCopyrightText: 2026 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.data;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.mojang.logging.LogUtils;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.profiling.ProfilerFiller;
import org.slf4j.Logger;

import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;

/**
 * Copied from 1.21.1
 */
public abstract class LegacySimpleJsonResourceReloadListener extends SimplePreparableReloadListener<Map<Identifier, JsonElement>> {
    private static final Logger LOGGER = LogUtils.getLogger();
    private final Gson gson;
    private final String directory;

    public LegacySimpleJsonResourceReloadListener(Gson gson, String directory) {
        this.gson = gson;
        this.directory = directory;
    }

    /**
     * Performs any reloading that can be done off-thread, such as file IO
     */
    protected Map<Identifier, JsonElement> prepare(ResourceManager resourceManager, ProfilerFiller profiler) {
        Map<Identifier, JsonElement> map = new HashMap<>();
        scanDirectory(resourceManager, this.directory, this.gson, map);
        return map;
    }

    public static void scanDirectory(ResourceManager resourceManager, String name, Gson gson, Map<Identifier, JsonElement> output) {
        FileToIdConverter filetoidconverter = FileToIdConverter.json(name);

        for (Map.Entry<Identifier, Resource> entry : filetoidconverter.listMatchingResources(resourceManager).entrySet()) {
            Identifier Identifier = entry.getKey();
            Identifier resourcelocation1 = filetoidconverter.fileToId(Identifier);

            try (Reader reader = entry.getValue().openAsReader()) {
                JsonElement jsonelement = GsonHelper.fromJson(gson, reader, JsonElement.class);
                JsonElement jsonelement1 = output.put(resourcelocation1, jsonelement);
                if (jsonelement1 != null) {
                    throw new IllegalStateException("Duplicate data file ignored with ID " + resourcelocation1);
                }
            } catch (IllegalArgumentException | IOException | JsonParseException jsonparseexception) {
                LOGGER.error("Couldn't parse data file {} from {}", resourcelocation1, Identifier, jsonparseexception);
            }
        }
    }

    protected Identifier getPreparedPath(Identifier rl) {
        return rl.withPath(this.directory + "/" + rl.getPath() + ".json");
    }
}
