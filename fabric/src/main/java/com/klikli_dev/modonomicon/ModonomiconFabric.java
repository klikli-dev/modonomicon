package com.klikli_dev.modonomicon;

import com.klikli_dev.modonomicon.data.LoaderRegistry;
import com.klikli_dev.modonomicon.network.Networking;
import com.klikli_dev.modonomicon.registry.CommandRegistry;
import com.klikli_dev.modonomicon.registry.CreativeModeTabRegistry;
import com.klikli_dev.modonomicon.registry.FabricClientCommandRegistry;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;

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
        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabRegistry.MODONOMICON_TAB_KEY).register(CreativeModeTabRegistry::onModifyEntries);

        //Equivalent to Common setup
        Networking.registerMessages();
        LoaderRegistry.registerLoaders();

        //register data managers as reload listeners

        //register commands
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) ->
                CommandRegistry.registerCommands(dispatcher)
        );
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) ->
                FabricClientCommandRegistry.registerClientCommands(dispatcher)
        );


        //TODO: Fabric: Register Network Messages
        //TODO: Fabric: Datagen
        //TODO: Fabric: Config
        //TODO: Fabric: on join world (see Forge)
        //TODO: Fabric: on recipes packet -> can mixin? its about client receive of this
        //TODO: Fabric: OnDataPackSyncEvent -> maybe placeNewPlayer + reloadResources of PlayerList
        //TODO: Fabric: clientticks events
        //TODO: Fabric: Multiblock onRenderHUD
        //TODO: Fabric: Multiblock onRenderLevelLastEvent
        //TODO: Fabric: GuiOverlay
        //TODO: Fabric: Geometry loader (book model)
        //TODO: Fabric: packet split
        //TODO: Fabric: FluidHolder
        //TODO: Fabric: FluidRenderHelper/Tooltip
        //TODO: Fabric: Patchouli
        //TODO: Fabric: reload listeners
        //TODO: Fabric: advancement
    }
}
