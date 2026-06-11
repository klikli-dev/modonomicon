# Trigger Context Refactor Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Replace the `Identifier`-only trigger dispatch with a properly typed `TriggerContext` system where each trigger type defines its own authored target type and runtime context, eliminating the item-id/downcast hacks and enabling modder extensibility for arbitrary trigger systems.

**Architecture:** Introduce `TriggerContext` interface + per-trigger context records. Make `TriggerType`, `TriggerHandler`, and `ResearchHookDefinition` generic over `<TTarget, TContext>`. Handlers own matching logic with typed `matches(target, context)`. Indexing via optional handler-provided keys. Datagen becomes type-safe with typed `GenericIngress`.

**Tech Stack:** Java 21, Mojang mappings, Gradle, NeoForge/Fabric multiplatform.

---

## File Structure

| File | Action | Responsibility |
|------|--------|---------------|
| `research/hook/TriggerContext.java` | Create | Interface for all runtime event payloads |
| `research/hook/EntryViewedContext.java` | Create | Context record for entry viewed once |
| `research/hook/AdvancementContext.java` | Create | Context record for advancement earned |
| `research/hook/ItemCraftedContext.java` | Create | Context record for item crafted |
| `research/hook/ItemAcquiredContext.java` | Create | Context record for item acquired |
| `data/TriggerType.java` | Modify | Make generic: `TriggerType<TTarget, TContext>` |
| `research/hook/TriggerHandler.java` | Modify | Make generic, add `matches()` + index key methods |
| `research/data/ResearchHookDefinition.java` | Modify | Make generic: `ResearchHookDefinition<TTarget>`, remove `triggerTargetId`/`targetItem`/`matchComponents` |
| `registry/TriggerTypeRegistry.java` | Modify | Pass handlers, update register signatures, update `handler()` return |
| `research/hook/handlers/EntryViewedOnceTriggerHandler.java` | Modify | Implement typed handler |
| `research/hook/handlers/ItemCraftedTriggerHandler.java` | Modify | Implement typed handler with `matches()` |
| `research/hook/handlers/ItemAcquiredTriggerHandler.java` | Modify | Implement typed handler with `matches()` |
| `research/hook/handlers/AdvancementTriggerHandler.java` | Modify | Implement typed handler with `replayAll` |
| `research/data/ResearchData.java` | Modify | Change hook storage to `Map<TriggerType<?>, List<ResearchHookDefinition<?>>>`, add optional index |
| `research/data/ResearchDataManager.java` | Modify | Update `hooksFor` to use handler `matches()`, remove orphaned `itemHooksFor` |
| `research/hook/ResearchHookService.java` | Modify | Update `fire()` to accept `TriggerContext`, update convenience methods |
| `api/datagen/research/ResearchHookSpec.java` | Modify | Carry typed target instead of `triggerTargetId + targetItem` |
| `api/datagen/research/ResearchDataBuilder.java` | Modify | Update convenience methods |
| `api/datagen/research/ResearchIngressHelper.java` | Modify | Make generic ingress type-safe |
| `api/datagen/research/ItemCraftedHookSpec.java` | Modify | Remove `matchComponents`, target is `ItemStackTemplate` |
| `api/datagen/research/ItemAcquiredHookSpec.java` | Modify | Remove `matchComponents`, target is `ItemStackTemplate` |
| `api/datagen/research/EntryViewedOnceHookSpec.java` | Modify | Target stays `Identifier` |
| `api/datagen/research/AdvancementHookSpec.java` | Modify | Target stays `Identifier` |
| `datagen/research/DemoResearch.java` | Modify | Update demo to exercise new API |

---

## Task 1: Create `TriggerContext` interface and built-in context records

**Files:**
- Create: `common/src/main/java/com/klikli_dev/modonomicon/research/hook/TriggerContext.java`
- Create: `common/src/main/java/com/klikli_dev/modonomicon/research/hook/EntryViewedContext.java`
- Create: `common/src/main/java/com/klikli_dev/modonomicon/research/hook/AdvancementContext.java`
- Create: `common/src/main/java/com/klikli_dev/modonomicon/research/hook/ItemCraftedContext.java`
- Create: `common/src/main/java/com/klikli_dev/modonomicon/research/hook/ItemAcquiredContext.java`

- [ ] **Step 1: Create `TriggerContext` interface**

```java
package com.klikli_dev.modonomicon.research.hook;

import net.minecraft.server.level.ServerPlayer;

public interface TriggerContext {
    ServerPlayer player();
}
```

- [ ] **Step 2: Create `EntryViewedContext`**

```java
package com.klikli_dev.modonomicon.research.hook;

import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;

public record EntryViewedContext(ServerPlayer player, Identifier entryId) implements TriggerContext {
}
```

