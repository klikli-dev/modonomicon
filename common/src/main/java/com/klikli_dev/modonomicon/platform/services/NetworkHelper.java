package com.klikli_dev.modonomicon.platform.services;

import net.minecraft.server.level.ServerPlayer;

public interface NetworkHelper {
    <T> void sendTo(ServerPlayer player, T message);

    <T> void sendToSplit(ServerPlayer player, T message);


    <T> void sendToServer(T message);
}