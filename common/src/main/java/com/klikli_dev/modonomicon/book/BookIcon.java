/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.book;

import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.klikli_dev.modonomicon.api.ModonomiconConstants;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class BookIcon {

    /**
     * A custom codec that still uses the "item" field instead of "id" for backwards comp,
     */
    public static final Codec<ItemStack> CUSTOM_ITEM_STACK_CODEC = RecordCodecBuilder.create((builder) -> builder.group(
            Item.CODEC.fieldOf("item").forGetter(ItemStack::getItemHolder),
            Codec.INT.optionalFieldOf("count", 1).forGetter(ItemStack::getCount),
            DataComponentPatch.CODEC.optionalFieldOf("components", DataComponentPatch.EMPTY).forGetter(ItemStack::getComponentsPatch)
    ).apply(builder, ItemStack::new));

    /**
     * We allow both vanilla item stack syntax and our custom syntax.
     */
    public static final Codec<ItemStack> ITEM_STACK_CODEC = Codec.withAlternative(CUSTOM_ITEM_STACK_CODEC, ItemStack.CODEC);

    private final ItemStack itemStack;
    private final ResourceLocation texture;

    private final int width;
    private final int height;

    public BookIcon(ItemStack stack) {
        this.itemStack = stack;
        this.texture = null;
        this.width = ModonomiconConstants.Data.Icon.DEFAULT_WIDTH;
        this.height = ModonomiconConstants.Data.Icon.DEFAULT_HEIGHT;
    }

    public BookIcon(ResourceLocation texture, int width, int height) {
        this.texture = texture;
        this.itemStack = ItemStack.EMPTY;
        this.width = width;
        this.height = height;
    }

    public static BookIcon fromJson(JsonElement jsonElement, HolderLookup.Provider provider) {
        //if string -> use from string
        //if json object -> parse from json
        if (jsonElement.isJsonPrimitive()) {
            return fromString(ResourceLocation.parse(jsonElement.getAsString()));
        }

        var jsonObject = jsonElement.getAsJsonObject();
        if (jsonObject.has("texture")) {
            var width = GsonHelper.getAsInt(jsonObject, "width", ModonomiconConstants.Data.Icon.DEFAULT_WIDTH);
            var height = GsonHelper.getAsInt(jsonObject, "height", ModonomiconConstants.Data.Icon.DEFAULT_HEIGHT);
            var texture = ResourceLocation.parse(GsonHelper.getAsString(jsonObject, "texture"));
            return new BookIcon(texture, width, height);
        } else {
            var stack = ITEM_STACK_CODEC.decode(provider.createSerializationContext(JsonOps.INSTANCE), jsonObject).getOrThrow((e) -> {
                throw new JsonParseException("BookIcon must have either item or texture defined." + jsonElement, new Throwable(e));
            }).getFirst();

            return new BookIcon(stack);
        }
    }

    private static BookIcon fromString(ResourceLocation value) {
        if (value.getPath().endsWith(".png")) {
            return new BookIcon(value, ModonomiconConstants.Data.Icon.DEFAULT_WIDTH, ModonomiconConstants.Data.Icon.DEFAULT_HEIGHT);
        } else {
            Item item = BuiltInRegistries.ITEM.getValue(value);
            return new BookIcon(new ItemStack(item));
        }
    }

    public static BookIcon fromNetwork(RegistryFriendlyByteBuf buffer) {
        if (buffer.readBoolean()) {
            ResourceLocation texture = buffer.readResourceLocation();
            int width = buffer.readVarInt();
            int height = buffer.readVarInt();
            return new BookIcon(texture, width, height);
        }

        var stack = ItemStack.STREAM_CODEC.decode(buffer);
        return new BookIcon(stack);
    }

    public void render(GuiGraphics guiGraphics, int x, int y) {
        if (this.texture != null) {
            //1.21.3+ parameter order taken from ImageWidget#renderWidget
            guiGraphics.blit(
                    RenderType::guiTextured,
                    this.texture,
                    x, y,
                    0, 0,
                    16, 16,
                    this.width, this.height
            );
        } else {
            guiGraphics.renderItem(this.itemStack, x, y);
        }
    }

    public void toNetwork(RegistryFriendlyByteBuf buffer) {
        buffer.writeBoolean(this.texture != null);
        if (this.texture != null) {
            buffer.writeResourceLocation(this.texture);
            buffer.writeVarInt(this.width);
            buffer.writeVarInt(this.height);
        } else {
            ItemStack.STREAM_CODEC.encode(buffer, this.itemStack);
        }
    }
}
