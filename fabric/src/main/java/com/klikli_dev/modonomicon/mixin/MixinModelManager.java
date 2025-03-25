package com.klikli_dev.modonomicon.mixin;

import com.google.common.collect.Multimap;
import com.klikli_dev.modonomicon.client.BookModel;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.SpecialBlockModelRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.AtlasSet;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Map;

@Mixin(ModelManager.class)
public class MixinModelManager {

    @Inject(method = "loadModels",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/client/resources/model/ModelManager;createBlockStateToModelDispatch(Ljava/util/Map;Lnet/minecraft/client/resources/model/BakedModel;)Ljava/util/Map;",
                    shift = At.Shift.BEFORE),
            locals = LocalCapture.CAPTURE_FAILEXCEPTION)
    private static void modifyItemModelsAfterBake(ProfilerFiller profiler, Map<ResourceLocation, AtlasSet.StitchResult> atlasPreperations, ModelBakery modelBakery, Object2IntMap<BlockState> modelGroups, EntityModelSet entityModelSet, SpecialBlockModelRenderer specialBlockModelRenderer, CallbackInfoReturnable<ModelManager.ReloadState> cir, Multimap multimap, Multimap multimap2, TextureAtlasSprite textureAtlasSprite, ModelBakery.BakingResult bakingResult) {
        BookModel.replace(bakingResult.itemStackModels());
    }
}
