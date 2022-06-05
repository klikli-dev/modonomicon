/*
 * LGPL-3.0
 *
 * Copyright (C) 2022 klikli-dev
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

package com.klikli_dev.modonomicon.book.page;

import com.google.gson.JsonObject;
import com.klikli_dev.modonomicon.Modonomicon;
import com.klikli_dev.modonomicon.api.ModonimiconConstants.Data.Page;
import com.klikli_dev.modonomicon.api.multiblock.Multiblock;
import com.klikli_dev.modonomicon.api.multiblock.Multiblock.SimulateResult;
import com.klikli_dev.modonomicon.book.BookEntry;
import com.klikli_dev.modonomicon.book.BookTextHolder;
import com.klikli_dev.modonomicon.book.RenderedBookTextHolder;
import com.klikli_dev.modonomicon.client.ClientTicks;
import com.klikli_dev.modonomicon.client.gui.book.BookContentScreen;
import com.klikli_dev.modonomicon.client.gui.book.markdown.BookTextRenderer;
import com.klikli_dev.modonomicon.data.MultiblockDataManager;
import com.klikli_dev.modonomicon.multiblock.matcher.Matchers;
import com.klikli_dev.modonomicon.util.BookGsonHelper;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.datafixers.util.Pair;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;
import com.mojang.math.Vector4f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

public class BookMultiblockPage extends BookPage implements PageWithText {
    protected BookTextHolder multiblockName;
    protected BookTextHolder text;
    protected ResourceLocation multiblockId;

    protected Multiblock multiblock;

    public BookMultiblockPage(BookTextHolder multiblockName, BookTextHolder text, ResourceLocation multiblockId, String anchor) {
        super(anchor);
        this.multiblockName = multiblockName;
        this.text = text;
    }

    public static BookMultiblockPage fromJson(JsonObject json) {
        var multiblockName = BookGsonHelper.getAsBookTextHolder(json, "multiblock_name", BookTextHolder.EMPTY);
        var multiblockId = ResourceLocation.tryParse(GsonHelper.getAsString(json, "multiblock_id"));
        var text = BookGsonHelper.getAsBookTextHolder(json, "text", BookTextHolder.EMPTY);
        var anchor = GsonHelper.getAsString(json, "anchor", "");
        return new BookMultiblockPage(multiblockName, text, multiblockId, anchor);
    }

    public static BookMultiblockPage fromNetwork(FriendlyByteBuf buffer) {
        var multiblockName = BookTextHolder.fromNetwork(buffer);
        var multiblockId = buffer.readResourceLocation();
        var text = BookTextHolder.fromNetwork(buffer);
        var anchor = buffer.readUtf();
        return new BookMultiblockPage(multiblockName, text, multiblockId, anchor);
    }

    public BookTextHolder getMultiblockName() {
        return this.multiblockName;
    }

    public BookTextHolder getText() {
        return this.text;
    }

    @Override
    public int getTextY() {
        //text is always below multiblock, and we don't shift based on multiblock name (unlike title for text pages)
        return 115;
    }

    @Override
    public ResourceLocation getType() {
        return Page.MULTIBLOCK;
    }

    @Override
    public void build(BookEntry parentEntry, int pageNum) {
        super.build(parentEntry, pageNum);

        if (this.multiblockId != null) {
            this.multiblock = MultiblockDataManager.get().getMultiblock(this.multiblockId);
        }

        if (this.multiblock == null) {
            throw new IllegalArgumentException("Invalid multiblock id " + this.multiblockId);
        }
    }

    @Override
    public void prerenderMarkdown(BookTextRenderer textRenderer) {
        super.prerenderMarkdown(textRenderer);

        if (!this.multiblockName.hasComponent()) {
            this.multiblockName = new BookTextHolder(new TranslatableComponent(this.multiblockName.getKey())
                    .withStyle(Style.EMPTY
                            .withBold(true)
                            .withColor(this.getParentEntry().getCategory().getBook().getDefaultTitleColor())));
        }
        if (!this.text.hasComponent()) {
            this.text = new RenderedBookTextHolder(this.text, textRenderer.render(this.text.getString()));
        }
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float ticks) {
        //render multiblock name in place of title
        this.renderTitle(this.multiblockName, false, poseStack, BookContentScreen.PAGE_WIDTH / 2, 0);

        //TODO: render mutliblock frame

        this.renderMultiblock(poseStack);

        //TODO: render text below multiblock
        this.renderBookTextHolder(this.getText(), poseStack, 0, this.getTextY(), BookContentScreen.PAGE_WIDTH);

        //TODO: render button to show multiblock in world

        var style = this.getClickedComponentStyleAt(mouseX, mouseY);
        if (style != null)
            this.parentScreen.renderComponentHoverEffect(poseStack, style, mouseX, mouseY);
    }

    @Override
    public void toNetwork(FriendlyByteBuf buffer) {
        this.multiblockName.toNetwork(buffer);
        buffer.writeResourceLocation(this.multiblockId);
        this.text.toNetwork(buffer);
        buffer.writeUtf(this.anchor);
    }

    @Nullable
    @Override
    public Style getClickedComponentStyleAt(double pMouseX, double pMouseY) {
        if (pMouseX > 0 && pMouseY > 0) {
            var multiblockNameStyle = this.getClickedComponentStyleAtForTitle(this.multiblockName, BookContentScreen.PAGE_WIDTH / 2, 0, pMouseX, pMouseY);
            if (multiblockNameStyle != null) {
                return multiblockNameStyle;
            }

            var textStyle = this.getClickedComponentStyleAtForTextHolder(this.text, 0, this.getTextY(), BookContentScreen.PAGE_WIDTH, pMouseX, pMouseY);
            if (textStyle != null) {
                return textStyle;
            }
        }
        return super.getClickedComponentStyleAt(pMouseX, pMouseY);
    }

    private void renderMultiblock(PoseStack ms) {
        //TODO: render multiblock

        Minecraft mc = Minecraft.getInstance();

        var pos = BlockPos.ZERO;
        var facingRotation = Rotation.NONE;

        if (multiblock.isSymmetrical()) {
            facingRotation = Rotation.NONE;
        }

//        EntityRenderDispatcher erd = mc.getEntityRenderDispatcher();
//        double renderPosX = erd.camera.getPosition().x();
//        double renderPosY = erd.camera.getPosition().y();
//        double renderPosZ = erd.camera.getPosition().z();
//        ms.pushPose();
//        ms.translate(-renderPosX, -renderPosY, -renderPosZ);

        var buffers = mc.renderBuffers().bufferSource();

        BlockPos checkPos = null;
        if (mc.hitResult instanceof BlockHitResult blockRes) {
            checkPos = blockRes.getBlockPos().relative(blockRes.getDirection());
        }

        blocks = blocksDone = airFilled = 0;
        lookingState = null;
        lookingPos = checkPos;

        Pair<BlockPos, Collection<SimulateResult>> sim = multiblock.simulate(null, pos, facingRotation, true);

        for (Multiblock.SimulateResult r : sim.getSecond()) {
            float alpha = 0.3F;
            if (r.getWorldPosition().equals(checkPos)) {
                lookingState = r.getStateMatcher().getDisplayedState(ClientTicks.ticks);
                alpha = 0.6F + (float) (Math.sin(ClientTicks.total * 0.3F) + 1F) * 0.1F;
            }

            if (!r.getStateMatcher().equals(Matchers.ANY)) {
                boolean air = r.getStateMatcher().equals(Matchers.AIR);
                if (!air) {
                    blocks++;
                }

                if (!r.test(world, facingRotation)) {
                    BlockState renderState = r.getStateMatcher().getDisplayedState(ClientTicks.ticks).rotate(facingRotation);
                    renderBlock(world, renderState, r.getWorldPosition(), alpha, ms);

                    if (renderState.getBlock() instanceof EntityBlock eb) {
                        var be = blockEntityCache.computeIfAbsent(pos.immutable(), p -> eb.newBlockEntity(pos, renderState));
                        if(be != null && !erroredBlockEntities.contains(be)) {
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
                                Modonomicon.LOGGER.error("Error rendering block entity", e);
                            }
                            ms.popPose();
                        }
                    }

                    if (air) {
                        airFilled++;
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

    private void renderRef(PoseStack ms) {
        multiblockObj.setWorld(mc.level);
        Vec3i size = multiblockObj.getSize();
        int sizeX = size.getX();
        int sizeY = size.getY();
        int sizeZ = size.getZ();
        float maxX = 90;
        float maxY = 90;
        float diag = (float) Math.sqrt(sizeX * sizeX + sizeZ * sizeZ);
        float scaleX = maxX / diag;
        float scaleY = maxY / sizeY;
        float scale = -Math.min(scaleX, scaleY);

        int xPos = GuiBook.PAGE_WIDTH / 2;
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

        float time = parent.ticksInBook * 0.5F;
        if (!Screen.hasShiftDown()) {
            time += ClientTicker.partialTicks;
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
		/* TODO XXX This does not handle visualization of sparse multiblocks correctly.
			Dense multiblocks store everything in positive X/Z, so this works, but sparse multiblocks store everything from the JSON as-is.
			Potential solution: Rotate around the offset vars of the multiblock, and add AABB method for extent of the multiblock
		*/
        renderElements(ms, multiblockObj, BlockPos.betweenClosed(BlockPos.ZERO, new BlockPos(sizeX - 1, sizeY - 1, sizeZ - 1)), eye);

        ms.popPose();
    }
}
