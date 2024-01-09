/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.networking;

import com.google.common.collect.Lists;
import com.klikli_dev.modonomicon.Modonomicon;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.world.level.storage.WorldData;

import java.util.Collection;

import static com.klikli_dev.modonomicon.api.ModonomiconConstants.I18n.Command.RELOAD_SUCCESS;

public class ReloadResourcesDoneMessage implements Message {

    public static final ResourceLocation ID = new ResourceLocation(Modonomicon.MOD_ID, "reload_resources_done");

    public ReloadResourcesDoneMessage() {
    }

    public ReloadResourcesDoneMessage(FriendlyByteBuf buf) {
        this.decode(buf);
    }

    @Override
    public void encode(FriendlyByteBuf buf) {
    }

    @Override
    public void decode(FriendlyByteBuf buf) {
    }

    @Override
    public ResourceLocation getId() {
        return ID;
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

    private static Collection<String> discoverNewPacks(PackRepository pPackRepository, WorldData pWorldData, Collection<String> pSelectedIds) {
        pPackRepository.reload();
        Collection<String> collection = Lists.newArrayList(pSelectedIds);
        Collection<String> collection1 = pWorldData.getDataConfiguration().dataPacks().getDisabled();

        for(String s : pPackRepository.getAvailableIds()) {
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
            player.sendSystemMessage(Component.translatable(RELOAD_SUCCESS).withStyle(ChatFormatting.GREEN));
        });
    }
}
