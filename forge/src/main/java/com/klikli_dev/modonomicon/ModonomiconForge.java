/*
 * SPDX-FileCopyrightText: 2023 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon;

import com.klikli_dev.modonomicon.bookstate.BookUnlockStateManager;
import com.klikli_dev.modonomicon.bookstate.BookVisualStateManager;
import com.klikli_dev.modonomicon.client.BookModel;
import com.klikli_dev.modonomicon.client.ClientTicks;
import com.klikli_dev.modonomicon.client.render.MultiblockPreviewRenderer;
import com.klikli_dev.modonomicon.client.render.page.PageRendererRegistry;
import com.klikli_dev.modonomicon.client.render.pip.GuiDirectEntryConnectionRenderer;
import com.klikli_dev.modonomicon.client.render.pip.GuiMultiblockRenderer;
import com.klikli_dev.modonomicon.client.render.state.pip.GuiDirectEntryConnectionRenderState;
import com.klikli_dev.modonomicon.client.render.state.pip.GuiMultiblockRenderState;
import com.klikli_dev.modonomicon.config.ClientConfig;
import com.klikli_dev.modonomicon.config.ServerConfig;
import com.klikli_dev.modonomicon.data.BookDataManager;
import com.klikli_dev.modonomicon.data.MultiblockDataManager;
import com.klikli_dev.modonomicon.datagen.DataGenerators;
import com.klikli_dev.modonomicon.integration.LecternIntegration;
import com.klikli_dev.modonomicon.item.IsBookOpen;
import com.klikli_dev.modonomicon.network.Networking;
import com.klikli_dev.modonomicon.registry.CommandRegistry;
import com.klikli_dev.modonomicon.registry.CreativeModeTabRegistry;
import com.klikli_dev.modonomicon.registry.RegistryBootstrap;
import com.mojang.blaze3d.framegraph.FramePass;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelTargetBundle;
import net.minecraft.client.renderer.item.properties.conditional.ConditionalItemModelProperties;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.FramePassManager;
import net.minecraftforge.client.event.*;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.event.*;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.player.AdvancementEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.level.LevelEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;

@Mod(Modonomicon.MOD_ID)
public class ModonomiconForge {

    public ModonomiconForge(FMLJavaModLoadingContext context) {
        // This method is invoked by the Forge mod loader when it is ready
        // to load your mod. You can access Forge and Common code in this
        // project.

        // Use Forge to bootstrap the Common mod.
        Modonomicon.init();

        context.registerConfig(ModConfig.Type.CLIENT, ClientConfig.get().spec);
        context.registerConfig(ModConfig.Type.SERVER, ServerConfig.get().spec);

        var modBusGroup = context.getModBusGroup();


        //Most registries are handled by common, but creative tabs are easier per loader
        CreativeModeTabRegistry.CREATIVE_MODE_TABS.register(modBusGroup);

        //directly register event handlers
        FMLCommonSetupEvent.getBus(modBusGroup).addListener(this::onCommonSetup);
        BuildCreativeModeTabContentsEvent.BUS.addListener(CreativeModeTabRegistry::onCreativeModeTabBuildContents);

        //register data managers as reload listeners
        AddReloadListenerEvent.BUS.addListener((AddReloadListenerEvent e) -> {
            BookDataManager.get().registries(e.getRegistries());
            e.addListener(BookDataManager.get());

            MultiblockDataManager.get().registries(e.getRegistries());
            e.addListener(MultiblockDataManager.get());
        });

        //register commands
        RegisterCommandsEvent.BUS.addListener((RegisterCommandsEvent e) ->
                CommandRegistry.registerCommands(e.getDispatcher())
        );
        RegisterClientCommandsEvent.BUS.addListener((RegisterClientCommandsEvent e) ->
                CommandRegistry.registerClientCommands(e.getDispatcher())
        );

        //datapack sync = build books and sync to client
        OnDatapackSyncEvent.BUS.addListener((OnDatapackSyncEvent e) -> {
            if (e.getPlayer() != null) {
                BookDataManager.get().onDatapackSync(e.getPlayer());
                MultiblockDataManager.get().onDatapackSync(e.getPlayer());
            }
        });

        //sync book state on player join
        EntityJoinLevelEvent.BUS.addListener((EntityJoinLevelEvent e) -> {
            if (e.getEntity() instanceof ServerPlayer player) {
                BookUnlockStateManager.get().updateAndSyncFor(player);
                BookVisualStateManager.get().syncFor(player);
            }
        });

        //on overworld unload clear the save data reference in the state manager
        // this ensures that if another world is loaded the save data is taken from file
        // instead of bleeding in from the previous level
        LevelEvent.Unload.BUS.addListener((LevelEvent.Unload e) -> {
            if (e.getLevel() instanceof Level level && level.dimension() == Level.OVERWORLD) {
                BookUnlockStateManager.get().saveData = null;
                BookVisualStateManager.get().saveData = null;
            }
        });


        //Advancement event handling for condition/unlock system
        AdvancementEvent.AdvancementEarnEvent.BUS.addListener((AdvancementEvent.AdvancementEarnEvent e) -> BookUnlockStateManager.get().onAdvancement((ServerPlayer) e.getEntity()));

        //We use server tick to flush the queue of players that need a book state sync
        TickEvent.ServerTickEvent.Post.BUS.addListener(((TickEvent.ServerTickEvent.Post e) -> {
            BookUnlockStateManager.get().onServerTickEnd(e.server());
        }));

        //Datagen
        GatherDataEvent.getBus(modBusGroup).addListener(DataGenerators::gatherData);

        //Client stuff
        if (FMLEnvironment.dist == Dist.CLIENT) {
            FMLClientSetupEvent.getBus(modBusGroup).addListener(Client::onClientSetup);
//            modEventBus.addListener(Client::onRegisterGuiOverlays);

            ModelEvent.ModifyBakingResult.BUS.addListener(Client::onModifyBakingResult);
            RegisterPictureInPictureRendererEvent.BUS.addListener(Client::onRegisterPipRenderers);

            //register client side reload listener that will reset the fallback font to handle locale changes on the fly
            RegisterClientReloadListenersEvent.BUS.addListener((RegisterClientReloadListenersEvent e) -> {
                e.registerReloadListener(BookDataManager.Client.get());
            });
        }
    }

    public void onCommonSetup(FMLCommonSetupEvent event) {
        Networking.registerMessages();

        RegistryBootstrap.bootstrap();

        PlayerInteractEvent.RightClickBlock.BUS.addListener((PlayerInteractEvent.RightClickBlock e) -> {
            var result = LecternIntegration.rightClick(e.getEntity(), e.getLevel(), e.getHand(), e.getHitVec());
            if (result.consumesAction()) {
                e.setCancellationResult(result);
                return true; //true = cancel
            }
            return false;
        });
    }

    public static class Client {
        public static void onClientSetup(FMLClientSetupEvent event) {
            PageRendererRegistry.registerPageRenderers();

            TickEvent.ClientTickEvent.Post.BUS.addListener((TickEvent.ClientTickEvent.Post e) -> {
                ClientTicks.endClientTick(Minecraft.getInstance());
            });
            TickEvent.RenderTickEvent.Pre.BUS.addListener((TickEvent.RenderTickEvent.Pre e) -> {
                ClientTicks.renderTickStart(e.timer().getGameTimeDeltaPartialTick(true));
            });
            TickEvent.RenderTickEvent.Post.BUS.addListener((TickEvent.RenderTickEvent.Post e) -> {
                ClientTicks.renderTickEnd();
            });

            //let multiblock preview renderer handle right clicks for anchoring
            PlayerInteractEvent.RightClickBlock.BUS.addListener((PlayerInteractEvent.RightClickBlock e) -> {
                InteractionResult result = MultiblockPreviewRenderer.onPlayerInteract(e.getEntity(), e.getLevel(), e.getHand(), e.getHitVec());
                if (result.consumesAction()) {
                    e.setCancellationResult(result);
                    return true; //true = cancel
                }
                return false;
            });

            //Tick multiblock preview
            TickEvent.ClientTickEvent.Post.BUS.addListener((TickEvent.ClientTickEvent.Post e) -> {
                MultiblockPreviewRenderer.onClientTick(Minecraft.getInstance());
            });

            //TODO re-enable once forge offers an API for the new two-pass approach
//            //Render multiblock preview
//            AddFramePassEvent.BUS.addListener((AddFramePassEvent e) -> {
//                var mainCamera = Minecraft.getInstance().gameRenderer.getMainCamera();
//                e.addPass(Modonomicon.loc("multiblock_preview"), (new FramePassManager.PassDefinition() {
//                    @Override
//                    public void targets(LevelTargetBundle bundle, FramePass pass) {
//                        bundle.main = pass.readsAndWrites(bundle.main);
//                    }
//
//                    @Override
//                    public void executes() {
//                        PoseStack ps = new PoseStack();
//                        ps.translate(mainCamera.getPosition().multiply(-1, -1, -1));
//                        ps.pushPose();
//                        MultiblockPreviewRenderer.onRenderLevelLastEvent(ps);
//                        ps.popPose();
//                    }
//                }));
//            });

            //register item model properties
            event.enqueueWork(() -> {
                ConditionalItemModelProperties.ID_MAPPER.put(
                        // The registry name
                        Modonomicon.loc("is_book_open"),
                        // The map codec
                        IsBookOpen.MAP_CODEC
                );
            });

        }

        public static void onModifyBakingResult(ModelEvent.ModifyBakingResult event) {
            BookModel.replace(event.getResults().itemStackModels());
        }

        //Currently done in MixinGui because forge removed the event and LayeredDraw seems not up to the task yet
//        public static void onRegisterGuiOverlays(RegisterGuiOverlaysEvent event) {
//            event.registerBelow(VanillaGuiOverlay.BOSS_EVENT_PROGRESS.id(), "multiblock_hud", (gui, guiGraphics, partialTick, screenWidth, screenHeight) -> {
//                MultiblockPreviewRenderer.onRenderHUD(guiGraphics, partialTick);
//            });
//        }

        public static void onRegisterPipRenderers(RegisterPictureInPictureRendererEvent event) {
            event.register(
                    new GuiDirectEntryConnectionRenderer(event.getBufferSource())
            );
            event.register(
                    new GuiMultiblockRenderer(event.getBufferSource())
            );
        }
    }
}
