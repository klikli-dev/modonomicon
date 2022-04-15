/*
 * LGPL-3.0
 *
 * Copyright (C) 2021 klikli-dev
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

package com.klikli_dev.modonomicon.util;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.world.item.ItemStack;

public class RenderUtil {
    /**
     * Applies the pose stack to the openGL matrix, then renders using @{@link net.minecraft.client.renderer.entity.ItemRenderer#renderAndDecorateItem(ItemStack, int, int)}
     */
    public static void renderAndDecorateItemWithPose(PoseStack poseStack, ItemStack stack, int x, int y) {
        PoseStack glPoseStack = RenderSystem.getModelViewStack();
        glPoseStack.pushPose();
        glPoseStack.mulPoseMatrix(poseStack.last().pose());
        RenderSystem.applyModelViewMatrix();
        Minecraft.getInstance().getItemRenderer().renderAndDecorateItem(stack, x, y);
        glPoseStack.popPose();
        RenderSystem.applyModelViewMatrix();
    }
}
