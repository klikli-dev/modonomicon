// SPDX-FileCopyrightText: 2023 klikli-dev
//
// SPDX-License-Identifier: MIT

package com.klikli_dev.modonomicon.client;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.klikli_dev.modonomicon.book.Book;
import com.klikli_dev.modonomicon.item.ModonomiconItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.model.BakedModelWrapper;
import net.minecraftforge.client.model.geometry.IGeometryBakingContext;
import net.minecraftforge.client.model.geometry.IGeometryLoader;
import net.minecraftforge.client.model.geometry.IUnbakedGeometry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.function.Function;

public class BookModelLoader implements IGeometryLoader<BookModelLoader.BookGeometry> {

    @Override
    public BookGeometry read(JsonObject jsonObject, JsonDeserializationContext deserializationContext) throws JsonParseException {
        return new BookGeometry(deserializationContext.deserialize(jsonObject.get("base_model"), BlockModel.class));
    }

    static class BookGeometry implements IUnbakedGeometry<BookGeometry> {
        private final BlockModel handleModel;

        BookGeometry(BlockModel handleModel) {
            this.handleModel = handleModel;
        }

        @Override
        public BakedModel bake(IGeometryBakingContext context, ModelBaker baker, Function<Material, TextureAtlasSprite> spriteGetter, ModelState modelState, ItemOverrides overrides, ResourceLocation modelLocation) {
            var bakedHandle = this.handleModel.bake(baker, this.handleModel, spriteGetter, modelState, modelLocation, false);
            return new BookOverrideModel(bakedHandle, baker);
        }
    }

    private static class BookOverrideModel extends BakedModelWrapper<BakedModel> {
        private final ItemOverrides overrides;

        BookOverrideModel(BakedModel originalModel, ModelBaker baker) {
            super(originalModel);
            BlockModel missing = (BlockModel) baker.getModel(ModelBakery.MISSING_MODEL_LOCATION);

            this.overrides = new ItemOverrides(baker, missing, Collections.emptyList()) {
                @Override
                public BakedModel resolve(@NotNull BakedModel original, @NotNull ItemStack stack,
                                          @Nullable ClientLevel world, @Nullable LivingEntity entity, int seed) {
                    Book book = ModonomiconItem.getBook(stack);
                    if (book != null) {
                        ModelResourceLocation modelPath = new ModelResourceLocation(book.getModel(), "inventory");
                        return Minecraft.getInstance().getModelManager().getModel(modelPath);
                    }
                    return original;
                }
            };
        }

        @Override
        public ItemOverrides getOverrides() {
            return this.overrides;
        }
    }
}
