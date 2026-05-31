# Research Progress Button Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Replace the old read-all book button with a research-progress button that replays `entry_viewed_once` hooks for visible entries on normal click and all book entries on shift-click, without directly mutating visual unread/read state.

**Architecture:** Keep the button in the same UI slot, but rename the button class, message class, state suppliers, and i18n keys so they describe research progression rather than read-state cleanup. The server handler will select target entries by mode, replay research hooks only, then use the existing research-sync and visibility-delta visual refresh path when research changes.

**Tech Stack:** Java 25, Minecraft mod common/Fabric/Neo modules, custom networking, Mojang codecs, datagen-backed lang generation, Gradle 9.

---

## File Map

### Create
- `common/src/main/java/com/klikli_dev/modonomicon/client/gui/book/button/ResearchProgressButton.java` - renamed side button with research-oriented suppliers, tooltips, and click behavior.
- `common/src/main/java/com/klikli_dev/modonomicon/networking/ClickResearchProgressButtonMessage.java` - renamed serverbound payload for visible-only vs all-book research progression.

### Modify
- `common/src/main/java/com/klikli_dev/modonomicon/api/ModonomiconConstants.java` - replace read-all GUI translation keys with research-progress keys.
- `common/src/main/java/com/klikli_dev/modonomicon/client/gui/book/node/BookParentNodeScreen.java` - rename button state fields and compute researchable visible/all entry state.
- `common/src/main/java/com/klikli_dev/modonomicon/client/gui/book/index/BookParentIndexScreen.java` - rename button state fields and compute researchable visible/all entry state.
- `common/src/main/java/com/klikli_dev/modonomicon/client/gui/book/theme/BookContentTheme.java` - rename read-all button sprite accessors to research-progress terminology.
- `common/src/main/java/com/klikli_dev/modonomicon/client/gui/book/theme/defaults/DefaultBookTheme.java` - implement renamed sprite accessors using the existing assets.
- `common/src/main/java/com/klikli_dev/modonomicon/datagen/EnUsProvider.java` - replace button label/tooltip text with research-progress wording.
- `fabric/src/main/java/com/klikli_dev/modonomicon/network/Networking.java` - register the renamed message.
- `neo/src/main/java/com/klikli_dev/modonomicon/network/Networking.java` - register the renamed message.
- `forge/src/main/java/com/klikli_dev/modonomicon/network/Networking.java` - register the renamed message.

### Delete
- `common/src/main/java/com/klikli_dev/modonomicon/client/gui/book/button/ReadAllButton.java`
- `common/src/main/java/com/klikli_dev/modonomicon/networking/ClickReadAllButtonMessage.java`

---

### Task 1: Rename the button and reframe its UI state

**Files:**
- Create: `common/src/main/java/com/klikli_dev/modonomicon/client/gui/book/button/ResearchProgressButton.java`
- Modify: `common/src/main/java/com/klikli_dev/modonomicon/client/gui/book/node/BookParentNodeScreen.java`
- Modify: `common/src/main/java/com/klikli_dev/modonomicon/client/gui/book/index/BookParentIndexScreen.java`
- Delete: `common/src/main/java/com/klikli_dev/modonomicon/client/gui/book/button/ReadAllButton.java`

- [ ] **Step 1: Create `ResearchProgressButton` from the current button implementation**

Create `common/src/main/java/com/klikli_dev/modonomicon/client/gui/book/button/ResearchProgressButton.java` by copying the current button structure and renaming the suppliers/callbacks to research language:

