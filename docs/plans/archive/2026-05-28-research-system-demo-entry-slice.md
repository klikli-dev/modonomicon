# Research System Demo Entry Slice Implementation Plan

> Archived: implemented and squash-merged into `feat/research-system/main` as commit `2edffa40`.

> **For agentic workers:** REQUIRED SUB-SKILL: Use executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Add the first research-backed progression slice beside the legacy book unlock system, limited to the demo entry chain `condition_root -> condition_level_1 -> condition_level_2`.

**Architecture:** Add a new `research` domain parallel to `bookstate`, with its own datapack resources, per-player save data, and hook dispatcher. Keep the new book visibility condition in the existing `book.conditions` layer, and bridge `entry_viewed_once` from `BookEntryReadMessage` before the legacy `updateAndSync` pass so the new research-backed entries can still flow through existing UI and unlock sync behavior.

**Tech Stack:** Java 21, Gradle, Minecraft/NeoForge/Fabric shared common module, Mojang codecs, datapack reload listeners, manual verification in-game.

---

## File Map

### Create
- `common/src/main/java/com/klikli_dev/modonomicon/research/ResearchServices.java` - static access point for research services
- `common/src/main/java/com/klikli_dev/modonomicon/research/state/PlayerResearchState.java` - per-player persisted facts and unlocked research nodes
- `common/src/main/java/com/klikli_dev/modonomicon/research/state/ResearchStatesSaveData.java` - separate saved-data container for per-player research state
- `common/src/main/java/com/klikli_dev/modonomicon/research/state/ResearchStateManager.java` - server-side save/load/reset/query/update manager
- `common/src/main/java/com/klikli_dev/modonomicon/research/data/ResearchFactDefinition.java` - authored fact id definition
- `common/src/main/java/com/klikli_dev/modonomicon/research/data/ResearchNodeDefinition.java` - authored node definition for fact-based unlocks
- `common/src/main/java/com/klikli_dev/modonomicon/research/data/ResearchHookDefinition.java` - authored hook definition for `entry_viewed_once`
- `common/src/main/java/com/klikli_dev/modonomicon/research/data/ResearchData.java` - immutable container for loaded research data
- `common/src/main/java/com/klikli_dev/modonomicon/research/data/ResearchDataManager.java` - datapack reload listener for research resources
- `common/src/main/java/com/klikli_dev/modonomicon/research/hook/ResearchHookService.java` - service that handles `entry_viewed_once`
- `common/src/main/java/com/klikli_dev/modonomicon/book/conditions/BookResearchNodeUnlockedCondition.java` - new book condition that queries research node unlock state
- `common/src/main/java/com/klikli_dev/modonomicon/api/datagen/book/condition/BookResearchNodeUnlockedConditionModel.java` - datagen model for the new condition
- `common/src/main/java/com/klikli_dev/modonomicon/command/ResetResearchCommand.java` - admin/debug reset command for research-only state
- `common/src/generated/resources/data/modonomicon/modonomicon/research/demo/facts.json` - generated demo fact definitions after `runData`
- `common/src/generated/resources/data/modonomicon/modonomicon/research/demo/nodes.json` - generated demo node definitions after `runData`
- `common/src/generated/resources/data/modonomicon/modonomicon/research/demo/hooks.json` - generated demo hook definitions after `runData`

### Modify
- `common/src/main/java/com/klikli_dev/modonomicon/api/ModonomiconConstants.java` - add research datapack folder constant and any reset-command i18n keys used by the slice
- `common/src/main/java/com/klikli_dev/modonomicon/networking/BookEntryReadMessage.java` - invoke research hook handling before legacy update/sync
- `common/src/main/java/com/klikli_dev/modonomicon/registry/BookConditionTypeRegistry.java` - register `research_node_unlocked`
- `common/src/main/java/com/klikli_dev/modonomicon/api/datagen/ConditionHelper.java` - add research condition helper methods
- `common/src/main/java/com/klikli_dev/modonomicon/registry/CommandRegistry.java` - register the new research reset command
- `common/src/main/java/com/klikli_dev/modonomicon/registry/RegistryBootstrap.java` - bootstrap any research-related registries if needed
- `common/src/main/java/com/klikli_dev/modonomicon/datagen/book/demo/FeaturesCategory.java` - switch only `condition_level_1` and `condition_level_2` to research-backed conditions
- `fabric/src/main/java/com/klikli_dev/modonomicon/ModonomiconFabric.java` - register research reload listener, datapack sync, join sync, unload clear, and server tick handling
- `neo/src/main/java/com/klikli_dev/modonomicon/ModonomiconNeo.java` - register research reload listener, datapack sync, join sync, unload clear, and server tick handling

### Reuse Without Modification
- `common/src/main/java/com/klikli_dev/modonomicon/bookstate/BookUnlockStateManager.java` - keep legacy update/sync as the adapter that still feeds existing UI state
- `common/src/main/java/com/klikli_dev/modonomicon/bookstate/visibility/DefaultBookVisibilityService.java` - unchanged; it continues to read legacy unlock state, which will now be influenced by the research-backed condition during the legacy update pass
- `common/src/main/java/com/klikli_dev/modonomicon/datagen/book/demo/features/ConditionRootEntry.java` - remains always visible and acts only as hook source
- `common/src/main/java/com/klikli_dev/modonomicon/datagen/book/demo/features/ConditionLevel1Entry.java` - no direct file change expected if condition stays assigned in `FeaturesCategory.java`
- `common/src/main/java/com/klikli_dev/modonomicon/datagen/book/demo/features/ConditionLevel2Entry.java` - no direct file change expected if condition stays assigned in `FeaturesCategory.java`