- [ ] **Step 3: Create `AdvancementContext`**

```java
package com.klikli_dev.modonomicon.research.hook;

import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;

public record AdvancementContext(ServerPlayer player, Identifier advancementId) implements TriggerContext {
}
```

- [ ] **Step 4: Create `ItemCraftedContext`**

```java
package com.klikli_dev.modonomicon.research.hook;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

public record ItemCraftedContext(ServerPlayer player, ItemStack stack) implements TriggerContext {
}
```

- [ ] **Step 5: Create `ItemAcquiredContext`**

```java
package com.klikli_dev.modonomicon.research.hook;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

public record ItemAcquiredContext(ServerPlayer player, ItemStack stack) implements TriggerContext {
}
```

- [ ] **Step 6: Verify compilation**

Run: `./gradlew.bat compileJava`

---

## Task 2: Make `TriggerType` generic

**Files:**
- Modify: `common/src/main/java/com/klikli_dev/modonomicon/data/TriggerType.java`

- [ ] **Step 1: Update `TriggerType` record**

Replace the existing record with:

```java
package com.klikli_dev.modonomicon.data;

import com.klikli_dev.modonomicon.research.hook.TriggerContext;
import com.klikli_dev.modonomicon.research.hook.TriggerHandler;
import com.mojang.serialization.MapCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;

import javax.annotation.Nullable;

public record TriggerType<TTarget, TContext extends TriggerContext>(
        Identifier id,
        @Nullable MapCodec<TTarget> targetCodec,
        @Nullable StreamCodec<RegistryFriendlyByteBuf, TTarget> targetStreamCodec,
        TriggerHandler<TTarget, TContext> handler
) {
}
```

Note: `targetCodec` and `targetStreamCodec` are now nullable for trigger types that only fire programmatically and never need JSON/network serialization of their target.

- [ ] **Step 2: Verify compilation (expect errors — callers need updating)**

Run: `./gradlew.bat compileJava`

---

## Task 3: Make `TriggerHandler` generic

**Files:**
- Modify: `common/src/main/java/com/klikli_dev/modonomicon/research/hook/TriggerHandler.java`

- [ ] **Step 1: Update `TriggerHandler` interface**

```java
package com.klikli_dev.modonomicon.research.hook;

import com.klikli_dev.modonomicon.data.TriggerType;
import com.klikli_dev.modonomicon.research.data.ResearchHookDefinition;
import com.klikli_dev.modonomicon.research.state.ResearchStateManager;
import net.minecraft.server.level.ServerPlayer;

import javax.annotation.Nullable;
import java.util.List;

public interface TriggerHandler<TTarget, TContext extends TriggerContext> {

    /**
     * Resolve hooks matching a specific event target.
     */
    List<ResearchHookDefinition<TTarget>> resolve(
            TriggerType<TTarget, TContext> type,
            ServerPlayer player,
            TContext context
    );

    /**
     * Check whether a single target matches a given context.
     */
    boolean matches(TTarget target, TContext context);

    /**
     * Return a stable index key for this target, or null if no fast-path index is available.
     */
    default @Nullable Object indexKey(TTarget target) {
        return null;
    }

    /**
     * Return a stable index key for this context, or null if no fast-path index is available.
     */
    default @Nullable Object indexKey(TContext context) {
        return null;
    }

    /**
     * Replay all hooks of this trigger type (optional).
     * Default returns false (no-op).
     */
    default boolean replayAll(ServerPlayer player, ResearchStateManager stateManager) {
        return false;
    }
}
```

- [ ] **Step 2: Verify compilation (expect errors — handlers and callers need updating)**

Run: `./gradlew.bat compileJava`

---

## Task 4: Make `ResearchHookDefinition` generic

**Files:**
- Modify: `common/src/main/java/com/klikli_dev/modonomicon/research/data/ResearchHookDefinition.java`

- [ ] **Step 1: Update `ResearchHookDefinition` record**

Remove `triggerTargetId`, `targetItem`, and `matchComponents` fields. Replace with typed `triggerTarget`:

```java
package com.klikli_dev.modonomicon.research.data;

import com.klikli_dev.modonomicon.data.TriggerType;
import com.klikli_dev.modonomicon.registry.TriggerTypeRegistry;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.Identifier;

import javax.annotation.Nullable;
import java.util.Optional;

public record ResearchHookDefinition<TTarget>(
        Identifier id,
        TriggerType<TTarget, ?> triggerType,
        TTarget triggerTarget,
        @Nullable Identifier factId,
        @Nullable Identifier valueId,
        int increment
) {
    // CODEC will be updated in Task 11 when we handle serialization
    // For now, this compiles the record shape
}
```

