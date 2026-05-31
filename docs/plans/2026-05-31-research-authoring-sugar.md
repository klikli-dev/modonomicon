# Research Authoring Sugar Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Add research-side typed refs, datagen builders, and provider glue so demo research content authors through a safer API while still generating the same explicit research JSON resources.

**Architecture:** Keep runtime research loading, validation, and semantics unchanged. Add a thin authoring layer under `api/datagen/research/` that wraps explicit `Identifier`s in typed refs, compiles authoring specs into the existing `Research*Definition` records, and moves demo research authoring out of the inline lists in `ResearchDataProvider`.

**Tech Stack:** Java 25, Gradle 9, Mojang codecs, Modonomicon common datagen, Fabric datagen, Neo datagen.

---

## File Map

### Create
- `common/src/main/java/com/klikli_dev/modonomicon/api/datagen/research/ResearchFactRef.java` - typed authoring-time wrapper for research fact ids.
- `common/src/main/java/com/klikli_dev/modonomicon/api/datagen/research/ResearchNodeRef.java` - typed authoring-time wrapper for research node ids.
- `common/src/main/java/com/klikli_dev/modonomicon/api/datagen/research/ResearchFactSpec.java` - compiles a fact ref into `ResearchFactDefinition`.
- `common/src/main/java/com/klikli_dev/modonomicon/api/datagen/research/ResearchNodeSpec.java` - compiles a node ref plus required fact refs into `ResearchNodeDefinition`.
- `common/src/main/java/com/klikli_dev/modonomicon/api/datagen/research/EntryViewedOnceHookSpec.java` - compiles explicit entry-viewed authoring into `ResearchHookDefinition`.
- `common/src/main/java/com/klikli_dev/modonomicon/api/datagen/research/AdvancementHookSpec.java` - compiles explicit advancement authoring into `AdvancementResearchHookDefinition`.
- `common/src/main/java/com/klikli_dev/modonomicon/api/datagen/research/ResearchDataBuilder.java` - thin builder/collector for refs and authoring specs.
- `common/src/main/java/com/klikli_dev/modonomicon/datagen/research/DemoResearchData.java` - demo research authoring migrated to the new builder API.

### Modify
- `common/src/main/java/com/klikli_dev/modonomicon/research/data/ResearchDataProvider.java` - switch from inline low-level lists to `ResearchDataBuilder` + `DemoResearchData`.
- `fabric/src/generated/resources/data/modonomicon/modonomicon/research/demo/facts.json` - regenerated explicit fact output.
- `fabric/src/generated/resources/data/modonomicon/modonomicon/research/demo/nodes.json` - regenerated explicit node output.
- `fabric/src/generated/resources/data/modonomicon/modonomicon/research/demo/hooks.json` - regenerated explicit entry-viewed hook output.
- `fabric/src/generated/resources/data/modonomicon/modonomicon/research/demo/advancement_hooks.json` - regenerated explicit advancement-hook output.
- `neo/src/generated/resources/data/modonomicon/modonomicon/research/demo/facts.json` - regenerated explicit fact output.
- `neo/src/generated/resources/data/modonomicon/modonomicon/research/demo/nodes.json` - regenerated explicit node output.
- `neo/src/generated/resources/data/modonomicon/modonomicon/research/demo/hooks.json` - regenerated explicit entry-viewed hook output.
- `neo/src/generated/resources/data/modonomicon/modonomicon/research/demo/advancement_hooks.json` - regenerated explicit advancement-hook output.

### Reuse Without Modification
- `common/src/main/java/com/klikli_dev/modonomicon/research/data/ResearchFactDefinition.java` - existing canonical fact resource shape.
- `common/src/main/java/com/klikli_dev/modonomicon/research/data/ResearchNodeDefinition.java` - existing canonical node resource shape.
- `common/src/main/java/com/klikli_dev/modonomicon/research/data/ResearchHookDefinition.java` - existing canonical `entry_viewed_once` resource shape.
- `common/src/main/java/com/klikli_dev/modonomicon/research/data/AdvancementResearchHookDefinition.java` - existing canonical advancement-hook resource shape.
- `common/src/main/java/com/klikli_dev/modonomicon/research/data/ResearchData.java` - runtime validation stays authoritative and unchanged.
- `common/src/main/java/com/klikli_dev/modonomicon/research/data/ResearchDataManager.java` - runtime loader stays unchanged.

