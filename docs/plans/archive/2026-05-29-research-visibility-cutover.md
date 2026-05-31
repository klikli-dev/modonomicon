# Research Visibility Cutover Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Remove book unlock state as progression authority by cutting category, entry, and page visibility over to computed research-backed access while keeping only visual and interaction state persisted on the book side.

**Architecture:** First create a computed visibility/access layer that answers category, entry, and page access from research plus authored book conditions. Then make the client able to evaluate that layer from synced research and visual state, cut UI/runtime callers over, and finally remove unlock snapshot persistence, sync, and lifecycle recompute. Keep visual state, bookmarks, read markers, command-use tracking, and recent presentation metadata as downstream book state only.

**Tech Stack:** Java 25, Gradle 9, Modonomicon common + Fabric + Neo modules, Mojang codecs and SavedData, custom networking, manual UI verification in client runtime.

---

## File Map

### Create
- `common/src/main/java/com/klikli_dev/modonomicon/networking/RequestSyncResearchStateMessage.java` - client request for authoritative research sync when local client state is missing
- `common/src/main/java/com/klikli_dev/modonomicon/networking/SyncResearchStateMessage.java` - clientbound research-state sync payload replacing unlock snapshot authority
- `common/src/main/java/com/klikli_dev/modonomicon/bookstate/visual/BookVisibilitySnapshots.java` - optional helper for computing newly visible categories/entries/pages during unread/recent migration without persisting unlock flags

### Modify
- `common/src/main/java/com/klikli_dev/modonomicon/bookstate/BookServices.java` - wire reworked access/visibility/interaction services
- `common/src/main/java/com/klikli_dev/modonomicon/bookstate/access/BookStateAccess.java` - remove progression-authority unlock API from the mixed interface and keep only visual/interaction accessors plus any remaining downstream helpers
- `common/src/main/java/com/klikli_dev/modonomicon/bookstate/access/DefaultBookStateAccess.java` - stop delegating progression checks to `BookUnlockStateManager`
- `common/src/main/java/com/klikli_dev/modonomicon/bookstate/visibility/BookVisibilityService.java` - expand to category, entry, and page access/visibility methods
- `common/src/main/java/com/klikli_dev/modonomicon/bookstate/visibility/DefaultBookVisibilityService.java` - compute access from research state and authored conditions instead of stored unlock flags
- `common/src/main/java/com/klikli_dev/modonomicon/research/state/ResearchStateManager.java` - add client sync path and stop depending on `RequestSyncBookStatesMessage`
- `common/src/main/java/com/klikli_dev/modonomicon/research/state/ResearchStatesSaveData.java` - expose encode/decode helpers if needed for networking
- `common/src/main/java/com/klikli_dev/modonomicon/research/state/PlayerResearchState.java` - expose network serialization helpers if needed for client sync
- `common/src/main/java/com/klikli_dev/modonomicon/bookstate/BookVisualStateManager.java` - request visual-state sync only and keep downstream unread/bookmark/navigation persistence
- `common/src/main/java/com/klikli_dev/modonomicon/bookstate/BookStatesSaveData.java` - remove the `unlockStates` half and become visual-state-only save data
- `common/src/main/java/com/klikli_dev/modonomicon/bookstate/BookUnlockStateManager.java` - strip progression-authority responsibilities or delete entirely once callers are gone
- `common/src/main/java/com/klikli_dev/modonomicon/bookstate/BookUnlockStates.java` - remove or reduce to any allowed downstream data that survives the cutover
- `common/src/main/java/com/klikli_dev/modonomicon/bookstate/interaction/DefaultBookInteractionService.java` - keep read/unread behavior while moving newly-visible logic off unlock snapshot diffs
- `common/src/main/java/com/klikli_dev/modonomicon/book/entries/BookContentEntry.java` - cut unlocked-page lookup over to computed page visibility
- `common/src/main/java/com/klikli_dev/modonomicon/book/BookCommand.java` - ensure command max-use tracking still uses downstream state only
- `common/src/main/java/com/klikli_dev/modonomicon/networking/RequestSyncBookStatesMessage.java` - narrow to visual-state sync only or replace with a visual-only request
- `common/src/main/java/com/klikli_dev/modonomicon/networking/SyncBookVisualStatesMessage.java` - keep as visual-state sync; may need minor updates if save-data type changes
- `common/src/main/java/com/klikli_dev/modonomicon/networking/SyncBookUnlockStatesMessage.java` - delete or retire
- `common/src/main/java/com/klikli_dev/modonomicon/networking/BookEntryReadMessage.java` - stop triggering unlock recompute/sync after research hook application
- `common/src/main/java/com/klikli_dev/modonomicon/networking/BookCategoryReadMessage.java` - stop relying on unlock snapshot sync
- `common/src/main/java/com/klikli_dev/modonomicon/networking/ClickReadAllButtonMessage.java` - stop rebuilding unlock state; only update visual/read state plus research hooks if needed
- `common/src/main/java/com/klikli_dev/modonomicon/networking/ReloadResourcesDoneMessage.java` - remove unlock recompute followup
- `common/src/main/java/com/klikli_dev/modonomicon/command/ResetBookUnlocksCommand.java` - delete or replace with a visual/interaction-only reset command if still warranted
- `common/src/main/java/com/klikli_dev/modonomicon/command/SaveUnlocksCommand.java` - delete or redesign away from progression snapshot export
- `common/src/main/java/com/klikli_dev/modonomicon/command/LoadUnlocksCommand.java` - delete or redesign away from progression snapshot import
- `common/src/main/java/com/klikli_dev/modonomicon/registry/CommandRegistry.java` - remove or rewire the legacy unlock commands
- `common/src/main/java/com/klikli_dev/modonomicon/client/gui/book/entry/linkhandler/BookLinkHandler.java` - use computed visibility/page access instead of `BookUnlockStateManager`
- `common/src/main/java/com/klikli_dev/modonomicon/client/gui/book/entry/ContentRenderingScreen.java` - use computed page/entry access checks for locked hover/warnings
- `common/src/main/java/com/klikli_dev/modonomicon/client/gui/book/search/BookSearchScreen.java` - filter via computed category/entry visibility
- `common/src/main/java/com/klikli_dev/modonomicon/client/gui/book/index/BookCategoryIndexScreen.java` - filter via computed category/entry visibility
- `common/src/main/java/com/klikli_dev/modonomicon/client/gui/book/button/EntryListButton.java` - render lock state from computed entry display/access
- `common/src/main/java/com/klikli_dev/modonomicon/client/gui/book/button/CategoryListButton.java` - render lock state from computed category visibility/access
- `common/src/main/java/com/klikli_dev/modonomicon/client/gui/book/button/CategoryButton.java` - cut category visibility checks over to computed service
- `common/src/main/java/com/klikli_dev/modonomicon/client/gui/book/bookmarks/BookBookmarksScreen.java` - use computed entry visibility instead of unlock snapshot checks
- `common/src/main/java/com/klikli_dev/modonomicon/client/gui/book/node/BookParentNodeScreen.java` - cut indirect unlock checks over to the visibility/access service
- `common/src/main/java/com/klikli_dev/modonomicon/client/gui/book/index/BookParentIndexScreen.java` - cut indirect unlock checks over to the visibility/access service
- `common/src/main/java/com/klikli_dev/modonomicon/client/gui/book/recentlyunlocked/BookRecentlyUnlockedScreen.java` - preserve recent-entry UX using downstream metadata rather than legacy unlock authority
- `fabric/src/main/java/com/klikli_dev/modonomicon/network/Networking.java` - register research sync packets, remove unlock snapshot packets when retired
- `fabric/src/main/java/com/klikli_dev/modonomicon/network/ClientNetworking.java` - register research sync receiver, remove unlock snapshot receiver when retired
- `neo/src/main/java/com/klikli_dev/modonomicon/network/Networking.java` - register research sync packets, remove unlock snapshot packets when retired
- `fabric/src/main/java/com/klikli_dev/modonomicon/ModonomiconFabric.java` - remove join/tick/unload hooks that exist only for unlock recompute or unlock save-data caching
- `fabric/src/main/java/com/klikli_dev/modonomicon/mixin/MixinPlayerAdvancements.java` - stop calling unlock recompute after research advancement hook processing
- `neo/src/main/java/com/klikli_dev/modonomicon/ModonomiconNeo.java` - remove join/tick/unload hooks that exist only for unlock recompute or unlock save-data caching

