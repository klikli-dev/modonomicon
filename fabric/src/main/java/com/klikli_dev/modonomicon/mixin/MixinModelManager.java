package com.klikli_dev.modonomicon.mixin;

import com.klikli_dev.modonomicon.client.BookModel;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.SpecialBlockModelRenderer;
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
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Map;

@Mixin(ModelManager.class)
public class MixinModelManager {

    @Inject(method = "loadModels",
            at = @At(value = "INVOKE",
                    target = "Lcom/google/common/collect/Multimap;asMap()Ljava/util/Map;",
                    ordinal = 1,
                    shift = At.Shift.AFTER),
            locals = LocalCapture.CAPTURE_FAILEXCEPTION)
    private void modifyItemModelsAfterBake(ProfilerFiller profiler, Map<ResourceLocation, AtlasSet.StitchResult> atlasPreperations, ModelBakery modelBakery, Object2IntMap<BlockState> modelGroups, EntityModelSet entityModelSet, SpecialBlockModelRenderer specialBlockModelRenderer, CallbackInfo ci, ModelBakery.BakingResult bakingResult) {
        BookModel.replace(bakingResult().itemStackModels());
    }
}
