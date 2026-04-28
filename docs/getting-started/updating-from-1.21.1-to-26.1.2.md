---
sidebar_position: 6
title: Updating from 1.21.1 to 26.1.2
---

# Updating from 1.21.1 to 26.1.2

This update changes how book styling is defined.

## Summary

If your 1.21.1 book used custom textures, colors, layout offsets, or other book styling fields, you will need to migrate that styling into a separate `theme.json` file and update how the textures are organised.

The main change is:

- `book.json` now focuses on book content and structure.
- `theme.json` now contains book appearance, layout, sprites, and palette data.
- Instead of the old manual texture atlases (`book_overview.png`, `book_content.png`, ...) each sprite now lives in its own file under `resources/assets/<modid>/textures/gui/sprites/modonomicon/themes/<themeid>/.../`. Minecraft then automatically assembles an atlas. This allows more flexible override of modonomicon textures and fully supports the `.mcmeta` format for e.g. texture tile, nine_slice or stretch instructions.

See also:

- [Book.json](../basics/structure/book)
- [Theme.json](../basics/structure/theme)

## What changed

### `theme.json` is now required for custom styling

Book appearance data is no longer stored inline in `book.json`.

This includes old styling values such as:

- frame and content textures
- crafting and recipe textures
- frame overlays
- text and title colors
- text and button offsets
- category icon scale
- single page texture overrides

If you had a styled book on 1.21.1, that styling now belongs in `theme.json`.

### Datagen API changed

If you generate books in code, the datagen API changed as well.

The old `BookModel` styling setters were replaced by a theme-based setup. Use a theme model and attach it with `withTheme(...)` instead of configuring textures and palette values directly on the book model.

### Theme system and registry were added

Themes are now backed by a dedicated runtime theme system.

For normal custom themes, use your own theme `id` together with the default theme `type` and place your files into the correct theme folder.
Only use a custom theme type if you also register a custom Java theme implementation.

### The old atlases were split into individual sprites

The old manual atlases such as `book_overview.png`, `book_content.png`, and similar shared GUI sheets were split into many individual sprite files under the theme folder.
Minecraft now assembles the atlas automatically from those sprite files.

This makes it easier to override only the parts you care about, and it also adds proper `.mcmeta` support for things like `tile`, `nine_slice`, and `stretch`.

If you previously maintained custom versions of the old manual atlases, see the automation section below for help splitting and converting them.

### Partial theme overrides now work well

You can override only the individual files you want to change.
If a theme file is missing, Modonomicon falls back to the default built-in theme asset for that file.

### Regenerate generated data

After migrating, rerun datagen so the generated book resources include the new theme output.

## How to update

### Datapack / JSON users

1. Keep your book structure in `book.json`.
2. Move old visual styling into a new `theme.json` next to the book data.
3. Set the correct theme `id` and usually keep `type` as `modonomicon:default`.
4. Place any custom theme textures into the matching theme asset folder.
5. Test the book in game and confirm buttons, backgrounds, node connectors, and recipe visuals render correctly.

### Datagen users

1. Remove old inline styling calls from your `BookModel` setup.
2. Create a theme model.
3. Attach it to the book with `withTheme(...)`.
4. Run datagen again.

## Need help converting old custom styling?

If you had a heavily customized 1.21.1 book, the conversion can be tedious.

Join the Discord and ask about the script that can automate conversion of old custom styling data:

**https://dsc.gg/klikli**

## Recommended checks after migrating

- open the book in game
- verify the overview screen styling
- verify entry backgrounds and node connections
- verify recipe page backgrounds
- verify frame overlays and text colors
- rerun datagen if generated resources are missing