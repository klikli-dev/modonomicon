<!--
SPDX-FileCopyrightText: 2026 klikli-dev

SPDX-License-Identifier: MIT
-->

# Modonomicon book theme migration plan

## Goal

Move Modonomicon GUI rendering from hand-made atlas textures with hard-coded UV regions to named individual sprite textures plus a `BookTheme` abstraction.

The migration should support:

- a Modonomicon default theme,
- mod-specific theme implementations,
- a Python extraction script that cuts sprites out of the currently configured atlas textures,
- generated Java theme source that records sprite identifiers and dimensions.

For **v1 runtime hookup**, only the default theme is wired. The script may still generate per-book theme classes, but `Book#theme()` will initially return the default theme until a later configuration step is added.

## Scope

This plan covers:

1. how to discover the currently used sprites,
2. how to implement the extraction script,
3. how to introduce the runtime theme API,
4. how to migrate existing renderers incrementally,
5. how to validate the result.

This plan does **not** define the final book-side theme configuration mechanism yet. For now, `Book#theme()` should statically return the default theme.

## Current texture families

The current book JSON config provides these texture entry points:

- `frame_texture`
- `top_frame_overlay.texture`
- `bottom_frame_overlay.texture`
- `left_frame_overlay.texture`
- `right_frame_overlay.texture`
- `crafting_texture`
- `single_page_texture`

The default themed content/overview atlases are extracted from built-in Modonomicon textures rather than per-book JSON fields.

The primary migration target requested here is the content / overview / frame family, while recipe and single-page assets should be planned as part of the same theme system so the API does not need to be reworked again immediately.

## How to get the sprite list

The sprite list should be built from the **call chains that end in rendering the configured book textures**, not by manually inspecting PNGs.

That gives a reviewable list of only the regions Modonomicon actually uses.

### 1. Content texture call chain

Primary source:

- built-in atlas `modonomicon:textures/gui/book_content.png`

Important direct bypasses:

- `BookEntrySinglePageScreen`
- `BookErrorScreen`
- any page renderer that blits the content texture directly

Current known hard-coded content UV users include:

- `BookContentRenderer.renderBookBackground(...)`
- `BookContentRenderer.drawTitleSeparator(...)`
- `BookContentRenderer.drawLock(...)`
- `BookContentRenderer.drawUnreadIndicator(...)`
- `BookButton` and button subclasses such as `ArrowButton`, `BackButton`, and `ExitButton`
- `BookSearchScreen` search field decoration/background
- navigation/button renderers that currently use content-atlas coordinates
- `BookImagePageRenderer`
- `BookEntityPageRenderer`
- `BookMultiblockPageRenderer`
- media/page renderers using content decorations or frames
- `BookErrorScreen`, including the current direct fallback to `modonomicon:textures/gui/book_content.png`

### 2. Overview texture call chain

Primary source:

- built-in atlas `modonomicon:textures/gui/book_overview.png`
- direct `GuiGraphicsExtractor.blit(...)` calls in overview button classes

Known overview users include:

- `CategoryButton`
- `SearchButton`
- `ShowBookmarksButton`
- `ShowRecentlyUnlockedButton`
- `AddBookmarkButton`
- `RemoveBookmarkButton`
- `ReadAllButton`

### 3. Frame texture call chain

Primary chain:

- `Book#getFrameTexture()`
- `BookParentNodeScreen.renderFrame(...)`
- `GuiGraphicsExt.blitWithBorder(...)`
- terminal `GuiGraphicsExtractor.blit(...)`

This path should be treated as a **nine-slice source definition**, not as one single sprite.

### 4. Frame overlay call chain

Primary chain:

- `Book#getTopFrameOverlay()` / bottom / left / right
- `BookParentNodeScreen.renderFrameOverlay(...)`
- `BookFrameOverlay.getFrameU()` / `getFrameV()`
- terminal `GuiGraphicsExtractor.blit(...)`

The overlay crop rectangle is derived from:

- `texture_width`
- `texture_height`
- `frame_width`
- `frame_height`

with the current centered calculation:

- `u = textureWidth / 2 - frameWidth / 2`
- `v = textureHeight / 2 - frameHeight / 2`

### 5. Manifest-building rule

For every terminal render path that uses one of the book texture families, record:

- logical sprite name,
- source texture family,
- source rectangle (`x`, `y`, `width`, `height`) or derived rectangle,
- state variant if applicable (`normal`, `hover`, `pressed`),
- source file and method,
- notes on semantics.

That manifest becomes the canonical source for the extraction script.

## Extraction manifest design

The Python script should use a checked-in extraction manifest instead of trying to parse Java source dynamically.

Recommended structure per entry:

- `key`: semantic name such as `content/title_separator`
- `source`: direct texture ids, `frame_texture`, `top_frame_overlay`, etc.
- `rect`: `x`, `y`, `width`, `height`
- `variants`: optional map for hover / pressed variants
- `kind`: `sprite`, `button_states`, or `nine_slice`
- `notes`: render-site summary

For frame rendering, use a special manifest entry of kind `nine_slice` with the current border values consumed by `GuiGraphicsExt.blitWithBorder(...)`.

For v1, the nine-slice manifest should describe **one extracted frame texture plus border metadata**, not nine separate PNG files. That matches the proposed `GuiNineSlice` runtime model and avoids introducing a second frame renderer design immediately.

## Script implementation plan

## 1. Script location

Add a script under a stable tooling path, for example:

- `tools/extract_modonomicon_theme.py`

## 2. CLI contract

Command shape:

```bash
python tools/extract_modonomicon_theme.py <repo_root> <book_id>
```

Inputs:

- `repo_root`: root of the target mod project,
- `book_id`: fully qualified ID such as `modonomicon:demo`.

Recommended optional flags:

- `--class-name`
- `--package`
- `--resource-root`
- `--dry-run`

## 3. Book JSON lookup

For a book ID `namespace:path`, the script should look for:

- `data/<namespace>/modonomicon/books/<path>/book.json`

Search roots in this order:

- `src/main/resources`
- `src/generated/resources`
- `common/src/main/resources`
- `common/src/generated/resources`
- loader-specific generated roots if present

Behavior:

- fail if no match is found,
- fail if multiple ambiguous matches are found,
- print the resolved book JSON path.

## 4. Resolve source textures

Read `book.json` and resolve these values into PNG file paths:

- `frame_texture`
- overlay textures from the frame overlay entries
- `crafting_texture`
- `single_page_texture`

The built-in theme atlases `modonomicon:textures/gui/book_content.png` and `modonomicon:textures/gui/book_overview.png` are resolved directly from the extraction manifest.

Resource resolution rule:

- `namespace:textures/gui/book_content.png`
  becomes
- `assets/<namespace>/textures/gui/book_content.png`

Search for the asset inside the target repo resource roots.

## 5. Decide output resource root

Use the **book namespace** as the target asset namespace, as requested.

Preferred output location:

- `<resource_root>/assets/<book_namespace>/textures/gui/sprites/modonomicon/...`

Recommended default behavior:

- derive the output resource root from the resolved `book.json` source set,
- use that same module/source set for generated assets,
- allow explicit override when needed.

For this repository, that usually means `common/src/main/resources` or `common/src/generated/resources`, not plain `src/main/resources`.

## 6. Extract sprites

Use Pillow to:

- open each resolved source PNG,
- crop each rectangle from the manifest,
- save each extracted sprite as its own PNG,
- preserve pixel-perfect size,
- fail on out-of-bounds rectangles.

### Naming recommendation

Include the book path to avoid collisions:

- `assets/<modid>/textures/gui/sprites/modonomicon/<book_path>/content/title_separator.png`
- `assets/<modid>/textures/gui/sprites/modonomicon/<book_path>/overview/search_button.png`
- `assets/<modid>/textures/gui/sprites/modonomicon/<book_path>/frame/top_overlay.png`

## 7. Frame handling in the script

### 7.1 Nine-slice frame extraction

Extract one frame texture asset plus border metadata:

- the extracted frame PNG,
- full frame texture width and height,
- left / right / top / bottom border sizes.

The border metadata should come from the current `blitWithBorder(...)` call configuration used by `BookParentNodeScreen.renderFrame(...)`.

### 7.2 Overlay extraction

For each of:

- top
- bottom
- left
- right

compute the centered crop rectangle from `BookFrameOverlay` values and extract it as an independent sprite.

The generated theme data must also preserve placement metadata currently carried by `BookFrameOverlay`:

- `frame_x_offset`
- `frame_y_offset`

Without those offsets, extracted overlay sprites cannot be positioned the same way as today.

## 8. Generate Java source