## Task 1: Add the Core Research State Object

**Files:**
- Create: `common/src/main/java/com/klikli_dev/modonomicon/research/state/PlayerResearchState.java`

- [ ] **Step 1: Create the minimal research state class**

Create `common/src/main/java/com/klikli_dev/modonomicon/research/state/PlayerResearchState.java`.

```java
package com.klikli_dev.modonomicon.research.state;

import com.klikli_dev.modonomicon.util.Codecs;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.resources.Identifier;

import java.util.List;
import java.util.Set;

public class PlayerResearchState {

    public static final Codec<PlayerResearchState> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codecs.set(Identifier.CODEC).optionalFieldOf("facts", Set.of()).forGetter(PlayerResearchState::factIds),
            Codecs.set(Identifier.CODEC).optionalFieldOf("unlocked_nodes", Set.of()).forGetter(PlayerResearchState::unlockedNodeIds)
    ).apply(instance, PlayerResearchState::new));

    private final Set<Identifier> factIds;
    private final Set<Identifier> unlockedNodeIds;

    public PlayerResearchState() {
        this(Set.of(), Set.of());
    }

    public PlayerResearchState(Set<Identifier> factIds, Set<Identifier> unlockedNodeIds) {
        this.factIds = new ObjectOpenHashSet<>(factIds);
        this.unlockedNodeIds = new ObjectOpenHashSet<>(unlockedNodeIds);
    }

    public boolean grantFact(Identifier factId) {
        return this.factIds.add(factId);
    }

    public boolean hasFact(Identifier factId) {
        return this.factIds.contains(factId);
    }

    public boolean isNodeUnlocked(Identifier nodeId) {
        return this.unlockedNodeIds.contains(nodeId);
    }

    public boolean reevaluate(List<NodeRule> nodeRules) {
        boolean changed = false;
        for (var rule : nodeRules) {
            if (this.factIds.containsAll(rule.requiredFactIds())) {
                changed |= this.unlockedNodeIds.add(rule.nodeId());
            }
        }
        return changed;
    }

    public Set<Identifier> factIds() {
        return Set.copyOf(this.factIds);
    }

    public Set<Identifier> unlockedNodeIds() {
        return Set.copyOf(this.unlockedNodeIds);
    }

    public record NodeRule(Identifier nodeId, List<Identifier> requiredFactIds) {
    }
}
```

- [ ] **Step 2: Add reset support before any manager code depends on it**

Add this method to `PlayerResearchState.java`.

```java
public void reset() {
    this.factIds.clear();
    this.unlockedNodeIds.clear();
}
```

- [ ] **Step 3: Compile the common module**

Run: `./gradlew.bat :common:compileJava`

Expected: BUILD SUCCESSFUL.

- [ ] **Step 4: Commit**

Run:

```bash
git add common/src/main/java/com/klikli_dev/modonomicon/research/state/PlayerResearchState.java
git commit -m "feat: add player research state"
```

## Task 2: Add Separate Research Save Data and Manager

**Files:**
- Create: `common/src/main/java/com/klikli_dev/modonomicon/research/state/ResearchStatesSaveData.java`
- Create: `common/src/main/java/com/klikli_dev/modonomicon/research/state/ResearchStateManager.java`
- Create: `common/src/main/java/com/klikli_dev/modonomicon/research/ResearchServices.java`
- Modify: `fabric/src/main/java/com/klikli_dev/modonomicon/ModonomiconFabric.java`
- Modify: `neo/src/main/java/com/klikli_dev/modonomicon/ModonomiconNeo.java`

- [ ] **Step 1: Implement the new manager and save-data classes**

Create the three new classes.

Create `ResearchStatesSaveData.java`.

```java
package com.klikli_dev.modonomicon.research.state;

import com.klikli_dev.modonomicon.Modonomicon;
import com.klikli_dev.modonomicon.util.Codecs;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.Object2ObjectMaps;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.resources.Identifier;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;

import java.util.Map;
import java.util.UUID;

public class ResearchStatesSaveData extends SavedData {
    public static final Identifier ID = Modonomicon.loc("research_states");

    public static final Codec<ResearchStatesSaveData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.unboundedMap(Codecs.UUID, PlayerResearchState.CODEC).fieldOf("playerResearchStates").forGetter(data -> data.playerResearchStates)
    ).apply(instance, ResearchStatesSaveData::new));

    public static final SavedDataType<ResearchStatesSaveData> TYPE = new SavedDataType<>(
            ID,
            ResearchStatesSaveData::new,
            CODEC,
            DataFixTypes.PLAYER
    );

    public final Map<UUID, PlayerResearchState> playerResearchStates;

    public ResearchStatesSaveData() {
        this(Object2ObjectMaps.emptyMap());
    }

    public ResearchStatesSaveData(Map<UUID, PlayerResearchState> playerResearchStates) {
        this.playerResearchStates = Object2ObjectMaps.synchronize(new Object2ObjectOpenHashMap<>(playerResearchStates));
        this.setDirty();
    }

    public PlayerResearchState getFor(UUID playerId) {
        return this.playerResearchStates.computeIfAbsent(playerId, ignored -> {
            this.setDirty();
            return new PlayerResearchState();
        });
    }
}
```

