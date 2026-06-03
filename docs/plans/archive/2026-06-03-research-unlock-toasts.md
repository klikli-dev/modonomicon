# Research Toasts

## Goal

Implement client-side toast notifications (like vanilla advancement toasts) that appear when research facts are granted, values are incremented, or nodes are unlocked. Each research element (fact, value, node) optionally carries toast display data authored alongside the research definition.

---

## How Vanilla Advancement Toasts Work

Vanilla's toast system consists of three key pieces:

### `Toast` interface (`net.minecraft.client.gui.components.toasts.Toast`)
- `getWantedVisibility()` — returns `SHOW` or `HIDE` to control slide-in/out
- `update(ToastManager, long fullyVisibleForMs)` — called each tick; controls timing
- `extractRenderState(GuiGraphicsExtractor, Font, long fullyVisibleForMs)` — renders the toast
- `getToken()` — deduplication key; prevents duplicate toasts of the same type
- `getSoundEvent()` — optional sound on appearance
- Default size: 160×32 pixels, configurable via `width()` / `height()`

### `ToastManager` (`net.minecraft.client.gui.components.toasts.ToastManager`)
- Manages up to 5 visible toast slots (stacked vertically)
- Handles slide-in/out animation (600ms ease-in-out)
- Queue system: toasts wait for a free slot
- Accessed via `Minecraft.getInstance().getToastManager()`
- Add toasts with `toastManager.addToast(toast)`
- Respects `options.notificationDisplayTime()` for display duration

### `AdvancementToast` (`net.minecraft.client.gui.components.toasts.AdvancementToast`)
- Shows background sprite `toast/advancement`
- Left side: item icon (8,8) via `graphics.fakeItem()`
- Text area: animated transition from advancement type name to title
- Display time: 5000ms (adjusted by notification display time multiplier)
- Token: advancement holder (deduplication)
- Challenge advancements get special color (-30465) and sound

---

## Toast Display Data Model

Each research element (fact, value, node) can optionally define toast display data. This data determines what the client shows when the element is triggered.

### What an advancement toast needs
- **Title** (Component) — primary text, second line of toast
- **Icon** (item or texture) — left-side icon
- **Description/category** (Component) — first line, advancement type name

### Research toast data fields
The toast data for a research element is:

| Field | Type | Required | Description |
|---|---|---|---|
| `title` | `Identifier` (translatable key) | Yes | Translatable key for the toast title (second line). Arguments are appended positionally. |
| `title_args` | `List<Component>` | No | Static component arguments for the title translation. For values, the current value is automatically appended as the last arg. |
| `description` | `Identifier` (translatable key) | No | Translatable key for the toast description/category (first line). Defaults to a generic "Research" key per element type. |
| `icon` | `BookIcon` | No | Icon to display. If omitted, no icon is rendered (background still draws). |

### Per-element behavior
- **Facts**: Toast fires once when the fact is newly granted. Title args are static.
- **Values**: Toast fires on every increment. The current value (after increment) is automatically appended as the last title arg, regardless of `title_args`.
- **Nodes**: Toast fires once when the node is newly unlocked. Title args are static.

### JSON shape (per element)
```json
{
  "id": "mod:example_fact",
  "toast": {
    "title": "gui.modonomicon.research.fact.example",
    "title_args": [
      {"text": "Diamond"}
    ],
    "description": "gui.modonomicon.research.fact",
    "icon": {
      "item": "minecraft:diamond"
    }
  }
}
```

For values, the current count is auto-appended:
```json
{
  "id": "mod:example_value",
  "toast": {
    "title": "gui.modonomicon.research.value.example",
    "title_args": [
      {"text": "Iron Ingot"}
    ]
    // Current value appended automatically: "Crafted Iron Ingot (42)"
  }
}
```

---

## Data Model Classes

### `ResearchToastDefinition` — Toast display data

**New file:** `common/src/main/java/com/klikli_dev/modonomicon/research/data/ResearchToastDefinition.java`

