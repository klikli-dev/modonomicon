/*
 * LGPL-3.0
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

package com.klikli_dev.modonomicon.handlers;

import com.klikli_dev.modonomicon.Modonomicon;
import com.klikli_dev.modonomicon.client.ClientTicks;
import net.minecraft.client.Minecraft;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

public class ClientSetupEventHandler {

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        registerItemModelProperties(event);

        //Keep track of ticks. We do this to stay loader agnostic so we can do a fabric version in the future
        MinecraftForge.EVENT_BUS.addListener((TickEvent.ClientTickEvent e) -> {
            if (e.phase == TickEvent.Phase.END) {
                ClientTicks.endClientTick(Minecraft.getInstance());
            }
        });

        //Not safe to call during parallel load, so register to run threadsafe.
        event.enqueueWork(() -> {
            //Register screen factories
            Modonomicon.LOGGER.debug("Registered Screen Containers");
        });

        Modonomicon.LOGGER.info("Client setup complete.");
    }

    public static void registerItemModelProperties(FMLClientSetupEvent event) {

        //Not safe to call during parallel load, so register to run threadsafe
        event.enqueueWork(() -> {
            //Register item model properties

            Modonomicon.LOGGER.debug("Registered Item Properties");
        });
    }
}