```java
public class ResearchProgressButton extends Button {

    public static final int WIDTH = 44;
    public static final int HEIGHT = 20;

    private final BookParentScreen parent;
    private final int scissorX;
    private final MutableComponent tooltipVisible;
    private final MutableComponent tooltipAll;
    private final MutableComponent tooltipNone;
    private final MutableComponent tooltipShiftInstructions;
    private final MutableComponent tooltipShiftWarning;
    private final Supplier<Boolean> hasVisibleResearchProgress;
    private final Supplier<Boolean> hasAnyResearchProgress;
    private final Runnable onProgressVisible;
    private final Runnable onProgressAll;
    private boolean wasHovered;
    private int tooltipMsDelay;
    private long hoveredStartTime;

    public ResearchProgressButton(BookParentScreen parent, int x, int y, int scissorX,
                                  Supplier<Boolean> hasVisibleResearchProgress,
                                  Supplier<Boolean> hasAnyResearchProgress,
                                  Runnable onProgressVisible,
                                  Runnable onProgressAll) {
        super(x, y, WIDTH, HEIGHT,
                Component.translatable(Gui.BUTTON_RESEARCH_PROGRESS),
                ResearchProgressButton::onPress, Button.DEFAULT_NARRATION);
        this.parent = parent;
        this.scissorX = scissorX;
        this.tooltipVisible = Component.translatable(Gui.BUTTON_RESEARCH_PROGRESS_TOOLTIP_VISIBLE);
        this.tooltipAll = Component.translatable(Gui.BUTTON_RESEARCH_PROGRESS_TOOLTIP_ALL);
        this.tooltipNone = Component.translatable(Gui.BUTTON_RESEARCH_PROGRESS_TOOLTIP_NONE);
        this.tooltipShiftInstructions = Component.translatable(Gui.BUTTON_RESEARCH_PROGRESS_TOOLTIP_SHIFT_INSTRUCTIONS);
        this.tooltipShiftWarning = Component.translatable(Gui.BUTTON_RESEARCH_PROGRESS_TOOLTIP_SHIFT_WARNING);
        this.hasVisibleResearchProgress = hasVisibleResearchProgress;
        this.hasAnyResearchProgress = hasAnyResearchProgress;
        this.onProgressVisible = onProgressVisible;
        this.onProgressAll = onProgressAll;
    }
}
```

- [ ] **Step 2: Implement the button click behavior with the renamed message**

Inside the new button class, wire normal click to visible-only progression and shift-click to all-book progression:

```java
private static void onPress(Button button) {
    ((ResearchProgressButton) button).onPress();
}

private void onPress() {
    if (this.hasVisibleResearchProgress.get() && !Minecraft.getInstance().hasShiftDown()) {
        Services.NETWORK.sendToServer(new ClickResearchProgressButtonMessage(this.parent.getBook().getId(), false));
        this.onProgressVisible.run();
    } else if (this.hasAnyResearchProgress.get() && Minecraft.getInstance().hasShiftDown()) {
        Services.NETWORK.sendToServer(new ClickResearchProgressButtonMessage(this.parent.getBook().getId(), true));
        this.onProgressAll.run();
    }
}
```

- [ ] **Step 3: Update the button active state, sprite selection, and tooltip logic**

Use the same existing theme sprites for now, but drive them from research-progress suppliers instead of unread suppliers:

```java
@Override
protected void extractContents(GuiGraphicsExtractor guiGraphics, int i, int j, float f) {
    this.active = this.hasVisibleResearchProgress.get() || this.hasAnyResearchProgress.get();
    if (!this.active) return;

    guiGraphics.pose().pushMatrix();
    var hovered = this.isHovered();

    GuiButtonSprites sprites = this.hasVisibleResearchProgress.get()
            ? this.parent.getBook().theme().content().researchVisibleButton()
            : this.parent.getBook().theme().content().researchNoneButton();

    if (Minecraft.getInstance().hasShiftDown()) {
        sprites = this.parent.getBook().theme().content().researchAllButton();
    }

    var background = this.parent.getBook().theme().content().researchProgressButtonBackground().state(hovered, false);
    // keep the remaining rendering identical to the old button
}

public List<Component> getCustomTooltip() {
    if (Minecraft.getInstance().hasShiftDown()) {
        return List.of(this.tooltipAll, Component.empty(), this.tooltipShiftWarning);
    }

    if (this.hasVisibleResearchProgress.get()) {
        return List.of(this.tooltipVisible, Component.empty(), this.tooltipShiftInstructions);
    }

    return List.of(this.tooltipNone, Component.empty(), this.tooltipShiftInstructions);
}
```

- [ ] **Step 4: Update `BookParentNodeScreen` to compute research-progress state**

Replace the old unread-oriented button fields with research-oriented fields near the existing booleans:

```java
private boolean hasVisibleResearchProgress;
private boolean hasAnyResearchProgress;
```

Then change `updateUnreadEntriesState()` to compute both unread state and research-progress state:

