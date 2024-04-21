/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.network;

import com.klikli_dev.modonomicon.Modonomicon;
import com.klikli_dev.modonomicon.networking.*;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlerEvent;
import net.neoforged.neoforge.network.registration.IPayloadRegistrar;

public class Networking {

    public static void register(final RegisterPayloadHandlerEvent event) {
        final IPayloadRegistrar registrar = event.registrar(Modonomicon.MOD_ID);

        registrar.play(BookEntryReadMessage.ID, (b) -> new NeoMessageWrapper(b, BookEntryReadMessage::new), MessageHandler::handle);
        registrar.play(ClickCommandLinkMessage.ID, (b) -> new NeoMessageWrapper(b, ClickCommandLinkMessage::new), MessageHandler::handle);
        registrar.play(ClickReadAllButtonMessage.ID, (b) -> new NeoMessageWrapper(b, ClickReadAllButtonMessage::new), MessageHandler::handle);
        registrar.play(SaveBookStateMessage.ID, (b) -> new NeoMessageWrapper(b, SaveBookStateMessage::new), MessageHandler::handle);
        registrar.play(SaveCategoryStateMessage.ID, (b) -> new NeoMessageWrapper(b, SaveCategoryStateMessage::new), MessageHandler::handle);
        registrar.play(SaveEntryStateMessage.ID, (b) -> new NeoMessageWrapper(b, SaveEntryStateMessage::new), MessageHandler::handle);
        registrar.play(SendUnlockCodeToClientMessage.ID, (b) -> new NeoMessageWrapper(b, SendUnlockCodeToClientMessage::new), MessageHandler::handle);
        registrar.play(SendAdvancementToClientMessage.ID, (b) -> new NeoMessageWrapper(b, SendAdvancementToClientMessage::new), MessageHandler::handle);
        registrar.play(SyncBookDataMessage.ID, (b) -> new NeoMessageWrapper(b, SyncBookDataMessage::new), MessageHandler::handle);
        registrar.play(SyncBookUnlockStatesMessage.ID, (b) -> new NeoMessageWrapper(b, SyncBookUnlockStatesMessage::new), MessageHandler::handle);
        registrar.play(SyncBookVisualStatesMessage.ID, (b) -> new NeoMessageWrapper(b, SyncBookVisualStatesMessage::new), MessageHandler::handle);
        registrar.play(SyncMultiblockDataMessage.ID, (b) -> new NeoMessageWrapper(b, SyncMultiblockDataMessage::new), MessageHandler::handle);
        registrar.play(ReloadResourcesOnClientMessage.ID, (b) -> new NeoMessageWrapper(b, ReloadResourcesOnClientMessage::new), MessageHandler::handle);
        registrar.play(ReloadResourcesDoneMessage.ID, (b) -> new NeoMessageWrapper(b, ReloadResourcesDoneMessage::new), MessageHandler::handle);
        registrar.play(RequestSyncBookStatesMessage.ID, (b) -> new NeoMessageWrapper(b, ReloadResourcesDoneMessage::new), MessageHandler::handle);
        registrar.play(RequestAdvancementMessage.ID, (b) -> new NeoMessageWrapper(b, RequestAdvancementMessage::new), MessageHandler::handle);
    }

    public static <T extends Message> void sendToSplit(ServerPlayer player, T message) {
        PacketDistributor.PLAYER.with(player).send(new NeoMessageWrapper(message));
    }

    public static <T extends Message> void sendTo(ServerPlayer player, T message) {
        PacketDistributor.PLAYER.with(player).send(new NeoMessageWrapper(message));
    }

    public static <T extends Message> void sendToServer(T message) {
        PacketDistributor.SERVER.noArg().send(new NeoMessageWrapper(message));
    }
}
