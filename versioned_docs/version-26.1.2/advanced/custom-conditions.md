---
sidebar_position: 40
---

# Custom Conditions

Mods can add custom conditions that can be used to lock entries or categories. To this end you need to:

1. Create a `ResourceLocation` ID for the new condition (e.g. `mymod:my_condition`)
2. Create a custom condition class with `ID`, `CODEC`, and `STREAM_CODEC`
3. Register the condition via a dedicated registry class
4. For datagen: create a condition model class
5. Finally use the condition in your book

:::tip

The `ResourceLocation` is what you will use in your entries and categories to gate them behind your custom condition.

:::

## Condition Class

Conditions need to extend `BookCondition` in the package `com.klikli_dev.modonomicon.book.conditions`.

Each condition class must declare three static fields:

- `ID` — a `ResourceLocation` (use `Modonomicon.loc(...)` or `new ResourceLocation("mymod", "my_condition")`)
- `CODEC` — a `MapCodec<T>` for JSON deserialization, typically built with `RecordCodecBuilder.mapCodec(...)`
- `STREAM_CODEC` — a `StreamCodec<RegistryFriendlyByteBuf, T>` for network serialization, typically built with `StreamCodec.composite(...)`

You must also override `type()` to return the corresponding `BookConditionType<T>` from your registry class.

See these example conditions for reference:
- [`BookModLoadedCondition`](https://github.com/klikli-dev/modonomicon/blob/-/common/src/main/java/com/klikli_dev/modonomicon/book/conditions/BookModLoadedCondition.java)
- [`BookFalseCondition`](https://github.com/klikli-dev/modonomicon/blob/-/common/src/main/java/com/klikli_dev/modonomicon/book/conditions/BookFalseCondition.java)
- [`BookResearchNodeUnlockedCondition`](https://github.com/klikli-dev/modonomicon/blob/-/common/src/main/java/com/klikli_dev/modonomicon/book/conditions/BookResearchNodeUnlockedCondition.java)

## Condition Registration

Create a dedicated registry class to hold your condition registrations. Use `public static final` fields initialized via `BookConditionTypeRegistry.register(...)`, and provide an empty `bootstrap()` method to trigger class loading:

```java
public final class MyModModonomiconConditionRegistry {

    public static final BookConditionType<BookMyCustomCondition> MY_CUSTOM =
        BookConditionTypeRegistry.register(
            BookMyCustomCondition.ID,
            BookMyCustomCondition.CODEC,
            BookMyCustomCondition.STREAM_CODEC
        );

    private MyModModonomiconConditionRegistry() {
    }

    public static void bootstrap() {
    }
}
```

Then call `bootstrap()` during `FMLCommonSetupEvent` (NeoForge) or your `ModInitializer.onInitialize` (Fabric):

```java
// NeoForge
public void onCommonSetup(FMLCommonSetupEvent event) {
    MyModModonomiconConditionRegistry.bootstrap();
}

// Fabric
@Override
public void onInitialize() {
    MyModModonomiconConditionRegistry.bootstrap();
}
```

## Condition Datagen Model

The model class helps you to generate the condition JSON files via DataGen.

Extend `BookConditionModel<T>` and override `toBookCondition(HolderLookup.Provider)` to return the runtime condition instance. Pass the condition's `ID` to the `super(...)` constructor.

See these example models for reference:
- [`BookModLoadedConditionModel`](https://github.com/klikli-dev/modonomicon/blob/-/common/src/main/java/com/klikli_dev/modonomicon/api/datagen/book/condition/BookModLoadedConditionModel.java)
- [`BookFalseConditionModel`](https://github.com/klikli-dev/modonomicon/blob/-/common/src/main/java/com/klikli_dev/modonomicon/api/datagen/book/condition/BookFalseConditionModel.java)
- [`BookResearchNodeUnlockedConditionModel`](https://github.com/klikli-dev/modonomicon/blob/-/common/src/main/java/com/klikli_dev/modonomicon/api/datagen/book/condition/BookResearchNodeUnlockedConditionModel.java)

:::tip
You need not register the model.
:::

## Usage

On your entry or category, add:

```json
{
  ...
  "condition": {
      "type": "mymod:my_condition"
  },
  ...
}
```

See also [Unlock Conditions](../basics/unlock-conditions) for more information on how to use conditions.
