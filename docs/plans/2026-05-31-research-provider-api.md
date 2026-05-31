# Research Provider API Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Rebuild research datagen around a provider/subprovider API that mirrors the book datagen infrastructure while keeping generated research JSON and runtime behavior unchanged.

**Architecture:** Introduce `ResearchProvider` as the top-level data writer, `ResearchSubProvider` as the extension seam, and `SingleResearchSubProvider` as the normal ergonomic base. Move demo research content into a real subprovider, add platform-specific registration helpers matching the existing book wrapper pattern, and retire direct registration of the old standalone `ResearchDataProvider`.

**Tech Stack:** Java 25, Gradle 9, Mojang codecs, Modonomicon common datagen, Fabric datagen, Neo datagen, Forge datagen.

---

## File Map

### Create
- `common/src/main/java/com/klikli_dev/modonomicon/api/datagen/research/ResearchSubProvider.java` - raw top-level research datagen extension seam.
- `common/src/main/java/com/klikli_dev/modonomicon/api/datagen/research/ResearchProviderBase.java` - shared research-side authoring base with builder/ref helpers.
- `common/src/main/java/com/klikli_dev/modonomicon/api/datagen/research/ResearchBundle.java` - immutable bundle record holding one authored research root and its builder output.
- `common/src/main/java/com/klikli_dev/modonomicon/api/datagen/research/ResearchProvider.java` - top-level orchestrator/writer for research bundles.
- `common/src/main/java/com/klikli_dev/modonomicon/api/datagen/research/SingleResearchSubProvider.java` - opinionated base for one research bundle.
- `fabric/src/main/java/com/klikli_dev/modonomicon/api/datagen/FabricResearchProvider.java` - Fabric wrapper mirroring `FabricBookProvider`.
- `forge/src/main/java/com/klikli_dev/modonomicon/api/datagen/ForgeResearchProvider.java` - Forge wrapper mirroring `ForgeBookProvider`.
- `neo/src/main/java/com/klikli_dev/modonomicon/api/datagen/NeoResearchProvider.java` - Neo wrapper mirroring `NeoBookProvider`.
- `common/src/main/java/com/klikli_dev/modonomicon/datagen/research/DemoResearch.java` - demo authored research moved to a real subprovider.

### Modify
- `common/src/main/java/com/klikli_dev/modonomicon/api/datagen/research/ResearchDataBuilder.java` - expose minimal read access needed by the top-level provider if required.
- `common/src/main/java/com/klikli_dev/modonomicon/research/data/ResearchDataProvider.java` - either delete or reduce to a compatibility shim that is no longer registered.
- `fabric/src/main/java/com/klikli_dev/modonomicon/datagen/DataGenerators.java` - register the new Fabric research provider wrapper.
- `neo/src/main/java/com/klikli_dev/modonomicon/datagen/DataGenerators.java` - register the new Neo research provider wrapper.
- `forge/src/main/java/com/klikli_dev/modonomicon/datagen/DataGenerators.java` - register the new Forge research provider wrapper.
- `fabric/src/generated/resources/data/modonomicon/modonomicon/research/demo/facts.json` - regenerated explicit fact output.
- `fabric/src/generated/resources/data/modonomicon/modonomicon/research/demo/nodes.json` - regenerated explicit node output.
- `fabric/src/generated/resources/data/modonomicon/modonomicon/research/demo/hooks.json` - regenerated explicit hook output.
- `fabric/src/generated/resources/data/modonomicon/modonomicon/research/demo/advancement_hooks.json` - regenerated explicit advancement-hook output.
- `neo/src/generated/resources/data/modonomicon/modonomicon/research/demo/facts.json` - regenerated explicit fact output.
- `neo/src/generated/resources/data/modonomicon/modonomicon/research/demo/nodes.json` - regenerated explicit node output.
- `neo/src/generated/resources/data/modonomicon/modonomicon/research/demo/hooks.json` - regenerated explicit hook output.
- `neo/src/generated/resources/data/modonomicon/modonomicon/research/demo/advancement_hooks.json` - regenerated explicit advancement-hook output.

### Delete
- `common/src/main/java/com/klikli_dev/modonomicon/datagen/research/DemoResearchData.java` - replaced by the subprovider class.

