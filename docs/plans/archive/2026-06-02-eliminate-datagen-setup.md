# Plan: Eliminate `ModonomiconDataGenSetup` — Pass Caches Directly

## Goal
Remove the `ModonomiconDataGenSetup` orchestrator class. The two caches (`LanguageProviderCache` and `ResearchCache`) are created by the datagen entry point and passed directly to each provider. No intermediate object.

## Backwards Compat
None. Old constructors and methods are removed outright.

## Research Cache Semantics
`accept()` (throws on duplicate) is used by `BookProvider` for book-generated bundles.
`merge()` (overwrites) is used by `ResearchProvider` for authored bundles.
A book-generated and authored bundle sharing an ID is a user error — throwing is intended.

---

## File Changes

### 1. `common/.../api/datagen/ModonomiconDataGenSetup.java` — **DELETE**

### 2. `common/.../api/datagen/BookProvider.java`
- Remove `private final ModonomiconDataGenSetup setup` field
- Remove the 5-arg constructor `(PackOutput, CompletableFuture, String, List<BookSubProvider>, ModonomiconDataGenSetup)`
- Remove the 4-arg constructor (delegated to 5-arg with `null`) — or keep as primary with `langCache = null`
- Add `private final LanguageProviderCache langCache` field
- New single constructor: `(PackOutput, CompletableFuture, String, List<BookSubProvider>, LanguageProviderCache)`
- In `run()`: replace `this.setup` with `this.langCache`; replace `this.setup.researchCache()` with a new `ResearchCache` passed from the constructor, or pass it separately

> **Revised**: BookProvider needs both caches — `langCache` for injection, `researchCache` for pushing compiled hierarchy research. New constructor takes both.

- Add `private final ResearchCache researchCache` field
- Constructor: `(PackOutput, CompletableFuture, String, List<BookSubProvider>, LanguageProviderCache langCache, ResearchCache researchCache)`
- In `run()`:
  - `if (this.langCache != null)` → inject into subproviders
  - `if (this.researchCache != null)` → `this.researchCache.accept(...)` instead of `this.setup.researchCache().accept(...)`

### 3. `common/.../api/datagen/research/ResearchProvider.java`
- Remove `private final ModonomiconDataGenSetup setup` field
- Remove the 5-arg constructor with `ModonomiconDataGenSetup`
- Remove the 4-arg constructor that delegated with `null`
- Add `private final ResearchCache researchCache` field
- New single constructor: `(PackOutput, CompletableFuture, String, List<ResearchSubProvider>, ResearchCache)`
- In `run()`: replace `this.setup.researchCache().build()` with `this.researchCache.build()`

### 4. `neo/.../api/datagen/NeoBookProvider.java`
Replace `of(ModonomiconDataGenSetup, GatherDataEvent, BookSubProvider...)` with:
```java
public static BookProvider of(GatherDataEvent event, LanguageProviderCache langCache, ResearchCache researchCache, BookSubProvider... subProviders) {
    return new BookProvider(event.getGenerator().getPackOutput(), event.getLookupProvider(),
            event.getModContainer().getModId(), List.of(subProviders), langCache, researchCache);
}
```

### 5. `neo/.../api/datagen/NeoResearchProvider.java`
Replace `of(ModonomiconDataGenSetup, GatherDataEvent, ResearchSubProvider...)` with:
```java
public static ResearchProvider of(GatherDataEvent event, ResearchCache researchCache, ResearchSubProvider... subProviders) {
    return new ResearchProvider(event.getGenerator().getPackOutput(), event.getLookupProvider(),
            event.getModContainer().getModId(), List.of(subProviders), researchCache);
}
```

### 6. `fabric/.../api/datagen/FabricBookProvider.java`
Replace with factory capturing both caches:
```java
public static FabricDataGenerator.Pack.RegistryDependentFactory<BookProvider> of(
        String modId, LanguageProviderCache langCache, ResearchCache researchCache, BookSubProvider... subProviders) {
    return (output, registries) ->
            new BookProvider(output, registries, modId, List.of(subProviders), langCache, researchCache);
}
```

