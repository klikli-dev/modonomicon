/*
 * SPDX-FileCopyrightText: 2026 klikli-dev
 * SPDX-FileCopyrightText: 2026 XFactHD
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.client.render.preview;

import com.klikli_dev.modonomicon.client.render.GhostVertexConsumer;
import com.mojang.blaze3d.vertex.ByteBufferBuilder;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import org.jspecify.annotations.NonNull;

import java.util.IdentityHashMap;
import java.util.Map;

/**
 * Ghost rendering plumbing for the multiblock preview: translucent render type remapping,
 * shared mesh buffer and ghost buffer sources.
 * <p>
 * Moved verbatim from the former {@code MultiblockPreviewRenderer} monolith. See
 * <a href="https://github.com/XFactHD/FramedBlocks/blob/26.1/src/main/java/io/github/xfacthd/framedblocks/client/render/special/GhostBlockRenderer.java">FramedBlocks' GhostBlockRenderer</a>
 * for the architecture this is adapted from.
 * </p>
 */
public final class PreviewGhostBuffers {

    private static final Map<RenderType, RenderType> GHOST_RENDER_TYPE_CACHE = new IdentityHashMap<>();
    private static final ByteBufferBuilder BUFFER_BUILDER = new ByteBufferBuilder(RenderType.TRANSIENT_BUFFER_SIZE);

    private PreviewGhostBuffers() {
    }

    /**
     * Shared transient mesh buffer for ghost block tessellation.
     * HACK / WHY: a single static builder is reused every frame to avoid reallocation; each
     * frame's mesh is uploaded and drawn before the next frame overwrites it.
     */
    public static ByteBufferBuilder bufferBuilder() {
        return BUFFER_BUILDER;
    }

    /**
     * Maps an opaque render type to a translucent ghost equivalent.
     * <p>
     * HACK / WHY: ghost blocks must render semi-transparent regardless of their original render
     * type, so non-blended block render types are swapped for {@code entityTranslucent} on the
     * same texture. Types that already blend, have no textures, or lack {@code Sampler0} are
     * returned untouched — the latter two should never happen with vanilla render types, but
     * custom render types may do anything, in which case we rather skip ghosting than crash.
     * </p>
     */
    public static RenderType getGhostRenderType(RenderType original) {
        if (original.pipeline().getColorTargetState().blendFunction().isPresent()) {
            return original;
        }

        return GHOST_RENDER_TYPE_CACHE.computeIfAbsent(original, rt -> {

            if (rt.hasBlending())
                return rt;

            //should never happen, but if there is some weird custom stuff going on we just not ghost it
            if (rt.state.textures.isEmpty())
                return rt;

            var sampler0 = rt.state.textures.get("Sampler0");

            //again, should not happen, but non-vanilla RTs might do whatever.
            //we could fall back onto any other texture, but let's only do that if something concrete is reported.
            if (sampler0 == null)
                return rt;

            return RenderTypes.entityTranslucent(sampler0.location());
        });
    }

    /**
     * Wraps a vertex consumer to multiply all colors with the given ghost alpha.
     */
    public static VertexConsumer ghostConsumer(VertexConsumer wrapped, int alpha) {
        return new GhostVertexConsumer(wrapped, alpha);
    }

    /**
     * Creates a buffer source that renders every requested render type as its ghost equivalent.
     * <p>
     * HACK / WHY: block entity renderers pick their own render types internally, so the only way
     * to force the whole block entity translucent is intercepting buffer acquisition and swapping
     * the render type plus alpha on the fly.
     * </p>
     *
     * @param ghostAlpha ghost alpha in the 0-255 range.
     */
    public static MultiBufferSource.BufferSource ghostBufferSource(int ghostAlpha) {
        var originalBufferSource = Minecraft.getInstance().renderBuffers().bufferSource();
        return new MultiBufferSource.BufferSource(originalBufferSource.sharedBuffer, originalBufferSource.fixedBuffers) {
            @Override
            public @NonNull VertexConsumer getBuffer(@NonNull RenderType renderType) {
                var ghostType = getGhostRenderType(renderType);
                return new GhostVertexConsumer(originalBufferSource.getBuffer(ghostType), ghostAlpha);
            }
        };
    }
}