```java
protected void updateUnreadEntriesState() {
    this.hasUnreadEntries = this.book.getEntries().values().stream().anyMatch(e -> BookServices.stateAccess().isEntryUnread(this.minecraft.player, e));
    this.hasUnreadCategories = this.book.getCategories().values().stream().anyMatch(c -> BookServices.interaction().isCategoryUnread(this.minecraft.player, c));
    this.hasUnreadUnlockedEntries = this.book.getEntries().values().stream().anyMatch(e ->
            BookServices.visibility().isVisible(this.minecraft.player, e) && BookServices.stateAccess().isEntryUnread(this.minecraft.player, e));
    this.hasUnreadUnlockedCategories = this.book.getCategories().values().stream().anyMatch(c ->
            BookServices.visibility().isVisible(this.minecraft.player, c) && BookServices.interaction().isCategoryUnread(this.minecraft.player, c));
    this.hasVisibleResearchProgress = this.book.getEntries().values().stream().anyMatch(e -> BookServices.visibility().isVisible(this.minecraft.player, e));
    this.hasAnyResearchProgress = !this.book.getEntries().isEmpty();
}
```

Keep the unread flags for the existing unread UI, but pass only the new research booleans into the renamed button.

- [ ] **Step 5: Update `BookParentIndexScreen` in the same way**

Mirror the same state-field and supplier changes in `BookParentIndexScreen`:

```java
private boolean hasVisibleResearchProgress;
private boolean hasAnyResearchProgress;
```

Use the same `BookServices.visibility().isVisible(...)` check for visible-only progression and `!this.book.getEntries().isEmpty()` for all-book progression.

- [ ] **Step 6: Replace button construction sites with the renamed class**

In both parent screens, replace `ReadAllButton` usage:

```java
var researchProgressButton = new ResearchProgressButton(this, rightButtonX, readAllButtonY, scissorX,
        () -> this.hasVisibleResearchProgress,
        () -> this.hasAnyResearchProgress,
        () -> this.hasVisibleResearchProgress = false,
        () -> this.hasAnyResearchProgress = false);
```

and in the index screen:

```java
var researchProgressButton = new ResearchProgressButton(this, searchButtonX, readAllButtonY, scissorX,
        () -> this.hasVisibleResearchProgress,
        () -> this.hasAnyResearchProgress,
        () -> this.hasVisibleResearchProgress = false,
        () -> this.hasAnyResearchProgress = false);
```

Also replace `ReadAllButton.HEIGHT` references with `ResearchProgressButton.HEIGHT`.

- [ ] **Step 7: Run compile to verify the UI rename compiles**

Run:

```bash
./gradlew.bat :common:compileJava :fabric:compileJava :neo:compileJava
```

Expected:

```text
BUILD SUCCESSFUL
```

- [ ] **Step 8: Commit**

Run:

```bash
git add common/src/main/java/com/klikli_dev/modonomicon/client/gui/book/button/ResearchProgressButton.java common/src/main/java/com/klikli_dev/modonomicon/client/gui/book/node/BookParentNodeScreen.java common/src/main/java/com/klikli_dev/modonomicon/client/gui/book/index/BookParentIndexScreen.java common/src/main/java/com/klikli_dev/modonomicon/client/gui/book/button/ReadAllButton.java
git commit -m "refactor: rename read all button for research"
```

### Task 2: Rename the network message and make it research-only

**Files:**
- Create: `common/src/main/java/com/klikli_dev/modonomicon/networking/ClickResearchProgressButtonMessage.java`
- Modify: `fabric/src/main/java/com/klikli_dev/modonomicon/network/Networking.java`
- Modify: `neo/src/main/java/com/klikli_dev/modonomicon/network/Networking.java`
- Modify: `forge/src/main/java/com/klikli_dev/modonomicon/network/Networking.java`
- Delete: `common/src/main/java/com/klikli_dev/modonomicon/networking/ClickReadAllButtonMessage.java`

- [ ] **Step 1: Create the renamed serverbound message class**

Create `common/src/main/java/com/klikli_dev/modonomicon/networking/ClickResearchProgressButtonMessage.java` with the same payload shape, but renamed fields and research-only semantics:

```java
public class ClickResearchProgressButtonMessage implements Message {

    public static final Type<ClickResearchProgressButtonMessage> TYPE = new Type<>(Identifier.fromNamespaceAndPath(Modonomicon.MOD_ID, "click_research_progress_button"));

    public static final StreamCodec<RegistryFriendlyByteBuf, ClickResearchProgressButtonMessage> STREAM_CODEC = StreamCodec.composite(
            Identifier.STREAM_CODEC,
            (m) -> m.bookId,
            ByteBufCodecs.BOOL,
            (m) -> m.progressAll,
            ClickResearchProgressButtonMessage::new
    );

    public Identifier bookId;
    public boolean progressAll;
}
```

