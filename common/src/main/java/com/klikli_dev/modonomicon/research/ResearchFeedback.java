/*
 * SPDX-FileCopyrightText: 2026 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.research;

import com.klikli_dev.modonomicon.api.ModonomiconConstants.I18n.Gui;
import com.klikli_dev.modonomicon.research.data.ResearchCost;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;

/**
 * Tells a player the outcome of a research action or manual stage completion: a quiet sound and an action bar message.
 */
public final class ResearchFeedback {

    private ResearchFeedback() {
    }

    public static void performed(ServerPlayer player) {
        playSound(player, SoundEvents.EXPERIENCE_ORB_PICKUP, 0.3F, 1.4F);
    }

    /**
     * Lists the missing part of a cost. Falls back to {@link #invalid} if nothing is missing.
     */
    public static void missing(ServerPlayer player, ResearchCost missing) {
        if (missing.isEmpty()) {
            invalid(player);
            return;
        }
        player.sendOverlayMessage(Component.translatable(Gui.FEEDBACK_MISSING, describe(missing)));
        playSound(player, SoundEvents.VILLAGER_NO, 0.4F, 1.0F);
    }

    public static void unavailable(ServerPlayer player) {
        player.sendOverlayMessage(Component.translatable(Gui.FEEDBACK_UNAVAILABLE));
        playSound(player, SoundEvents.VILLAGER_NO, 0.4F, 1.0F);
    }

    public static void invalid(ServerPlayer player) {
        player.sendOverlayMessage(Component.translatable(Gui.FEEDBACK_INVALID));
        playSound(player, SoundEvents.VILLAGER_NO, 0.4F, 1.0F);
    }

    /**
     * Describes a cost as a comma separated list, e.g. "1× Paper, 2 levels".
     */
    public static Component describe(ResearchCost cost) {
        MutableComponent result = Component.empty();
        boolean first = true;
        for (var item : cost.items()) {
            if (!first) {
                result.append(", ");
            }
            result.append(describe(item));
            first = false;
        }
        if (cost.levels() > 0) {
            if (!first) {
                result.append(", ");
            }
            result.append(Component.translatable(Gui.REQUIREMENTS_COST_LEVELS, cost.levels()));
        }
        return result;
    }

    /**
     * Describes one cost entry, e.g. "2× Paper" or "1 durability: Shears".
     */
    public static Component describe(ResearchCost.ItemCost item) {
        if (item.durability().isPresent()) {
            return Component.translatable(Gui.REQUIREMENTS_COST_DURABILITY, item.durability().get(), item.displayName());
        }
        return Component.translatable(Gui.REQUIREMENTS_COST_ITEM, item.count(), item.displayName());
    }

    /**
     * A quiet sound for learning something by using an item.
     */
    public static void learned(ServerPlayer player) {
        playSound(player, SoundEvents.BOOK_PAGE_TURN, 0.5F, 1.2F);
    }

    private static void playSound(ServerPlayer player, SoundEvent sound, float volume, float pitch) {
        player.level().playSound(null, player.getX(), player.getY(), player.getZ(), sound, SoundSource.PLAYERS, volume, pitch);
    }
}
