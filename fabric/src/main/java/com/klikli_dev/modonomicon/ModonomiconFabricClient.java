package com.klikli_dev.modonomicon;

import com.klikli_dev.modonomicon.client.ClientTicks;
import com.klikli_dev.modonomicon.client.render.MultiblockPreviewRenderer;
import com.klikli_dev.modonomicon.client.render.page.PageRendererRegistry;
import com.klikli_dev.modonomicon.data.BookDataManager;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.model.ModelLoadingRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.client.resources.model.ModelResourceLocation;

public class ModonomiconFabricClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        PageRendererRegistry.registerPageRenderers();

        //build books and render markdown when client receives recipes
        //done in MixinClientPacketListener, because we have no event in Fabric

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

        //book geometry loader
        //done in MixinModelManager, because we have no event in Fabric

        //TODO: Fabric: FluidRenderHelper/Tooltip
        //gui helper
    }
}
