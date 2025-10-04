package com.klikli_dev.modonomicon.item;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.ClampedItemPropertyFunction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public class BookOpenStateItemPropertyGetter implements ClampedItemPropertyFunction {
    @Override
    public float unclampedCall(ItemStack itemStack, @Nullable ClientLevel clientLevel, @Nullable LivingEntity livingEntity, int i) {
        return ModonomiconItem.isBookOpen(itemStack) ? 1.0f : 0.0f;
    }
}