- [ ] **Step 2: Implement research-only target selection in the server handler**

In `onServerReceived(...)`, do not call `BookServices.interaction()` at all. Use this shape instead:

```java
@Override
public void onServerReceived(MinecraftServer minecraftServer, ServerPlayer player) {
    var book = BookDataManager.get().getBook(this.bookId);
    if (book == null) {
        return;
    }

    var before = BookVisibilitySnapshots.collect(player, book);
    boolean researchChanged = false;

    for (var entry : book.getEntries().values()) {
        if (!this.progressAll && !BookServices.visibility().isVisible(player, entry)) {
            continue;
        }
        researchChanged |= ResearchServices.hooks().onEntryViewedOnce(player, entry.getId());
    }

    if (researchChanged) {
        BookVisualStateManager.get().updateVisibilityDrivenUnread(player, book, before, BookVisibilitySnapshots.collect(player, book));
        ResearchServices.state().syncFor(player);
        BookVisualStateManager.get().syncFor(player);
    }
}
```

This preserves visibility-driven unread updates while preventing direct visual read/unread mutation.

- [ ] **Step 3: Register the renamed message on Fabric, Neo, and Forge**

Replace `ClickReadAllButtonMessage` registrations with `ClickResearchProgressButtonMessage` in:

`fabric/src/main/java/com/klikli_dev/modonomicon/network/Networking.java`

```java
ServerPlayNetworking.registerGlobalReceiver(ClickResearchProgressButtonMessage.TYPE, new ServerMessageHandler<>());
PayloadTypeRegistry.serverboundPlay().register(ClickResearchProgressButtonMessage.TYPE, ClickResearchProgressButtonMessage.STREAM_CODEC);
```

`neo/src/main/java/com/klikli_dev/modonomicon/network/Networking.java`

```java
registrar.playToServer(ClickResearchProgressButtonMessage.TYPE, ClickResearchProgressButtonMessage.STREAM_CODEC, MessageHandler::handle);
```

`forge/src/main/java/com/klikli_dev/modonomicon/network/Networking.java`

```java
INSTANCE.messageBuilder(ClickResearchProgressButtonMessage.class)
        .encoder(encoder(ClickResearchProgressButtonMessage.STREAM_CODEC))
        .decoder(decoder(ClickResearchProgressButtonMessage.STREAM_CODEC))
        .consumerNetworkThread((BiConsumer<ClickResearchProgressButtonMessage, CustomPayloadEvent.Context>) MessageHandler::handle)
        .add();
```

- [ ] **Step 4: Remove the old message class and remaining references**

Delete `common/src/main/java/com/klikli_dev/modonomicon/networking/ClickReadAllButtonMessage.java` and replace all imports/usages with the new class.

