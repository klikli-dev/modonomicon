/*
 * SPDX-FileCopyrightText: 2022 Authors of Patchouli
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.client.render;

import com.klikli_dev.modonomicon.Modonomicon;
import com.klikli_dev.modonomicon.api.ModonomiconConstants;
import com.klikli_dev.modonomicon.api.multiblock.Multiblock;
import com.klikli_dev.modonomicon.api.multiblock.MultiblockPreviewData;
import com.klikli_dev.modonomicon.client.ClientTicks;
import com.klikli_dev.modonomicon.multiblock.AbstractMultiblock;
import com.klikli_dev.modonomicon.multiblock.matcher.DisplayOnlyMatcher;
import com.klikli_dev.modonomicon.multiblock.matcher.Matchers;
import com.klikli_dev.modonomicon.util.GuiGraphicsExt;
import com.klikli_dev.modonomicon.util.RenderUtil;
import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.pipeline.ColorTargetState;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.FilterMode;
import com.mojang.blaze3d.vertex.*;
import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.SubmitNodeStorage;
import net.minecraft.client.renderer.block.BlockQuadOutput;
import net.minecraft.client.renderer.block.ModelBlockRenderer;
import net.minecraft.client.renderer.block.model.BlockModelPart;
import net.minecraft.client.renderer.block.model.BlockStateModel;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.feature.FeatureRenderDispatcher;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.client.renderer.state.LevelRenderState;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import net.minecraft.client.renderer.MultiBufferSource;
import org.jspecify.annotations.NonNull;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.function.Function;

/**
 * Handles rendering of the multiblock preview in the world as semi-transparent blocks.
 * See https://github.com/XFactHD/FramedBlocks/blob/26.1/src/main/java/io/github/xfacthd/framedblocks/client/render/special/GhostBlockRenderer.java for the architecture we used for 26.1.
 */
public class MultiblockPreviewRenderer {

    private static final Map<BlockPos, BlockEntity> blockEntityCache = new Object2ObjectOpenHashMap<>();
    private static final Set<BlockEntity> erroredBlockEntities = Collections.newSetFromMap(new WeakHashMap<>());
    private static final List<BlockEntityRenderState> blockEntityRenderStates = new ArrayList<>();
    private static final RandomSource RANDOM = RandomSource.create();
    private static final ObjectArrayList<BlockModelPart> PART_SCRATCH_LIST = new ObjectArrayList<>();
    private static final ByteBufferBuilder BUFFER_BUILDER = new ByteBufferBuilder(RenderType.TRANSIENT_BUFFER_SIZE);

    private static final Map<RenderType, RenderType> GHOST_RENDER_TYPE_CACHE = new java.util.IdentityHashMap<>();

    private static RenderType getGhostRenderType(RenderType original) {
        if (original.pipeline().getColorTargetState().blendFunction().isPresent()) {
            return original;
        }

        return GHOST_RENDER_TYPE_CACHE.computeIfAbsent(original, rt -> {

            if(rt.hasBlending())
                return rt;

            //should never happen, but if there is some weird custom stuff going on we just not ghost it
            if(rt.state.textures.isEmpty())
                return rt;

            var sampler0 = rt.state.textures.get("Sampler0");

            //again, should not happen, but non-vanilla RTs might do whatever.
            //we could fall back onto any other texture, but let's only do that if something concrete is reported.
            if(sampler0 == null)
                return rt;

            return RenderTypes.entityTranslucent(sampler0.location());
        });
    }

    public static boolean hasMultiblock;
    private static Multiblock multiblock;
    private static Component name;
    private static BlockPos pos;
    private static boolean isAnchored;
    private static Rotation facingRotation;
    private static Function<BlockPos, BlockPos> offsetApplier;
    private static int blocks, blocksDone, airFilled;
    private static int timeComplete;
    private static BlockState lookingState;
    private static BlockPos lookingPos;

    public static void setMultiblock(Multiblock multiblock, Component name, boolean flip) {
        setMultiblock(multiblock, name, flip, pos -> pos);
    }

    public static void setMultiblock(Multiblock multiblock, Component name, boolean flip, Function<BlockPos, BlockPos> offsetApplier) {
        if (flip && hasMultiblock && MultiblockPreviewRenderer.multiblock == multiblock) {
            hasMultiblock = false;
        } else {
            MultiblockPreviewRenderer.multiblock = multiblock;
            MultiblockPreviewRenderer.blockEntityCache.clear();
            MultiblockPreviewRenderer.erroredBlockEntities.clear();
            MultiblockPreviewRenderer.name = name;
            MultiblockPreviewRenderer.offsetApplier = offsetApplier;
            pos = null;
            hasMultiblock = multiblock != null;
            isAnchored = false;
        }
    }

