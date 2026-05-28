# Research System Demo `entry_read` Migration Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Migrate all demo-book `entry_read`-based progression to research-backed progression while keeping legacy runtime support in place.

**Architecture:** Reuse the explicit research primitives from slice 1: facts, nodes, hooks, and `research_node_unlocked`. Replace every demo-book `entry_read` condition, including nested/composite uses, with equivalent research-backed conditions. Keep old runtime condition support in place, but stop depending on it for demo-book progression authoring.

**Tech Stack:** Java 21, Gradle, Mojang codecs, Modonomicon datagen providers, manual verification in-game.

---

## File Map

### Modify
- `common/src/main/java/com/klikli_dev/modonomicon/datagen/book/DemoBook.java` - migrate the `conditional` category gate away from `entry_read`
- `common/src/main/java/com/klikli_dev/modonomicon/datagen/book/demo/FormattingCategory.java` - migrate formatting-chain progression conditions
- `common/src/main/java/com/klikli_dev/modonomicon/datagen/book/demo/FeaturesCategory.java` - migrate remaining direct and composite `entry_read` usage
- `common/src/main/java/com/klikli_dev/modonomicon/research/data/ResearchDataProvider.java` - add explicit research facts/nodes/hooks for all migrated demo-book progression
- `fabric/src/generated/resources/data/modonomicon/modonomicon/books/demo/categories/conditional.json` - generated category migration output after `runData`
- `fabric/src/generated/resources/data/modonomicon/modonomicon/books/demo/entries/formatting/advanced.json` - generated formatting migration output after `runData`
- `fabric/src/generated/resources/data/modonomicon/modonomicon/books/demo/entries/formatting/link.json` - generated formatting migration output after `runData`
- `fabric/src/generated/resources/data/modonomicon/modonomicon/books/demo/entries/features/spotlight.json` - generated features migration output after `runData`
- `fabric/src/generated/resources/data/modonomicon/modonomicon/books/demo/entries/features/component_icon.json` - generated features migration output after `runData`
- `fabric/src/generated/resources/data/modonomicon/modonomicon/books/demo/entries/features/empty.json` - generated features migration output after `runData`
- `fabric/src/generated/resources/data/modonomicon/modonomicon/books/demo/entries/features/image.json` - generated features migration output after `runData`
- `fabric/src/generated/resources/data/modonomicon/modonomicon/books/demo/entries/features/custom_icon.json` - generated features migration output after `runData`
- `fabric/src/generated/resources/data/modonomicon/modonomicon/books/demo/entries/features/two_parents.json` - generated composite migration output after `runData`
- `fabric/src/generated/resources/data/modonomicon/modonomicon/research/demo/facts.json` - expanded generated research facts after `runData`
- `fabric/src/generated/resources/data/modonomicon/modonomicon/research/demo/nodes.json` - expanded generated research nodes after `runData`
- `fabric/src/generated/resources/data/modonomicon/modonomicon/research/demo/hooks.json` - expanded generated research hooks after `runData`

### Reuse Without Modification
- `common/src/main/java/com/klikli_dev/modonomicon/research/data/ResearchHookDefinition.java` - reuse the current explicit hook model
- `common/src/main/java/com/klikli_dev/modonomicon/research/hook/ResearchHookService.java` - reuse the current `entry_viewed_once` runtime path
- `common/src/main/java/com/klikli_dev/modonomicon/book/conditions/BookResearchNodeUnlockedCondition.java` - reuse the current research-backed condition

## Task 1: Map Every Demo-Book `entry_read` Dependency To Research IDs

**Files:**
- Modify: `common/src/main/java/com/klikli_dev/modonomicon/research/data/ResearchDataProvider.java`

- [ ] **Step 1: Add explicit research ids for every remaining demo-book `entry_read` source**

Extend `ResearchDataProvider.java` so it declares facts and hooks for each entry whose view event currently drives demo-book progression.

The provider should include at least the following viewed facts:

```java
modonomicon:demo/formatting_basic_viewed
modonomicon:demo/formatting_advanced_viewed
modonomicon:demo/features_recipe_viewed
modonomicon:demo/features_spotlight_viewed
modonomicon:demo/features_component_icon_viewed
modonomicon:demo/features_empty_viewed
modonomicon:demo/features_image_viewed
```

Reuse already existing slice-1 facts where appropriate instead of duplicating them.

- [ ] **Step 2: Add explicit research nodes for every migrated progression milestone**

In the same provider, declare nodes representing the milestones that replace the old `entry_read` conditions.

The provider should include at least:

```java
modonomicon:demo/formatting_advanced
modonomicon:demo/formatting_link
modonomicon:demo/features_spotlight
modonomicon:demo/features_component_icon
modonomicon:demo/features_empty
modonomicon:demo/features_image
modonomicon:demo/features_custom_icon
modonomicon:demo/features_two_parents_root
modonomicon:demo/features_two_parents_level_2
```

