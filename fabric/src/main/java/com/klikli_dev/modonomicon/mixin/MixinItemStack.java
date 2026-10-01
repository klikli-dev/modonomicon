/*
 * SPDX-FileCopyrightText: 2026 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.mixin;

import com.klikli_dev.modonomicon.client.gui.book.associated.AssociatedHoverTracker;
import com.klikli_dev.modonomicon.client.gui.book.associated.AssociatedTooltipHelper;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

/**
 * Attaches the associated-entry image to item tooltips that do not already have one.
 * The text lines are added via Fabric's {@code ItemTooltipCallback}.
 */
@Mixin(ItemStack.class)
public abstract class MixinItemStack {

    @Inject(method = "getTooltipImage", at = @At("RETURN"), cancellable = true)
    private void modonomicon$addAssociatedImage(CallbackInfoReturnable<Optional<TooltipComponent>> cir) {
        if (cir.getReturnValue().isPresent()) {
            return;
        }
        ItemStack self = (ItemStack) (Object) this;
        AssociatedHoverTracker.onTooltipGather(self);
        AssociatedTooltipHelper.gatherImage(self).ifPresent(image -> cir.setReturnValue(Optional.of(image)));
    }
}
