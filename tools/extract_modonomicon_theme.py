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


def parse_args() -> argparse.Namespace:
    parser = argparse.ArgumentParser(description="Extract Modonomicon theme sprites and generate Java theme data.")
    parser.add_argument("repo_root", help="Root of the target mod project.")
    parser.add_argument("book_id", help="Fully qualified book id, for example modonomicon:demo.")
    parser.add_argument("--class-name", default=DEFAULT_CLASS_NAME)
    parser.add_argument("--package", default=DEFAULT_PACKAGE)
    parser.add_argument("--resource-root", help="Resource root to write generated assets into.")
    parser.add_argument("--theme-path", help="Output theme path below sprites/modonomicon. Defaults to the book path.")
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


def resolve_output_resource_root(repo_root: Path, book_json_path: Path, override: str | None) -> Path:
    if override:
        result = (repo_root / override).resolve() if not Path(override).is_absolute() else Path(override).resolve()
        if not result.exists():
            fail(f"Resource root does not exist: {result}")
        return result

    resource_path = book_json_path.resolve()
    parts = resource_path.parts
    for marker in ("src",):
        for index in range(len(parts) - 2):
            if parts[index] == marker and parts[index + 1] in {"main", "generated"} and parts[index + 2] == "resources":
                return Path(*parts[: index + 3])

    fail(f"Could not derive resource root from {book_json_path}")
    raise AssertionError


def resolve_java_source_root(resource_root: Path) -> Path:
    parts = list(resource_root.parts)
    if len(parts) < 3 or parts[-3:] not in (["src", "main", "resources"], ["src", "generated", "resources"]):
        fail(f"Cannot derive java source root from resource root {resource_root}")

    source_kind = parts[-2]
    return Path(*parts[:-3], "src", source_kind, "java")


def validate_package_name(value: str) -> None:
    if not re.fullmatch(r"[A-Za-z_]\w*(\.[A-Za-z_]\w*)*", value):
        fail(f"Invalid package name: {value}")

    for segment in value.split("."):
        if keyword.iskeyword(segment):
            fail(f"Invalid package name segment: {segment}")


def validate_class_name(value: str) -> None:
    if not re.fullmatch(r"[A-Za-z_]\w*", value) or keyword.iskeyword(value):
        fail(f"Invalid class name: {value}")


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


def sprite_output_relative(theme_path: str, key: str, suffix: str | None = None) -> Path:
    name = f"{key}_{suffix}.png" if suffix else f"{key}.png"
    return Path("textures/gui/sprites/modonomicon") / theme_path / name


def write_image(path: Path, image: Image.Image, dry_run: bool) -> None:
    if dry_run:
        return
    path.parent.mkdir(parents=True, exist_ok=True)
    image.save(path)


def source_descriptor(book_json: dict, source: str) -> tuple[str, dict | str]:
    overlay_keys = {
        "top_frame_overlay",
        "bottom_frame_overlay",
        "left_frame_overlay",
        "right_frame_overlay",
    }
    if source in overlay_keys:
        overlay = book_json.get(source)
        if overlay is None:
            fail(f"Book json is missing required overlay entry: {source}")
        return "overlay", overlay

    value = book_json.get(source)
    if value is None:
        fail(f"Book json is missing required texture entry: {source}")
    return "texture", value


