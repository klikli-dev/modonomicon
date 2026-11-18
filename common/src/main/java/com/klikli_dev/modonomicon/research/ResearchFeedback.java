/*
 * SPDX-FileCopyrightText: 2026 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.research;

import com.klikli_dev.modonomicon.api.ModonomiconConstants.I18n.Gui;
import com.klikli_dev.modonomicon.research.cost.ResearchCostEntry;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;

import java.util.List;

/**
 * Tells a player the outcome of a research action or manual stage completion: a quiet sound and an action bar message.
 */
public final class ResearchFeedback {

    private ResearchFeedback() {
    }

    /**
     * A quiet sound for a performed action or completed stage.
     */
    public static void performed(ServerPlayer player) {
        playSound(player, SoundEvents.EXPERIENCE_ORB_PICKUP, 0.3F, 1.4F);
    }

    /**
     * Lists the entries of a cost the player cannot pay. Falls back to {@link #invalid} if nothing is missing.
     */
    public static void missing(ServerPlayer player, List<ResearchCostEntry> missing) {
        if (missing.isEmpty()) {
            invalid(player);
            return;
        }
        player.sendOverlayMessage(Component.translatable(Gui.FEEDBACK_MISSING, describe(missing)));
        playSound(player, SoundEvents.VILLAGER_NO, 0.4F, 1.0F);
    }

    /**
     * The action's own checks failed, or its effects changed nothing.
     */
    public static void unavailable(ServerPlayer player) {
        player.sendOverlayMessage(Component.translatable(Gui.FEEDBACK_UNAVAILABLE));
        playSound(player, SoundEvents.VILLAGER_NO, 0.4F, 1.0F);
    }

    /**
     * The node, stage or fact does not allow the action or the stage completion.
     */
    public static void invalid(ServerPlayer player) {
        player.sendOverlayMessage(Component.translatable(Gui.FEEDBACK_INVALID));
        playSound(player, SoundEvents.VILLAGER_NO, 0.4F, 1.0F);
    }

    /**
     * Describes the entries of a cost as a comma separated list, e.g. "1x Paper, 2 levels".
     */
    public static Component describe(List<ResearchCostEntry> entries) {
        MutableComponent result = Component.empty();
        for (int i = 0; i < entries.size(); i++) {
            if (i > 0) {
                result.append(", ");
            }
            result.append(entries.get(i).describe());
        }
        return result;
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
