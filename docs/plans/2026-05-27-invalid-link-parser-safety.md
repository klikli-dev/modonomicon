# Invalid Link Parser Safety Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Make markdown link failures safe in multiplayer by keeping book data codec-safe, showing the error screen only when `allowOpenBooksWithInvalidLinks = false`, and rendering broken non-clickable links when the flag is `true`.

**Architecture:** Keep the existing prerender pipeline, but separate two concerns that are currently conflated: whether an error should block opening the book, and whether the book model is safe to serialize. Centralize link-failure fallback in the markdown renderer, classify errors as blocking or non-blocking, and make `RenderedBookTextHolder` serialize back to its original `BookTextHolder` source data.

**Tech Stack:** Java 25, NeoForge/Fabric multi-loader Gradle build, CommonMark renderer, Minecraft `StreamCodec` / DFU codecs.

---

## File map

### Modify
- `common/src/main/java/com/klikli_dev/modonomicon/book/error/BookErrorInfo.java`
  - Add a `blocksOpening` flag so invalid-link errors can be recorded without always forcing the error screen.
- `common/src/main/java/com/klikli_dev/modonomicon/book/error/BookErrorHolder.java`
  - Add helpers for blocking-error queries.
- `common/src/main/java/com/klikli_dev/modonomicon/book/error/BookErrorManager.java`
  - Add blocking-aware query/record helpers used by the GUI gate and prerender pipeline.
- `common/src/main/java/com/klikli_dev/modonomicon/client/gui/BookGuiManager.java`
  - Gate `BookErrorScreen` on blocking errors only.
- `common/src/main/java/com/klikli_dev/modonomicon/client/gui/book/BookErrorScreen.java`
  - Prefer the first blocking error when preparing the displayed message.
- `common/src/main/java/com/klikli_dev/modonomicon/data/BookDataManager.java`
  - Skip markdown prerender only when the book already has blocking build errors.
- `common/src/main/java/com/klikli_dev/modonomicon/client/gui/book/markdown/CoreComponentNodeRenderer.java`
  - Catch any link parsing/render failure and convert it into a safe broken-link fallback.
- `common/src/main/java/com/klikli_dev/modonomicon/client/gui/book/markdown/BookLinkRenderer.java`
  - Remove the special local lenient-mode catch and let the central renderer handle it.
- `common/src/main/java/com/klikli_dev/modonomicon/client/gui/book/markdown/ItemLinkRenderer.java`
  - Stop swallowing parse exceptions locally so item-link failures use the same central fallback path.
- `common/src/main/java/com/klikli_dev/modonomicon/book/BookTextHolder.java`
  - Refactor codec serialization through an overridable source-data method.
- `common/src/main/java/com/klikli_dev/modonomicon/book/RenderedBookTextHolder.java`
  - Serialize the original holder, not the renderer cache.
- `common/src/main/java/com/klikli_dev/modonomicon/api/ModonomiconConstants.java`
  - Add a generic hover key for broken markdown links.
- `common/src/main/java/com/klikli_dev/modonomicon/datagen/EnUsProvider.java`
  - Provide the new hover text string.

### Validate with existing demo content
- `common/src/main/java/com/klikli_dev/modonomicon/datagen/book/demo/formatting/LinkFormattingEntry.java`
  - Use the existing `invalid_link` demo page for manual verification; only modify it if the current page is no longer sufficient.

### Generated outputs if localization key changes
- `fabric/src/generated/resources/assets/modonomicon/lang/en_us.json`
- `neo/src/generated/resources/assets/modonomicon/lang/en_us.json`

## Task 1: Classify book errors as blocking vs non-blocking

**Files:**
- Modify: `common/src/main/java/com/klikli_dev/modonomicon/book/error/BookErrorInfo.java`
- Modify: `common/src/main/java/com/klikli_dev/modonomicon/book/error/BookErrorHolder.java`
- Modify: `common/src/main/java/com/klikli_dev/modonomicon/book/error/BookErrorManager.java`
- Modify: `common/src/main/java/com/klikli_dev/modonomicon/client/gui/BookGuiManager.java`
- Modify: `common/src/main/java/com/klikli_dev/modonomicon/client/gui/book/BookErrorScreen.java`
- Modify: `common/src/main/java/com/klikli_dev/modonomicon/data/BookDataManager.java`

