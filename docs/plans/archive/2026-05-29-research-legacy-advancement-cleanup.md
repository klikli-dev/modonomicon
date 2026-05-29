# Research Legacy Advancement Cleanup Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Remove the obsolete legacy advancement networking/client-cache path and the advancement-locking config plumbing while preserving the current research-backed advancement flow.

**Architecture:** Keep advancement progression research-owned. Delete the old request/response advancement packet path and the client-side advancement cache that supported direct book-side advancement checks. Remove the now-unused server config service and per-loader advancement-locking toggle without changing the current Fabric/Neo research ingress or the explicit rejection of removed legacy condition ids.

**Tech Stack:** Java 25, Gradle 9, Mojang/Fabric/Neo networking, Modonomicon common + Fabric + Neo modules, Fabric datagen, Neo clientData datagen, manual verification.

---

## File Map

### Delete
- `common/src/main/java/com/klikli_dev/modonomicon/networking/RequestAdvancementMessage.java` - old serverbound request for direct advancement lookup
- `common/src/main/java/com/klikli_dev/modonomicon/networking/SendAdvancementToClientMessage.java` - old clientbound advancement payload for the book-side cache
- `common/src/main/java/com/klikli_dev/modonomicon/platform/services/ServerConfigHelper.java` - obsolete common service interface for advancement-lock skipping
- `fabric/src/main/java/com/klikli_dev/modonomicon/config/FabricServerConfigHelper.java` - Fabric implementation of the removed service
- `neo/src/main/java/com/klikli_dev/modonomicon/config/NeoServerConfigHelper.java` - Neo implementation of the removed service
- `fabric/src/main/resources/META-INF/services/com.klikli_dev.modonomicon.platform.services.ServerConfigHelper` - Fabric service-loader entry for the removed service
- `neo/src/main/resources/META-INF/services/com.klikli_dev.modonomicon.platform.services.ServerConfigHelper` - Neo service-loader entry for the removed service

### Modify
- `common/src/main/java/com/klikli_dev/modonomicon/data/BookDataManager.java` - remove the client-side advancement cache and its reload reset logic
- `common/src/main/java/com/klikli_dev/modonomicon/platform/Services.java` - stop loading the removed config service
- `common/src/main/java/com/klikli_dev/modonomicon/datagen/EnUsProvider.java` - remove the obsolete translation key source for advancement-lock skipping
- `fabric/src/main/java/com/klikli_dev/modonomicon/network/Networking.java` - stop registering the removed advancement packets
- `fabric/src/main/java/com/klikli_dev/modonomicon/network/ClientNetworking.java` - stop registering the removed client receiver
- `fabric/src/main/java/com/klikli_dev/modonomicon/config/ServerConfig.java` - remove the obsolete `disableAdvancementLocking` server config entry
- `neo/src/main/java/com/klikli_dev/modonomicon/network/Networking.java` - stop registering the removed advancement packets
- `neo/src/main/java/com/klikli_dev/modonomicon/config/ServerConfig.java` - remove the obsolete `disableAdvancementLocking` server config entry
- `fabric/src/generated/resources/assets/modonomicon/lang/en_us.json` - regenerated after removing the config translation source
- `neo/src/generated/resources/assets/modonomicon/lang/en_us.json` - regenerated after removing the config translation source

### Reuse Without Modification
- `common/src/main/java/com/klikli_dev/modonomicon/book/conditions/BookCondition.java` - must keep explicit rejection for `modonomicon:entry_read`, `modonomicon:entry_unlocked`, and `modonomicon:advancement`
- `common/src/main/java/com/klikli_dev/modonomicon/research/hook/AdvancementResearchHookService.java` - remains the authoritative advancement-to-research ingress
- `common/src/main/java/com/klikli_dev/modonomicon/research/ResearchServices.java` - keeps exposing advancement research ingress
- `common/src/main/java/com/klikli_dev/modonomicon/bookstate/BookUnlockStateManager.java` - stays as the downstream recompute/sync owner after research changes
- `fabric/src/main/java/com/klikli_dev/modonomicon/ModonomiconFabric.java` - keep current startup/config init behavior except what falls out of config shape changes
- `neo/src/main/java/com/klikli_dev/modonomicon/ModonomiconNeo.java` - keep the current advancement-earned research-first flow intact

### Out of Scope For This Plan
- `forge/**` runtime and config cleanup - the Forge module is excluded in `settings.gradle`, so do not fold Forge-specific cleanup into this slice
- forge generated resource cleanup - explicitly deferred by product direction
- any compatibility replacement for advancement-lock skipping

## Task 1: Remove The Legacy Advancement Packet Path

