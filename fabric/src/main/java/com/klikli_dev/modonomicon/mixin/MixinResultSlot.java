/*
 * SPDX-FileCopyrightText: 2026 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.mixin;

import com.klikli_dev.modonomicon.bookstate.BookVisualStateManager;
import com.klikli_dev.modonomicon.research.ResearchServices;
import com.klikli_dev.modonomicon.research.state.ResearchStateManager;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ResultSlot;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ResultSlot.class)
public abstract class MixinResultSlot {

    @Accessor("player")
    abstract Player modonomicon$getPlayer();

    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;onCraftedBy(Lnet/minecraft/world/entity/player/Player;I)V", shift = At.Shift.AFTER), method = "checkTakeAchievements")
    private void modonomicon$onCraftItem(ItemStack carried, CallbackInfo ci) {
        if (carried.isEmpty()) return;
        var player = this.modonomicon$getPlayer();
        if (player instanceof ServerPlayer serverPlayer) {
            var itemId = carried.getItem().builtInRegistryHolder().unwrapKey().map(net.minecraft.resources.ResourceKey::identifier).orElse(null);
            if (itemId != null) {
                if (ResearchServices.hooks().onItemCrafted(serverPlayer, itemId)) {
                    ResearchStateManager.get().syncFor(serverPlayer);
                    BookVisualStateManager.get().syncFor(serverPlayer);
                }
            }
        }
    }
}