Create `ResearchStateManager.java`.

```java
package com.klikli_dev.modonomicon.research.state;

import com.klikli_dev.modonomicon.networking.RequestSyncBookStatesMessage;
import com.klikli_dev.modonomicon.platform.Services;
import com.klikli_dev.modonomicon.research.data.ResearchDataManager;
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

    public boolean grantFact(ServerPlayer player, net.minecraft.resources.Identifier factId) {
        boolean changed = this.getStateFor(player).grantFact(factId);
        if (changed) {
            this.saveData.setDirty();
        }
        return changed;
    }

    public boolean reevaluate(ServerPlayer player) {
        boolean changed = this.getStateFor(player).reevaluate(ResearchDataManager.get().nodeRules());
        if (changed) {
            this.saveData.setDirty();
        }
        return changed;
    }

    public boolean isNodeUnlocked(Player player, net.minecraft.resources.Identifier nodeId) {
        return this.getStateFor(player).isNodeUnlocked(nodeId);
    }

    public void resetFor(ServerPlayer player) {
        this.getStateFor(player).reset();
        this.saveData.setDirty();
    }

    public void clearCachedSaveData() {
        this.saveData = null;
    }

    public void onDatapackSync(ServerPlayer player) {
        this.getSaveDataIfNecessary(player);
    }

    public void onServerTickEnd(MinecraftServer server) {
    }

    private void getSaveDataIfNecessary(Player player) {
        if (this.saveData == null) {
            if (player instanceof ServerPlayer serverPlayer) {
                this.saveData = serverPlayer.level().getServer().overworld().getDataStorage().computeIfAbsent(ResearchStatesSaveData.TYPE);
            } else {
                Services.NETWORK.sendToServer(RequestSyncBookStatesMessage.INSTANCE);
                this.saveData = new ResearchStatesSaveData();
            }
        }
    }
}
```

Create `ResearchServices.java`.

```java
package com.klikli_dev.modonomicon.research;

import com.klikli_dev.modonomicon.research.hook.ResearchHookService;
import com.klikli_dev.modonomicon.research.state.ResearchStateManager;

public final class ResearchServices {
    private static final ResearchStateManager STATE = ResearchStateManager.get();
    private static final ResearchHookService HOOKS = new ResearchHookService(STATE);

    private ResearchServices() {
    }

    public static ResearchStateManager state() {
        return STATE;
    }

    public static ResearchHookService hooks() {
        return HOOKS;
    }
}
```

- [ ] **Step 2: Register save-data lifecycle hooks on Fabric and NeoForge**

Update the loader entrypoints.

In `ModonomiconFabric.java` add imports and lines:

```java
import com.klikli_dev.modonomicon.research.data.ResearchDataManager;
import com.klikli_dev.modonomicon.research.state.ResearchStateManager;
```

Add after the multiblock reload listener:

```java
ResourceLoader.get(PackType.SERVER_DATA).registerReloadListener(Modonomicon.loc("research_data_manager"), (sharedState, executor, barrier, applyExecutor) -> {
    ResearchDataManager.get().registries(sharedState.get(ResourceLoader.REGISTRY_LOOKUP_KEY));
    return ResearchDataManager.get().reload(sharedState, executor, barrier, applyExecutor);
});
```

Add inside datapack sync:

```java
ResearchDataManager.get().onDatapackSync(player);
ResearchStateManager.get().onDatapackSync(player);
```

Add inside join sync:

```java
ResearchStateManager.get().onDatapackSync(handler.getPlayer());
```

Add inside overworld unload:

```java
ResearchStateManager.get().clearCachedSaveData();
```

Add inside end server tick:

```java
ResearchStateManager.get().onServerTickEnd(server);
```

In `ModonomiconNeo.java` add imports and lines:

```java
import com.klikli_dev.modonomicon.research.data.ResearchDataManager;
import com.klikli_dev.modonomicon.research.state.ResearchStateManager;
```

Add in `AddServerReloadListenersEvent`:

```java
ResearchDataManager.get().registries(e.getRegistryAccess());
e.addListener(Modonomicon.loc("research_data_manager"), ResearchDataManager.get());
```

Add in datapack sync:

```java
ResearchDataManager.get().onDatapackSync(e.getPlayer());
ResearchStateManager.get().onDatapackSync(e.getPlayer());
```

Add in player join:

```java
ResearchStateManager.get().onDatapackSync(player);
```

Add in level unload:

```java
ResearchStateManager.get().clearCachedSaveData();
```

Add in server tick:

```java
ResearchStateManager.get().onServerTickEnd(e.getServer());
```

- [ ] **Step 3: Compile the common module and loader entrypoints**

Run: `./gradlew.bat :common:compileJava`

Expected: BUILD SUCCESSFUL.

- [ ] **Step 4: Commit**

Run:

