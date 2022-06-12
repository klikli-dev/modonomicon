/*
 *
 *  * SPDX-FileCopyrightText: 2022 klikli-dev
 *  *
 *  * SPDX-License-Identifier: MIT
 *
 */

package com.klikli_dev.modonomicon.registry;

import com.klikli_dev.modonomicon.Modonomicon;
import com.klikli_dev.modonomicon.capability.BookDataCapability;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;

public class CapabilityRegistry {

    public static final ResourceLocation BOOK_DATA_ID = Modonomicon.loc("book_data");

    public static Capability<BookDataCapability> BOOK_DATA = CapabilityManager.get(new CapabilityToken<>() {
    });


    public static void onRegisterCapabilities(final RegisterCapabilitiesEvent event) {
        event.register(BookDataCapability.class);
    }

    public static void onPlayerClone(final PlayerEvent.Clone event) {
        //only handle respawn after death -> not portal transfers
        if (event.isWasDeath()) {
            //copy capability to new player instance
            event.getPlayer().getCapability(BOOK_DATA).ifPresent(newCap -> {
                        event.getOriginal().getCapability(BOOK_DATA).ifPresent(newCap::clone);
                    }
            );
        }
    }

    public static void onAttachCapabilities(final AttachCapabilitiesEvent<Entity> evt) {
        if (evt.getObject() instanceof Player) {
            if (!evt.getObject().getCapability(BOOK_DATA).isPresent()) {
                evt.addCapability(BOOK_DATA_ID, new BookDataCapability.Dispatcher());
            }
        }
    }

}