- [ ] **Step 2: Verify compilation (expect errors — serialization and callers need updating)**

Run: `./gradlew.bat compileJava`

---

## Task 5: Update `TriggerTypeRegistry` with typed handlers

**Files:**
- Modify: `common/src/main/java/com/klikli_dev/modonomicon/registry/TriggerTypeRegistry.java`

- [ ] **Step 1: Update registry to store typed types and pass handlers**

```java
package com.klikli_dev.modonomicon.registry;

import com.klikli_dev.modonomicon.Modonomicon;
import com.klikli_dev.modonomicon.data.DispatchCodecRegistry;
import com.klikli_dev.modonomicon.data.TriggerType;
import com.klikli_dev.modonomicon.research.hook.*;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.Items;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class TriggerTypeRegistry {

    private static final DispatchCodecRegistry<TriggerType<?, ?>> TYPES =
            new DispatchCodecRegistry<>(TriggerType::id, "trigger type");

    public static final TriggerType<Identifier, EntryViewedContext> ENTRY_VIEWED_ONCE = register(
            Modonomicon.loc("entry_viewed_once"),
            Identifier.CODEC.fieldOf(""),
            Identifier.STREAM_CODEC.cast(),
            new EntryViewedOnceTriggerHandler()
    );

    public static final TriggerType<ItemStackTemplate, ItemCraftedContext> ITEM_CRAFTED = register(
            Modonomicon.loc("item_crafted"),
            ItemStackTemplate.CODEC.fieldOf(""),
            ItemStackTemplate.STREAM_CODEC.cast(),
            new ItemCraftedTriggerHandler()
    );

    public static final TriggerType<ItemStackTemplate, ItemAcquiredContext> ITEM_ACQUIRED = register(
            Modonomicon.loc("item_acquired"),
            ItemStackTemplate.CODEC.fieldOf(""),
            ItemStackTemplate.STREAM_CODEC.cast(),
            new ItemAcquiredTriggerHandler()
    );

    public static final TriggerType<Identifier, AdvancementContext> ADVANCEMENT = register(
            Modonomicon.loc("advancement"),
            Identifier.CODEC.fieldOf(""),
            Identifier.STREAM_CODEC.cast(),
            new AdvancementTriggerHandler()
    );

    private TriggerTypeRegistry() {
    }

    public static void bootstrap() {
    }

    @SuppressWarnings("unchecked")
    public static <TTarget, TContext extends TriggerContext> TriggerType<TTarget, TContext> register(
            Identifier id,
            MapCodec<TTarget> codec,
            StreamCodec<RegistryFriendlyByteBuf, TTarget> streamCodec,
            TriggerHandler<TTarget, TContext> handler
    ) {
        var type = (TriggerType<TTarget, TContext>) (TriggerType<?, ?>)
                TYPES.register(id, new TriggerType<>(id, codec, streamCodec, handler));
        return type;
    }

    @SuppressWarnings("unchecked")
    public static <TTarget, TContext extends TriggerContext> TriggerHandler<TTarget, TContext> handler(
            TriggerType<TTarget, TContext> type
    ) {
        return type.handler();
    }

    public static Codec<TriggerType<?, ?>> codec() {
        return TYPES.byNameCodec();
    }

    public static StreamCodec<RegistryFriendlyByteBuf, TriggerType<?, ?>> streamCodec() {
        return TYPES.byNameStreamCodec();
    }
}
```

Note the `@SuppressWarnings("unchecked")` casts — these are the single controlled bridge between the erased registry and the typed API.

- [ ] **Step 2: Verify compilation**

Run: `./gradlew.bat compileJava`

---

## Task 6: Implement typed handlers

**Files:**
- Modify: `common/src/main/java/com/klikli_dev/modonomicon/research/hook/handlers/EntryViewedOnceTriggerHandler.java`
- Modify: `common/src/main/java/com/klikli_dev/modonomicon/research/hook/handlers/ItemCraftedTriggerHandler.java`
- Modify: `common/src/main/java/com/klikli_dev/modonomicon/research/hook/handlers/ItemAcquiredTriggerHandler.java`
- Modify: `common/src/main/java/com/klikli_dev/modonomicon/research/hook/handlers/AdvancementTriggerHandler.java`

- [ ] **Step 1: Update `EntryViewedOnceTriggerHandler`**

