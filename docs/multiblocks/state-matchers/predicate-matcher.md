---
sidebar_position: 50
---

# Predicate Matchers

# Tag Matcher

**Type:** `modonomicon:predicate`

Predicate matchers can use advanced logic to match blocks. The matching logic must be provided as Java Code and registerd with a Resource Location ID.
Tag matchers will check if the placed block is part of the provided tag. Additionally BlockState properties can be provided to match against, in that case the matcher will only match if the block is in the tag, and has the provided properties.

## Attributes

### **predicate** (ResourceLocation, _mandatory_)
  
  The ID of the predicate to use for the matcher.

### **display** (BlockState, _mandatory_)
  
  The block to display in the multiblock preview. 

  :::info

  If you omit the BlockState properties (`[key=value]`), Modonomicon will display the Block's default BlockState.

  :::

## Registering Predicates

Predicates are registered in the `FMLCommonSetupEvent`: 

```java 
public void onCommonSetup(FMLCommonSetupEvent event) {
    LoaderRegistry.registerPredicate(new ResourceLocation("modonomicon:air"), (getter, pos, state) -> state.isAir());
}
```

You can of course define any predicate you like that fits a 
```java 
TriPredicate<BlockGetter, BlockPos, BlockState>
```
:::caution 

To access the `LoaderRegistry` you need to define a dependency on the full modonomicon jar in your `build.gralde` (See **[Maven Dependencies](../../getting-started/maven-dependencies)**)
If you then call `LoaderRegistry.registerPredicate` in your `FMLCommonSetupEvent` like above you effectively create a hard dependency on Modonomicon. 
You can avoid this by calling `LoaderRegistry.registerPredicate` in a separate class, and only call that classes Methods if Modonomicon is loaded.

::: 

<!-- TODO: Link to an article that explains in detail how to guard against no class def -->

## Builtin Predicates

### Air Predicate

**Predicate ID:** `modonomicon:air`

Matches air blocks. Requires players to remove any blocks in the location of this matcher.

## Usage Examples

Match air predicate and display .. nothing

```json
{
  "type": "modonomicon:predicate",
  "predicate": "modonomicon:air",
  "display": "minecraft:air"
}
```