- [ ] **Step 1: Add the `blocksOpening` flag to `BookErrorInfo`**

Replace the current class body with this shape so every recorded error explicitly says whether it should block book opening:

```java
public class BookErrorInfo {
    private final String errorMessage;
    private final Exception exception;
    private final String context;
    private final boolean blocksOpening;

    public BookErrorInfo(String errorMessage, Exception exception, String context) {
        this(errorMessage, exception, context, true);
    }

    public BookErrorInfo(String errorMessage, Exception exception, String context, boolean blocksOpening) {
        this.errorMessage = errorMessage;
        this.exception = exception;
        this.context = context;
        this.blocksOpening = blocksOpening;
    }

    public boolean blocksOpening() {
        return this.blocksOpening;
    }

    @Override
    public String toString() {
        var errorMessage = this.errorMessage == null ? "" : this.errorMessage;
        var context = this.context == null ? "" : this.context;
        var exception = this.exception == null ? "" : this.exception.toString();
        return "BookErrorInfo{ " +
                "\nerrorMessage='" + errorMessage + "'" +
                ", \ncontext='" + context + "'" +
                ", \nexception='" + exception + "'" +
                ", \nblocksOpening='" + this.blocksOpening + "'" +
                "\n}";
    }
}
```

- [ ] **Step 2: Add blocking-aware queries to `BookErrorHolder` and `BookErrorManager`**

Extend `BookErrorHolder` with helpers used by the GUI gate and error screen:

```java
public class BookErrorHolder {

    private final List<BookErrorInfo> errors = new ArrayList<>();

    public void addError(BookErrorInfo error) {
        this.errors.add(error);
    }

    public List<BookErrorInfo> getErrors() {
        return this.errors;
    }

    public boolean hasBlockingErrors() {
        return this.errors.stream().anyMatch(BookErrorInfo::blocksOpening);
    }

    public BookErrorInfo getFirstBlockingError() {
        return this.errors.stream().filter(BookErrorInfo::blocksOpening).findFirst().orElse(null);
    }
}
```

Add matching helpers to `BookErrorManager`:

```java
public boolean hasBlockingErrors(Identifier book) {
    var holder = this.booksErrors.get(book);
    return holder != null && holder.hasBlockingErrors();
}

public void error(String message, Exception exception, boolean blocksOpening) {
    this.error(new BookErrorInfo(message, exception, this.currentContext, blocksOpening));
}

public void error(Identifier book, String message, Exception exception, boolean blocksOpening) {
    this.error(book, new BookErrorInfo(message, exception, this.currentContext, blocksOpening));
}
```

Keep the existing overloads and have them default to `blocksOpening = true` so build failures stay strict by default.

- [ ] **Step 3: Gate prerender and GUI opening on blocking errors only**

In `BookGuiManager.showErrorScreen(...)`, change the guard to:

```java
protected boolean showErrorScreen(Identifier bookId) {
    if (BookErrorManager.get().hasBlockingErrors(bookId)) {
        var book = BookDataManager.get().getBook(bookId);
        Minecraft.getInstance().setScreen(new BookErrorScreen(book));
        return true;
    }
    return false;
}
```

In `BookDataManager.prerenderMarkdown(...)`, change the skip check so non-blocking markdown diagnostics do not suppress future render passes:

```java
if (!BookErrorManager.get().hasBlockingErrors(book.getId())) {
    try {
        book.prerenderMarkdown(textRenderer);
    } catch (Exception e) {
        BookErrorManager.get().error("Failed to render markdown for book '" + book.getId() + "'", e);
    }
} else {
    BookErrorManager.get().error("Cannot render markdown for book '" + book.getId() + " because of errors during book build'");
}
```

In `BookErrorScreen.prepareError()`, prefer a blocking error when present:

```java
var errorHolder = BookErrorManager.get().getErrors(this.book.getId());
var firstError = errorHolder.getFirstBlockingError();
if (firstError == null && !errorHolder.getErrors().isEmpty()) {
    firstError = errorHolder.getErrors().get(0);
}
```

