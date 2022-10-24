/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.client.render.page;

import com.klikli_dev.modonomicon.Modonomicon;
import com.klikli_dev.modonomicon.api.multiblock.Multiblock;
import com.klikli_dev.modonomicon.api.multiblock.Multiblock.SimulateResult;
import com.klikli_dev.modonomicon.book.page.BookMultiblockPage;
import com.klikli_dev.modonomicon.client.ClientTicks;
import com.klikli_dev.modonomicon.client.gui.book.BookContentScreen;
import com.klikli_dev.modonomicon.client.gui.book.button.VisualizeButton;
import com.klikli_dev.modonomicon.client.render.MultiblockPreviewRenderer;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.datafixers.util.Pair;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;
import com.mojang.math.Vector4f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.core.Vec3i;
import net.minecraft.network.chat.Style;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.ColorResolver;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.lighting.LevelLightEngine;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class BookMultiblockPageRenderer extends BookPageRenderer<BookMultiblockPage> implements PageWithTextRenderer {

    public static final BlockAndTintGetter BLOCK_AND_TINT_GETTER = new BlockAndTintGetter() {
        @Override
        public float getShade(Direction pDirection, boolean pShade) {
            return 1.0f;
        }

        @Override
        public LevelLightEngine getLightEngine() {
            return null;
        }

        @Override
        public int getBlockTint(BlockPos pBlockPos, ColorResolver pColorResolver) {
            var plains = Minecraft.getInstance().player.level.registryAccess().registryOrThrow(Registry.BIOME_REGISTRY)
                    .getOrThrow(Biomes.PLAINS);
            return pColorResolver.getColor(plains, pBlockPos.getX(), pBlockPos.getZ());
        }

        @javax.annotation.Nullable
        @Override
        public BlockEntity getBlockEntity(BlockPos pPos) {
            return null;
        }

        @Override
        public BlockState getBlockState(BlockPos p_45571_) {
            return null;
        }

        @Override
        public FluidState getFluidState(BlockPos pPos) {
            return Fluids.EMPTY.defaultFluidState();
        }

        @Override
        public int getHeight() {
            return 255;
        }

        @Override
        public int getMinBuildHeight() {
            return 0;
        }
    };
    private final Map<BlockPos, BlockEntity> blockEntityCache = new HashMap<>();
    private final Set<BlockEntity> erroredBlockEntities = Collections.newSetFromMap(new WeakHashMap<>());

    protected Pair<BlockPos, Collection<SimulateResult>> multiblockSimulation;
    protected Button visualizeButton;

    public BookMultiblockPageRenderer(BookMultiblockPage page) {
        super(page);
    }

    public void handleButtonVisualize(Button button) {
        MultiblockPreviewRenderer.setMultiblock(this.page.getMultiblock(), this.page.getMultiblockName().getComponent(), true);
        this.parentScreen.simulateEscClosing = true; //will cause the book to close entirely, and safe open page
        this.parentScreen.onClose();

        //TODO: visualizer bookmark to go back to this page quickly?
        //String entryKey =  this.parentEntry.getId().toString(); will be used for bookmark for multiblock
//        Bookmark bookmark = new Bookmark(entryKey, pageNum / 2);
//        parent.addBookmarkButtons();
    }

    private void renderMultiblock(PoseStack ms) {
        var mc = Minecraft.getInstance();
        var level = mc.level;

        var pos = BlockPos.ZERO;
        var facingRotation = Rotation.NONE;

        if (this.page.getMultiblock().isSymmetrical()) {
            facingRotation = Rotation.NONE;
        }

        Vec3i size = this.page.getMultiblock().getSize();
        int sizeX = size.getX();
        int sizeY = size.getY();
        int sizeZ = size.getZ();
        float maxX = 90;
        float maxY = 90;
        float diag = (float) Math.sqrt(sizeX * sizeX + sizeZ * sizeZ);
        float scaleX = maxX / diag;
        float scaleY = maxY / sizeY;
        float scale = -Math.min(scaleX, scaleY);

        int xPos = BookContentScreen.PAGE_WIDTH / 2;
        int yPos = 60;

        ms.pushPose();

        ms.translate(xPos, yPos, 100);
        ms.scale(scale, scale, scale);
        ms.translate(-(float) sizeX / 2, -(float) sizeY / 2, 0);


        // Initial eye pos somewhere off in the distance in the -Z direction
        Vector4f eye = new Vector4f(0, 0, -100, 1);
        Matrix4f rotMat = new Matrix4f();
        rotMat.setIdentity();

        // For each GL rotation done, track the opposite to keep the eye pos accurate
        ms.mulPose(Vector3f.XP.rotationDegrees(-30F));
        rotMat.multiply(Vector3f.XP.rotationDegrees(30));

        float offX = (float) -sizeX / 2;
        float offZ = (float) -sizeZ / 2 + 1;

        float time = this.parentScreen.ticksInBook * 0.5F;
        if (!Screen.hasShiftDown()) {
            time += ClientTicks.partialTicks;
        }
        ms.translate(-offX, 0, -offZ);
        ms.mulPose(Vector3f.YP.rotationDegrees(time));
        rotMat.multiply(Vector3f.YP.rotationDegrees(-time));
        ms.mulPose(Vector3f.YP.rotationDegrees(45));
        rotMat.multiply(Vector3f.YP.rotationDegrees(-45));
        ms.translate(offX, 0, offZ);

        // Finally apply the rotations
        eye.transform(rotMat);
        eye.perspectiveDivide();


        var buffers = mc.renderBuffers().bufferSource();

        BlockPos checkPos = null;
        if (mc.hitResult instanceof BlockHitResult blockRes) {
            checkPos = blockRes.getBlockPos().relative(blockRes.getDirection());
        }

        ms.pushPose();
        RenderSystem.setShaderColor(1F, 1F, 1F, 1F);
        ms.translate(0, 0, -1);

        for (Multiblock.SimulateResult r : this.multiblockSimulation.getSecond()) {
            float alpha = 0.3F;
            if (r.getWorldPosition().equals(checkPos)) {
                alpha = 0.6F + (float) (Math.sin(ClientTicks.total * 0.3F) + 1F) * 0.1F;
            }

            BlockState renderState = r.getStateMatcher().getDisplayedState(ClientTicks.ticks).rotate(facingRotation);

            this.renderBlock(buffers, level, renderState, r.getWorldPosition(), alpha, ms);

            if (renderState.getBlock() instanceof EntityBlock eb) {
                var be = this.blockEntityCache.computeIfAbsent(pos.immutable(), p -> eb.newBlockEntity(pos, renderState));
                if (be != null && !this.erroredBlockEntities.contains(be)) {
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
                        this.erroredBlockEntities.add(be);
                        Modonomicon.LOGGER.error("Error rendering block entity", e);
                    }
                    ms.popPose();
                }
            }
        }
        ms.popPose();
        buffers.endBatch();
        ms.popPose();

    }

    private void renderBlock(MultiBufferSource.BufferSource buffers, ClientLevel level, BlockState state, BlockPos pos, float alpha, PoseStack ps) {
        if (pos != null) {
            ps.pushPose();
            ps.translate(pos.getX(), pos.getY(), pos.getZ());

            Minecraft.getInstance().getBlockRenderer().renderSingleBlock(state, ps, buffers, 0xF000F0, OverlayTexture.NO_OVERLAY);

            ps.popPose();
        }
    }

    @Override
    public int getTextY() {
        //text is always below multiblock, and we don't shift based on multiblock name (unlike title for text pages)
        return 115;
    }

    @Override
    public void onBeginDisplayPage(BookContentScreen parentScreen, int left, int top) {
        super.onBeginDisplayPage(parentScreen, left, top);

        this.multiblockSimulation = this.page.getMultiblock().simulate(null, BlockPos.ZERO, Rotation.NONE, true, true);

        if (this.page.showVisualizeButton()) {
            this.addButton(this.visualizeButton = new VisualizeButton(this.parentScreen, 12, 97, this::handleButtonVisualize));
        }
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float ticks) {

        //render a frame for the multiblock render area
        int x = BookContentScreen.PAGE_WIDTH / 2 - 53;
        int y = 7;
        RenderSystem.enableBlend();
        RenderSystem.setShaderColor(1F, 1F, 1F, 1F);
        BookContentScreen.drawFromTexture(poseStack, this.page.getBook(), x, y, 405, 149, 106, 106);

        //render multiblock name in place of title
        if(!this.page.getMultiblockName().isEmpty()){
            this.renderTitle(this.page.getMultiblockName(), false, poseStack, BookContentScreen.PAGE_WIDTH / 2, 0);
        }

        this.renderMultiblock(poseStack);

        this.renderBookTextHolder(this.page.getText(), poseStack, 0, this.getTextY(), BookContentScreen.PAGE_WIDTH);

        //TODO: render button to show multiblock in world
        //            //TODO: show multiblock preview on button click
//            var block = MultiblockDataManager.get().getMultiblock(ResourceLocation.tryParse("modonomicon:blockentity"));
//            MultiblockPreviewRenderer.setMultiblock(block, Component.translatable("multiblock.modonomicon.test"), true);

        var style = this.getClickedComponentStyleAt(mouseX, mouseY);
        if (style != null)
            this.parentScreen.renderComponentHoverEffect(poseStack, style, mouseX, mouseY);
    }

    @Nullable
    @Override
    public Style getClickedComponentStyleAt(double pMouseX, double pMouseY) {
        if (pMouseX > 0 && pMouseY > 0) {
            var multiblockNameStyle = this.getClickedComponentStyleAtForTitle(this.page.getMultiblockName(), BookContentScreen.PAGE_WIDTH / 2, 0, pMouseX, pMouseY);
            if (multiblockNameStyle != null) {
                return multiblockNameStyle;
            }

            var textStyle = this.getClickedComponentStyleAtForTextHolder(this.page.getText(), 0, this.getTextY(), BookContentScreen.PAGE_WIDTH, pMouseX, pMouseY);
            if (textStyle != null) {
                return textStyle;
            }
        }
        return super.getClickedComponentStyleAt(pMouseX, pMouseY);
    }


}