```bash
git add common/src/main/java/com/klikli_dev/modonomicon/research/state/PlayerResearchState.java common/src/main/java/com/klikli_dev/modonomicon/research/state/ResearchStatesSaveData.java common/src/main/java/com/klikli_dev/modonomicon/research/state/ResearchStateManager.java common/src/main/java/com/klikli_dev/modonomicon/research/ResearchServices.java fabric/src/main/java/com/klikli_dev/modonomicon/ModonomiconFabric.java neo/src/main/java/com/klikli_dev/modonomicon/ModonomiconNeo.java
git commit -m "feat: add research state persistence"
```

## Task 3: Add Research Datapack Resources and Validation

**Files:**
- Create: `common/src/main/java/com/klikli_dev/modonomicon/research/data/ResearchFactDefinition.java`
- Create: `common/src/main/java/com/klikli_dev/modonomicon/research/data/ResearchNodeDefinition.java`
- Create: `common/src/main/java/com/klikli_dev/modonomicon/research/data/ResearchHookDefinition.java`
- Create: `common/src/main/java/com/klikli_dev/modonomicon/research/data/ResearchData.java`
- Create: `common/src/main/java/com/klikli_dev/modonomicon/research/data/ResearchDataManager.java`
- Modify: `common/src/main/java/com/klikli_dev/modonomicon/api/ModonomiconConstants.java`

- [ ] **Step 1: Add the research resource model and validation**

Use Mojang codecs as the only loading path for this slice. Do not hand-parse JSON fields. Each authored research resource type should expose a `Codec`, and `ResearchDataManager` should decode datapack payloads through those codecs during reload.

Create `ResearchFactDefinition.java`.

```java
package com.klikli_dev.modonomicon.research.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.Identifier;

public record ResearchFactDefinition(Identifier id) {
    public static final Codec<ResearchFactDefinition> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Identifier.CODEC.fieldOf("id").forGetter(ResearchFactDefinition::id)
    ).apply(instance, ResearchFactDefinition::new));
}
```

Create `ResearchNodeDefinition.java`.

```java
package com.klikli_dev.modonomicon.research.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.Identifier;

import java.util.List;

public record ResearchNodeDefinition(Identifier id, List<Identifier> requiredFacts) {
    public static final Codec<ResearchNodeDefinition> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Identifier.CODEC.fieldOf("id").forGetter(ResearchNodeDefinition::id),
            Identifier.CODEC.listOf().fieldOf("required_facts").forGetter(ResearchNodeDefinition::requiredFacts)
    ).apply(instance, ResearchNodeDefinition::new));
}
```

Create `ResearchHookDefinition.java`.

```java
package com.klikli_dev.modonomicon.research.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.Identifier;

public record ResearchHookDefinition(Identifier id, TriggerType triggerType, Identifier triggerTargetId, Identifier factId) {
    public static final Codec<ResearchHookDefinition> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Identifier.CODEC.fieldOf("id").forGetter(ResearchHookDefinition::id),
            TriggerType.CODEC.fieldOf("trigger_type").forGetter(ResearchHookDefinition::triggerType),
            Identifier.CODEC.fieldOf("event_target_id").forGetter(ResearchHookDefinition::triggerTargetId),
            Identifier.CODEC.fieldOf("fact_id").forGetter(ResearchHookDefinition::factId)
    ).apply(instance, ResearchHookDefinition::new));

    public enum TriggerType {
        ENTRY_VIEWED_ONCE("entry_viewed_once");

        public static final Codec<TriggerType> CODEC = Codec.STRING.xmap(TriggerType::fromSerializedName, TriggerType::serializedName);

        private final String serializedName;

        TriggerType(String serializedName) {
            this.serializedName = serializedName;
        }

        public String serializedName() {
            return this.serializedName;
        }

        public static TriggerType fromSerializedName(String name) {
            if (ENTRY_VIEWED_ONCE.serializedName.equals(name)) {
                return ENTRY_VIEWED_ONCE;
            }
            throw new IllegalArgumentException("Unsupported research trigger type: " + name);
        }
    }
}
```

Create `ResearchData.java`.

```java
package com.klikli_dev.modonomicon.research.data;

import com.klikli_dev.modonomicon.research.state.PlayerResearchState;
import net.minecraft.resources.Identifier;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public record ResearchData(
        Set<Identifier> factIds,
        List<PlayerResearchState.NodeRule> nodeRules,
        Map<Identifier, List<ResearchHookDefinition>> entryViewedOnceHooks
) {

    public static ResearchData validate(Set<ResearchFactDefinition> facts, List<ResearchNodeDefinition> nodes, List<ResearchHookDefinition> hooks) {
        var factIds = facts.stream().map(ResearchFactDefinition::id).collect(Collectors.toSet());

        for (var node : nodes) {
            for (var factId : node.requiredFacts()) {
                if (!factIds.contains(factId)) {
                    throw new IllegalArgumentException("Unknown fact '" + factId + "' referenced by node '" + node.id() + "'");
                }
            }
        }

        for (var hook : hooks) {
            if (!factIds.contains(hook.factId())) {
                throw new IllegalArgumentException("Unknown fact '" + hook.factId() + "' referenced by hook '" + hook.id() + "'");
            }
        }

        var nodeRules = nodes.stream()
                .map(node -> new PlayerResearchState.NodeRule(node.id(), node.requiredFacts()))
                .toList();

        var groupedHooks = hooks.stream()
                .collect(Collectors.groupingBy(ResearchHookDefinition::triggerTargetId));

        return new ResearchData(Set.copyOf(factIds), nodeRules, groupedHooks);
    }
}
```