- [ ] **Step 4: Compile the common sources after the error-classification pass**

Run: `./gradlew.bat :common:compileJava`

Expected: `BUILD SUCCESSFUL`

- [ ] **Step 5: Commit the error-classification pass**

```bash
git add common/src/main/java/com/klikli_dev/modonomicon/book/error/BookErrorInfo.java common/src/main/java/com/klikli_dev/modonomicon/book/error/BookErrorHolder.java common/src/main/java/com/klikli_dev/modonomicon/book/error/BookErrorManager.java common/src/main/java/com/klikli_dev/modonomicon/client/gui/BookGuiManager.java common/src/main/java/com/klikli_dev/modonomicon/client/gui/book/BookErrorScreen.java common/src/main/java/com/klikli_dev/modonomicon/data/BookDataManager.java
git commit -m "fix: distinguish blocking book errors"
```

## Task 2: Centralize safe broken-link fallback for all markdown link failures

**Files:**
- Modify: `common/src/main/java/com/klikli_dev/modonomicon/client/gui/book/markdown/CoreComponentNodeRenderer.java`
- Modify: `common/src/main/java/com/klikli_dev/modonomicon/client/gui/book/markdown/BookLinkRenderer.java`
- Modify: `common/src/main/java/com/klikli_dev/modonomicon/client/gui/book/markdown/ItemLinkRenderer.java`
- Modify: `common/src/main/java/com/klikli_dev/modonomicon/api/ModonomiconConstants.java`
- Modify: `common/src/main/java/com/klikli_dev/modonomicon/datagen/EnUsProvider.java`
- Regenerate: `fabric/src/generated/resources/assets/modonomicon/lang/en_us.json`
- Regenerate: `neo/src/generated/resources/assets/modonomicon/lang/en_us.json`

- [ ] **Step 1: Add a generic broken-link fallback helper in `CoreComponentNodeRenderer`**

Add imports for `Modonomicon`, `BookErrorManager`, and `ChatFormatting`, then add a helper like this near `visit(Link)`:

```java
private void renderBrokenLink(Link link, Exception exception) {
    var blocksOpening = !this.context.getBook().allowOpenBooksWithInvalidLinks();
    BookErrorManager.get().error(
            "Failed to parse markdown link '" + link.getDestination() + "'",
            exception,
            blocksOpening
    );

    Modonomicon.LOG.error(
            "Failed to parse markdown link. allowOpenBooksWithInvalidLinks = {}, blocksOpening = {}. Original error:",
            this.context.getBook().allowOpenBooksWithInvalidLinks(),
            blocksOpening,
            exception
    );

    var currentColor = this.context.getCurrentStyle().getColor();
    var hoverComponent = Component.translatable(Gui.HOVER_LINK_ERROR, link.getDestination()).withStyle(ChatFormatting.RED);

    this.context.setCurrentStyle(this.context.getCurrentStyle()
            .withColor(ChatFormatting.RED)
            .withClickEvent(null)
            .withHoverEvent(new HoverEvent.ShowText(hoverComponent))
    );

    this.visitChildren(link);

    this.context.setCurrentStyle(this.context.getCurrentStyle()
            .withColor(currentColor)
            .withClickEvent(null)
            .withHoverEvent(null)
    );
}
```

Then wrap `visit(Link)` so any custom-renderer failure or malformed HTTP link goes through that fallback instead of escaping out of prerender:

```java
@Override
public void visit(Link link) {
    try {
        for (var renderer : this.context.getLinkRenderers()) {
            if (renderer.visit(link, this::visitChildren, this.context)) {
                return;
            }
        }

        var currentColor = this.context.getCurrentStyle().getColor();
        var hoverComponent = Component.translatable(Gui.HOVER_HTTP_LINK, link.getDestination());
        this.context.setCurrentStyle(this.context.getCurrentStyle()
                .withColor(currentColor == null ? this.context.getLinkColor() : currentColor)
                .withClickEvent(new ClickEvent.OpenUrl(URI.create(link.getDestination())))
                .withHoverEvent(new HoverEvent.ShowText(hoverComponent))
        );

        this.visitChildren(link);

        this.context.setCurrentStyle(this.context.getCurrentStyle()
                .withColor(currentColor)
                .withClickEvent(null)
                .withHoverEvent(null)
        );
    } catch (Exception e) {
        this.renderBrokenLink(link, e);
    }
}
```