```java
/**
 * Optional toast display data for a research element.
 *
 * @param title translatable key for the toast title (second line)
 * @param titleArgs static component arguments for the title (values auto-append current count)
 * @param description translatable key for the toast category/description (first line), null uses default
 * @param icon optional icon to render; null means no icon
 */
public record ResearchToastDefinition(
        Identifier title,
        List<Component> titleArgs,
        Identifier description,
        BookIcon icon
) {
    public static final Codec<ResearchToastDefinition> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Identifier.CODEC.fieldOf("title").forGetter(ResearchToastDefinition::title),
            Component.CODEC.listOf().optionalFieldOf("title_args", List.of()).forGetter(ResearchToastDefinition::titleArgs),
            Identifier.CODEC.optionalFieldOf("description").forGetter(ResearchToastDefinition::description),
            BookIcon.CODEC.optionalFieldOf("icon").forGetter(ResearchToastDefinition::icon)
    ).apply(instance, ResearchToastDefinition::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, ResearchToastDefinition> STREAM_CODEC =
            ByteBufCodecs.fromCodecWithRegistries(CODEC);
}
```

### Modified element definitions

Each element definition gains an optional `toast` field:

- `ResearchFactDefinition` — add `Optional<ResearchToastDefinition> toast`
- `ResearchValueDefinition` — add `Optional<ResearchToastDefinition> toast`
- `ResearchNodeDefinition` — add `Optional<ResearchToastDefinition> toast`

---

## Architecture

### Server-side: Collect toast triggers during state changes

The server already tracks state changes in `ResearchStateManager` and `ResearchHookService`. The approach:

1. **`ResearchStateManager`** methods that mutate state return change information
2. **`ResearchHookService`** collects toast triggers during the before→after cycle
3. A new `ResearchToastMessage` sends the collected triggers to the client

### Trigger collection record

**New record:** `ResearchToastTrigger`

```java
/**
 * A single toast trigger sent from server to client.
 *
 * @param type fact, value, or node
 * @param elementId the research element's id
 * @param currentValue for values: the value after increment; 0 for facts/nodes
 */
public record ResearchToastTrigger(
        ToastTriggerType type,
        Identifier elementId,
        int currentValue
) {
    public enum ToastTriggerType {
        FACT_GRANTED,
        VALUE_INCREMENTED,
        NODE_UNLOCKED
    }
}
```

### Why send triggers instead of rendered data?
- Server is authoritative on what changed
- Client already has the research data loaded (toast definitions live in the research JSON)
- Client can resolve translatable keys, icons, and render the toast
- Smaller network payload (id + type + count vs full components)

---

## Implementation Plan

### 1. Create `ResearchToastDefinition`

**New file:** `common/src/main/java/com/klikli_dev/modonomicon/research/data/ResearchToastDefinition.java`

- [ ] Create the record with `title`, `titleArgs`, `description`, `icon` fields
- [ ] Implement `CODEC` and `STREAM_CODEC`
- [ ] Compile check: `./gradlew.bat compileJava`
- [ ] Commit

### 2. Add optional `toast` field to element definitions

**Files:**
- `common/src/main/java/com/klikli_dev/modonomicon/research/data/ResearchFactDefinition.java`
- `common/src/main/java/com/klikli_dev/modonomicon/research/data/ResearchValueDefinition.java`
- `common/src/main/java/com/klikli_dev/modonomicon/research/data/ResearchNodeDefinition.java`

- [ ] Add `Optional<ResearchToastDefinition> toast` field to each definition's record and codec
- [ ] The field is optional — existing JSON without `toast` continues to work
- [ ] Compile check: `./gradlew.bat compileJava`
- [ ] Commit

### 3. Build toast index in `ResearchData`

**File:** `common/src/main/java/com/klikli_dev/modonomicon/research/data/ResearchData.java`

The validated `ResearchData` record needs a toast lookup so the server can quickly find toast definitions for triggered elements.

- [ ] Add `Map<Identifier, ResearchToastDefinition> factToasts`, `valueToasts`, `nodeToasts` fields to `ResearchData`
- [ ] In `validate()`, populate these maps from elements that have a non-empty toast definition
- [ ] Update `ResearchDataManager` default instance for new fields
- [ ] Compile check: `./gradlew.bat compileJava`
- [ ] Commit

### 4. Create `ResearchToastTrigger` and `ResearchToastMessage`

**New files:**
- `common/src/main/java/com/klikli_dev/modonomicon/research/networking/ResearchToastTrigger.java`
- `common/src/main/java/com/klikli_dev/modonomicon/networking/ResearchToastMessage.java`

