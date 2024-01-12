---
sidebar_position: 30
---

# Maven Dependencies

To use modonomicon you need to set up your `build.gradle` file (and `gradle.properties`, if you want to store versions in variables) to include modonomicon as dependency.

:::tip 

If you followed the **[Step by Step with Datagen](./step-by-step-with-datagen/)** guide you already have this set up. This section is only if you want to add modonomicon to an existing project. 

::: 

## Repository 

First, add the Modonomicon Maven repository to your `build.gradle` file in the `repositories` section:

```groovy
repositories {
    maven {
        url "https://dl.cloudsmith.io/public/klikli-dev/mods/maven/"
        content {
            includeGroup "com.klikli_dev"
        }
    }
}
```

## Dependencies

Modonomicon only provides a single jar file, which contains both the API and the implementation. 

:::tip

Even compile time dependency against the full Modonomicon jar does not imply that Modonomicon will be a required dependency for your mod at runtime! 
Modonomicon only becomes a required runtime dependency if any of your classes that reference Modonomicon Classes are loaded at runtime.

This is especially relevant if you want to extend Modonomicon content rendering for your mod: You can reference the full Modonomicon jar at compile time, but keep Modonomicon an optional dependency on Curseforge by ensuring only loads classes that reference Modonomicon classes, if modonomicon is loaded.

:::

The below use cases use variables for the modonomicon version, set up your `gradle.properties` file accordingly:

`gradle.properties`:
```properties
# choose appropriate latest version from: https://cloudsmith.io/~klikli-dev/repos/mods/groups/
minecraft_version=1.20.1
modonomicon_version=1.38.5
```


The `dependencies` section of your `build.gradle` should look like this:


### Forge

`build.gradle`:
```groovy
dependencies {
    ... //other dependencies
    implementation fg.deobf("com.klikli_dev:modonomicon-${minecraft_version}-forge:${modonomicon_version}") 
}
```

:::info

You may need to add `{transitive=false}` at the end of the `implementation ...` line.

:::

### Fabric

```groovy
dependencies {
    ... //other dependencies
    modImplementation "com.klikli_dev:modonomicon-${minecraft_version}-fabric:${modonomicon_version}"
}
```

### Common

```groovy
dependencies {
    ... //other dependencies
    compileOnly "com.klikli_dev:modonomicon-${minecraft_version}-common:${modonomicon_version}"
}
```