### Reuse Without Modification
- `common/src/main/java/com/klikli_dev/modonomicon/api/datagen/research/ResearchFactRef.java`
- `common/src/main/java/com/klikli_dev/modonomicon/api/datagen/research/ResearchNodeRef.java`
- `common/src/main/java/com/klikli_dev/modonomicon/api/datagen/research/ResearchFactSpec.java`
- `common/src/main/java/com/klikli_dev/modonomicon/api/datagen/research/ResearchNodeSpec.java`
- `common/src/main/java/com/klikli_dev/modonomicon/api/datagen/research/EntryViewedOnceHookSpec.java`
- `common/src/main/java/com/klikli_dev/modonomicon/api/datagen/research/AdvancementHookSpec.java`
- `common/src/main/java/com/klikli_dev/modonomicon/research/data/ResearchFactDefinition.java`
- `common/src/main/java/com/klikli_dev/modonomicon/research/data/ResearchNodeDefinition.java`
- `common/src/main/java/com/klikli_dev/modonomicon/research/data/ResearchHookDefinition.java`
- `common/src/main/java/com/klikli_dev/modonomicon/research/data/AdvancementResearchHookDefinition.java`
- `common/src/main/java/com/klikli_dev/modonomicon/research/data/ResearchData.java`
- `common/src/main/java/com/klikli_dev/modonomicon/research/data/ResearchDataManager.java`

---

### Task 1: Add the research provider API seam

**Files:**
- Create: `common/src/main/java/com/klikli_dev/modonomicon/api/datagen/research/ResearchSubProvider.java`
- Create: `common/src/main/java/com/klikli_dev/modonomicon/api/datagen/research/ResearchBundle.java`
- Create: `common/src/main/java/com/klikli_dev/modonomicon/api/datagen/research/ResearchProviderBase.java`

- [ ] **Step 1: Create `ResearchSubProvider`**

Create the raw extension seam:

```java
package com.klikli_dev.modonomicon.api.datagen.research;

import net.minecraft.core.HolderLookup;
import net.minecraft.resources.Identifier;

import java.util.function.BiConsumer;

public interface ResearchSubProvider {
    void generate(BiConsumer<Identifier, ResearchBundle> consumer, HolderLookup.Provider registries);
}
```

- [ ] **Step 2: Create `ResearchBundle`**

Create a bundle record holding one research root id and the compiled builder:

```java
package com.klikli_dev.modonomicon.api.datagen.research;

public record ResearchBundle(ResearchDataBuilder data) {
}
```

The bundle id will be provided by the outer `BiConsumer<Identifier, ResearchBundle>` contract instead of being duplicated inside the record.

- [ ] **Step 3: Create `ResearchProviderBase`**

Create a narrow shared base for research authoring helpers:

```java
package com.klikli_dev.modonomicon.api.datagen.research;

import net.minecraft.core.HolderLookup;
import net.minecraft.resources.Identifier;

public abstract class ResearchProviderBase {
    protected final String modId;
    private HolderLookup.Provider registries;
    private ResearchDataBuilder research;

    protected ResearchProviderBase(String modId) {
        this.modId = modId;
    }

    protected void registries(HolderLookup.Provider registries) {
        this.registries = registries;
    }

    protected HolderLookup.Provider registries() {
        return this.registries;
    }

    protected void research(ResearchDataBuilder research) {
        this.research = research;
    }

    protected ResearchDataBuilder research() {
        return this.research;
    }

    protected Identifier modLoc(String path) {
        return Identifier.fromNamespaceAndPath(this.modId, path);
    }

    protected ResearchFactRef fact(String path) {
        return this.research.fact(path);
    }

    protected ResearchNodeRef node(String path, ResearchFactRef... requiredFacts) {
        return this.research.node(path, requiredFacts);
    }

    protected void entryViewedOnce(String path, Identifier entryId, ResearchFactRef factRef) {
        this.research.entryViewedOnce(path, entryId, factRef);
    }

    protected void advancement(String path, Identifier advancementId, ResearchFactRef factRef) {
        this.research.advancement(path, advancementId, factRef);
    }
}
```