- [ ] Create `ResearchToastTrigger` record with `ToastTriggerType`, `elementId`, `currentValue`
- [ ] Create `ResearchToastMessage` — server→client message carrying `List<ResearchToastTrigger>`
- [ ] Implement `STREAM_CODEC` for both
- [ ] Register message in NeoForge and Fabric networking
- [ ] Compile check: `./gradlew.bat compileJava`
- [ ] Commit

### 5. Collect toast triggers in `ResearchStateManager`

**File:** `common/src/main/java/com/klikli_dev/modonomicon/research/state/ResearchStateManager.java`

The manager needs to collect toast triggers as state changes occur. Approach: the manager populates a thread-local or passed-in collector during mutation.

- [ ] Add `ThreadLocal<List<ResearchToastTrigger>> TOAST_TRIGGER_COLLECTOR` 
- [ ] In `grantFact()`: if fact changed and has toast definition, add `FACT_GRANTED` trigger
- [ ] In `incrementValue()`: if value changed and has toast definition, add `VALUE_INCREMENTED` trigger with new value
- [ ] In `reevaluate()`: after node unlock, if node has toast definition, add `NODE_UNLOCKED` trigger
- [ ] Add `beginToastCollection()` / `endToastCollection()` methods that callers wrap around batches of state changes
- [ ] Compile check: `./gradlew.bat compileJava`
- [ ] Commit

### 6. Send toast triggers from hook services

**Files:**
- `common/src/main/java/com/klikli_dev/modonomicon/research/hook/ResearchHookService.java`
- `common/src/main/java/com/klikli_dev/modonomicon/research/hook/AdvancementResearchHookService.java`

- [ ] In each hook method (`onEntryViewedOnce`, `onItemCrafted`, `onItemAcquired`, `onAdvancement`):
  - Wrap state mutations in `beginToastCollection()` / `endToastCollection()`
  - If triggers collected, send `ResearchToastMessage` to the player
- [ ] In `replayAll()`: do NOT collect toasts (skip toast collection entirely)
- [ ] Compile check: `./gradlew.bat compileJava`
- [ ] Commit

### 7. Create `ResearchToast` client-side toast

**New file:** `common/src/main/java/com/klikli_dev/modonomicon/client/gui/toast/ResearchToast.java`

Implements `Toast`. Renders using vanilla's toast infrastructure.

- [ ] Create toast class with:
  - Background sprite: `toast/advancement`
  - Description line (first line): from toast definition's description, or default per type
  - Title line (second line): resolved translatable component with args
  - Icon: rendered via `BookIcon.render()` or skipped if null
  - Display time: 5000ms
  - Token: composite of `(triggerType, elementId)` for deduplication
  - For value toasts: token includes the current value so each increment shows a new toast
- [ ] Handle both item and texture icons via `BookIcon`
- [ ] Compile check: `./gradlew.bat compileJava`
- [ ] Commit

### 8. Handle `ResearchToastMessage` on client

**File:** `common/src/main/java/com/klikli_dev/modonomicon/networking/ResearchToastMessage.java` (client handler)

- [ ] In `onClientReceived()`:
  - For each trigger, look up the toast definition from `ResearchDataManager`
  - Resolve translatable title with args (for values, append current value as last arg)
  - Create `ResearchToast` instance and add to `Minecraft.getInstance().getToastManager()`
  - Skip if toast definition is missing (element had no toast configured)
  - Respect client config option for enabling/disabling research toasts
- [ ] Compile check: `./gradlew.bat compileJava`
- [ ] Commit

### 9. Add client config option

**File:** client config class (check existing config location)

- [ ] Add `showResearchToasts` boolean (default: `true`)
- [ ] Check in `ResearchToastMessage.onClientReceived()` before adding toasts
- [ ] Compile check: `./gradlew.bat compileJava`
- [ ] Commit

### 10. Add i18n keys

**File:** `common/src/main/resources/assets/modonomicon/lang/en_us.json`

- [ ] Add default description keys:
  ```json
  "gui.modonomicon.research.fact": "Research Fact",
  "gui.modonomicon.research.value": "Research Progress",
  "gui.modonomicon.research.node": "Research Unlocked"
  ```
- [ ] Compile check
- [ ] Commit

### 11. Update datagen API

