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

package com.klikli_dev.modonomicon.multiblock.matcher;

import com.google.gson.JsonObject;
import com.klikli_dev.modonomicon.Modonomicon;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Blocks;

/**
 * Matches any block, including air, and does not display anything.
 */
public class AnyMatcher extends DisplayOnlyMatcher {

    public static final AnyMatcher ANY = new AnyMatcher();

    public static final ResourceLocation ID = Modonomicon.loc("any");

    protected AnyMatcher() {
        super(Blocks.AIR.defaultBlockState());
    }

    public static AnyMatcher fromJson(JsonObject json) {
        return new AnyMatcher();
    }

    public static AnyMatcher fromNetwork(FriendlyByteBuf buffer) {
        return new AnyMatcher();
    }

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    public void toNetwork(FriendlyByteBuf buffer) {
    }
}
