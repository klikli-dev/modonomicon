/*
 * SPDX-FileCopyrightText: 2023 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.research.state;

import com.klikli_dev.modonomicon.networking.RequestSyncResearchStateMessage;
import com.klikli_dev.modonomicon.networking.SyncResearchStateMessage;
import com.klikli_dev.modonomicon.platform.Services;
import com.klikli_dev.modonomicon.research.data.ResearchDataManager;
import com.klikli_dev.modonomicon.research.data.ResearchNodeDefinition;
import com.klikli_dev.modonomicon.research.networking.ResearchToastTrigger;
import it.unimi.dsi.fastutil.objects.Object2ObjectMaps;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

import java.util.ArrayList;
import java.util.List;

public class ResearchStateManager {

    private static final ResearchStateManager INSTANCE = new ResearchStateManager();
    private static final ThreadLocal<List<ResearchToastTrigger>> TOAST_TRIGGER_COLLECTOR = ThreadLocal.withInitial(() -> null);

    public ResearchStatesSaveData saveData;

    public static ResearchStateManager get() {
        return INSTANCE;
    }

    public PlayerResearchState getStateFor(Player player) {
        this.getSaveDataIfNecessary(player);
        return this.saveData.getFor(player.getUUID());
    }

    public boolean grantFact(ServerPlayer player, Identifier factId) {
        boolean changed = this.getStateFor(player).grantFact(factId);
        if (changed) {
            this.saveData.setDirty();
            var collector = TOAST_TRIGGER_COLLECTOR.get();
            if (collector != null && ResearchDataManager.get().data().factToasts().containsKey(factId)) {
                collector.add(new ResearchToastTrigger(ResearchToastTrigger.ToastTriggerType.FACT_GRANTED, factId, 0));
            }
        }
        return changed;
    }

    public boolean revokeFact(ServerPlayer player, Identifier factId) {
        boolean changed = this.getStateFor(player).revokeFact(factId);
        if (changed) {
            this.saveData.setDirty();
        }
        return changed;
    }

    public boolean incrementValue(ServerPlayer player, Identifier valueId, int amount) {
        var state = this.getStateFor(player);
        state.incrementValue(valueId, amount);
        this.saveData.setDirty();
        var collector = TOAST_TRIGGER_COLLECTOR.get();
        if (collector != null && ResearchDataManager.get().data().valueToasts().containsKey(valueId)) {
            collector.add(new ResearchToastTrigger(ResearchToastTrigger.ToastTriggerType.VALUE_INCREMENTED, valueId, state.getValue(valueId)));
        }
        return true;
    }

    public int setValue(ServerPlayer player, Identifier valueId, int amount) {
        var state = this.getStateFor(player);
        int newValue = state.setValue(valueId, amount);
        this.saveData.setDirty();
        return newValue;
    }

    public boolean lockNode(ServerPlayer player, Identifier nodeId) {
        boolean changed = this.getStateFor(player).lockNode(nodeId);
        if (changed) {
            this.saveData.setDirty();
        }
        return changed;
    }

    public boolean reevaluate(ServerPlayer player) {
        var state = this.getStateFor(player);
        boolean changed = false;
        for (var rule : ResearchDataManager.get().data().nodeRules()) {
            boolean factsMet = true;
            for (var factId : rule.requiredFactIds()) {
                if (!state.hasFact(factId)) {
                    factsMet = false;
                    break;
                }
            }
            if (!factsMet) {
                continue;
            }

            boolean valuesMet = true;
            for (var valueReq : rule.requiredValueRequirements()) {
                if (state.getValue(valueReq.valueId()) < valueReq.threshold()) {
                    valuesMet = false;
                    break;
                }
            }
            if (!valuesMet) {
                continue;
            }

            boolean nodeChanged = state.unlockNode(rule.nodeId());
            if (nodeChanged) {
                var collector = TOAST_TRIGGER_COLLECTOR.get();
                if (collector != null && ResearchDataManager.get().data().nodeToasts().containsKey(rule.nodeId())) {
                    collector.add(new ResearchToastTrigger(ResearchToastTrigger.ToastTriggerType.NODE_UNLOCKED, rule.nodeId(), 0));
                }
            }
            changed |= nodeChanged;
        }
        if (changed) {
            this.saveData.setDirty();
        }
        return changed;
    }

    public boolean isNodeUnlocked(Player player, Identifier nodeId) {
        return this.getStateFor(player).isNodeUnlocked(nodeId);
    }

    public void resetFor(ServerPlayer player) {
        this.getStateFor(player).reset();
        this.saveData.setDirty();
    }

    public void syncFor(ServerPlayer player) {
        Services.NETWORK.sendTo(player, new SyncResearchStateMessage(this.getStateFor(player)));
    }

    public void installClientState(Player player, PlayerResearchState state) {
        this.saveData = new ResearchStatesSaveData(Object2ObjectMaps.singleton(player.getUUID(), state));
    }

    public void clearCachedSaveData() {
        this.saveData = null;
    }

    public void onDatapackSync(Player player) {
        this.getSaveDataIfNecessary(player);
    }

    /**
     * Begins toast trigger collection for the current thread.
     * Call this before a batch of state mutations to collect toast triggers.
     */
    public static void beginToastCollection() {
        TOAST_TRIGGER_COLLECTOR.set(new ArrayList<>());
    }

    /**
     * Ends toast trigger collection and returns the collected triggers.
     * Returns an empty list if collection was not active.
     */
    public static List<ResearchToastTrigger> endToastCollection() {
        var collector = TOAST_TRIGGER_COLLECTOR.get();
        List<ResearchToastTrigger> triggers;
        if (collector == null) {
            triggers = List.of();
        } else {
            triggers = List.copyOf(collector);
            TOAST_TRIGGER_COLLECTOR.set(null);
        }
        return triggers;
    }

    public void onServerTickEnd(MinecraftServer server) {
    }

    private void getSaveDataIfNecessary(Player player) {
        if (this.saveData == null) {
            if (player instanceof ServerPlayer serverPlayer) {
                this.saveData = serverPlayer.level().getServer().overworld().getDataStorage().computeIfAbsent(ResearchStatesSaveData.TYPE);
            } else {
                this.saveData = new ResearchStatesSaveData();
                Services.NETWORK.sendToServer(RequestSyncResearchStateMessage.INSTANCE);
            }
        }
    }
}
