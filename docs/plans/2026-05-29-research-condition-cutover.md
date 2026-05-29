# Research Condition Cutover Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Remove legacy `entry_read`, cut `entry_unlocked` over to explicit research-node targeting, and replace advancement-backed book progression with an explicit research-backed path while keeping book-only conditions out of the cutover.

**Architecture:** Keep research nodes as the primary visibility target. Remove `entry_read` from both runtime registration and datagen authoring helpers. Add an explicit advancement-backed research ingress path that grants facts and unlocks nodes, then expose node-backed and narrowly-scoped fact/value-backed research-facing conditions only where needed. Preserve `mod_loaded`, `category_has_visible_entries`, `none`, `true`, and `false` as book-local conditions.

**Tech Stack:** Java 21, Gradle, Mojang codecs, Modonomicon shared common module, Fabric/Neo datagen, manual verification in-game.

---

## File Map

### Create
- `common/src/main/java/com/klikli_dev/modonomicon/research/hook/AdvancementResearchHookService.java` - explicit advancement ingress that grants research facts from configured advancement hooks
- `common/src/main/java/com/klikli_dev/modonomicon/research/data/AdvancementResearchHookDefinition.java` - authored advancement-to-fact hook definition resource

### Modify
- `common/src/main/java/com/klikli_dev/modonomicon/registry/BookConditionTypeRegistry.java` - remove `entry_read`, remove `advancement`, remove `entry_unlocked`
- `common/src/main/java/com/klikli_dev/modonomicon/api/datagen/ConditionHelper.java` - remove `entryRead(...)`, remove `advancement(...)`, remove `entryUnlocked(...)`
- `common/src/main/java/com/klikli_dev/modonomicon/api/datagen/book/condition/BookEntryReadConditionModel.java` - remove file
- `common/src/main/java/com/klikli_dev/modonomicon/api/datagen/book/condition/BookAdvancementConditionModel.java` - remove file
- `common/src/main/java/com/klikli_dev/modonomicon/api/datagen/book/condition/BookEntryUnlockedConditionModel.java` - remove legacy unlock-state datagen model
- `common/src/main/java/com/klikli_dev/modonomicon/book/conditions/BookEntryReadCondition.java` - remove runtime condition class
- `common/src/main/java/com/klikli_dev/modonomicon/book/conditions/BookAdvancementCondition.java` - remove runtime condition class
- `common/src/main/java/com/klikli_dev/modonomicon/book/conditions/BookEntryUnlockedCondition.java` - remove legacy unlock-state runtime condition class
- `common/src/main/java/com/klikli_dev/modonomicon/research/data/ResearchHookDefinition.java` - keep `entry_viewed_once`; no changes expected
- `common/src/main/java/com/klikli_dev/modonomicon/research/data/ResearchData.java` - validate the new advancement-backed hook dataset
- `common/src/main/java/com/klikli_dev/modonomicon/research/data/ResearchDataManager.java` - decode and expose advancement hook definitions
- `common/src/main/java/com/klikli_dev/modonomicon/research/ResearchServices.java` - expose the new advancement research hook service
- `common/src/main/java/com/klikli_dev/modonomicon/bookstate/BookUnlockStateManager.java` - keep as downstream recompute/sync owner only; no advancement-ingress ownership changes
- `common/src/main/java/com/klikli_dev/modonomicon/datagen/book/demo/features/ConditionAdvancementEntry.java` - migrate the demo advancement example away from the legacy advancement book condition
- `common/src/main/java/com/klikli_dev/modonomicon/research/data/ResearchDataProvider.java` - author explicit advancement-backed demo facts/nodes/hooks
- `docs/spec/2026-05-28-research-system-revised-design.md` - append the future optional-dependency research note from the accepted spec
- `fabric/src/generated/resources/data/modonomicon/modonomicon/books/demo/entries/features/condition_advancement.json` - regenerated advancement demo output after `runData`
- `fabric/src/generated/resources/data/modonomicon/modonomicon/books/demo/entries/features/condition_advancement/pages/conditional_page.json` - regenerated advancement demo page condition output after `runData`
- `fabric/src/generated/resources/data/modonomicon/modonomicon/research/demo/facts.json` - expanded research facts after `runData`
- `fabric/src/generated/resources/data/modonomicon/modonomicon/research/demo/nodes.json` - expanded research nodes after `runData`
- `fabric/src/generated/resources/data/modonomicon/modonomicon/research/demo/hooks.json` - unchanged `entry_viewed_once` hooks after `runData`
- `fabric/src/generated/resources/data/modonomicon/modonomicon/research/demo/advancement_hooks.json` - generated advancement research hook output after `runData`
- `neo/src/generated/resources/data/modonomicon/modonomicon/books/demo/entries/features/condition_advancement.json` - regenerated advancement demo output after `neo:runClientData`
- `neo/src/generated/resources/data/modonomicon/modonomicon/books/demo/entries/features/condition_advancement/pages/conditional_page.json` - regenerated advancement demo page condition output after `neo:runClientData`
- `neo/src/generated/resources/data/modonomicon/modonomicon/research/demo/facts.json` - expanded research facts after `neo:runClientData`
- `neo/src/generated/resources/data/modonomicon/modonomicon/research/demo/nodes.json` - expanded research nodes after `neo:runClientData`
- `neo/src/generated/resources/data/modonomicon/modonomicon/research/demo/hooks.json` - unchanged `entry_viewed_once` hooks after `neo:runClientData`
- `neo/src/generated/resources/data/modonomicon/modonomicon/research/demo/advancement_hooks.json` - generated advancement research hook output after `neo:runClientData`

