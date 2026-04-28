#!/usr/bin/env python3
# SPDX-FileCopyrightText: 2026 klikli-dev
#
# SPDX-License-Identifier: MIT

from __future__ import annotations

import argparse
import json
import keyword
import re
import sys
from pathlib import Path

from PIL import Image


BOOK_JSON_RELATIVE_PATTERN = "data/{namespace}/modonomicon/books/{path}/book.json"
MANIFEST_PATH = Path("tools/modonomicon_theme_manifest.json")
DEFAULT_PACKAGE = "com.klikli_dev.modonomicon.client.gui.book.theme"
DEFAULT_CLASS_NAME = "GeneratedBookThemeData"

DEFAULT_BOOK_SOURCES = {
    "single_page_texture": "modonomicon:textures/gui/single_page_entry.png",
    "frame_texture": "modonomicon:textures/gui/book_frame.png",
    "crafting_texture": "modonomicon:textures/gui/crafting_textures.png",
    "top_frame_overlay": {
        "texture": "modonomicon:textures/gui/book_frame_top_overlay.png",
        "texture_width": 256,
        "texture_height": 256,
        "frame_width": 72,
        "frame_height": 7,
        "frame_x_offset": 0,
        "frame_y_offset": 4,
    },
    "bottom_frame_overlay": {
        "texture": "modonomicon:textures/gui/book_frame_bottom_overlay.png",
        "texture_width": 256,
        "texture_height": 256,
        "frame_width": 72,
        "frame_height": 8,
        "frame_x_offset": 0,
        "frame_y_offset": -4,
    },
    "left_frame_overlay": {
        "texture": "modonomicon:textures/gui/book_frame_left_overlay.png",
        "texture_width": 256,
        "texture_height": 256,
        "frame_width": 7,
        "frame_height": 70,
        "frame_x_offset": 3,
        "frame_y_offset": 0,
    },
    "right_frame_overlay": {
        "texture": "modonomicon:textures/gui/book_frame_right_overlay.png",
        "texture_width": 256,
        "texture_height": 256,
        "frame_width": 8,
        "frame_height": 70,
        "frame_x_offset": -4,
        "frame_y_offset": 0,
    },
}

OUTPUT_PATHS = {
    "content/double_page_background": "backgrounds/book/double_page_background",
    "content/single_page_background": "backgrounds/book/single_page_background",
    "content/title_separator": "decorations/book/title_separator",
    "content/lock_icon": "icons/book/lock_icon",
    "content/unread_indicator": "indicators/book/unread_indicator",
    "content/next_page_button": "buttons/navigation/next_page_button",
    "content/previous_page_button": "buttons/navigation/previous_page_button",
    "content/small_next_page_button": "buttons/navigation/small_next_page_button",
    "content/small_previous_page_button": "buttons/navigation/small_previous_page_button",
    "content/back_button": "buttons/navigation/back_button",
    "content/exit_button": "buttons/navigation/exit_button",
    "content/visualize_button": "buttons/navigation/visualize_button",
    "content/category_scroll_up_button": "buttons/category/category_scroll_up_button",
    "content/category_scroll_down_button": "buttons/category/category_scroll_down_button",
    "content/search_field_background": "fields/search/background",
    "content/media_frame": "pages/media/frame",
    "overview/category_button": "buttons/category/category_button",
    "overview/search_button": "buttons/side/search_button",
    "overview/show_bookmarks_button": "buttons/side/show_bookmarks_button",
    "overview/show_recently_unlocked_button": "buttons/side/show_recently_unlocked_button",
    "overview/add_bookmark_button": "buttons/side/add_bookmark_button",
    "overview/remove_bookmark_button": "buttons/side/remove_bookmark_button",
    "overview/read_unlocked_button": "buttons/read/read_unlocked_button",
    "overview/read_all_button": "buttons/read/read_all_button",
    "overview/read_none_button": "buttons/read/read_none_button",
    "node/entry_background_0_0": "nodes/entry_backgrounds/entry_background_0_0",
    "node/entry_background_0_1": "nodes/entry_backgrounds/entry_background_0_1",
    "node/entry_background_0_2": "nodes/entry_backgrounds/entry_background_0_2",
    "node/entry_background_1_0": "nodes/entry_backgrounds/entry_background_1_0",
    "node/entry_background_1_1": "nodes/entry_backgrounds/entry_background_1_1",
    "node/small_curve_left_down": "nodes/connections/small_curve_left_down",
    "node/small_curve_right_down": "nodes/connections/small_curve_right_down",
    "node/small_curve_left_up": "nodes/connections/small_curve_left_up",
    "node/small_curve_right_up": "nodes/connections/small_curve_right_up",
    "node/large_curve_left_down": "nodes/connections/large_curve_left_down",
    "node/large_curve_right_down": "nodes/connections/large_curve_right_down",
    "node/large_curve_left_up": "nodes/connections/large_curve_left_up",
    "node/large_curve_right_up": "nodes/connections/large_curve_right_up",
    "node/vertical_line": "nodes/connections/vertical_line",
    "node/horizontal_line": "nodes/connections/horizontal_line",
    "node/up_arrow": "nodes/connections/up_arrow",
    "node/down_arrow": "nodes/connections/down_arrow",
    "node/right_arrow": "nodes/connections/right_arrow",
    "node/left_arrow": "nodes/connections/left_arrow",
    "frame/frame": "frame/frame",
    "frame/top_overlay": "frame/top_overlay",
    "frame/bottom_overlay": "frame/bottom_overlay",
    "frame/left_overlay": "frame/left_overlay",
    "frame/right_overlay": "frame/right_overlay",
    "recipes/crafting_grid": "pages/recipes/crafting_grid",
    "recipes/shapeless_icon": "pages/recipes/shapeless_icon",
    "recipes/processing_recipe_background": "pages/recipes/processing_recipe_background",
    "recipes/smithing_recipe_background": "pages/recipes/smithing_recipe_background",
    "recipes/spotlight_slot": "pages/recipes/spotlight_slot",
}


