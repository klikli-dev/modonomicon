package com.klikli_dev.modonomicon;

import net.minecraftforge.fml.common.Mod;

@Mod(CommonConstants.MOD_ID)
public class ModonomiconForge {
    
    public ModonomiconForge() {
    
        // This method is invoked by the Forge mod loader when it is ready
        // to load your mod. You can access Forge and Common code in this
        // project.
    
        // Use Forge to bootstrap the Common mod.
        CommonConstants.LOG.info("Hello Forge world!");
        Modonomicon.init();
        
    }
}