### Reuse Without Modification
- `common/src/main/java/com/klikli_dev/modonomicon/book/conditions/BookModLoadedCondition.java` - remains book-only
- `common/src/main/java/com/klikli_dev/modonomicon/book/conditions/BookCategoryHasVisibleEntriesCondition.java` - remains book-only
- `common/src/main/java/com/klikli_dev/modonomicon/book/conditions/BookAndCondition.java` - keep structural combinator semantics unchanged
- `common/src/main/java/com/klikli_dev/modonomicon/book/conditions/BookOrCondition.java` - keep structural combinator semantics unchanged
- `common/src/main/java/com/klikli_dev/modonomicon/book/conditions/BookResearchNodeUnlockedCondition.java` - remains the primary visibility condition

## Task 1: Remove `entry_read` From Runtime and Datagen Authoring

**Files:**
- Modify: `common/src/main/java/com/klikli_dev/modonomicon/registry/BookConditionTypeRegistry.java`
- Modify: `common/src/main/java/com/klikli_dev/modonomicon/api/datagen/ConditionHelper.java`
- Delete: `common/src/main/java/com/klikli_dev/modonomicon/book/conditions/BookEntryReadCondition.java`
- Delete: `common/src/main/java/com/klikli_dev/modonomicon/api/datagen/book/condition/BookEntryReadConditionModel.java`

- [ ] **Step 1: Remove the runtime `entry_read` registration**

Edit `BookConditionTypeRegistry.java` so the `ENTRY_READ` constant and `BookEntryReadCondition` import are removed.

The relevant section should go from:

```java
import com.klikli_dev.modonomicon.book.conditions.BookEntryReadCondition;
...
public static final BookConditionType<BookEntryReadCondition> ENTRY_READ = register(BookEntryReadCondition.ID, BookEntryReadCondition.CODEC, BookEntryReadCondition.STREAM_CODEC);
```

to:

```java
import com.klikli_dev.modonomicon.book.conditions.BookEntryUnlockedCondition;
...
public static final BookConditionType<BookEntryUnlockedCondition> ENTRY_UNLOCKED = register(BookEntryUnlockedCondition.ID, BookEntryUnlockedCondition.CODEC, BookEntryUnlockedCondition.STREAM_CODEC);
```

- [ ] **Step 2: Remove the datagen helpers for `entry_read`**

Edit `ConditionHelper.java` and delete these methods exactly:

```java
public BookEntryReadConditionModel entryRead(Identifier entryId) {
    return BookEntryReadConditionModel.create().withEntry(entryId);
}

public BookEntryReadConditionModel entryReadBuilder(Identifier entryId) {
    return BookEntryReadConditionModel.create().withEntry(entryId);
}

public BookEntryReadConditionModel entryRead(BookEntryModel entry) {
    return BookEntryReadConditionModel.create().withEntry(entry.getId());
}

public BookEntryReadConditionModel entryReadBuilder(BookEntryModel entry) {
    return BookEntryReadConditionModel.create().withEntry(entry.getId());
}
```

- [ ] **Step 3: Delete the runtime and datagen `entry_read` classes**

Delete these files:

```text
common/src/main/java/com/klikli_dev/modonomicon/book/conditions/BookEntryReadCondition.java
common/src/main/java/com/klikli_dev/modonomicon/api/datagen/book/condition/BookEntryReadConditionModel.java
```

- [ ] **Step 4: Run compile and fix remaining `entry_read` references until it succeeds**

Run:

```bash
./gradlew.bat :common:compileJava
```

Expected:

```text
BUILD SUCCESSFUL
```

If compile fails, remove the remaining references to `BookEntryReadCondition`, `BookEntryReadConditionModel`, or `entryRead(...)` in this task before moving on.

- [ ] **Step 5: Commit**

Run:

```bash
git add common/src/main/java/com/klikli_dev/modonomicon/registry/BookConditionTypeRegistry.java common/src/main/java/com/klikli_dev/modonomicon/api/datagen/ConditionHelper.java common/src/main/java/com/klikli_dev/modonomicon/book/conditions/BookEntryReadCondition.java common/src/main/java/com/klikli_dev/modonomicon/api/datagen/book/condition/BookEntryReadConditionModel.java
git commit -m "refactor: remove entry read condition type"
```

## Task 2: Replace `entry_unlocked` With Explicit Node-Targeting Semantics

**Files:**
- Modify: `common/src/main/java/com/klikli_dev/modonomicon/registry/BookConditionTypeRegistry.java`
- Delete: `common/src/main/java/com/klikli_dev/modonomicon/book/conditions/BookEntryUnlockedCondition.java`
- Delete: `common/src/main/java/com/klikli_dev/modonomicon/api/datagen/book/condition/BookEntryUnlockedConditionModel.java`
- Modify: `common/src/main/java/com/klikli_dev/modonomicon/api/datagen/ConditionHelper.java`
- Modify: `common/src/main/java/com/klikli_dev/modonomicon/datagen/book/demo/FeaturesCategory.java`

- [ ] **Step 1: Remove the runtime `entry_unlocked` registration and class**

Delete the `BookEntryUnlockedCondition` import and `ENTRY_UNLOCKED` constant from `BookConditionTypeRegistry.java`, then delete `BookEntryUnlockedCondition.java`.

The relevant registration should be removed from:

```java
import com.klikli_dev.modonomicon.book.conditions.BookEntryUnlockedCondition;
...
public static final BookConditionType<BookEntryUnlockedCondition> ENTRY_UNLOCKED = register(BookEntryUnlockedCondition.ID, BookEntryUnlockedCondition.CODEC, BookEntryUnlockedCondition.STREAM_CODEC);
```

- [ ] **Step 2: Remove the legacy `entry_unlocked` datagen model**

Delete `common/src/main/java/com/klikli_dev/modonomicon/api/datagen/book/condition/BookEntryUnlockedConditionModel.java`.

- [ ] **Step 3: Remove the legacy `entry_unlocked` helpers from `ConditionHelper`**

Delete the old `entryUnlocked(...)` and `entryUnlockedBuilder(...)` helper overloads from `ConditionHelper.java`.

```java
public BookEntryUnlockedConditionModel entryUnlocked(Identifier entryId) { ... }
public BookEntryUnlockedConditionModel entryUnlockedBuilder(Identifier entryId) { ... }
public BookEntryUnlockedConditionModel entryUnlocked(BookEntryModel entry) { ... }
public BookEntryUnlockedConditionModel entryUnlockedBuilder(BookEntryModel entry) { ... }
```