Create `ResearchDataManager.java`.

```java
package com.klikli_dev.modonomicon.research.data;

import com.google.gson.JsonElement;
import com.klikli_dev.modonomicon.api.ModonomiconConstants.Data;
import com.mojang.serialization.JsonOps;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.profiling.ProfilerFiller;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class ResearchDataManager extends SimpleJsonResourceReloadListener<JsonElement> {
    private static final ResearchDataManager INSTANCE = new ResearchDataManager();

    private HolderLookup.Provider registries;
    private ResearchData data = new ResearchData(Set.of(), List.of(), Map.of());

    private ResearchDataManager() {
        super(ExtraCodecs.JSON, FileToIdConverter.json(Data.RESEARCH_DATA_PATH));
    }

    public static ResearchDataManager get() {
        return INSTANCE;
    }

    public void registries(HolderLookup.Provider registries) {
        this.registries = registries;
    }

    public List<PlayerResearchState.NodeRule> nodeRules() {
        return this.data.nodeRules();
    }

    public List<ResearchHookDefinition> entryViewedOnceHooksFor(net.minecraft.resources.Identifier entryId) {
        return this.data.entryViewedOnceHooks().getOrDefault(entryId, List.of());
    }

    public void onDatapackSync(ServerPlayer player) {
    }

    @Override
    protected void apply(Map<net.minecraft.resources.Identifier, JsonElement> elements, ResourceManager resourceManager, ProfilerFiller profiler) {
        Set<ResearchFactDefinition> facts = Set.of();
        List<ResearchNodeDefinition> nodes = List.of();
        List<ResearchHookDefinition> hooks = List.of();

        for (var element : elements.entrySet()) {
            var json = element.getValue();
            String path = element.getKey().getPath();
            if (path.endsWith("facts")) {
                facts = Set.copyOf(ResearchFactDefinition.CODEC.listOf().parse(this.registries.createSerializationContext(JsonOps.INSTANCE), json).getOrThrow());
            } else if (path.endsWith("nodes")) {
                nodes = ResearchNodeDefinition.CODEC.listOf().parse(this.registries.createSerializationContext(JsonOps.INSTANCE), json).getOrThrow();
            } else if (path.endsWith("hooks")) {
                hooks = ResearchHookDefinition.CODEC.listOf().parse(this.registries.createSerializationContext(JsonOps.INSTANCE), json).getOrThrow();
            }
        }

        this.data = ResearchData.validate(facts, nodes, hooks);
    }
}
```

Modify `ModonomiconConstants.java` to add the folder constant.

```java
public static final String RESEARCH_DATA_PATH = ModonomiconAPI.ID + "/research";
```

- [ ] **Step 2: Compile the common module**

Run: `./gradlew.bat :common:compileJava`

Expected: BUILD SUCCESSFUL.

- [ ] **Step 3: Commit**

Run:

```bash
git add common/src/main/java/com/klikli_dev/modonomicon/research/data/ResearchFactDefinition.java common/src/main/java/com/klikli_dev/modonomicon/research/data/ResearchNodeDefinition.java common/src/main/java/com/klikli_dev/modonomicon/research/data/ResearchHookDefinition.java common/src/main/java/com/klikli_dev/modonomicon/research/data/ResearchData.java common/src/main/java/com/klikli_dev/modonomicon/research/data/ResearchDataManager.java common/src/main/java/com/klikli_dev/modonomicon/api/ModonomiconConstants.java
git commit -m "feat: add research data loading"
```

## Task 4: Add `entry_viewed_once` Hook Handling at the Safe Integration Seam

**Files:**
- Create: `common/src/main/java/com/klikli_dev/modonomicon/research/hook/ResearchHookService.java`
- Modify: `common/src/main/java/com/klikli_dev/modonomicon/networking/BookEntryReadMessage.java`

- [ ] **Step 1: Implement the hook service**

Create `ResearchHookService.java`.

```java
package com.klikli_dev.modonomicon.research.hook;

import com.klikli_dev.modonomicon.research.data.ResearchDataManager;
import com.klikli_dev.modonomicon.research.data.ResearchHookDefinition;
import com.klikli_dev.modonomicon.research.state.ResearchStateManager;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;

import java.util.List;
import java.util.UUID;
import java.util.function.Function;

public class ResearchHookService {
    private final ResearchStateManager stateManager;
    private final Function<Identifier, List<ResearchHookDefinition>> entryViewedOnceLookup;

    public ResearchHookService(ResearchStateManager stateManager) {
        this(stateManager, ResearchDataManager.get()::entryViewedOnceHooksFor);
    }

    public ResearchHookService(ResearchStateManager stateManager, Function<Identifier, List<ResearchHookDefinition>> entryViewedOnceLookup) {
        this.stateManager = stateManager;
        this.entryViewedOnceLookup = entryViewedOnceLookup;
    }

    public boolean onEntryViewedOnce(ServerPlayer player, Identifier entryId) {
        boolean changed = false;
        for (var hook : this.entryViewedOnceLookup.apply(entryId)) {
            changed |= this.stateManager.grantFact(player, hook.factId());
        }
        changed |= this.stateManager.reevaluate(player);
        return changed;
    }

}
```