Use one node per old dependency input, not one oversized combined node, so composite migration stays explicit.

- [ ] **Step 3: Add hook definitions for each migrated source entry**

Still in `ResearchDataProvider.java`, add `entry_viewed_once` hooks from each source entry to its corresponding fact.

Examples to include:

```java
features/recipe -> demo/features_recipe_viewed
features/spotlight -> demo/features_spotlight_viewed
formatting/basic -> demo/formatting_basic_viewed
formatting/advanced -> demo/formatting_advanced_viewed
```

- [ ] **Step 4: Run compile and datagen**

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

- [ ] **Step 5: Commit**

Run:

```bash
git add common/src/main/java/com/klikli_dev/modonomicon/research/data/ResearchDataProvider.java fabric/src/generated/resources/data/modonomicon/modonomicon/research/demo/facts.json fabric/src/generated/resources/data/modonomicon/modonomicon/research/demo/nodes.json fabric/src/generated/resources/data/modonomicon/modonomicon/research/demo/hooks.json neo/src/generated/resources/data/modonomicon/modonomicon/research/demo/facts.json neo/src/generated/resources/data/modonomicon/modonomicon/research/demo/nodes.json neo/src/generated/resources/data/modonomicon/modonomicon/research/demo/hooks.json forge/src/generated/resources/data/modonomicon/modonomicon/research/demo/facts.json forge/src/generated/resources/data/modonomicon/modonomicon/research/demo/nodes.json forge/src/generated/resources/data/modonomicon/modonomicon/research/demo/hooks.json
git commit -m "feat: expand demo research progression data"
```

## Task 2: Migrate Formatting Demo Progression

**Files:**
- Modify: `common/src/main/java/com/klikli_dev/modonomicon/datagen/book/demo/FormattingCategory.java`

- [ ] **Step 1: Replace the `advanced` unlock dependency**

Update `FormattingCategory.java` so `advancedFormattingEntry` uses a research-backed condition instead of implicit `entry_read` progression.

The resulting condition should point at the node that replaces `entry_read(formatting/basic)`.

- [ ] **Step 2: Replace the `link` unlock dependency**

Update `FormattingCategory.java` so `linkFormattingEntry` also uses a research-backed condition instead of legacy `entry_read` progression.

The resulting condition should point at the node that replaces `entry_read(formatting/advanced)`.

- [ ] **Step 3: Run compile and datagen**

Run:

```bash
./gradlew.bat :common:compileJava
./gradlew.bat runData
```

Expected: BUILD SUCCESSFUL for both.

- [ ] **Step 4: Commit**

Run:

```bash
git add common/src/main/java/com/klikli_dev/modonomicon/datagen/book/demo/FormattingCategory.java fabric/src/generated/resources/data/modonomicon/modonomicon/books/demo/entries/formatting/advanced.json fabric/src/generated/resources/data/modonomicon/modonomicon/books/demo/entries/formatting/link.json neo/src/generated/resources/data/modonomicon/modonomicon/books/demo/entries/formatting/advanced.json neo/src/generated/resources/data/modonomicon/modonomicon/books/demo/entries/formatting/link.json forge/src/generated/resources/data/modonomicon/modonomicon/books/demo/entries/formatting/advanced.json forge/src/generated/resources/data/modonomicon/modonomicon/books/demo/entries/formatting/link.json
git commit -m "feat: migrate formatting demo progression"
```

## Task 3: Migrate Category And Remaining Feature Chains

**Files:**
- Modify: `common/src/main/java/com/klikli_dev/modonomicon/datagen/book/DemoBook.java`
- Modify: `common/src/main/java/com/klikli_dev/modonomicon/datagen/book/demo/FeaturesCategory.java`

- [ ] **Step 1: Migrate the `conditional` category gate**

In `DemoBook.java`, replace the `conditional` category’s old `entry_read(features/condition_root)` gate with the research node already tied to the viewed-once progression for `condition_root`.

- [ ] **Step 2: Migrate the recipe-to-spotlight chain**

In `FeaturesCategory.java`, replace the old progression semantics behind:

```java
recipe -> spotlight -> component_icon -> empty -> image -> custom_icon
```

with research-backed node conditions.

Each step should keep the same player-facing order while replacing the old `entry_read` dependency it used.

- [ ] **Step 3: Migrate the composite `two_parents` condition**

Replace the nested composite condition on `twoParentsEntry` so the surrounding `and(...)` remains, but each `entry_read(...)` leaf becomes the corresponding `researchNodeUnlocked(...)` leaf.

- [ ] **Step 4: Run compile and datagen**

Run:

```bash
./gradlew.bat :common:compileJava
./gradlew.bat runData
```

