/*
 * LGPL-3-0
 *
 * Copyright (C) 2021 klikli-dev
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

package com.klikli_dev.modonomicon;

import com.klikli_dev.modonomicon.api.ModonomiconAPI;
import com.klikli_dev.modonomicon.config.ClientConfig;
import com.klikli_dev.modonomicon.config.CommonConfig;
import com.klikli_dev.modonomicon.config.ServerConfig;
import com.klikli_dev.modonomicon.data.BookDataManager;
import com.klikli_dev.modonomicon.datagen.DataGenerators;
import com.klikli_dev.modonomicon.handlers.ClientSetupEventHandler;
import com.klikli_dev.modonomicon.handlers.RegistryEventHandler;
import com.klikli_dev.modonomicon.item.ModonomiconCreativeModeTab;
import com.klikli_dev.modonomicon.network.Networking;
import com.klikli_dev.modonomicon.registry.ItemRegistry;
import com.klikli_dev.modonomicon.registry.MenuRegistry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterClientReloadListenersEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLDedicatedServerSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(Modonomicon.MODID)
public class Modonomicon {
    public static final String MODID = ModonomiconAPI.ID;
    public static final Logger LOGGER = LogManager.getLogger(MODID);

    public static final CreativeModeTab CREATIVE_MODE_TAB = new ModonomiconCreativeModeTab();

    public static Modonomicon INSTANCE;

    public Modonomicon() {
        INSTANCE = this;

        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, ServerConfig.get().spec);
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, CommonConfig.get().spec);
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, ClientConfig.get().spec);

        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        ItemRegistry.ITEMS.register(modEventBus);
        MenuRegistry.MENUS.register(modEventBus);

        //directly register event handlers, can't register the object and use annotations, because we have events from both buses
        modEventBus.addListener(this::onCommonSetup);
        modEventBus.addListener(this::onServerSetup);
        modEventBus.addListener(this::onRegisterClientReloadListeners);
        MinecraftForge.EVENT_BUS.addListener(this::onAddReloadListener);

        //register event listener objects
        MinecraftForge.EVENT_BUS.register(BookDataManager.get());

        //register event listener classes
        modEventBus.register(RegistryEventHandler.class);
        modEventBus.register(DataGenerators.class);

        if (FMLEnvironment.dist == Dist.CLIENT) {
            modEventBus.register(ClientSetupEventHandler.class);
        }
    }

    public static ResourceLocation loc(String path) {
        return new ResourceLocation(MODID, path);
    }

    public void onAddReloadListener(AddReloadListenerEvent event) {
        event.addListener(BookDataManager.get());
    }

    public void onRegisterClientReloadListeners(RegisterClientReloadListenersEvent event) {

    }

    //RegisterClientReloadListenersEvent

    public void onCommonSetup(FMLCommonSetupEvent event) {
        Networking.registerMessages();
        LOGGER.info("Common setup complete.");
    }

    public void onServerSetup(FMLDedicatedServerSetupEvent event) {
        LOGGER.info("Dedicated server setup complete.");
    }
}