### Verification Note
- There is currently no `src/test/java` coverage in this repo for common datagen code.
- Verification for this slice is compile + datagen + generated-resource inspection + one runtime sanity launch.

---

### Task 1: Add typed research refs

**Files:**
- Create: `common/src/main/java/com/klikli_dev/modonomicon/api/datagen/research/ResearchFactRef.java`
- Create: `common/src/main/java/com/klikli_dev/modonomicon/api/datagen/research/ResearchNodeRef.java`

- [ ] **Step 1: Create `ResearchFactRef`**

Create `ResearchFactRef.java` as a minimal typed wrapper around an explicit `Identifier`:

```java
package com.klikli_dev.modonomicon.api.datagen.research;

import net.minecraft.resources.Identifier;

public record ResearchFactRef(Identifier id) {
    public static ResearchFactRef of(Identifier id) {
        return new ResearchFactRef(id);
    }
}
```

- [ ] **Step 2: Create `ResearchNodeRef`**

Create `ResearchNodeRef.java` with the same shape for node ids:

```java
package com.klikli_dev.modonomicon.api.datagen.research;

import net.minecraft.resources.Identifier;

public record ResearchNodeRef(Identifier id) {
    public static ResearchNodeRef of(Identifier id) {
        return new ResearchNodeRef(id);
    }
}
```

- [ ] **Step 3: Compile the new ref package**

Run:

```bash
./gradlew.bat :common:compileJava
```

Expected:

```text
BUILD SUCCESSFUL
```

- [ ] **Step 4: Commit**

Run:

```bash
git add common/src/main/java/com/klikli_dev/modonomicon/api/datagen/research/ResearchFactRef.java common/src/main/java/com/klikli_dev/modonomicon/api/datagen/research/ResearchNodeRef.java
git commit -m "feat: add typed research authoring refs"
```

### Task 2: Add research authoring specs and the thin builder layer

**Files:**
- Create: `common/src/main/java/com/klikli_dev/modonomicon/api/datagen/research/ResearchFactSpec.java`
- Create: `common/src/main/java/com/klikli_dev/modonomicon/api/datagen/research/ResearchNodeSpec.java`
- Create: `common/src/main/java/com/klikli_dev/modonomicon/api/datagen/research/EntryViewedOnceHookSpec.java`
- Create: `common/src/main/java/com/klikli_dev/modonomicon/api/datagen/research/AdvancementHookSpec.java`
- Create: `common/src/main/java/com/klikli_dev/modonomicon/api/datagen/research/ResearchDataBuilder.java`

- [ ] **Step 1: Create the fact and node spec types**

Create `ResearchFactSpec.java` and `ResearchNodeSpec.java` as the compilation bridge into the existing canonical definitions:

```java
package com.klikli_dev.modonomicon.api.datagen.research;

import com.klikli_dev.modonomicon.research.data.ResearchFactDefinition;

public record ResearchFactSpec(ResearchFactRef ref) {
    public ResearchFactDefinition toDefinition() {
        return new ResearchFactDefinition(this.ref.id());
    }
}
```

```java
package com.klikli_dev.modonomicon.api.datagen.research;

import com.klikli_dev.modonomicon.research.data.ResearchNodeDefinition;

import java.util.List;

public record ResearchNodeSpec(ResearchNodeRef ref, List<ResearchFactRef> requiredFacts) {
    public ResearchNodeDefinition toDefinition() {
        return new ResearchNodeDefinition(
                this.ref.id(),
                this.requiredFacts.stream().map(ResearchFactRef::id).toList()
        );
    }
}
```

- [ ] **Step 2: Create the entry-viewed and advancement hook spec types**

Create `EntryViewedOnceHookSpec.java` and `AdvancementHookSpec.java` so both hook families compile into the existing runtime definition records:

```java
package com.klikli_dev.modonomicon.api.datagen.research;

import com.klikli_dev.modonomicon.research.data.ResearchHookDefinition;
import net.minecraft.resources.Identifier;

public record EntryViewedOnceHookSpec(Identifier id, Identifier entryId, ResearchFactRef factRef) {
    public ResearchHookDefinition toDefinition() {
        return new ResearchHookDefinition(
                this.id,
                ResearchHookDefinition.TriggerType.ENTRY_VIEWED_ONCE,
                this.entryId,
                this.factRef.id()
        );
    }
}
```

```java
package com.klikli_dev.modonomicon.api.datagen.research;

import com.klikli_dev.modonomicon.research.data.AdvancementResearchHookDefinition;
import net.minecraft.resources.Identifier;

public record AdvancementHookSpec(Identifier id, Identifier advancementId, ResearchFactRef factRef) {
    public AdvancementResearchHookDefinition toDefinition() {
        return new AdvancementResearchHookDefinition(this.id, this.advancementId, this.factRef.id());
    }
}
```

- [ ] **Step 3: Create `ResearchDataBuilder`**

Create `ResearchDataBuilder.java` as a thin builder/collector. It should own namespace-local id creation, store authoring specs, and expose compiled definition lists:

```java
package com.klikli_dev.modonomicon.api.datagen.research;

import com.klikli_dev.modonomicon.research.data.AdvancementResearchHookDefinition;
import com.klikli_dev.modonomicon.research.data.ResearchFactDefinition;
import com.klikli_dev.modonomicon.research.data.ResearchHookDefinition;
import com.klikli_dev.modonomicon.research.data.ResearchNodeDefinition;
import net.minecraft.resources.Identifier;

import java.util.ArrayList;
import java.util.List;

public final class ResearchDataBuilder {
    private final String namespace;
    private final List<ResearchFactSpec> facts = new ArrayList<>();
    private final List<ResearchNodeSpec> nodes = new ArrayList<>();
    private final List<EntryViewedOnceHookSpec> entryViewedOnceHooks = new ArrayList<>();
    private final List<AdvancementHookSpec> advancementHooks = new ArrayList<>();

    public ResearchDataBuilder(String namespace) {
        this.namespace = namespace;
    }

    public ResearchFactRef fact(String path) {
        var ref = ResearchFactRef.of(Identifier.fromNamespaceAndPath(this.namespace, path));
        this.facts.add(new ResearchFactSpec(ref));
        return ref;
    }

    public ResearchNodeRef node(String path, ResearchFactRef... requiredFacts) {
        var ref = ResearchNodeRef.of(Identifier.fromNamespaceAndPath(this.namespace, path));
        this.nodes.add(new ResearchNodeSpec(ref, List.of(requiredFacts)));
        return ref;
    }

    public void entryViewedOnce(String path, Identifier entryId, ResearchFactRef factRef) {
        this.entryViewedOnceHooks.add(new EntryViewedOnceHookSpec(
                Identifier.fromNamespaceAndPath(this.namespace, path),
                entryId,
                factRef
        ));
    }

    public void advancement(String path, Identifier advancementId, ResearchFactRef factRef) {
        this.advancementHooks.add(new AdvancementHookSpec(
                Identifier.fromNamespaceAndPath(this.namespace, path),
                advancementId,
                factRef
        ));
    }

    public List<ResearchFactDefinition> factDefinitions() {
        return this.facts.stream().map(ResearchFactSpec::toDefinition).toList();
    }

    public List<ResearchNodeDefinition> nodeDefinitions() {
        return this.nodes.stream().map(ResearchNodeSpec::toDefinition).toList();
    }

    public List<ResearchHookDefinition> hookDefinitions() {
        return this.entryViewedOnceHooks.stream().map(EntryViewedOnceHookSpec::toDefinition).toList();
    }

    public List<AdvancementResearchHookDefinition> advancementHookDefinitions() {
        return this.advancementHooks.stream().map(AdvancementHookSpec::toDefinition).toList();
    }
}
```

- [ ] **Step 4: Compile the new builder layer**

Run:

```bash
./gradlew.bat :common:compileJava
```

Expected:

```text
BUILD SUCCESSFUL
```

- [ ] **Step 5: Commit**

Run:

