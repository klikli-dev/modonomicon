/*
 * SPDX-FileCopyrightText: 2026 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.client.gui.book.entry.scholarship;

import com.klikli_dev.modonomicon.api.ModonomiconConstants.I18n.Gui;
import com.klikli_dev.modonomicon.networking.GrantResearchFactMessage;
import com.klikli_dev.modonomicon.networking.PerformResearchActionMessage;
import com.klikli_dev.modonomicon.networking.CompleteResearchStageMessage;
import com.klikli_dev.modonomicon.platform.Services;
import com.klikli_dev.modonomicon.research.action.ResearchAction;
import com.klikli_dev.modonomicon.research.data.ResearchCost;
import com.klikli_dev.modonomicon.scholarship.ScholarshipFeedback;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.Identifier;
import net.minecraft.server.permissions.Permissions;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

/**
 * Client side of requirement interactions: pre-checks clicks locally for feedback, sends the requests and builds the cost lines.
 * The server validates every request again, so local checks only decide whether a request is sent.
 */
public final class ScholarshipInteractions {

    private static final long COMPLETING_TIMEOUT_MS = 3000L;

    /**
     * A completion request in flight, the stage index is the one shown when the request was sent.
     */
    private record Completing(int stageIndex, long startedAt) {
    }

    /**
     * The completion requests in flight by node id. Owned by one screen, see the constructor of the controller.
     */
    private final Map<Identifier, Completing> completing = new HashMap<>();

    public ScholarshipInteractions() {
    }

    @Nullable
    public static Player player() {
        return Minecraft.getInstance().player;
    }

    /**
     * Interprets a row action. A research action is performed if it looks available and affordable, otherwise the fail sound
     * plays and nothing is sent. Opening an entry is handed to the navigation of the screen.
     */
    public static void perform(ScholarshipRowAction action, BiConsumer<Identifier, Identifier> openEntry) {
        switch (action) {
            case ScholarshipRowAction.Perform perform -> performAction(perform);
            case ScholarshipRowAction.OpenEntry open -> openEntry.accept(open.bookId(), open.entryId());
        }
    }

    /**
     * True if the player may grant the fact of a requirement by shift-clicking it. Same permission as the grant fact command.
     * The server checks the permission again, see {@link GrantResearchFactMessage}.
     */
    public static boolean canGrantFacts(@Nullable Player player) {
        return player != null && player.permissions().hasPermission(Permissions.COMMANDS_GAMEMASTER);
    }

    /**
     * Asks the server to grant the fact of a requirement row. Only sent for rows that are not met yet, the server ignores unknown facts.
     */
    public static void grantFact(Identifier factId) {
        Services.NETWORK.sendToServer(new GrantResearchFactMessage(factId));
        playSound(SoundEvents.UI_BUTTON_CLICK.value());
    }

    /**
     * The hint line of a requirement row that shift-clicking grants, only shown to players that may grant facts.
     */
    @Nullable
    public static Component grantHint(ScholarshipRow row, @Nullable Player player) {
        if (row.factId() == null || row.met() || !canGrantFacts(player)) {
            return null;
        }
        return Component.translatable(Gui.SCHOLARSHIP_GRANT_FACT_HINT).withStyle(Style.EMPTY.withColor(ChatFormatting.DARK_GREEN).withItalic(true));
    }

    private static void performAction(ScholarshipRowAction.Perform info) {
        var player = player();
        if (player == null) {
            return;
        }
        var reason = unavailableReason(info.action(), player);
        if (reason != null || !info.action().cost().canAfford(player)) {
            playSound(SoundEvents.VILLAGER_NO);
            return;
        }
        Services.NETWORK.sendToServer(new PerformResearchActionMessage(info.nodeId(), info.factId()));
        playSound(SoundEvents.UI_BUTTON_CLICK.value());
    }

