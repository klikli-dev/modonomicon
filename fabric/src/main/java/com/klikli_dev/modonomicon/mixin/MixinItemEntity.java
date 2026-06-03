/*
 * SPDX-FileCopyrightText: 2026 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.mixin;

import com.klikli_dev.modonomicon.bookstate.BookVisualStateManager;
import com.klikli_dev.modonomicon.research.ResearchServices;
import com.klikli_dev.modonomicon.research.state.ResearchStateManager;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemEntity.class)
public abstract class MixinItemEntity {

    @Inject(
        at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Inventory;add(Lnet/minecraft/world/item/ItemStack;)Z", shift = At.Shift.AFTER),
        method = "playerTouch"
    )
    private void modonomicon$onItemAcquired(Player player, CallbackInfo ci, @Local(name = "orgCount") int orgCount) {
        if (player instanceof ServerPlayer serverPlayer) {
            var self = (ItemEntity) (Object) this;
            var originalStackFromEntity = self.getItem();
            var originalStackFromEntityCount = originalStackFromEntity.getCount();
            //this is necessary to allow copy/copywithcount to work.
            //if originalStackFromEntity comes in with count 0, then copy returns ItemStack.EMPTY, so even setting the count never restores the proper item.
            originalStackFromEntity.setCount(1);
            var itemStack = self.getItem().copyWithCount(orgCount);
            originalStackFromEntity.setCount(originalStackFromEntityCount);

            if (ResearchServices.hooks().onItemAcquired(serverPlayer, itemStack)) {
                ResearchStateManager.get().syncFor(serverPlayer);
                BookVisualStateManager.get().syncFor(serverPlayer);
            }
        }
    }
}
