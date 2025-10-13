package com.klikli_dev.modonomicon.mixin;

import com.google.common.collect.Multimap;
import com.klikli_dev.modonomicon.client.BookModel;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.SpecialBlockModelRenderer;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Mixin(ModelManager.class)
public class MixinModelManager {
    /**
     * This mixes into the synthethic method created for the lambda "return modelBakery.bakeModels(new SpriteGetter() { ... }, executor).thenApply((bakingResult) -> { });" in ModelManager#loadModels. There we can access the bakingResult
     */
    @Inject(
            method = "method_68047(Lcom/google/common/collect/Multimap;Lcom/google/common/collect/Multimap;Lit/unimi/dsi/fastutil/objects/Object2IntMap;Lnet/minecraft/client/model/geom/EntityModelSet;Lnet/minecraft/client/renderer/SpecialBlockModelRenderer;Lnet/minecraft/client/resources/model/ModelBakery$BakingResult;)Lnet/minecraft/client/resources/model/ModelManager$ReloadState;",
            at = @At("HEAD")
    )
    private static void onMethod68047(
            Multimap multimap, Multimap multimap2, Object2IntMap object2IntMap, EntityModelSet entityModelSet, SpecialBlockModelRenderer specialBlockModelRenderer, ModelBakery.BakingResult bakingResult, CallbackInfoReturnable<ModelManager.ReloadState> cir
    ) {
        BookModel.replace(bakingResult.itemStackModels());
        // Your code here
    }
}
