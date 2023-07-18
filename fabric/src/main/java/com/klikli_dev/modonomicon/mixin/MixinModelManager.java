package com.klikli_dev.modonomicon.mixin;

import com.klikli_dev.modonomicon.client.BookModel;
import com.klikli_dev.modonomicon.client.render.MultiblockPreviewRenderer;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.resources.model.AtlasSet;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.profiling.ProfilerFiller;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(ModelManager.class)
public abstract class MixinModelManager {
    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/resources/model/ModelBakery;getBakedTopLevelModels()Ljava/util/Map;"), method = "loadModels")
    public void replaceBookModel(ProfilerFiller profilerFiller, Map<ResourceLocation, AtlasSet.StitchResult> map, ModelBakery modelBakery, CallbackInfo info) {
        BookModel.replace(modelBakery.getBakedTopLevelModels(), modelBakery);
    }
}
