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
import com.klikli_dev.modonomicon.network.Networking;
import com.klikli_dev.modonomicon.registry.CommandRegistry;
import com.klikli_dev.modonomicon.registry.CreativeModeTabRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.javafmlmod.FMLJavaModLoadingContext;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.client.event.*;
import net.neoforged.neoforge.client.gui.overlay.VanillaGuiOverlay;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.AddReloadListenerEvent;
import net.neoforged.neoforge.event.OnDatapackSyncEvent;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.TickEvent;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.player.AdvancementEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.level.LevelEvent;

@Mod(Modonomicon.MOD_ID)
public class ModonomiconNeo {

    public ModonomiconNeo() {
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
        NeoForge.EVENT_BUS.addListener((AddReloadListenerEvent e) -> {
            e.addListener(BookDataManager.get());
            e.addListener(MultiblockDataManager.get());
        });

        //register commands
        NeoForge.EVENT_BUS.addListener((RegisterCommandsEvent e) ->
                CommandRegistry.registerCommands(e.getDispatcher())
        );
        NeoForge.EVENT_BUS.addListener((RegisterClientCommandsEvent e) ->
                CommandRegistry.registerClientCommands(e.getDispatcher())
        );

        //datapack sync = build books and sync to client
        NeoForge.EVENT_BUS.addListener((OnDatapackSyncEvent e) -> {
            if (e.getPlayer() != null) {
                BookDataManager.get().onDatapackSync(e.getPlayer());
                MultiblockDataManager.get().onDatapackSync(e.getPlayer());
            }
        });

        //sync book state on player join
        NeoForge.EVENT_BUS.addListener((EntityJoinLevelEvent e) -> {
            if (e.getEntity() instanceof ServerPlayer player) {
                BookUnlockStateManager.get().updateAndSyncFor(player);
                BookVisualStateManager.get().syncFor(player);
            }
        });

        //on overworld unload clear the save data reference in the state manager
        // this ensures that if another world is loaded the save data is taken from file
        // instead of bleeding in from the previous level
        NeoForge.EVENT_BUS.addListener((LevelEvent.Unload e) -> {
            if (e.getLevel() instanceof Level level && level.dimension() == Level.OVERWORLD) {
                BookUnlockStateManager.get().saveData = null;
                BookVisualStateManager.get().saveData = null;
            }
        });


        //Advancement event handling for condition/unlock system
        NeoForge.EVENT_BUS.addListener((AdvancementEvent.AdvancementEarnEvent e) -> BookUnlockStateManager.get().onAdvancement((ServerPlayer) e.getEntity()));

        //Datagen
        modEventBus.addListener(DataGenerators::gatherData);

        //Client stuff
        if (FMLEnvironment.dist == Dist.CLIENT) {
            modEventBus.addListener(Client::onClientSetup);
            modEventBus.addListener(Client::onRegisterGeometryLoaders);
            modEventBus.addListener(Client::onRegisterGuiOverlays);
            //build books and render markdown when client receives recipes
            NeoForge.EVENT_BUS.addListener(Client::onRecipesUpdated);
        }
    }

    public void onCommonSetup(FMLCommonSetupEvent event) {
        Networking.registerMessages();

        LoaderRegistry.registerLoaders();
    }

    public static class Client {
        public static void onClientSetup(FMLClientSetupEvent event) {
            PageRendererRegistry.registerPageRenderers();

            NeoForge.EVENT_BUS.addListener((TickEvent.ClientTickEvent e) -> {
                if (e.phase == TickEvent.Phase.END) {
                    ClientTicks.endClientTick(Minecraft.getInstance());
                }
            });
            NeoForge.EVENT_BUS.addListener((TickEvent.RenderTickEvent e) -> {
                if (e.phase == TickEvent.Phase.START) {
                    ClientTicks.renderTickStart(e.renderTickTime);
                } else {
                    ClientTicks.renderTickEnd();
                }
            });

            //let multiblock preview renderer handle right clicks for anchoring
            NeoForge.EVENT_BUS.addListener((PlayerInteractEvent.RightClickBlock e) -> {
                InteractionResult result = MultiblockPreviewRenderer.onPlayerInteract(e.getEntity(), e.getLevel(), e.getHand(), e.getHitVec());
                if (result.consumesAction()) {
                    e.setCanceled(true);
                    e.setCancellationResult(result);
                }
            });

            //Tick multiblock preview
            NeoForge.EVENT_BUS.addListener((TickEvent.ClientTickEvent e) -> {
                if (e.phase == TickEvent.Phase.END) {
                    MultiblockPreviewRenderer.onClientTick(Minecraft.getInstance());
                }
            });

            //Render multiblock preview
            NeoForge.EVENT_BUS.addListener((RenderLevelStageEvent e) -> {
                if (e.getStage() == RenderLevelStageEvent.Stage.AFTER_TRIPWIRE_BLOCKS) { //After translucent causes block entities to error out on render in preview
                    MultiblockPreviewRenderer.onRenderLevelLastEvent(e.getPoseStack());
                }
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

        public static void onRegisterGuiOverlays(RegisterGuiOverlaysEvent event) {
            event.registerBelow(VanillaGuiOverlay.BOSS_EVENT_PROGRESS.id(), "multiblock_hud", (gui, guiGraphics, partialTick, screenWidth, screenHeight) -> {
                MultiblockPreviewRenderer.onRenderHUD(guiGraphics, partialTick);
            });
        }
    }
}