package com.klikli_dev.modonomicon.client.render;

import com.klikli_dev.modonomicon.api.ModonomiconAPI;
import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.platform.DepthTestFunction;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.MeshData;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ShaderDefines;
import org.jetbrains.annotations.NotNull;

import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Optional;

class GhostRenderType extends RenderType {
    private static final Map<RenderType, RenderType> remappedTypes = new IdentityHashMap<>();

    private final RenderPipeline pipeline;
    private final RenderType original;

    private GhostRenderType(RenderType original, RenderPipeline pipeline) {
        super(String.format("%s_%s_ghost", original.toString(), ModonomiconAPI.ID), original.bufferSize(), original.affectsCrumbling(), true, () -> {

            original.setupRenderState();
//                RenderSystem.disableDepthTest();
//                RenderSystem.enableBlend();
            //don't need the above, now in pipeline
            RenderSystem.setShaderColor(1, 1, 1, 0.4F);
        }, () -> {
            RenderSystem.setShaderColor(1, 1, 1, 1);
//                RenderSystem.disableBlend();
//                RenderSystem.enableDepthTest();
            //don't need the above, now in pipeline

            original.clearRenderState();
        });

        this.pipeline = pipeline;
        this.original = original;
    }

    public static RenderType remap(RenderType in) {
        if (in instanceof GhostRenderType) {
            return in;
        } else {
            return remappedTypes.computeIfAbsent(in, (type) -> {
                //TODO: Do we need cutout/entity handling still?
//                    //hack to address https://github.com/klikli-dev/modonomicon/issues/260, but it should work reasonably well
//                    //need to exclude entity, because otherwise entity cutout layers will render using the block atlas.
//                    if (type.name.contains("cutout") && !type.name.contains("entity"))
//                        type = RenderType.translucent();

                //modify the pipeline
                if (in instanceof CompositeRenderType composite) {
                    //if we have a composite render type, we need to modify the pipeline of the composite
                    var pipeline = toBuilder(composite.renderPipeline)
                            .withBlend(BlendFunction.TRANSLUCENT)
                            .withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST);

                    return new GhostRenderType(type, pipeline.build());
                }

                //otherwise, we fall back to an empty pipeline
                return new GhostRenderType(type, RenderPipeline.builder().build());
            });
        }
    }

    public static RenderPipeline.Builder toBuilder(RenderPipeline pipeline) {
        RenderPipeline.Builder builder = RenderPipeline.builder();
        builder.withLocation(pipeline.getLocation());
        builder.withFragmentShader(pipeline.getFragmentShader());
        builder.withVertexShader(pipeline.getVertexShader());

        if (!pipeline.getShaderDefines().isEmpty()) {
            ShaderDefines.Builder defBuilder = ShaderDefines.builder();
            for (Map.Entry<String, String> entry : pipeline.getShaderDefines().values().entrySet()) {
                defBuilder.define(entry.getKey(), entry.getValue());
            }
            for (String flag : pipeline.getShaderDefines().flags()) {
                defBuilder.define(flag);
            }
            builder.definesBuilder = Optional.of(defBuilder);
        }

        if (!pipeline.getSamplers().isEmpty()) {
            pipeline.getSamplers().forEach(builder::withSampler);
        }

        if (!pipeline.getUniforms().isEmpty()) {
            pipeline.getUniforms().forEach(u -> builder.withUniform(u.name(), u.type()));
        }

        builder.withDepthTestFunction(pipeline.getDepthTestFunction());
        builder.withPolygonMode(pipeline.getPolygonMode());
        builder.withCull(pipeline.isCull());
        builder.withColorWrite(pipeline.isWriteColor(), pipeline.isWriteAlpha());
        builder.withDepthWrite(pipeline.isWriteDepth());
        builder.withColorLogic(pipeline.getColorLogic());

        if (!pipeline.getBlendFunction().isEmpty())
            builder.withBlend(pipeline.getBlendFunction().get());
        else
            builder.withoutBlend();
        builder.withVertexFormat(pipeline.getVertexFormat(), pipeline.getVertexFormatMode());
        builder.withDepthBias(pipeline.getDepthBiasScaleFactor(), pipeline.getDepthBiasConstant());

        return builder;
    }

    @Override
    public void draw(@NotNull MeshData meshData) {
        //TODO: this is not working yet, it's not using the pipeline translucency settings
        //overlay works but looks horrible
        if (this.original instanceof CompositeRenderType composite) {
            var oldPipeline = composite.renderPipeline;
            composite.renderPipeline = this.pipeline; //set our own modified pipeline
            this.original.draw(meshData);
            composite.renderPipeline = oldPipeline; //restore
        } else {
            this.original.draw(meshData);
        }
    }

    @Override
    public @NotNull VertexFormat format() {
        return this.original.format();
    }

    @Override
    public VertexFormat.@NotNull Mode mode() {
        return this.original.mode();
    }

}