### 7. `fabric/.../api/datagen/FabricResearchProvider.java`
Replace with factory capturing research cache:
```java
public static FabricDataGenerator.Pack.RegistryDependentFactory<ResearchProvider> of(
        String modId, ResearchCache researchCache, ResearchSubProvider... subProviders) {
    return (output, registries) ->
            new ResearchProvider(output, registries, modId, List.of(subProviders), researchCache);
}
```

### 8. `forge/.../api/datagen/ForgeBookProvider.java`
Same pattern as Neo:
```java
public static BookProvider of(GatherDataEvent event, LanguageProviderCache langCache, ResearchCache researchCache, BookSubProvider... subProviders) {
    return new BookProvider(event.getGenerator().getPackOutput(), event.getLookupProvider(),
            event.getModContainer().getModId(), List.of(subProviders), langCache, researchCache);
}
```

### 9. `forge/.../api/datagen/ForgeResearchProvider.java`
Same pattern as Neo:
```java
public static ResearchProvider of(GatherDataEvent event, ResearchCache researchCache, ResearchSubProvider... subProviders) {
    return new ResearchProvider(event.getGenerator().getPackOutput(), event.getLookupProvider(),
            event.getModContainer().getModId(), List.of(subProviders), researchCache);
}
```

### 10. `neo/.../datagen/DataGenerators.java`
```java
// Before:
var setup = ModonomiconDataGenSetup.create(Modonomicon.MOD_ID, "en_us");
generator.addProvider(true, NeoBookProvider.of(setup, event, new DemoBook(), ...));
generator.addProvider(true, NeoResearchProvider.of(setup, event, new DemoResearch()));
generator.addProvider(true, new EnUsProvider(generator.getPackOutput(), setup.langCache()));

// After:
var langCache = new LanguageProviderCache("en_us");
var researchCache = new ResearchCache();
generator.addProvider(true, NeoBookProvider.of(event, langCache, researchCache, new DemoBook(), ...));
generator.addProvider(true, NeoResearchProvider.of(event, researchCache, new DemoResearch()));
generator.addProvider(true, new EnUsProvider(generator.getPackOutput(), langCache));
```

### 11. `fabric/.../datagen/DataGenerators.java`
```java
// Before:
var setup = ModonomiconDataGenSetup.create(Modonomicon.MOD_ID, "en_us");
pack.addProvider(FabricBookProvider.of(setup, new DemoBook(), ...));
pack.addProvider(FabricResearchProvider.of(setup, new DemoResearch()));
pack.addProvider((output) -> new EnUsProvider(output, setup.langCache()));

// After:
var langCache = new LanguageProviderCache("en_us");
var researchCache = new ResearchCache();
pack.addProvider(FabricBookProvider.of(Modonomicon.MOD_ID, langCache, researchCache, new DemoBook(), ...));
pack.addProvider(FabricResearchProvider.of(Modonomicon.MOD_ID, researchCache, new DemoResearch()));
pack.addProvider((output) -> new EnUsProvider(output, langCache));
```

### 12. `forge/.../datagen/DataGenerators.java`
Same pattern as Neo.

### 13. `common/.../api/datagen/ModonomiconProviderBase.java`
Line 71: Update javadoc — remove reference to `ModonomiconDataGenSetup`. Replace with something like "Injects a language provider. Called by `BookProvider` during generation."

---

## Summary: 13 files touched

| # | File | Action |
|---|---|---|
| 1 | `ModonomiconDataGenSetup.java` | **delete** |
| 2 | `BookProvider.java` | new constructor, new fields, updated `run()` |
| 3 | `ResearchProvider.java` | new constructor, new field, updated `run()` |
| 4 | `NeoBookProvider.java` | replace `of()` |
| 5 | `NeoResearchProvider.java` | replace `of()` |
| 6 | `FabricBookProvider.java` | replace `of()` |
| 7 | `FabricResearchProvider.java` | replace `of()` |
| 8 | `ForgeBookProvider.java` | replace `of()` |
| 9 | `ForgeResearchProvider.java` | replace `of()` |
| 10 | `neo/.../DataGenerators.java` | wire caches directly |
| 11 | `fabric/.../DataGenerators.java` | wire caches directly |
| 12 | `forge/.../DataGenerators.java` | wire caches directly |
| 13 | `ModonomiconProviderBase.java` | fix javadoc |