- [ ] **Step 2: Bridge the hook call into `BookEntryReadMessage` before the legacy update pass**

Update `BookEntryReadMessage.java`.

```java
import com.klikli_dev.modonomicon.research.ResearchServices;
```

Replace the `if` body with:

```java
if (BookServices.interaction().markEntryRead(player, entry)) {
    ResearchServices.hooks().onEntryViewedOnce(player, entry.getId());
    BookServices.stateAccess().updateAndSync(player);
    ModonomiconEvents.server().entryFirstRead(new EntryFirstReadEvent(entry.getBook().getId(), entry.getId()));
}
```

- [ ] **Step 3: Compile the common module**

Run: `./gradlew.bat :common:compileJava`

Expected: BUILD SUCCESSFUL.

- [ ] **Step 4: Commit**

Run:

```bash
git add common/src/main/java/com/klikli_dev/modonomicon/research/hook/ResearchHookService.java common/src/main/java/com/klikli_dev/modonomicon/networking/BookEntryReadMessage.java common/src/test/java/com/klikli_dev/modonomicon/research/hook/EntryViewedOnceResearchHookServiceTest.java
git commit -m "feat: wire entry viewed research hooks"
```

## Task 5: Add the `research_node_unlocked` Book Condition and Datagen Model

**Files:**
- Create: `common/src/main/java/com/klikli_dev/modonomicon/book/conditions/BookResearchNodeUnlockedCondition.java`
- Create: `common/src/main/java/com/klikli_dev/modonomicon/api/datagen/book/condition/BookResearchNodeUnlockedConditionModel.java`
- Modify: `common/src/main/java/com/klikli_dev/modonomicon/registry/BookConditionTypeRegistry.java`
- Modify: `common/src/main/java/com/klikli_dev/modonomicon/api/datagen/ConditionHelper.java`
- Modify: `common/src/main/java/com/klikli_dev/modonomicon/api/ModonomiconConstants.java`

- [ ] **Step 1: Implement the condition, registry entry, datagen model, and helper**

Create `BookResearchNodeUnlockedCondition.java`.

```java
package com.klikli_dev.modonomicon.book.conditions;

import com.klikli_dev.modonomicon.Modonomicon;
import com.klikli_dev.modonomicon.api.ModonomiconConstants.I18n.Tooltips;
import com.klikli_dev.modonomicon.book.conditions.context.BookConditionContext;
import com.klikli_dev.modonomicon.data.BookConditionType;
import com.klikli_dev.modonomicon.registry.BookConditionTypeRegistry;
import com.klikli_dev.modonomicon.research.ResearchServices;
import com.klikli_dev.modonomicon.util.Codecs;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;

import java.util.Optional;

public class BookResearchNodeUnlockedCondition extends BookCondition {
    public static final Identifier ID = Modonomicon.loc("research_node_unlocked");
    public static final MapCodec<BookResearchNodeUnlockedCondition> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            ComponentSerialization.CODEC.optionalFieldOf("tooltip").forGetter(condition -> Optional.ofNullable(condition.tooltip())),
            Codecs.STRICT_IDENTIFIER.fieldOf("node_id").forGetter(condition -> condition.nodeId)
    ).apply(instance, (tooltip, nodeId) -> new BookResearchNodeUnlockedCondition(tooltip.orElse(null), nodeId)));
    public static final StreamCodec<RegistryFriendlyByteBuf, BookResearchNodeUnlockedCondition> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.optional(ComponentSerialization.TRUSTED_STREAM_CODEC), condition -> Optional.ofNullable(condition.tooltip()),
            Identifier.STREAM_CODEC, condition -> condition.nodeId,
            (tooltip, nodeId) -> new BookResearchNodeUnlockedCondition(tooltip.orElse(null), nodeId)
    );

    private final Identifier nodeId;

    public BookResearchNodeUnlockedCondition(Component tooltip, Identifier nodeId) {
        super(tooltip);
        this.nodeId = nodeId;
    }

    @Override
    public BookConditionType<?> type() {
        return BookConditionTypeRegistry.RESEARCH_NODE_UNLOCKED;
    }

    @Override
    public boolean test(BookConditionContext context, Player player) {
        return ResearchServices.state().isNodeUnlocked(player, this.nodeId);
    }
}
```

Create `BookResearchNodeUnlockedConditionModel.java`.

```java
package com.klikli_dev.modonomicon.api.datagen.book.condition;

import com.klikli_dev.modonomicon.book.conditions.BookCondition;
import com.klikli_dev.modonomicon.book.conditions.BookResearchNodeUnlockedCondition;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.Identifier;

public class BookResearchNodeUnlockedConditionModel extends BookConditionModel<BookResearchNodeUnlockedConditionModel> {
    protected Identifier nodeId;

    protected BookResearchNodeUnlockedConditionModel() {
        super(BookResearchNodeUnlockedCondition.ID);
    }

    public static BookResearchNodeUnlockedConditionModel create() {
        return new BookResearchNodeUnlockedConditionModel();
    }

    @Override
    public BookCondition toBookCondition(HolderLookup.Provider provider) {
        return new BookResearchNodeUnlockedCondition(this.tooltipComponent(), this.nodeId);
    }

    public BookResearchNodeUnlockedConditionModel withNode(Identifier nodeId) {
        this.nodeId = nodeId;
        return this;
    }
}
```