    /**
     * Tooltip lines of an action row: what clicking does, the cost, and the reason it is unavailable, if any.
     */
    public static List<Component> actionTooltip(ScholarshipRow row, Player player) {
        var lines = new ArrayList<Component>();
        if (!(row.action() instanceof ScholarshipRowAction.Perform info)) {
            return lines;
        }
        var action = info.action();
        //once the fact is recorded there is nothing left to record, a repeatable action only offers what it does again
        if (action.grantsFact() && !row.met()) {
            lines.add(Component.translatable(Gui.SCHOLARSHIP_ACTION_PERFORM_TITLED, row.title()).copy().withStyle(ChatFormatting.DARK_AQUA));
        }
        var actionContext = new ScholarshipActionContext(action, row.title(), row.met());
        for (var effect : action.effects()) {
            lines.add(ScholarshipEffectPresenterRegistry.get(effect.type().id()).line(effect, actionContext).copy().withStyle(ChatFormatting.DARK_AQUA));
        }
        lines.addAll(costLines(action.cost(), player));
        var reason = unavailableReason(action, player);
        if (reason != null) {
            lines.add(reason.copy().withStyle(ChatFormatting.GRAY));
        }
        return lines;
    }

    /**
     * The reason the action cannot be performed right now, given by the first of its effects that reports one. Null if all look available.
     */
    @Nullable
    public static Component unavailableReason(ResearchAction action, Player player) {
        for (var effect : action.effects()) {
            var reason = ScholarshipEffectPresenterRegistry.get(effect.type().id()).unavailableReason(effect, player);
            if (reason != null) {
                return reason;
            }
        }
        return null;
    }

    /**
     * "Costs:" followed by one line per cost entry, each described by its cost presenter. Entries the player cannot pay are red, others gray.
     * Empty for free costs.
     */
    public static List<Component> costLines(ResearchCost cost, Player player) {
        var lines = new ArrayList<Component>();
        if (cost.isEmpty()) {
            return lines;
        }
        lines.add(Component.translatable(Gui.SCHOLARSHIP_COSTS).withStyle(ChatFormatting.GRAY));
        var missing = cost.missing(player);
        for (var entry : cost.entries()) {
            boolean unpayable = missing.stream().anyMatch(candidate -> candidate == entry);
            var color = unpayable ? ChatFormatting.RED : ChatFormatting.GRAY;
            lines.add(ScholarshipCostPresenterRegistry.get(entry.type().id()).line(entry).copy().withStyle(color));
        }
        return lines;
    }
    /**
     * Requests the completion of a manual stage if it is affordable locally. Otherwise plays the fail sound and sends nothing.
     * Starts the "Completing..." state, which ends when the stage changes or after a timeout.
     */
    public void completeStage(ScholarshipBlock.ManualStage manual) {
        var player = player();
        if (player == null || isCompleting(manual)) {
            return;
        }
        if (!manual.cost().canAfford(player)) {
            playSound(SoundEvents.VILLAGER_NO);
            return;
        }
        this.completing.put(manual.nodeId(), new Completing(manual.stageIndex(), System.currentTimeMillis()));
        Services.NETWORK.sendToServer(new CompleteResearchStageMessage(manual.nodeId()));
        playSound(SoundEvents.UI_BUTTON_CLICK.value());
    }

    /**
     * True while a completion of the shown stage is in flight. Ends once the stage index changed (the sync arrived) or after a timeout.
     */
    public boolean isCompleting(ScholarshipBlock.ManualStage manual) {
        var inFlight = this.completing.get(manual.nodeId());
        if (inFlight == null) {
            return false;
        }
        if (inFlight.stageIndex() != manual.stageIndex() || System.currentTimeMillis() - inFlight.startedAt() > COMPLETING_TIMEOUT_MS) {
            this.completing.remove(manual.nodeId());
            return false;
        }
        return true;
    }

    public static void playSound(SoundEvent sound) {
        Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(sound, 1.0F));
    }
}
