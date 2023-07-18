package com.klikli_dev.modonomicon.client;

import com.klikli_dev.modonomicon.item.ModonomiconItem;
import com.klikli_dev.modonomicon.registry.ItemRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.*;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class BookModel implements BakedModel {
    private final BakedModel original;
    private final ItemOverrides itemHandler;

    private BookModel(BakedModel original, ModelBakery loader) {
        this.original = original;
        BlockModel missing = (BlockModel) loader.getModel(ModelBakery.MISSING_MODEL_LOCATION);

        this.itemHandler = new ItemOverrides(new ModelBaker() {
            public Function<Material, TextureAtlasSprite> getModelTextureGetter() {
                return null;
            }

            public BakedModel bake(ResourceLocation location, ModelState state, Function<Material, TextureAtlasSprite> sprites) {
                return null;
            }

            @Override
            public UnbakedModel getModel(ResourceLocation resourceLocation) {
                return null;
            }

            @Nullable
            @Override
            public BakedModel bake(ResourceLocation resourceLocation, ModelState modelState) {
                return null;
            }
        }, missing, Collections.emptyList()) {
            @Override
            public BakedModel resolve(@NotNull BakedModel original, @NotNull ItemStack stack,
                                      @Nullable ClientLevel world, @Nullable LivingEntity entity, int seed) {
                var book = ModonomiconItem.getBook(stack);
                if (book != null) {
                    ModelResourceLocation modelPath = new ModelResourceLocation(book.getModel(), "inventory");
                    return Minecraft.getInstance().getModelManager().getModel(modelPath);
                }
                return original;
            }
        };
    }

    public static void replace(Map<ResourceLocation, BakedModel> models, ModelBakery bakery) {
        ModelResourceLocation key = new ModelResourceLocation(ItemRegistry.MODONOMICON.getId(), "inventory");
        models.computeIfPresent(key, (k, oldModel) -> new BookModel(oldModel, bakery));
    }

    @NotNull
    @Override
    public ItemOverrides getOverrides() {
        return this.itemHandler;
    }

    @NotNull
    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, @NotNull RandomSource rand) {
        return this.original.getQuads(state, side, rand);
    }

    @Override
    public boolean useAmbientOcclusion() {
        return this.original.useAmbientOcclusion();
    }

    @Override
    public boolean isGui3d() {
        return this.original.isGui3d();
    }

    @Override
    public boolean usesBlockLight() {
        return this.original.usesBlockLight();
    }

    @Override
    public boolean isCustomRenderer() {
        return this.original.isCustomRenderer();
    }

    @NotNull
    @Override
    public TextureAtlasSprite getParticleIcon() {
        return this.original.getParticleIcon();
    }

    @Override
    public ItemTransforms getTransforms() {
        return this.original.getTransforms();
    }
}