### Reuse Without Modification
- `common/src/main/java/com/klikli_dev/modonomicon/book/conditions/BookResearchNodeUnlockedCondition.java` - remains the primary research-backed visibility condition
- `common/src/main/java/com/klikli_dev/modonomicon/book/conditions/BookCategoryHasVisibleEntriesCondition.java` - remains a downstream book-local condition that should consume the visibility service
- `common/src/main/java/com/klikli_dev/modonomicon/bookstate/BookVisualStates.java` - visual/bookmark/navigation persistence survives conceptually
- `common/src/main/java/com/klikli_dev/modonomicon/bookstate/visual/BookVisualState.java` - stays visual only
- `common/src/main/java/com/klikli_dev/modonomicon/bookstate/visual/CategoryVisualState.java` - stays visual only
- `common/src/main/java/com/klikli_dev/modonomicon/bookstate/visual/EntryVisualState.java` - stays visual only
- `common/src/main/java/com/klikli_dev/modonomicon/research/hook/ResearchHookService.java` - stays authoritative for `entry_viewed_once`
- `common/src/main/java/com/klikli_dev/modonomicon/research/hook/AdvancementResearchHookService.java` - stays authoritative for advancement ingress

### Out Of Scope For This Plan
- Adding `item_crafted` or any other new trigger family
- Adding a sanctioned research bypass/skip mechanism
- Authoring sugar, typed refs, or bridge/compiler work

## Task 1: Add Client Research Sync And Decouple Research From Book-State Sync

**Files:**
- Create: `common/src/main/java/com/klikli_dev/modonomicon/networking/RequestSyncResearchStateMessage.java`
- Create: `common/src/main/java/com/klikli_dev/modonomicon/networking/SyncResearchStateMessage.java`
- Modify: `common/src/main/java/com/klikli_dev/modonomicon/research/state/ResearchStateManager.java`
- Modify: `common/src/main/java/com/klikli_dev/modonomicon/research/state/ResearchStatesSaveData.java`
- Modify: `common/src/main/java/com/klikli_dev/modonomicon/research/state/PlayerResearchState.java`
- Modify: `fabric/src/main/java/com/klikli_dev/modonomicon/network/Networking.java`
- Modify: `fabric/src/main/java/com/klikli_dev/modonomicon/network/ClientNetworking.java`
- Modify: `neo/src/main/java/com/klikli_dev/modonomicon/network/Networking.java`

- [ ] **Step 1: Add the research sync request message**

Create `common/src/main/java/com/klikli_dev/modonomicon/networking/RequestSyncResearchStateMessage.java` with the same message shape pattern used by `RequestSyncBookStatesMessage`, but scoped to research:

```java
public class RequestSyncResearchStateMessage implements Message {

    public static final RequestSyncResearchStateMessage INSTANCE = new RequestSyncResearchStateMessage();
    public static final Type<RequestSyncResearchStateMessage> TYPE = new Type<>(Identifier.fromNamespaceAndPath(Modonomicon.MOD_ID, "request_sync_research_state"));
    public static final StreamCodec<RegistryFriendlyByteBuf, RequestSyncResearchStateMessage> STREAM_CODEC = StreamCodec.unit(INSTANCE);

    private RequestSyncResearchStateMessage() {
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    @Override
    public void onServerReceived(MinecraftServer minecraftServer, ServerPlayer player) {
        ResearchStateManager.get().syncFor(player);
    }
}
```

- [ ] **Step 2: Add the clientbound research sync payload**

Create `common/src/main/java/com/klikli_dev/modonomicon/networking/SyncResearchStateMessage.java` so it mirrors the structure of `SyncBookUnlockStatesMessage`, but writes research state into `ResearchStateManager` instead of `BookUnlockStateManager`:

```java
public class SyncResearchStateMessage implements Message {

    public static final Type<SyncResearchStateMessage> TYPE = new Type<>(Identifier.fromNamespaceAndPath(Modonomicon.MOD_ID, "sync_research_state"));
    public static final StreamCodec<RegistryFriendlyByteBuf, SyncResearchStateMessage> STREAM_CODEC = StreamCodec.composite(
            PlayerResearchState.STREAM_CODEC,
            message -> message.state,
            SyncResearchStateMessage::new
    );

    public final PlayerResearchState state;

    public SyncResearchStateMessage(PlayerResearchState state) {
        this.state = state;
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    @Override
    public void onClientReceived(Minecraft minecraft, Player player) {
        if (minecraft.getSingleplayerServer() == null) {
            ResearchStateManager.get().installClientState(player, this.state);
        }
    }
}
```

- [ ] **Step 3: Teach `ResearchStateManager` how to sync and install client state**

Modify `common/src/main/java/com/klikli_dev/modonomicon/research/state/ResearchStateManager.java` so it stops sending `RequestSyncBookStatesMessage.INSTANCE` from `getSaveDataIfNecessary(...)` and instead sends `RequestSyncResearchStateMessage.INSTANCE`.

Add these methods:

```java
public void syncFor(ServerPlayer player) {
    Services.NETWORK.sendTo(player, new SyncResearchStateMessage(this.getStateFor(player)));
}

public void installClientState(Player player, PlayerResearchState state) {
    this.saveData = new ResearchStatesSaveData(Map.of(player.getUUID(), state));
}
```

The client branch of `getSaveDataIfNecessary(...)` should change from:

```java
this.saveData = new ResearchStatesSaveData();
Services.NETWORK.sendToServer(RequestSyncBookStatesMessage.INSTANCE);
```

to:

```java
this.saveData = new ResearchStatesSaveData();
Services.NETWORK.sendToServer(RequestSyncResearchStateMessage.INSTANCE);
```

- [ ] **Step 4: Expose codecs needed for the research sync payload**

If `PlayerResearchState` does not already have a stream codec, add one in `common/src/main/java/com/klikli_dev/modonomicon/research/state/PlayerResearchState.java` using the same pattern already used elsewhere in the codebase:

```java
public static final StreamCodec<RegistryFriendlyByteBuf, PlayerResearchState> STREAM_CODEC = ByteBufCodecs.fromCodecWithRegistries(CODEC);
```

If `ResearchStatesSaveData` lacks a constructor for a preloaded state map, add one matching the new `installClientState(...)` call.

- [ ] **Step 5: Register the new research packets on Fabric and Neo**

In `fabric/src/main/java/com/klikli_dev/modonomicon/network/Networking.java`, add:

```java
ServerPlayNetworking.registerGlobalReceiver(RequestSyncResearchStateMessage.TYPE, new ServerMessageHandler<>());
PayloadTypeRegistry.serverboundPlay().register(RequestSyncResearchStateMessage.TYPE, RequestSyncResearchStateMessage.STREAM_CODEC);
PayloadTypeRegistry.clientboundPlay().register(SyncResearchStateMessage.TYPE, SyncResearchStateMessage.STREAM_CODEC);
```

In `fabric/src/main/java/com/klikli_dev/modonomicon/network/ClientNetworking.java`, add:

```java
ClientPlayNetworking.registerGlobalReceiver(SyncResearchStateMessage.TYPE, new ClientMessageHandler<>());
```

In `neo/src/main/java/com/klikli_dev/modonomicon/network/Networking.java`, add:

```java
registrar.playToServer(RequestSyncResearchStateMessage.TYPE, RequestSyncResearchStateMessage.STREAM_CODEC, MessageHandler::handle);
registrar.playToClient(SyncResearchStateMessage.TYPE, SyncResearchStateMessage.STREAM_CODEC, MessageHandler::handle);
```

- [ ] **Step 6: Run compile to verify research sync stands alone**

Run:

```bash
./gradlew.bat :common:compileJava :fabric:compileJava :neo:compileJava
```

Expected:

```text
BUILD SUCCESSFUL
```

- [ ] **Step 7: Commit**

Run:

```bash
git add common/src/main/java/com/klikli_dev/modonomicon/networking/RequestSyncResearchStateMessage.java common/src/main/java/com/klikli_dev/modonomicon/networking/SyncResearchStateMessage.java common/src/main/java/com/klikli_dev/modonomicon/research/state/ResearchStateManager.java common/src/main/java/com/klikli_dev/modonomicon/research/state/ResearchStatesSaveData.java common/src/main/java/com/klikli_dev/modonomicon/research/state/PlayerResearchState.java fabric/src/main/java/com/klikli_dev/modonomicon/network/Networking.java fabric/src/main/java/com/klikli_dev/modonomicon/network/ClientNetworking.java neo/src/main/java/com/klikli_dev/modonomicon/network/Networking.java
git commit -m "feat: sync research state to clients"
```

## Task 2: Expand Computed Visibility To Categories, Entries, And Pages

**Files:**
- Modify: `common/src/main/java/com/klikli_dev/modonomicon/bookstate/visibility/BookVisibilityService.java`
- Modify: `common/src/main/java/com/klikli_dev/modonomicon/bookstate/visibility/DefaultBookVisibilityService.java`
- Modify: `common/src/main/java/com/klikli_dev/modonomicon/bookstate/BookServices.java`
- Modify: `common/src/main/java/com/klikli_dev/modonomicon/book/entries/BookContentEntry.java`
- Modify: `common/src/main/java/com/klikli_dev/modonomicon/bookstate/access/BookStateAccess.java`
- Modify: `common/src/main/java/com/klikli_dev/modonomicon/bookstate/access/DefaultBookStateAccess.java`

- [ ] **Step 1: Expand the visibility service interface**

Edit `common/src/main/java/com/klikli_dev/modonomicon/bookstate/visibility/BookVisibilityService.java` so it becomes the authority for category, entry, and page access:

```java
public interface BookVisibilityService {

    boolean isVisible(Player player, BookCategory category);

    boolean isVisible(Player player, BookEntry entry);

    boolean isVisible(Player player, BookPage page);

    boolean isAccessible(Player player, BookEntry entry);

    boolean isAccessible(Player player, BookPage page);

    List<BookPage> getVisiblePages(Player player, BookEntry entry);

    EntryDisplayState getEntryDisplayState(Player player, BookEntry entry);
}
```

Add the missing imports for `BookCategory`, `BookPage`, and `List`.

- [ ] **Step 2: Implement computed category/entry/page evaluation**

