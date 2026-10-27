/*
 * SPDX-FileCopyrightText: 2026 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.client.render.preview;

import com.klikli_dev.modonomicon.client.render.GhostVertexConsumer;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.QuadInstance;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.gui.Font;
import net.minecraft.client.model.Model;
import net.minecraft.client.renderer.OrderedSubmitNodeCollector;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.block.MovingBlockRenderState;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.gizmos.DrawableGizmoPrimitives;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.state.level.QuadParticleRenderState;
import net.minecraft.client.renderer.texture.UvMapping;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.client.resources.model.geometry.ItemQuads;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.util.ARGB;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.joml.Quaternionf;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

/**
 * Wraps an {@link OrderedSubmitNodeCollector} so that everything submitted through it is rendered
 * as a translucent ghost.
 * <p>
 * 26.3 replaced the 26.1 {@code MultiBufferSource}/{@code FeatureRenderDispatcher} pipeline with
 * deferred submission nodes: block entity renderers hand their geometry to a
 * {@link SubmitNodeCollector} and the render type is baked into each node. To keep the ghost look
 * we therefore intercept the submissions, remap opaque render types to
 * {@link RenderTypes#entityTranslucent(net.minecraft.resources.Identifier)} and let
 * {@link GhostVertexConsumer} multiply the block entity alpha into the vertex colors.
 * </p>
 * <p>
 * {@code submitBlockModel} and {@code submitItem} normally encode their render types in baked
 * quads (and {@code submitBlockModel} hard-codes an opaque tint), so they are re-submitted as
 * custom geometry where we control both the render type and the vertex color.
 * </p>
 */
class PreviewGhostOrderedSubmitNodeCollector implements OrderedSubmitNodeCollector {

    private static final Map<RenderType, RenderType> GHOST_RENDER_TYPES = new IdentityHashMap<>();
    private static final Direction[] DIRECTIONS = Direction.values();

    private final OrderedSubmitNodeCollector delegate;
    protected final int alpha;
    private final float alphaFactor;

    PreviewGhostOrderedSubmitNodeCollector(OrderedSubmitNodeCollector delegate, int alpha) {
        this.delegate = delegate;
        this.alpha = alpha;
        this.alphaFactor = alpha / 255.0F;
    }

    /**
     * Returns a render type that blends so the ghost alpha is visible.
     * <p>
     * Render types that already blend are kept as-is (their own alpha is multiplied by
     * {@link GhostVertexConsumer}), everything else is remapped to an entity-translucent variant
     * using the original {@code Sampler0} texture. Render types without a primary texture are left
     * untouched, as there is no sensible translucent replacement.
     * </p>
     */
    static RenderType getGhostRenderType(RenderType original) {
        if (original.isOutline() || original.hasBlending()) {
            return original;
        }

        return GHOST_RENDER_TYPES.computeIfAbsent(original, rt -> {
            if (rt.state.textures.isEmpty()) {
                return rt;
            }

            var sampler0 = rt.state.textures.get("Sampler0");
            if (sampler0 == null) {
                return rt;
            }

            return RenderTypes.entityTranslucent(sampler0.location());
        });
    }

    private VertexConsumer ghostBuffer(VertexConsumer buffer) {
        return new GhostVertexConsumer(buffer, this.alpha);
    }

    private int ghostTint(int color) {
        return ARGB.multiplyAlpha(color, this.alphaFactor);
    }

    @Override
    public void submitShadow(PoseStack poseStack, float radius, List<EntityRenderState.ShadowPiece> pieces) {
        this.delegate.submitShadow(poseStack, radius, pieces);
    }

    @Override
    public void submitNameTag(PoseStack poseStack, @Nullable Vec3 nameTagAttachment, int offset, Component name,
                              boolean seeThrough, int lightCoords, CameraRenderState camera) {
        this.delegate.submitNameTag(poseStack, nameTagAttachment, offset, name, seeThrough, lightCoords, camera);
    }

    @Override
    public void submitText(PoseStack poseStack, float x, float y, FormattedCharSequence string, boolean dropShadow,
                           Font.DisplayMode displayMode, int lightCoords, int color, int backgroundColor, int outlineColor) {
        //Sign text goes through this path; fold the ghost alpha into the glyph colors.
        this.delegate.submitText(poseStack, x, y, string, dropShadow, displayMode, lightCoords,
                this.ghostTint(color), this.ghostTint(backgroundColor), this.ghostTint(outlineColor));
    }

    @Override
    public void submitTextBackground(PoseStack poseStack, float x0, float y0, float x1, float y1, int color,
                                     Font.DisplayMode displayMode, int lightCoords) {
        this.delegate.submitTextBackground(poseStack, x0, y0, x1, y1, this.ghostTint(color), displayMode, lightCoords);
    }

    @Override
    public void submitFlame(PoseStack poseStack, EntityRenderState renderState, Quaternionf rotation) {
        this.delegate.submitFlame(poseStack, renderState, rotation);
    }

    @Override
    public void submitLeash(PoseStack poseStack, EntityRenderState.LeashState leashState) {
        this.delegate.submitLeash(poseStack, leashState);
    }

    @Override
    public <S> void submitModel(Model<? super S> model, S state, PoseStack poseStack, RenderType renderType,
                                int lightCoords, int overlayCoords, int tintedColor, @Nullable UvMapping uvMapping, int outlineColor) {
        //The model writes tintedColor straight into the vertex color, so folding the ghost alpha
        //into it is enough; no vertex consumer wrapper is needed here.
        this.delegate.submitModel(model, state, poseStack, getGhostRenderType(renderType),
                lightCoords, overlayCoords, this.ghostTint(tintedColor), uvMapping, 0);
    }