- [ ] **Step 4: Compile the new seam and base**

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
git add common/src/main/java/com/klikli_dev/modonomicon/api/datagen/research/ResearchSubProvider.java common/src/main/java/com/klikli_dev/modonomicon/api/datagen/research/ResearchBundle.java common/src/main/java/com/klikli_dev/modonomicon/api/datagen/research/ResearchProviderBase.java
git commit -m "feat: add research provider api seam"
```

### Task 2: Add the top-level provider and opinionated subprovider base

**Files:**
- Create: `common/src/main/java/com/klikli_dev/modonomicon/api/datagen/research/ResearchProvider.java`
- Create: `common/src/main/java/com/klikli_dev/modonomicon/api/datagen/research/SingleResearchSubProvider.java`

- [ ] **Step 1: Create `SingleResearchSubProvider`**

Create the opinionated one-bundle base:

```java
package com.klikli_dev.modonomicon.api.datagen.research;

import net.minecraft.core.HolderLookup;
import net.minecraft.resources.Identifier;

import java.util.function.BiConsumer;

public abstract class SingleResearchSubProvider extends ResearchProviderBase implements ResearchSubProvider {
    private final String researchId;

    protected SingleResearchSubProvider(String researchId, String modId) {
        super(modId);
        this.researchId = researchId;
    }

    protected String researchId() {
        return this.researchId;
    }

    @Override
    public void generate(BiConsumer<Identifier, ResearchBundle> consumer, HolderLookup.Provider registries) {
        this.registries(registries);

        var research = new ResearchDataBuilder(this.modId);
        this.research(research);
        this.generateResearch();

        consumer.accept(this.modLoc(this.researchId), new ResearchBundle(research));
    }

    protected abstract void generateResearch();
}
```

- [ ] **Step 2: Create `ResearchProvider`**

Create the orchestrator/writer equivalent of `BookProvider`:

```java
package com.klikli_dev.modonomicon.api.datagen.research;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.klikli_dev.modonomicon.research.data.AdvancementResearchHookDefinition;
import com.klikli_dev.modonomicon.research.data.ResearchFactDefinition;
import com.klikli_dev.modonomicon.research.data.ResearchHookDefinition;
import com.klikli_dev.modonomicon.research.data.ResearchNodeDefinition;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class ResearchProvider implements DataProvider {
    private final String modId;
    private final PackOutput packOutput;
    private final CompletableFuture<HolderLookup.Provider> registries;
    private final List<ResearchSubProvider> subProviders;
    private final Map<Identifier, ResearchBundle> bundles = new Object2ObjectOpenHashMap<>();

    public ResearchProvider(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> registries, String modId, List<ResearchSubProvider> subProviders) {
        this.packOutput = packOutput;
        this.registries = registries;
        this.modId = modId;
        this.subProviders = subProviders;
    }

    @Override
    public @NotNull CompletableFuture<?> run(@NotNull CachedOutput cache) {
        return this.registries.thenCompose(registries -> {
            this.subProviders.forEach(subProvider -> subProvider.generate(this::add, registries));

            var futures = new ArrayList<CompletableFuture<?>>();
            Path root = this.packOutput.getOutputFolder(PackOutput.Target.DATA_PACK);

            for (var entry : this.bundles.entrySet()) {
                var id = entry.getKey();
                var data = entry.getValue().data();
                var base = root.resolve(id.getNamespace()).resolve("modonomicon/research").resolve(id.getPath());

                futures.add(this.save(cache, ResearchFactDefinition.CODEC, data.factDefinitions(), base.resolve("facts.json")));
                futures.add(this.save(cache, ResearchNodeDefinition.CODEC, data.nodeDefinitions(), base.resolve("nodes.json")));
                futures.add(this.save(cache, ResearchHookDefinition.CODEC, data.hookDefinitions(), base.resolve("hooks.json")));
                futures.add(this.save(cache, AdvancementResearchHookDefinition.CODEC, data.advancementHookDefinitions(), base.resolve("advancement_hooks.json")));
            }

            return CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new));
        });
    }

    private void add(Identifier id, ResearchBundle bundle) {
        if (this.bundles.containsKey(id)) {
            throw new IllegalStateException("Duplicate research bundle " + id);
        }
        this.bundles.put(id, bundle);
    }

    private <T> CompletableFuture<?> save(CachedOutput cache, Codec<T> codec, List<T> values, Path path) {
        return DataProvider.saveStable(cache, list(codec, values), path);
    }

    private static <T> JsonElement list(Codec<T> codec, List<T> values) {
        JsonArray array = new JsonArray();
        for (T value : values) {
            array.add(codec.encodeStart(JsonOps.INSTANCE, value).getOrThrow());
        }
        return array;
    }

    @Override
    public @NotNull String getName() {
        return "Research Data: " + this.modId;
    }
}
```

- [ ] **Step 3: Compile the new provider infrastructure**

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
git add common/src/main/java/com/klikli_dev/modonomicon/api/datagen/research/ResearchProvider.java common/src/main/java/com/klikli_dev/modonomicon/api/datagen/research/SingleResearchSubProvider.java
git commit -m "feat: add research provider infrastructure"
```