```java
package com.klikli_dev.modonomicon.research.hook.handlers;

import com.klikli_dev.modonomicon.data.TriggerType;
import com.klikli_dev.modonomicon.research.data.ResearchHookDefinition;
import com.klikli_dev.modonomicon.research.data.ResearchDataManager;
import com.klikli_dev.modonomicon.research.hook.EntryViewedContext;
import com.klikli_dev.modonomicon.research.hook.TriggerHandler;
import net.minecraft.server.level.ServerPlayer;

import javax.annotation.Nullable;
import java.util.List;

public class EntryViewedOnceTriggerHandler implements TriggerHandler<Identifier, EntryViewedContext> {

    @Override
    public List<ResearchHookDefinition<Identifier>> resolve(
            TriggerType<Identifier, EntryViewedContext> type,
            ServerPlayer player,
            EntryViewedContext context
    ) {
        return ResearchDataManager.get().hooksFor(type, context.entryId());
    }

    @Override
    public boolean matches(Identifier target, EntryViewedContext context) {
        return target.equals(context.entryId());
    }

    @Override
    public @Nullable Object indexKey(Identifier target) {
        return target;
    }

    @Override
    public @Nullable Object indexKey(EntryViewedContext context) {
        return context.entryId();
    }
}
```

Wait — this handler references `Identifier` directly. It needs the import. Full corrected file:

```java
package com.klikli_dev.modonomicon.research.hook.handlers;

import com.klikli_dev.modonomicon.data.TriggerType;
import com.klikli_dev.modonomicon.research.data.ResearchHookDefinition;
import com.klikli_dev.modonomicon.research.data.ResearchDataManager;
import com.klikli_dev.modonomicon.research.hook.EntryViewedContext;
import com.klikli_dev.modonomicon.research.hook.TriggerHandler;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;

import javax.annotation.Nullable;
import java.util.List;

public class EntryViewedOnceTriggerHandler implements TriggerHandler<Identifier, EntryViewedContext> {

    @Override
    public List<ResearchHookDefinition<Identifier>> resolve(
            TriggerType<Identifier, EntryViewedContext> type,
            ServerPlayer player,
            EntryViewedContext context
    ) {
        return ResearchDataManager.get().hooksFor(type, context.entryId());
    }

    @Override
    public boolean matches(Identifier target, EntryViewedContext context) {
        return target.equals(context.entryId());
    }

    @Override
    public @Nullable Object indexKey(Identifier target) {
        return target;
    }

    @Override
    public @Nullable Object indexKey(EntryViewedContext context) {
        return context.entryId();
    }
}
```

- [ ] **Step 2: Update `ItemCraftedTriggerHandler`**

```java
package com.klikli_dev.modonomicon.research.hook.handlers;

import com.klikli_dev.modonomicon.data.TriggerType;
import com.klikli_dev.modonomicon.research.data.ResearchHookDefinition;
import com.klikli_dev.modonomicon.research.data.ResearchDataManager;
import com.klikli_dev.modonomicon.research.hook.ItemCraftedContext;
import com.klikli_dev.modonomicon.research.hook.TriggerHandler;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStackTemplate;

import javax.annotation.Nullable;
import java.util.List;

public class ItemCraftedTriggerHandler implements TriggerHandler<ItemStackTemplate, ItemCraftedContext> {

    @Override
    public List<ResearchHookDefinition<ItemStackTemplate>> resolve(
            TriggerType<ItemStackTemplate, ItemCraftedContext> type,
            ServerPlayer player,
            ItemCraftedContext context
    ) {
        return ResearchDataManager.get().hooksForType(type).stream()
                .filter(h -> matches(h.triggerTarget(), context))
                .toList();
    }

    @Override
    public boolean matches(ItemStackTemplate target, ItemCraftedContext context) {
        return target.matches(context.stack());
    }

    @Override
    public @Nullable Object indexKey(ItemStackTemplate target) {
        return target.item().unwrapKey().map(ResourceKey::identifier).orElse(null);
    }

    @Override
    public @Nullable Object indexKey(ItemCraftedContext context) {
        return context.stack().getItem().builtInRegistryHolder().unwrapKey()
                .map(ResourceKey::identifier).orElse(null);
    }
}
```

- [ ] **Step 3: Update `ItemAcquiredTriggerHandler`**

