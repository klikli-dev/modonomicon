/*
 * SPDX-FileCopyrightText: 2023 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.research.state;

import com.klikli_dev.modonomicon.networking.RequestSyncResearchStateMessage;
import com.klikli_dev.modonomicon.networking.SyncResearchStateMessage;
import com.klikli_dev.modonomicon.platform.Services;
import com.klikli_dev.modonomicon.research.data.ResearchData;
import com.klikli_dev.modonomicon.research.data.ResearchData.StageLocation;
import com.klikli_dev.modonomicon.research.data.ResearchDataManager;
import com.klikli_dev.modonomicon.research.data.ResearchNodeDefinition;
import com.klikli_dev.modonomicon.research.effect.ResearchEffectContext;
import com.klikli_dev.modonomicon.research.networking.ResearchToastTrigger;
import com.klikli_dev.modonomicon.research.networking.ResearchToastTrigger.ToastTriggerType;
import it.unimi.dsi.fastutil.objects.Object2ObjectMaps;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Owns the research state of all players and applies changes to it. Mutations mark the save data dirty and collect
 * toast triggers while a toast collection is active, see {@link #beginToastCollection()}.
 * See the {@code research} package documentation for where this fits in the pipeline.
 */
public class ResearchStateManager {

    private static final ResearchStateManager INSTANCE = new ResearchStateManager();
    private static final ThreadLocal<ToastCollection> TOAST_COLLECTION = new ThreadLocal<>();

    public ResearchStatesSaveData saveData;

    public static ResearchStateManager get() {
        return INSTANCE;
    }

    /**
     * Returns the research state of the player. On the client, the first access without installed state requests a sync
     * from the server.
     */
    public PlayerResearchState getStateFor(Player player) {
        this.getSaveDataIfNecessary(player);
        return this.saveData.getFor(player.getUUID());
    }