    public static void onRenderHUD(GuiGraphics guiGraphics, float partialTicks) {
        if (hasMultiblock) {
            int waitTime = 40;
            int fadeOutSpeed = 4;
            int fullAnimTime = waitTime + 10;
            float animTime = timeComplete + (timeComplete == 0 ? 0 : partialTicks);

            if (animTime > fullAnimTime) {
                hasMultiblock = false;
                return;
            }

            guiGraphics.pose().pushMatrix();
            guiGraphics.pose().translate(0, -Math.max(0, animTime - waitTime) * fadeOutSpeed);

            Minecraft mc = Minecraft.getInstance();
            int x = mc.getWindow().getGuiScaledWidth() / 2;
            int y = 12;

            GuiGraphicsExt.drawString(guiGraphics, mc.font, name, x - mc.font.width(name) / 2.0F, y, -1, false);

            int width = 180;
            int height = 9;
            int left = x - width / 2;
            int top = y + 10;

            if (timeComplete > 0) {
                String s = I18n.get(ModonomiconConstants.I18n.Multiblock.COMPLETE);
                guiGraphics.pose().pushMatrix();
                guiGraphics.pose().translate(0, Math.min(height + 5, animTime));
                guiGraphics.drawString(mc.font, s, (int) (x - mc.font.width(s) / 2.0F), top + height - 10, 0x00FF00, false);
                guiGraphics.pose().popMatrix();
            }

            //render a black square at the "bottom", 1px larger than the actual progress bar, so it acts as a border
            guiGraphics.fill(left - 1, top - 1, left + width + 1, top + height + 1, 0xFF000000);

            //then, on top of that, render a gray gradient as "empty progress"
            guiGraphics.fillGradient(left, top, left + width, top + height, 0xFF666666, 0xFF666666);

            float fract = (float) blocksDone / Math.max(1, blocks);
            int progressWidth = (int) ((float) width * fract);
            int color = Mth.hsvToRgb(fract / 3.0F, 1.0F, 1.0F) | 0xFF000000;
            int color2 = new Color(color).darker().getRGB();

            //finally, on top of that, render a colored gradient as "filled progress"
            guiGraphics.fillGradient(left, top, left + progressWidth, top + height, color, color2);

            if (!isAnchored) {
                String s = I18n.get(ModonomiconConstants.I18n.Multiblock.NOT_ANCHORED);
                guiGraphics.drawString(mc.font, s, (int) (x - mc.font.width(s) / 2.0F), top + height + 8, 0xFFFFFF, false);
            } else {
                if (lookingState != null) {
                    // try-catch around here because the state isn't necessarily present in the world in this instance,
                    // which isn't really expected behavior for getPickBlock
                    try {
                        var stack = lookingState.getCloneItemStack(mc.level, lookingPos, true);
                        if (!stack.isEmpty()) {
                            guiGraphics.drawString(mc.font, stack.getHoverName(), left + 20, top + height + 8, 0xFFFFFF, false);

                            guiGraphics.renderItem(stack, left, top + height + 2);
                        }
                    } catch (Exception ignored) {
                    }
                }

                if (timeComplete == 0) {
                    color = 0xFFFFFF;
                    int posx = left + width;
                    int posy = top + height + 2;
                    float mult = 1;
                    String progress = blocksDone + "/" + blocks;

                    if (blocksDone == blocks && airFilled > 0) {
                        progress = I18n.get(ModonomiconConstants.I18n.Multiblock.REMOVE_BLOCKS);
                        color = 0xDA4E3F;
                        mult *= 2;
                        posx -= width / 2;
                        posy += 2;
                    }

                    guiGraphics.drawString(mc.font, progress, (int) (posx - mc.font.width(progress) / mult), posy, color, true);
                }
            }

            guiGraphics.pose().popMatrix();
        }
    }

    public static void onRenderLevelLastEvent(LevelRenderState levelRenderState, PoseStack poseStack) {
        if (hasMultiblock && multiblock != null) {
            renderMultiblock(levelRenderState, poseStack);
        }
    }

    public static void anchorTo(BlockPos target, Rotation rot) {
        pos = target;
        facingRotation = rot;
        isAnchored = true;
    }

