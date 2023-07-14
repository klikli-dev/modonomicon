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

        //TODO: Listen for advancement events and call it on BookUnlockStateManager
    }
}
