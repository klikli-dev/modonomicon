---
sidebar_position: 6
title: Updating from 1.21.1 to 26.1.2
---

# Updating from 1.21.1 to 26.1.2

This upgrade has three separate migration areas:

- [Codec and content type changes](./codecs)
- [Theme and styling changes](./theme)
- [Research system and datagen](./research-datagen)

Most mods need at least two of them:

- If you only changed book appearance, textures, colors, or layout, go to [Theme and styling changes](./theme).
- If you maintain custom Modonomicon integrations in code or raw JSON content, go to [Codec and content type changes](./codecs).
- If you use unlock conditions or want to author research content, go to [Research system and datagen](./research-datagen).

:::warning Mod loaded condition

`modonomicon:mod_loaded` used as regular `condition` no longer prevents content from being loaded, it only hides it. Move it to `load_condition` (datagen: `withLoadCondition(...)`) to remove content if the mod is not loaded. See [Load Conditions](../../basics/unlock-conditions/load-conditions).

Custom conditions overriding `BookCondition#testOnLoad()` need to override `supportsLoadTime()` and `testOnLoad(BookLoadConditionContext)` instead.

:::