    public static InteractionResult onPlayerInteract(Player player, Level world, InteractionHand hand, BlockHitResult hit) {
        if (hasMultiblock && !isAnchored && player == Minecraft.getInstance().player) {
            anchorTo(hit.getBlockPos(), getRotation(player));
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }

    public static void onClientTick(Minecraft mc) {
        if (Minecraft.getInstance().level == null) {
            hasMultiblock = false;
        } else if (isAnchored && blocks == blocksDone && airFilled == 0) {
            timeComplete++;
            if (timeComplete == 14) {
                Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.EXPERIENCE_ORB_PICKUP, 1.0F));
            }
        } else {
            timeComplete = 0;
        }
    }

    /**
     * Phase 1: Extract render state from the level.
     * This should be called during the render state extraction phase.
     *
     * @param levelRenderState The level render state to extract data into
     */
    public static void extractRenderState(LevelRenderState levelRenderState) {
        if (!hasMultiblock || multiblock == null) {
            return;
        }

        blockEntityRenderStates.clear();

        Minecraft mc = Minecraft.getInstance();
        Level level = mc.level;
        if (level == null) {
            return;
        }

        if (!isAnchored) {
            facingRotation = getRotation(mc.player);
            if (mc.hitResult instanceof BlockHitResult) {
                pos = ((BlockHitResult) mc.hitResult).getBlockPos();
            }
        } else if (pos.distToCenterSqr(mc.player.position()) > 64 * 64) {
            return;
        }

        if (pos == null) {
            return;
        }
        if (multiblock.isSymmetrical()) {
            facingRotation = Rotation.NONE;
        }

        multiblock.setLevel(level);

        // Extract block entity render states from the simulated multiblock
        BlockPos startPos = getStartPos();
        Pair<BlockPos, Collection<Multiblock.SimulateResult>> sim = multiblock.simulate(level, startPos, getFacingRotation(), true, false);
        for (Multiblock.SimulateResult r : sim.getSecond()) {
            try {
                BlockState displayedState = r.stateMatcher().getDisplayedState(ClientTicks.ticks).rotate(facingRotation);

                if (displayedState.getBlock() instanceof EntityBlock eb) {
                    // Cache/create a fake block entity at the simulated position (translate by startPos)
                    var cacheKey = r.worldPosition().subtract(startPos).immutable();
                    var be = blockEntityCache.compute(cacheKey, (p, cachedBe) -> {
                        if (cachedBe != null && !cachedBe.getType().isValid(displayedState)) {
                            return eb.newBlockEntity(p, displayedState);
                        }
                        return cachedBe != null ? cachedBe : eb.newBlockEntity(p, displayedState);
                    });

                    if (be != null && !erroredBlockEntities.contains(be)) {
                        be.setLevel(mc.level);
                        // Provide the displayed state to avoid querying the real world
                        be.setBlockState(displayedState);

                        var dispatcher = Minecraft.getInstance().getBlockEntityRenderDispatcher();
                        var renderer = dispatcher.getRenderer(be);
                        if (renderer != null) {
                            var renderState = renderer.createRenderState();
                            var eye = mc.getEntityRenderDispatcher().camera.position();
                            eye = eye.subtract(startPos.getX(), startPos.getY(), startPos.getZ());

                            //Note: we cannot use Minecraft.getInstance().getBlockEntityRenderDispatcher().tryExtractRenderState because that takes the camera eye position of the in-world camera, but our multiblock exists in a virtual level close to 0 0 0
                            renderer.extractRenderState(be, renderState, ClientTicks.partialTicks, eye, null);
                            renderState.blockPos = r.worldPosition();
                            blockEntityRenderStates.add(renderState);
                        }
                    }
                }
            } catch (Exception e) {
                // Don't let a failure extracting one block entity abort the entire extraction loop
                Modonomicon.LOG.error("Error extracting block entity render state", e);
            }
        }
    }


    /**
     * Phase 2: Render the multiblock using the extracted render state.
     * This should be called during the actual rendering phase with the levelRenderState.
     *
     * @param levelRenderState The level render state containing extracted data
     * @param ms               The pose stack for rendering
     */
    public static void renderMultiblock(LevelRenderState levelRenderState, PoseStack ms) {
        if (!hasMultiblock || multiblock == null) {
            return;
        }

        Minecraft mc = Minecraft.getInstance();
        Level level = mc.level;
        if (level == null) {
            return;
        }

        if (!isAnchored) {
            if (mc.hitResult instanceof BlockHitResult) {
                pos = ((BlockHitResult) mc.hitResult).getBlockPos();
            }
        } else if (pos.distToCenterSqr(mc.player.position()) > 64 * 64) {
            return;
        }

        if (pos == null) {
            return;
        }

        RenderPipeline pipeline = RenderPipelines.TRANSLUCENT_MOVING_BLOCK;
        BufferBuilder buffer = new BufferBuilder(BUFFER_BUILDER, pipeline.getVertexFormatMode(), pipeline.getVertexFormat());
        ModelBlockRenderer blockRenderer = new ModelBlockRenderer(mc.options.ambientOcclusion().get(), false, mc.getBlockColors());

        BlockPos checkPos = null;
        if (mc.hitResult instanceof BlockHitResult blockRes) {
            checkPos = blockRes.getBlockPos().relative(blockRes.getDirection());
        }

        BlockPos startPos = getStartPos();

        Pair<BlockPos, Collection<Multiblock.SimulateResult>> sim = multiblock.simulate(level, startPos, getFacingRotation(), true, false);
        for (Multiblock.SimulateResult r : sim.getSecond()) {
            float alpha = 0.3F;
            if (r.worldPosition().equals(checkPos)) {
                alpha = 0.6F + (float) (Math.sin(ClientTicks.total * 0.3F) + 1F) * 0.1F;
            }

            if (!r.stateMatcher().equals(Matchers.ANY) && r.stateMatcher().getType() != DisplayOnlyMatcher.TYPE) {
                boolean air = !r.stateMatcher().countsTowardsTotalBlocks();

                if (!r.test(level, facingRotation)) {
                    BlockState displayedState = r.stateMatcher().getDisplayedState(ClientTicks.ticks).rotate(facingRotation);
                    renderBlock(level, displayedState, r.worldPosition(), multiblock, air, alpha, blockRenderer, ms, buffer);
                }
            }
        }

        MeshData meshData = buffer.build();
        if (meshData != null) {
            try (meshData) {
                uploadAndDraw(pipeline, mc.getMainRenderTarget(), meshData);
            }
        }

        // Render block entities using the extracted render states
        EntityRenderDispatcher erd = mc.getEntityRenderDispatcher();
        double renderPosX = erd.camera.position().x();
        double renderPosY = erd.camera.position().y();
        double renderPosZ = erd.camera.position().z();
        ms.pushPose();
        ms.translate(-renderPosX, -renderPosY, -renderPosZ);

        var dispatcher = Minecraft.getInstance().getBlockEntityRenderDispatcher();
        var featureDispatcher = Minecraft.getInstance().gameRenderer.getFeatureRenderDispatcher();
        var originalBufferSource = Minecraft.getInstance().renderBuffers().bufferSource();

        int ghostAlpha = (int) (0.6f * 255);
        var ghostBufferSource = new MultiBufferSource.BufferSource(originalBufferSource.sharedBuffer, originalBufferSource.fixedBuffers) {
            @Override
            public @NonNull VertexConsumer getBuffer(@NonNull RenderType renderType) {
                var ghostType = getGhostRenderType(renderType);
                return new GhostVertexConsumer(originalBufferSource.getBuffer(ghostType), ghostAlpha);
            }
        };

        var customSubmitStorage = new SubmitNodeStorage();
        var ghostFeatureDispatcher = new FeatureRenderDispatcher(
                customSubmitStorage,
                Minecraft.getInstance().getBlockRenderer(),
                ghostBufferSource,
                Minecraft.getInstance().getAtlasManager(),
                Minecraft.getInstance().renderBuffers().outlineBufferSource(),
                Minecraft.getInstance().renderBuffers().crumblingBufferSource(),
                Minecraft.getInstance().font
        );

        for (var blockEntityRenderState : blockEntityRenderStates) {
            ms.pushPose();

            ms.translate(blockEntityRenderState.blockPos.getX(),
                    blockEntityRenderState.blockPos.getY(),
                    blockEntityRenderState.blockPos.getZ());

            var cameraRenderState = new CameraRenderState();
            dispatcher.submit(blockEntityRenderState, ms, ghostFeatureDispatcher.getSubmitNodeStorage(), cameraRenderState);
            ghostFeatureDispatcher.renderAllFeatures();

            ms.popPose();
        }

        ghostFeatureDispatcher.close(); // Clean up if required
        blockEntityRenderStates.clear();

        ms.popPose();
    }

    public static void renderBlock(Level world, BlockState state, BlockPos pos, Multiblock multiblock, boolean isAir, float alpha, ModelBlockRenderer blockRenderer, PoseStack poseStack, BufferBuilder buffer) {
        if (pos == null) return;

        Minecraft mc = Minecraft.getInstance();
        Vec3 cameraPos = mc.gameRenderer.getMainCamera().position();
        Vec3 offset = Vec3.atLowerCornerOf(pos).subtract(cameraPos);

        VertexConsumer consumer = new GhostVertexConsumer(buffer, (int) (alpha * 255.0f));
        BlockQuadOutput output = (levelIn, stateIn, posIn, quad, instance) ->
                consumer.putBakedQuad(poseStack.last(), quad, instance);

        poseStack.pushPose();
        poseStack.translate(offset.x + .5, offset.y + .5, offset.z + .5);

        if (state.getBlock() == Blocks.AIR) {
            float scale = 0.3F;
            poseStack.scale(scale, scale, scale);
            state = Blocks.RED_CONCRETE.defaultBlockState();
        } else {
            poseStack.scale(1.0001F, 1.0001F, 1.0001F);
        }

        poseStack.translate(-.5F, -.5F, -.5F);

        BlockStateModel model = mc.getBlockRenderer().getBlockModel(state);
        model.collectParts(RANDOM, PART_SCRATCH_LIST);
        blockRenderer.tesselateBlock(output, 0, 0, 0, multiblock, pos, state, model, 0);
        PART_SCRATCH_LIST.clear();

        poseStack.popPose();
    }

    private static void uploadAndDraw(RenderPipeline pipeline, RenderTarget target, MeshData meshData) {
        meshData.sortQuads(BUFFER_BUILDER, RenderSystem.getProjectionType().vertexSorting());
        VertexFormat vertexFormat = pipeline.getVertexFormat();
        GpuBuffer vertexBuffer = vertexFormat.uploadImmediateVertexBuffer(meshData.vertexBuffer());
        GpuBuffer indexBuffer;
        VertexFormat.IndexType indexType;

        if (meshData.indexBuffer() != null) {
            indexBuffer = vertexFormat.uploadImmediateIndexBuffer(meshData.indexBuffer());
            indexType = meshData.drawState().indexType();
        } else {
            RenderSystem.AutoStorageIndexBuffer autoIndexBuffer = RenderSystem.getSequentialBuffer(meshData.drawState().mode());
            indexBuffer = autoIndexBuffer.getBuffer(meshData.drawState().indexCount());
            indexType = autoIndexBuffer.type();
        }

        GpuBufferSlice dynamicUniforms = RenderSystem.getDynamicUniforms()
                .writeTransform(
                        RenderSystem.getModelViewMatrix(),
                        new Vector4f(1.0F, 1.0F, 1.0F, 1.0F),
                        new Vector3f(),
                        new Matrix4f()
                );

        try (RenderPass renderPass = RenderSystem.getDevice().createCommandEncoder().createRenderPass(
                () -> "modonomicon_multiblock_preview",
                target.getColorTextureView(),
                java.util.OptionalInt.empty(),
                target.getDepthTextureView(),
                java.util.OptionalDouble.empty()
        )) {
            renderPass.setPipeline(pipeline);
            RenderSystem.bindDefaultUniforms(renderPass);
            renderPass.setUniform("DynamicTransforms", dynamicUniforms);
            renderPass.setVertexBuffer(0, vertexBuffer);
            renderPass.setIndexBuffer(indexBuffer, indexType);

            renderPass.bindTexture("Sampler0", Minecraft.getInstance().getTextureManager().getTexture(TextureAtlas.LOCATION_BLOCKS).getTextureView(), RenderTypes.MOVING_BLOCK_SAMPLER.get());
            renderPass.bindTexture("Sampler1", null, null);
            renderPass.bindTexture("Sampler2", Minecraft.getInstance().gameRenderer.lightmap(), RenderSystem.getSamplerCache().getClampToEdge(FilterMode.LINEAR));

            renderPass.drawIndexed(0, 0, meshData.drawState().indexCount(), 1);
        }
    }

    @Nullable
    public static MultiblockPreviewData getMultiblockPreviewData() {
        if (!hasMultiblock) {
            return null;
        }
        return new MultiblockPreviewData(multiblock, pos, facingRotation, isAnchored);
    }

    public static Multiblock getMultiblock() {
        return multiblock;
    }

    public static boolean isAnchored() {
        return isAnchored;
    }

    public static Rotation getFacingRotation() {
        return multiblock.isSymmetrical() ? Rotation.NONE : facingRotation;
    }

    public static BlockPos getStartPos() {
        return offsetApplier.apply(pos);
    }

    /**
     * Returns the Rotation of a multiblock structure based on the given entity's facing direction.
     */
    private static Rotation getRotation(Entity entity) {
        return AbstractMultiblock.rotationFromFacing(entity.getDirection());
    }
}
