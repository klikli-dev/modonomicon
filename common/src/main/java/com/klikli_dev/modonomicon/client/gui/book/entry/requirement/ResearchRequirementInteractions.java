/*
 * SPDX-FileCopyrightText: 2026 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.client.gui.book.entry.requirement;

import com.klikli_dev.modonomicon.api.ModonomiconConstants.I18n.Gui;
import com.klikli_dev.modonomicon.networking.PerformResearchActionMessage;
import com.klikli_dev.modonomicon.networking.CompleteResearchStageMessage;
import com.klikli_dev.modonomicon.platform.Services;
import com.klikli_dev.modonomicon.research.data.ResearchCost;
import com.klikli_dev.modonomicon.research.ResearchFeedback;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Client side of requirement interactions: pre-checks clicks locally for feedback, sends the requests and builds the cost lines.
 * The server validates every request again, so local checks only decide whether a request is sent.
 */
public final class ResearchRequirementInteractions {

    private static final long COMPLETING_TIMEOUT_MS = 3000L;
    private static final Map<Identifier, Completing> COMPLETING = new HashMap<>();

    /**
     * A completion request in flight, the stage index is the one shown when the request was sent.
     */
    private record Completing(int stageIndex, long startedAt) {
    }

    private ResearchRequirementInteractions() {
    }

    @Nullable
    public static Player player() {
        return Minecraft.getInstance().player;
    }

    /**
     * Performs the action of a row if it looks available and affordable. Otherwise plays the fail sound and sends nothing.
     */
    public static void performAction(RequirementRow.ActionInfo info) {
        var player = player();
        if (player == null) {
            return;
        }
        var presenter = ResearchActionPresenterRegistry.get(info.action().type().id());
        if (presenter.unavailableReason(info.action(), player) != null || !info.action().cost().canAfford(player)) {
            playSound(SoundEvents.VILLAGER_NO);
            return;
        }
        Services.NETWORK.sendToServer(new PerformResearchActionMessage(info.nodeId(), info.factId()));
        playSound(SoundEvents.UI_BUTTON_CLICK.value());
    }

    /**
     * Tooltip lines of an action row: what clicking does, the cost, and the reason it is unavailable, if any.
     */
    public static List<Component> actionTooltip(RequirementRow row, Player player) {
        var lines = new ArrayList<Component>();
        var info = row.action();
        if (info == null) {
            return lines;
        }
        var presenter = ResearchActionPresenterRegistry.get(info.action().type().id());
        lines.add(presenter.line(info.action(), row.title()).copy().withStyle(ChatFormatting.DARK_AQUA));
        lines.addAll(costLines(info.action().cost(), player));
        var reason = presenter.unavailableReason(info.action(), player);
        if (reason != null) {
            lines.add(reason.copy().withStyle(ChatFormatting.GRAY));
        }
        return lines;
    }

    /**
     * "Costs:" followed by one line per cost entry. Entries the player cannot pay are red, others gray. Empty for free costs.
     */
    public static List<Component> costLines(ResearchCost cost, Player player) {
        var lines = new ArrayList<Component>();
        if (cost.isEmpty()) {
            return lines;
        }
        var missing = cost.missing(player);
        lines.add(Component.translatable(Gui.REQUIREMENTS_COSTS).withStyle(ChatFormatting.GRAY));
        for (var item : cost.items()) {
            boolean lacking = missing.items().stream().anyMatch(item::sameEntry);
            var color = lacking ? ChatFormatting.RED : ChatFormatting.GRAY;
            lines.add(ResearchFeedback.describe(item).copy().withStyle(color));
        }
        if (cost.levels() > 0) {
            var color = missing.levels() > 0 ? ChatFormatting.RED : ChatFormatting.GRAY;
            lines.add(Component.translatable(Gui.REQUIREMENTS_COST_LEVELS, cost.levels()).withStyle(color));
        }
        return lines;
    }

    /**
     * Requests the completion of a manual stage if it is affordable locally. Otherwise plays the fail sound and sends nothing.
     * Starts the "Completing..." state, which ends when the stage changes or after a timeout.
     */
    public static void completeStage(RequirementBlock.ManualStage manual) {
        var player = player();
        if (player == null || isCompleting(manual)) {
            return;
        }
        if (!manual.cost().canAfford(player)) {
            playSound(SoundEvents.VILLAGER_NO);
            return;
        }
        COMPLETING.put(manual.nodeId(), new Completing(manual.stageIndex(), System.currentTimeMillis()));
        Services.NETWORK.sendToServer(new CompleteResearchStageMessage(manual.nodeId()));
        playSound(SoundEvents.UI_BUTTON_CLICK.value());
    }

    /**
     * True while a completion of the shown stage is in flight. Ends once the stage index changed (the sync arrived) or after a timeout.
     */
    public static boolean isCompleting(RequirementBlock.ManualStage manual) {
        var completing = COMPLETING.get(manual.nodeId());
        if (completing == null) {
            return false;
        }
        if (completing.stageIndex() != manual.stageIndex() || System.currentTimeMillis() - completing.startedAt() > COMPLETING_TIMEOUT_MS) {
            COMPLETING.remove(manual.nodeId());
            return false;
        }
        return true;
    }

    public static void playSound(SoundEvent sound) {
        Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(sound, 1.0F));
    }
}