def parse_args() -> argparse.Namespace:
    parser = argparse.ArgumentParser(description="Extract Modonomicon theme sprites and generate Java theme data.")
    parser.add_argument("repo_root", help="Root of the target mod project.")
    parser.add_argument("book_id", help="Fully qualified book id, for example modonomicon:demo.")
    parser.add_argument("--class-name", default=DEFAULT_CLASS_NAME)
    parser.add_argument("--package", default=DEFAULT_PACKAGE)
    parser.add_argument("--resource-root", help="Resource root to write generated assets into.")
    parser.add_argument("--theme-path", help="Output theme path below sprites/modonomicon/themes. Defaults to the book path.")
    parser.add_argument("--book-json-path", help="Explicit book.json path to use when multiple matches exist.")
    parser.add_argument("--dry-run", action="store_true")
    return parser.parse_args()


def fail(message: str) -> None:
    print(f"ERROR: {message}", file=sys.stderr)
    raise SystemExit(1)


def validate_identifier(value: str) -> tuple[str, str]:
    if ":" not in value:
        fail(f"Book id '{value}' must be namespace:path.")

    namespace, path = value.split(":", 1)
    if not namespace or not path:
        fail(f"Book id '{value}' must be namespace:path.")

    return namespace, path


def resource_roots(repo_root: Path) -> list[Path]:
    candidates = [
        repo_root / "src/main/resources",
        repo_root / "src/generated/resources",
        repo_root / "common/src/main/resources",
        repo_root / "common/src/generated/resources",
        repo_root / "fabric/src/main/resources",
        repo_root / "fabric/src/generated/resources",
        repo_root / "forge/src/main/resources",
        repo_root / "forge/src/generated/resources",
        repo_root / "neo/src/main/resources",
        repo_root / "neo/src/generated/resources",
    ]
    return [candidate for candidate in candidates if candidate.exists()]


def resolve_book_json(repo_root: Path, namespace: str, path: str, override: str | None) -> Path:
    if override:
        resolved = (repo_root / override).resolve() if not Path(override).is_absolute() else Path(override).resolve()
        if not resolved.exists():
            fail(f"Book json override does not exist: {resolved}")
        return resolved

    relative = BOOK_JSON_RELATIVE_PATTERN.format(namespace=namespace, path=path)
    matches = [root / relative for root in resource_roots(repo_root) if (root / relative).exists()]
    if not matches:
        fail(f"No book.json found for {namespace}:{path}")

    unique_matches = sorted({match.resolve() for match in matches})
    if len(unique_matches) > 1:
        formatted = "\n".join(f" - {match}" for match in unique_matches)
        fail(f"Multiple book.json matches found for {namespace}:{path}:\n{formatted}\nUse --book-json-path to disambiguate.")

    return unique_matches[0]