    public boolean grantFact(ServerPlayer player, Identifier factId) {
        boolean changed = this.getStateFor(player).grantFact(factId);
        if (changed) {
            this.saveData.setDirty();
            collectToast(ToastTriggerType.FACT_GRANTED, factId, 0, ResearchDataManager.get().data().factToasts().containsKey(factId));
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

    /**
     * Increments a value and returns true if the stored value actually changed. A zero increment changes nothing.
     */
    public boolean incrementValue(ServerPlayer player, Identifier valueId, int amount) {
        var state = this.getStateFor(player);
        int before = state.getValue(valueId);
        int after = state.incrementValue(valueId, amount);
        if (after == before) {
            return false;
        }
        this.saveData.setDirty();
        collectToast(ToastTriggerType.VALUE_INCREMENTED, valueId, after, ResearchDataManager.get().data().valueToasts().containsKey(valueId));
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
     * Sets the raw stage progress for a node (see {@link PlayerResearchState#setNodeStageIndex}). Used by admin commands
     * and resets. Stages that are no longer completed forget their granted effects, so they grant them again when completed anew.
     */
    public void setNodeStage(ServerPlayer player, Identifier nodeId, int stageIndex) {
        var state = this.getStateFor(player);
        state.setNodeStageIndex(nodeId, stageIndex);

        var node = ResearchDataManager.get().data().nodeDefinitions().get(nodeId);
        if (node != null) {
            for (int i = state.completedStageCount(nodeId); i < node.stages().size(); i++) {
                state.forgetEffectsGranted(node.stages().get(i).id());
            }
        }
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

    /**
     * Activates all nodes whose requirements are met and advances their stages as far as possible.
     * Repeats until nothing changes, because stage effects can grant facts and values that unlock further nodes or stages.
     * Every change is monotonic (unlocks, stage advances and effects happen once), so this terminates.
     *
     * @return true if any research state changed
     */
    public boolean reevaluate(ServerPlayer player) {
        boolean changed = false;
        boolean passChanged;
        do {
            passChanged = this.reevaluatePass(player);
            changed |= passChanged;
        } while (passChanged);
        return changed;
    }

    private boolean reevaluatePass(ServerPlayer player) {
        var state = this.getStateFor(player);
        var data = ResearchDataManager.get().data();
        boolean changed = false;

        for (var rule : data.nodeRules()) {
            if (!requirementsMet(state, rule.requiredFactIds(), rule.requiredValueRequirements())) {
                continue;
            }
            if (!stageDependenciesMet(state, data, rule.requiredStageDependencies())) {
                continue;
            }

            boolean nodeChanged = state.unlockNode(rule.nodeId());
            collectToast(ToastTriggerType.NODE_UNLOCKED, rule.nodeId(), 0, nodeChanged && data.nodeToasts().containsKey(rule.nodeId()));
            changed |= nodeChanged;
            changed |= state.startNodeIfNotStarted(rule.nodeId());
            changed |= advanceStages(player, state, rule.nodeId(), data);
        }

        if (changed) {
            this.saveData.setDirty();
        }
        return changed;
    }

    /**
     * Completes stages of the node while the next stage's requirements are met. Manual stages stop the advance,
     * they are only completed via {@link #completeManualStage}.
     *
     * @return true if at least one stage was completed
     */
    private static boolean advanceStages(ServerPlayer player, PlayerResearchState state, Identifier nodeId, ResearchData data) {
        boolean changed = false;
        while (true) {
            var location = new StageLocation(nodeId, state.currentStageIndex(nodeId));
            var rule = data.stageRulesByLocation().get(location);
            if (rule == null || rule.completion().isManual() || !requirementsMet(state, rule.requiredFactIds(), rule.requiredValueRequirements())) {
                break;
            }

            completeStage(player, state, data, rule);
            changed = true;
        }
        return changed;
    }

    /**
     * Marks the current stage of the rule's node as completed and grants its effects, at most once per stage and player.
     * Used by automatic and manual completion.
     */
    private static void completeStage(ServerPlayer player, PlayerResearchState state, ResearchData data, ResearchData.NodeStageRule rule) {
        state.completeCurrentStage(rule.nodeId());
        collectToast(ToastTriggerType.NODE_STAGE_COMPLETED, rule.stageId(), 0, data.stageToasts().containsKey(rule.stageId()));

        if (!rule.effects().isEmpty() && state.markEffectsGranted(rule.stageId())) {
            for (var effect : rule.effects()) {
                effect.apply(player, ResearchEffectContext.NONE);
            }
        }
    }

    /**
     * Returns the current stage of a node if it is a manual stage whose requirements are met.
     * Empty if the node is not unlocked, the current stage is automatic or its requirements are not met yet.
     */
    public Optional<ResearchData.NodeStageRule> readyManualStage(Player player, Identifier nodeId) {
        var state = this.getStateFor(player);
        if (!state.isNodeUnlocked(nodeId)) {
            return Optional.empty();
        }
        var data = ResearchDataManager.get().data();
        var rule = data.stageRulesByLocation().get(new StageLocation(nodeId, state.currentStageIndex(nodeId)));
        if (rule == null || !rule.completion().isManual() || !requirementsMet(state, rule.requiredFactIds(), rule.requiredValueRequirements())) {
            return Optional.empty();
        }
        return Optional.of(rule);
    }

    /**
     * Completes a manual stage returned by {@link #readyManualStage}, grants its effects and cascades through
     * following automatic stages. Costs are not handled here, the caller consumes them.
     */
    public void completeManualStage(ServerPlayer player, ResearchData.NodeStageRule rule) {
        var state = this.getStateFor(player);
        var data = ResearchDataManager.get().data();
        completeStage(player, state, data, rule);
        advanceStages(player, state, rule.nodeId(), data);
        this.saveData.setDirty();
    }

    /**
     * Checks the fact and value requirements of a node or a stage. Used for both, so the two cannot drift apart.
     */
    private static boolean requirementsMet(
            PlayerResearchState state,
            List<Identifier> requiredFactIds,
            List<ResearchNodeDefinition.ValueRequirement> requiredValues
    ) {
        for (var factId : requiredFactIds) {
            if (!state.hasFact(factId)) {
                return false;
            }
        }
        for (var valueReq : requiredValues) {
            if (state.getValue(valueReq.valueId()) < valueReq.threshold()) {
                return false;
            }
        }
        return true;
    }

    private static boolean stageDependenciesMet(
            PlayerResearchState state,
            ResearchData data,
            List<ResearchNodeDefinition.StageDependency> stageDependencies
    ) {
        for (var stageDep : stageDependencies) {
            var location = data.stageLocations().get(stageDep.stageId());
            if (location == null || !location.nodeId().equals(stageDep.nodeId())) {
                return false;
            }
            if (!state.isStageCompleted(location.nodeId(), location.stageIndex())) {
                return false;
            }
        }
        return true;
    }

    public boolean isNodeUnlocked(Player player, Identifier nodeId) {
        return this.getStateFor(player).isNodeUnlocked(nodeId);
    }

    /**
     * Returns true if the player completed all stages of a node. A node without stages is complete once started.
     */
    public boolean isNodeComplete(Player player, Identifier nodeId) {
        var node = ResearchDataManager.get().data().nodeDefinitions().get(nodeId);
        if (node == null) {
            return false;
        }
        var state = this.getStateFor(player);
        return state.isNodeStarted(nodeId) && state.completedStageCount(nodeId) >= node.stages().size();
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
     * <p>
     * Collections nest: a nested begin joins the outer collection, and only the matching outermost
     * {@link #endToastCollection()} returns the triggers, so one batch never produces more than one toast message.
     */
    public static void beginToastCollection() {
        var collection = TOAST_COLLECTION.get();
        if (collection == null) {
            collection = new ToastCollection();
            TOAST_COLLECTION.set(collection);
        }
        collection.depth++;
    }

    /**
     * Ends toast trigger collection and returns the collected triggers.
     * Returns an empty list if collection was not active, or if this ends a nested collection.
     */
    public static List<ResearchToastTrigger> endToastCollection() {
        var collection = TOAST_COLLECTION.get();
        if (collection == null) {
            return List.of();
        }
        collection.depth--;
        if (collection.depth > 0) {
            return List.of();
        }
        TOAST_COLLECTION.remove();
        return List.copyOf(collection.triggers);
    }

    private static void collectToast(ToastTriggerType type, Identifier elementId, int currentValue, boolean hasToast) {
        var collection = TOAST_COLLECTION.get();
        if (collection != null && hasToast) {
            collection.triggers.add(new ResearchToastTrigger(type, elementId, currentValue));
        }
    }

    /**
     * Called by the loaders at the end of every server tick. Research currently has no per-tick work; the hook is kept so
     * loaders have a stable place to call into if that changes.
     */
    public void onServerTickEnd(MinecraftServer server) {
    }

    /**
     * On the server, loads the save data from the overworld data storage if needed. On the client, creates empty save
     * data and requests a sync from the server, which is the only path that makes the client's state available.
     */
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

    private static final class ToastCollection {
        private final List<ResearchToastTrigger> triggers = new ArrayList<>();
        private int depth;
    }
}
