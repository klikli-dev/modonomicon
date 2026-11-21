/*
 * SPDX-FileCopyrightText: 2026 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.mixin;

import com.klikli_dev.modonomicon.Modonomicon;
import com.klikli_dev.modonomicon.bookstate.BookVisualStateManager;
import com.klikli_dev.modonomicon.scholarship.ScholarshipFeedback;
import com.klikli_dev.modonomicon.research.ResearchServices;
import com.klikli_dev.modonomicon.research.state.ResearchStateManager;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerPlayerGameMode;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Fires the item_used research trigger when a player uses an item in the air, on all loaders.
 * <p>
 * Research state that changed is synced to the player. If the outcome is handled, the consumed items are taken, the learning feedback
 * plays and the vanilla use is cancelled by returning success. Otherwise the vanilla use runs unchanged.
 */
@Mixin(ServerPlayerGameMode.class)
public abstract class MixinServerPlayerGameMode {

    @Inject(
        method = "useItem(Lnet/minecraft/server/level/ServerPlayer;Lnet/minecraft/world/level/Level;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/InteractionHand;)Lnet/minecraft/world/InteractionResult;",
        at = @At("HEAD"),
        cancellable = true
    )
    private void modonomicon$onItemUsed(ServerPlayer player, Level level, ItemStack stack, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir) {
        if (stack.isEmpty() || player.isSpectator() || player.getCooldowns().isOnCooldown(stack)) {
            return;
        }
        Modonomicon.LOG.debug("item_used: {} used {} in air", player.getGameProfile().name(), stack);

        var outcome = ResearchServices.hooks().onItemUsed(player, stack);
        if (!outcome.changed()) {
            return;
        }
        ResearchStateManager.get().syncFor(player);
        BookVisualStateManager.get().syncFor(player);
        if (!outcome.handled()) {
            return;
        }

        int consume = Math.min(outcome.consume(), stack.getCount());
        if (consume > 0) {
            stack.consume(consume, player);
        }
        ScholarshipFeedback.learned(player);
        cir.setReturnValue(InteractionResult.SUCCESS);
    }
}