### Task 3: Move demo research content to a real subprovider

**Files:**
- Create: `common/src/main/java/com/klikli_dev/modonomicon/datagen/research/DemoResearch.java`
- Delete: `common/src/main/java/com/klikli_dev/modonomicon/datagen/research/DemoResearchData.java`
- Modify: `common/src/main/java/com/klikli_dev/modonomicon/research/data/ResearchDataProvider.java`

- [ ] **Step 1: Create `DemoResearch`**

Create the new demo subprovider as the research-side equivalent of a single book subprovider:

```java
package com.klikli_dev.modonomicon.datagen.research;

import com.klikli_dev.modonomicon.api.datagen.research.SingleResearchSubProvider;

public class DemoResearch extends SingleResearchSubProvider {
    public DemoResearch(String modId) {
        super("demo", modId);
    }

    @Override
    protected void generateResearch() {
        var conditionRootViewed = this.fact("demo/condition_root_viewed");
        var conditionLevel1Viewed = this.fact("demo/condition_level_1_viewed");
        var conditionLevel2Viewed = this.fact("demo/condition_level_2_viewed");
        var formattingBasicViewed = this.fact("demo/formatting_basic_viewed");
        var formattingAdvancedViewed = this.fact("demo/formatting_advanced_viewed");
        var featuresRecipeViewed = this.fact("demo/features_recipe_viewed");
        var featuresSpotlightViewed = this.fact("demo/features_spotlight_viewed");
        var featuresComponentIconViewed = this.fact("demo/features_component_icon_viewed");
        var featuresEmptyViewed = this.fact("demo/features_empty_viewed");
        var featuresImageViewed = this.fact("demo/features_image_viewed");
        var advancementMineStoneCompleted = this.fact("demo/advancement_mine_stone_completed");
        var advancementRideBoatWithGoatCompleted = this.fact("demo/advancement_ride_boat_with_goat_completed");

        this.node("demo/condition_level_1", conditionRootViewed);
        this.node("demo/condition_level_2", conditionLevel1Viewed);
        this.node("demo/formatting_advanced", formattingBasicViewed);
        this.node("demo/formatting_link", formattingAdvancedViewed);
        this.node("demo/features_spotlight", featuresRecipeViewed);
        this.node("demo/features_component_icon", featuresSpotlightViewed);
        this.node("demo/features_empty", featuresSpotlightViewed);
        this.node("demo/features_image", featuresEmptyViewed);
        this.node("demo/features_custom_icon", featuresImageViewed);
        this.node("demo/features_two_parents_root", conditionRootViewed);
        this.node("demo/features_two_parents_level_2", conditionLevel2Viewed);
        this.node("demo/advancement_mine_stone", advancementMineStoneCompleted);
        this.node("demo/advancement_ride_boat_with_goat", advancementRideBoatWithGoatCompleted);

        this.entryViewedOnce("demo/condition_root_viewed_once", this.modLoc("features/condition_root"), conditionRootViewed);
        this.entryViewedOnce("demo/condition_level_1_viewed_once", this.modLoc("features/condition_level_1"), conditionLevel1Viewed);
        this.entryViewedOnce("demo/condition_level_2_viewed_once", this.modLoc("features/condition_level_2"), conditionLevel2Viewed);
        this.entryViewedOnce("demo/formatting_basic_viewed_once", this.modLoc("formatting/basic"), formattingBasicViewed);
        this.entryViewedOnce("demo/formatting_advanced_viewed_once", this.modLoc("formatting/advanced"), formattingAdvancedViewed);
        this.entryViewedOnce("demo/features_recipe_viewed_once", this.modLoc("features/recipe"), featuresRecipeViewed);
        this.entryViewedOnce("demo/features_spotlight_viewed_once", this.modLoc("features/spotlight"), featuresSpotlightViewed);
        this.entryViewedOnce("demo/features_component_icon_viewed_once", this.modLoc("features/component_icon"), featuresComponentIconViewed);
        this.entryViewedOnce("demo/features_empty_viewed_once", this.modLoc("features/empty"), featuresEmptyViewed);
        this.entryViewedOnce("demo/features_image_viewed_once", this.modLoc("features/image"), featuresImageViewed);

        this.advancement("demo/advancement_mine_stone_completed_hook", net.minecraft.resources.Identifier.parse("minecraft:story/mine_stone"), advancementMineStoneCompleted);
        this.advancement("demo/advancement_ride_boat_with_goat_completed_hook", net.minecraft.resources.Identifier.parse("minecraft:husbandry/ride_a_boat_with_a_goat"), advancementRideBoatWithGoatCompleted);
    }
}
```

