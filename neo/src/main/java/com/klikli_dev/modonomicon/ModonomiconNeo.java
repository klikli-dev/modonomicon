/*
 * SPDX-FileCopyrightText: 2023 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon;

import com.klikli_dev.modonomicon.bookstate.BookVisualStateManager;
import com.klikli_dev.modonomicon.book.runtime.DemoRuntimeBookContent;
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
import com.klikli_dev.modonomicon.research.ResearchServices;
import com.klikli_dev.modonomicon.research.data.ResearchDataManager;
import com.klikli_dev.modonomicon.research.state.ResearchStateManager;
import com.klikli_dev.modonomicon.registry.TriggerTypeRegistry;
import com.klikli_dev.modonomicon.registry.CommandRegistry;
import com.klikli_dev.modonomicon.registry.CreativeModeTabRegistry;
import com.klikli_dev.modonomicon.registry.RegistryBootstrap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.item.properties.conditional.ConditionalItemModelProperties;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.client.event.*;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.AddServerReloadListenersEvent;
import net.neoforged.neoforge.resource.ContextAwareReloadListener;
import net.neoforged.neoforge.event.OnDatapackSyncEvent;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.player.AdvancementEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.level.LevelEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Mod(Modonomicon.MOD_ID)
public class ModonomiconNeo {

    public ModonomiconNeo(IEventBus modEventBus, ModContainer modContainer) {
        // This method is invoked by the Forge mod loader when it is ready
        // to load your mod. You can access Forge and Common code in this
        // project.

        // Use Forge to bootstrap the Common mod.
        Modonomicon.init();

        modContainer.registerConfig(ModConfig.Type.CLIENT, ClientConfig.get().spec);
        modContainer.registerConfig(ModConfig.Type.SERVER, ServerConfig.get().spec);

        //Most registries are handled by common, but creative tabs are easier per loader
        CreativeModeTabRegistry.CREATIVE_MODE_TABS.register(modEventBus);

        //directly register event handlers
        modEventBus.addListener(this::onCommonSetup);
        modEventBus.addListener(Networking::register);
        modEventBus.addListener(CreativeModeTabRegistry::onCreativeModeTabBuildContents);

        //register data managers as reload listeners
        NeoForge.EVENT_BUS.addListener((AddServerReloadListenersEvent e) -> {
            e.addListener(Modonomicon.loc("book_data_manager"), new ContextAwareReloadListener() {
                @Override
                public CompletableFuture<Void> reload(PreparableReloadListener.SharedState currentReload, Executor taskExecutor, PreparableReloadListener.PreparationBarrier preparationBarrier, Executor reloadExecutor) {
                    BookDataManager.get().registries(this.getRegistryLookup());
                    return BookDataManager.get().reload(currentReload, taskExecutor, preparationBarrier, reloadExecutor);
                }
            });

            e.addListener(Modonomicon.loc("multiblock_data_manager"), new ContextAwareReloadListener() {
                @Override
                public CompletableFuture<Void> reload(PreparableReloadListener.SharedState currentReload, Executor taskExecutor, PreparableReloadListener.PreparationBarrier preparationBarrier, Executor reloadExecutor) {
                    MultiblockDataManager.get().registries(this.getRegistryLookup());
                    return MultiblockDataManager.get().reload(currentReload, taskExecutor, preparationBarrier, reloadExecutor);
                }
            });

            e.addListener(Modonomicon.loc("research_data_manager"), ResearchDataManager.get());
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
                ResearchDataManager.get().onDatapackSync(e.getPlayer());
                ResearchStateManager.get().onDatapackSync(e.getPlayer());
            }
        });

        //sync book state on player join
        NeoForge.EVENT_BUS.addListener((EntityJoinLevelEvent e) -> {
            if (e.getEntity() instanceof ServerPlayer player) {
                BookVisualStateManager.get().syncFor(player);
                ResearchStateManager.get().onDatapackSync(player);
                // Replay advancement-backed hooks if research state is stale (e.g. reset while offline).
                if (ResearchServices.hooks().needsAdvancementReplay(player)) {
                    ResearchServices.hooks().replayAdvancements(player);
                    ResearchStateManager.get().syncFor(player);
                    BookVisualStateManager.get().syncFor(player);
                }
            }
        });

        //on overworld unload clear the save data reference in the state manager
        // this ensures that if another world is loaded the save data is taken from file
        // instead of bleeding in from the previous level
        NeoForge.EVENT_BUS.addListener((LevelEvent.Unload e) -> {
            if (e.getLevel() instanceof Level level && level.dimension() == Level.OVERWORLD) {
                BookVisualStateManager.get().saveData = null;
                ResearchStateManager.get().clearCachedSaveData();
            }
        });


        //Advancement event handling for condition/unlock system
        NeoForge.EVENT_BUS.addListener((AdvancementEvent.AdvancementEarnEvent e) -> {
            var player = (ServerPlayer) e.getEntity();
            if (ResearchServices.hooks().onAdvancement(player, e.getAdvancement().id())) {
                ResearchStateManager.get().syncFor(player);
                BookVisualStateManager.get().syncFor(player);
            }
        });

        //Item crafted event handling for research progression
        NeoForge.EVENT_BUS.addListener((PlayerEvent.ItemCraftedEvent e) -> {
            if(!(e.getEntity() instanceof ServerPlayer player))
                return;

            var crafting = e.getCrafting();
            if (!crafting.isEmpty()) {
                if (ResearchServices.hooks().onItemCrafted(player, crafting)) {
                    ResearchStateManager.get().syncFor(player);
                    BookVisualStateManager.get().syncFor(player);
                }
            }
        });

        //We use server tick to flush the queue of players that need a book state sync
        NeoForge.EVENT_BUS.addListener(((ServerTickEvent.Post e) -> {
            ResearchStateManager.get().onServerTickEnd(e.getServer());
        }));

        //Datagen
        modEventBus.addListener(DataGenerators::gatherData);

        //Client stuff
        if (FMLEnvironment.getDist() == Dist.CLIENT) {
            modEventBus.addListener(Client::onClientSetup);
            modEventBus.addListener(Client::onRegisterGuiOverlays);
            modEventBus.addListener(Client::onModifyBakingResult);
            modEventBus.addListener(Client::onRegisterPipRenderers);

            //register client side reload listener that will reset the fallback font to handle locale changes on the fly
            modEventBus.addListener((AddClientReloadListenersEvent e) -> {
                e.addListener(Modonomicon.loc("book_data_manager_client"), BookDataManager.Client.get());
            });

            Client.registerConfigScreen(modContainer);
        }
    }

    public void onCommonSetup(FMLCommonSetupEvent event) {
        RegistryBootstrap.bootstrap();
        DemoRuntimeBookContent.register();

        NeoForge.EVENT_BUS.addListener((PlayerInteractEvent.RightClickBlock e) -> {
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

            NeoForge.EVENT_BUS.addListener((ClientTickEvent.Post e) -> {
                ClientTicks.endClientTick(Minecraft.getInstance());
                MultiblockPreviewRenderer.onClientTick(Minecraft.getInstance());
            });
            NeoForge.EVENT_BUS.addListener((RenderFrameEvent.Pre e) -> {
                ClientTicks.renderTickStart(e.getPartialTick().getGameTimeDeltaPartialTick(true));
            });
            NeoForge.EVENT_BUS.addListener((RenderFrameEvent.Post e) -> {
                ClientTicks.renderTickEnd();
            });

            //let multiblock preview renderer handle right clicks for anchoring
            NeoForge.EVENT_BUS.addListener((PlayerInteractEvent.RightClickBlock e) -> {
                InteractionResult result = MultiblockPreviewRenderer.onPlayerInteract(e.getEntity(), e.getLevel(), e.getHand(), e.getHitVec());
                if (result.consumesAction()) {
                    e.setCanceled(true);
                    e.setCancellationResult(result);
                }
            });

            //Render multiblock preview - Phase 1: Extract render state
            NeoForge.EVENT_BUS.addListener((ExtractLevelRenderStateEvent e) -> {
                MultiblockPreviewRenderer.extractRenderState(e.getRenderState());
            });

            // Submit before vanilla executes its feature frame. Rendering a second frame during a render stage is invalid.
            NeoForge.EVENT_BUS.addListener((SubmitCustomGeometryEvent e) -> {
                MultiblockPreviewRenderer.submitRenderFeatures(e.getLevelRenderState(), e.getSubmitNodeCollector());
            });

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

        public static void registerConfigScreen(ModContainer modContainer) {
            modContainer.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
        }

        public static void onRegisterGuiOverlays(RegisterGuiLayersEvent event) {
            event.registerBelow(VanillaGuiLayers.BOSS_OVERLAY, Modonomicon.loc("multiblock_hud"), (guiGraphics, partialTicks) -> MultiblockPreviewRenderer.onRenderHUD(guiGraphics, partialTicks.getGameTimeDeltaPartialTick(true)));
        }

        public static void onModifyBakingResult(ModelEvent.ModifyBakingResult event) {
            BookModel.replace(event.getBakingResult().itemStackModels());
        }

        public static void onRegisterPipRenderers(RegisterPictureInPictureRenderersEvent event) {
            event.register(
                    GuiMultiblockRenderState.class,
                    GuiMultiblockRenderer::new
            );
            event.register(
                    GuiDirectEntryConnectionRenderState.class,
                    GuiDirectEntryConnectionRenderer::new
            );
        }
    }
}