def resolve_first_category_json(book_json_path: Path) -> Path:
    categories_dir = book_json_path.parent / "categories"
    if not categories_dir.exists() or not categories_dir.is_dir():
        fail(f"Missing categories directory next to book.json: {categories_dir}")

    matches = sorted(path.resolve() for path in categories_dir.glob("*.json") if path.is_file())
    if not matches:
        fail(f"No category json files found in {categories_dir}")

    return matches[0]


def read_json(path: Path) -> dict:
    with path.open("r", encoding="utf-8") as handle:
        return json.load(handle)


def resolve_resource_path(repo_root: Path, identifier: str) -> Path:
    namespace, path = validate_identifier(identifier)
    asset_relative = Path("assets") / namespace / path
    matches = [root / asset_relative for root in resource_roots(repo_root) if (root / asset_relative).exists()]
    if not matches:
        fail(f"Missing source texture for {identifier}")

    unique_matches = sorted({match.resolve() for match in matches})
    if len(unique_matches) > 1:
        formatted = "\n".join(f" - {match}" for match in unique_matches)
        fail(f"Multiple source texture matches found for {identifier}:\n{formatted}")

    return unique_matches[0]


def try_resolve_resource_path(repo_root: Path, identifier: str) -> Path | None:
    namespace, path = validate_identifier(identifier)
    asset_relative = Path("assets") / namespace / path
    matches = [root / asset_relative for root in resource_roots(repo_root) if (root / asset_relative).exists()]
    unique_matches = sorted({match.resolve() for match in matches})
    if len(unique_matches) == 1:
        return unique_matches[0]
    return None


def resolve_output_resource_root(repo_root: Path, book_json_path: Path, override: str | None) -> Path:
    if override:
        result = (repo_root / override).resolve() if not Path(override).is_absolute() else Path(override).resolve()
        if not result.exists():
            fail(f"Resource root does not exist: {result}")
        return result

    resource_path = book_json_path.resolve()
    parts = resource_path.parts
    for index in range(len(parts) - 2):
        if parts[index] == "src" and parts[index + 1] in {"main", "generated"} and parts[index + 2] == "resources":
            return Path(*parts[: index + 3])

    fail(f"Could not derive resource root from {book_json_path}")
    raise AssertionError


def resolve_java_source_root(resource_root: Path) -> Path:
    parts = list(resource_root.parts)
    if len(parts) < 3 or parts[-3:] not in (["src", "main", "resources"], ["src", "generated", "resources"]):
        fail(f"Cannot derive java source root from resource root {resource_root}")

    return Path(*parts[:-3], "src", parts[-2], "java")


def validate_package_name(value: str) -> None:
    if not re.fullmatch(r"[A-Za-z_]\w*(\.[A-Za-z_]\w*)*", value):
        fail(f"Invalid package name: {value}")
    for segment in value.split("."):
        if keyword.iskeyword(segment):
            fail(f"Invalid package name segment: {segment}")


def validate_class_name(value: str) -> None:
    if not re.fullmatch(r"[A-Za-z_]\w*", value) or keyword.iskeyword(value):
        fail(f"Invalid class name: {value}")


def logical_output_base(key: str) -> str:
    return OUTPUT_PATHS.get(key, key)


def sprite_relative_path(key: str, suffix: str | None = None) -> str:
    base = Path(logical_output_base(key))
    if suffix:
        return (base.parent / f"{base.name}_{suffix}.png").as_posix()
    return base.with_suffix(".png").as_posix()


def sprite_output_relative(theme_path: str, key: str, suffix: str | None = None) -> Path:
    return Path("textures/gui/sprites/modonomicon/themes") / theme_path / sprite_relative_path(key, suffix)


def write_image(path: Path, image: Image.Image, dry_run: bool) -> None:
    if dry_run:
        return
    path.parent.mkdir(parents=True, exist_ok=True)
    image.save(path)