**Files:**
- Delete: `common/src/main/java/com/klikli_dev/modonomicon/networking/RequestAdvancementMessage.java`
- Delete: `common/src/main/java/com/klikli_dev/modonomicon/networking/SendAdvancementToClientMessage.java`
- Modify: `fabric/src/main/java/com/klikli_dev/modonomicon/network/Networking.java`
- Modify: `fabric/src/main/java/com/klikli_dev/modonomicon/network/ClientNetworking.java`
- Modify: `neo/src/main/java/com/klikli_dev/modonomicon/network/Networking.java`

- [ ] **Step 1: Remove the Fabric server receiver and payload registration for `RequestAdvancementMessage`**

Edit `fabric/src/main/java/com/klikli_dev/modonomicon/network/Networking.java` so these lines are removed:

```java
ServerPlayNetworking.registerGlobalReceiver(RequestAdvancementMessage.TYPE, new ServerMessageHandler<>());
```

and:

```java
PayloadTypeRegistry.serverboundPlay().register(RequestAdvancementMessage.TYPE, RequestAdvancementMessage.STREAM_CODEC);
```

The surrounding code should still keep the neighboring book-state packets, for example:

```java
ServerPlayNetworking.registerGlobalReceiver(RequestSyncBookStatesMessage.TYPE, new ServerMessageHandler<>());
ServerPlayNetworking.registerGlobalReceiver(AddBookmarkMessage.TYPE, new ServerMessageHandler<>());
```

- [ ] **Step 2: Remove the Fabric client payload registration for `SendAdvancementToClientMessage`**

In the same file, remove this clientbound payload registration:

```java
PayloadTypeRegistry.clientboundPlay().register(SendAdvancementToClientMessage.TYPE, SendAdvancementToClientMessage.STREAM_CODEC);
```

and in `fabric/src/main/java/com/klikli_dev/modonomicon/network/ClientNetworking.java` remove:

```java
ClientPlayNetworking.registerGlobalReceiver(SendAdvancementToClientMessage.TYPE, new ClientMessageHandler<>());
```

The remaining client registrations should still include entries like:

```java
ClientPlayNetworking.registerGlobalReceiver(ReloadResourcesOnClientMessage.TYPE, new ClientMessageHandler<>());
ClientPlayNetworking.registerGlobalReceiver(OpenBookOnClientMessage.TYPE, new ClientMessageHandler<>());
```

- [ ] **Step 3: Remove the Neo payload registrations for the legacy advancement packets**

Edit `neo/src/main/java/com/klikli_dev/modonomicon/network/Networking.java` and delete these registrations exactly:

```java
registrar.playToServer(RequestAdvancementMessage.TYPE, RequestAdvancementMessage.STREAM_CODEC, MessageHandler::handle);
registrar.playToClient(SendAdvancementToClientMessage.TYPE, SendAdvancementToClientMessage.STREAM_CODEC, MessageHandler::handle);
```

The result should still keep the adjacent sync packets intact, for example:

```java
registrar.playToServer(RequestSyncBookStatesMessage.TYPE, RequestSyncBookStatesMessage.STREAM_CODEC, MessageHandler::handle);
registrar.playToClient(SyncBookDataMessage.TYPE, SyncBookDataMessage.STREAM_CODEC, MessageHandler::handle);
```

- [ ] **Step 4: Delete the obsolete message classes**

Delete these files:

```text
common/src/main/java/com/klikli_dev/modonomicon/networking/RequestAdvancementMessage.java
common/src/main/java/com/klikli_dev/modonomicon/networking/SendAdvancementToClientMessage.java
```

- [ ] **Step 5: Run compile to catch remaining packet references**

Run:

```bash
./gradlew.bat :fabric:compileJava :neo:compileJava
```

Expected:

```text
BUILD SUCCESSFUL
```

If compilation fails, remove the remaining `RequestAdvancementMessage` or `SendAdvancementToClientMessage` references before moving on.

- [ ] **Step 6: Commit**

Run:

```bash
git add common/src/main/java/com/klikli_dev/modonomicon/networking/RequestAdvancementMessage.java common/src/main/java/com/klikli_dev/modonomicon/networking/SendAdvancementToClientMessage.java fabric/src/main/java/com/klikli_dev/modonomicon/network/Networking.java fabric/src/main/java/com/klikli_dev/modonomicon/network/ClientNetworking.java neo/src/main/java/com/klikli_dev/modonomicon/network/Networking.java
git commit -m "refactor: remove legacy advancement packets"
```

## Task 2: Remove The Client-Side Advancement Cache

**Files:**
- Modify: `common/src/main/java/com/klikli_dev/modonomicon/data/BookDataManager.java`