```bash
git add common/src/main/java/com/klikli_dev/modonomicon/api/datagen/research/ResearchFactSpec.java common/src/main/java/com/klikli_dev/modonomicon/api/datagen/research/ResearchNodeSpec.java common/src/main/java/com/klikli_dev/modonomicon/api/datagen/research/EntryViewedOnceHookSpec.java common/src/main/java/com/klikli_dev/modonomicon/api/datagen/research/AdvancementHookSpec.java common/src/main/java/com/klikli_dev/modonomicon/api/datagen/research/ResearchDataBuilder.java
git commit -m "feat: add research datagen builder layer"
```

### Task 3: Migrate demo research authoring to the new builder API

**Files:**
- Create: `common/src/main/java/com/klikli_dev/modonomicon/datagen/research/DemoResearchData.java`
- Modify: `common/src/main/java/com/klikli_dev/modonomicon/research/data/ResearchDataProvider.java`

- [ ] **Step 1: Create `DemoResearchData`**

Create `DemoResearchData.java` so the proof-case authoring moves out of the provider and uses typed refs throughout:

```java
package com.klikli_dev.modonomicon.datagen.research;

import com.klikli_dev.modonomicon.api.datagen.research.ResearchDataBuilder;
import net.minecraft.resources.Identifier;

public final class DemoResearchData {
    private DemoResearchData() {
    }

    public static void populate(ResearchDataBuilder research) {
        var conditionRootViewed = research.fact("demo/condition_root_viewed");
        var conditionLevel1Viewed = research.fact("demo/condition_level_1_viewed");
        var conditionLevel2Viewed = research.fact("demo/condition_level_2_viewed");
        var formattingBasicViewed = research.fact("demo/formatting_basic_viewed");
        var formattingAdvancedViewed = research.fact("demo/formatting_advanced_viewed");
        var featuresRecipeViewed = research.fact("demo/features_recipe_viewed");
        var featuresSpotlightViewed = research.fact("demo/features_spotlight_viewed");
        var featuresComponentIconViewed = research.fact("demo/features_component_icon_viewed");
        var featuresEmptyViewed = research.fact("demo/features_empty_viewed");
        var featuresImageViewed = research.fact("demo/features_image_viewed");
        var advancementMineStoneCompleted = research.fact("demo/advancement_mine_stone_completed");
        var advancementRideBoatWithGoatCompleted = research.fact("demo/advancement_ride_boat_with_goat_completed");

        research.node("demo/condition_level_1", conditionRootViewed);
        research.node("demo/condition_level_2", conditionLevel1Viewed);
        research.node("demo/formatting_advanced", formattingBasicViewed);
        research.node("demo/formatting_link", formattingAdvancedViewed);
        research.node("demo/features_spotlight", featuresRecipeViewed);
        research.node("demo/features_component_icon", featuresSpotlightViewed);
        research.node("demo/features_empty", featuresSpotlightViewed);
        research.node("demo/features_image", featuresEmptyViewed);
        research.node("demo/features_custom_icon", featuresImageViewed);
        research.node("demo/features_two_parents_root", conditionRootViewed);
        research.node("demo/features_two_parents_level_2", conditionLevel2Viewed);
        research.node("demo/advancement_mine_stone", advancementMineStoneCompleted);
        research.node("demo/advancement_ride_boat_with_goat", advancementRideBoatWithGoatCompleted);

        research.entryViewedOnce("demo/condition_root_viewed_once", Identifier.parse("modonomicon:features/condition_root"), conditionRootViewed);
        research.entryViewedOnce("demo/condition_level_1_viewed_once", Identifier.parse("modonomicon:features/condition_level_1"), conditionLevel1Viewed);
        research.entryViewedOnce("demo/condition_level_2_viewed_once", Identifier.parse("modonomicon:features/condition_level_2"), conditionLevel2Viewed);
        research.entryViewedOnce("demo/formatting_basic_viewed_once", Identifier.parse("modonomicon:formatting/basic"), formattingBasicViewed);
        research.entryViewedOnce("demo/formatting_advanced_viewed_once", Identifier.parse("modonomicon:formatting/advanced"), formattingAdvancedViewed);
        research.entryViewedOnce("demo/features_recipe_viewed_once", Identifier.parse("modonomicon:features/recipe"), featuresRecipeViewed);
        research.entryViewedOnce("demo/features_spotlight_viewed_once", Identifier.parse("modonomicon:features/spotlight"), featuresSpotlightViewed);
        research.entryViewedOnce("demo/features_component_icon_viewed_once", Identifier.parse("modonomicon:features/component_icon"), featuresComponentIconViewed);
        research.entryViewedOnce("demo/features_empty_viewed_once", Identifier.parse("modonomicon:features/empty"), featuresEmptyViewed);
        research.entryViewedOnce("demo/features_image_viewed_once", Identifier.parse("modonomicon:features/image"), featuresImageViewed);

        research.advancement("demo/advancement_mine_stone_completed_hook", Identifier.parse("minecraft:story/mine_stone"), advancementMineStoneCompleted);
        research.advancement("demo/advancement_ride_boat_with_goat_completed_hook", Identifier.parse("minecraft:husbandry/ride_a_boat_with_a_goat"), advancementRideBoatWithGoatCompleted);
    }
}
```