Rewrite `common/src/main/java/com/klikli_dev/modonomicon/bookstate/visibility/DefaultBookVisibilityService.java` so it no longer imports or uses `BookUnlockStateManager`.

Base the implementation on authored conditions and research-backed condition evaluation, for example:

```java
@Override
public boolean isVisible(Player player, BookCategory category) {
    return category.getCondition().test(BookConditionContext.of(category.getBook(), category), player);
}

@Override
public boolean isAccessible(Player player, BookEntry entry) {
    return entry.getCondition().test(BookConditionContext.of(entry.getBook(), entry), player);
}

@Override
public boolean isVisible(Player player, BookPage page) {
    return page.getCondition().test(BookConditionContext.of(page.getBook(), page), player);
}

@Override
public List<BookPage> getVisiblePages(Player player, BookEntry entry) {
    return entry.getPages().stream().filter(page -> this.isVisible(player, page)).toList();
}
```

Keep `getEntryDisplayState(...)` but compute hidden-vs-locked using parent entry visibility/access via the same service rather than persisted unlock flags.

- [ ] **Step 3: Cut `BookContentEntry` over to computed page visibility**

Edit `common/src/main/java/com/klikli_dev/modonomicon/book/entries/BookContentEntry.java` and change:

```java
BookUnlockStateManager unlockManager = BookUnlockStateManager.get();
return unlockManager.getUnlockedPagesFor(player, this);
```

to:

```java
return BookServices.visibility().getVisiblePages(player, this);
```

Add the `BookServices` import and remove the `BookUnlockStateManager` import.

- [ ] **Step 4: Remove progression-authority unlock methods from `BookStateAccess`**

Edit `common/src/main/java/com/klikli_dev/modonomicon/bookstate/access/BookStateAccess.java` and delete these methods exactly:

```java
boolean isUnlocked(Player player, BookCategory category);
boolean isUnlocked(Player player, BookEntry entry);
boolean isUnlocked(Player player, BookPage page);
List<BookPage> getUnlockedPages(Player player, BookEntry entry);
void updateAndSync(ServerPlayer player);
void sync(ServerPlayer player);
```

The interface should keep only downstream visual/interaction concerns such as read state, command runs, timestamps, and visual-state accessors.

- [ ] **Step 5: Remove the old unlock delegation from `DefaultBookStateAccess`**

Edit `common/src/main/java/com/klikli_dev/modonomicon/bookstate/access/DefaultBookStateAccess.java` and delete the removed methods plus the matching `BookUnlockStateManager` calls.

Keep only the remaining downstream methods, for example:

```java
@Override
public boolean isEntryRead(Player player, BookEntry entry) {
    return BookUnlockStateManager.get().isReadFor(player, entry);
}
```

Do not try to preserve `updateAndSync(...)` or `sync(...)` on this interface.

- [ ] **Step 6: Run compile to verify the new service boundary**

Run:

```bash
./gradlew.bat :common:compileJava
```

Expected:

```text
BUILD SUCCESSFUL
```

If compile fails, fix all callers to use `BookServices.visibility()` rather than the old unlock-state accessors before moving on.

- [ ] **Step 7: Commit**

Run:

```bash
git add common/src/main/java/com/klikli_dev/modonomicon/bookstate/visibility/BookVisibilityService.java common/src/main/java/com/klikli_dev/modonomicon/bookstate/visibility/DefaultBookVisibilityService.java common/src/main/java/com/klikli_dev/modonomicon/bookstate/BookServices.java common/src/main/java/com/klikli_dev/modonomicon/book/entries/BookContentEntry.java common/src/main/java/com/klikli_dev/modonomicon/bookstate/access/BookStateAccess.java common/src/main/java/com/klikli_dev/modonomicon/bookstate/access/DefaultBookStateAccess.java
git commit -m "feat: compute book visibility from research"
```

## Task 3: Cut UI And Runtime Callers Over To The Visibility Service

**Files:**
- Modify: `common/src/main/java/com/klikli_dev/modonomicon/client/gui/book/entry/linkhandler/BookLinkHandler.java`
- Modify: `common/src/main/java/com/klikli_dev/modonomicon/client/gui/book/entry/ContentRenderingScreen.java`
- Modify: `common/src/main/java/com/klikli_dev/modonomicon/client/gui/book/search/BookSearchScreen.java`
- Modify: `common/src/main/java/com/klikli_dev/modonomicon/client/gui/book/index/BookCategoryIndexScreen.java`
- Modify: `common/src/main/java/com/klikli_dev/modonomicon/client/gui/book/button/EntryListButton.java`
- Modify: `common/src/main/java/com/klikli_dev/modonomicon/client/gui/book/button/CategoryListButton.java`
- Modify: `common/src/main/java/com/klikli_dev/modonomicon/client/gui/book/button/CategoryButton.java`
- Modify: `common/src/main/java/com/klikli_dev/modonomicon/client/gui/book/bookmarks/BookBookmarksScreen.java`
- Modify: `common/src/main/java/com/klikli_dev/modonomicon/client/gui/book/node/BookParentNodeScreen.java`
- Modify: `common/src/main/java/com/klikli_dev/modonomicon/client/gui/book/index/BookParentIndexScreen.java`
- Modify: `common/src/main/java/com/klikli_dev/modonomicon/client/gui/book/recentlyunlocked/BookRecentlyUnlockedScreen.java`

- [ ] **Step 1: Replace direct `BookUnlockStateManager` usage in `BookLinkHandler`**

Edit `common/src/main/java/com/klikli_dev/modonomicon/client/gui/book/entry/linkhandler/BookLinkHandler.java` and replace:

```java
if (!BookUnlockStateManager.get().isUnlockedFor(this.player(), entry)) {
    return ClickResult.FAILURE;
}
...
if (page != null && !BookUnlockStateManager.get().isUnlockedFor(this.player(), entry.getPages().get(page))) {
    return ClickResult.UNHANDLED;
}
```

with:

```java
if (!BookServices.visibility().isAccessible(this.player(), entry)) {
    return ClickResult.FAILURE;
}
...
if (page != null && !BookServices.visibility().isAccessible(this.player(), entry.getPages().get(page))) {
    return ClickResult.UNHANDLED;
}
```

Remove the `BookUnlockStateManager` import and add `BookServices`.

