# Minecraft 26.2 Port Implementation Plan

## Scope

This plan completes the remaining 26.1.2 to 26.2 migration failures identified by `./gradlew.bat compileJava` on `feat/finish-porting-to-26.2`. The target dependency set is defined in `gradle.properties`:

- Minecraft `26.2`
- NeoForge `26.2.0.36-beta`
- Forge `62.0.1`
- Fabric API `0.155.2+26.2`

The rendering work must be implemented against the exact resolved 26.2 sources immediately before editing. The loader APIs are beta APIs and have changed during the port; do not copy an old 26.1 transformed source signature into the implementation.

## Invariants

The migration is not complete until all of the following existing behavior is retained:

- The in-world multiblock preview keeps its anchoring, rotation, 64-block distance cutoff, target-block pulse, 30% block alpha, 60% block-entity alpha, full-bright block entities, air placeholder, progress HUD, completion sound, and per-block-entity failure isolation.
- The book multiblock PIP keeps its 106-by-106 texture output, isometric transforms, automatic rotation, Shift pause, scale-to-fit calculation, per-page fake block-entity cache, error quarantine, and translucent/cutout material behavior.
- Direct-entry connection PIP output keeps segment geometry, variable line width, opacity and brightness factors, wiggle phase/amplitude/speed, and color fade.
- GUI-layer detection, research toasts, the runtime lightning-rod icon, and generated item tags retain their current user-visible results.
- No implementation depends on OpenGL-only classes or direct GL uploads. The result must work with both supported backend implementations.

## API Verification Gate

Before each rendering edit, inspect the resolved Mojmaps source/classes with the project Minecraft source lookup workflow and record the result in the implementation commit. Confirm these concrete signatures:

```java
// 26.2 public API expected from the migration primer
PictureInPictureRenderer#renderToTexture(S, PoseStack, SubmitNodeCollector)
FeatureRenderDispatcher#renderAllFeatures(SubmitNodeStorage)
FeatureRenderDispatcher#prepareFrame(SubmitNodeStorage)
SubmitNodeCollector#submitCustomGeometry(PoseStack, RenderType, CustomGeometryRenderer)
RenderPipeline#getVertexFormatBinding(int)
RenderPipeline#getPrimitiveTopology()
GameRenderer#mainRenderTarget()
GameRenderer#mainCamera()
Minecraft#gui
Gui#screen()
```

Also inspect the current signatures for `RenderSystem.getDynamicUniforms`, `TransientMemory`, `GpuBufferSlice`, `RenderPassDescriptor`, `CommandEncoder#createRenderPass`, `RenderPass#setVertexBuffer`, and `RenderPass#drawIndexed`. Those methods changed during 26.2 development; the snippets below describe required ownership and ordering, not a substitute for those signatures.

## 1. World Multiblock Preview

### Files

- Modify `common/src/main/java/com/klikli_dev/modonomicon/client/render/MultiblockPreviewRenderer.java`.
- Add a focused common client renderer class only if geometry submission cannot remain readable in `MultiblockPreviewRenderer`. Suggested name: `MultiblockPreviewFeatureRenderer`.
- Do not change public methods used by Fabric/Forge/Neo event registrations: `extractRenderState`, `onRenderLevelLastEvent`, `onRenderHUD`, `onPlayerInteract`, and `onClientTick`.

### Replace removed direct geometry APIs

Replace all of these 26.1 APIs:

```java
pipeline.getVertexFormatMode();
pipeline.getVertexFormat();
vertexFormat.uploadImmediateVertexBuffer(...);
vertexFormat.uploadImmediateIndexBuffer(...);
VertexFormat.IndexType;
mc.getMainRenderTarget();
mc.gameRenderer.getMainCamera();
```

with their 26.2 equivalents:

```java
RenderPipeline pipeline = RenderTypes.translucentMovingBlock().pipeline();
VertexFormat vertexFormat = pipeline.getVertexFormatBinding(0);
PrimitiveTopology topology = pipeline.getPrimitiveTopology();
BufferBuilder builder = new BufferBuilder(BUFFER_BUILDER, topology, vertexFormat);

RenderTarget target = Minecraft.getInstance().gameRenderer.mainRenderTarget();
Vec3 cameraPos = Minecraft.getInstance().gameRenderer.mainCamera().position();
```

Use the actual resolved accessor names if the API gate finds a minor beta rename. Import `com.mojang.blaze3d.IndexType` rather than the removed `VertexFormat.IndexType`.

Keep `meshData.sortQuads(BUFFER_BUILDER, RenderSystem.getProjectionType().vertexSorting())` before uploading translucent geometry. Preserve the current dynamic transform contents, block atlas `Sampler0`, null `Sampler1`, lightmap `Sampler2`, and the target's color/depth attachments.

### Upload and draw model blocks through 26.2 buffers

Rewrite `uploadAndDraw` around 26.2 buffer ownership:

1. Obtain vertex binding zero from the pipeline and the mesh draw state's index type.
2. Allocate/copy the mesh vertex and optional index bytes through the public transient/staged-buffer API selected by the API gate. Do not cache a `GpuBuffer` or `GpuBufferSlice` past the command/frame lifetime.
3. For meshes without indices, use `RenderSystem.getSequentialBuffer(meshData.drawState().primitiveTopology())` or the exact renamed draw-state accessor and wrap/reference its resulting buffer as the `GpuBufferSlice` required by `RenderPass#setIndexBuffer`.
4. Create the render pass using the current `RenderPassDescriptor` / `CommandEncoder#createRenderPass` overload, passing `Optional.empty()` for no color clear and retaining the current depth attachment with no depth clear.
5. Bind pipeline, default uniforms, dynamic transforms, vertex binding `0`, index buffer/type, and all three samplers.
6. Call the five-argument 26.2 draw method in its required order:

```java
// indexCount, instanceCount, firstIndex, baseVertex, firstInstance
renderPass.drawIndexed(meshData.drawState().indexCount(), 1, 0, 0, 0);
```

The old `drawIndexed(0, 0, indexCount, 1)` ordering must not be retained.

### Submit preview block entities without MultiBufferSource

The existing `MultiBufferSource` ghost wrapper and locally constructed `FeatureRenderDispatcher` must be deleted. They are tied to the removed buffer-source architecture and must not be recreated through access widening.

Use a dedicated `SubmitNodeStorage` for this preview invocation. Submit each extracted `BlockEntityRenderState` to it with the same per-state translated pose and `CameraRenderState` used today. Render that storage using the public game renderer dispatcher API verified at the API gate:

```java
SubmitNodeStorage submits = new SubmitNodeStorage();
CameraRenderState camera = new CameraRenderState();

for (BlockEntityRenderState state : blockEntityRenderStates) {
    poseStack.pushPose();
    poseStack.translate(state.blockPos.getX(), state.blockPos.getY(), state.blockPos.getZ());
    blockEntityDispatcher.submit(state, poseStack, submits, camera);
    poseStack.popPose();
}

Minecraft.getInstance().gameRenderer.featureRenderDispatcher().renderAllFeatures(submits);
```

If only `prepareFrame(submits)` is public, use try-with-resources and execute the solid, translucent, translucent-after-terrain, and always-on-top phases in vanilla order before closing the prepared frame. This must happen at the current level-last callback so the preview remains rendered after the world.

The standard feature dispatcher cannot apply the old `GhostVertexConsumer` at dispatch time. Preserve ghost alpha by introducing a dedicated preview submission/feature renderer only if block-entity render states need alpha remapping. The renderer must:

- Implement a `SubmitNode` that captures a `BlockEntityRenderState`, pose, and alpha.
- Be a `RenderTypeFeatureRenderer` when its resolved 26.2 API supports it, obtaining a `VertexConsumer` for each render type and wrapping it in `GhostVertexConsumer`.
- Submit the node into the translucent phase, batch by `RenderType`, and use the existing `getGhostRenderType` mapping.
- Be registered with the actual `FeatureRenderDispatcher` via the supported NeoForge/Fabric extension point. Do not mutate its private renderer map or inject into its constructor unless no loader extension exists.

If the stock dispatcher supports a render-type transformation hook for block entity submissions, use that narrower hook instead and do not add a custom renderer. Either route must demonstrate that opaque block entities are submitted to a translucent render type and multiplied by alpha `0.6F` exactly once.

Do not construct a second `FeatureRenderDispatcher`, call removed `renderBuffers()`, or render/clear global feature storage. The preview storage must be disposed/cleared after its own render only.

### Regression checks

- A multiblock containing a normal cube, a cutout block, a translucent block, air expectation, and a block entity renders as a ghost in the world.
- Hovered expected blocks pulse and the HUD item name/progress remains correct.
- Completion increments once per tick, plays the orb sound at tick 14, and fades the HUD.
- A deliberately failing block-entity renderer logs once and does not prevent other preview blocks from rendering.

## 2. Picture-in-Picture Renderers

### Files

- Modify `common/src/main/java/com/klikli_dev/modonomicon/client/render/pip/GuiDirectEntryConnectionRenderer.java`.
- Modify `common/src/main/java/com/klikli_dev/modonomicon/client/render/pip/GuiMultiblockRenderer.java`.
- Modify PIP registration in:
  - `fabric/src/main/java/com/klikli_dev/modonomicon/ModonomiconFabricClient.java`
  - `forge/src/main/java/com/klikli_dev/modonomicon/ModonomiconForge.java`
  - `neo/src/main/java/com/klikli_dev/modonomicon/ModonomiconNeo.java` only if its registration signature changes.

### Direct-entry connections

Remove the constructor that takes `MultiBufferSource.BufferSource`, remove `super(bufferSource)`, and retain a public no-argument constructor (implicit is preferred). Implement the required 26.2 PIP method:

```java
@Override
protected void renderToTexture(
        GuiDirectEntryConnectionRenderState state,
        PoseStack poseStack,
        SubmitNodeCollector collector
) {
    collector.submitCustomGeometry(poseStack, RenderTypes.linesTranslucent(), (pose, consumer) -> {
        for (var connection : state.connections()) {
            drawLine(consumer, pose, connection, state.animationTime(), state.lineWidth(),
                    state.opacity(), state.brightness(), state.oscillationAmplitude(), state.oscillationSpeed());
        }
    });
}
```

Use the exact 26.2 `submitCustomGeometry` signature if it differs. The callback must use the `PoseStack.Pose` supplied by the collector, not a captured mutable pose, and all existing `drawLine`, `applyVisibility`, and `drawSegment` math remains unchanged.

Update registration:

```java
// Fabric
PictureInPictureRendererRegistry.register(ignored -> new GuiDirectEntryConnectionRenderer());

// Forge
event.register(new GuiDirectEntryConnectionRenderer());

// Neo
event.register(GuiDirectEntryConnectionRenderState.class, GuiDirectEntryConnectionRenderer::new);
```

Adapt only the lambda/factory parameter required by each loader's resolved registration event. Do not pass `ctx.bufferSource()` or `event.getBufferSource()`.

### Book multiblock PIP

Remove the `bufferSource` field and constructor from `GuiMultiblockRenderer`. The PIP method already receives a `SubmitNodeCollector`; use it for every block and block entity.

For block models, retain the current `ModelBlockRenderer.tesselateBlock` inputs and submit the final vertex construction through custom geometry:

```java
collector.submitCustomGeometry(poseStack, renderType, (pose, consumer) -> {
    BlockQuadOutput output = (_, _, _, quad, instance) -> {
        instance.setLightCoords(lightCoords);
        consumer.putBakedQuad(pose, quad, instance);
    };
    blockRenderer.tesselateBlock(output, 0, 0, 0, ghostState, pos, state, model, 0);
});
```

Use a fresh/correctly scoped `ModelBlockRenderer` with the current ambient-occlusion option and block colors. Do not retain a `VertexConsumer` between render calls. Preserve `Sheets.translucentBlockItemSheet()` for translucent quads and `Sheets.cutoutBlockItemSheet()` otherwise.

Replace the removed lighting calls with `LightCoordsUtil`:

```java
int lightCoords = LightCoordsUtil.getLightCoords(level, pos);
```

For the overload that previously passed `LevelRenderer.BrightnessGetter.DEFAULT`, inspect the new `LightCoordsUtil` overload and pass its default brightness implementation. Do not substitute `FULL_BRIGHT`: book PIP currently uses calculated lighting.

For fake block entities, keep extraction inside the existing `try/catch`, then submit to the PIP collector:

```java
renderer.extractRenderState(blockEntity, renderState, ClientTicks.partialTicks, eye3, null);
renderState.lightCoords = LightCoordsUtil.getLightCoords(level, blockEntityPos);
blockEntityDispatcher.submit(renderState, poseStack, collector, new CameraRenderState());
```

Do not acquire `Minecraft.gameRenderer`'s feature dispatcher and do not call `renderAllFeatures` inside `renderToTexture`. `PictureInPictureRenderer` owns rendering the collected nodes into its texture after this method returns.

### Regression checks

- Open a book page with a multiblock and verify centering, rotation, Shift pause, clipping, scale-to-fit, and material selection.
- Verify a PIP multiblock containing a block entity does not appear in the main world or disappear after the PIP is rendered.
- Verify direct-entry connection lines retain their endpoints, width, wiggle, fade, opacity, and brightness at multiple GUI scales.

## 3. GUI, Toast, and Text Color API Replacements

### Files and exact replacements

1. `common/src/main/java/com/klikli_dev/modonomicon/api/datagen/ModonomiconProviderBase.java`

`ChatFormatting#getColor()` is removed. Its colors are now represented through `Style`/`TextColor`. Use the resolved public `ChatFormatting` color accessor, normally `color.getColor()` replacement `color.getColorValue()` or `color.getColor().getValue()` depending on the final 26.2 source. The implementation must reject non-color formats exactly as the old nullable result did, if this helper can receive `BOLD`, `ITALIC`, or `RESET`.

Target shape:

```java
protected String color(String text, ChatFormatting formatting) {
    Integer rgb = /* resolved 26.2 formatting color accessor */;
    if (rgb == null) {
        throw new IllegalArgumentException("Formatting does not define a color: " + formatting);
    }
    return this.color(text, rgb);
}
```

Before adding the exception, search call sites and confirm only color enum constants are passed. If the existing method deliberately supported non-color formats, preserve its prior null behavior instead.

2. `common/src/main/java/com/klikli_dev/modonomicon/book/runtime/DemoRuntimeBookContent.java`

`Items.LIGHTNING_ROD` is now a `WeatheringCopperCollection<Item>`. Use the unweathered entry explicitly, normally:

```java
new ItemStackTemplate(Items.LIGHTNING_ROD.getUnweathered())
```

Verify the exact accessor (`getUnweathered`, `unaffected`, or equivalent) in resolved sources. Do not select a weathered or waxed variant: the runtime API example must retain the normal lightning-rod icon.

3. `forge/src/main/java/com/klikli_dev/modonomicon/gui/ForgeGuiHelper.java` and `neo/src/main/java/com/klikli_dev/modonomicon/gui/NeoGuiHelper.java`

Replace:

```java
return Minecraft.getInstance().screen;
```

with:

```java
return Minecraft.getInstance().gui.screen();
```

