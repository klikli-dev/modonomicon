package com.klikli_dev.modonomicon.mixin;

import com.klikli_dev.modonomicon.data.BookDataManager;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.protocol.game.ClientboundUpdateRecipesPacket;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;

@Mixin(ClientPacketListener.class)
public abstract class MixinClientPacketListener {
    @Inject(at = @At("TAIL"), method = "handleUpdateRecipes(Lnet/minecraft/network/protocol/game/ClientboundUpdateRecipesPacket;)V")
    private void handleUpdateRecipes(ClientboundUpdateRecipesPacket pPacket) {
        BookDataManager.get().onRecipesUpdated();
    }
}