The script should generate a Java class that records the resulting sprite identifiers and dimensions.

Recommended output path shape:

- `<java_source_root>/<package path>/Generated<BookName>Theme.java`

The Java source root should be derived from the resolved book source set, mirroring the chosen resource source set. In this repository that will usually be `common/src/main/java`.

Recommended generated contents:

- `GuiSprite` constants for each extracted sprite,
- `GuiButtonSprites` constants for grouped button states,
- `GuiNineSlice` constant for the frame,
- implementation of the `BookTheme` methods,
- no dynamic logic beyond returning constants.

Recommended style:

- generate a concrete implementation,
- keep behavior trivial,
- keep naming semantic and deterministic.

## Runtime API implementation plan

The runtime API should be added **before** the extraction script starts generating Java so the generated code has stable target types.

## 1. Add low-level sprite value types

Add small immutable runtime types.

### `GuiSprite`

Fields:

- `Identifier texture`
- `int width`
- `int height`

### `GuiButtonSprites`

Fields:

- `GuiSprite normal`
- `GuiSprite hover`
- optional `GuiSprite pressed`

### `GuiNineSlice`

Fields:

- `Identifier texture`
- `int width`
- `int height`
- `int left`
- `int right`
- `int top`
- `int bottom`

This model intentionally represents one texture plus border metadata, matching the v1 extraction plan.

### `GuiFrameOverlay`

Fields:

- `GuiSprite sprite`
- `int frameXOffset`
- `int frameYOffset`

This preserves the placement information currently carried by `BookFrameOverlay` after sprite extraction.

## 2. Add the theme contract

Create a top-level `BookTheme` interface.

Recommended structure:

- `BookContentTheme content()`
- `BookOverviewTheme overview()`
- `BookFrameTheme frame()`
- `BookRecipeTheme recipes()`
- `BookLayoutTheme layout()`
- `BookPaletteTheme palette()`

### `BookContentTheme`

Expose at least:

- `GuiSprite doublePageBackground()`
- `GuiSprite singlePageBackground()`
- `GuiSprite titleSeparator()`
- `GuiSprite lockIcon()`
- `GuiButtonSprites unreadIndicator()`
- `GuiButtonSprites nextPageButton()`
- `GuiButtonSprites previousPageButton()`
- `GuiButtonSprites smallNextPageButton()`
- `GuiButtonSprites smallPreviousPageButton()`
- `GuiButtonSprites backButton()`
- `GuiButtonSprites exitButton()`
- `GuiButtonSprites visualizeButton()`
- any content decorations used by page renderers

### `BookOverviewTheme`

Expose at least:

- `GuiButtonSprites categoryButton()`
- `GuiButtonSprites searchButton()`
- `GuiButtonSprites showBookmarksButton()`
- `GuiButtonSprites showRecentlyUnlockedButton()`
- `GuiButtonSprites addBookmarkButton()`
- `GuiButtonSprites removeBookmarkButton()`
- `GuiButtonSprites readAllButton()`
- `GuiButtonSprites readNoneButton()`
- `GuiButtonSprites readUnlockedButton()`

### `BookFrameTheme`

Expose at least:

- `GuiNineSlice frame()`
- `GuiFrameOverlay topOverlay()`
- `GuiFrameOverlay bottomOverlay()`
- `GuiFrameOverlay leftOverlay()`
- `GuiFrameOverlay rightOverlay()`

### `BookRecipeTheme`

Expose at least:

- `GuiSprite craftingGrid()`
- `GuiSprite craftingArrow()`
- `GuiSprite shapelessIcon()`
- `GuiSprite processingRecipeBackground()`
- `GuiSprite smithingRecipeBackground()`
- `GuiSprite spotlightSlot()`

### `BookLayoutTheme`

Move current visual offsets here:

- `bookTextOffsetX()`
- `bookTextOffsetY()`
- `bookTextOffsetWidth()`
- `bookTextOffsetHeight()`
- `categoryButtonXOffset()`
- `categoryButtonYOffset()`
- `searchButtonXOffset()`
- `searchButtonYOffset()`
- `readAllButtonYOffset()`
- `categoryButtonIconScale()`

For v1, broader screen/page dimensions and anchors that are currently hard-coded should stay fixed unless a concrete themed need appears during migration. In particular, dimensions in classes such as `BookPaginatedScreen`, `BookEntrySinglePageScreen`, `BookEntryScreen`, and `BookParentNodeScreen` remain non-theme data in the first iteration.

