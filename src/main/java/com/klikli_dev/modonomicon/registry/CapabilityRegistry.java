/*
 *
 *  * SPDX-FileCopyrightText: 2022 klikli-dev
 *  *
 *  * SPDX-License-Identifier: MIT
 *
 */

package com.klikli_dev.modonomicon.registry;

import com.klikli_dev.modonomicon.Modonomicon;
import com.klikli_dev.modonomicon.capability.BookUnlockCapability;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;

public class CapabilityRegistry {

    public static final ResourceLocation BOOK_UNLOCK_ID = Modonomicon.loc("book_unlock");

    public static Capability<BookUnlockCapability> BOOK_UNLOCK = CapabilityManager.get(new CapabilityToken<>() {
    });


    public static void onRegisterCapabilities(final RegisterCapabilitiesEvent event) {
        event.register(BookUnlockCapability.class);
    }

    public static void onPlayerClone(final PlayerEvent.Clone event) {
        //only handle respawn after death -> not portal transfers
        if (event.isWasDeath()) {
            //copy capability to new player instance
            event.getPlayer().getCapability(BOOK_UNLOCK).ifPresent(newCap -> {
                        event.getOriginal().getCapability(BOOK_UNLOCK).ifPresent(newCap::clone);
                    }
            );
        }
    }

    public static void onAttachCapabilities(final AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof Player) {
            if (!event.getObject().getCapability(BOOK_UNLOCK).isPresent()) {
                event.addCapability(BOOK_UNLOCK_ID, new BookUnlockCapability.Dispatcher());
            }
        }
    }

    public static void onJoinWorld(final EntityJoinWorldEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            player.getCapability(BOOK_UNLOCK).ifPresent(capability -> {
                        capability.update(player);
                        capability.sync(player);
                    }
            );
        }
    }
}