- [ ] **Step 2: Retire the old flat demo helper**

Delete:

```text
common/src/main/java/com/klikli_dev/modonomicon/datagen/research/DemoResearchData.java
```

- [ ] **Step 3: Replace `ResearchDataProvider` with a compatibility shim or remove its logic**

Because platform entrypoints will stop registering it after Task 4, reduce `ResearchDataProvider.java` to a deprecated compatibility shell that delegates to `ResearchProvider` with the demo subprovider:

```java
@Deprecated(forRemoval = true)
public class ResearchDataProvider extends ResearchProvider {
    public ResearchDataProvider(PackOutput output) {
        super(output, java.util.concurrent.CompletableFuture.completedFuture(net.minecraft.core.HolderLookup.Provider.create(java.util.stream.Stream.of())), "modonomicon", java.util.List.of());
        throw new UnsupportedOperationException("Use platform-specific research provider wrappers instead.");
    }
}
```

If that constructor shape is too awkward because of lookup-provider requirements, delete the class entirely in this task instead of keeping a bad shim.

- [ ] **Step 4: Compile common/fabric/neo after the migration**

Run:

```bash
./gradlew.bat :common:compileJava :fabric:compileJava :neo:compileJava
```

Expected:

```text
BUILD SUCCESSFUL
```

- [ ] **Step 5: Commit**

Run:

```bash
git add common/src/main/java/com/klikli_dev/modonomicon/datagen/research/DemoResearch.java common/src/main/java/com/klikli_dev/modonomicon/datagen/research/DemoResearchData.java common/src/main/java/com/klikli_dev/modonomicon/research/data/ResearchDataProvider.java
git commit -m "refactor: move demo research to subprovider"
```

### Task 4: Add platform-specific research provider wrappers and update registrations

**Files:**
- Create: `fabric/src/main/java/com/klikli_dev/modonomicon/api/datagen/FabricResearchProvider.java`
- Create: `forge/src/main/java/com/klikli_dev/modonomicon/api/datagen/ForgeResearchProvider.java`
- Create: `neo/src/main/java/com/klikli_dev/modonomicon/api/datagen/NeoResearchProvider.java`
- Modify: `fabric/src/main/java/com/klikli_dev/modonomicon/datagen/DataGenerators.java`
- Modify: `forge/src/main/java/com/klikli_dev/modonomicon/datagen/DataGenerators.java`
- Modify: `neo/src/main/java/com/klikli_dev/modonomicon/datagen/DataGenerators.java`

- [ ] **Step 1: Create `FabricResearchProvider`**

