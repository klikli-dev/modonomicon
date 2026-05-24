/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.book;

import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.klikli_dev.modonomicon.api.ModonomiconConstants;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;

import java.util.function.Function;

public class BookIcon {

    /**
     * A custom codec that still uses the "item" field instead of "id" for backwards comp,
     */
    public static final Codec<ItemStack> CUSTOM_ITEM_STACK_CODEC = RecordCodecBuilder.create((builder) -> builder.group(
            Item.CODEC.fieldOf("item").forGetter(ItemStack::typeHolder),
            Codec.INT.optionalFieldOf("count", 1).forGetter(ItemStack::getCount),
            DataComponentPatch.CODEC.optionalFieldOf("components", DataComponentPatch.EMPTY).forGetter(ItemStack::getComponentsPatch)
    ).apply(builder, ItemStack::new));
    private static final Codec<BookIcon> TEXTURE_CODEC = RecordCodecBuilder.create(builder -> builder.group(
            Identifier.CODEC.fieldOf("texture").forGetter(BookIcon::texture),
            Codec.INT.optionalFieldOf("width", ModonomiconConstants.Data.Icon.DEFAULT_WIDTH).forGetter(BookIcon::width),
            Codec.INT.optionalFieldOf("height", ModonomiconConstants.Data.Icon.DEFAULT_HEIGHT).forGetter(BookIcon::height)
    ).apply(builder, (texture, width, height) -> new BookIcon(texture, width, height)));
    public static final Codec<BookIcon> CODEC = Codec.either(
            Identifier.CODEC,
            Codec.either(
                    TEXTURE_CODEC,
                    ItemStackTemplate.CODEC
            )
    ).xmap(value -> value.map(BookIcon::fromString, inner -> inner.map(Function.identity(), BookIcon::new)), icon -> {
        if (icon.texture != null && icon.width == ModonomiconConstants.Data.Icon.DEFAULT_WIDTH && icon.height == ModonomiconConstants.Data.Icon.DEFAULT_HEIGHT) {
            return Either.left(icon.texture);
        }

        if (icon.texture != null) {
            return Either.right(Either.left(icon));
        }

        return Either.right(Either.right(icon.itemStackTemplate));
    });
    public static final StreamCodec<RegistryFriendlyByteBuf, BookIcon> STREAM_CODEC = ByteBufCodecs.fromCodecWithRegistries(CODEC);

    private final ItemStackTemplate itemStackTemplate;
    private ItemStack cachedItemStack;
    private final Identifier texture;

    private final int width;
    private final int height;

    public BookIcon(ItemStackTemplate stack) {
        this.itemStackTemplate = stack;
        this.texture = null;
        this.width = ModonomiconConstants.Data.Icon.DEFAULT_WIDTH;
        this.height = ModonomiconConstants.Data.Icon.DEFAULT_HEIGHT;
    }

    public BookIcon(Identifier texture, int width, int height) {
        this.texture = texture;
        this.itemStackTemplate = null;
        this.width = width;
        this.height = height;
    }

    public static BookIcon fromJson(JsonElement jsonElement, HolderLookup.Provider provider) {
        //if string -> use from string
        //if json object -> parse from json
        if (jsonElement.isJsonPrimitive()) {
            return fromString(Identifier.parse(jsonElement.getAsString()));
        }

        var jsonObject = jsonElement.getAsJsonObject();
        if (jsonObject.has("texture")) {
            var width = GsonHelper.getAsInt(jsonObject, "width", ModonomiconConstants.Data.Icon.DEFAULT_WIDTH);
            var height = GsonHelper.getAsInt(jsonObject, "height", ModonomiconConstants.Data.Icon.DEFAULT_HEIGHT);
            var texture = Identifier.parse(GsonHelper.getAsString(jsonObject, "texture"));
            return new BookIcon(texture, width, height);
        } else {
            //TODO provide backwards compatible codec for itemstack template
            var stack = ItemStackTemplate.CODEC.decode(provider.createSerializationContext(JsonOps.INSTANCE), jsonObject).getOrThrow((e) -> {
                throw new JsonParseException("BookIcon must have either item or texture defined." + jsonElement, new Throwable(e));
            }).getFirst();

            return new BookIcon(stack);
        }
    }

    private static BookIcon fromString(Identifier value) {
        if (value.getPath().endsWith(".png")) {
            return new BookIcon(value, ModonomiconConstants.Data.Icon.DEFAULT_WIDTH, ModonomiconConstants.Data.Icon.DEFAULT_HEIGHT);
        } else {
            Item item = BuiltInRegistries.ITEM.getValue(value);
            return new BookIcon(new ItemStackTemplate(item));
        }
    }

    public static BookIcon fromNetwork(RegistryFriendlyByteBuf buffer) {
        return STREAM_CODEC.decode(buffer);
    }

    public void render(GuiGraphicsExtractor guiGraphics, int x, int y) {
        if (this.texture != null) {
            //1.21.3+ parameter order taken from ImageWidget#renderWidget
            guiGraphics.blit(
                    RenderPipelines.GUI_TEXTURED,
                    this.texture,
                    x, y,
                    0, 0,
                   16, 16,
                    this.width, this.height,
                    this.width, this.height
            );

            //    guiGraphics.blit(RenderPipelines.GUI_TEXTURED, this.page.getImages()[this.index], x * 2 + 6, y * 2 + 6, 0, 0, 200, 200, 200, 200, 200, 200);
        } else {
            if (this.cachedItemStack == null) {
                this.cachedItemStack = this.itemStackTemplate.create();
            }
            guiGraphics.item(this.cachedItemStack, x, y);
        }
    }

    public void toNetwork(RegistryFriendlyByteBuf buffer) {
        STREAM_CODEC.encode(buffer, this);
    }

    public ItemStackTemplate itemStackTemplate() {
        return this.itemStackTemplate;
    }

    public Identifier texture() {
        return this.texture;
    }

    public int width() {
        return this.width;
    }

    public int height() {
        return this.height;
    }
}