Expected: BUILD SUCCESSFUL for both.

- [ ] **Step 5: Commit**

Run:

```bash
git add common/src/main/java/com/klikli_dev/modonomicon/datagen/book/DemoBook.java common/src/main/java/com/klikli_dev/modonomicon/datagen/book/demo/FeaturesCategory.java fabric/src/generated/resources/data/modonomicon/modonomicon/books/demo/categories/conditional.json fabric/src/generated/resources/data/modonomicon/modonomicon/books/demo/entries/features/spotlight.json fabric/src/generated/resources/data/modonomicon/modonomicon/books/demo/entries/features/component_icon.json fabric/src/generated/resources/data/modonomicon/modonomicon/books/demo/entries/features/empty.json fabric/src/generated/resources/data/modonomicon/modonomicon/books/demo/entries/features/image.json fabric/src/generated/resources/data/modonomicon/modonomicon/books/demo/entries/features/custom_icon.json fabric/src/generated/resources/data/modonomicon/modonomicon/books/demo/entries/features/two_parents.json neo/src/generated/resources/data/modonomicon/modonomicon/books/demo/categories/conditional.json neo/src/generated/resources/data/modonomicon/modonomicon/books/demo/entries/features/spotlight.json neo/src/generated/resources/data/modonomicon/modonomicon/books/demo/entries/features/component_icon.json neo/src/generated/resources/data/modonomicon/modonomicon/books/demo/entries/features/empty.json neo/src/generated/resources/data/modonomicon/modonomicon/books/demo/entries/features/image.json neo/src/generated/resources/data/modonomicon/modonomicon/books/demo/entries/features/custom_icon.json neo/src/generated/resources/data/modonomicon/modonomicon/books/demo/entries/features/two_parents.json forge/src/generated/resources/data/modonomicon/modonomicon/books/demo/categories/conditional.json forge/src/generated/resources/data/modonomicon/modonomicon/books/demo/entries/features/spotlight.json forge/src/generated/resources/data/modonomicon/modonomicon/books/demo/entries/features/component_icon.json forge/src/generated/resources/data/modonomicon/modonomicon/books/demo/entries/features/empty.json forge/src/generated/resources/data/modonomicon/modonomicon/books/demo/entries/features/image.json forge/src/generated/resources/data/modonomicon/modonomicon/books/demo/entries/features/custom_icon.json forge/src/generated/resources/data/modonomicon/modonomicon/books/demo/entries/features/two_parents.json
git commit -m "feat: migrate demo entry-read conditions"
```

## Task 4: Verify Demo-Book Migration Boundary

**Files:**
- Modify: no code changes required unless verification exposes a concrete defect

- [ ] **Step 1: Confirm there are no remaining demo-book `entry_read` conditions**

Run:

```bash
rg '"type":\s*"modonomicon:entry_read"' fabric/src/generated/resources/data/modonomicon/modonomicon/books/demo
```

Expected:

```text
No matches for migrated demo progression paths.
```

If any remaining matches are intentional non-progression leftovers, inspect them and either migrate them too or document the exception before finishing.

- [ ] **Step 2: Run the final verification commands**

Run:

```bash
./gradlew.bat :common:compileJava
./gradlew.bat runData
```

Expected: BUILD SUCCESSFUL for both.

- [ ] **Step 3: Perform manual verification in-game**

Run: `./gradlew.bat runClient`

Verify this exact flow:

```text
1. Open the demo book.
2. Confirm formatting progression still works from basic -> advanced -> link.
3. Confirm recipe -> spotlight -> later feature chain still works.
4. Confirm two_parents still requires both expected progress milestones.
5. Confirm the conditional category unlocks through research-backed progression.
6. Confirm the original slice-1 condition_root -> condition_level_1 -> condition_level_2 chain still works.
7. Run /modonomicon research reset.
8. Confirm migrated demo-book progression relocks correctly.
9. Confirm unrelated non-demo legacy behavior remains unchanged.
```

- [ ] **Step 4: Commit any final fix if verification required code changes**

If a verification fix was needed, run:

```bash
git add <exact changed files>
git commit -m "fix: polish demo research migration"
```

If no fix was needed, skip this step.

## Notes For The Implementer

- Keep the migration at the authored-content layer for the demo book.
- Do not remove runtime support for `entry_read`.
- Do not introduce a new trigger family in this slice.
- Preserve composite condition structure while replacing leaf progression inputs.
- Reuse existing slice-1 research facts/nodes where they already model the needed milestone.

## Spec Coverage Check

- All demo-book `entry_read` usage migrated: covered by Tasks 1-3.
- Conditional category migrated: covered by Task 3.
- Composite/nested `entry_read` migration: covered by Task 3.
- Legacy support retained for safety: preserved by scope and notes.
- Manual verification of full demo migration: covered by Task 4.