```java
package com.klikli_dev.modonomicon.research.hook.handlers;

import com.klikli_dev.modonomicon.data.TriggerType;
import com.klikli_dev.modonomicon.research.data.ResearchHookDefinition;
import com.klikli_dev.modonomicon.research.data.ResearchDataManager;
import com.klikli_dev.modonomicon.research.hook.ItemAcquiredContext;
import com.klikli_dev.modonomicon.research.hook.TriggerHandler;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStackTemplate;

import javax.annotation.Nullable;
import java.util.List;

public class ItemAcquiredTriggerHandler implements TriggerHandler<ItemStackTemplate, ItemAcquiredContext> {

    @Override
    public List<ResearchHookDefinition<ItemStackTemplate>> resolve(
            TriggerType<ItemStackTemplate, ItemAcquiredContext> type,
            ServerPlayer player,
            ItemAcquiredContext context
    ) {
        return ResearchDataManager.get().hooksForType(type).stream()
                .filter(h -> matches(h.triggerTarget(), context))
                .toList();
    }

    @Override
    public boolean matches(ItemStackTemplate target, ItemAcquiredContext context) {
        return target.matches(context.stack());
    }

    @Override
    public @Nullable Object indexKey(ItemStackTemplate target) {
        return target.item().unwrapKey().map(ResourceKey::identifier).orElse(null);
    }

    @Override
    public @Nullable Object indexKey(ItemAcquiredContext context) {
        return context.stack().getItem().builtInRegistryHolder().unwrapKey()
                .map(ResourceKey::identifier).orElse(null);
    }
}
```

- [ ] **Step 4: Update `AdvancementTriggerHandler`**

```java
package com.klikli_dev.modonomicon.research.hook.handlers;

import com.klikli_dev.modonomicon.data.TriggerType;
import com.klikli_dev.modonomicon.research.data.ResearchDataManager;
import com.klikli_dev.modonomicon.research.data.ResearchHookDefinition;
import com.klikli_dev.modonomicon.research.hook.AdvancementContext;
import com.klikli_dev.modonomicon.research.hook.TriggerHandler;
import com.klikli_dev.modonomicon.research.state.ResearchStateManager;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;

import javax.annotation.Nullable;
import java.util.List;

public class AdvancementTriggerHandler implements TriggerHandler<Identifier, AdvancementContext> {

    @Override
    public List<ResearchHookDefinition<Identifier>> resolve(
            TriggerType<Identifier, AdvancementContext> type,
            ServerPlayer player,
            AdvancementContext context
    ) {
        return ResearchDataManager.get().hooksFor(type, context.advancementId());
    }

    @Override
    public boolean matches(Identifier target, AdvancementContext context) {
        return target.equals(context.advancementId());
    }

    @Override
    public @Nullable Object indexKey(Identifier target) {
        return target;
    }

    @Override
    public @Nullable Object indexKey(AdvancementContext context) {
        return context.advancementId();
    }

    @Override
    public boolean replayAll(ServerPlayer player, ResearchStateManager stateManager) {
        boolean changed = false;
        var serverAdvancements = player.level().getServer().getAdvancements();
        var playerAdvancements = player.getAdvancements();
        for (var hook : ResearchDataManager.get().hooksForType(TriggerTypeRegistry.ADVANCEMENT)) {
            var holder = serverAdvancements.get(hook.triggerTarget());
            if (holder == null) {
                continue;
            }
            var progress = playerAdvancements.getOrStartProgress(holder);
            if (!progress.isDone()) {
                continue;
            }
            if (hook.factId() != null) {
                changed |= stateManager.grantFact(player, hook.factId());
            } else if (hook.valueId() != null) {
                changed |= stateManager.incrementValue(player, hook.valueId(), hook.increment());
            }
        }
        return changed;
    }
}
```

Note: `hook.triggerTarget()` returns `Identifier` (the advancement ID), replacing the old `hook.triggerTargetId()`.

- [ ] **Step 5: Verify compilation**

Run: `./gradlew.bat compileJava`

---

## Task 7: Update `ResearchData` and `ResearchDataManager`

**Files:**
- Modify: `common/src/main/java/com/klikli_dev/modonomicon/research/data/ResearchData.java`
- Modify: `common/src/main/java/com/klikli_dev/modonomicon/research/data/ResearchDataManager.java`

- [ ] **Step 1: Update `ResearchData` record and methods**

Change the hook storage from `Map<TriggerType, Map<Identifier, List<ResearchHookDefinition>>>` to `Map<TriggerType<?, ?>, List<ResearchHookDefinition<?>>>`. Add an optional secondary index map.

Update `hooksFor` to use handler's `matches()` + index keys. Update `hooksForType` similarly. Update `validate()` to iterate the new hook shape.

Key changes:
- `hooksByType` replaces `hooksByTypeAndTarget`
- `hooksByTypeAndIndex` is the optional secondary index
- `hooksFor(type, context)` takes a typed context and returns resolved hooks
- `hooksForType(type)` returns all hooks for a type
- `validate()` builds the index from handler's `indexKey(target)`

- [ ] **Step 2: Update `ResearchDataManager`**

Remove the orphaned `itemHooksFor(TriggerType, ItemStack)` method (it was never called). Update `hooksFor` to accept typed context. Update `hooksForType` accordingly.

- [ ] **Step 3: Verify compilation**

Run: `./gradlew.bat compileJava`

---

## Task 8: Update `ResearchHookService`