```java
package com.klikli_dev.modonomicon.api.datagen;

import com.klikli_dev.modonomicon.api.datagen.research.ResearchProvider;
import com.klikli_dev.modonomicon.api.datagen.research.ResearchSubProvider;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.minecraft.core.HolderLookup;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class FabricResearchProvider {
    public static FabricDataGenerator.Pack.RegistryDependentFactory<ResearchProvider> of(ResearchSubProvider... subProviders) {
        return (FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) ->
                new ResearchProvider(output, registriesFuture, output.getModId(), List.of(subProviders));
    }
}
```

- [ ] **Step 2: Create `ForgeResearchProvider` and `NeoResearchProvider`**

```java
package com.klikli_dev.modonomicon.api.datagen;

import com.klikli_dev.modonomicon.api.datagen.research.ResearchProvider;
import com.klikli_dev.modonomicon.api.datagen.research.ResearchSubProvider;
import net.minecraftforge.data.event.GatherDataEvent;

import java.util.List;

public class ForgeResearchProvider {
    public static ResearchProvider of(GatherDataEvent event, ResearchSubProvider... subProviders) {
        return new ResearchProvider(event.getGenerator().getPackOutput(), event.getLookupProvider(), event.getModContainer().getModId(), List.of(subProviders));
    }
}
```

```java
package com.klikli_dev.modonomicon.api.datagen;

import com.klikli_dev.modonomicon.api.datagen.research.ResearchProvider;
import com.klikli_dev.modonomicon.api.datagen.research.ResearchSubProvider;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.List;

public class NeoResearchProvider {
    public static ResearchProvider of(GatherDataEvent event, ResearchSubProvider... subProviders) {
        return new ResearchProvider(event.getGenerator().getPackOutput(), event.getLookupProvider(), event.getModContainer().getModId(), List.of(subProviders));
    }
}
```

- [ ] **Step 3: Update platform datagen entrypoints**

Replace direct `ResearchDataProvider` registration with the new wrappers and the demo subprovider:

In Fabric `DataGenerators.java` replace:

```java
pack.addProvider((FabricPackOutput output) -> new ResearchDataProvider(output));
```

with:

```java
pack.addProvider(FabricResearchProvider.of(new DemoResearch(Modonomicon.MOD_ID)));
```

In Neo `DataGenerators.java` replace:

```java
generator.addProvider(true, new ResearchDataProvider(generator.getPackOutput()));
```

with:

```java
generator.addProvider(true, NeoResearchProvider.of(event, new DemoResearch(Modonomicon.MOD_ID)));
```

In Forge `DataGenerators.java` replace:

```java
generator.addProvider(event.includeServer(), new ResearchDataProvider(generator.getPackOutput()));
```

with:

```java
generator.addProvider(event.includeServer(), ForgeResearchProvider.of(event, new DemoResearch(Modonomicon.MOD_ID)));
```

Also update imports accordingly.

- [ ] **Step 4: Compile common/fabric/neo/forge after registration changes**

Run:

```bash
./gradlew.bat :common:compileJava :fabric:compileJava :neo:compileJava :forge:compileJava
```

Expected:

```text
BUILD SUCCESSFUL
```

- [ ] **Step 5: Commit**

Run:

```bash
git add fabric/src/main/java/com/klikli_dev/modonomicon/api/datagen/FabricResearchProvider.java forge/src/main/java/com/klikli_dev/modonomicon/api/datagen/ForgeResearchProvider.java neo/src/main/java/com/klikli_dev/modonomicon/api/datagen/NeoResearchProvider.java fabric/src/main/java/com/klikli_dev/modonomicon/datagen/DataGenerators.java forge/src/main/java/com/klikli_dev/modonomicon/datagen/DataGenerators.java neo/src/main/java/com/klikli_dev/modonomicon/datagen/DataGenerators.java
git commit -m "feat: register research subproviders in datagen"
```

### Task 5: Regenerate research outputs and verify scope

**Files:**
- Modify: `fabric/src/generated/resources/data/modonomicon/modonomicon/research/demo/facts.json`
- Modify: `fabric/src/generated/resources/data/modonomicon/modonomicon/research/demo/nodes.json`
- Modify: `fabric/src/generated/resources/data/modonomicon/modonomicon/research/demo/hooks.json`
- Modify: `fabric/src/generated/resources/data/modonomicon/modonomicon/research/demo/advancement_hooks.json`
- Modify: `neo/src/generated/resources/data/modonomicon/modonomicon/research/demo/facts.json`
- Modify: `neo/src/generated/resources/data/modonomicon/modonomicon/research/demo/nodes.json`
- Modify: `neo/src/generated/resources/data/modonomicon/modonomicon/research/demo/hooks.json`
- Modify: `neo/src/generated/resources/data/modonomicon/modonomicon/research/demo/advancement_hooks.json`

