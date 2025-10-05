/*
 * SPDX-FileCopyrightText: 2023 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon;

import com.klikli_dev.modonomicon.bookstate.BookUnlockStateManager;
import com.klikli_dev.modonomicon.bookstate.BookVisualStateManager;
import com.klikli_dev.modonomicon.client.BookModelLoader;
import com.klikli_dev.modonomicon.client.ClientTicks;
import com.klikli_dev.modonomicon.client.render.MultiblockPreviewRenderer;
import com.klikli_dev.modonomicon.client.render.page.PageRendererRegistry;
import com.klikli_dev.modonomicon.config.ClientConfig;
import com.klikli_dev.modonomicon.data.BookDataManager;
import com.klikli_dev.modonomicon.data.LoaderRegistry;
import com.klikli_dev.modonomicon.data.MultiblockDataManager;
import com.klikli_dev.modonomicon.datagen.DataGenerators;
import com.klikli_dev.modonomicon.integration.LecternIntegration;
import com.klikli_dev.modonomicon.item.BookOpenStateItemPropertyGetter;
import com.klikli_dev.modonomicon.network.Networking;
import com.klikli_dev.modonomicon.registry.CommandRegistry;
import com.klikli_dev.modonomicon.registry.CreativeModeTabRegistry;
import com.klikli_dev.modonomicon.registry.ItemRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.client.event.RecipesUpdatedEvent;
import net.minecraftforge.client.event.RegisterClientCommandsEvent;
import net.minecraftforge.client.event.RegisterClientReloadListenersEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.OnDatapackSyncEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.player.AdvancementEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.level.LevelEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;

@Mod(Modonomicon.MOD_ID)
public class ModonomiconForge {

    public ModonomiconForge() {
        // This method is invoked by the Forge mod loader when it is ready
        // to load your mod. You can access Forge and Common code in this
        // project.

        // Use Forge to bootstrap the Common mod.
        Modonomicon.init();

        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, ClientConfig.get().spec);

        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        //Most registries are handled by common, but creative tabs are easier per loader
        CreativeModeTabRegistry.CREATIVE_MODE_TABS.register(modEventBus);

        //directly register event handlers
        modEventBus.addListener(this::onCommonSetup);
        modEventBus.addListener(CreativeModeTabRegistry::onCreativeModeTabBuildContents);

        //register data managers as reload listeners
        MinecraftForge.EVENT_BUS.addListener((AddReloadListenerEvent e) -> {
            BookDataManager.get().registries(e.getRegistryAccess());
            e.addListener(BookDataManager.get());

            MultiblockDataManager.get().registries(e.getRegistryAccess());
            e.addListener(MultiblockDataManager.get());
        });

        //register commands
        MinecraftForge.EVENT_BUS.addListener((RegisterCommandsEvent e) ->
                CommandRegistry.registerCommands(e.getDispatcher())
        );
        MinecraftForge.EVENT_BUS.addListener((RegisterClientCommandsEvent e) ->
                CommandRegistry.registerClientCommands(e.getDispatcher())
        );

        //datapack sync = build books and sync to client
        MinecraftForge.EVENT_BUS.addListener((OnDatapackSyncEvent e) -> {
            if (e.getPlayer() != null) {
                BookDataManager.get().onDatapackSync(e.getPlayer());
                MultiblockDataManager.get().onDatapackSync(e.getPlayer());
            }
        });

        //sync book state on player join
        MinecraftForge.EVENT_BUS.addListener((EntityJoinLevelEvent e) -> {
            if (e.getEntity() instanceof ServerPlayer player) {
                BookUnlockStateManager.get().updateAndSyncFor(player);
                BookVisualStateManager.get().syncFor(player);
            }
        });

        //on overworld unload clear the save data reference in the state manager
        // this ensures that if another world is loaded the save data is taken from file
        // instead of bleeding in from the previous level
        MinecraftForge.EVENT_BUS.addListener((LevelEvent.Unload e) -> {
            if (e.getLevel() instanceof Level level && level.dimension() == Level.OVERWORLD) {
                BookUnlockStateManager.get().saveData = null;
                BookVisualStateManager.get().saveData = null;
            }
        });


        //Advancement event handling for condition/unlock system
        MinecraftForge.EVENT_BUS.addListener((AdvancementEvent.AdvancementEarnEvent e) -> BookUnlockStateManager.get().onAdvancement((ServerPlayer) e.getEntity()));

        //We use server tick to flush the queue of players that need a book state sync
        MinecraftForge.EVENT_BUS.addListener(((TickEvent.ServerTickEvent.Post e) -> {
            BookUnlockStateManager.get().onServerTickEnd(e.getServer());
        }));

        //Datagen
        modEventBus.addListener(DataGenerators::gatherData);

        //Client stuff
        if (FMLEnvironment.dist == Dist.CLIENT) {
            modEventBus.addListener(Client::onClientSetup);
            modEventBus.addListener(Client::onRegisterGeometryLoaders);
//            modEventBus.addListener(Client::onRegisterGuiOverlays);
            //build books and render markdown when client receives recipes
            MinecraftForge.EVENT_BUS.addListener(Client::onRecipesUpdated);

            //register client side reload listener that will reset the fallback font to handle locale changes on the fly
            modEventBus.addListener((RegisterClientReloadListenersEvent e) -> {
                e.registerReloadListener(BookDataManager.Client.get());
            });
        }
    }

    public void onCommonSetup(FMLCommonSetupEvent event) {
        Networking.registerMessages();

        LoaderRegistry.registerLoaders();

        MinecraftForge.EVENT_BUS.addListener((PlayerInteractEvent.RightClickBlock e) -> {
            var result = LecternIntegration.rightClick(e.getEntity(), e.getLevel(), e.getHand(), e.getHitVec());
            if (result.consumesAction()) {
                e.setCanceled(true);
                e.setCancellationResult(result);
            }
        });
    }

    public static class Client {
        public static void onClientSetup(FMLClientSetupEvent event) {
            PageRendererRegistry.registerPageRenderers();

            MinecraftForge.EVENT_BUS.addListener((TickEvent.ClientTickEvent e) -> {
                if (e.phase == TickEvent.Phase.END) {
                    ClientTicks.endClientTick(Minecraft.getInstance());
                }
            });
            MinecraftForge.EVENT_BUS.addListener((TickEvent.RenderTickEvent e) -> {
                if (e.phase == TickEvent.Phase.START) {
                    ClientTicks.renderTickStart(e.getTimer().getGameTimeDeltaPartialTick(true));
                } else {
                    ClientTicks.renderTickEnd();
                }
            });

            //let multiblock preview renderer handle right clicks for anchoring
            MinecraftForge.EVENT_BUS.addListener((PlayerInteractEvent.RightClickBlock e) -> {
                InteractionResult result = MultiblockPreviewRenderer.onPlayerInteract(e.getEntity(), e.getLevel(), e.getHand(), e.getHitVec());
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

//            //Render multiblock preview
            //currently done with MixinLevelRenderer because forge no longer gives us a posestack ..
//            MinecraftForge.EVENT_BUS.addListener((RenderLevelStageEvent e) -> {
//                if (e.getStage() == RenderLevelStageEvent.Stage.AFTER_TRIPWIRE_BLOCKS) { //After translucent causes block entities to error out on render in preview
//                    var pose = new PoseStack();
//                    pose.last().pose = e.getPoseStack();
//                    MultiblockPreviewRenderer.onRenderLevelLastEvent(pose);
//                }
//            });
            event.enqueueWork(() -> {
                //register item properties
                ItemProperties.registerGeneric(Modonomicon.loc("open_state"), new BookOpenStateItemPropertyGetter());
            });
        }

        /**
         * Needs to be in the client inner class to not load clientlevel on server
         */
        public static void onRecipesUpdated(RecipesUpdatedEvent event) {
            BookDataManager.get().onRecipesUpdated(Minecraft.getInstance().level);
        }

        public static void onRegisterGeometryLoaders(ModelEvent.RegisterGeometryLoaders event) {
            event.register("book_model_loader", new BookModelLoader());
        }

        //Currently done in MixinGui because forge removed the event and LayeredDraw seems not up to the task yet
//        public static void onRegisterGuiOverlays(RegisterGuiOverlaysEvent event) {
//            event.registerBelow(VanillaGuiOverlay.BOSS_EVENT_PROGRESS.id(), "multiblock_hud", (gui, guiGraphics, partialTick, screenWidth, screenHeight) -> {
//                MultiblockPreviewRenderer.onRenderHUD(guiGraphics, partialTick);
//            });
//        }
    }
}