**Files:**
- Modify: `common/src/main/java/com/klikli_dev/modonomicon/research/hook/ResearchHookService.java`

- [ ] **Step 1: Update `fire()` to accept `TriggerContext`**

```java
public <TTarget, TContext extends TriggerContext> boolean fire(
        TriggerType<TTarget, TContext> triggerType,
        TContext context
) {
    var hooks = triggerType.resolveFor(context.player(), context);
    return this.applyHooks(context.player(), hooks);
}
```

- [ ] **Step 2: Update convenience methods to construct contexts**

```java
public boolean onEntryViewedOnce(ServerPlayer player, Identifier entryId) {
    return this.fire(TriggerTypeRegistry.ENTRY_VIEWED_ONCE,
            new EntryViewedContext(player, entryId));
}

public boolean onItemCrafted(ServerPlayer player, ItemStack stack) {
    return this.fire(TriggerTypeRegistry.ITEM_CRAFTED,
            new ItemCraftedContext(player, stack));
}

public boolean onItemAcquired(ServerPlayer player, ItemStack stack) {
    return this.fire(TriggerTypeRegistry.ITEM_ACQUIRED,
            new ItemAcquiredContext(player, stack));
}

public boolean onAdvancement(ServerPlayer player, Identifier advancementId) {
    return this.fire(TriggerTypeRegistry.ADVANCEMENT,
            new AdvancementContext(player, advancementId));
}
```

- [ ] **Step 3: Update `canProgress` to accept `TriggerContext`**

```java
public <TTarget, TContext extends TriggerContext> boolean canProgress(
        TriggerType<TTarget, TContext> triggerType,
        TContext context
) {
    var hooks = triggerType.resolveFor(context.player(), context);
    var state = this.stateManager.getStateFor(context.player());
    for (var hook : hooks) {
        if (hook.factId() != null && !state.hasFact(hook.factId())) {
            return true;
        }
    }
    return false;
}
```

- [ ] **Step 4: Update `canProgressByType` to work with typed hooks**

Update the private method to use the new `hooksForType` return type.

- [ ] **Step 5: Update `replayAll` and `replayAdvancements`**

These should pass the correct handler method. `replayAdvancements` calls `replayAll` on the advancement handler.

- [ ] **Step 6: Update `needsAdvancementReplay`**

Update to use the new API (no more passing `null` as targetId — use `replayAll` or scan the typed hooks).

- [ ] **Step 7: Verify compilation**

Run: `./gradlew.bat compileJava`

---

## Task 9: Update datagen layer — `ResearchHookSpec`

**Files:**
- Modify: `common/src/main/java/com/klikli_dev/modonomicon/api/datagen/research/ResearchHookSpec.java`

- [ ] **Step 1: Update `ResearchHookSpec` to carry typed target**

Replace `triggerTargetId + targetItem + matchComponents` with a typed `triggerTarget`:

```java
public record ResearchHookSpec<TTarget>(
        Identifier id,
        TriggerType<TTarget, ?> triggerType,
        TTarget triggerTarget,
        @Nullable ResearchFactRef factRef,
        @Nullable ResearchValueRef valueRef,
        int increment
) {
    // Convenience factory methods for item-based hooks
    public static ResearchHookSpec<ItemStackTemplate> grantFact(
            Identifier id, TriggerType<ItemStackTemplate, ?> triggerType,
            ItemStackTemplate targetItem
    ) {
        return new ResearchHookSpec<>(id, triggerType, targetItem, null, null, 1);
    }

    public static ResearchHookSpec<ItemStackTemplate> incrementValue(
            Identifier id, TriggerType<ItemStackTemplate, ?> triggerType,
            ItemStackTemplate targetItem, ResearchValueRef valueRef, int increment
    ) {
        return new ResearchHookSpec<>(id, triggerType, targetItem, null, valueRef, increment);
    }

    // Generic factory for arbitrary target types
    public static <TTarget> ResearchHookSpec<TTarget> of(
            Identifier id, TriggerType<TTarget, ?> triggerType,
            TTarget target, @Nullable ResearchFactRef factRef,
            @Nullable ResearchValueRef valueRef, int increment
    ) {
        return new ResearchHookSpec<>(id, triggerType, target, factRef, valueRef, increment);
    }

    public ResearchHookDefinition<TTarget> toDefinition() {
        return new ResearchHookDefinition<>(
                this.id,
                this.triggerType,
                this.triggerTarget,
                this.factRef != null ? this.factRef.id() : null,
                this.valueRef != null ? this.valueRef.id() : null,
                this.increment
        );
    }
}
```

- [ ] **Step 2: Verify compilation**

Run: `./gradlew.bat compileJava`

---

## Task 10: Update `ResearchDataBuilder` and `ResearchIngressHelper`

