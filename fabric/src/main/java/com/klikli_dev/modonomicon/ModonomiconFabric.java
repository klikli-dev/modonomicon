/*
 * SPDX-FileCopyrightText: 2023 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon;

import com.klikli_dev.modonomicon.book.runtime.DemoRuntimeBookContent;
import com.klikli_dev.modonomicon.bookstate.BookUnlockStateManager;
import com.klikli_dev.modonomicon.bookstate.BookVisualStateManager;
import com.klikli_dev.modonomicon.config.ServerConfig;
import com.klikli_dev.modonomicon.data.BookDataManager;
import com.klikli_dev.modonomicon.data.MultiblockDataManager;
import com.klikli_dev.modonomicon.registry.RegistryBootstrap;
import com.klikli_dev.modonomicon.integration.LecternIntegration;
import com.klikli_dev.modonomicon.network.Networking;
import com.klikli_dev.modonomicon.registry.CommandRegistry;
import com.klikli_dev.modonomicon.registry.CreativeModeTabRegistry;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLevelEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.resource.v1.ResourceLoader;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.packs.PackType;
import net.minecraft.world.level.Level;

public class ModonomiconFabric implements ModInitializer {

    @Override
    public void onInitialize() {

        // This method is invoked by the Fabric mod loader when it is ready
        // to load your mod. You can access Fabric and Common code in this
        // project.

        // Use Fabric to bootstrap the Common mod.
        Modonomicon.init();

        ServerConfig.init();

        //Most registries are handled by common, but creative tabs are easier per loader
        Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, CreativeModeTabRegistry.MODONOMICON_TAB_KEY, CreativeModeTabRegistry.MODONOMICON);
        CreativeModeTabEvents.MODIFY_OUTPUT_ALL.register(CreativeModeTabRegistry::onModifyOutput);

        //Equivalent to Common setup
        Networking.registerMessages();
        Networking.registerReceivers();

        RegistryBootstrap.bootstrap();
        DemoRuntimeBookContent.register();

        //register data managers as reload listeners
        ResourceLoader.get(PackType.SERVER_DATA).registerReloadListener(Modonomicon.loc("book_data_manager"), (sharedState, exectutor, barrier, applyExectutor) -> {
            BookDataManager.get().registries(sharedState.get(ResourceLoader.REGISTRY_LOOKUP_KEY));
            return BookDataManager.get().reload(sharedState, exectutor, barrier, applyExectutor);
        });
        ResourceLoader.get(PackType.SERVER_DATA).registerReloadListener(Modonomicon.loc("multiblock_data_manager"), (sharedState, exectutor, barrier, applyExectutor) -> {
            MultiblockDataManager.get().registries(sharedState.get(ResourceLoader.REGISTRY_LOOKUP_KEY));
            return MultiblockDataManager.get().reload(sharedState, exectutor, barrier, applyExectutor);
        });

        //register commands
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) ->
                CommandRegistry.registerCommands(dispatcher)
        );


        //datapack sync = build books and sync to client
        ServerLifecycleEvents.SYNC_DATA_PACK_CONTENTS.register((player, joined) -> {
            BookDataManager.get().onDatapackSync(player);
            MultiblockDataManager.get().onDatapackSync(player);
        });

        //sync book state on player join
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            BookUnlockStateManager.get().updateAndSyncFor(handler.getPlayer());
            BookVisualStateManager.get().syncFor(handler.getPlayer());
        });

        //on overworld unload clear the save data reference in the state manager
        // this ensures that if another world is loaded the save data is taken from file
        // instead of bleeding in from the previous level
        ServerLevelEvents.UNLOAD.register((server, level) -> {
            if (level.dimension() == Level.OVERWORLD) {
                BookUnlockStateManager.get().saveData = null;
                BookVisualStateManager.get().saveData = null;
            }
        });

        UseBlockCallback.EVENT.register(LecternIntegration::rightClick);

        //We use server tick to flush the queue of players that need a book state sync
        ServerTickEvents.END_SERVER_TICK.register((server) -> {
            BookUnlockStateManager.get().onServerTickEnd(server);
        });

        //Advancement event handling for condition/unlock system
        //done in MixinPlayerAdvancements, because we have no event in Fabric

        //TODO: Fabric: packet split
    }
}
