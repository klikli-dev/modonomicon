# Book Research Glue and Generated Entry-Hierarchy Progression Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Add typed book-side research condition glue plus a per-book compiler that generates canonical `entry_viewed_once` research from entry parent relationships.

**Architecture:** Keep runtime progression authority in research. Extend the book datagen models with typed `ResearchNodeRef` helpers, add a datagen-only `BookHierarchyResearchCompiler` that traverses `BookModel` entry trees, and have `BookProvider` write the generated canonical research JSON beside the normal book JSON. Because generated research now lives in its own bundle path, update research loading to aggregate all bundle files instead of only the last `facts.json` / `nodes.json` / `hooks.json` seen.

**Tech Stack:** Java 25, Gradle 9, Modonomicon common datagen API, Fabric datagen, NeoForge datagen, Mojang codecs.

---

## File Map

### Create
- `common/src/main/java/com/klikli_dev/modonomicon/api/datagen/BookHierarchyResearchCompiler.java` - datagen-only compiler that turns eligible book entry parent links into canonical research facts, hooks, nodes, and generated child visibility conditions.
- `common/src/main/java/com/klikli_dev/modonomicon/datagen/research/DemoResearchRefs.java` - typed `ResearchNodeRef` catalog for the explicit demo nodes that remain authored manually.

### Modify
- `common/src/main/java/com/klikli_dev/modonomicon/api/datagen/ConditionHelper.java` - overload `researchNodeUnlocked(...)` for `ResearchNodeRef`.
- `common/src/main/java/com/klikli_dev/modonomicon/api/datagen/book/BookModel.java` - add the per-book `generateEntryHierarchyResearch` opt-in flag.
- `common/src/main/java/com/klikli_dev/modonomicon/api/datagen/book/BookCategoryModel.java` - overload `withCondition(...)` for `ResearchNodeRef`.
- `common/src/main/java/com/klikli_dev/modonomicon/api/datagen/book/BookEntryModel.java` - overload `withCondition(...)` for `ResearchNodeRef` and expose a small helper the compiler can use to skip explicit conditions.
- `common/src/main/java/com/klikli_dev/modonomicon/api/datagen/book/page/BookPageModel.java` - overload `withCondition(...)` for `ResearchNodeRef`.
- `common/src/main/java/com/klikli_dev/modonomicon/api/datagen/BookProvider.java` - invoke the compiler, write generated research bundles, then write book JSON.
- `common/src/main/java/com/klikli_dev/modonomicon/research/data/ResearchDataManager.java` - aggregate all `facts.json`, `nodes.json`, `hooks.json`, and `advancement_hooks.json` files across all research bundle folders.
- `common/src/main/java/com/klikli_dev/modonomicon/research/data/ResearchData.java` - reject duplicate fact/node/hook ids clearly during validation.
- `common/src/main/java/com/klikli_dev/modonomicon/datagen/book/DemoBook.java` - enable the per-book opt-in and switch the conditional category to typed refs.
- `common/src/main/java/com/klikli_dev/modonomicon/datagen/book/demo/FormattingCategory.java` - remove explicit entry conditions that should now be generated from parents.
- `common/src/main/java/com/klikli_dev/modonomicon/datagen/book/demo/FeaturesCategory.java` - remove explicit child-entry conditions that should now be generated from parents while keeping parent topology intact.
- `common/src/main/java/com/klikli_dev/modonomicon/datagen/book/demo/features/ConditionAdvancementEntry.java` - switch explicit entry/page conditions to typed refs.
- `common/src/main/java/com/klikli_dev/modonomicon/datagen/research/DemoResearch.java` - keep only the explicit demo research still needed after hierarchy generation.
- `fabric/src/generated/resources/data/modonomicon/modonomicon/research/demo/*.json` - explicit demo research shrinks to the remaining manual nodes/hooks.
- `fabric/src/generated/resources/data/modonomicon/modonomicon/research/generated/demo/*.json` - new generated hierarchy research bundle.
- `fabric/src/generated/resources/data/modonomicon/modonomicon/books/demo/**/*.json` - regenerated child-entry conditions pointing at generated node ids.
- `neo/src/generated/resources/data/modonomicon/modonomicon/research/demo/*.json` - explicit demo research shrinks to the remaining manual nodes/hooks.
- `neo/src/generated/resources/data/modonomicon/modonomicon/research/generated/demo/*.json` - new generated hierarchy research bundle.
- `neo/src/generated/resources/data/modonomicon/modonomicon/books/demo/**/*.json` - regenerated child-entry conditions pointing at generated node ids.