- [ ] **Step 5: Run compile to verify networking rename and research-only flow**

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
git add common/src/main/java/com/klikli_dev/modonomicon/networking/ClickResearchProgressButtonMessage.java common/src/main/java/com/klikli_dev/modonomicon/networking/ClickReadAllButtonMessage.java fabric/src/main/java/com/klikli_dev/modonomicon/network/Networking.java neo/src/main/java/com/klikli_dev/modonomicon/network/Networking.java forge/src/main/java/com/klikli_dev/modonomicon/network/Networking.java
git commit -m "refactor: make bulk button research only"
```

### Task 3: Rename theme accessors and user-facing text

**Files:**
- Modify: `common/src/main/java/com/klikli_dev/modonomicon/api/ModonomiconConstants.java`
- Modify: `common/src/main/java/com/klikli_dev/modonomicon/client/gui/book/theme/BookContentTheme.java`
- Modify: `common/src/main/java/com/klikli_dev/modonomicon/client/gui/book/theme/defaults/DefaultBookTheme.java`
- Modify: `common/src/main/java/com/klikli_dev/modonomicon/datagen/EnUsProvider.java`

- [ ] **Step 1: Replace the GUI translation keys with research-progress keys**

In `ModonomiconConstants.Gui`, replace the read-all key block with:

```java
public static final String BUTTON_RESEARCH_PROGRESS = PREFIX + "button.research_progress";
public static final String BUTTON_RESEARCH_PROGRESS_TOOLTIP_VISIBLE = PREFIX + "button.research_progress.tooltip.visible";
public static final String BUTTON_RESEARCH_PROGRESS_TOOLTIP_ALL = PREFIX + "button.research_progress.tooltip.all";
public static final String BUTTON_RESEARCH_PROGRESS_TOOLTIP_NONE = PREFIX + "button.research_progress.tooltip.none";
public static final String BUTTON_RESEARCH_PROGRESS_TOOLTIP_SHIFT_INSTRUCTIONS = PREFIX + "button.research_progress.tooltip.shift";
public static final String BUTTON_RESEARCH_PROGRESS_TOOLTIP_SHIFT_WARNING = PREFIX + "button.research_progress.tooltip.shift_warning";
```

- [ ] **Step 2: Rename the theme sprite accessors without changing assets**

In `BookContentTheme.java`, rename the accessors to:

```java
GuiButtonSprites researchProgressButtonBackground();
GuiButtonSprites researchAllButton();
GuiButtonSprites researchNoneButton();
GuiButtonSprites researchVisibleButton();
```

Then update `DefaultBookTheme.java` to implement those methods by returning the same existing sprite definitions that previously powered the read-all button.

- [ ] **Step 3: Replace the English strings with research-progress wording**

Update `EnUsProvider.java` entries to:

```java
this.add(Gui.BUTTON_RESEARCH_PROGRESS, "Advance research progression");
this.add(Gui.BUTTON_RESEARCH_PROGRESS_TOOLTIP_VISIBLE, "Replay §avisible§r entry-viewed research hooks for this book.");
this.add(Gui.BUTTON_RESEARCH_PROGRESS_TOOLTIP_SHIFT_INSTRUCTIONS, "Shift-Click to replay research hooks for §call§r entries in this book.");
this.add(Gui.BUTTON_RESEARCH_PROGRESS_TOOLTIP_ALL, "Replay research hooks for §call§r entries in this book.");
this.add(Gui.BUTTON_RESEARCH_PROGRESS_TOOLTIP_SHIFT_WARNING, "§l§cWarning:§r This may unlock research for entries you have not opened yet.");
this.add(Gui.BUTTON_RESEARCH_PROGRESS_TOOLTIP_NONE, "There are currently no entries in this book that can be progressed by this action.");
```

These strings must not mention marking entries read or clearing unread markers.

- [ ] **Step 4: Run datagen-backed verification**

Run:

```bash
./gradlew.bat :common:compileJava :fabric:compileJava :neo:compileJava fabric:runDatagen neo:runClientData
```

Expected:

```text
BUILD SUCCESSFUL
```

- [ ] **Step 5: Commit**

Run:

```bash
git add common/src/main/java/com/klikli_dev/modonomicon/api/ModonomiconConstants.java common/src/main/java/com/klikli_dev/modonomicon/client/gui/book/theme/BookContentTheme.java common/src/main/java/com/klikli_dev/modonomicon/client/gui/book/theme/defaults/DefaultBookTheme.java common/src/main/java/com/klikli_dev/modonomicon/datagen/EnUsProvider.java
git commit -m "feat: rename bulk research progress button text"
```

### Task 4: Final verification and cleanup

**Files:**
- Modify as needed based on verification fallout.

- [ ] **Step 1: Search for stale read-all names and references**

Run:

```bash
rg "ReadAllButton|ClickReadAllButtonMessage|BUTTON_READ_ALL|read all|read_unlocked" common/src fabric/src neo/src forge/src
```

Expected:

```text
No matches, or only unchanged asset/helper names you intentionally kept out of scope.
```

- [ ] **Step 2: Manually verify the demo-book behavior expectations**

Run:

```bash
./gradlew.bat neo:runClient
```

Then verify:

1. normal click on the button advances only currently visible demo-book condition chains
2. shift-click advances hidden later demo-book entries too
3. clicking the button does not directly clear unread badges
4. newly visible content can still become unread as a result of research changing
5. tooltip wording describes research progression, not read-state cleanup

- [ ] **Step 3: Commit any final follow-up adjustments**

If manual verification reveals a minor issue, stage only the needed fixes and commit with a focused message, for example:

```bash
git add <exact files>
git commit -m "fix: polish research progress button behavior"
```
