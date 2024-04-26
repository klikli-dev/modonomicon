/*
 * SPDX-FileCopyrightText: 2023 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon;

import com.google.gson.JsonElement;
import com.klikli_dev.modonomicon.client.ClientTicks;
import com.klikli_dev.modonomicon.client.render.MultiblockPreviewRenderer;
import com.klikli_dev.modonomicon.client.render.page.PageRendererRegistry;
import com.klikli_dev.modonomicon.config.ClientConfig;
import com.klikli_dev.modonomicon.data.BookDataManager;
import com.klikli_dev.modonomicon.data.ReloadListenerWrapper;
import com.klikli_dev.modonomicon.network.ClientNetworking;
import com.klikli_dev.modonomicon.network.Networking;
import com.klikli_dev.modonomicon.registry.FabricClientCommandRegistry;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class ModonomiconFabricClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {

        ClientConfig.init();
        ClientNetworking.registerReceivers();

        PageRendererRegistry.registerPageRenderers();

        //build books and render markdown when client receives recipes
        //done in MixinClientPacketListener, because we have no event in Fabric

        //register client commands
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) ->
                FabricClientCommandRegistry.registerClientCommands(dispatcher)
        );

        //Client tick
        //Client render start + end done in MixinGameRenderer, because we have no event in Fabric
        ClientTickEvents.END_CLIENT_TICK.register(ClientTicks::endClientTick);

        //let multiblock preview renderer handle right clicks for anchoring
        UseBlockCallback.EVENT.register(MultiblockPreviewRenderer::onPlayerInteract);


        //Tick multiblock preview
        ClientTickEvents.END_CLIENT_TICK.register(MultiblockPreviewRenderer::onClientTick);

        //Render multiblock preview
        //done in MixinLevelRenderer, because we have no event in Fabric

        //render multiblock hud
        HudRenderCallback.EVENT.register(MultiblockPreviewRenderer::onRenderHUD);

        //register client side reload listener that will reset the fallback font to handle locale changes on the fly
        ResourceManagerHelper.get(PackType.CLIENT_RESOURCES).registerReloadListener(new ReloadListenerWrapper(
                Modonomicon.loc("book_data_manager_client"),
                BookDataManager.Client.get()
        ));

        //book geometry loader
        //done in MixinModelManager, because we have no event in Fabric
    }
}
