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
import it.unimi.dsi.fastutil.objects.Object2ObjectMaps;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

public class ResearchStateManager {

    private static final ResearchStateManager INSTANCE = new ResearchStateManager();

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
        }
        return changed;
    }

    public boolean incrementValue(ServerPlayer player, Identifier valueId, int amount) {
        var state = this.getStateFor(player);
        state.incrementValue(valueId, amount);
        this.saveData.setDirty();
        return true;
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

            changed |= state.unlockNode(rule.nodeId());
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
