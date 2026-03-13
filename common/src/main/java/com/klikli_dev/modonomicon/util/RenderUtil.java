package com.klikli_dev.modonomicon.util;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import net.minecraft.client.renderer.ShaderDefines;
import net.minecraft.client.renderer.rendertype.RenderSetup;

import java.util.ArrayList;
import java.util.Map;
import java.util.Optional;

public class RenderUtil {

    public static RenderPipeline.Builder toBuilder(RenderPipeline pipeline) {
        RenderPipeline.Builder builder = RenderPipeline.builder();
        builder.location = Optional.of(pipeline.location);
        builder.fragmentShader = Optional.of(pipeline.fragmentShader);
        builder.vertexShader = Optional.of(pipeline.vertexShader);

        if (!pipeline.shaderDefines.isEmpty()) {
            ShaderDefines.Builder defBuilder = ShaderDefines.builder();

            for (Map.Entry<String, String> entry : pipeline.shaderDefines.values().entrySet()) {
                defBuilder.define(entry.getKey(), entry.getValue());
            }

            for (String flag : pipeline.shaderDefines.flags()) {
                defBuilder.define(flag);
            }

            builder.definesBuilder = Optional.of(defBuilder);
        }

        if (!pipeline.samplers.isEmpty()) {
            builder.samplers = Optional.of(new ArrayList<>(pipeline.samplers));
        }

        if (!pipeline.uniforms.isEmpty()) {
            builder.uniforms = Optional.of(new ArrayList<>(pipeline.uniforms));
        }

        builder.depthStencilState = Optional.ofNullable(pipeline.depthStencilState);
        builder.polygonMode = Optional.of(pipeline.polygonMode);
        builder.cull = Optional.of(pipeline.cull);
        builder.colorTargetState = Optional.of(pipeline.colorTargetState);
        builder.vertexFormat = Optional.of(pipeline.vertexFormat);
        builder.vertexFormatMode = Optional.of(pipeline.vertexFormatMode);

        return builder;
    }

    public static RenderSetup.RenderSetupBuilder toBuilder(
            RenderSetup setup,
            RenderPipeline pipeline) {

        var builder = RenderSetup.builder(pipeline);

        builder.bufferSize(setup.bufferSize);
        if (setup.useLightmap) builder.useLightmap();
        if (setup.useOverlay) builder.useOverlay();
        if (setup.affectsCrumbling) builder.affectsCrumbling();
        if (setup.sortOnUpload) builder.sortOnUpload();
        builder.setOutline(setup.outlineProperty);
        builder.setLayeringTransform(setup.layeringTransform);
        builder.setTextureTransform(setup.textureTransform);
        builder.setOutputTarget(setup.outputTarget);

        if (setup.textures != null) {
            for (var entry : setup.textures.entrySet()) {
                var location = entry.getValue().location();
                var sampler = entry.getValue().sampler();
                if (sampler != null) {
                    builder.withTexture(entry.getKey(), location, sampler);
                } else {
                    builder.withTexture(entry.getKey(), location);
                }
            }
        }

        return builder;
    }
}
