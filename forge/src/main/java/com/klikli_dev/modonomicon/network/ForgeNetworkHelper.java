package com.klikli_dev.modonomicon.network;

import com.klikli_dev.modonomicon.platform.services.NetworkHelper;
import net.minecraft.server.level.ServerPlayer;

public class ForgeNetworkHelper implements NetworkHelper {
    @Override
    public <T> void sendTo(ServerPlayer player, T message) {
        Services.NETWORK.sendTo(player, message);
    }

    @Override
    public <T> void sendToSplit(ServerPlayer player, T message) {
        Services.NETWORK.sendToSplit(player, message);
    }

    @Override
    public <T> void sendToServer(T message) {
        Services.NETWORK.sendToServer(message);
    }
}