Modify `BookConditionTypeRegistry.java` to add:

```java
import com.klikli_dev.modonomicon.book.conditions.BookResearchNodeUnlockedCondition;
```

and register:

```java
public static final BookConditionType<BookResearchNodeUnlockedCondition> RESEARCH_NODE_UNLOCKED = register(BookResearchNodeUnlockedCondition.ID, BookResearchNodeUnlockedCondition.CODEC, BookResearchNodeUnlockedCondition.STREAM_CODEC);
```

Modify `ConditionHelper.java` to add:

```java
public BookResearchNodeUnlockedConditionModel researchNodeUnlocked(Identifier nodeId) {
    return BookResearchNodeUnlockedConditionModel.create().withNode(nodeId);
}

public BookResearchNodeUnlockedConditionModel researchNodeUnlockedBuilder(Identifier nodeId) {
    return BookResearchNodeUnlockedConditionModel.create().withNode(nodeId);
}
```

Modify `ModonomiconConstants.java` to add the tooltip key if needed:

```java
public static final String CONDITION_RESEARCH_NODE_UNLOCKED = CONDITION_PREFIX + "research_node_unlocked";
```

- [ ] **Step 2: Compile the common module**

Run: `./gradlew.bat :common:compileJava`

Expected: BUILD SUCCESSFUL.

- [ ] **Step 3: Commit**

Run:

```bash
git add common/src/main/java/com/klikli_dev/modonomicon/book/conditions/BookResearchNodeUnlockedCondition.java common/src/main/java/com/klikli_dev/modonomicon/api/datagen/book/condition/BookResearchNodeUnlockedConditionModel.java common/src/main/java/com/klikli_dev/modonomicon/registry/BookConditionTypeRegistry.java common/src/main/java/com/klikli_dev/modonomicon/api/datagen/ConditionHelper.java common/src/main/java/com/klikli_dev/modonomicon/api/ModonomiconConstants.java
git commit -m "feat: add research node unlocked condition"
```

## Task 6: Author Demo Research Data and Convert Only the Two Follow-Up Entries

**Files:**
- Modify: `common/src/main/java/com/klikli_dev/modonomicon/datagen/book/demo/FeaturesCategory.java`
- Create: `common/src/generated/resources/data/modonomicon/modonomicon/research/demo/facts.json`
- Create: `common/src/generated/resources/data/modonomicon/modonomicon/research/demo/nodes.json`
- Create: `common/src/generated/resources/data/modonomicon/modonomicon/research/demo/hooks.json`

- [ ] **Step 1: Update the demo datagen to use the new condition only for the two follow-up entries**

Change `FeaturesCategory.java`.

```java
var conditionLevel1Entry = this.add(new ConditionLevel1Entry(this).generate())
        .withCondition(this.condition().researchNodeUnlocked(Identifier.parse("modonomicon:demo/condition_level_1_unlocked")))
        .withParent(this.parent(conditionRootEntry).withLineReversed(true));

var conditionLevel2Entry = this.add(new ConditionLevel2Entry(this).generate())
        .withCondition(this.condition().researchNodeUnlocked(Identifier.parse("modonomicon:demo/condition_level_2_unlocked")))
        .withParent(conditionLevel1Entry);
```

Leave `condition_root`, `two_parents`, and the `conditional` category unchanged.

- [ ] **Step 2: Add the authored research resource payloads**

Create `common/src/generated/resources/data/modonomicon/modonomicon/research/demo/facts.json`.

```json
[
  {
    "id": "modonomicon:demo/condition_root_viewed"
  },
  {
    "id": "modonomicon:demo/condition_level_1_viewed"
  }
]
```

Create `common/src/generated/resources/data/modonomicon/modonomicon/research/demo/nodes.json`.

```json
[
  {
    "id": "modonomicon:demo/condition_level_1_unlocked",
    "required_facts": [
      "modonomicon:demo/condition_root_viewed"
    ]
  },
  {
    "id": "modonomicon:demo/condition_level_2_unlocked",
    "required_facts": [
      "modonomicon:demo/condition_level_1_viewed"
    ]
  }
]
```

Create `common/src/generated/resources/data/modonomicon/modonomicon/research/demo/hooks.json`.

```json
[
  {
    "id": "modonomicon:demo/condition_root_view_hook",
    "trigger_type": "entry_viewed_once",
    "event_target_id": "modonomicon:features/condition_root",
    "fact_id": "modonomicon:demo/condition_root_viewed"
  },
  {
    "id": "modonomicon:demo/condition_level_1_view_hook",
    "trigger_type": "entry_viewed_once",
    "event_target_id": "modonomicon:features/condition_level_1",
    "fact_id": "modonomicon:demo/condition_level_1_viewed"
  }
]
```

- [ ] **Step 3: Run data generation and compile**

Run: `./gradlew.bat runData :common:compileJava`

Expected: BUILD SUCCESSFUL and generated research resources present under `common/src/generated/resources/data/modonomicon/modonomicon/research/demo/`.

- [ ] **Step 4: Commit**

Run:

