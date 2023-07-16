package com.klikli_dev.modonomicon;

import net.fabricmc.api.ModInitializer;

public class ModonomiconFabric implements ModInitializer {
    
    @Override
    public void onInitialize() {
        
        // This method is invoked by the Fabric mod loader when it is ready
        // to load your mod. You can access Fabric and Common code in this
        // project.

        // Use Fabric to bootstrap the Common mod.
        Modonomicon.LOG.info("Hello Fabric world!");
        Modonomicon.init();

        //TODO: Fabric: Listen for advancement events and call it on BookUnlockStateManager

        //TODO: Fabric: Register commands
        //TODO: Fabric: Register Creative Tabs + Contents
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
    }
}
