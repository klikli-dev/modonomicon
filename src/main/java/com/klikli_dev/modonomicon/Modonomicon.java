/*
 * MIT License
 *
 * Copyright 2021 klikli-dev
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
 * of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following
 * conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial
 * portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 * PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT
 * OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 * OTHER DEALINGS IN THE SOFTWARE.
 */

package com.klikli_dev.modonomicon;

import com.klikli_dev.modonomicon.api.ModonomiconAPI;
import com.klikli_dev.modonomicon.config.ClientConfig;
import com.klikli_dev.modonomicon.config.CommonConfig;
import com.klikli_dev.modonomicon.config.ServerConfig;
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
        MinecraftForge.EVENT_BUS.addListener(this::onAddReloadListener);
        MinecraftForge.EVENT_BUS.addListener(this::onModConfigEvent);

        //register event listener objects

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

    public void onModConfigEvent(final ModConfigEvent event) {
        if (event.getConfig().getSpec() == ClientConfig.get().spec) {
            //Clear the config cache on reload.
            ClientConfig.get().clear();
        }
        if (event.getConfig().getSpec() == CommonConfig.get().spec) {
            //Clear the config cache on reload.
            CommonConfig.get().clear();
        }
        if (event.getConfig().getSpec() == ServerConfig.get().spec) {
            //Clear the config cache on reload.
            ServerConfig.get().clear();
        }
    }

    public void onAddReloadListener(AddReloadListenerEvent event) {

    }

    public void onCommonSetup(FMLCommonSetupEvent event) {
        Networking.registerMessages();
        LOGGER.info("Common setup complete.");
    }

    public void onServerSetup(FMLDedicatedServerSetupEvent event) {
        LOGGER.info("Dedicated server setup complete.");
    }
}