- [ ] **Step 4: Replace authored `entryUnlocked(...)` uses with direct `researchNodeUnlocked(...)` calls**

Search for `entryUnlocked(` outside `ConditionHelper.java` and replace each use with the explicit authoritative node condition.

```java
this.condition().researchNodeUnlocked(this.modLoc("demo/condition_level_1"))
```

- [ ] **Step 5: Update authored content to use explicit node ids only where needed**

If `entryUnlocked(` is used in authored content, replace every use in this task. Map each entry reference to the authoritative research node already defined for that entry.

If no authored uses exist outside `ConditionHelper.java`, do not add any replacement helper or demo-only sugar.

- [ ] **Step 6: Run compile to verify the cutover API is consistent**

Run:

```bash
./gradlew.bat :common:compileJava
```

Expected:

```text
BUILD SUCCESSFUL
```

If compile fails, remove any remaining references to `BookEntryUnlockedCondition`, `BookEntryUnlockedConditionModel`, or `entryUnlocked(...)` before moving on.

- [ ] **Step 7: Commit**

Run:

```bash
git add common/src/main/java/com/klikli_dev/modonomicon/registry/BookConditionTypeRegistry.java common/src/main/java/com/klikli_dev/modonomicon/book/conditions/BookEntryUnlockedCondition.java common/src/main/java/com/klikli_dev/modonomicon/api/datagen/book/condition/BookEntryUnlockedConditionModel.java common/src/main/java/com/klikli_dev/modonomicon/api/datagen/ConditionHelper.java common/src/main/java/com/klikli_dev/modonomicon/datagen/book/demo/FeaturesCategory.java
git commit -m "feat: cut entry unlocked over to research nodes"
```

## Task 3: Add Explicit Advancement-Backed Research Ingress

**Files:**
- Create: `common/src/main/java/com/klikli_dev/modonomicon/research/data/AdvancementResearchHookDefinition.java`
- Create: `common/src/main/java/com/klikli_dev/modonomicon/research/hook/AdvancementResearchHookService.java`
- Modify: `common/src/main/java/com/klikli_dev/modonomicon/research/data/ResearchData.java`
- Modify: `common/src/main/java/com/klikli_dev/modonomicon/research/data/ResearchDataManager.java`
- Modify: `common/src/main/java/com/klikli_dev/modonomicon/research/ResearchServices.java`
- Modify: `fabric/src/main/java/com/klikli_dev/modonomicon/mixin/MixinPlayerAdvancements.java`
- Modify: `neo/src/main/java/com/klikli_dev/modonomicon/ModonomiconNeo.java`

- [ ] **Step 1: Add the advancement hook definition resource type**

Create `AdvancementResearchHookDefinition.java` with a codec shaped like:

```java
public record AdvancementResearchHookDefinition(Identifier id, Identifier advancementId, Identifier factId) {
    public static final Codec<AdvancementResearchHookDefinition> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Identifier.CODEC.fieldOf("id").forGetter(AdvancementResearchHookDefinition::id),
            Identifier.CODEC.fieldOf("advancement_id").forGetter(AdvancementResearchHookDefinition::advancementId),
            Identifier.CODEC.fieldOf("fact_id").forGetter(AdvancementResearchHookDefinition::factId)
    ).apply(instance, AdvancementResearchHookDefinition::new));
}
```

- [ ] **Step 2: Extend `ResearchData` to validate and expose advancement hooks**

Modify `ResearchData.java` so it stores a lookup like:

```java
Map<Identifier, List<AdvancementResearchHookDefinition>> advancementHooks
```

and validate that every `factId` referenced by an advancement hook exists in the loaded fact set.

- [ ] **Step 3: Extend `ResearchDataManager` to load `advancement_hooks.json` without colliding with `hooks.json`**

Modify `apply(...)` in `ResearchDataManager.java` so it recognizes an additional file kind by exact basename, not broad suffix matching.

