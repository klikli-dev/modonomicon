/*
 * LGPL-3.0
 *
 * Copyright (C) 2022 klikli-dev
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

package com.klikli_dev.modonomicon.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.klikli_dev.modonomicon.api.ModonimiconConstants.Data;
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

import java.util.HashMap;
import java.util.Map;


public class MultiblockDataManager extends SimpleJsonResourceReloadListener {
    public static final String FOLDER = Data.MULTIBLOCK_DATA_PATH;
    public static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

    private static final MultiblockDataManager instance = new MultiblockDataManager();

    private Map<ResourceLocation, Multiblock> multiblocks = new HashMap<>();
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

    public Message getSyncMessage() {
        return new SyncMultiblockDataMessage(this.multiblocks);
    }

    public void onDatapackSyncPacket(SyncMultiblockDataMessage message) {
        this.preLoad();
        this.multiblocks = message.multiblocks;
        this.onLoadingComplete();
        this.postLoad(); //needs to be called after loading complete, because that sets our loaded flag
    }

    public void onDatapackSync(OnDatapackSyncEvent event) {
        Message syncMessage = this.getSyncMessage();

        if (event.getPlayer() != null) {
            Networking.sendTo(event.getPlayer(), syncMessage);
        } else {
            for (ServerPlayer player : event.getPlayerList().getPlayers()) {
                Networking.sendTo(player, syncMessage);
            }
        }
    }

    public void postLoad() {
        BookDataManager.get().tryBuildBooks();
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
        this.postLoad(); //needs to be called after loading complete, because that sets our loaded flag
    }
}
