---
sidebar_position: 5
---

# Getting Started

:::tip

Modonomicon uses both resources (translation files) and data (page definitions). If you want to live-reload your changes run `/modonomicon reload` to reload both resource- and datapacks.

This of course requires the changed files to be in the correct data/resource pack locations for minecraft to load.

:::


The easiest way to get started is to follow the step by step guide **[Step by Step with Datagen](./step-by-step-with-datagen/)**. This guide uses datagen, a minecraft development feature, so it assumes that you know how to set up a mod project in either forge, neoforge or fabric.

:::tip

Even for modpack creators it is recommended to follow this process. Datagen is a much more convenient way to create books and ensure they do not have errors that might keep them from loading or from working in game.

:::

## Manually creating books 

:::warning

It is not recommended to manually create books. It is much more convenient and error-free to use datagen.

:::

If you instead want to manually create a book by editign JSON files directly there is currently no guide, however you can look at the generated results of the demo book **[here](https://github.com/klikli-dev/modonomicon/tree/version/1.20.1/neo/src/generated/resources/data/modonomicon/modonomicon/books)**.

Further, you can reference the [Structure](../basics/structure/) section to learn about the file structures.