- [ ] **Step 2: Replace direct unlock-state checks in list/search/index screens**

In each of these files, replace `BookUnlockStateManager.get().isUnlockedFor(...)` or `BookServices.stateAccess().isUnlocked(...)` with the matching visibility call:

```java
BookServices.visibility().isVisible(player, category)
BookServices.visibility().isVisible(player, entry)
BookServices.visibility().isAccessible(player, entry)
```

Files:

```text
common/src/main/java/com/klikli_dev/modonomicon/client/gui/book/search/BookSearchScreen.java
common/src/main/java/com/klikli_dev/modonomicon/client/gui/book/index/BookCategoryIndexScreen.java
common/src/main/java/com/klikli_dev/modonomicon/client/gui/book/bookmarks/BookBookmarksScreen.java
common/src/main/java/com/klikli_dev/modonomicon/client/gui/book/node/BookParentNodeScreen.java
common/src/main/java/com/klikli_dev/modonomicon/client/gui/book/index/BookParentIndexScreen.java
common/src/main/java/com/klikli_dev/modonomicon/client/gui/book/recentlyunlocked/BookRecentlyUnlockedScreen.java
```

- [ ] **Step 3: Replace button locked-state rendering with computed display state**

In `EntryListButton.java`, `CategoryListButton.java`, and `CategoryButton.java`, replace any direct unlock checks with:

```java
BookServices.visibility().getEntryDisplayState(player, entry)
BookServices.visibility().isVisible(player, category)
```

Use the existing `EntryDisplayState` enum where available rather than inventing a new button-only state type.

- [ ] **Step 4: Update `ContentRenderingScreen` to use computed page/entry access**

Replace any hover, tooltip, or warning logic that consults legacy unlock state with `BookServices.visibility().isAccessible(...)` and `BookServices.visibility().isVisible(...)` so locked/hidden messaging continues to work from computed visibility.

- [ ] **Step 5: Run compile to verify no UI caller still depends on unlock authority**

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
git add common/src/main/java/com/klikli_dev/modonomicon/client/gui/book/entry/linkhandler/BookLinkHandler.java common/src/main/java/com/klikli_dev/modonomicon/client/gui/book/entry/ContentRenderingScreen.java common/src/main/java/com/klikli_dev/modonomicon/client/gui/book/search/BookSearchScreen.java common/src/main/java/com/klikli_dev/modonomicon/client/gui/book/index/BookCategoryIndexScreen.java common/src/main/java/com/klikli_dev/modonomicon/client/gui/book/button/EntryListButton.java common/src/main/java/com/klikli_dev/modonomicon/client/gui/book/button/CategoryListButton.java common/src/main/java/com/klikli_dev/modonomicon/client/gui/book/button/CategoryButton.java common/src/main/java/com/klikli_dev/modonomicon/client/gui/book/bookmarks/BookBookmarksScreen.java common/src/main/java/com/klikli_dev/modonomicon/client/gui/book/node/BookParentNodeScreen.java common/src/main/java/com/klikli_dev/modonomicon/client/gui/book/index/BookParentIndexScreen.java common/src/main/java/com/klikli_dev/modonomicon/client/gui/book/recentlyunlocked/BookRecentlyUnlockedScreen.java
git commit -m "refactor: cut ui access over to computed visibility"
```

## Task 4: Move Newly-Visible Tracking To Downstream Visual State

**Files:**
- Create: `common/src/main/java/com/klikli_dev/modonomicon/bookstate/visual/BookVisibilitySnapshots.java`
- Modify: `common/src/main/java/com/klikli_dev/modonomicon/bookstate/interaction/DefaultBookInteractionService.java`
- Modify: `common/src/main/java/com/klikli_dev/modonomicon/bookstate/BookVisualStateManager.java`
- Modify: `common/src/main/java/com/klikli_dev/modonomicon/client/gui/book/recentlyunlocked/BookRecentlyUnlockedScreen.java`
- Modify: `common/src/main/java/com/klikli_dev/modonomicon/book/BookCommand.java`

- [ ] **Step 1: Introduce a helper for comparing current computed visibility**

Create `common/src/main/java/com/klikli_dev/modonomicon/bookstate/visual/BookVisibilitySnapshots.java` as a small utility that can collect visible categories and entries for a player/book using `BookServices.visibility()`, for example:

```java
public record BookVisibilitySnapshots(Set<Identifier> categories, Set<Identifier> entries, Map<Identifier, Set<Integer>> pages) {

    public static BookVisibilitySnapshots collect(Player player, Book book) {
        var categories = new HashSet<Identifier>();
        var entries = new HashSet<Identifier>();
        var pages = new HashMap<Identifier, Set<Integer>>();

        for (var category : book.getCategories().values()) {
            if (!BookServices.visibility().isVisible(player, category)) {
                continue;
            }
            categories.add(category.getId());
            for (var entry : category.getEntries().values()) {
                if (!BookServices.visibility().isVisible(player, entry)) {
                    continue;
                }
                entries.add(entry.getId());
                pages.put(entry.getId(), BookServices.visibility().getVisiblePages(player, entry).stream().map(BookPage::getPageNumber).collect(Collectors.toSet()));
            }
        }

        return new BookVisibilitySnapshots(categories, entries, pages);
    }
}
```

- [ ] **Step 2: Move unread-on-newly-visible logic out of unlock-state diffs**

Add a method on `BookVisualStateManager` such as:

```java
public void updateVisibilityDrivenUnread(ServerPlayer player, Book book, BookVisibilitySnapshots before, BookVisibilitySnapshots after) {
    after.entries().stream().filter(entryId -> !before.entries().contains(entryId)).forEach(entryId -> this.setEntryUnreadFor(player, book.getEntry(entryId), true));
    after.categories().stream().filter(categoryId -> !before.categories().contains(categoryId)).forEach(categoryId -> this.setCategoryUnreadFor(player, book.getCategory(categoryId), true));
}
```

Keep it downstream: it reacts to visibility changes, it does not compute progression.

- [ ] **Step 3: Preserve “recently unlocked” using downstream visibility timestamps**

Update `BookRecentlyUnlockedScreen.java` so it stops assuming timestamps come from legacy unlock-state authority.

If the existing timestamp map remains acceptable as downstream presentation metadata, access it through `BookStateAccess.getUnlockTimestamps(...)` only after that data has been moved out of legacy unlock authority. If that move is not yet done in this task, add a comment-free direct call site change that points at the new downstream storage method you define.

The resulting open-check should use:

```java
if (!BookServices.visibility().isAccessible(Minecraft.getInstance().player, entry.getEntry())) {
    return;
}
```

- [ ] **Step 4: Ensure first-read command handling no longer implies unlock authority**

Update `BookCommand.java` and any command-use checks so they continue to use downstream command-use tracking only. Do not reintroduce unlock snapshot checks while preserving max-use behavior.

- [ ] **Step 5: Run compile to verify unread/recent handling is downstream-only**

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
git add common/src/main/java/com/klikli_dev/modonomicon/bookstate/visual/BookVisibilitySnapshots.java common/src/main/java/com/klikli_dev/modonomicon/bookstate/interaction/DefaultBookInteractionService.java common/src/main/java/com/klikli_dev/modonomicon/bookstate/BookVisualStateManager.java common/src/main/java/com/klikli_dev/modonomicon/client/gui/book/recentlyunlocked/BookRecentlyUnlockedScreen.java common/src/main/java/com/klikli_dev/modonomicon/book/BookCommand.java
git commit -m "refactor: move newly visible tracking to visual state"
```

