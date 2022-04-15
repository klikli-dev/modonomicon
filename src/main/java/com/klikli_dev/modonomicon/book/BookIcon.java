/*
 * LGPL-3.0
 *
 * Copyright (C) 2022 klikli-dev
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

package com.klikli_dev.modonomicon.book;

import com.klikli_dev.modonomicon.util.RenderUtil;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;

public class BookIcon {
    private final ItemStack itemStack;
    private final ResourceLocation texture;

    public BookIcon(ItemStack stack) {
        this.itemStack = stack;
        this.texture = null;
    }

    public BookIcon(ResourceLocation texture) {
        this.texture = texture;
        this.itemStack = ItemStack.EMPTY;
    }

    public static BookIcon fromString(String value) {
        if (value.endsWith(".png")) {
            return new BookIcon(new ResourceLocation(value));
        } else {
            Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(value));
            return new BookIcon(new ItemStack(item));
        }
    }

    public static BookIcon fromNetwork(FriendlyByteBuf buffer) {
        ResourceLocation rl = buffer.readResourceLocation();
        if (rl.getPath().endsWith(".png")) {
            return new BookIcon(rl);
        } else {
            Item item = ForgeRegistries.ITEMS.getValue(rl);
            return new BookIcon(new ItemStack(item));
        }
    }

    public void render(PoseStack ms, int x, int y) {
        if (this.texture != null) {
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            RenderSystem.setShaderTexture(0, this.texture);
            GuiComponent.blit(ms, x, y, 0, 0, 0, 16, 16, 16, 16);
        } else {
            RenderUtil.renderAndDecorateItemWithPose(ms, this.itemStack, x, y);
        }
    }

    public void toNetwork(FriendlyByteBuf buffer) {
        if (this.texture != null) {
            buffer.writeResourceLocation(this.texture);
        } else {
            buffer.writeResourceLocation(this.itemStack.getItem().getRegistryName());
        }
    }
}