### `BookPaletteTheme`

Expose:

- `defaultTitleColor()`
- `defaultTextColor()`

## 3. Add default theme implementation

Add a handwritten default implementation first:

- `DefaultBookTheme`

Initial behavior:

- return constants from a stub class,
- later replace the stub constants with script-generated values.

Recommended long-term shape:

- handwritten `DefaultBookTheme`
- generated `GeneratedDefaultBookThemeData` or similar constant holder

This keeps generated code as data, not behavior.

## 4. Add `Book#theme()`

Add a new method:

- `public BookTheme theme()`

For the first iteration it should simply return:

- `DefaultBookTheme.INSTANCE`

That satisfies the immediate requirement and decouples the rest of the migration from theme configuration.

## Renderer migration plan

The migration should be incremental.

## 1. Migrate shared helpers first

Start with the most central rendering helpers:

- `BookContentRenderer`
- frame rendering path in `BookParentNodeScreen`
- `GuiGraphicsExt` frame helper if necessary

New helper methods should render `GuiSprite` or `GuiNineSlice` rather than raw atlas UVs.

## 2. Migrate content and overview buttons

Update button classes so they stop storing atlas coordinates and instead ask the theme for semantic sprites.

Priority targets:

- overview buttons
- navigation buttons
- bookmark buttons
- search buttons

## 3. Migrate content/page screens

Update:

- `BookEntrySinglePageScreen`
- `BookErrorScreen`
- index/search/recent/bookmark screens that consume shared content helpers

## 4. Migrate page renderers

Update recipe and media renderers to consume `BookTheme` assets.

## 5. Remove atlas assumptions last

Only after all renderers are migrated should the code remove or deprecate assumptions such as:

- fixed `512x256` content texture sizes,
- fixed `256x256` overview texture sizes,
- fixed hard-coded UVs in button classes.

## Suggested implementation order

1. Create the extraction manifest.
2. Add `GuiSprite`, `GuiButtonSprites`, `GuiNineSlice`, and `GuiFrameOverlay`.
3. Add `BookTheme` and subinterfaces.
4. Add handwritten `DefaultBookTheme` stub.
5. Add `Book#theme()` returning the default theme.
6. Implement the Python script against the new runtime types and verify extraction output.
7. Generate the default theme data/class.
8. Migrate `BookContentRenderer`.
9. Migrate frame rendering.
10. Migrate overview and navigation buttons.
11. Migrate screens and page renderers.
12. Replace the default theme stub constants with script-generated constants.
13. Remove or deprecate direct atlas usage.

## Validation plan

## 1. Script validation

The script should fail loudly on:

- missing `book.json`,
- ambiguous `book.json` matches,
- missing source textures,
- crop rectangles outside image bounds,
- duplicate output sprite names,
- invalid generated package or class names.

It should also print a summary containing:

- resolved book path,
- resolved source textures,
- number of extracted sprites,
- generated Java file path,
- output resource root.

## 2. Runtime validation

After the code migration:

- run `./gradlew.bat compileJava`
- run the client and open a themed book
- verify:
  - content background,
  - title separator,
  - lock icon,
  - unread indicator,
  - overview buttons,
  - frame nine-slice,
  - frame overlays,
  - single-page background,
  - recipe/media renderers.

## Risks and notes

### 1. Not all sprite uses are centralized today

Some atlas regions are defined in local button/page classes. That is why the manifest must be built from the render call chains and terminal blits before extraction starts.

### 2. Frame rendering is a special case

The frame path is not a normal atlas icon lookup. It is a nine-slice source definition plus separately centered overlays.

### 3. Keep the first iteration deterministic

Do not attempt automatic Java parsing in v1. A manually curated, code-reviewed manifest derived from the discovered call chains is simpler and safer.

### 4. Keep generated code simple

The generated Java should behave like recorded data. Avoid emitting custom rendering logic from the script.

## Deliverables

1. A checked-in extraction manifest covering all current content / overview / frame render uses.
2. A Python script that resolves a target book and extracts sprite PNGs.
3. Generated Java theme source for the extracted sprites.
4. `BookTheme` runtime API and supporting value types.
5. `Book#theme()` returning a default theme.
6. Incremental renderer migration from atlas UVs to named theme sprites.