```java
List<AdvancementResearchHookDefinition> advancementHooks = List.of();
...
var fileName = path.substring(path.lastIndexOf('/') + 1);
if (fileName.equals("facts")) {
    ...
} else if (fileName.equals("nodes")) {
    ...
} else if (fileName.equals("advancement_hooks")) {
    advancementHooks = new ArrayList<>(AdvancementResearchHookDefinition.CODEC.listOf().parse(JsonOps.INSTANCE, entry.getValue()).getOrThrow());
} else if (fileName.equals("hooks")) {
    ...
}
```

and pass those hooks into `ResearchData.validate(...)`.

- [ ] **Step 4: Add the runtime service that grants research facts for the specific completed advancement**

Create `AdvancementResearchHookService.java` with logic shaped like:

```java
public class AdvancementResearchHookService {
    private final ResearchStateManager stateManager;

    public AdvancementResearchHookService(ResearchStateManager stateManager) {
        this.stateManager = stateManager;
    }

    public boolean onAdvancement(ServerPlayer player, Identifier advancementId) {
        boolean changed = false;
        for (var hook : ResearchDataManager.get().data().advancementHooks().getOrDefault(advancementId, List.of())) {
            changed |= this.stateManager.grantFact(player, hook.factId());
        }
        if (changed) {
            changed |= this.stateManager.reevaluate(player);
        }
        return changed;
    }
}
```

- [ ] **Step 5: Expose the new service from `ResearchServices`**

Add:

```java
private static final AdvancementResearchHookService ADVANCEMENTS = new AdvancementResearchHookService(STATE);

public static AdvancementResearchHookService advancements() {
    return ADVANCEMENTS;
}
```

- [ ] **Step 6: Route server-side advancement-earned events through research before book unlock recompute**

Edit the existing platform/server advancement hooks so they call `ResearchServices.advancements().onAdvancement(player, advancementId)` first.

Use the current hook points already present in this repo:

- `fabric/src/main/java/com/klikli_dev/modonomicon/mixin/MixinPlayerAdvancements.java`
- `neo/src/main/java/com/klikli_dev/modonomicon/ModonomiconNeo.java`

Then only queue or trigger the existing `BookUnlockStateManager.onAdvancement(player)` path if the research service reported a state change.

```java
if (ResearchServices.advancements().onAdvancement(player, advancementId)) {
    BookUnlockStateManager.get().onAdvancement(player);
}
```

Do not route advancement ingress through `RequestAdvancementMessage`.

Do not add a full advancement rescan helper in this slice.

- [ ] **Step 7: Keep `BookUnlockStateManager` focused on recompute and queueing**

Do not move research ingress ownership into `BookUnlockStateManager`.

Only keep its existing responsibility: throttled unlock-state recomputation and sync after research-relevant advancement events.

- [ ] **Step 8: Run compile and fix any missing imports/signatures**

Run:

```bash
./gradlew.bat :common:compileJava
```

Expected:

```text
BUILD SUCCESSFUL
```

- [ ] **Step 9: Commit**

Run:

```bash
git add common/src/main/java/com/klikli_dev/modonomicon/research/data/AdvancementResearchHookDefinition.java common/src/main/java/com/klikli_dev/modonomicon/research/hook/AdvancementResearchHookService.java common/src/main/java/com/klikli_dev/modonomicon/research/data/ResearchData.java common/src/main/java/com/klikli_dev/modonomicon/research/data/ResearchDataManager.java common/src/main/java/com/klikli_dev/modonomicon/research/ResearchServices.java fabric/src/main/java/com/klikli_dev/modonomicon/mixin/MixinPlayerAdvancements.java neo/src/main/java/com/klikli_dev/modonomicon/ModonomiconNeo.java
git commit -m "feat: add advancement research hooks"
```

## Task 4: Remove Legacy Advancement Book Condition Authoring And Runtime Support

