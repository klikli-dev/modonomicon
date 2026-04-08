package com.klikli_dev.modonomicon.mixin;

import com.google.common.collect.Multimap;
import com.klikli_dev.modonomicon.client.BookModel;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;

@Mixin(ModelManager.class)
public class MixinModelManager {

    /**
     * This mixes into the synthethic method created for the lambda "bakedStateResults.thenCombine(bakedModelsFuture, (bakingResult, bakedModels) -> {...}" at the very end of ModelManager#loadModels. There we can access the finished bakingResult.
     */
    @Inject(
            method = "lambda$loadModels$1(Lnet/minecraft/client/resources/model/ModelManager$MaterialBakerImpl;Lit/unimi/dsi/fastutil/objects/Object2IntMap;Lnet/minecraft/client/model/geom/EntityModelSet;Lnet/minecraft/client/resources/model/ModelBakery$BakingResult;Ljava/util/Map;)Lnet/minecraft/client/resources/model/ModelManager$ReloadState;",
            at = @At("HEAD")
    )
    private static void onLambdaLoadModels1(
            ModelManager.MaterialBakerImpl materialBaker, Object2IntMap<BlockState> modelGroups, EntityModelSet entityModelSet, ModelBakery.BakingResult bakingResult, Map bakedModels, CallbackInfoReturnable<ModelManager.ReloadState> cir
    ) {
        BookModel.replace(bakingResult.itemStackModels());
    }
}
