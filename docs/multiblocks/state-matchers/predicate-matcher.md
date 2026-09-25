---
sidebar_position: 50
---

# Predicate Matchers

**Type:** `modonomicon:predicate`

Predicate matchers can use advanced logic to match blocks. The matching logic must be provided as Java code and registered with an `Identifier` ID.

## Attributes

### **predicate** (Identifier, _mandatory_)

  The ID of the predicate to use for the matcher.

### **display** (BlockState, _optional_)

  The block to display in the multiblock preview.

  :::info

  If you omit the BlockState properties (`[key=value]`), Modonomicon will display the Block's default BlockState.

  :::

### **counts_towards_total_blocks** (boolean, _optional_, default: `true`)

  If set to `false` the block will not count towards the total number of blocks required to complete the multiblock.
  E.g. for the `modonomicon:air` predicate you would set this to `false`.
  If set to `true` the block will count towards the total number of blocks required to complete the multiblock, which in the case of air and air-like blocks would lead to an insane number of blocks shown as still required to complete.

## Registering Predicates

Create a dedicated registry class and register your predicates via `PredicateRegistry.register(...)`:

```java
public final class MyModModonomiconPredicateRegistry {

    public static final PredicateType MY_PREDICATE = PredicateRegistry.register(
        new Identifier("mymod", "my_predicate"),
        (getter, pos, state) -> state.isSolid() && pos.getY() > 64
    );

    private MyModModonomiconPredicateRegistry() {
    }

    public static void bootstrap() {
    }
}
```

The predicate lambda must match the `TriPredicate<BlockGetter, BlockPos, BlockState>` signature.

Then call `bootstrap()` during `FMLCommonSetupEvent` (NeoForge) or your `ModInitializer.onInitialize` (Fabric):

```java
// NeoForge
public void onCommonSetup(FMLCommonSetupEvent event) {
    MyModModonomiconPredicateRegistry.bootstrap();
}

// Fabric
@Override
public void onInitialize() {
    MyModModonomiconPredicateRegistry.bootstrap();
}
```

:::caution

To access `PredicateRegistry` you need to define a dependency on the full modonomicon jar in your `build.gradle` (See **[Maven Dependencies](../../getting-started/maven-dependencies)**).

If you then call `PredicateRegistry.register(...)` directly you effectively create a hard dependency on Modonomicon. You can avoid this by calling `PredicateRegistry.register(...)` in a separate class, and only calling that class's methods if Modonomicon is loaded.

:::

## Builtin Predicates

### Air Predicate

**Predicate ID:** `modonomicon:air`

Matches air blocks. Requires players to remove any blocks in the location of this matcher.

### Non-Solid Predicate

**Predicate ID:** `modonomicon:non_solid`

Matches blocks that are not solid.

## Usage Examples

Match air predicate and display nothing:

```json
{
  "type": "modonomicon:predicate",
  "predicate": "modonomicon:air",
  "display": "minecraft:air"
}
```