### Reuse Without Modification
- `common/src/main/java/com/klikli_dev/modonomicon/api/datagen/research/ResearchDataBuilder.java` - already emits canonical research definitions and is reused by the book compiler.
- `common/src/main/java/com/klikli_dev/modonomicon/api/datagen/research/ResearchIngressHelper.java` - already exposes `onEntryViewedOnce(...).declareFact(...)` for generated ingress.
- `common/src/main/java/com/klikli_dev/modonomicon/api/datagen/research/ResearchNodeRef.java` - typed node refs already exist and become the glue surface for books.
- `common/src/main/java/com/klikli_dev/modonomicon/api/datagen/research/ResearchProvider.java` - explicit research provider stays unchanged; generated book research is written by `BookProvider` in the same canonical shape.
- `fabric/src/main/java/com/klikli_dev/modonomicon/datagen/DataGenerators.java` - registration order stays the same.
- `neo/src/main/java/com/klikli_dev/modonomicon/datagen/DataGenerators.java` - registration order stays the same.

### Verification Note
- There is no live `src/test/java` coverage for this area in this checkout.
- Treat `:common:compileJava`, platform compiles, platform datagen runs, generated-resource inspection, and one manual client launch as the verification stack.

---

### Task 1: Add typed book-side research condition glue

**Files:**
- Modify: `common/src/main/java/com/klikli_dev/modonomicon/api/datagen/ConditionHelper.java`
- Modify: `common/src/main/java/com/klikli_dev/modonomicon/api/datagen/book/BookCategoryModel.java`
- Modify: `common/src/main/java/com/klikli_dev/modonomicon/api/datagen/book/BookEntryModel.java`
- Modify: `common/src/main/java/com/klikli_dev/modonomicon/api/datagen/book/page/BookPageModel.java`

- [ ] **Step 1: Add the `ResearchNodeRef` overloads in `ConditionHelper`**

Add the typed-ref overloads beside the existing `Identifier` overloads:

```java
package com.klikli_dev.modonomicon.api.datagen;

import com.klikli_dev.modonomicon.api.datagen.book.BookEntryModel;
import com.klikli_dev.modonomicon.api.datagen.book.condition.*;
import com.klikli_dev.modonomicon.api.datagen.research.ResearchNodeRef;
import net.minecraft.resources.Identifier;

public class ConditionHelper {
    public BookCategoryHasVisibleEntriesConditionModel categoryHasEntries(BookEntryModel entry) {
        return BookCategoryHasVisibleEntriesConditionModel.create().withCategory(entry.getId());
    }

    public BookResearchNodeUnlockedConditionModel researchNodeUnlocked(Identifier nodeId) {
        return BookResearchNodeUnlockedConditionModel.create().withNode(nodeId);
    }

    public BookResearchNodeUnlockedConditionModel researchNodeUnlocked(ResearchNodeRef nodeRef) {
        return this.researchNodeUnlocked(nodeRef.id());
    }

    public BookResearchNodeUnlockedConditionModel researchNodeUnlockedBuilder(Identifier nodeId) {
        return BookResearchNodeUnlockedConditionModel.create().withNode(nodeId);
    }

    public BookResearchNodeUnlockedConditionModel researchNodeUnlockedBuilder(ResearchNodeRef nodeRef) {
        return this.researchNodeUnlockedBuilder(nodeRef.id());
    }
}
```

- [ ] **Step 2: Add model-level `withCondition(ResearchNodeRef)` helpers**

Add these overloads to `BookCategoryModel`, `BookEntryModel`, and `BookPageModel` so book authors can pass typed refs directly instead of building the condition manually.

Insert this overload into `BookCategoryModel`:

```java
import com.klikli_dev.modonomicon.api.datagen.research.ResearchNodeRef;
import com.klikli_dev.modonomicon.api.datagen.book.condition.BookResearchNodeUnlockedConditionModel;

public BookCategoryModel withCondition(ResearchNodeRef nodeRef) {
    return this.withCondition(BookResearchNodeUnlockedConditionModel.create().withNode(nodeRef.id()));
}
```

Insert these helpers into `BookEntryModel`:

```java
import com.klikli_dev.modonomicon.api.datagen.book.condition.BookResearchNodeUnlockedConditionModel;
import com.klikli_dev.modonomicon.api.datagen.research.ResearchNodeRef;

public boolean hasCondition() {
    return this.condition != null;
}

public BookEntryModel withCondition(ResearchNodeRef nodeRef) {
    return this.withCondition(BookResearchNodeUnlockedConditionModel.create().withNode(nodeRef.id()));
}
```

Insert this overload into `BookPageModel`:

```java
import com.klikli_dev.modonomicon.api.datagen.book.condition.BookResearchNodeUnlockedConditionModel;
import com.klikli_dev.modonomicon.api.datagen.research.ResearchNodeRef;

public T withCondition(@NotNull ResearchNodeRef nodeRef) {
    return this.withCondition(BookResearchNodeUnlockedConditionModel.create().withNode(nodeRef.id()));
}
```

- [ ] **Step 3: Compile the common source set**

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
git add common/src/main/java/com/klikli_dev/modonomicon/api/datagen/ConditionHelper.java common/src/main/java/com/klikli_dev/modonomicon/api/datagen/book/BookCategoryModel.java common/src/main/java/com/klikli_dev/modonomicon/api/datagen/book/BookEntryModel.java common/src/main/java/com/klikli_dev/modonomicon/api/datagen/book/page/BookPageModel.java
git commit -m "feat: add book-side research condition glue"
```

### Task 2: Add the per-book opt-in and the book hierarchy research compiler

**Files:**
- Modify: `common/src/main/java/com/klikli_dev/modonomicon/api/datagen/book/BookModel.java`
- Create: `common/src/main/java/com/klikli_dev/modonomicon/api/datagen/BookHierarchyResearchCompiler.java`
- Modify: `common/src/main/java/com/klikli_dev/modonomicon/api/datagen/BookProvider.java`

- [ ] **Step 1: Add the datagen-only opt-in flag on `BookModel`**

Add a field, getter, and fluent setter. Do **not** serialize this field into `book.json`; it is a datagen-only knob.

```java
protected boolean generateEntryHierarchyResearch = false;

public boolean generateEntryHierarchyResearch() {
    return this.generateEntryHierarchyResearch;
}

public BookModel withGenerateEntryHierarchyResearch(boolean value) {
    this.generateEntryHierarchyResearch = value;
    return this;
}
```

- [ ] **Step 2: Create `BookHierarchyResearchCompiler`**

Create `common/src/main/java/com/klikli_dev/modonomicon/api/datagen/BookHierarchyResearchCompiler.java` with this core structure. The important behaviors are: skip entries that already have an explicit condition, reuse one viewed-once fact per parent entry, generate one node per eligible child entry, and emit into the deterministic bundle id `generated/<book-id-path>`.

```java
package com.klikli_dev.modonomicon.api.datagen;