**Files:**
- Modify: `common/src/main/java/com/klikli_dev/modonomicon/registry/BookConditionTypeRegistry.java`
- Modify: `common/src/main/java/com/klikli_dev/modonomicon/api/datagen/ConditionHelper.java`
- Delete: `common/src/main/java/com/klikli_dev/modonomicon/book/conditions/BookAdvancementCondition.java`
- Delete: `common/src/main/java/com/klikli_dev/modonomicon/api/datagen/book/condition/BookAdvancementConditionModel.java`
- Modify: `common/src/main/java/com/klikli_dev/modonomicon/datagen/book/demo/features/ConditionAdvancementEntry.java`

- [ ] **Step 1: Remove the runtime `advancement` registration**

Delete the `BookAdvancementCondition` import and `ADVANCEMENT` constant from `BookConditionTypeRegistry.java`.

- [ ] **Step 2: Remove the datagen helpers for legacy advancement conditions**

Delete these methods from `ConditionHelper.java`:

```java
public BookAdvancementConditionModel advancement(Identifier advancementId) {
    return BookAdvancementConditionModel.create().withAdvancementId(advancementId);
}

public BookAdvancementConditionModel advancementBuilder(Identifier advancementId) {
    return BookAdvancementConditionModel.create().withAdvancementId(advancementId);
}
```

- [ ] **Step 3: Delete the legacy runtime and datagen advancement condition classes**

Delete:

```text
common/src/main/java/com/klikli_dev/modonomicon/book/conditions/BookAdvancementCondition.java
common/src/main/java/com/klikli_dev/modonomicon/api/datagen/book/condition/BookAdvancementConditionModel.java
```

- [ ] **Step 4: Rewrite the demo advancement example to use research-backed visibility**

Edit `ConditionAdvancementEntry.java` so it no longer constructs `BookAdvancementConditionModel` instances.

Replace code shaped like:

```java
var pageCondition = BookAdvancementConditionModel.create()
        .withAdvancementId(Identifier.parse("minecraft:story/mine_stone"));
```

with code shaped like:

```java
var pageCondition = this.condition().researchNodeUnlocked(Identifier.parse("modonomicon:demo/advancement_mine_stone"));
```

and update any entry-level conditions in the same file to point at explicit demo research nodes.

- [ ] **Step 5: Run compile to catch remaining legacy advancement references**

Run:

```bash
./gradlew.bat :common:compileJava
```

Expected:

```text
BUILD SUCCESSFUL
```

- [ ] **Step 6: Commit**

Run:

```bash
git add common/src/main/java/com/klikli_dev/modonomicon/registry/BookConditionTypeRegistry.java common/src/main/java/com/klikli_dev/modonomicon/api/datagen/ConditionHelper.java common/src/main/java/com/klikli_dev/modonomicon/book/conditions/BookAdvancementCondition.java common/src/main/java/com/klikli_dev/modonomicon/api/datagen/book/condition/BookAdvancementConditionModel.java common/src/main/java/com/klikli_dev/modonomicon/datagen/book/demo/features/ConditionAdvancementEntry.java
git commit -m "refactor: remove advancement book condition"
```

## Task 5: Author Demo Research Data For Advancement And Regenerate Content

**Files:**
- Modify: `common/src/main/java/com/klikli_dev/modonomicon/research/data/ResearchDataProvider.java`
- Modify: `fabric/src/generated/resources/data/modonomicon/modonomicon/books/demo/entries/features/condition_advancement.json`
- Modify: `fabric/src/generated/resources/data/modonomicon/modonomicon/books/demo/entries/features/condition_advancement/pages/conditional_page.json`
- Modify: `fabric/src/generated/resources/data/modonomicon/modonomicon/research/demo/facts.json`
- Modify: `fabric/src/generated/resources/data/modonomicon/modonomicon/research/demo/nodes.json`
- Create/Modify: `fabric/src/generated/resources/data/modonomicon/modonomicon/research/demo/advancement_hooks.json`
- Modify: `neo/src/generated/resources/data/modonomicon/modonomicon/books/demo/entries/features/condition_advancement.json`
- Modify: `neo/src/generated/resources/data/modonomicon/modonomicon/books/demo/entries/features/condition_advancement/pages/conditional_page.json`
- Modify: `neo/src/generated/resources/data/modonomicon/modonomicon/research/demo/facts.json`
- Modify: `neo/src/generated/resources/data/modonomicon/modonomicon/research/demo/nodes.json`
- Create/Modify: `neo/src/generated/resources/data/modonomicon/modonomicon/research/demo/advancement_hooks.json`