def crop_rect(image: Image.Image, rect: dict, source_name: str) -> Image.Image:
    x = rect["x"]
    y = rect["y"]
    width = rect["width"]
    height = rect["height"]
    if x < 0 or y < 0 or width <= 0 or height <= 0 or x + width > image.width or y + height > image.height:
        fail(f"Crop rectangle {rect} is outside bounds for {source_name} ({image.width}x{image.height})")
    return image.crop((x, y, x + width, y + height))


def constant_name_from_key(key: str) -> str:
    return re.sub(r"[^A-Z0-9]+", "_", key.upper()).strip("_")


def is_column_transparent(image: Image.Image, x: int, y: int, height: int) -> bool:
    return all(image.getpixel((x, yy))[3] == 0 for yy in range(y, y + height))


def apply_adjustment(image: Image.Image, rect: dict) -> dict:
    adjusted = {key: value for key, value in rect.items() if key != "adjust"}
    adjust = rect.get("adjust")
    if adjust is None:
        return adjusted

    probe = adjust.get("probe")
    mode = adjust.get("mode")

    if probe == "left" and mode == "shift_until_transparent":
        while adjusted["x"] > 0 and not is_column_transparent(image, adjusted["x"] - 1, adjusted["y"], adjusted["height"]):
            adjusted["x"] -= 1
        return adjusted

    if probe == "right" and mode == "expand_to_transparent":
        end_x = adjusted["x"] + adjusted["width"]
        while end_x < image.width and not is_column_transparent(image, end_x, adjusted["y"], adjusted["height"]):
            end_x += 1
        adjusted["width"] = end_x - adjusted["x"]
        return adjusted

    fail(f"Unsupported rect adjustment: {adjust}")
    raise AssertionError


def source_descriptor(book_json: dict, source: str) -> tuple[str, dict | str]:
    overlay_keys = {"top_frame_overlay", "bottom_frame_overlay", "left_frame_overlay", "right_frame_overlay"}
    if source in overlay_keys:
        overlay = book_json.get(source, DEFAULT_BOOK_SOURCES.get(source))
        if overlay is None:
            fail(f"Book json is missing required overlay entry: {source}")
        return "overlay", overlay

    if ":" in source:
        return "texture", source

    value = book_json.get(source, DEFAULT_BOOK_SOURCES.get(source))
    if value is None:
        fail(f"Book json is missing required texture entry: {source}")
    return "texture", value


def resolve_category_entry_textures_source(repo_root: Path, category_json: dict, namespace: str) -> str:
    value = category_json.get("entry_textures")
    if value is not None:
        return value

    namespace_default = f"{namespace}:textures/gui/entry_textures.png"
    if try_resolve_resource_path(repo_root, namespace_default) is not None:
        return namespace_default

    return "modonomicon:textures/gui/entry_textures.png"


def resolve_variant_alias(variants: dict, variant_name: str) -> str:
    visited = set()
    current = variant_name
    while True:
        if current in visited:
            fail(f"Variant alias loop detected for {variant_name}")
        visited.add(current)
        variant = variants[current]
        if "copy_of" in variant:
            current = variant["copy_of"]
            if current not in variants:
                fail(f"Variant '{variant_name}' copies unknown variant '{current}'")
            continue
        return current