def generated_java(package_name: str, class_name: str, namespace: str, theme_path: str, entries: list[dict], generated: list[dict]) -> str:
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
            lines.append(f"    public static final GuiSprite {name} = sprite(\"{key}.png\", {rect['width']}, {rect['height']});")
        elif kind == "button_states":
            variants = item["variants"]
            pressed = variants.get("pressed")
            if pressed is None:
                lines.append(
                    f"    public static final GuiButtonSprites {name} = new GuiButtonSprites("
                    f"sprite(\"{key}_normal.png\", {variants['normal']['width']}, {variants['normal']['height']}), "
                    f"sprite(\"{key}_hover.png\", {variants['hover']['width']}, {variants['hover']['height']})"
                    ");"
                )
            else:
                lines.append(
                    f"    public static final GuiButtonSprites {name} = new GuiButtonSprites("
                    f"sprite(\"{key}_normal.png\", {variants['normal']['width']}, {variants['normal']['height']}), "
                    f"sprite(\"{key}_hover.png\", {variants['hover']['width']}, {variants['hover']['height']}), "
                    f"sprite(\"{key}_pressed.png\", {pressed['width']}, {pressed['height']})"
                    ");"
                )
        elif kind == "nine_slice":
            rect = item["rect"]
            borders = item["borders"]
            lines.append(
                f"    public static final GuiNineSlice {name} = new GuiNineSlice(texture(\"{key}.png\"), {rect['width']}, {rect['height']}, "
                f"{borders['left']}, {borders['right']}, {borders['top']}, {borders['bottom']});"
            )
        elif kind == "frame_overlay":
            rect = item["rect"]
            lines.append(
                f"    public static final GuiFrameOverlay {name} = new GuiFrameOverlay(sprite(\"{key}.png\", {rect['width']}, {rect['height']}), "
                f"{item['frame_x_offset']}, {item['frame_y_offset']});"
            )

    lines.extend([
        "",
        f"    private static final String TEXTURE_ROOT = \"textures/gui/sprites/modonomicon/{theme_path}/\";",
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
    print(f"Resolved book json: {book_json_path}")

    manifest_path = repo_root / MANIFEST_PATH
    if not manifest_path.exists():
        fail(f"Manifest not found: {manifest_path}")

    manifest = read_json(manifest_path)
    entries = manifest.get("entries", [])
    if not isinstance(entries, list) or not entries:
        fail("Manifest does not contain any entries.")

    book_json = read_json(book_json_path)
    output_resource_root = resolve_output_resource_root(repo_root, book_json_path, args.resource_root)
    java_source_root = resolve_java_source_root(output_resource_root)
    target_texture_root = output_resource_root / "assets" / namespace / "textures/gui/sprites/modonomicon" / theme_path
    target_java_path = java_source_root / Path(args.package.replace(".", "/")) / f"{args.class_name}.java"

    seen_outputs: set[Path] = set()
    resolved_sources: dict[str, Path] = {}
    generated_entries: list[dict] = []
    extracted_count = 0
    seen_constant_names: set[str] = set()

    for entry in entries:
        constant_name = constant_name_from_key(entry["key"])
        if constant_name in seen_constant_names:
            fail(f"Duplicate generated constant name: {constant_name}")
        seen_constant_names.add(constant_name)

        kind = entry["kind"]
        source_kind, source_value = source_descriptor(book_json, entry["source"])

        if source_kind == "texture":
            source_path = resolve_resource_path(repo_root, source_value)
            resolved_sources.setdefault(entry["source"], source_path)
            with Image.open(source_path) as image:
                if kind == "sprite":
                    output_relative = sprite_output_relative(theme_path, entry["key"])
                    output_path = output_resource_root / "assets" / namespace / output_relative
                    if output_path in seen_outputs:
                        fail(f"Duplicate output sprite path: {output_path}")
                    seen_outputs.add(output_path)
                    write_image(output_path, crop_rect(image, entry["rect"], str(source_path)), args.dry_run)
                    generated_entries.append(entry)
                    extracted_count += 1
                elif kind == "button_states":
                    generated_entry = {**entry}
                    generated_entries.append(generated_entry)
                    for variant_name, rect in entry["variants"].items():
                        output_relative = sprite_output_relative(theme_path, entry["key"], variant_name)
                        output_path = output_resource_root / "assets" / namespace / output_relative
                        if output_path in seen_outputs:
                            fail(f"Duplicate output sprite path: {output_path}")
                        seen_outputs.add(output_path)
                        write_image(output_path, crop_rect(image, rect, str(source_path)), args.dry_run)
                        extracted_count += 1
                elif kind == "nine_slice":
                    output_relative = sprite_output_relative(theme_path, entry["key"])
                    output_path = output_resource_root / "assets" / namespace / output_relative
                    if output_path in seen_outputs:
                        fail(f"Duplicate output sprite path: {output_path}")
                    seen_outputs.add(output_path)
                    write_image(output_path, crop_rect(image, entry["rect"], str(source_path)), args.dry_run)
                    generated_entries.append(entry)
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
            generated_entry = {
                **entry,
                "rect": rect,
                "frame_x_offset": overlay["frame_x_offset"],
                "frame_y_offset": overlay["frame_y_offset"],
            }
            with Image.open(source_path) as image:
                output_relative = sprite_output_relative(theme_path, entry["key"])
                output_path = output_resource_root / "assets" / namespace / output_relative
                if output_path in seen_outputs:
                    fail(f"Duplicate output sprite path: {output_path}")
                seen_outputs.add(output_path)
                write_image(output_path, crop_rect(image, rect, str(source_path)), args.dry_run)
                extracted_count += 1
            generated_entries.append(generated_entry)

    java_source = generated_java(args.package, args.class_name, namespace, theme_path, entries, generated_entries)
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