```bash
git add common/src/main/java/com/klikli_dev/modonomicon/datagen/book/demo/FeaturesCategory.java common/src/generated/resources/data/modonomicon/modonomicon/research/demo/facts.json common/src/generated/resources/data/modonomicon/modonomicon/research/demo/nodes.json common/src/generated/resources/data/modonomicon/modonomicon/research/demo/hooks.json
git commit -m "feat: migrate demo entries to research slice"
```

## Task 7: Add Nested Research Reset Command and Manual Verification Flow

**Files:**
- Create: `common/src/main/java/com/klikli_dev/modonomicon/command/ResetResearchCommand.java`
- Modify: `common/src/main/java/com/klikli_dev/modonomicon/registry/CommandRegistry.java`
- Modify: `common/src/main/java/com/klikli_dev/modonomicon/api/ModonomiconConstants.java`

- [ ] **Step 1: Add the reset command implementation under `modonomicon research reset`**

Create `ResetResearchCommand.java`.

```java
package com.klikli_dev.modonomicon.command;

import com.klikli_dev.modonomicon.api.ModonomiconConstants.I18n.Command;
import com.klikli_dev.modonomicon.research.ResearchServices;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;

public class ResetResearchCommand implements com.mojang.brigadier.Command<CommandSourceStack> {
    private static final ResetResearchCommand CMD = new ResetResearchCommand();

    public static ArgumentBuilder<CommandSourceStack, ?> register(CommandDispatcher<CommandSourceStack> dispatcher) {
        return Commands.literal("research")
                .then(Commands.literal("reset")
                        .requires(Commands.hasPermission(Commands.LEVEL_ALL))
                        .executes(CMD));
    }

    @Override
    public int run(CommandContext<CommandSourceStack> context) {
        var player = context.getSource().getPlayer();
        ResearchServices.state().resetFor(player);
        context.getSource().sendSuccess(() -> Component.translatable(Command.SUCCESS_RESET_RESEARCH), true);
        return 1;
    }
}
```

- [ ] **Step 2: Register the command and its translation key constant**

In `CommandRegistry.java` add:

```java
import com.klikli_dev.modonomicon.command.ResetResearchCommand;
```

and add it to the main command tree:

```java
.then(ResetResearchCommand.register(dispatcher))
```

In `ModonomiconConstants.java` add:

```java
public static final String SUCCESS_RESET_RESEARCH = SUCCESS_PREFIX + "research_reset";
```

- [ ] **Step 3: Compile the common module**

Run: `./gradlew.bat :common:compileJava`

Expected: `:common:compileJava` succeeds.

- [ ] **Step 4: Run the manual verification flow**

Run: `./gradlew.bat runClient`

Verify this exact flow in-game:

```text
1. Open the demo book.
2. Confirm condition_root is visible.
3. Confirm condition_level_1 is hidden.
4. Confirm condition_level_2 is hidden.
5. Open condition_root once.
6. Confirm condition_level_1 becomes visible.
7. Open condition_level_1 once.
8. Confirm condition_level_2 becomes visible.
9. Confirm two_parents still uses legacy entry_read behavior.
10. Run /modonomicon research reset.
11. Confirm condition_level_1 and condition_level_2 become hidden again.
12. Confirm unrelated visual unread state is still separate.
```

- [ ] **Step 5: Commit**

Run:

```bash
git add common/src/main/java/com/klikli_dev/modonomicon/command/ResetResearchCommand.java common/src/main/java/com/klikli_dev/modonomicon/registry/CommandRegistry.java common/src/main/java/com/klikli_dev/modonomicon/api/ModonomiconConstants.java
git commit -m "feat: add research reset command"
```

## Task 8: Final Verification and Cleanup Pass

**Files:**
- Modify: any files from earlier tasks only if verification exposed concrete defects

- [ ] **Step 1: Run the full slice verification command set**

Run:

```bash
./gradlew.bat :common:compileJava
./gradlew.bat runData
```

Expected:

```text
:common:compileJava -> BUILD SUCCESSFUL
runData -> BUILD SUCCESSFUL
```

- [ ] **Step 2: Inspect the working tree for unintended churn**

Run: `git status --short`

Expected: only the planned research, datagen, and command files are modified.

- [ ] **Step 3: Create the final verification commit if fixes were needed**

If verification required code changes, run:

```bash
git add <exact changed files>
git commit -m "fix: polish research demo entry slice"
```

If no changes were needed, skip this step.

## Notes for the Implementer

- Keep research persistence separate from `BookStatesSaveData`; do not add research fields there.
- Do not move the `conditional` category in this slice.
- Do not route `entry_viewed_once` through `ClickReadAllButtonMessage`; bind it only to the first-open path in `BookEntryReadMessage`.
- Accept the temporary adapter that legacy unlock recomputation still feeds existing UI state. Do not try to replace the whole visibility pipeline in this slice.
- If `common/src/generated/resources` is not the repo’s actual datagen target for research resources, adjust the generated file path during implementation but keep the authored ids and resource split exactly the same.

## Spec Coverage Check

- Separate research state: covered by Tasks 1-2.
- `entry_viewed_once` only: covered by Tasks 3-4.
- `research_node_unlocked` condition: covered by Task 5.
- Demo entry-only migration: covered by Task 6.
- Research-only reset and manual verification: covered by Task 7.
- No category migration and no old-system deletion: preserved throughout all tasks.
