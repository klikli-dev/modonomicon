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
import com.klikli_dev.modonomicon.network.Message;
import com.klikli_dev.modonomicon.network.Networking;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraftforge.event.OnDatapackSyncEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.Map;


public class MultiblockDataManager extends SimpleJsonResourceReloadListener {
    public static final String FOLDER = Data.MULTIBLOCK_DATA_PATH;
    public static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

    private static final MultiblockDataManager instance = new MultiblockDataManager();

    //private Map<ResourceLocation, Book> books = new HashMap<>();
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

    public Message getSyncMessage() {
        //TODO: implement
        //return new SyncBookDataMessage(this.books);
        throw new UnsupportedOperationException("Not implemented yet");
    }

    //TODO: onDatapackSyncPacket
//    public void onDatapackSyncPacket(SyncBookDataMessage message) {
//
//        this.books = message.books;
//        this.postLoad();
//        this.onLoadingComplete();
//    }

    @SubscribeEvent
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

    protected void onLoadingComplete() {
        this.loaded = true;
    }



    @Override
    protected void apply(Map<ResourceLocation, JsonElement> content, ResourceManager pResourceManager, ProfilerFiller pProfiler) {
        this.loaded = false;

        this.onLoadingComplete();
    }
}