## Task 5: Remove Unlock Snapshot Persistence, Sync, And Commands

**Files:**
- Modify: `common/src/main/java/com/klikli_dev/modonomicon/bookstate/BookStatesSaveData.java`
- Modify or Delete: `common/src/main/java/com/klikli_dev/modonomicon/bookstate/BookUnlockStates.java`
- Modify or Delete: `common/src/main/java/com/klikli_dev/modonomicon/bookstate/BookUnlockStateManager.java`
- Modify: `common/src/main/java/com/klikli_dev/modonomicon/networking/RequestSyncBookStatesMessage.java`
- Delete: `common/src/main/java/com/klikli_dev/modonomicon/networking/SyncBookUnlockStatesMessage.java`
- Modify: `common/src/main/java/com/klikli_dev/modonomicon/networking/BookEntryReadMessage.java`
- Modify: `common/src/main/java/com/klikli_dev/modonomicon/networking/BookCategoryReadMessage.java`
- Modify: `common/src/main/java/com/klikli_dev/modonomicon/networking/ClickReadAllButtonMessage.java`
- Modify: `common/src/main/java/com/klikli_dev/modonomicon/networking/ReloadResourcesDoneMessage.java`
- Modify: `common/src/main/java/com/klikli_dev/modonomicon/command/ResetBookUnlocksCommand.java`
- Modify: `common/src/main/java/com/klikli_dev/modonomicon/command/SaveUnlocksCommand.java`
- Modify: `common/src/main/java/com/klikli_dev/modonomicon/command/LoadUnlocksCommand.java`
- Modify: `common/src/main/java/com/klikli_dev/modonomicon/registry/CommandRegistry.java`
- Modify: `fabric/src/main/java/com/klikli_dev/modonomicon/network/Networking.java`
- Modify: `fabric/src/main/java/com/klikli_dev/modonomicon/network/ClientNetworking.java`
- Modify: `neo/src/main/java/com/klikli_dev/modonomicon/network/Networking.java`

- [ ] **Step 1: Remove unlock snapshot storage from `BookStatesSaveData`**

Edit `common/src/main/java/com/klikli_dev/modonomicon/bookstate/BookStatesSaveData.java` so the codec and data model stop storing `unlockStates`.

Change the codec from:

```java
Codec.unboundedMap(Codecs.UUID, BookUnlockStates.CODEC).fieldOf("unlockStates").forGetter((state) -> state.unlockStates),
Codec.unboundedMap(Codecs.UUID, BookVisualStates.CODEC).fieldOf("visualStates").forGetter((state) -> state.visualStates)
```

to:

```java
Codec.unboundedMap(Codecs.UUID, BookVisualStates.CODEC).fieldOf("visualStates").forGetter((state) -> state.visualStates)
```

Delete the `unlockStates` field, the `getUnlockStates(...)` method, and the constructor parameter for unlock states.

- [ ] **Step 2: Delete the unlock snapshot packet and narrow the sync request**

Delete `common/src/main/java/com/klikli_dev/modonomicon/networking/SyncBookUnlockStatesMessage.java`.

Then edit `RequestSyncBookStatesMessage.java` so `onServerReceived(...)` becomes:

```java
@Override
public void onServerReceived(MinecraftServer minecraftServer, ServerPlayer player) {
    BookVisualStateManager.get().syncFor(player);
}
```

Remove the `BookUnlockStateManager` import.

- [ ] **Step 3: Remove unlock-state recompute followups from book read/reload flows**

In these files, remove the final `updateAndSyncFor(...)` / `syncFor(...)` calls that only exist to rebuild a legacy unlock snapshot:

```text
common/src/main/java/com/klikli_dev/modonomicon/networking/BookEntryReadMessage.java
common/src/main/java/com/klikli_dev/modonomicon/networking/BookCategoryReadMessage.java
common/src/main/java/com/klikli_dev/modonomicon/networking/ClickReadAllButtonMessage.java
common/src/main/java/com/klikli_dev/modonomicon/networking/ReloadResourcesDoneMessage.java
```

Keep research hook invocation and visual sync where needed.

- [ ] **Step 4: Remove or retire legacy unlock commands**

If the commands are purely progression-snapshot commands, remove their registration from `CommandRegistry.java` and delete or stub out:

```text
common/src/main/java/com/klikli_dev/modonomicon/command/ResetBookUnlocksCommand.java
common/src/main/java/com/klikli_dev/modonomicon/command/SaveUnlocksCommand.java
common/src/main/java/com/klikli_dev/modonomicon/command/LoadUnlocksCommand.java
```

Do not replace them with compatibility behavior in this slice.

- [ ] **Step 5: Remove unlock snapshot packet registrations on Fabric and Neo**

Delete the `SyncBookUnlockStatesMessage` registration and receiver lines from:

```text
fabric/src/main/java/com/klikli_dev/modonomicon/network/Networking.java
fabric/src/main/java/com/klikli_dev/modonomicon/network/ClientNetworking.java
neo/src/main/java/com/klikli_dev/modonomicon/network/Networking.java
```