def generated_java(package_name: str, class_name: str, namespace: str, theme_path: str, generated: list[dict]) -> str:
    lines = [
        "/*",
        " * SPDX-FileCopyrightText: 2026 klikli-dev",
        " *",
        " * SPDX-License-Identifier: MIT",
        " */",
        "",
        f"package {package_name};",
        "",
        "import net.minecraft.resources.Identifier;",
        "",
        f"public final class {class_name} {{",
        "",
    ]

    for item in generated:
        key = item["key"]
        name = constant_name_from_key(key)
        kind = item["kind"]

        if kind == "sprite":
            rect = item["rect"]
            lines.append(f"    public static final GuiSprite {name} = sprite(\"{sprite_relative_path(key)}\", {rect['width']}, {rect['height']});")
        elif kind == "button_states":
            variants = item["resolved_variants"]
            pressed = variants.get("pressed")
            normal_ref = f"sprite(\"{sprite_relative_path(key, variants['normal']['target'])}\", {variants['normal']['width']}, {variants['normal']['height']})"
            hover_ref = f"sprite(\"{sprite_relative_path(key, variants['hover']['target'])}\", {variants['hover']['width']}, {variants['hover']['height']})"
            if pressed is None:
                lines.append(f"    public static final GuiButtonSprites {name} = new GuiButtonSprites({normal_ref}, {hover_ref});")
            else:
                pressed_ref = f"sprite(\"{sprite_relative_path(key, pressed['target'])}\", {pressed['width']}, {pressed['height']})"
                lines.append(f"    public static final GuiButtonSprites {name} = new GuiButtonSprites({normal_ref}, {hover_ref}, {pressed_ref});")
        elif kind == "nine_slice":
            rect = item["rect"]
            borders = item["borders"]
            lines.append(
                f"    public static final GuiNineSlice {name} = new GuiNineSlice(texture(\"{sprite_relative_path(key)}\"), {rect['width']}, {rect['height']}, "
                f"{borders['left']}, {borders['right']}, {borders['top']}, {borders['bottom']});"
            )
        elif kind == "frame_overlay":
            rect = item["rect"]
            lines.append(
                f"    public static final GuiFrameOverlay {name} = new GuiFrameOverlay(sprite(\"{sprite_relative_path(key)}\", {rect['width']}, {rect['height']}), "
                f"{item['frame_x_offset']}, {item['frame_y_offset']});"
            )

    lines.extend([
        "",
        f"    private static final String TEXTURE_ROOT = \"textures/gui/sprites/modonomicon/themes/{theme_path}/\";",
        "",
        f"    private {class_name}() {{",
        "    }",
        "",
        "    private static GuiSprite sprite(String relativePath, int width, int height) {",
        "        return new GuiSprite(texture(relativePath), width, height);",
        "    }",
        "",
        "    private static Identifier texture(String relativePath) {",
        f"        return Identifier.fromNamespaceAndPath(\"{namespace}\", TEXTURE_ROOT + relativePath);",
        "    }",
        "}",
        "",
    ])

    return "\n".join(lines)


