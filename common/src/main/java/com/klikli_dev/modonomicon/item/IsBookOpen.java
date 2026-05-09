/*
 * SPDX-FileCopyrightText: 2026 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.item;

import com.mojang.serialization.MapCodec;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.properties.conditional.ConditionalItemModelProperty;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public class IsBookOpen implements ConditionalItemModelProperty {
    public static final MapCodec<IsBookOpen> MAP_CODEC = MapCodec.unit(new IsBookOpen());

    @Override
    public boolean get(ItemStack stack, @Nullable ClientLevel level, @Nullable LivingEntity entity, int seed, ItemDisplayContext displayContext) {
        return ModonomiconItem.isBookOpen(stack);
    }

    @Override
    public MapCodec<IsBookOpen> type() {
        return MAP_CODEC;
    }
}