- [ ] **Step 6: Run compile to verify the snapshot path is gone**

Run:

```bash
./gradlew.bat :common:compileJava :fabric:compileJava :neo:compileJava
```

Expected:

```text
BUILD SUCCESSFUL
```

- [ ] **Step 7: Commit**

Run:

```bash
git add common/src/main/java/com/klikli_dev/modonomicon/bookstate/BookStatesSaveData.java common/src/main/java/com/klikli_dev/modonomicon/bookstate/BookUnlockStates.java common/src/main/java/com/klikli_dev/modonomicon/bookstate/BookUnlockStateManager.java common/src/main/java/com/klikli_dev/modonomicon/networking/RequestSyncBookStatesMessage.java common/src/main/java/com/klikli_dev/modonomicon/networking/SyncBookUnlockStatesMessage.java common/src/main/java/com/klikli_dev/modonomicon/networking/BookEntryReadMessage.java common/src/main/java/com/klikli_dev/modonomicon/networking/BookCategoryReadMessage.java common/src/main/java/com/klikli_dev/modonomicon/networking/ClickReadAllButtonMessage.java common/src/main/java/com/klikli_dev/modonomicon/networking/ReloadResourcesDoneMessage.java common/src/main/java/com/klikli_dev/modonomicon/command/ResetBookUnlocksCommand.java common/src/main/java/com/klikli_dev/modonomicon/command/SaveUnlocksCommand.java common/src/main/java/com/klikli_dev/modonomicon/command/LoadUnlocksCommand.java common/src/main/java/com/klikli_dev/modonomicon/registry/CommandRegistry.java fabric/src/main/java/com/klikli_dev/modonomicon/network/Networking.java fabric/src/main/java/com/klikli_dev/modonomicon/network/ClientNetworking.java neo/src/main/java/com/klikli_dev/modonomicon/network/Networking.java
git commit -m "refactor: remove book unlock snapshot authority"
```

## Task 6: Simplify Loader Lifecycle Hooks To Research + Visual State Only

**Files:**
- Modify: `fabric/src/main/java/com/klikli_dev/modonomicon/ModonomiconFabric.java`
- Modify: `fabric/src/main/java/com/klikli_dev/modonomicon/mixin/MixinPlayerAdvancements.java`
- Modify: `neo/src/main/java/com/klikli_dev/modonomicon/ModonomiconNeo.java`

- [ ] **Step 1: Remove join-time unlock recompute/sync on Fabric**

In `fabric/src/main/java/com/klikli_dev/modonomicon/ModonomiconFabric.java`, change the join sync block from:

```java
BookUnlockStateManager.get().updateAndSyncFor(handler.getPlayer());
BookVisualStateManager.get().syncFor(handler.getPlayer());
ResearchStateManager.get().onDatapackSync(handler.getPlayer());
```

to:

```java
BookVisualStateManager.get().syncFor(handler.getPlayer());
ResearchStateManager.get().onDatapackSync(handler.getPlayer());
ResearchStateManager.get().syncFor(handler.getPlayer());
```

Remove any now-unused `BookUnlockStateManager` import.

- [ ] **Step 2: Remove advancement-triggered unlock recompute on Fabric**

In `fabric/src/main/java/com/klikli_dev/modonomicon/mixin/MixinPlayerAdvancements.java`, change the post-advancement path from:

```java
if (ResearchServices.advancements().onAdvancement(player, advancement.id())) {
    BookUnlockStateManager.get().onAdvancement(player);
}
```

to:

```java
if (ResearchServices.advancements().onAdvancement(player, advancement.id())) {
    ResearchStateManager.get().syncFor(player);
    BookVisualStateManager.get().syncFor(player);
}
```

Add/remove imports accordingly.

- [ ] **Step 3: Apply the same lifecycle simplification on Neo**

In `neo/src/main/java/com/klikli_dev/modonomicon/ModonomiconNeo.java`, remove the join, advancement, tick, and unload hooks that exist only to support unlock-state caching or deferred unlock recompute.

The advancement handler should follow the same research-first sync shape:

```java
if (ResearchServices.advancements().onAdvancement(player, e.getAdvancement().id())) {
    ResearchStateManager.get().syncFor(player);
    BookVisualStateManager.get().syncFor(player);
}
```

If the server tick handler becomes unlock-state-only after prior tasks, delete that hook registration entirely.

- [ ] **Step 4: Run compile to verify loader lifecycle parity**

Run:

```bash
./gradlew.bat :fabric:compileJava :neo:compileJava
```

Expected:

```text
BUILD SUCCESSFUL
```

- [ ] **Step 5: Commit**

Run:

```bash
git add fabric/src/main/java/com/klikli_dev/modonomicon/ModonomiconFabric.java fabric/src/main/java/com/klikli_dev/modonomicon/mixin/MixinPlayerAdvancements.java neo/src/main/java/com/klikli_dev/modonomicon/ModonomiconNeo.java
git commit -m "refactor: simplify research visibility lifecycle"
```

## Task 7: Final Verification And Manual Cutover Checks

**Files:**
- Reuse: `common/src/main/java/com/klikli_dev/modonomicon/bookstate/visibility/DefaultBookVisibilityService.java`
- Reuse: `common/src/main/java/com/klikli_dev/modonomicon/research/state/ResearchStateManager.java`
- Reuse: `common/src/main/java/com/klikli_dev/modonomicon/bookstate/BookVisualStateManager.java`
- Reuse: `common/src/main/java/com/klikli_dev/modonomicon/book/conditions/BookCategoryHasVisibleEntriesCondition.java`
- Reuse: `common/src/main/java/com/klikli_dev/modonomicon/book/conditions/BookResearchNodeUnlockedCondition.java`

- [ ] **Step 1: Run the full compile and datagen verification set**

Run:

```bash
./gradlew.bat :common:compileJava :fabric:compileJava :neo:compileJava fabric:runDatagen neo:runClientData
```

Expected:

```text
BUILD SUCCESSFUL
```

- [ ] **Step 2: Verify unlock snapshot authority is gone from the codebase**

Run:

```bash
rg "SyncBookUnlockStatesMessage|unlockedEntries|unlockedCategories|unlockedPages|updateAndSyncFor\(|clearResearchBackedUnlocksFor\(" common/src fabric/src neo/src
```

Expected:

```text
[no output, or only hits in intentionally retained transitional comments removed before commit]
```

- [ ] **Step 3: Manually verify category, entry, and page visibility in-game**

Run one loader runtime. Prefer Neo because it already has the research-first advancement hook path:

```bash
./gradlew.bat neo:runClient
```

In-game verification:

```text
1. Open the demo book from a fresh profile.
2. Confirm research-gated categories are hidden until their research conditions are satisfied.
3. Confirm research-gated entries become visible without any unlock snapshot sync packet.
4. Confirm page-level conditions hide and reveal pages correctly from computed visibility.
5. Confirm search, bookmarks, link navigation, and category/entry buttons reflect computed visibility correctly.
6. Confirm newly visible entries/categories still show unread/new visual behavior.
7. Run `modonomicon research reset` and confirm visibility falls back from research state alone, without rebuilding legacy unlock state.
```

- [ ] **Step 4: Commit the final verified cutover state**

Run:

```bash
git add common/src/main/java/com/klikli_dev/modonomicon/networking/RequestSyncResearchStateMessage.java common/src/main/java/com/klikli_dev/modonomicon/networking/SyncResearchStateMessage.java common/src/main/java/com/klikli_dev/modonomicon/research/state/ResearchStateManager.java common/src/main/java/com/klikli_dev/modonomicon/research/state/ResearchStatesSaveData.java common/src/main/java/com/klikli_dev/modonomicon/research/state/PlayerResearchState.java common/src/main/java/com/klikli_dev/modonomicon/bookstate/BookServices.java common/src/main/java/com/klikli_dev/modonomicon/bookstate/access/BookStateAccess.java common/src/main/java/com/klikli_dev/modonomicon/bookstate/access/DefaultBookStateAccess.java common/src/main/java/com/klikli_dev/modonomicon/bookstate/visibility/BookVisibilityService.java common/src/main/java/com/klikli_dev/modonomicon/bookstate/visibility/DefaultBookVisibilityService.java common/src/main/java/com/klikli_dev/modonomicon/bookstate/BookVisualStateManager.java common/src/main/java/com/klikli_dev/modonomicon/bookstate/BookStatesSaveData.java common/src/main/java/com/klikli_dev/modonomicon/bookstate/BookUnlockStates.java common/src/main/java/com/klikli_dev/modonomicon/bookstate/BookUnlockStateManager.java common/src/main/java/com/klikli_dev/modonomicon/bookstate/visual/BookVisibilitySnapshots.java common/src/main/java/com/klikli_dev/modonomicon/bookstate/interaction/DefaultBookInteractionService.java common/src/main/java/com/klikli_dev/modonomicon/book/entries/BookContentEntry.java common/src/main/java/com/klikli_dev/modonomicon/book/BookCommand.java common/src/main/java/com/klikli_dev/modonomicon/networking/RequestSyncBookStatesMessage.java common/src/main/java/com/klikli_dev/modonomicon/networking/SyncBookUnlockStatesMessage.java common/src/main/java/com/klikli_dev/modonomicon/networking/BookEntryReadMessage.java common/src/main/java/com/klikli_dev/modonomicon/networking/BookCategoryReadMessage.java common/src/main/java/com/klikli_dev/modonomicon/networking/ClickReadAllButtonMessage.java common/src/main/java/com/klikli_dev/modonomicon/networking/ReloadResourcesDoneMessage.java common/src/main/java/com/klikli_dev/modonomicon/command/ResetBookUnlocksCommand.java common/src/main/java/com/klikli_dev/modonomicon/command/SaveUnlocksCommand.java common/src/main/java/com/klikli_dev/modonomicon/command/LoadUnlocksCommand.java common/src/main/java/com/klikli_dev/modonomicon/registry/CommandRegistry.java common/src/main/java/com/klikli_dev/modonomicon/client/gui/book/entry/linkhandler/BookLinkHandler.java common/src/main/java/com/klikli_dev/modonomicon/client/gui/book/entry/ContentRenderingScreen.java common/src/main/java/com/klikli_dev/modonomicon/client/gui/book/search/BookSearchScreen.java common/src/main/java/com/klikli_dev/modonomicon/client/gui/book/index/BookCategoryIndexScreen.java common/src/main/java/com/klikli_dev/modonomicon/client/gui/book/button/EntryListButton.java common/src/main/java/com/klikli_dev/modonomicon/client/gui/book/button/CategoryListButton.java common/src/main/java/com/klikli_dev/modonomicon/client/gui/book/button/CategoryButton.java common/src/main/java/com/klikli_dev/modonomicon/client/gui/book/bookmarks/BookBookmarksScreen.java common/src/main/java/com/klikli_dev/modonomicon/client/gui/book/node/BookParentNodeScreen.java common/src/main/java/com/klikli_dev/modonomicon/client/gui/book/index/BookParentIndexScreen.java common/src/main/java/com/klikli_dev/modonomicon/client/gui/book/recentlyunlocked/BookRecentlyUnlockedScreen.java fabric/src/main/java/com/klikli_dev/modonomicon/network/Networking.java fabric/src/main/java/com/klikli_dev/modonomicon/network/ClientNetworking.java fabric/src/main/java/com/klikli_dev/modonomicon/ModonomiconFabric.java fabric/src/main/java/com/klikli_dev/modonomicon/mixin/MixinPlayerAdvancements.java neo/src/main/java/com/klikli_dev/modonomicon/network/Networking.java neo/src/main/java/com/klikli_dev/modonomicon/ModonomiconNeo.java
git commit -m "feat: cut book visibility over to research"
```

## Self-Review Against The Spec

- The plan removes category, entry, and page unlock state as progression authority through Tasks 2-6.
- The plan replaces persisted unlock visibility with computed visibility/access through Tasks 2 and 3.
- The plan keeps visual/interaction state and unread/bookmark/navigation concerns through Tasks 4 and 5.
- The plan removes legacy unlock snapshot sync/recompute through Tasks 1, 5, and 6.
- The plan keeps research as the sole durable progression authority by adding dedicated research client sync in Task 1.
- No new trigger families, values work, bypass policy, or authoring sugar are included.
