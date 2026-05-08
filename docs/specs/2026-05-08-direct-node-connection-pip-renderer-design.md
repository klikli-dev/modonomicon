<!--
SPDX-FileCopyrightText: 2026 klikli-dev

SPDX-License-Identifier: MIT
-->

# Direct node connection PIP renderer design

## Summary

Add a second category-node connection rendering path that draws direct animated lines between node centers through the GUI picture-in-picture pipeline. Keep the existing sprite-based routed connection renderer unchanged for later user choice, but temporarily switch `BookCategoryNodeScreen` to the new renderer.

## Goals

- Add a sibling class to `EntryConnectionRenderer` for direct node-to-node connections.
- Render the effect through modern GUI rendering infrastructure backed by a `MultiBufferSource.BufferSource`.
- Preserve the visual behavior of the legacy effect: animation over time, tapered shape, fade along the path, and optional wiggle.
- Reuse the existing picture-in-picture registration pattern already used by the multiblock GUI renderer.
- Keep node scrolling, zooming, clipping, icons, tooltips, and unread indicators working as they do now.

## Non-goals

- Do not remove or rewrite the existing `EntryConnectionRenderer`.
- Do not add the user-facing toggle yet.
- Do not preserve the current routed corner/arrow sprite layout in the temporary switch.
- Do not change entry hit testing, category background rendering, or frame rendering.

## Decision

### Chosen approach

Use one picture-in-picture render state for the full visible connection layer of a category node screen.

`BookCategoryNodeScreen` will gather all visible parent-child connections, convert them to final screen-space line endpoints, and queue one custom PIP state. The PIP renderer will then draw all lines into the target texture with a shared `MultiBufferSource.BufferSource`.

### Rejected alternatives

1. **Direct GUI-pass rendering without PIP**
   - Simpler at first glance, but it would depend more heavily on GUI extraction internals and would be harder to keep aligned with clipping and draw ordering.

2. **One PIP state per connection**
   - Works mechanically, but creates unnecessary state churn and makes ordering and clipping harder to reason about.

3. **Modern line primitives with wide-line state**
   - Wide-line behavior is not reliable enough across modern drivers. A generated translucent ribbon is a more stable way to preserve thickness and taper.

## Architecture

### 1. `DirectEntryConnectionRenderer`

Location: `common/.../client/gui/book/entry/DirectEntryConnectionRenderer.java`

Responsibilities:

- Act as the sibling entry-connection renderer used by `BookCategoryNodeScreen`.
- Walk category entries and collect only connections that should currently be visible.
- Skip hidden parent entries and parents with disabled lines.
- Compute final screen-space positions for the center point of each visible node.
- Convert those positions into coordinates local to the inner-frame picture-in-picture target.
- Queue one `GuiDirectEntryConnectionRenderState` on `guiGraphics.guiRenderState`.

This class should not perform low-level vertex emission. Its job is state preparation only.

### 2. `GuiDirectEntryConnectionRenderState`

Location: `common/.../client/render/state/pip/GuiDirectEntryConnectionRenderState.java`

Responsibilities:

- Implement `PictureInPictureRenderState`.
- Store the target rectangle (`x0`, `y0`, `x1`, `y1`), computed bounds, and current scissor area.
- Store the animation time for the current frame.
- Store a collection of immutable connection draw records, each containing:
  - local start point
  - local end point
  - base color
  - wiggle flag

The state should contain only render-ready data so the PIP renderer does not need to query book or GUI objects.

### 3. `GuiDirectEntryConnectionRenderer`

Location: `common/.../client/render/pip/GuiDirectEntryConnectionRenderer.java`

Responsibilities:

- Extend `PictureInPictureRenderer<GuiDirectEntryConnectionRenderState>`.
- Render all queued connections into the PIP texture in 2D local target space.
- Use the provided `MultiBufferSource.BufferSource`.
- Build each connection as a translucent ribbon instead of relying on wide-line GL state.
- Approximate the legacy look with these rules:
  - split the path into evenly spaced samples based on distance
  - apply animated offset per sample when wiggle is enabled
  - reduce width toward the target to create taper
  - fade color and alpha along the path

The renderer should treat the connection layer as GUI content, not world content.

## Rendering model

### Coordinate flow

1. `BookCategoryNodeScreen` already knows scroll, zoom, and the inner frame bounds.
2. `DirectEntryConnectionRenderer` will compute each node center in category content space.
3. It will apply the same offset and zoom used by the node screen to get final screen-space positions.
4. It will subtract the inner-frame origin so the render state stores local coordinates inside the PIP target.
5. The PIP renderer will draw directly in that local 2D space.

This avoids duplicating node-screen transform logic inside the PIP renderer.

### Draw ordering

Queue the connection PIP state before drawing entry backgrounds and icons so nodes remain visually readable above the animated lines.

This is a deliberate difference from the current segmented sprite pass and is acceptable for the temporary switch because the new renderer uses direct center-to-center lines instead of routed overlays.

### Clipping

Use the existing inner-frame scissor from `guiGraphics.scissorStack.peek()` and the same inner-frame rectangle already used by `BookCategoryNodeScreen`.

The connection layer must never render outside the inner frame.

## Screen integration

`BookCategoryNodeScreen` changes:

- Replace the `EntryConnectionRenderer` field with `DirectEntryConnectionRenderer` for the temporary switch.
- Replace the per-entry `renderConnections(...)` pass with one connection-layer render call per frame.
- Keep entry rendering, hover logic, tooltips, scroll, and zoom behavior unchanged.

The old sprite renderer stays available in code for the future setting.

## Registration

Register the new PIP renderer anywhere the existing GUI multiblock PIP renderer is registered:

- Fabric: `ModonomiconFabricClient`
- Neo: `ModonomiconNeo.Client#onRegisterPipRenderers`
- Forge: `ModonomiconForge.Client#onRegisterPipRenderers`

This keeps loader behavior aligned and avoids common-code features depending on loader-specific registration gaps.

## Animation and styling

### Time source

Use the existing client tick timing utilities for smooth animation, with partial ticks included for interpolation.

### Connection colors

Start with a single default connection color in code for the temporary switch. The color should be easy to extract into configuration or theme data later.

### Wiggle behavior

Support a per-connection wiggle flag in the render state even if the initial screen integration enables it uniformly. This keeps the renderer compatible with later rule-based styling.

## Error handling

- If the inner bounds are invalid or empty, do not queue a render state.
- If there are no visible connections, do not queue a render state.
- If a connection collapses to a near-zero length after transforms, skip it.
- Keep the renderer stateless apart from the provided buffer source so failed frames do not poison later renders.

## Testing and verification

### Functional checks

- Open a category with visible parent-child relationships and confirm direct animated lines render.
- Confirm the layer scrolls and zooms with the category content.
- Confirm clipping holds at the inner frame edges.
- Confirm hidden entries do not contribute lines.
- Confirm the screen still renders correctly on Fabric and Neo registration paths, with Forge registration kept in sync.

### Technical checks

- Run `./gradlew.bat compileJava` from repo root.
- Verify imports remain simple names only.
- Keep changes limited to the new renderer path, state class, loader registrations, and the temporary screen switch.

## Implementation notes

- Preserve the existing `EntryConnectionRenderer` class unchanged except for any import cleanup required by unrelated compile errors.
- Prefer immutable records for render payload data.
- Keep all new names neutral so the later user-facing option can describe the mode without renaming internal classes again.