- [ ] **Step 2: Remove the special-case local catch from `BookLinkRenderer`**

Replace the whole `try/catch` block in `BookLinkRenderer.visit(...)` with the direct rendering path only. The method should simply parse the link and let exceptions bubble:

```java
var bookLink = BookLink.from(context.getBook(), link.getDestination());
var book = BookDataManager.get().getBook(bookLink.bookId);
var goToText = Component.translatable(book.getName());
if (bookLink.categoryId != null) {
    var category = book.getCategory(bookLink.categoryId);
    goToText = Component.translatable(category.getName());
}
if (bookLink.entryId != null) {
    var entry = book.getEntry(bookLink.entryId);
    goToText = Component.translatable(entry.getName());
}

var hoverComponent = Component.translatable(Gui.HOVER_BOOK_LINK, goToText);
context.setCurrentStyle(context.getCurrentStyle()
        .withColor(currentColor == null ? context.getLinkColor() : currentColor)
        .withClickEvent(new ClickEvent.OpenFile(link.getDestination()))
        .withHoverEvent(new HoverEvent.ShowText(hoverComponent))
);

visitChildren.accept(link);

context.setCurrentStyle(context.getCurrentStyle()
        .withColor(currentColor)
        .withClickEvent(null)
        .withHoverEvent(null)
);
```

Delete the existing `catch (Exception e) { ... }` block entirely.

- [ ] **Step 3: Let item-link parse failures flow into the same fallback path**

In `ItemLinkRenderer.visit(...)`, replace the local `try/catch` block with direct parsing so malformed item links do not create a half-valid clickable link:

```java
var itemId = link.getDestination().substring(PROTOCOL_ITEM_LENGTH);
var reader = new StringReader(itemId);
var itemResult = itemParser.parse(reader);
var itemInput = new ItemInput(itemResult.item(), itemResult.components());
var itemStack = itemInput.createItemStack(1);
```

Keep the rest of the rendering logic the same. If parsing throws, `CoreComponentNodeRenderer.visit(Link)` now converts it to a broken link.

- [ ] **Step 4: Add a generic broken-link hover translation key**

In `ModonomiconConstants`, add:

```java
public static final String HOVER_LINK_ERROR = PREFIX + "hover.link.error";
```

In `EnUsProvider`, add:

```java
this.add(Gui.HOVER_LINK_ERROR, "Invalid link: %s. Please contact the author of the book or the translator to fix this. More information can be found in the log.");
```

Then regenerate generated lang outputs:

Run: `./gradlew.bat :neo:runClientData`

Expected: generated `en_us.json` files include `modonomicon.gui.hover.link.error`

- [ ] **Step 5: Compile the markdown renderer changes**

Run: `./gradlew.bat :common:compileJava :neo:compileJava`

Expected: `BUILD SUCCESSFUL`

- [ ] **Step 6: Commit the link-fallback pass**

```bash
git add common/src/main/java/com/klikli_dev/modonomicon/client/gui/book/markdown/CoreComponentNodeRenderer.java common/src/main/java/com/klikli_dev/modonomicon/client/gui/book/markdown/BookLinkRenderer.java common/src/main/java/com/klikli_dev/modonomicon/client/gui/book/markdown/ItemLinkRenderer.java common/src/main/java/com/klikli_dev/modonomicon/api/ModonomiconConstants.java common/src/main/java/com/klikli_dev/modonomicon/datagen/EnUsProvider.java fabric/src/generated/resources/assets/modonomicon/lang/en_us.json neo/src/generated/resources/assets/modonomicon/lang/en_us.json
git commit -m "fix: render invalid markdown links safely"
```

## Task 3: Make prerendered text holders serialize back to original source data

**Files:**
- Modify: `common/src/main/java/com/klikli_dev/modonomicon/book/BookTextHolder.java`
- Modify: `common/src/main/java/com/klikli_dev/modonomicon/book/RenderedBookTextHolder.java`

- [ ] **Step 1: Refactor `BookTextHolder` codec serialization through an overridable method**