**Files:**
- Modify: `common/src/main/java/com/klikli_dev/modonomicon/api/datagen/research/ResearchDataBuilder.java`
- Modify: `common/src/main/java/com/klikli_dev/modonomicon/api/datagen/research/ResearchIngressHelper.java`

- [ ] **Step 1: Update `ResearchDataBuilder` convenience methods**

Update `grantFactOnItemCrafted`, `incrementValueOnItemCrafted`, etc. to use `ResearchHookSpec.grantFact(id, triggerType, targetItem)` (without `matchComponents`).

Update the generic `grantFact(path, triggerType, targetId, factRef)` to accept a typed target:

```java
public <TTarget> void grantFact(String path, TriggerType<TTarget, ?> triggerType,
        TTarget target, ResearchFactRef factRef) {
    this.hooks.add(ResearchHookSpec.of(
            Identifier.fromNamespaceAndPath(this.namespace, path),
            triggerType, target, factRef, null, 1
    ));
}
```

- [ ] **Step 2: Update `ResearchIngressHelper` generic ingress**

Make `GenericIngress` generic over the target type:

```java
public <TTarget> GenericIngress<TTarget> on(
        TriggerType<TTarget, ?> triggerType, TTarget target) {
    return new GenericIngress<>(this.research, triggerType, target);
}
```

Update `GenericIngress` inner class to be generic.

- [ ] **Step 3: Verify compilation**

Run: `./gradlew.bat compileJava`

---

## Task 11: Update hook spec classes

**Files:**
- Modify: `common/src/main/java/com/klikli_dev/modonomicon/api/datagen/research/ItemCraftedHookSpec.java`
- Modify: `common/src/main/java/com/klikli_dev/modonomicon/api/datagen/research/ItemAcquiredHookSpec.java`
- Modify: `common/src/main/java/com/klikli_dev/modonomicon/api/datagen/research/EntryViewedOnceHookSpec.java`
- Modify: `common/src/main/java/com/klikli_dev/modonomicon/api/datagen/research/AdvancementHookSpec.java` (if exists)

- [ ] **Step 1: Update `ItemCraftedHookSpec`**

Remove `matchComponents` field. The `toDefinition()` now creates a `ResearchHookDefinition<ItemStackTemplate>` with `targetItem` as the `triggerTarget`:

```java
public record ItemCraftedHookSpec(
        Identifier id,
        ItemStackTemplate targetItem,
        ResearchFactRef factRef,
        ResearchValueRef valueRef,
        int increment
) {
    // ... factory methods without matchComponents ...

    public ResearchHookDefinition<ItemStackTemplate> toDefinition() {
        return new ResearchHookDefinition<>(
                this.id,
                TriggerTypeRegistry.ITEM_CRAFTED,
                this.targetItem,
                this.factRef != null ? this.factRef.id() : null,
                this.valueRef != null ? this.valueRef.id() : null,
                this.increment
        );
    }
}
```

- [ ] **Step 2: Update `ItemAcquiredHookSpec`**

Same pattern as `ItemCraftedHookSpec` — remove `matchComponents`, use typed target.

- [ ] **Step 3: Update `EntryViewedOnceHookSpec`**

No structural change needed (target was already `Identifier`), just update `toDefinition()` to return `ResearchHookDefinition<Identifier>`.

- [ ] **Step 4: Verify compilation**

Run: `./gradlew.bat compileJava`

---

## Task 12: Update demo research and fix remaining callers

**Files:**
- Modify: `common/src/main/java/com/klikli_dev/modonomicon/datagen/research/DemoResearch.java`
- Modify: `common/src/main/java/com/klikli_dev/modonomicon/api/datagen/research/ResearchDataBuilder.java` (remaining methods)

- [ ] **Step 1: Update demo to exercise new API**

Review `DemoResearch.java` and update any calls that used `matchComponents` or the old `grantFactOnItemCrafted(path, item, fact, matchComponents)` overloads.

- [ ] **Step 2: Audit all remaining callers**

Search for any remaining references to:
- `triggerTargetId` (should be `triggerTarget`)
- `targetItem` on `ResearchHookDefinition`
- `matchComponents` on any definition/spec
- `itemHooksFor` on `ResearchDataManager`
- Old `fire(player, triggerType, Identifier)` calls

- [ ] **Step 3: Verify compilation**

Run: `./gradlew.bat compileJava`

---

## Task 13: Update JSON serialization

**Files:**
- Modify: `common/src/main/java/com/klikli_dev/modonomicon/research/data/ResearchHookDefinition.java` (codec)
- Modify: `common/src/main/java/com/klikli_dev/modonomicon/research/data/ResearchData.java` (validate)