- [ ] **Step 1: Remove the advancement imports and cache field from `BookDataManager.Client`**

Delete these imports from `common/src/main/java/com/klikli_dev/modonomicon/data/BookDataManager.java`:

```java
import net.minecraft.advancements.AdvancementHolder;
```

Then delete this field from the nested `Client` class:

```java
private final Map<Identifier, AdvancementHolder> advancements = Object2ObjectMaps.synchronize(new Object2ObjectOpenHashMap<>());
```

After the edit, the client-only state in that section should start directly with:

```java
private final Object2FloatOpenHashMap<BookTextHolder.ScaleCacheKey> bookTextHolderScaleCache = new Object2FloatOpenHashMap<>();
private boolean isFallbackLocale;
private boolean isFontInitialized;
```

- [ ] **Step 2: Remove the dead cache accessors from `BookDataManager.Client`**

Delete these methods exactly:

```java
public AdvancementHolder getAdvancement(Identifier id) {
    return this.advancements.get(id);
}

public void addAdvancement(AdvancementHolder advancement) {
    this.advancements.put(advancement.id(), advancement);
}
```

- [ ] **Step 3: Remove the reload-time cache clear**

In the nested `Client.apply(...)` method, delete this line:

```java
this.advancements.clear();
```

The reset block should remain:

```java
this.resetUseFallbackFont();
this.bookTextHolderScaleCache.clear();
```

- [ ] **Step 4: Run compile to verify the cache is truly unused**

Run:

```bash
./gradlew.bat :common:compileJava :fabric:compileJava :neo:compileJava
```

Expected:

```text
BUILD SUCCESSFUL
```

If compilation fails, remove the remaining `getAdvancement(...)`, `addAdvancement(...)`, or `AdvancementHolder` references before moving on.

- [ ] **Step 5: Commit**

Run:

```bash
git add common/src/main/java/com/klikli_dev/modonomicon/data/BookDataManager.java
git commit -m "refactor: drop advancement client cache"
```

## Task 3: Remove The Advancement-Locking Config Service And Loader Config Entries

**Files:**
- Delete: `common/src/main/java/com/klikli_dev/modonomicon/platform/services/ServerConfigHelper.java`
- Modify: `common/src/main/java/com/klikli_dev/modonomicon/platform/Services.java`
- Modify: `fabric/src/main/java/com/klikli_dev/modonomicon/config/ServerConfig.java`
- Delete: `fabric/src/main/java/com/klikli_dev/modonomicon/config/FabricServerConfigHelper.java`
- Delete: `fabric/src/main/resources/META-INF/services/com.klikli_dev.modonomicon.platform.services.ServerConfigHelper`
- Modify: `neo/src/main/java/com/klikli_dev/modonomicon/config/ServerConfig.java`
- Delete: `neo/src/main/java/com/klikli_dev/modonomicon/config/NeoServerConfigHelper.java`
- Delete: `neo/src/main/resources/META-INF/services/com.klikli_dev.modonomicon.platform.services.ServerConfigHelper`

- [ ] **Step 1: Remove the common `ServerConfigHelper` service from `Services.java`**

Edit `common/src/main/java/com/klikli_dev/modonomicon/platform/Services.java` and remove this import:

```java
import com.klikli_dev.modonomicon.platform.services.ServerConfigHelper;
```

Then remove this field:

```java
public static final ServerConfigHelper SERVER_CONFIG = load(ServerConfigHelper.class);
```

The service list should now stop at:

```java
public static final PlatformHelper PLATFORM = load(PlatformHelper.class);
public static final NetworkHelper NETWORK = load(NetworkHelper.class);
public static final PatchouliHelper PATCHOULI = load(PatchouliHelper.class);
```

- [ ] **Step 2: Delete the common service interface and per-loader implementations**

Delete these files:

```text
common/src/main/java/com/klikli_dev/modonomicon/platform/services/ServerConfigHelper.java
fabric/src/main/java/com/klikli_dev/modonomicon/config/FabricServerConfigHelper.java
neo/src/main/java/com/klikli_dev/modonomicon/config/NeoServerConfigHelper.java
fabric/src/main/resources/META-INF/services/com.klikli_dev.modonomicon.platform.services.ServerConfigHelper
neo/src/main/resources/META-INF/services/com.klikli_dev.modonomicon.platform.services.ServerConfigHelper
```

- [ ] **Step 3: Remove the Fabric server config property**

Edit `fabric/src/main/java/com/klikli_dev/modonomicon/config/ServerConfig.java` and delete this field:

```java
public static PropertyMirror<Boolean> disableAdvancementLocking = PropertyMirror.create(ConfigTypes.BOOLEAN);
```

Then remove this config value block:

```java
.fork("unlock")
.withComment("Unlock Settings")
.beginValue("disableAdvancementLocking", ConfigTypes.BOOLEAN, false)
.withComment("If true, advancement-based unlock conditions will always return true, " +
        "effectively disabling advancement-gated progression in all books. " +
        "Other unlock conditions (e.g. entry read, mod loaded) are not affected.")
.finishValue(disableAdvancementLocking::mirror)
.finishBranch()
```

The resulting config tree should simply build without an `unlock` branch, for example:

```java
private static final ConfigTree CONFIG = ConfigTree.builder()
        .build();
```

- [ ] **Step 4: Remove the Neo server config property**

Edit `neo/src/main/java/com/klikli_dev/modonomicon/config/ServerConfig.java` and delete the entire `disableAdvancementLocking` member plus its builder block:

```java
public final ModConfigSpec.BooleanValue disableAdvancementLocking;
...
builder.comment("Unlock Settings").push("unlock");
this.disableAdvancementLocking = builder.comment(
                "If true, advancement-based unlock conditions will always return true, " +
                        "effectively disabling advancement-gated progression in all books. " +
                        "Other unlock conditions (e.g. entry read, mod loaded) are not affected.")
        .define("disableAdvancementLocking", false);
builder.pop();
```

Collapse the nested config shape so the file becomes:

```java
public final ModConfigSpec spec;

private ServerConfig() {
    var builder = new ModConfigSpec.Builder();
    this.spec = builder.build();
}
```

Delete the now-empty `UnlockCategory` inner class entirely.

- [ ] **Step 5: Run compile to confirm the config service is gone**

Run:

```bash
./gradlew.bat :common:compileJava :fabric:compileJava :neo:compileJava
```

Expected:

```text
BUILD SUCCESSFUL
```

If compilation fails, remove the remaining `ServerConfigHelper`, `SERVER_CONFIG`, `disableAdvancementLocking`, or `unlockCategory` references before moving on.

- [ ] **Step 6: Commit**

Run:

```bash
git add common/src/main/java/com/klikli_dev/modonomicon/platform/Services.java common/src/main/java/com/klikli_dev/modonomicon/platform/services/ServerConfigHelper.java fabric/src/main/java/com/klikli_dev/modonomicon/config/ServerConfig.java fabric/src/main/java/com/klikli_dev/modonomicon/config/FabricServerConfigHelper.java fabric/src/main/resources/META-INF/services/com.klikli_dev.modonomicon.platform.services.ServerConfigHelper neo/src/main/java/com/klikli_dev/modonomicon/config/ServerConfig.java neo/src/main/java/com/klikli_dev/modonomicon/config/NeoServerConfigHelper.java neo/src/main/resources/META-INF/services/com.klikli_dev.modonomicon.platform.services.ServerConfigHelper
git commit -m "refactor: remove advancement locking config"
```

## Task 4: Remove The Translation Source And Regenerate Loader Assets

**Files:**
- Modify: `common/src/main/java/com/klikli_dev/modonomicon/datagen/EnUsProvider.java`
- Modify: `fabric/src/generated/resources/assets/modonomicon/lang/en_us.json`
- Modify: `neo/src/generated/resources/assets/modonomicon/lang/en_us.json`

- [ ] **Step 1: Remove the obsolete config translation source**

Edit `common/src/main/java/com/klikli_dev/modonomicon/datagen/EnUsProvider.java` and delete these lines exactly:

```java
this.addConfig("unlock", "Unlock Settings");
this.addConfig("disableAdvancementLocking", "Disable Advancement Locking");
```

The remaining configuration translations should end with:

```java
this.addConfig("qol", "Quality of Life Settings");
this.addConfig("enableSmoothZoom", "Enable Smooth Zoom");
this.addConfig("storeLastOpenPageWhenClosingEntry", "Store Last Open Page When Closing Entry");
this.addConfig("fontFallbackLocales", "Font Fallback Locales");
```

- [ ] **Step 2: Regenerate Fabric generated assets**

Run:

```bash
./gradlew.bat fabric:runDatagen
```

Expected:

```text
BUILD SUCCESSFUL
```

This should update `fabric/src/generated/resources/assets/modonomicon/lang/en_us.json` so the `modonomicon.configuration.disableAdvancementLocking` entry is removed.

- [ ] **Step 3: Regenerate Neo generated assets**

Run:

```bash
./gradlew.bat neo:runClientData
```

Expected:

```text
BUILD SUCCESSFUL
```