Change the codec definition from directly reading private fields to calling a helper:

```java
public static final Codec<BookTextHolder> CODEC = Codec.either(Codec.STRING, ComponentSerialization.CODEC)
        .xmap(
                value -> value.map(BookTextHolder::new, BookTextHolder::new),
                BookTextHolder::toSerializableValue
        );
```

Add this helper method to `BookTextHolder`:

```java
protected Either<String, Component> toSerializableValue() {
    return this.hasComponent() ? Either.right(this.component) : Either.left(this.string);
}
```

Do not change `getString()`; that method is UI-facing and localized. The codec must keep using the original source string/component.

- [ ] **Step 2: Override serialization in `RenderedBookTextHolder`**

Add constructor guards and override the serializable value to always use the original holder:

```java
public RenderedBookTextHolder(BookTextHolder original, List<MutableComponent> renderedText) {
    if (original == null) {
        throw new IllegalArgumentException("original cannot be null");
    }
    if (renderedText == null) {
        throw new IllegalArgumentException("renderedText cannot be null");
    }
    this.original = original;
    this.renderedText = renderedText;
}

@Override
protected Either<String, Component> toSerializableValue() {
    return this.original.toSerializableValue();
}
```

This is the key multiplayer fix: `RenderedBookTextHolder` can stay in prerendered model fields for UI rendering, but `BookTextHolder.CODEC` and `STREAM_CODEC` now encode the original non-null source payload instead of a null subclass field.

- [ ] **Step 3: Compile after the codec-safety change**

Run: `./gradlew.bat :common:compileJava :neo:compileJava`

Expected: `BUILD SUCCESSFUL`

- [ ] **Step 4: Commit the codec-safety pass**

```bash
git add common/src/main/java/com/klikli_dev/modonomicon/book/BookTextHolder.java common/src/main/java/com/klikli_dev/modonomicon/book/RenderedBookTextHolder.java
git commit -m "fix: serialize rendered book text safely"
```

## Task 4: Verify strict and lenient behavior end to end

**Files:**
- Use for validation: `common/src/main/java/com/klikli_dev/modonomicon/datagen/book/demo/formatting/LinkFormattingEntry.java`

- [ ] **Step 1: Regenerate demo data if the current `invalid_link` page changed during implementation**

Only do this if you touched demo datagen or localization content:

Run: `./gradlew.bat :neo:runClientData`

Expected: generated demo JSON and language files are up to date.

- [ ] **Step 2: Run the compile validation pass**

Run: `./gradlew.bat :common:compileJava :neo:compileJava`

Expected: `BUILD SUCCESSFUL`

- [ ] **Step 3: Verify strict mode manually**

Temporarily set the affected demo book to `allow_open_book_with_invalid_links = false` in its generated or source JSON, then run:

Run: `./gradlew.bat :neo:runClient`

Expected:
- the book still builds and the game stays running
- opening the demo book goes to `BookErrorScreen`
- the log contains the recorded invalid-link error
- no `sync_book_data` encoder exception appears

- [ ] **Step 4: Verify lenient mode manually**

Set the same book back to `allow_open_book_with_invalid_links = true`, then rerun:

Run: `./gradlew.bat :neo:runClient`

Expected:
- the book opens normally
- the page with `invalid_link` still renders
- the broken link is red / error-styled and has no click action
- the rest of the page remains readable
- no `sync_book_data` encoder exception appears

- [ ] **Step 5: Commit any final validation-only asset refresh**

If validation required only generated lang/data refreshes, commit them separately:

```bash
git add fabric/src/generated/resources/assets/modonomicon/lang/en_us.json neo/src/generated/resources/assets/modonomicon/lang/en_us.json
git commit -m "chore: refresh generated assets for link parser safety"
```

If no additional generated files changed, skip this commit.

## Self-review checklist

- Spec coverage: strict mode error screen, lenient broken-link rendering, codec safety, and multiplayer sync verification are all covered by Tasks 1-4.
- Placeholder scan: no TBD/TODO placeholders remain.
- Type consistency: the plan consistently uses `blocksOpening`, `hasBlockingErrors`, and `toSerializableValue` across all tasks.
