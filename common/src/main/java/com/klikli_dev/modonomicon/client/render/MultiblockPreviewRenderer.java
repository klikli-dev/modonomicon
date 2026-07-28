/*
 * SPDX-FileCopyrightText: 2022 Authors of Patchouli
 * SPDX-FileCopyrightText: 2022 klikli-dev
 * SPDX-FileCopyrightText: 2026 XFactHD
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.client.render;

import com.klikli_dev.modonomicon.Modonomicon;
import com.klikli_dev.modonomicon.api.ModonomiconConstants;
import com.klikli_dev.modonomicon.api.multiblock.Multiblock;
import com.klikli_dev.modonomicon.api.multiblock.MultiblockPreviewData;
import com.klikli_dev.modonomicon.client.ClientTicks;
import com.klikli_dev.modonomicon.client.render.fakelevel.GhostRenderState;
import com.klikli_dev.modonomicon.multiblock.AbstractMultiblock;
import com.klikli_dev.modonomicon.multiblock.matcher.DisplayOnlyMatcher;
import com.klikli_dev.modonomicon.multiblock.matcher.Matchers;
import com.klikli_dev.modonomicon.registry.StateMatcherTypeRegistry;
import com.klikli_dev.modonomicon.util.TextRenderHelper;
import com.mojang.blaze3d.vertex.*;
import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.SubmitNodeStorage;
import net.minecraft.client.renderer.block.BlockQuadOutput;
import net.minecraft.client.renderer.block.ModelBlockRenderer;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.state.level.LevelRenderState;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.LightCoordsUtil;
import net.minecraft.util.Mth;
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
    public static boolean hasMultiblock;
    private static Multiblock multiblock;
    private static Component name;
    private static BlockPos pos;
    private static boolean isAnchored;
    private static Rotation facingRotation = Rotation.NONE;
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

    public static void onRenderHUD(GuiGraphicsExtractor guiGraphics, float partialTicks) {
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

            TextRenderHelper.drawString(guiGraphics, mc.font, name, x - mc.font.width(name) / 2.0F, y, -1, false);

            int width = 180;
            int height = 9;
            int left = x - width / 2;
            int top = y + 10;

            if (timeComplete > 0) {
                String s = I18n.get(ModonomiconConstants.I18n.Multiblock.COMPLETE);
                guiGraphics.pose().pushMatrix();
                guiGraphics.pose().translate(0, Math.min(height + 5, animTime));
                guiGraphics.text(mc.font, s, (int) (x - mc.font.width(s) / 2.0F), top + height - 10, 0xFF00FF00, false);
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
                guiGraphics.text(mc.font, s, (int) (x - mc.font.width(s) / 2.0F), top + height + 8, 0xFFFFFFFF, false);
            } else {
                if (lookingState != null) {
                    // try-catch around here because the state isn't necessarily present in the world in this instance,
                    // which isn't really expected behavior for getPickBlock
                    try {
                        var stack = lookingState.getCloneItemStack(mc.level, lookingPos, true);
                        if (!stack.isEmpty()) {
                            guiGraphics.text(mc.font, stack.getHoverName().copy(), left + 20, top + height + 8, 0xFFFFFFFF, false);

                            guiGraphics.item(stack, left, top + height + 2);
                        }
                    } catch (Exception ignored) {
                    }
                }

                if (timeComplete == 0) {
                    color = 0xFFFFFFFF;
                    int posx = left + width;
                    int posy = top + height + 2;
                    float mult = 1;
                    String progress = blocksDone + "/" + blocks;

                    if (blocksDone == blocks && airFilled > 0) {
                        progress = I18n.get(ModonomiconConstants.I18n.Multiblock.REMOVE_BLOCKS);
                        color = 0xFFDA4E3F;
                        mult *= 2;
                        posx -= width / 2;
                        posy += 2;
                    }

                    guiGraphics.text(mc.font, progress, (int) (posx - mc.font.width(progress) / mult), posy, color, true);
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
                            renderState.lightCoords = LightCoordsUtil.FULL_BRIGHT;
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

        ModelBlockRenderer blockRenderer = new ModelBlockRenderer(mc.options.ambientOcclusion().get(), false, mc.getBlockColors());
        SubmitNodeStorage submitNodeStorage = new SubmitNodeStorage();

        BlockPos checkPos = null;
        if (mc.hitResult instanceof BlockHitResult blockRes) {
            checkPos = blockRes.getBlockPos().relative(blockRes.getDirection());
        }

        BlockPos startPos = getStartPos();

        blocks = blocksDone = airFilled = 0;
        lookingState = null;
        lookingPos = checkPos;

        Pair<BlockPos, Collection<Multiblock.SimulateResult>> sim = multiblock.simulate(level, startPos, getFacingRotation(), true, false);
        for (Multiblock.SimulateResult r : sim.getSecond()) {
            float alpha = 0.3F;
            if (r.worldPosition().equals(checkPos)) {
                lookingState = r.stateMatcher().getDisplayedState(ClientTicks.ticks);
                alpha = 0.6F + (float) (Math.sin(ClientTicks.total * 0.3F) + 1F) * 0.1F;
            }

            if (!r.stateMatcher().equals(Matchers.ANY) && r.stateMatcher().type() != StateMatcherTypeRegistry.DISPLAY) {
                boolean air = !r.stateMatcher().countsTowardsTotalBlocks();
                if (!air) {
                    blocks++;
                }

                if (!r.test(level, facingRotation)) {
                    BlockState displayedState = r.stateMatcher().getDisplayedState(ClientTicks.ticks).rotate(facingRotation);
                    renderBlock(level, displayedState, r.worldPosition(), alpha, blockRenderer, ms, submitNodeStorage);

                    if (air) {
                        airFilled++;
                    }
                } else if (!air) {
                    blocksDone++;
                }
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
        var cameraRenderState = new CameraRenderState();

        for (var blockEntityRenderState : blockEntityRenderStates) {
            ms.pushPose();

            ms.translate(blockEntityRenderState.blockPos.getX(),
                    blockEntityRenderState.blockPos.getY(),
                    blockEntityRenderState.blockPos.getZ());

            dispatcher.submit(blockEntityRenderState, ms, submitNodeStorage, cameraRenderState);

            ms.popPose();
        }

        mc.gameRenderer.featureRenderDispatcher().renderAllFeatures(submitNodeStorage);

        ms.popPose();

        if (!isAnchored) {
            blocks = blocksDone = 0;
        }
    }

    public static void renderBlock(Level world, BlockState state, BlockPos pos, float alpha, ModelBlockRenderer blockRenderer, PoseStack poseStack, SubmitNodeStorage submitNodeStorage) {
        if (pos == null) return;

        Minecraft mc = Minecraft.getInstance();
        if (!(world instanceof ClientLevel clientLevel)) {
            return;
        }
        Vec3 cameraPos = mc.gameRenderer.mainCamera().position();
        Vec3 offset = Vec3.atLowerCornerOf(pos).subtract(cameraPos);

        poseStack.pushPose();
        poseStack.translate(offset.x + .5, offset.y + .5, offset.z + .5);

        if (state.getBlock() == Blocks.AIR) {
            float scale = 0.3F;
            poseStack.scale(scale, scale, scale);
            state = Blocks.CONCRETE.red().defaultBlockState();
        } else {
            poseStack.scale(1.0001F, 1.0001F, 1.0001F);
        }

        poseStack.translate(-.5F, -.5F, -.5F);

        BlockStateModel model = mc.getModelManager().getBlockStateModelSet().get(state);
        GhostRenderState renderState = new GhostRenderState(clientLevel, pos, state);
        BlockState renderedState = state;
        RenderType renderType = RenderTypes.translucentMovingBlock();
        submitNodeStorage.submitCustomGeometry(poseStack, renderType, (pose, buffer) -> {
            VertexConsumer consumer = new GhostVertexConsumer(buffer, (int) (alpha * 255.0F));
            BlockQuadOutput output = (levelIn, stateIn, posIn, quad, instance) -> consumer.putBakedQuad(pose, quad, instance);
            blockRenderer.tesselateBlock(output, 0, 0, 0, renderState, pos, renderedState, model, 0);
        });

        poseStack.popPose();
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