- [ ] **Step 1: Run Fabric and Neo datagen**

Run:

```bash
./gradlew.bat fabric:runDatagen neo:runClientData
```

Expected:

```text
BUILD SUCCESSFUL
```

- [ ] **Step 2: Check generated research diff only**

Run:

```bash
git diff -- fabric/src/generated/resources/data/modonomicon/modonomicon/research/demo/facts.json fabric/src/generated/resources/data/modonomicon/modonomicon/research/demo/nodes.json fabric/src/generated/resources/data/modonomicon/modonomicon/research/demo/hooks.json fabric/src/generated/resources/data/modonomicon/modonomicon/research/demo/advancement_hooks.json neo/src/generated/resources/data/modonomicon/modonomicon/research/demo/facts.json neo/src/generated/resources/data/modonomicon/modonomicon/research/demo/nodes.json neo/src/generated/resources/data/modonomicon/modonomicon/research/demo/hooks.json neo/src/generated/resources/data/modonomicon/modonomicon/research/demo/advancement_hooks.json
```

Expected:

```text
Either no diff, or only non-semantic ordering/formatting changes.
```

- [ ] **Step 3: Restore unrelated generated files if datagen touched them**

If Fabric or Neo datagen touched unrelated files such as generated lang assets, restore them before continuing:

```bash
git restore -- fabric/src/generated/resources/assets/modonomicon/lang/en_us.json
git restore -- neo/src/generated/resources/assets/modonomicon/lang/en_us.json
```

Only restore files that were changed out of scope.

- [ ] **Step 4: Run final scoped verification**

Run:

```bash
./gradlew.bat :common:compileJava :fabric:compileJava :neo:compileJava :forge:compileJava fabric:runDatagen neo:runClientData
git status --short
git diff -- common/src/main/java/com/klikli_dev/modonomicon/api/datagen/research common/src/main/java/com/klikli_dev/modonomicon/datagen/research fabric/src/main/java/com/klikli_dev/modonomicon/api/datagen/FabricResearchProvider.java forge/src/main/java/com/klikli_dev/modonomicon/api/datagen/ForgeResearchProvider.java neo/src/main/java/com/klikli_dev/modonomicon/api/datagen/NeoResearchProvider.java fabric/src/main/java/com/klikli_dev/modonomicon/datagen/DataGenerators.java forge/src/main/java/com/klikli_dev/modonomicon/datagen/DataGenerators.java neo/src/main/java/com/klikli_dev/modonomicon/datagen/DataGenerators.java fabric/src/generated/resources/data/modonomicon/modonomicon/research/demo neo/src/generated/resources/data/modonomicon/modonomicon/research/demo
```

Expected:

```text
Compile/datagen succeeds and the remaining diff is limited to the research provider API refactor.
```

- [ ] **Step 5: Commit regenerated outputs and any final cleanup**

Run:

```bash
git add common/src/main/java/com/klikli_dev/modonomicon/api/datagen/research common/src/main/java/com/klikli_dev/modonomicon/datagen/research fabric/src/main/java/com/klikli_dev/modonomicon/api/datagen/FabricResearchProvider.java forge/src/main/java/com/klikli_dev/modonomicon/api/datagen/ForgeResearchProvider.java neo/src/main/java/com/klikli_dev/modonomicon/api/datagen/NeoResearchProvider.java fabric/src/main/java/com/klikli_dev/modonomicon/datagen/DataGenerators.java forge/src/main/java/com/klikli_dev/modonomicon/datagen/DataGenerators.java neo/src/main/java/com/klikli_dev/modonomicon/datagen/DataGenerators.java fabric/src/generated/resources/data/modonomicon/modonomicon/research/demo neo/src/generated/resources/data/modonomicon/modonomicon/research/demo
git commit -m "refactor: align research datagen with provider api"
```