    @Override
    public <S> void submitCrumblingOverlay(Model<? super S> model, S state, PoseStack poseStack, RenderType renderType,
                                           int lightCoords, int overlayCoords, int tintedColor,
                                           ModelFeatureRenderer.CrumblingOverlay crumblingOverlay) {
        this.delegate.submitCrumblingOverlay(model, state, poseStack, getGhostRenderType(renderType),
                lightCoords, overlayCoords, this.ghostTint(tintedColor), crumblingOverlay);
    }

    @Override
    public void submitMovingBlock(PoseStack poseStack, MovingBlockRenderState movingBlockRenderState, int outlineColor) {
        this.delegate.submitMovingBlock(poseStack, movingBlockRenderState, outlineColor);
    }

    @Override
    public void submitBlockModel(PoseStack poseStack, RenderType renderType, List<BlockStateModelPart> parts,
                                 int[] tintLayers, int lightCoords, int overlayCoords, int outlineColor) {
        //submitBlockModel hard-codes an opaque tint and keeps the render type in the baked quads,
        //so re-emit it as custom geometry where the ghost consumer can apply the alpha.
        this.delegate.submitCustomGeometry(poseStack, getGhostRenderType(renderType), (pose, buffer) -> {
            VertexConsumer consumer = this.ghostBuffer(buffer);
            QuadInstance instance = new QuadInstance();
            instance.setLightCoords(lightCoords);
            instance.setOverlayCoords(overlayCoords);

            for (BlockStateModelPart part : parts) {
                for (Direction direction : DIRECTIONS) {
                    for (BakedQuad quad : part.getQuads(direction)) {
                        putQuad(pose, quad, instance, tintLayers, consumer);
                    }
                }
                for (BakedQuad quad : part.getQuads(null)) {
                    putQuad(pose, quad, instance, tintLayers, consumer);
                }
            }
        });
    }

    @Override
    public void submitBreakingBlockModel(PoseStack poseStack, List<BlockStateModelPart> parts, int progress, boolean isBlockTranslucent) {
        this.delegate.submitBreakingBlockModel(poseStack, parts, progress, isBlockTranslucent);
    }

    @Override
    public void submitShapeOutline(PoseStack poseStack, VoxelShape shape, RenderType renderType, int color, float width, boolean afterTerrain) {
        this.delegate.submitShapeOutline(poseStack, shape, renderType, color, width, afterTerrain);
    }

    @Override
    public void submitItem(PoseStack poseStack, ItemDisplayContext displayContext, int lightCoords, int overlayCoords,
                           int outlineColor, int[] tintLayers, ItemQuads quads, ItemStackRenderState.FoilType foilType) {
        //Item quads carry their own render type per quad, so group them by ghost render type and
        //re-emit as custom geometry. Glint/foil is intentionally dropped: it needs extra samplers
        //the ghost render types do not provide.
        if (quads.all().isEmpty()) {
            return;
        }

        Map<RenderType, List<BakedQuad>> quadsByType = new IdentityHashMap<>();
        for (BakedQuad quad : quads.all()) {
            RenderType ghostType = getGhostRenderType(quad.materialInfo().itemRenderType());
            quadsByType.computeIfAbsent(ghostType, type -> new ArrayList<>()).add(quad);
        }

        for (var entry : quadsByType.entrySet()) {
            List<BakedQuad> typeQuads = entry.getValue();
            this.delegate.submitCustomGeometry(poseStack, entry.getKey(), (pose, buffer) -> {
                VertexConsumer consumer = this.ghostBuffer(buffer);
                QuadInstance instance = new QuadInstance();
                instance.setLightCoords(lightCoords);
                instance.setOverlayCoords(overlayCoords);

                for (BakedQuad quad : typeQuads) {
                    int tintIndex = quad.materialInfo().tintIndex();
                    instance.setColor(tintIndex != -1 && tintIndex < tintLayers.length ? tintLayers[tintIndex] : -1);
                    consumer.putBakedQuad(pose, quad, instance);
                }
            });
        }
    }

    @Override
    public void submitCustomGeometry(PoseStack poseStack, RenderType renderType, SubmitNodeCollector.CustomGeometryRenderer customGeometryRenderer) {
        this.delegate.submitCustomGeometry(poseStack, getGhostRenderType(renderType),
                (pose, buffer) -> customGeometryRenderer.render(pose, this.ghostBuffer(buffer)));
    }

    @Override
    public void submitQuadParticleGroup(QuadParticleRenderState particles) {
        this.delegate.submitQuadParticleGroup(particles);
    }

    @Override
    public void submitGizmoPrimitives(DrawableGizmoPrimitives.Group group, CameraRenderState camera, boolean onTop) {
        this.delegate.submitGizmoPrimitives(group, camera, onTop);
    }

    private static void putQuad(PoseStack.Pose pose, BakedQuad quad, QuadInstance instance, int[] tintLayers, VertexConsumer consumer) {
        int tintIndex = quad.materialInfo().tintIndex();
        instance.setColor(tintIndex != -1 && tintIndex < tintLayers.length ? tintLayers[tintIndex] : -1);
        consumer.putBakedQuad(pose, quad, instance);
    }
}