- [ ] **Step 1: Update `ResearchHookDefinition.CODEC`**

The codec must now be dynamic — dispatching on the trigger type's target codec to decode the target. The hook definition codec should:

1. Decode `trigger_type` to get the `TriggerType<?, ?>`
2. Use the type's `targetCodec` to decode the target field
3. Decode `fact_id`, `value_id`, `increment` as before

This is the most complex serialization change. The target field name should be `trigger_target` (replacing `event_target_id` and `event_target_item`).

For backward compatibility, consider a migration that:
- If `event_target_item` is present, treat it as the `ItemStackTemplate` target for item hooks
- If `event_target_id` is present, treat it as the `Identifier` target for non-item hooks
- If `trigger_target` is present, use it directly

- [ ] **Step 2: Update `ResearchData.validate()`**

Remove validation of `targetItem`/`matchComponents`. Update hook iteration to use the new generic shape. Build the secondary index from handler's `indexKey`.

- [ ] **Step 3: Verify compilation and test with existing hooks.json**

Run: `./gradlew.bat compileJava`
Run: `./gradlew.bat runData` to verify datagen produces valid JSON

---

## Task 14: Update platform wiring

**Files:**
- Modify: `neo/src/main/java/com/klikli_dev/modonomicon/ModonomiconNeo.java`
- Modify: `fabric/src/main/java/com/klikli_dev/modonomicon/mixin/MixinResultSlot.java`
- Modify: `common/src/main/java/com/klikli_dev/modonomicon/mixin/MixinAbstractContainerMenu.java`

- [ ] **Step 1: Verify NeoForge wiring**

The NeoForge event handlers call `ResearchServices.hooks().onItemCrafted(player, stack)` etc. — these convenience methods already accept `ItemStack` and construct the context internally, so no changes should be needed at the call sites. Verify.

- [ ] **Step 2: Verify Fabric wiring**

Same check for Fabric mixin call sites.

- [ ] **Step 3: Verify compilation**

Run: `./gradlew.bat compileJava`

---

## Task 15: Update modonomicon-docs

**Files:**
- Modify: `J:\Projects\Minecraft\modonomicon-docs\versioned_docs\version-26.1.2\advanced\custom-hooks.md`
- Modify: `J:\Projects\Minecraft\modonomicon-docs\versioned_docs\version-26.1.2\basics\research\research.md`
- Modify: `J:\Projects\Minecraft\modonomicon-docs\versioned_docs\version-26.1.2\basics\research\datagen.md`

- [ ] **Step 1: Update `advanced/custom-hooks.md`**

Replace the entire file with updated documentation reflecting the new typed API:

- Update `TriggerHandler` interface signature to show generics: `TriggerHandler<TTarget, TContext extends TriggerContext>`
- Add `matches(TTarget, TContext)` to the interface description
- Add `indexKey(TTarget)` and `indexKey(TContext)` documentation
- Update built-in trigger types table to show target types (`Identifier` vs `ItemStackTemplate`) and context types
- Update handler examples to show typed signatures
- Update registration example to show generic `TriggerType<TTarget, TContext>`
- Update wiring examples to show `fire(triggerType, context)` instead of `fire(player, triggerType, targetId)`
- Update datagen example to show typed `GenericIngress<TTarget>`
- Update JSON format to show `trigger_target` field (item hooks store `ItemStackTemplate` directly)
- Add a full "modder example" section showing a custom trigger with compound target

- [ ] **Step 2: Update `basics/research/research.md`**

Update the hooks table to mention target types. Add a note that item hooks match by `ItemStackTemplate` (item + optional components). No separate `match_components` flag.

- [ ] **Step 3: Update `basics/research/datagen.md`**

Update the research ingress section to show typed API. Note that `onItemCrafted` now takes `ItemStackTemplate` directly and component matching is implicit in the template.

---

## Task 16: Final validation

- [ ] **Step 1: Full compilation check**

Run: `./gradlew.bat compileJava`

- [ ] **Step 2: Run data generators**

Run: `./gradlew.bat runData`

- [ ] **Step 3: Run client**

Run: `./gradlew.bat runClient`

- [ ] **Step 4: Commit all changes**

```bash
git add -A
git commit -m "refactor: typed trigger context system for research hooks

- Introduce TriggerContext interface and per-trigger context records
- Make TriggerType, TriggerHandler, ResearchHookDefinition generic
- Handlers own matching logic with typed matches(target, context)
- Item hooks use ItemStackTemplate as primary target (no matchComponents flag)
- Modders register custom triggers with arbitrary target/context types
- Datagen API becomes type-safe with GenericIngress<TTarget>
- Remove orphaned itemHooksFor() and matchComponents hacks"
```
