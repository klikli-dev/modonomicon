/*
 * SPDX-FileCopyrightText: 2026 klikli-dev
 * SPDX-FileCopyrightText: 2026 XFactHD
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.client.render.preview;

import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.FilterMode;
import com.mojang.blaze3d.vertex.MeshData;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlas;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.util.OptionalDouble;
import java.util.OptionalInt;

/**
 * Low-level GPU upload + draw for the tessellated ghost block mesh.
 * <p>
 * Moved verbatim from the former {@code MultiblockPreviewRenderer} monolith.
 * HACK / WHY: the preview bypasses the regular chunk/section render pipeline and submits its
 * transient mesh directly, so sampler bindings ({@code Sampler0} = block atlas,
 * {@code Sampler1} = none, {@code Sampler2} = lightmap) and dynamic transforms must be set up
 * manually, mirroring what the vanilla moving-block / section path would do.
 * </p>
 */
public final class PreviewGpuUploader {

    private PreviewGpuUploader() {
    }

    public static void uploadAndDraw(RenderPipeline pipeline, RenderTarget target, MeshData meshData) {
        meshData.sortQuads(PreviewGhostBuffers.bufferBuilder(), RenderSystem.getProjectionType().vertexSorting());
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
                OptionalInt.empty(),
                target.getDepthTextureView(),
                OptionalDouble.empty()
        )) {
            renderPass.setPipeline(pipeline);
            RenderSystem.bindDefaultUniforms(renderPass);
            renderPass.setUniform("DynamicTransforms", dynamicUniforms);
            renderPass.setVertexBuffer(0, vertexBuffer);
            renderPass.setIndexBuffer(indexBuffer, indexType);

            renderPass.bindTexture("Sampler0", Minecraft.getInstance().getTextureManager().getTexture(TextureAtlas.LOCATION_BLOCKS).getTextureView(), RenderSystem.getSamplerCache().getRepeat(FilterMode.NEAREST));
            renderPass.bindTexture("Sampler1", null, null);
            renderPass.bindTexture("Sampler2", Minecraft.getInstance().gameRenderer.lightmap(), RenderSystem.getSamplerCache().getClampToEdge(FilterMode.LINEAR));

            renderPass.drawIndexed(0, 0, meshData.drawState().indexCount(), 1);
        }
    }
}
