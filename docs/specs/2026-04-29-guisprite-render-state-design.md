/*
 * SPDX-FileCopyrightText: 2026 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

# GuiSprite render-state migration design

## Goal

Migrate all `GuiSprite` rendering away from raw texture `blit(...)` calls that pass `guiSprite.sprite()` directly.
`GuiSprite` should own GUI sprite rendering through `extractRenderState(...)`, modeled after `code-defined-gui`.

## Scope

In scope:
- Add render extraction helpers to `GuiSprite`
- Add `tinted(int)` and `sized(int, int)` convenience variants to `GuiSprite`
- Switch all `GuiSprite` rendering call sites to delegate through `extractRenderState(...)`
- Update default theme sprite identifiers to atlas sprite ids suitable for `blitSprite(...)`
- Keep theme override fallback behavior for custom themes
- Update default entry background sprite identifiers to atlas sprite ids

Out of scope:
- Non-`GuiSprite` texture rendering such as page images, parallax backgrounds, generic texture helpers, and custom icon textures
- Broader theme/render architecture refactors

## Chosen approach

Use `GuiSprite` as render owner.

`GuiSprite` will expose:
- `sized(int width, int height)`
- `tinted(int tint)`
- `extractRenderState(GuiGraphicsExtractor guiGraphics, int x, int y)`
- `extractRenderState(GuiGraphicsExtractor guiGraphics, int x, int y, int width, int height)`

Both render methods will call `blitSprite(...)` internally.
This keeps sprite-id details localized and removes direct sprite blit setup from callers.

## Sprite identifier rules

`GuiSprite` identifiers must be atlas sprite ids, not raw texture file paths.

Example conversion:
- before: `modonomicon:textures/gui/sprites/modonomicon/themes/default/content/icons/book/lock_icon.png`
- after: `modonomicon:modonomicon/themes/default/content/icons/book/lock_icon`

Default theme lookup will still probe resource existence using the physical texture file path under `textures/gui/sprites/<sprite-path>.png`, but the returned identifier stored in `GuiSprite` remains the sprite id.

## Call-site migration

All current `GuiSprite` render call sites should move to one of these forms:
- `sprite.extractRenderState(guiGraphics, x, y)`
- `sprite.sized(width, height).extractRenderState(guiGraphics, x, y)`
- `sprite.tinted(color).extractRenderState(guiGraphics, x, y)`
- `sprite.sized(width, height).tinted(color).extractRenderState(guiGraphics, x, y)`

This applies to book content rendering, node rendering, frame overlays, buttons, and page renderers that currently render `GuiSprite` instances.

## Validation

- Search for remaining direct `blit(...)` calls that render `GuiSprite.sprite()`
- Run `./gradlew.bat compileJava`
- Fix any compile issues caused by render helper changes

## Risks and mitigations

- Risk: custom theme fallback breaks because resource probing needs texture file path, not sprite id
  - Mitigation: separate sprite-id construction from texture-file existence probing
- Risk: tinted and resized renders regress if overload coverage is incomplete
  - Mitigation: migrate all existing color and region render helpers to `GuiSprite` composition methods
