package com.klikli_dev.modonomicon;

import com.klikli_dev.modonomicon.registry.CreativeModeTabRegistry;
import net.fabricmc.api.ModInitializer;
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

        Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, CreativeModeTabRegistry.MODONOMICON_TAB_KEY, CreativeModeTabRegistry.MODONOMICON);

        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabRegistry.MODONOMICON_TAB_KEY).register(CreativeModeTabRegistry::onModifyEntries);


        //TODO: Fabric: Register commands
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
