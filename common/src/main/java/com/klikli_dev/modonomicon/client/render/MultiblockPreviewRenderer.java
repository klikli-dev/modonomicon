/*
 * SPDX-FileCopyrightText: 2022 Authors of Patchouli
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.client.render;

import com.klikli_dev.modonomicon.Modonomicon;
import com.klikli_dev.modonomicon.api.ModonomiconAPI;
import com.klikli_dev.modonomicon.api.ModonomiconConstants;
import com.klikli_dev.modonomicon.api.multiblock.Multiblock;
import com.klikli_dev.modonomicon.api.multiblock.MultiblockPreviewData;
import com.klikli_dev.modonomicon.client.ClientTicks;
import com.klikli_dev.modonomicon.multiblock.AbstractMultiblock;
import com.klikli_dev.modonomicon.multiblock.matcher.DisplayOnlyMatcher;
import com.klikli_dev.modonomicon.multiblock.matcher.Matchers;
import com.klikli_dev.modonomicon.platform.ClientServices;
import com.klikli_dev.modonomicon.util.GuiGraphicsExt;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.blaze3d.vertex.VertexFormat.Mode;
import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;

import java.awt.*;
import java.util.*;
import java.util.function.Function;

public class MultiblockPreviewRenderer {

    private static final Map<BlockPos, BlockEntity> blockEntityCache = new Object2ObjectOpenHashMap<>();
    private static final Set<BlockEntity> erroredBlockEntities = Collections.newSetFromMap(new WeakHashMap<>());
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
    private static MultiBufferSource.BufferSource buffers = null;

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

            guiGraphics.pose().pushPose();
            guiGraphics.pose().translate(0, -Math.max(0, animTime - waitTime) * fadeOutSpeed, 0);

            Minecraft mc = Minecraft.getInstance();
            int x = mc.getWindow().getGuiScaledWidth() / 2;
            int y = 12;

            GuiGraphicsExt.drawString(guiGraphics, mc.font, name, x - mc.font.width(name) / 2.0F, y, 0xFFFFFF, false);

            int width = 180;
            int height = 9;
            int left = x - width / 2;
            int top = y + 10;

            if (timeComplete > 0) {
                String s = I18n.get(ModonomiconConstants.I18n.Multiblock.COMPLETE);
                guiGraphics.pose().pushPose();
                guiGraphics.pose().translate(0, Math.min(height + 5, animTime), 0);
                guiGraphics.drawString(mc.font, s, (int) (x - mc.font.width(s) / 2.0F), top + height - 10, 0x00FF00, false);
                guiGraphics.pose().popPose();
            }

            guiGraphics.fill(left - 1, top - 1, left + width + 1, top + height + 1, 0xFF000000);
            drawGradientRect(guiGraphics, left, top, left + width, top + height, 0xFF666666, 0xFF555555);

            float fract = (float) blocksDone / Math.max(1, blocks);
            int progressWidth = (int) ((float) width * fract);
            int color = Mth.hsvToRgb(fract / 3.0F, 1.0F, 1.0F) | 0xFF000000;
            int color2 = new Color(color).darker().getRGB();
            drawGradientRect(guiGraphics, left, top, left + progressWidth, top + height, color, color2);

            if (!isAnchored) {
                String s = I18n.get(ModonomiconConstants.I18n.Multiblock.NOT_ANCHORED);
                guiGraphics.drawString(mc.font, s, (int) (x - mc.font.width(s) / 2.0F), top + height + 8, 0xFFFFFF, false);
            } else {
                if (lookingState != null) {
                    // try-catch around here because the state isn't necessarily present in the world in this instance,
                    // which isn't really expected behavior for getPickBlock
                    try {
                        Block block = lookingState.getBlock();
                        ItemStack stack = block.getCloneItemStack(mc.level, lookingPos, lookingState);

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

            guiGraphics.pose().popPose();
        }
    }

    public static void onRenderLevelLastEvent(PoseStack poseStack) {
        if (hasMultiblock && multiblock != null) {
            renderMultiblock(Minecraft.getInstance().level, poseStack);
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

    public static void renderMultiblock(Level level, PoseStack ms) {
        Minecraft mc = Minecraft.getInstance();
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

        EntityRenderDispatcher erd = mc.getEntityRenderDispatcher();
        double renderPosX = erd.camera.getPosition().x();
        double renderPosY = erd.camera.getPosition().y();
        double renderPosZ = erd.camera.getPosition().z();
        ms.pushPose();
        ms.translate(-renderPosX, -renderPosY, -renderPosZ);

        if (buffers == null) {
            buffers = initBuffers(mc.renderBuffers().bufferSource());
        }

        BlockPos checkPos = null;
        if (mc.hitResult instanceof BlockHitResult blockRes) {
            checkPos = blockRes.getBlockPos().relative(blockRes.getDirection());
        }

        blocks = blocksDone = airFilled = 0;
        lookingState = null;
        lookingPos = checkPos;

        BlockPos startPos = getStartPos();

        Pair<BlockPos, Collection<Multiblock.SimulateResult>> sim = multiblock.simulate(level, startPos, getFacingRotation(), true, false);
        for (Multiblock.SimulateResult r : sim.getSecond()) {
            float alpha = 0.3F;
            if (r.getWorldPosition().equals(checkPos)) {
                lookingState = r.getStateMatcher().getDisplayedState(ClientTicks.ticks);
                alpha = 0.6F + (float) (Math.sin(ClientTicks.total * 0.3F) + 1F) * 0.1F;
            }

            if (!r.getStateMatcher().equals(Matchers.ANY) && r.getStateMatcher().getType() != DisplayOnlyMatcher.TYPE) {
                boolean air = !r.getStateMatcher().countsTowardsTotalBlocks();
                if (!air) {
                    blocks++;
                }

                if (!r.test(level, facingRotation)) {
                    BlockState renderState = r.getStateMatcher().getDisplayedState(ClientTicks.ticks).rotate(facingRotation);
                    renderBlock(level, renderState, r.getWorldPosition(), multiblock, air, alpha, ms);

                    if (renderState.getBlock() instanceof EntityBlock eb) {

                        //if our cached be is not compatible with the render state, remove it.
                        //this happens e.g. if there is a blocktag that contains multible blocks with different BEs
                        //we also have to translate by startpos to counteract the preview moving in the world (but the BE cache being static)
                        var be = blockEntityCache.compute(r.getWorldPosition().subtract(startPos).immutable(), (p, cachedBe) -> {
                            if (cachedBe != null && !cachedBe.getType().isValid(renderState)) {
                                return eb.newBlockEntity(p, renderState);
                            }
                            return cachedBe != null ? cachedBe : eb.newBlockEntity(p, renderState);
                        });
                        if (be != null && !erroredBlockEntities.contains(be)) {
                            be.setLevel(mc.level);

                            // fake cached state in case the renderer checks it as we don't want to query the actual world
                            be.setBlockState(renderState);

                            ms.pushPose();
                            var bePos = r.getWorldPosition();
                            ms.translate(bePos.getX(), bePos.getY(), bePos.getZ());

                            try {
                                BlockEntityRenderer<BlockEntity> renderer = Minecraft.getInstance().getBlockEntityRenderDispatcher().getRenderer(be);
                                if (renderer != null) {
                                    renderer.render(be, ClientTicks.partialTicks, ms, buffers, 0xF000F0, OverlayTexture.NO_OVERLAY);
                                }
                            } catch (Exception e) {
                                erroredBlockEntities.add(be);
                                Modonomicon.LOG.error("Error rendering block entity", e);
                            }
                            ms.popPose();
                        }
                    }

                    if (air) {
                        airFilled++;
                    }
                } else if (!air) {
                    blocksDone++;
                }
            }
        }

        buffers.endBatch();
        ms.popPose();

        if (!isAnchored) {
            blocks = blocksDone = 0;
        }
    }

    public static void renderBlock(Level world, BlockState state, BlockPos pos, Multiblock multiblock, boolean isAir, float alpha, PoseStack ms) {
        if (pos != null) {
            ms.pushPose();
            ms.translate(pos.getX(), pos.getY(), pos.getZ());

            if (state.getBlock() == Blocks.AIR) {
                float scale = 0.3F;
                float off = (1F - scale) / 2;
                ms.translate(off, off, off);
                ms.scale(scale, scale, scale);

                state = Blocks.RED_CONCRETE.defaultBlockState();
            }

            ClientServices.MULTIBLOCK.renderBlock(state, pos, multiblock, ms, buffers, world.getRandom());

//            Minecraft.getInstance().getBlockRenderer().renderSingleBlock(state, ms, buffers, 0xF000F0, OverlayTexture.NO_OVERLAY);

            ms.popPose();
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

    private static void drawGradientRect(GuiGraphics guiGraphics, int left, int top, int right, int bottom, int startColor, int endColor) {
        float f = (float) (startColor >> 24 & 255) / 255.0F;
        float f1 = (float) (startColor >> 16 & 255) / 255.0F;
        float f2 = (float) (startColor >> 8 & 255) / 255.0F;
        float f3 = (float) (startColor & 255) / 255.0F;
        float f4 = (float) (endColor >> 24 & 255) / 255.0F;
        float f5 = (float) (endColor >> 16 & 255) / 255.0F;
        float f6 = (float) (endColor >> 8 & 255) / 255.0F;
        float f7 = (float) (endColor & 255) / 255.0F;
        RenderSystem.enableBlend();
        RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        BufferBuilder bufferbuilder = Tesselator.getInstance().begin(Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
        Matrix4f mat = guiGraphics.pose().last().pose();
        bufferbuilder.addVertex(mat, right, top, 0).setColor(f1, f2, f3, f);
        bufferbuilder.addVertex(mat, left, top, 0).setColor(f1, f2, f3, f);
        bufferbuilder.addVertex(mat, left, bottom, 0).setColor(f5, f6, f7, f4);
        bufferbuilder.addVertex(mat, right, bottom, 0).setColor(f5, f6, f7, f4);
        BufferUploader.drawWithShader(bufferbuilder.buildOrThrow());
        RenderSystem.disableBlend();
    }

    /**
     * Returns the Rotation of a multiblock structure based on the given entity's facing direction.
     */
    private static Rotation getRotation(Entity entity) {
        return AbstractMultiblock.rotationFromFacing(entity.getDirection());
    }

    private static MultiBufferSource.BufferSource initBuffers(MultiBufferSource.BufferSource original) {
        var fallback = original.sharedBuffer;
        var layerBuffers = original.fixedBuffers;
        SequencedMap<RenderType, ByteBufferBuilder> remapped = new Object2ObjectLinkedOpenHashMap<>();
        for (Map.Entry<RenderType, ByteBufferBuilder> e : layerBuffers.entrySet()) {
            remapped.put(GhostRenderLayer.remap(e.getKey()), e.getValue());
        }
        return new GhostBuffers(fallback, remapped);
    }

    private static class GhostBuffers extends MultiBufferSource.BufferSource {
        protected GhostBuffers(ByteBufferBuilder fallback, SequencedMap<RenderType, ByteBufferBuilder> layerBuffers) {
            super(fallback, layerBuffers);
        }

        @Override
        public VertexConsumer getBuffer(RenderType type) {
            return super.getBuffer(GhostRenderLayer.remap(type));
        }
    }

    private static class GhostRenderLayer extends RenderType {
        private static final Map<RenderType, RenderType> remappedTypes = new IdentityHashMap<>();

        private GhostRenderLayer(RenderType original) {
            super(String.format("%s_%s_ghost", original.toString(), ModonomiconAPI.ID), original.format(), original.mode(), original.bufferSize(), original.affectsCrumbling(), true, () -> {
                original.setupRenderState();

                RenderSystem.disableDepthTest();
                RenderSystem.enableBlend();
                RenderSystem.setShaderColor(1, 1, 1, 0.4F);
            }, () -> {
                RenderSystem.setShaderColor(1, 1, 1, 1);
                RenderSystem.disableBlend();
                RenderSystem.enableDepthTest();

                original.clearRenderState();
            });
        }

        public static RenderType remap(RenderType in) {
            if (in instanceof GhostRenderLayer) {
                return in;
            } else {
                return remappedTypes.computeIfAbsent(in, (type) -> {
                    //hack to address https://github.com/klikli-dev/modonomicon/issues/260, but it should work reasonably well
                    //need to exclude entity, because otherwise entity cutout layers will render using the block atlas.
                    if(type.name.contains("cutout") && !type.name.contains("entity"))
                        type = RenderType.translucent();
                    return new GhostRenderLayer(type);
                });
            }
        }
    }

}
