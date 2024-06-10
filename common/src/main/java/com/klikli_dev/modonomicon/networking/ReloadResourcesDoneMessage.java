/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.networking;

import com.google.common.collect.Lists;
import com.klikli_dev.modonomicon.Modonomicon;
import com.klikli_dev.modonomicon.bookstate.BookUnlockStateManager;
import net.minecraft.ChatFormatting;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.world.level.storage.WorldData;

import java.util.Collection;

import static com.klikli_dev.modonomicon.api.ModonomiconConstants.I18n.Command.RELOAD_SUCCESS;

public class ReloadResourcesDoneMessage implements Message {

    public static final ReloadResourcesDoneMessage INSTANCE = new ReloadResourcesDoneMessage();

    public static final Type<ReloadResourcesDoneMessage> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(Modonomicon.MOD_ID, "reload_resources_done"));

    public static final StreamCodec<RegistryFriendlyByteBuf, ReloadResourcesDoneMessage> STREAM_CODEC = StreamCodec.unit(INSTANCE);

    private ReloadResourcesDoneMessage() {
    }

    private static Collection<String> discoverNewPacks(PackRepository pPackRepository, WorldData pWorldData, Collection<String> pSelectedIds) {
        pPackRepository.reload();
        Collection<String> collection = Lists.newArrayList(pSelectedIds);
        Collection<String> collection1 = pWorldData.getDataConfiguration().dataPacks().getDisabled();

        for (String s : pPackRepository.getAvailableIds()) {
            if (!collection1.contains(s) && !collection.contains(s)) {
                collection.add(s);
            }
        }

        return collection;
    }

    public static void reloadPacks(Collection<String> pSelectedIds, ServerPlayer player, MinecraftServer server) {
        server.reloadResources(pSelectedIds).exceptionally((error) -> {
            Modonomicon.LOG.warn("Failed to execute reload", error);
            player.sendSystemMessage(Component.translatable("commands.reload.failure").withStyle(ChatFormatting.RED));
            return null;
        }).thenRun(() -> {
            BookUnlockStateManager.get().updateAndSyncFor(player);
            player.sendSystemMessage(Component.translatable(RELOAD_SUCCESS).withStyle(ChatFormatting.GREEN));
        });
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    @Override
    public void onServerReceived(MinecraftServer minecraftServer, ServerPlayer player) {

        if (!player.hasPermissions(2)) //same leve las the reload command
            return;

        //below is copied from ReloadCommand, modified not to need a command source and to only post messages after reload is complete
        PackRepository packrepository = minecraftServer.getPackRepository();
        WorldData worlddata = minecraftServer.getWorldData();
        Collection<String> selectedIds = packrepository.getSelectedIds();
        Collection<String> packs = discoverNewPacks(packrepository, worlddata, selectedIds);
        reloadPacks(packs, player, minecraftServer);
    }
}