This matches the existing Fabric implementation and preserves `BookGuiManager` screen identity comparisons.

4. `common/src/main/java/com/klikli_dev/modonomicon/networking/ResearchToastMessage.java`

Replace the removed `Minecraft#getToastManager` call with the resolved 26.2 GUI-owned toast accessor, expected to be:

```java
var toastManager = minecraft.gui.getToastManager();
```

Confirm whether the final source uses `toastManager()` instead. Preserve the configuration early return and call `addToast` once for every defined trigger; do not coalesce the trigger list or alter title/description resolution.

## 4. Item Tag Datagen

### Files

- Modify `neo/src/main/java/com/klikli_dev/modonomicon/datagen/ItemTagsProvider.java`.
- Modify `forge/src/main/java/com/klikli_dev/modonomicon/datagen/ItemTagsProvider.java` only to remove the stale `IntrinsicHolderTagsProvider` import.

### NeoForge implementation

`IntrinsicHolderTagsProvider` was removed. Replace it with the standard `TagsProvider<Item>` (or the exact NeoForge 26.2 item-tag base found during the API gate). Supply the item registry key in the superclass constructor and add resource keys rather than runtime item instances:

```java
public final class ItemTagsProvider extends TagsProvider<Item> {
    public ItemTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(output, Registries.ITEM, lookupProvider, Modonomicon.MOD_ID);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        this.tag(ItemTags.BOOKSHELF_BOOKS).add(ItemRegistry.MODONOMICON.getResourceKey());
        this.tag(ItemTags.LECTERN_BOOKS).add(ItemRegistry.MODONOMICON.getResourceKey());
    }
}
```

Use `ItemRegistry.MODONOMICON.getResourceKey()` as Fabric already does. Verify the exact `TagsProvider` constructor because NeoForge may provide an `ExistingFileHelper` parameter or a distinct item provider class. Do not retain `builtInRegistryHolder().key()`; the point of the 26.2 tag-provider migration is to avoid runtime registry-object access.

## 5. Access Rules

No new access widener or access transformer entry is planned.

The existing access rules for `RenderType.state` and `RenderSetup.textures` remain necessary for `getGhostRenderType`. The migration must not add access to `FeatureRenderDispatcher` fields, `SubmitNodeStorage` internals, `PictureInPictureRenderer` internals, or GPU backend classes.

If the API verification gate finds that one required public behavior has no public route:

1. Prefer a supported NeoForge/Fabric event or extension point.
2. If no extension point exists, add exactly one documented access rule for the smallest necessary member in all three files:
   - `common/src/main/resources/modonomicon.accesswidener`
   - `neo/src/main/resources/META-INF/accesstransformer.cfg`
   - `forge/src/main/resources/META-INF/accesstransformer.cfg`
3. Use Mojmaps names in the access widener and Neo transformer, and the resolved SRG name in Forge's transformer.
4. Add a comment explaining why the public API cannot implement the behavior and link the relevant upstream issue/API signature.

Do not add an access rule merely to preserve a 26.1 implementation strategy.

## 6. Execution Order and Validation

Implement and validate in this order:

1. Apply the non-rendering replacements and tag-provider migration.
2. Migrate direct-entry PIP submission and registration.
3. Migrate multiblock PIP submission, including fake block entities.
4. Migrate world block geometry to 26.2 buffers/render pass.
5. Migrate world block-entity submission and ghost alpha handling.
6. Remove imports, stale constructors, and obsolete access rules only after all consumers compile.

Run after each numbered step:

```powershell
.\gradlew.bat compileJava
```

After compilation succeeds:

```powershell
.\gradlew.bat runData
.\gradlew.bat runClient
```

Run the client validation on every enabled loader target. Perform the rendering checks above with the default backend and Vulkan if available. Capture any backend-specific issue with the render type, feature phase, and affected block/entity type before changing rendering order or alpha values.
