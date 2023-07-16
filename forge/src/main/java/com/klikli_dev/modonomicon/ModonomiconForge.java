package com.klikli_dev.modonomicon;

import com.klikli_dev.modonomicon.bookstate.BookUnlockStateManager;
import com.klikli_dev.modonomicon.data.LoaderRegistry;
import com.klikli_dev.modonomicon.network.Networking;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.AdvancementEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(Modonomicon.MOD_ID)
public class ModonomiconForge {

    public ModonomiconForge() {
        // This method is invoked by the Forge mod loader when it is ready
        // to load your mod. You can access Forge and Common code in this
        // project.

        // Use Forge to bootstrap the Common mod.
        Modonomicon.init();


        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        //directly register event handlers
        modEventBus.addListener(this::onCommonSetup);

        //event handler for condition system
        MinecraftForge.EVENT_BUS.addListener((AdvancementEvent.AdvancementEarnEvent e) -> BookUnlockStateManager.get().onAdvancement((ServerPlayer) e.getEntity()));

    }

    public void onCommonSetup(FMLCommonSetupEvent event) {
        Networking.registerMessages();

        LoaderRegistry.registerLoaders();

        Modonomicon.LOG.info("Common setup complete.");
    }
}