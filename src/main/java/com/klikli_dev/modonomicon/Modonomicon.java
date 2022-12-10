/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon;

import com.klikli_dev.modonomicon.api.ModonomiconAPI;
import com.klikli_dev.modonomicon.capability.BookUnlockCapability;
import com.klikli_dev.modonomicon.client.ClientSetupEventHandler;
import com.klikli_dev.modonomicon.config.ClientConfig;
import com.klikli_dev.modonomicon.config.CommonConfig;
import com.klikli_dev.modonomicon.config.ServerConfig;
import com.klikli_dev.modonomicon.data.BookDataManager;
import com.klikli_dev.modonomicon.data.LoaderRegistry;
import com.klikli_dev.modonomicon.data.MultiblockDataManager;
import com.klikli_dev.modonomicon.datagen.DataGenerators;
import com.klikli_dev.modonomicon.item.ModonomiconItem;
import com.klikli_dev.modonomicon.network.Networking;
import com.klikli_dev.modonomicon.registry.*;
import com.mojang.logging.LogUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLDedicatedServerSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;
import org.slf4j.Logger;

@Mod(Modonomicon.MODID)
public class Modonomicon {
    public static final String MODID = ModonomiconAPI.ID;
    public static final Logger LOGGER = LogUtils.getLogger();

    public static Modonomicon INSTANCE;

    public Modonomicon() {
        INSTANCE = this;

        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, ServerConfig.get().spec);
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, CommonConfig.get().spec);
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, ClientConfig.get().spec);

        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        ItemRegistry.ITEMS.register(modEventBus);
        MenuRegistry.MENUS.register(modEventBus);
        SoundRegistry.SOUNDS.register(modEventBus);


        //directly register event handlers
        modEventBus.addListener(this::onCommonSetup);
        modEventBus.addListener(this::onServerSetup);
        modEventBus.addListener(ModonomiconItem::onCreativeModeTabBuildContents);
        modEventBus.addListener(ItemRegistry::onRegisterCreativeModeTabs);

        MinecraftForge.EVENT_BUS.addListener(this::onAddReloadListener);
        MinecraftForge.EVENT_BUS.addListener(CommandRegistry::registerCommands);
        MinecraftForge.EVENT_BUS.addListener(CommandRegistry::registerClientCommands);
        MinecraftForge.EVENT_BUS.addListener(BookDataManager.get()::onDatapackSync);
        MinecraftForge.EVENT_BUS.addListener(MultiblockDataManager.get()::onDatapackSync);

        //register event handlers for our capability & cap sync
        modEventBus.addListener(CapabilityRegistry::onRegisterCapabilities);
        MinecraftForge.EVENT_BUS.addListener(CapabilityRegistry::onPlayerClone);
        MinecraftForge.EVENT_BUS.addGenericListener(Entity.class, CapabilityRegistry::onAttachCapabilities);
        MinecraftForge.EVENT_BUS.addListener(CapabilityRegistry::onJoinWorld);

        //event handler for condition system
        MinecraftForge.EVENT_BUS.addListener(BookUnlockCapability::onAdvancement);


        modEventBus.addListener(DataGenerators::gatherData);

        if (FMLEnvironment.dist == Dist.CLIENT) {
            modEventBus.addListener(ClientSetupEventHandler::onClientSetup);
            modEventBus.addListener(ClientSetupEventHandler::onRegisterGeometryLoaders);
            modEventBus.addListener(ClientSetupEventHandler::onRegisterGuiOverlays);
            MinecraftForge.EVENT_BUS.addListener(BookDataManager.get()::onRecipesUpdated);
        }
    }

    public static ResourceLocation loc(String path) {
        return new ResourceLocation(MODID, path);
    }

    public void onAddReloadListener(AddReloadListenerEvent event) {
        event.addListener(BookDataManager.get());
        event.addListener(MultiblockDataManager.get());
    }


    public void onCommonSetup(FMLCommonSetupEvent event) {
        Networking.registerMessages();

        LoaderRegistry.registerLoaders();

        LOGGER.info("Common setup complete.");
    }

    public void onServerSetup(FMLDedicatedServerSetupEvent event) {
        LOGGER.info("Dedicated server setup complete.");
    }
}
