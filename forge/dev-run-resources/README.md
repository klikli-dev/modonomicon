# Forge dev-run resources (NOT shipped in the mod jar)

## Why does this exist?

The MCP config for Minecraft 26.3 (`26.3-20260918.230105`) produces an **empty**
`client-extra` artifact (only `META-INF/MANIFEST.MF`, no `assets/`).
`client-extra` is what puts vanilla client resources such as
`assets/minecraft/lang/en_us.json` into the `minecraft` game module of Forge
dev runs.

Without that file, vanilla `Language.<clinit>` throws a `NullPointerException`
(`Language.getResourceAsStream("/assets/minecraft/lang/en_us.json")` returns
`null`) during `ForgeBootstrap`, before any mod code runs. This breaks every
Forge run that boots the game, including `:forge:runData` and
`:forge:runClientData`.

## How does the workaround work?

`forge/build.gradle` builds `client-extra-dev-stub.jar` from
`assets/**` in this directory and adds it via `runtimeOnly files(...)`, so it
is only on dev-run classpaths: it is never compiled and never packaged into
the mod jar.

The file name deliberately starts with `client-extra`: Forge's
`ForgeUserdevLaunchHandler.getMinecraftPaths()` locates the client resources
jar with `findJarOnClasspath(classpath, "client-extra")`, which returns the
first match. Our stub jar sorts before the (empty) real `client-extra` on the
run classpath, so it is unioned into the `minecraft` game module and provides
the stub translation file.

The stub is a minimal, empty `en_us.json` (`{}`). It is only used as the
fallback `DEFAULT_INSTANCE` map; Forge replaces the instance with real
language data at runtime, and datagen providers do not read vanilla
translations, so generated output is unaffected (verified byte-identical to
Fabric/Neo output).

## Removal

Delete this directory (and the `clientExtraStubJar` task plus the
`runtimeOnly` line in `forge/build.gradle`) once an MCP config / Forge version
ships a non-empty `client-extra` for 26.3 again. Verify by running
`:forge:runData` without it.
