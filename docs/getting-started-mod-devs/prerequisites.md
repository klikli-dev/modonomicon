---
sidebar_position: 10
---

# Prerequisites

To use modonomicon you need to set up your `build.gradle` file (and `gradle.properties`, if you want to store versions in variables) to include modonomicon as dependency.

## Repository 

First, add the Modonomicon Maven repository to your `build.gradle` file in the `repositories` section:

```groovy
repositories {
    maven {
        name = "KliKli Dev Repsy Maven"
        url = "https://repo.repsy.io/mvn/klikli-dev/mods"
    }
}
```

## Dependencies

Depending on your use case you need a compile time dependency against the Modonomicon API, against the full Modonomicon jar, or just a runtime dependency against the full Modonomicon jar (to load Modonomicon in your Dev env). 

:::tip

Even compile time dependency against the full Modonomicon jar does not imply that Modonomicon will be a required dependency for your mod at runtime! 
Modonomicon only becomes a required runtime dependency if any of your classes that reference Modonomicon Classes are loaded at runtime.

This is especially relevant if you want to extend Modonomicon content rendering for your mod: You can reference the full Modonomicon jar at compile time, but keep Modonomicon an optional dependency on Curseforge by ensuring only loads classes that reference Modonomicon classes, if modonomicon is loaded.

:::

<!-- TODO: Link to an appropriate section in extending modonomicon that explains how to guard calls behind isLoaded checks and to only call in separate "buffer" classes -->


### Use Case 1: I want to use datagen and convenience features to generate a Modonomicon Book

In this case you need a compileOnly dependency against the Modonomicon API jar to access the Modonomicon Datamodel and datagen helpers. 
Additionally a runtimeOnly dependency against the full Modonomicon jar is required to load Modonomicon in your dev environment.

The `dependencies` section of your `build.gradle` should look like this:

`build.gradle`:
```groovy
dependencies {
    compileOnly fg.deobf("com.klikli_dev:modonomicon:${modonomicon_mc_version}-${modonomicon_version}:api")
    runtimeOnly fg.deobf("com.klikli_dev:modonomicon:${modonomicon_mc_version}-${modonomicon_version}")
}
```

`gradle.properties`:
```properties
modonomicon_mc_version=1.19.2
modonomicon_version=1.0.17
```

**Ensure UTF-8 Encoding**

To avoid errors with special characters in your generated texts, you should set the encoding to UTF-8 in your `build.gradle` file. This can be done by adding the following lines to the bottom of your `build.gradle`:

```groovy
compileJava.options.encoding = 'UTF-8'
tasks.withType(JavaCompile) {
    options.encoding = 'UTF-8'
}

```

:::tip

This is recommended even if you do not use Modonomicon datagen or Modonomicon at all. As soon as you datagen `.lang` this will save you a lot of headache. 

:::

### Use Case 2: I just want to manually write a Modonomicon Book

For convenience it is recommended to use Use Case 1 over Use Case 2, but if you prefer to edit JSON files directly this is the way to go. 

In this case you do not need a compile time dependency against Modonomicon at all. 
A runtimeOnly dependency against the full Modonomicon jar is required to load Modonomicon in your dev environment.

The `dependencies` section of your `build.gradle` should look like this:

`build.gradle`:
```groovy
dependencies {
    runtimeOnly fg.deobf("com.klikli_dev:modonomicon:${modonomicon_mc_version}-${modonomicon_version}")
}
```

`gradle.properties`:
```properties
modonomicon_mc_version=1.19.2
modonomicon_version=1.0.17
```

### Use Case 3: I want to extend Modonomicon with custom features for my mod 

In this case you need a compile time dependency against the full Modonomicon jar to access all of Modonomicon's classes and features, especially the registries for Loader and Renderer classes for new content.

The `dependencies` section of your `build.gradle` should look like this:

`build.gradle`:
```groovy
dependencies {
    implementation fg.deobf("com.klikli_dev:modonomicon:${modonomicon_mc_version}-${modonomicon_version}")
}
```

`gradle.properties`:
```properties
modonomicon_mc_version=1.19.2
modonomicon_version=1.0.17
```