def main() -> None:
    args = parse_args()
    repo_root = Path(args.repo_root).resolve()
    if not repo_root.exists():
        fail(f"Repo root does not exist: {repo_root}")

    validate_package_name(args.package)
    validate_class_name(args.class_name)

    namespace, book_path = validate_identifier(args.book_id)
    theme_path = args.theme_path or book_path
    book_json_path = resolve_book_json(repo_root, namespace, book_path, args.book_json_path)
    category_json_path = resolve_first_category_json(book_json_path)
    print(f"Resolved book json: {book_json_path}")
    print(f"Resolved first category json: {category_json_path}")

    manifest_path = repo_root / MANIFEST_PATH
    if not manifest_path.exists():
        fail(f"Manifest not found: {manifest_path}")

    manifest = read_json(manifest_path)
    entries = manifest.get("entries", [])
    if not isinstance(entries, list) or not entries:
        fail("Manifest does not contain any entries.")

    book_json = read_json(book_json_path)
    category_json = read_json(category_json_path)
    output_resource_root = resolve_output_resource_root(repo_root, book_json_path, args.resource_root)
    java_source_root = resolve_java_source_root(output_resource_root)
    target_java_path = java_source_root / Path(args.package.replace(".", "/")) / f"{args.class_name}.java"

    seen_outputs: set[Path] = set()
    seen_constant_names: set[str] = set()
    resolved_sources: dict[str, Path] = {}
    generated_entries: list[dict] = []
    extracted_count = 0

    for entry in entries:
        constant_name = constant_name_from_key(entry["key"])
        if constant_name in seen_constant_names:
            fail(f"Duplicate generated constant name: {constant_name}")
        seen_constant_names.add(constant_name)

        kind = entry["kind"]
        if entry["source"] == "entry_textures":
            source_kind, source_value = ("texture", resolve_category_entry_textures_source(repo_root, category_json, namespace))
        else:
            source_kind, source_value = source_descriptor(book_json, entry["source"])

        if source_kind == "texture":
            source_path = resolve_resource_path(repo_root, source_value)
            resolved_sources.setdefault(entry["source"], source_path)

            with Image.open(source_path).convert("RGBA") as image:
                if kind == "sprite":
                    rect = apply_adjustment(image, entry["rect"])
                    output_relative = sprite_output_relative(theme_path, entry["key"])
                    output_path = output_resource_root / "assets" / namespace / output_relative
                    if output_path in seen_outputs:
                        fail(f"Duplicate output sprite path: {output_path}")
                    seen_outputs.add(output_path)
                    write_image(output_path, crop_rect(image, rect, str(source_path)), args.dry_run)
                    generated_entries.append({**entry, "rect": rect})
                    extracted_count += 1
                elif kind == "button_states":
                    variants = entry["variants"]
                    alias_map = {variant_name: resolve_variant_alias(variants, variant_name) for variant_name in variants}
                    canonical_names = list(dict.fromkeys(alias_map.values()))
                    resolved_variants = {}

                    for canonical_name in canonical_names:
                        rect = apply_adjustment(image, variants[canonical_name])
                        output_relative = sprite_output_relative(theme_path, entry["key"], canonical_name)
                        output_path = output_resource_root / "assets" / namespace / output_relative
                        if output_path in seen_outputs:
                            fail(f"Duplicate output sprite path: {output_path}")
                        seen_outputs.add(output_path)
                        write_image(output_path, crop_rect(image, rect, str(source_path)), args.dry_run)
                        extracted_count += 1
                        resolved_variants[canonical_name] = {**rect, "target": canonical_name}

                    generated_entry = {**entry, "resolved_variants": {}}
                    for variant_name, canonical_name in alias_map.items():
                        rect = resolved_variants[canonical_name]
                        generated_entry["resolved_variants"][variant_name] = {
                            "width": rect["width"],
                            "height": rect["height"],
                            "target": canonical_name,
                        }
                    generated_entries.append(generated_entry)
                elif kind == "nine_slice":
                    rect = apply_adjustment(image, entry["rect"])
                    output_relative = sprite_output_relative(theme_path, entry["key"])
                    output_path = output_resource_root / "assets" / namespace / output_relative
                    if output_path in seen_outputs:
                        fail(f"Duplicate output sprite path: {output_path}")
                    seen_outputs.add(output_path)
                    write_image(output_path, crop_rect(image, rect, str(source_path)), args.dry_run)
                    generated_entries.append({**entry, "rect": rect})
                    extracted_count += 1
                else:
                    fail(f"Entry kind '{kind}' requires overlay source data, but '{entry['key']}' uses a texture source.")
        else:
            if kind != "frame_overlay":
                fail(f"Entry kind '{kind}' cannot use overlay source '{entry['source']}'.")

            overlay = source_value
            overlay_texture = overlay.get("texture")
            if overlay_texture is None:
                fail(f"Overlay {entry['source']} is missing a texture id")

            source_path = resolve_resource_path(repo_root, overlay_texture)
            resolved_sources.setdefault(entry["source"], source_path)
            rect = {
                "x": overlay["texture_width"] // 2 - overlay["frame_width"] // 2,
                "y": overlay["texture_height"] // 2 - overlay["frame_height"] // 2,
                "width": overlay["frame_width"],
                "height": overlay["frame_height"],
            }

            with Image.open(source_path).convert("RGBA") as image:
                output_relative = sprite_output_relative(theme_path, entry["key"])
                output_path = output_resource_root / "assets" / namespace / output_relative
                if output_path in seen_outputs:
                    fail(f"Duplicate output sprite path: {output_path}")
                seen_outputs.add(output_path)
                write_image(output_path, crop_rect(image, rect, str(source_path)), args.dry_run)
                extracted_count += 1

            generated_entries.append({
                **entry,
                "rect": rect,
                "frame_x_offset": overlay["frame_x_offset"],
                "frame_y_offset": overlay["frame_y_offset"],
            })

    java_source = generated_java(args.package, args.class_name, namespace, theme_path, generated_entries)
    if not args.dry_run:
        target_java_path.parent.mkdir(parents=True, exist_ok=True)
        target_java_path.write_text(java_source, encoding="utf-8")

    print("Resolved source textures:")
    for source_name, source_path in resolved_sources.items():
        print(f" - {source_name}: {source_path}")
    print(f"Output resource root: {output_resource_root}")
    print(f"Generated Java file: {target_java_path}")
    print(f"Extracted sprites: {extracted_count}")
    if args.dry_run:
        print("Dry run complete; no files written.")


if __name__ == "__main__":
    main()
