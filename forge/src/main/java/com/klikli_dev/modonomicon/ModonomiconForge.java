package com.klikli_dev.modonomicon;

import com.klikli_dev.modonomicon.bookstate.BookUnlockStateManager;
import com.klikli_dev.modonomicon.bookstate.BookVisualStateManager;
import com.klikli_dev.modonomicon.config.ClientConfig;
import com.klikli_dev.modonomicon.data.LoaderRegistry;
import com.klikli_dev.modonomicon.item.ModonomiconItem;
import com.klikli_dev.modonomicon.network.Networking;
import com.klikli_dev.modonomicon.registry.CommandRegistry;
import com.klikli_dev.modonomicon.registry.CreativeModeTabRegistry;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.client.event.RegisterClientCommandsEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.player.AdvancementEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
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

        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, ClientConfig.get().spec);

        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        //Most registries are handled by common, but creative tabs are easier per loader
        CreativeModeTabRegistry.CREATIVE_MODE_TABS.register(modEventBus);

        //directly register event handlers
        modEventBus.addListener(this::onCommonSetup);
        modEventBus.addListener(CreativeModeTabRegistry::onCreativeModeTabBuildContents);

        MinecraftForge.EVENT_BUS.addListener((RegisterCommandsEvent e) -> CommandRegistry.registerCommands(e.getDispatcher()));
        MinecraftForge.EVENT_BUS.addListener((RegisterClientCommandsEvent e) -> CommandRegistry.registerClientCommands(e.getDispatcher()));

        //register event handlers for book state sync
        MinecraftForge.EVENT_BUS.addListener(this::onJoinWorld);

        //event handler for condition system
        MinecraftForge.EVENT_BUS.addListener((AdvancementEvent.AdvancementEarnEvent e) -> BookUnlockStateManager.get().onAdvancement((ServerPlayer) e.getEntity()));
    }

    public void onCommonSetup(FMLCommonSetupEvent event) {
        Networking.registerMessages();

        LoaderRegistry.registerLoaders();
    }

    public void onJoinWorld(final EntityJoinLevelEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            BookUnlockStateManager.get().updateAndSyncFor(player);
            BookVisualStateManager.get().syncFor(player);
        }
    }
}