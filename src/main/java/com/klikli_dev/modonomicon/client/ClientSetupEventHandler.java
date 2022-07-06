/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.client;

import com.klikli_dev.modonomicon.Modonomicon;
import com.klikli_dev.modonomicon.api.ModonimiconConstants;
import com.klikli_dev.modonomicon.client.render.MultiblockPreviewRenderer;
import com.klikli_dev.modonomicon.client.render.page.PageRendererRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.world.InteractionResult;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

public class ClientSetupEventHandler {

    public static void onClientSetup(FMLClientSetupEvent event) {
        PageRendererRegistry.registerPageRenderers();

        registerItemModelProperties(event);

        //Keep track of ticks. We do this to stay loader agnostic so we can do a fabric version in the future
        MinecraftForge.EVENT_BUS.addListener((TickEvent.ClientTickEvent e) -> {
            if (e.phase == TickEvent.Phase.END) {
                ClientTicks.endClientTick(Minecraft.getInstance());
            }
        });
        MinecraftForge.EVENT_BUS.addListener((TickEvent.RenderTickEvent e) -> {
            if (e.phase == TickEvent.Phase.START) {
                ClientTicks.renderTickStart(e.renderTickTime);
            } else {
                ClientTicks.renderTickEnd();
            }
        });


        //let multiblock preview renderer handle right clicks for anchoring
        MinecraftForge.EVENT_BUS.addListener((PlayerInteractEvent.RightClickBlock e) -> {
            InteractionResult result = MultiblockPreviewRenderer.onPlayerInteract(e.getPlayer(), e.getWorld(), e.getHand(), e.getHitVec());
            if (result.consumesAction()) {
                e.setCanceled(true);
                e.setCancellationResult(result);
            }
        });

        //Tick multiblock preview
        MinecraftForge.EVENT_BUS.addListener((TickEvent.ClientTickEvent e) -> {
            if (e.phase == TickEvent.Phase.END) {
                MultiblockPreviewRenderer.onClientTick(Minecraft.getInstance());
            }
        });

        //Draw multiblock as overlay
        MinecraftForge.EVENT_BUS.addListener((RenderGameOverlayEvent.Post e) -> {
            if (e.getType() == RenderGameOverlayEvent.ElementType.ALL) {
                MultiblockPreviewRenderer.onRenderHUD(e.getPoseStack(), e.getPartialTick());
            }
        });

        //Register event handler
        MinecraftForge.EVENT_BUS.addListener(MultiblockPreviewRenderer::onRenderLevelLastEvent);

        //Not safe to call during parallel load, so register to run threadsafe.
        event.enqueueWork(() -> {
            //Register screen factories
            Modonomicon.LOGGER.debug("Registered Screen Containers");
        });

        Modonomicon.LOGGER.info("Client setup complete.");
    }

    public static void registerItemModelProperties(FMLClientSetupEvent event) {

        //Not safe to call during parallel load, so register to run threadsafe
        event.enqueueWork(() -> {
            //Register item model properties

            Modonomicon.LOGGER.debug("Registered Item Properties");
        });
    }

    public static void onModelBake(ModelBakeEvent event) {
        ModelResourceLocation key = new ModelResourceLocation(ModonimiconConstants.Data.Book.ITEM_ID, "inventory");
        BakedModel oldModel = event.getModelRegistry().get(key);
        if (oldModel != null) {
            event.getModelRegistry().put(key, new BookBakedModel(oldModel, event.getModelLoader()));
        }
    }
}
