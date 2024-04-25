/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.multiblock.matcher;

import com.google.gson.JsonObject;
import com.klikli_dev.modonomicon.Modonomicon;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Blocks;

/**
 * Matches any block, including air, and does not display anything.
 */
public class AnyMatcher extends DisplayOnlyMatcher {

    public static final ResourceLocation TYPE = Modonomicon.loc("any");

    protected AnyMatcher() {
        super(Blocks.AIR.defaultBlockState());
    }

    public static AnyMatcher fromJson(JsonObject json, HolderLookup.Provider provider) {
        return Matchers.ANY;
    }

    public static AnyMatcher fromNetwork(RegistryFriendlyByteBuf buffer) {
        return Matchers.ANY;
    }

    @Override
    public ResourceLocation getType() {
        return TYPE;
    }

    @Override
    public void toNetwork(FriendlyByteBuf buffer) {
    }
}