**Files:**
- `common/src/main/java/com/klikli_dev/modonomicon/api/datagen/research/ResearchDataBuilder.java`
- `common/src/main/java/com/klikli_dev/modonomicon/api/datagen/research/ResearchFactSpec.java`
- `common/src/main/java/com/klikli_dev/modonomicon/api/datagen/research/ResearchValueSpec.java`
- `common/src/main/java/com/klikli_dev/modonomicon/api/datagen/research/ResearchNodeSpec.java`

- [ ] Add `ResearchToastSpec` authoring spec for datagen
- [ ] Add `toast(ResearchToastSpec)` overload to `fact()`, `value()`, `node()` methods
- [ ] Update specs to carry optional toast spec through to definition
- [ ] Update `ResearchDataManager.apply()` to write toast data to generated JSON
- [ ] Compile check: `./gradlew.bat compileJava`
- [ ] Commit

---

## Files to Create/Modify

### New files
1. `common/src/main/java/com/klikli_dev/modonomicon/research/data/ResearchToastDefinition.java` — toast data model
2. `common/src/main/java/com/klikli_dev/modonomicon/research/networking/ResearchToastTrigger.java` — trigger record
3. `common/src/main/java/com/klikli_dev/modonomicon/networking/ResearchToastMessage.java` — S2C network message
4. `common/src/main/java/com/klikli_dev/modonomicon/client/gui/toast/ResearchToast.java` — client toast

### Modified files
1. `research/data/ResearchFactDefinition.java` — optional `toast` field
2. `research/data/ResearchValueDefinition.java` — optional `toast` field
3. `research/data/ResearchNodeDefinition.java` — optional `toast` field
4. `research/data/ResearchData.java` — toast lookup maps, validation
5. `research/data/ResearchDataManager.java` — default instance update
6. `research/state/ResearchStateManager.java` — toast trigger collection
7. `research/hook/ResearchHookService.java` — send toast triggers
8. `research/hook/AdvancementResearchHookService.java` — send toast triggers
9. `neo/src/main/java/.../network/Networking.java` — register message
10. `fabric/src/main/java/.../network/Networking.java` — register message
11. Client config — `showResearchToasts` option
12. `assets/modonomicon/lang/en_us.json` — default i18n keys
13. `api/datagen/research/ResearchDataBuilder.java` — toast authoring API
14. `api/datagen/research/ResearchFactSpec.java` — toast spec
15. `api/datagen/research/ResearchValueSpec.java` — toast spec
16. `api/datagen/research/ResearchNodeSpec.java` — toast spec

---

## Edge Cases & Considerations

### No toasts on login/replay
`AdvancementResearchHookService.replayAll()` should NOT trigger toasts. The replay path skips toast collection entirely.

### Value toast deduplication
Each value increment produces a unique toast because the token includes the current value. This prevents the toast system from suppressing repeated increments while still allowing rapid increments to queue.

### Toast stacking
Vanilla's `ToastManager` handles up to 5 visible toasts. Multiple triggers firing simultaneously (e.g., crafting an item that grants a fact AND increments a value) will stack naturally.

### Missing toast definition
If a trigger references an element without a toast definition, the client silently skips it. This is the expected behavior — toast is opt-in per element.

### Missing book/element data on client
If research data hasn't loaded when the message arrives, the client should queue the toast triggers and process them once data is available. Alternatively, the sync order should guarantee research data loads before hooks fire.

### Texture-based icons
`BookIcon` handles both item and texture icons. The toast renderer should use `BookIcon.render()` or equivalent logic.

### Config option
`showResearchToasts` (default: true) in client config. Checked before adding any toast.

### Singleplayer / LAN
The message is server→client, works correctly in both dedicated server and singleplayer/LAN.

---

## Verification

1. Create a test fact with toast data, trigger it, verify toast appears
2. Create a test value with toast data, increment it, verify toast shows with correct count
3. Create a test node with toast data, unlock it, verify toast appears
4. Verify toast does NOT appear on login/replay
5. Verify toast respects config option
6. Verify multiple simultaneous triggers stack properly
7. Verify texture-based icons render correctly
8. Verify item-based icons render correctly
9. Verify elements without toast data produce no toast
10. Verify value toasts show correct increment count
11. `./gradlew.bat compileJava` passes
