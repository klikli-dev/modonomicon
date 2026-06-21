/*
 * SPDX-FileCopyrightText: 2026 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.mixin;

import com.klikli_dev.modonomicon.bookstate.BookVisualStateManager;
import com.klikli_dev.modonomicon.research.ResearchServices;
import com.klikli_dev.modonomicon.research.state.ResearchStateManager;
import net.minecraft.core.NonNullList;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Supplier;

@Mixin(AbstractContainerMenu.class)
public abstract class MixinAbstractContainerMenu {

    @Shadow
    @Final
    private NonNullList<ItemStack> lastSlots;

    @Shadow
    @Final
    public NonNullList<Slot> slots;

    @Inject(
        method = "triggerSlotListeners",
        at = @At("HEAD")
    )
    private void modonomicon$onSlotChanged(int i, ItemStack current, Supplier<ItemStack> currentCopy, CallbackInfo ci) {
        ItemStack oldItem = this.lastSlots.get(i);
        if (ItemStack.matches(oldItem, current)) {
            return;
        }

        AbstractContainerMenu self = (AbstractContainerMenu) (Object) this;
        Slot slot = self.slots.get(i);

        if (slot.container instanceof Inventory inventory && inventory.player instanceof ServerPlayer player) {
            if (modonomicon$isAcquisition(oldItem, current)) {
                ItemStack acquired = currentCopy.get();
                if (ResearchServices.hooks().onItemAcquired(player, acquired)) {
                    ResearchStateManager.get().syncFor(player);
                    BookVisualStateManager.get().syncFor(player);
                }
            }
        }
    }

    private static boolean modonomicon$isAcquisition(ItemStack oldItem, ItemStack newItem) {
        if (oldItem.isEmpty() && !newItem.isEmpty()) {
            return true;
        }
        if (!oldItem.isEmpty() && !newItem.isEmpty()
                && ItemStack.isSameItem(oldItem, newItem)
                && newItem.getCount() > oldItem.getCount()) {
            return true;
        }
        return false;
    }
}