This should update `neo/src/generated/resources/assets/modonomicon/lang/en_us.json` so the same obsolete translation key is removed there too.

- [ ] **Step 4: Verify the translation key is gone from source and generated output**

Run:

```bash
rg "disableAdvancementLocking" common/src/main/java/com/klikli_dev/modonomicon/datagen/EnUsProvider.java fabric/src/generated/resources/assets/modonomicon/lang/en_us.json neo/src/generated/resources/assets/modonomicon/lang/en_us.json
```

Expected:

```text
[no output]
```

- [ ] **Step 5: Commit**

Run:

```bash
git add common/src/main/java/com/klikli_dev/modonomicon/datagen/EnUsProvider.java fabric/src/generated/resources/assets/modonomicon/lang/en_us.json neo/src/generated/resources/assets/modonomicon/lang/en_us.json
git commit -m "chore: remove advancement locking translations"
```

## Task 5: Final Verification And Manual Slice Checks

**Files:**
- Reuse: `common/src/main/java/com/klikli_dev/modonomicon/book/conditions/BookCondition.java`
- Reuse: `common/src/main/java/com/klikli_dev/modonomicon/research/hook/AdvancementResearchHookService.java`
- Reuse: `common/src/main/java/com/klikli_dev/modonomicon/datagen/book/demo/features/ConditionAdvancementEntry.java`
- Reuse: `fabric/src/generated/resources/data/modonomicon/modonomicon/research/demo/advancement_hooks.json`
- Reuse: `neo/src/generated/resources/data/modonomicon/modonomicon/research/demo/advancement_hooks.json`

- [ ] **Step 1: Run the full compile + datagen verification set**

Run:

```bash
./gradlew.bat :common:compileJava :fabric:compileJava :neo:compileJava fabric:runDatagen neo:runClientData
```

Expected:

```text
BUILD SUCCESSFUL
```

- [ ] **Step 2: Verify removed legacy symbols are gone while preserved guardrails remain**

Run:

```bash
rg "RequestAdvancementMessage|SendAdvancementToClientMessage|ServerConfigHelper|disableAdvancementLocking" common/src fabric/src neo/src
```

Expected:

```text
common/src/main/java/com/klikli_dev/modonomicon/book/conditions/BookCondition.java:... modonomicon:advancement ... no longer supported
```

There should be no hits for the deleted packet or config symbols. The only advancement-related legacy hit that should remain is the explicit rejection message in `BookCondition.java`.

- [ ] **Step 3: Manually verify research-backed advancement progression still works**

Run one loader locally; prefer Neo because its runtime advancement flow is explicitly research-first today:

```bash
./gradlew.bat neo:runClient
```

In-game verification:

```text
1. Open the demo book entry `condition_advancement`.
2. Confirm the entry/page conditions are still authored against `research_node_unlocked`.
3. Earn `minecraft:story/mine_stone` and confirm the research-backed conditional page unlocks.
4. Earn `minecraft:husbandry/ride_a_boat_with_a_goat` and confirm the entry unlocks.
5. Confirm there is no crash or missing-packet error when opening the book before or after those advancements.
```

- [ ] **Step 4: Commit the verification-safe final state**

Run:

```bash
git add common/src/main/java/com/klikli_dev/modonomicon/data/BookDataManager.java common/src/main/java/com/klikli_dev/modonomicon/platform/Services.java common/src/main/java/com/klikli_dev/modonomicon/datagen/EnUsProvider.java fabric/src/main/java/com/klikli_dev/modonomicon/network/Networking.java fabric/src/main/java/com/klikli_dev/modonomicon/network/ClientNetworking.java fabric/src/main/java/com/klikli_dev/modonomicon/config/ServerConfig.java fabric/src/generated/resources/assets/modonomicon/lang/en_us.json neo/src/main/java/com/klikli_dev/modonomicon/network/Networking.java neo/src/main/java/com/klikli_dev/modonomicon/config/ServerConfig.java neo/src/generated/resources/assets/modonomicon/lang/en_us.json
git commit -m "refactor: clean up legacy advancement paths"
```

## Self-Review Against The Spec

- Cleanup-only scope is covered by Tasks 1-4.
- Legacy advancement networking/client-cache removal is covered by Tasks 1-2.
- Advancement-locking config removal is covered by Task 3.
- Preserving current research-backed advancement ingress is protected by Task 5 manual verification and the explicit reuse list.
- Clear rejection of removed legacy condition ids is protected by Task 5 grep verification against `BookCondition.java`.
- Forge-specific cleanup is intentionally excluded because `settings.gradle` excludes the Forge module and product direction explicitly deferred Forge generated cleanup.
