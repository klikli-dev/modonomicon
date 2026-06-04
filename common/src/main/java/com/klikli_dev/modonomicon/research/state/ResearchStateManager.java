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
import com.klikli_dev.modonomicon.research.data.ResearchData;
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

    /**
     * Sets the stage index for a node. Used by admin commands.
     */
    public void setNodeStage(ServerPlayer player, Identifier nodeId, int stageIndex) {
        var state = this.getStateFor(player);
        state.setNodeStageIndex(nodeId, stageIndex);
        this.saveData.setDirty();
    }

    /**
     * Returns true if the player has completed the given stage of a node.
     * Resolves the stage id to its location via stageLocations.
     */
    public boolean isStageCompleted(Player player, Identifier nodeId, Identifier stageId) {
        var state = this.getStateFor(player);
        var location = ResearchDataManager.get().data().stageLocations().get(stageId);
        if (location == null || !location.nodeId().equals(nodeId)) {
            return false;
        }
        return state.isStageCompleted(nodeId, location.stageIndex());
    }

    public boolean reevaluate(ServerPlayer player) {
        var state = this.getStateFor(player);
        var data = ResearchDataManager.get().data();
        boolean changed = false;

        for (var rule : data.nodeRules()) {
            // Check base fact requirements
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

            // Check base value requirements
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

            // Check stage dependencies on other nodes
            boolean stageDepsMet = true;
            for (var stageDep : rule.requiredStageDependencies()) {
                var location = data.stageLocations().get(stageDep.stageId());
                if (location == null || !location.nodeId().equals(stageDep.nodeId())) {
                    stageDepsMet = false;
                    break;
                }
                if (!state.isStageCompleted(location.nodeId(), location.stageIndex())) {
                    stageDepsMet = false;
                    break;
                }
            }
            if (!stageDepsMet) {
                continue;
            }

            // Base requirements met - activate the node
            boolean nodeChanged = state.unlockNode(rule.nodeId());
            if (nodeChanged) {
                var collector = TOAST_TRIGGER_COLLECTOR.get();
                if (collector != null && data.nodeToasts().containsKey(rule.nodeId())) {
                    collector.add(new ResearchToastTrigger(ResearchToastTrigger.ToastTriggerType.NODE_UNLOCKED, rule.nodeId(), 0));
                }
            }
            changed |= nodeChanged;

            // Set initial stage index if not started
            if (state.getNodeStageIndex(rule.nodeId()) == 0) {
                state.setNodeStageIndex(rule.nodeId(), 1);
                changed = true;
            }

            // Advance through stages
            changed |= advanceStages(state, rule.nodeId(), data.nodeStageRules(), data.stageLocations(), data.stageToasts());
        }

        if (changed) {
            this.saveData.setDirty();
        }
        return changed;
    }

    private boolean advanceStages(PlayerResearchState state, Identifier nodeId, List<ResearchData.NodeStageRule> stageRules, java.util.Map<Identifier, ResearchData.StageLocation> stageLocations, java.util.Map<Identifier, com.klikli_dev.modonomicon.research.data.ResearchToastDefinition> stageToasts) {
        boolean advanced;
        do {
            advanced = false;
            int currentStage = state.getNodeStageIndex(nodeId);
            int nextStageIndex = currentStage - 1;
            ResearchData.NodeStageRule nextRule = null;
            for (var rule : stageRules) {
                if (rule.nodeId().equals(nodeId) && rule.stageIndex() == nextStageIndex) {
                    nextRule = rule;
                    break;
                }
            }
            if (nextRule == null) {
                break;
            }

            // Check stage fact requirements
            boolean factsMet = true;
            for (var factId : nextRule.requiredFactIds()) {
                if (!state.hasFact(factId)) {
                    factsMet = false;
                    break;
                }
            }
            if (!factsMet) {
                break;
            }

            // Check stage value requirements
            boolean valuesMet = true;
            for (var valueReq : nextRule.requiredValueRequirements()) {
                if (state.getValue(valueReq.valueId()) < valueReq.threshold()) {
                    valuesMet = false;
                    break;
                }
            }
            if (!valuesMet) {
                break;
            }

            // Advance to next stage
            state.setNodeStageIndex(nodeId, currentStage + 1);
            advanced = true;

            // Emit stage completion toast trigger
            var collector = TOAST_TRIGGER_COLLECTOR.get();
            if (collector != null) {
                // Find the stage id for the stage we just completed
                for (var entry : stageLocations.entrySet()) {
                    if (entry.getValue().nodeId().equals(nodeId) && entry.getValue().stageIndex() == nextStageIndex) {
                        Identifier stageId = entry.getKey();
                        if (stageToasts.containsKey(stageId)) {
                            collector.add(new ResearchToastTrigger(ResearchToastTrigger.ToastTriggerType.NODE_STAGE_COMPLETED, stageId, 0));
                        }
                        break;
                    }
                }
            }
        } while (advanced);
        return advanced;
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
