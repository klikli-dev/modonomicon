/*
 * SPDX-FileCopyrightText: 2023 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon;

import com.klikli_dev.modonomicon.bookstate.BookUnlockStateManager;
import com.klikli_dev.modonomicon.bookstate.BookVisualStateManager;
import com.klikli_dev.modonomicon.config.ClientConfig;
import com.klikli_dev.modonomicon.data.BookDataManager;
import com.klikli_dev.modonomicon.data.LoaderRegistry;
import com.klikli_dev.modonomicon.data.MultiblockDataManager;
import com.klikli_dev.modonomicon.data.ReloadListenerWrapper;
import com.klikli_dev.modonomicon.network.Networking;
import com.klikli_dev.modonomicon.registry.CommandRegistry;
import com.klikli_dev.modonomicon.registry.CreativeModeTabRegistry;
import com.klikli_dev.modonomicon.registry.FabricClientCommandRegistry;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
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

        //Most registries are handled by common, but creative tabs are easier per loader
        Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, CreativeModeTabRegistry.MODONOMICON_TAB_KEY, CreativeModeTabRegistry.MODONOMICON);
        ItemGroupEvents.MODIFY_ENTRIES_ALL.register(CreativeModeTabRegistry::onModifyEntries);

        //Equivalent to Common setup
        Networking.registerMessages();
        Networking.registerReceivers();

        LoaderRegistry.registerLoaders();

        //register data managers as reload listeners
        ResourceManagerHelper.get(PackType.SERVER_DATA).registerReloadListener(new ReloadListenerWrapper(
                Modonomicon.loc("book_data_manager"),
                BookDataManager.get()
        ));
        ResourceManagerHelper.get(PackType.SERVER_DATA).registerReloadListener(new ReloadListenerWrapper(
                Modonomicon.loc("multiblock_data_manager"),
                MultiblockDataManager.get()
        ));

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
        ServerWorldEvents.UNLOAD.register((server, level) -> {
            if(level.dimension() == Level.OVERWORLD) {
                BookUnlockStateManager.get().saveData = null;
                BookVisualStateManager.get().saveData = null;
            }
        });

        //Advancement event handling for condition/unlock system
        //done in MixinPlayerAdvancements, because we have no event in Fabric

        //TODO: Fabric: packet split
    }
}
