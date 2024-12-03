/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.klikli_dev.modonomicon.api.ModonomiconConstants.Data;
import com.klikli_dev.modonomicon.api.multiblock.Multiblock;
import com.klikli_dev.modonomicon.networking.Message;
import com.klikli_dev.modonomicon.networking.SyncMultiblockDataMessage;
import com.klikli_dev.modonomicon.platform.Services;
import it.unimi.dsi.fastutil.objects.Object2ObjectMaps;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.profiling.ProfilerFiller;

import java.util.Map;


public class MultiblockDataManager extends LegacySimpleJsonResourceReloadListener {
    public static final String FOLDER = Data.MULTIBLOCK_DATA_PATH;
    public static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

    private static final MultiblockDataManager instance = new MultiblockDataManager();

    private Map<ResourceLocation, Multiblock> multiblocks = Object2ObjectMaps.synchronize(new Object2ObjectOpenHashMap<>());
    private boolean loaded;
    private HolderLookup.Provider registries;

    private MultiblockDataManager() {
        super(GSON, FOLDER);
    }

    public static MultiblockDataManager get() {
        return instance;
    }

    public void registries(HolderLookup.Provider registries) {
        this.registries = registries;
    }

    public boolean isLoaded() {
        return this.loaded;
    }

    public Multiblock getMultiblock(ResourceLocation id) {
        return this.multiblocks.get(id);
    }

    public Map<ResourceLocation, Multiblock> getMultiblocks() {
        return this.multiblocks;
    }

    public Message getSyncMessage() {
        //we hand over a copy of the map, because otherwise in SP scenarios if we clear this.multiblocks to prepare for receiving the message, we also clear the books in the message
        return new SyncMultiblockDataMessage(this.multiblocks);
    }

    public void onDatapackSyncPacket(SyncMultiblockDataMessage message) {
        this.preLoad();
        this.multiblocks = Object2ObjectMaps.synchronize(new Object2ObjectOpenHashMap<>(message.multiblocks));
        this.onLoadingComplete();
    }

    public void onDatapackSync(ServerPlayer player) {
        //If integrated server and host (= SP or lan host), don't send as we already have it
        if(player.connection.connection.isMemoryConnection())
            return;


        Message syncMessage = this.getSyncMessage();

        Services.NETWORK.sendToSplit(player, syncMessage);
    }

    public void preLoad() {
        this.loaded = false;
        this.multiblocks.clear();
    }

    protected void onLoadingComplete() {
        this.loaded = true;
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> content, ResourceManager pResourceManager, ProfilerFiller pProfiler) {
        this.preLoad();

        for (var entry : content.entrySet()) {
            var json = GsonHelper.convertToJsonObject(entry.getValue(), "multiblock json file");
            var type = ResourceLocation.tryParse(GsonHelper.getAsString(json, "type"));
            var multiblock = LoaderRegistry.getMultiblockJsonLoader(type).fromJson(json, this.registries);
            multiblock.setId(entry.getKey());
            this.multiblocks.put(multiblock.getId(), multiblock);
        }

        this.onLoadingComplete();
    }
}