import com.klikli_dev.modonomicon.api.datagen.book.BookEntryModel;
import com.klikli_dev.modonomicon.api.datagen.book.BookModel;
import com.klikli_dev.modonomicon.api.datagen.book.condition.BookResearchNodeUnlockedConditionModel;
import com.klikli_dev.modonomicon.api.datagen.research.ResearchDataBuilder;
import com.klikli_dev.modonomicon.api.datagen.research.ResearchFactRef;
import com.klikli_dev.modonomicon.api.datagen.research.ResearchIngressHelper;
import net.minecraft.resources.Identifier;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public final class BookHierarchyResearchCompiler {
    public Optional<CompiledBookResearch> compile(BookModel book) {
        if (!book.generateEntryHierarchyResearch()) {
            return Optional.empty();
        }

        var research = new ResearchDataBuilder(book.getId().getNamespace());
        var ingress = new ResearchIngressHelper(research);
        var parentFacts = new HashMap<Identifier, ResearchFactRef>();
        boolean generatedAny = false;

        for (var category : book.getCategories()) {
            for (var entry : category.getEntries()) {
                if (entry.hasCondition() || entry.getParents().isEmpty()) {
                    continue;
                }

                var requiredFacts = new ArrayList<ResearchFactRef>();
                for (var parent : entry.getParents()) {
                    var parentEntryId = parent.getEntryId();
                    var factRef = parentFacts.computeIfAbsent(parentEntryId, id ->
                            ingress.onEntryViewedOnce(id).declareFact(this.factPath(book, id))
                    );
                    requiredFacts.add(factRef);
                }

                var nodeRef = research.node(this.nodePath(book, entry), requiredFacts.toArray(ResearchFactRef[]::new));
                entry.withCondition(BookResearchNodeUnlockedConditionModel.create().withNode(nodeRef.id()));
                generatedAny = true;
            }
        }

        if (!generatedAny) {
            return Optional.empty();
        }

        return Optional.of(new CompiledBookResearch(this.bundleId(book), research));
    }

    private Identifier bundleId(BookModel book) {
        return Identifier.fromNamespaceAndPath(book.getId().getNamespace(), "generated/" + book.getId().getPath());
    }

    private String factPath(BookModel book, Identifier parentEntryId) {
        return "generated/" + book.getId().getPath() + "/viewed/" + parentEntryId.getNamespace() + "/" + parentEntryId.getPath();
    }

    private String nodePath(BookModel book, BookEntryModel entry) {
        return "generated/" + book.getId().getPath() + "/unlock/" + entry.getId().getNamespace() + "/" + entry.getId().getPath();
    }

    public record CompiledBookResearch(Identifier bundleId, ResearchDataBuilder data) {
    }
}
```

- [ ] **Step 3: Teach `BookProvider` to write the generated research bundle**

Add the needed imports to `BookProvider.java`:

```java
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.klikli_dev.modonomicon.api.datagen.BookHierarchyResearchCompiler.CompiledBookResearch;
import com.klikli_dev.modonomicon.research.data.AdvancementResearchHookDefinition;
import com.klikli_dev.modonomicon.research.data.ResearchFactDefinition;
import com.klikli_dev.modonomicon.research.data.ResearchHookDefinition;
import com.klikli_dev.modonomicon.research.data.ResearchNodeDefinition;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
```

Add these helpers near the existing path methods:

```java
protected Path getResearchBasePath(Path dataFolder, Identifier bundleId) {
    return dataFolder
            .resolve(bundleId.getNamespace())
            .resolve(ModonomiconConstants.Data.RESEARCH_DATA_PATH)
            .resolve(bundleId.getPath());
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
```

Then update the top of `run(...)` so it compiles each book before writing JSON:

```java
var compiler = new BookHierarchyResearchCompiler();

for (var bookModel : this.bookModels.values()) {
    var generatedResearch = compiler.compile(bookModel);
    generatedResearch.ifPresent(compiled -> {
        Path researchBase = this.getResearchBasePath(dataFolder, compiled.bundleId());
        futures.add(this.save(cache, ResearchFactDefinition.CODEC, compiled.data().factDefinitions(), researchBase.resolve("facts.json")));
        futures.add(this.save(cache, ResearchNodeDefinition.CODEC, compiled.data().nodeDefinitions(), researchBase.resolve("nodes.json")));
        futures.add(this.save(cache, ResearchHookDefinition.CODEC, compiled.data().hookDefinitions(), researchBase.resolve("hooks.json")));
        futures.add(this.save(cache, AdvancementResearchHookDefinition.CODEC, compiled.data().advancementHookDefinitions(), researchBase.resolve("advancement_hooks.json")));
    });

    Path bookPath = this.getPath(dataFolder, bookModel);
    // keep the existing book/category/entry/page write logic unchanged below this point
}
```

- [ ] **Step 4: Compile common, Fabric, and Neo after the provider/compiler changes**

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
git add common/src/main/java/com/klikli_dev/modonomicon/api/datagen/book/BookModel.java common/src/main/java/com/klikli_dev/modonomicon/api/datagen/BookHierarchyResearchCompiler.java common/src/main/java/com/klikli_dev/modonomicon/api/datagen/BookProvider.java
git commit -m "feat: generate research from book entry hierarchy"
```

### Task 3: Aggregate multiple research bundles at load time and reject duplicate ids clearly

**Files:**
- Modify: `common/src/main/java/com/klikli_dev/modonomicon/research/data/ResearchDataManager.java`
- Modify: `common/src/main/java/com/klikli_dev/modonomicon/research/data/ResearchData.java`

- [ ] **Step 1: Aggregate all research bundle files in `ResearchDataManager`**

Replace the overwrite logic in `apply(...)` with additive collection logic so every research bundle folder contributes to the final validated dataset:

```java
import java.util.LinkedHashSet;

@Override
protected void apply(Map<Identifier, JsonElement> elements, ResourceManager resourceManager, ProfilerFiller profiler) {
    Set<ResearchFactDefinition> facts = new LinkedHashSet<>();
    List<ResearchNodeDefinition> nodes = new ArrayList<>();
    List<ResearchHookDefinition> hooks = new ArrayList<>();
    List<AdvancementResearchHookDefinition> advancementHooks = new ArrayList<>();

    for (var entry : elements.entrySet()) {
        var path = entry.getKey().getPath();
        var fileName = path.substring(path.lastIndexOf('/') + 1);
        if (fileName.equals("facts")) {
            facts.addAll(ResearchFactDefinition.CODEC.listOf().parse(JsonOps.INSTANCE, entry.getValue()).getOrThrow());
        } else if (fileName.equals("nodes")) {
            nodes.addAll(ResearchNodeDefinition.CODEC.listOf().parse(JsonOps.INSTANCE, entry.getValue()).getOrThrow());
        } else if (fileName.equals("advancement_hooks")) {
            advancementHooks.addAll(AdvancementResearchHookDefinition.CODEC.listOf().parse(JsonOps.INSTANCE, entry.getValue()).getOrThrow());
        } else if (fileName.equals("hooks")) {
            hooks.addAll(ResearchHookDefinition.CODEC.listOf().parse(JsonOps.INSTANCE, entry.getValue()).getOrThrow());
        }
    }

    this.data = ResearchData.validate(facts, nodes, hooks, advancementHooks);
}
```

- [ ] **Step 2: Add duplicate-id validation to `ResearchData`**

Add a small helper at the top of `validate(...)` so collisions from explicit + generated content fail clearly instead of disappearing into sets:

```java
import java.util.LinkedHashMap;
import java.util.function.Function;

private static <T> void requireUniqueIds(List<T> values, Function<T, Identifier> idGetter, String kind) {
    var seen = new LinkedHashMap<Identifier, Integer>();
    for (int i = 0; i < values.size(); i++) {
        var id = idGetter.apply(values.get(i));
        if (seen.putIfAbsent(id, i) != null) {
            throw new IllegalArgumentException("Duplicate " + kind + " id '" + id + "'");
        }
    }
}
```

Then call it at the start of `validate(...)`:

```java
requireUniqueIds(new ArrayList<>(facts), ResearchFactDefinition::id, "research fact");
requireUniqueIds(nodes, ResearchNodeDefinition::id, "research node");
requireUniqueIds(hooks, ResearchHookDefinition::id, "research hook");
requireUniqueIds(advancementHooks, AdvancementResearchHookDefinition::id, "advancement research hook");
```

- [ ] **Step 3: Compile common after the loader/validation changes**

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
git add common/src/main/java/com/klikli_dev/modonomicon/research/data/ResearchDataManager.java common/src/main/java/com/klikli_dev/modonomicon/research/data/ResearchData.java
git commit -m "fix: aggregate research data across bundles"
```

### Task 4: Migrate the demo book and demo research to the new authoring model

**Files:**
- Create: `common/src/main/java/com/klikli_dev/modonomicon/datagen/research/DemoResearchRefs.java`
- Modify: `common/src/main/java/com/klikli_dev/modonomicon/datagen/book/DemoBook.java`
- Modify: `common/src/main/java/com/klikli_dev/modonomicon/datagen/book/demo/FormattingCategory.java`
- Modify: `common/src/main/java/com/klikli_dev/modonomicon/datagen/book/demo/FeaturesCategory.java`
- Modify: `common/src/main/java/com/klikli_dev/modonomicon/datagen/book/demo/features/ConditionAdvancementEntry.java`
- Modify: `common/src/main/java/com/klikli_dev/modonomicon/datagen/research/DemoResearch.java`

- [ ] **Step 1: Create the typed explicit-ref catalog**

Create `DemoResearchRefs.java` for the explicit demo nodes that remain hand-authored:

```java
package com.klikli_dev.modonomicon.datagen.research;

import com.klikli_dev.modonomicon.api.datagen.research.ResearchNodeRef;
import net.minecraft.resources.Identifier;

public final class DemoResearchRefs {
    public static final ResearchNodeRef CONDITION_LEVEL_1 = ResearchNodeRef.of(Identifier.parse("modonomicon:demo/condition_level_1"));
    public static final ResearchNodeRef ADVANCEMENT_MINE_STONE = ResearchNodeRef.of(Identifier.parse("modonomicon:demo/advancement_mine_stone"));
    public static final ResearchNodeRef ADVANCEMENT_RIDE_BOAT_WITH_GOAT = ResearchNodeRef.of(Identifier.parse("modonomicon:demo/advancement_ride_boat_with_goat"));

    private DemoResearchRefs() {
    }
}
```

- [ ] **Step 2: Opt the demo book in and switch the explicit book-side conditions to typed refs**

Update `DemoBook.additionalSetup(...)` and `generateCategories()` like this:

```java
import com.klikli_dev.modonomicon.datagen.research.DemoResearchRefs;

@Override
protected BookModel additionalSetup(BookModel book) {
    return book.withModel(Identifier.parse("modonomicon:modonomicon_green"))
            .withTheme(theme -> theme.withLayout(layout -> layout
                    .withBookTextOffsetX(5)
                    .withBookTextOffsetY(0)
                    .withBookTextOffsetWidth(-5)))
            .withCommand(commandEntryCommand)
            .withCommand(commandEntryLinkCommand)
            .withAllowOpenBooksWithInvalidLinks(true)
            .withGenerateEntryHierarchyResearch(true);
}

@Override
protected void generateCategories() {
    var featuresCategory = this.add(new FeaturesCategory(this).generate());
    var formattingCategory = this.add(new FormattingCategory(this).generate());

    var conditionalCategory = this.add(new ConditionalCategory(this).generate())
            .withCondition(DemoResearchRefs.CONDITION_LEVEL_1);

    var indexModeCategory = this.add(new IndexModeCategory(this).generate());
}
```

Update `ConditionAdvancementEntry` to use the typed refs directly:

```java
import com.klikli_dev.modonomicon.datagen.research.DemoResearchRefs;

var pageCondition = this.condition().researchNodeUnlocked(DemoResearchRefs.ADVANCEMENT_MINE_STONE);

@Override
protected BookEntryModel additionalSetup(BookEntryModel entry) {
    return entry.withCondition(DemoResearchRefs.ADVANCEMENT_RIDE_BOAT_WITH_GOAT);
}
```

- [ ] **Step 3: Remove the demo entry conditions that should now be generated from parents**

Update `FormattingCategory.generateEntries()` so the parent links remain but the explicit node conditions are removed:

```java
var advancedFormattingEntry = this.add(new AdvancedFormattingEntry(this).generate())
        .withParent(this.parent(basicFormattingEntry));

var linkFormattingEntry = this.add(new LinkFormattingEntry(this).generate())
        .withParent(advancedFormattingEntry);
```

Update `FeaturesCategory.generateEntries()` so the compiler now owns the normal child progression. Replace the current blocks with these versions:

```java
var conditionLevel1Entry = this.add(new ConditionLevel1Entry(this).generate())
        .withParent(this.parent(conditionRootEntry).withLineReversed(true));

var conditionLevel2Entry = this.add(new ConditionLevel2Entry(this).generate())
        .withParent(conditionLevel1Entry);

var twoParentsEntry = this.add(new TwoParentEntry(this).generate())
        .showWhenAnyParentUnlocked(true)
        .withParent(this.parent(conditionRootEntry).withLineReversed(true))
        .withParent(conditionLevel2Entry);

var spotlightEntry = this.add(new SpotlightEntry(this).generate())
        .withParent(this.parent(recipeEntry).withLineReversed(true));

var componentIconEntry = this.add(new EntryWithComponentIcon(this).generate())
        .withParent(spotlightEntry);

var emptyEntry = this.add(new EmptyPageEntry(this).generate())
        .withParent(spotlightEntry);

var imageEntry = new ImageEntry(this).generate();
imageEntry.withParent(this.parent(emptyEntry));

var customIconEntry = this.add(new CustomIconEntry(this).generate())
        .withParent(imageEntry);
```

The explicit advancement-based condition entry stays explicit; do not touch that one beyond the typed-ref change above.

- [ ] **Step 4: Prune `DemoResearch` down to the still-explicit research**

Replace `generateResearch()` in `DemoResearch.java` with the reduced explicit set. Keep only the root-viewed condition used by the conditional category and the two advancement-backed nodes:

```java
@Override
protected void generateResearch() {
    var conditionRootViewed = this.ingress()
            .onEntryViewedOnce(this.modLoc("features/condition_root"))
            .declareFact("demo/condition_root_viewed");
    var advancementMineStoneCompleted = this.ingress()
            .onAdvancementEarned(Identifier.parse("minecraft:story/mine_stone"))
            .declareFact("demo/advancement_mine_stone_completed");
    var advancementRideBoatWithGoatCompleted = this.ingress()
            .onAdvancementEarned(Identifier.parse("minecraft:husbandry/ride_a_boat_with_a_goat"))
            .declareFact("demo/advancement_ride_boat_with_goat_completed");

    this.node("demo/condition_level_1", conditionRootViewed);
    this.node("demo/advancement_mine_stone", advancementMineStoneCompleted);
    this.node("demo/advancement_ride_boat_with_goat", advancementRideBoatWithGoatCompleted);
}
```

- [ ] **Step 5: Compile common, Fabric, and Neo after the demo migration**

Run:

```bash
./gradlew.bat :common:compileJava :fabric:compileJava :neo:compileJava
```

Expected:

```text
BUILD SUCCESSFUL
```

- [ ] **Step 6: Commit**

Run:

```bash
git add common/src/main/java/com/klikli_dev/modonomicon/datagen/research/DemoResearchRefs.java common/src/main/java/com/klikli_dev/modonomicon/datagen/book/DemoBook.java common/src/main/java/com/klikli_dev/modonomicon/datagen/book/demo/FormattingCategory.java common/src/main/java/com/klikli_dev/modonomicon/datagen/book/demo/FeaturesCategory.java common/src/main/java/com/klikli_dev/modonomicon/datagen/book/demo/features/ConditionAdvancementEntry.java common/src/main/java/com/klikli_dev/modonomicon/datagen/research/DemoResearch.java
git commit -m "refactor: migrate demo book progression to generated research"
```

### Task 5: Regenerate platform outputs and verify the generated research shape

**Files:**
- Modify: `fabric/src/generated/resources/data/modonomicon/modonomicon/research/demo/facts.json`
- Modify: `fabric/src/generated/resources/data/modonomicon/modonomicon/research/demo/nodes.json`
- Modify: `fabric/src/generated/resources/data/modonomicon/modonomicon/research/demo/hooks.json`
- Modify: `fabric/src/generated/resources/data/modonomicon/modonomicon/research/demo/advancement_hooks.json`
- Create/Modify: `fabric/src/generated/resources/data/modonomicon/modonomicon/research/generated/demo/facts.json`
- Create/Modify: `fabric/src/generated/resources/data/modonomicon/modonomicon/research/generated/demo/nodes.json`
- Create/Modify: `fabric/src/generated/resources/data/modonomicon/modonomicon/research/generated/demo/hooks.json`
- Create/Modify: `fabric/src/generated/resources/data/modonomicon/modonomicon/research/generated/demo/advancement_hooks.json`
- Modify: `fabric/src/generated/resources/data/modonomicon/modonomicon/books/demo/**/*.json`
- Modify: `neo/src/generated/resources/data/modonomicon/modonomicon/research/demo/facts.json`
- Modify: `neo/src/generated/resources/data/modonomicon/modonomicon/research/demo/nodes.json`
- Modify: `neo/src/generated/resources/data/modonomicon/modonomicon/research/demo/hooks.json`
- Modify: `neo/src/generated/resources/data/modonomicon/modonomicon/research/demo/advancement_hooks.json`
- Create/Modify: `neo/src/generated/resources/data/modonomicon/modonomicon/research/generated/demo/facts.json`
- Create/Modify: `neo/src/generated/resources/data/modonomicon/modonomicon/research/generated/demo/nodes.json`
- Create/Modify: `neo/src/generated/resources/data/modonomicon/modonomicon/research/generated/demo/hooks.json`
- Create/Modify: `neo/src/generated/resources/data/modonomicon/modonomicon/research/generated/demo/advancement_hooks.json`
- Modify: `neo/src/generated/resources/data/modonomicon/modonomicon/books/demo/**/*.json`

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

- [ ] **Step 3: Inspect the generated research diff for the explicit and generated bundles**

Run:

```bash
git diff -- fabric/src/generated/resources/data/modonomicon/modonomicon/research/demo fabric/src/generated/resources/data/modonomicon/modonomicon/research/generated/demo fabric/src/generated/resources/data/modonomicon/modonomicon/books/demo neo/src/generated/resources/data/modonomicon/modonomicon/research/demo neo/src/generated/resources/data/modonomicon/modonomicon/research/generated/demo neo/src/generated/resources/data/modonomicon/modonomicon/books/demo
```

Expected:

```text
- `research/demo/*` now contains only the remaining explicit category/advancement-backed research.
- `research/generated/demo/*` contains the generated parent-viewed facts, entry_viewed_once hooks, and child unlock nodes.
- Eligible child entry JSON files now point at generated node ids.
- Entries that kept explicit conditions, such as the advancement demo entry, still point at the explicit `demo/...` node ids.
```

If any entry with an explicit authored condition was changed to a generated condition, stop and fix the compiler skip rule before continuing.

- [ ] **Step 4: Run one manual client sanity launch**

Run:

```bash
./gradlew.bat runClient
```

Expected:

```text
The client launches, the demo book opens, generated child entries stay hidden until their parents are viewed, the two-parent demo stays locked until both parents were viewed, and the advancement-gated entry/page still use the explicit advancement research.
```

- [ ] **Step 5: Commit the regenerated outputs**

Run:

```bash
git add fabric/src/generated/resources/data/modonomicon/modonomicon/research/demo fabric/src/generated/resources/data/modonomicon/modonomicon/research/generated/demo fabric/src/generated/resources/data/modonomicon/modonomicon/books/demo neo/src/generated/resources/data/modonomicon/modonomicon/research/demo neo/src/generated/resources/data/modonomicon/modonomicon/research/generated/demo neo/src/generated/resources/data/modonomicon/modonomicon/books/demo
git commit -m "chore: regenerate book research glue outputs"
```

### Task 6: Final verification sweep

**Files:**
- Reuse only; no new edits expected.

- [ ] **Step 1: Run the full compile + datagen verification batch**

Run:

```bash
./gradlew.bat :common:compileJava :fabric:compileJava :neo:compileJava fabric:runDatagen neo:runClientData
```

Expected:

```text
BUILD SUCCESSFUL
```

- [ ] **Step 2: Confirm the working tree only contains the intended slice**

Run:

```bash
git status --short
```

Expected:

```text
Only the book glue files, compiler/loader files, demo authoring files, and regenerated generated-resource files are modified.
```

- [ ] **Step 3: Review the final diff for boundary discipline**

Run:

```bash
git diff -- common/src/main/java/com/klikli_dev/modonomicon/api/datagen common/src/main/java/com/klikli_dev/modonomicon/research/data common/src/main/java/com/klikli_dev/modonomicon/datagen/book common/src/main/java/com/klikli_dev/modonomicon/datagen/research fabric/src/generated/resources/data/modonomicon/modonomicon/books/demo fabric/src/generated/resources/data/modonomicon/modonomicon/research/demo fabric/src/generated/resources/data/modonomicon/modonomicon/research/generated/demo neo/src/generated/resources/data/modonomicon/modonomicon/books/demo neo/src/generated/resources/data/modonomicon/modonomicon/research/demo neo/src/generated/resources/data/modonomicon/modonomicon/research/generated/demo
```

Expected:

```text
Diff is limited to book-side glue, generated-hierarchy compiler output, research bundle aggregation, demo migration, and regenerated canonical assets.
```

- [ ] **Step 4: Commit any verification-only cleanup**

If the verification batch required one last tiny cleanup, run:

```bash
git add common/src/main/java/com/klikli_dev/modonomicon/api/datagen common/src/main/java/com/klikli_dev/modonomicon/research/data common/src/main/java/com/klikli_dev/modonomicon/datagen/book common/src/main/java/com/klikli_dev/modonomicon/datagen/research fabric/src/generated/resources/data/modonomicon/modonomicon/books/demo fabric/src/generated/resources/data/modonomicon/modonomicon/research/demo fabric/src/generated/resources/data/modonomicon/modonomicon/research/generated/demo neo/src/generated/resources/data/modonomicon/modonomicon/books/demo neo/src/generated/resources/data/modonomicon/modonomicon/research/demo neo/src/generated/resources/data/modonomicon/modonomicon/research/generated/demo
git commit -m "fix: polish generated book research flow"
```

If no cleanup was needed, skip this step.