- [ ] **Step 2: Refactor `ResearchDataProvider` to compile builder output**

Replace the inline definition lists in `ResearchDataProvider.java` with a builder + compiler flow. The run method should look like this:

```java
@Override
public CompletableFuture<?> run(CachedOutput cache) {
    Path root = this.output.getOutputFolder();

    var research = new ResearchDataBuilder("modonomicon");
    DemoResearchData.populate(research);

    return CompletableFuture.allOf(
            this.save(cache, ResearchFactDefinition.CODEC, research.factDefinitions(), root.resolve("data/modonomicon/modonomicon/research/demo/facts.json")),
            this.save(cache, ResearchNodeDefinition.CODEC, research.nodeDefinitions(), root.resolve("data/modonomicon/modonomicon/research/demo/nodes.json")),
            this.save(cache, ResearchHookDefinition.CODEC, research.hookDefinitions(), root.resolve("data/modonomicon/modonomicon/research/demo/hooks.json")),
            this.save(cache, AdvancementResearchHookDefinition.CODEC, research.advancementHookDefinitions(), root.resolve("data/modonomicon/modonomicon/research/demo/advancement_hooks.json"))
    );
}

private <T> CompletableFuture<?> save(CachedOutput cache, Codec<T> codec, List<T> values, Path path) {
    return DataProvider.saveStable(cache, list(codec, values), path);
}
```

Add these imports at the top as part of the same change:

```java
import com.klikli_dev.modonomicon.api.datagen.research.ResearchDataBuilder;
import com.klikli_dev.modonomicon.datagen.research.DemoResearchData;
import com.mojang.serialization.Codec;
```

- [ ] **Step 3: Compile common, Fabric, and Neo after the provider refactor**

Run:

```bash
./gradlew.bat :common:compileJava :fabric:compileJava :neo:compileJava
```

Expected:

```text
BUILD SUCCESSFUL
```

- [ ] **Step 4: Commit**

Run:

```bash
git add common/src/main/java/com/klikli_dev/modonomicon/datagen/research/DemoResearchData.java common/src/main/java/com/klikli_dev/modonomicon/research/data/ResearchDataProvider.java
git commit -m "refactor: move demo research authoring to builder"
```

### Task 4: Regenerate explicit research resources and verify no semantic drift

**Files:**
- Modify: `fabric/src/generated/resources/data/modonomicon/modonomicon/research/demo/facts.json`
- Modify: `fabric/src/generated/resources/data/modonomicon/modonomicon/research/demo/nodes.json`
- Modify: `fabric/src/generated/resources/data/modonomicon/modonomicon/research/demo/hooks.json`
- Modify: `fabric/src/generated/resources/data/modonomicon/modonomicon/research/demo/advancement_hooks.json`
- Modify: `neo/src/generated/resources/data/modonomicon/modonomicon/research/demo/facts.json`
- Modify: `neo/src/generated/resources/data/modonomicon/modonomicon/research/demo/nodes.json`
- Modify: `neo/src/generated/resources/data/modonomicon/modonomicon/research/demo/hooks.json`
- Modify: `neo/src/generated/resources/data/modonomicon/modonomicon/research/demo/advancement_hooks.json`

- [ ] **Step 1: Run Fabric datagen**

Run:

```bash
./gradlew.bat fabric:runDatagen
```