- [ ] **Step 1: Add explicit demo fact and node ids for the advancement example**

Extend `ResearchDataProvider.java` with at least these new definitions:

```java
new ResearchFactDefinition(Identifier.parse("modonomicon:demo/advancement_mine_stone_completed"))
```

and:

```java
new ResearchNodeDefinition(Identifier.parse("modonomicon:demo/advancement_mine_stone"), List.of(Identifier.parse("modonomicon:demo/advancement_mine_stone_completed")))
```

- [ ] **Step 2: Add an explicit advancement hook definition to datagen**

In the same provider, add a generated advancement hook definition shaped like:

```java
new AdvancementResearchHookDefinition(
        Identifier.parse("modonomicon:demo/advancement_mine_stone_completed_hook"),
        Identifier.parse("minecraft:story/mine_stone"),
        Identifier.parse("modonomicon:demo/advancement_mine_stone_completed")
)
```

- [ ] **Step 3: Run Fabric datagen and compile**

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

- [ ] **Step 4: Run Neo datagen**

Run:

```bash
./gradlew.bat neo:runClientData
```

Expected:

```text
BUILD SUCCESSFUL
```

- [ ] **Step 5: Inspect the generated output**

Confirm that:

```text
fabric/src/generated/resources/data/modonomicon/modonomicon/books/demo/entries/features/condition_advancement.json
fabric/src/generated/resources/data/modonomicon/modonomicon/books/demo/entries/features/condition_advancement/pages/conditional_page.json
fabric/src/generated/resources/data/modonomicon/modonomicon/research/demo/advancement_hooks.json
neo/src/generated/resources/data/modonomicon/modonomicon/books/demo/entries/features/condition_advancement.json
neo/src/generated/resources/data/modonomicon/modonomicon/books/demo/entries/features/condition_advancement/pages/conditional_page.json
neo/src/generated/resources/data/modonomicon/modonomicon/research/demo/advancement_hooks.json
```

contain research-backed conditions/resources and no legacy `modonomicon:advancement` conditions.

- [ ] **Step 6: Commit**

Run:

```bash
git add common/src/main/java/com/klikli_dev/modonomicon/research/data/ResearchDataProvider.java fabric/src/generated/resources/data/modonomicon/modonomicon/books/demo/entries/features/condition_advancement.json fabric/src/generated/resources/data/modonomicon/modonomicon/books/demo/entries/features/condition_advancement/pages/conditional_page.json fabric/src/generated/resources/data/modonomicon/modonomicon/research/demo/facts.json fabric/src/generated/resources/data/modonomicon/modonomicon/research/demo/nodes.json fabric/src/generated/resources/data/modonomicon/modonomicon/research/demo/advancement_hooks.json neo/src/generated/resources/data/modonomicon/modonomicon/books/demo/entries/features/condition_advancement.json neo/src/generated/resources/data/modonomicon/modonomicon/books/demo/entries/features/condition_advancement/pages/conditional_page.json neo/src/generated/resources/data/modonomicon/modonomicon/research/demo/facts.json neo/src/generated/resources/data/modonomicon/modonomicon/research/demo/nodes.json neo/src/generated/resources/data/modonomicon/modonomicon/research/demo/advancement_hooks.json
git commit -m "feat: migrate advancement progression to research"
```

## Task 6: Add Validation And Documentation Boundary Cleanup

**Files:**
- Modify: `common/src/main/java/com/klikli_dev/modonomicon/book/conditions/BookCondition.java`
- Modify: `common/src/main/java/com/klikli_dev/modonomicon/book/conditions/BookResearchNodeUnlockedCondition.java`
- Modify: `docs/spec/2026-05-28-research-system-revised-design.md`

- [ ] **Step 1: Add clear failure messages at the book-condition boundary for removed condition ids**

