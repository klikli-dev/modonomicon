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
import com.klikli_dev.modonomicon.network.Message;
import com.klikli_dev.modonomicon.network.Networking;
import com.klikli_dev.modonomicon.network.messages.SyncMultiblockDataMessage;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraftforge.event.OnDatapackSyncEvent;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;


public class MultiblockDataManager extends SimpleJsonResourceReloadListener {
    public static final String FOLDER = Data.MULTIBLOCK_DATA_PATH;
    public static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

    private static final MultiblockDataManager instance = new MultiblockDataManager();

    private ConcurrentMap<ResourceLocation, Multiblock> multiblocks = new ConcurrentHashMap<>();
    private boolean loaded;

    private MultiblockDataManager() {
        super(GSON, FOLDER);
    }

    public static MultiblockDataManager get() {
        return instance;
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
        return new SyncMultiblockDataMessage(this.multiblocks);
    }

    public void onDatapackSyncPacket(SyncMultiblockDataMessage message) {
        this.preLoad();
        this.multiblocks = message.multiblocks;
        this.onLoadingComplete();
    }

    public void onDatapackSync(OnDatapackSyncEvent event) {
        Message syncMessage = this.getSyncMessage();

        if (event.getPlayer() != null) {
            Networking.sendToSplit(event.getPlayer(), syncMessage);
        } else {
            for (ServerPlayer player : event.getPlayerList().getPlayers()) {
                Networking.sendToSplit(player, syncMessage);
            }
        }
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
            var multiblock = LoaderRegistry.getMultiblockJsonLoader(type).fromJson(json);
            multiblock.setId(entry.getKey());
            this.multiblocks.put(multiblock.getId(), multiblock);
        }

        this.onLoadingComplete();
    }
}