Expected:

```text
BUILD SUCCESSFUL
```

- [ ] **Step 2: Run Neo datagen**

Run:

```bash
./gradlew.bat neo:runClientData
```

Expected:

```text
BUILD SUCCESSFUL
```

- [ ] **Step 3: Inspect the generated diffs and reject semantic drift**

Run:

```bash
git diff -- fabric/src/generated/resources/data/modonomicon/modonomicon/research/demo/facts.json fabric/src/generated/resources/data/modonomicon/modonomicon/research/demo/nodes.json fabric/src/generated/resources/data/modonomicon/modonomicon/research/demo/hooks.json fabric/src/generated/resources/data/modonomicon/modonomicon/research/demo/advancement_hooks.json neo/src/generated/resources/data/modonomicon/modonomicon/research/demo/facts.json neo/src/generated/resources/data/modonomicon/modonomicon/research/demo/nodes.json neo/src/generated/resources/data/modonomicon/modonomicon/research/demo/hooks.json neo/src/generated/resources/data/modonomicon/modonomicon/research/demo/advancement_hooks.json
```

Expected:

```text
Either no diff, or only formatting/order-preserving output changes with the same ids and dependency relationships.
```

If any id, required-fact relationship, hook target, or advancement id changed unexpectedly, stop and fix the builder normalization before continuing.

- [ ] **Step 4: Run one runtime sanity launch**

Run:

```bash
./gradlew.bat runClient
```

Expected:

```text
The client launches and the demo book still loads with the same research-driven visibility behavior.
```

- [ ] **Step 5: Commit regenerated outputs**

Run:

```bash
git add fabric/src/generated/resources/data/modonomicon/modonomicon/research/demo/facts.json fabric/src/generated/resources/data/modonomicon/modonomicon/research/demo/nodes.json fabric/src/generated/resources/data/modonomicon/modonomicon/research/demo/hooks.json fabric/src/generated/resources/data/modonomicon/modonomicon/research/demo/advancement_hooks.json neo/src/generated/resources/data/modonomicon/modonomicon/research/demo/facts.json neo/src/generated/resources/data/modonomicon/modonomicon/research/demo/nodes.json neo/src/generated/resources/data/modonomicon/modonomicon/research/demo/hooks.json neo/src/generated/resources/data/modonomicon/modonomicon/research/demo/advancement_hooks.json
git commit -m "chore: regenerate research authoring outputs"
```

### Task 5: Final full-slice verification

**Files:**
- Reuse only; no new file edits expected.

- [ ] **Step 1: Run the full compile + datagen verification batch**

Run:

```bash
./gradlew.bat :common:compileJava :fabric:compileJava :neo:compileJava fabric:runDatagen neo:runClientData
```

Expected:

```text
BUILD SUCCESSFUL
```

- [ ] **Step 2: Confirm the working tree only contains the intended slice files**

Run:

```bash
git status --short
```

Expected:

```text
No unexpected runtime or book-side glue files are modified.
```

- [ ] **Step 3: Review the final diff for boundary discipline**

Run:

```bash
git diff -- common/src/main/java/com/klikli_dev/modonomicon/api/datagen/research common/src/main/java/com/klikli_dev/modonomicon/datagen/research common/src/main/java/com/klikli_dev/modonomicon/research/data/ResearchDataProvider.java fabric/src/generated/resources/data/modonomicon/modonomicon/research/demo neo/src/generated/resources/data/modonomicon/modonomicon/research/demo
```

Expected:

```text
Diff is limited to research-side authoring refs/builders/provider glue and regenerated explicit research outputs.
```

- [ ] **Step 4: Commit any final verification-only adjustments**

If the verification pass required a small cleanup commit, run:

```bash
git add common/src/main/java/com/klikli_dev/modonomicon/api/datagen/research common/src/main/java/com/klikli_dev/modonomicon/datagen/research common/src/main/java/com/klikli_dev/modonomicon/research/data/ResearchDataProvider.java fabric/src/generated/resources/data/modonomicon/modonomicon/research/demo neo/src/generated/resources/data/modonomicon/modonomicon/research/demo
git commit -m "fix: polish research authoring builder flow"
```

If no cleanup was needed, skip this step.