Update the book-condition decode/load path so removed ids fail with explicit errors for `modonomicon:entry_read`, `modonomicon:entry_unlocked`, and `modonomicon:advancement`.

Use messages shaped like:

```java
throw new IllegalArgumentException("Book condition type 'modonomicon:entry_read' is no longer supported. Model this progression through explicit research hooks and research nodes instead.");
```

and equivalent messages for `entry_unlocked` and `advancement`.

Do not place this validation in `ResearchData`.

- [ ] **Step 2: Add explicit validation for unknown research node ids used by book conditions**

Update `BookResearchNodeUnlockedCondition` or the relevant book-load validation path so a referenced node id must exist in loaded research data.

Fail with an explicit error instead of silently leaving the condition permanently false.

- [ ] **Step 3: Ensure the revised design ends with the future optional-dependency note**

Ensure the bottom of `docs/spec/2026-05-28-research-system-revised-design.md` contains this exact note. Append it if absent; otherwise leave the existing identical note unchanged:

```markdown
## Additional Future Work Note

A future slice may add a research-side optional-dependency predicate or function for research content that belongs to an optional dependency mod.

That is not part of the current condition cutover and does not change the rule that `mod_loaded` remains a book-local/environment condition.
```

- [ ] **Step 4: Run the final verification commands**

Run:

```bash
./gradlew.bat :common:compileJava
./gradlew.bat runData
./gradlew.bat neo:runClientData
rg '"type":\s*"modonomicon:entry_read"|"type":\s*"modonomicon:advancement"' fabric/src/generated/resources/data/modonomicon/modonomicon/books neo/src/generated/resources/data/modonomicon/modonomicon/books
rg '"type":\s*"modonomicon:entry_unlocked"' fabric/src/generated/resources/data/modonomicon/modonomicon/books neo/src/generated/resources/data/modonomicon/modonomicon/books
rg 'entryUnlocked\(|BookEntryUnlockedCondition|BookEntryUnlockedConditionModel' common/src/main/java
```

Expected:

```text
:common:compileJava -> BUILD SUCCESSFUL
runData -> BUILD SUCCESSFUL
neo:runClientData -> BUILD SUCCESSFUL
rg -> no matches for removed condition types in migrated/generated book content
rg -> no generated `modonomicon:entry_unlocked` conditions remain
rg -> no remaining legacy `entry_unlocked` helpers or models in common Java sources
```

- [ ] **Step 5: Perform manual verification in-game**

Run:

```bash
./gradlew.bat runClient
```

Verify this exact flow:

```text
1. Open the demo book.
2. Confirm previously migrated demo research progression still works.
3. Confirm the advancement demo entry/page unlocks through the new research-backed path.
4. Confirm toggling the server advancement-locking config no longer controls progression through a direct book advancement condition.
5. Confirm no authored progression path still depends on entry_read semantics.
6. Confirm mod_loaded-gated content still behaves as an environment/book condition.
7. Confirm category_has_visible_entries behavior remains downstream/book-local.
8. Run /modonomicon research reset.
9. Confirm advancement-backed research progression relocks correctly.
10. Confirm a deliberately broken research node id now fails loudly during load instead of silently staying locked.
```

- [ ] **Step 6: Commit**

Run:

```bash
git add common/src/main/java/com/klikli_dev/modonomicon/book/conditions/BookCondition.java common/src/main/java/com/klikli_dev/modonomicon/book/conditions/BookResearchNodeUnlockedCondition.java docs/spec/2026-05-28-research-system-revised-design.md
git commit -m "fix: validate research condition cutover"
```

## Spec Coverage Check

- Remove `entry_read` entirely: covered by Tasks 1 and 6.
- Node-targeting rule for `entry_unlocked`: covered by Task 2.
- Explicit research-backed replacement for `advancement`: covered by Tasks 3-5.
- Keep `mod_loaded`, `category_has_visible_entries`, `none`, `true`, and `false` book-only: preserved by scope and verified in Task 6.
- Keep nodes primary while allowing only narrow secondary research-facing conditions: covered by Tasks 2-3 and enforced by Task 6 validation.
