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
import com.klikli_dev.modonomicon.network.Networking;
import com.klikli_dev.modonomicon.network.messages.SyncBookDataCapabilityMessage;
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

    public static void onAttachCapabilities(final AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof Player) {
            if (!event.getObject().getCapability(BOOK_DATA).isPresent()) {
                event.addCapability(BOOK_DATA_ID, new BookDataCapability.Dispatcher());
            }
        }
    }

    public static void onJoinWorld(final EntityJoinWorldEvent event) {
        if (!event.getWorld().isClientSide && event.getEntity() instanceof Player player) {
            player.getCapability(BOOK_DATA).ifPresent(capability -> {
                        Networking.sendTo((ServerPlayer) player, new SyncBookDataCapabilityMessage(capability));
                    }
            );
        }
    }
}
