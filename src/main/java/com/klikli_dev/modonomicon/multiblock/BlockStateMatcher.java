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

package com.klikli_dev.modonomicon.multiblock;

import com.google.gson.JsonObject;
import com.klikli_dev.modonomicon.Modonomicon;
import com.klikli_dev.modonomicon.api.multiblock.TriPredicate;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.arguments.blocks.BlockStateParser;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;

public class BlockStateMatcher extends AbstractStateMatcher {
    private static final ResourceLocation ID = Modonomicon.loc("block");
    private final BlockState matchState;
    private final boolean strict;
    private final TriPredicate<BlockGetter, BlockPos, BlockState> predicate;

    public BlockStateMatcher(BlockState displayState, BlockState matchState, boolean strict) {
        super(displayState);

        this.matchState = matchState;
        this.strict = strict;
        this.predicate = (blockGetter, blockPos, blockState) ->
                strict ? blockState == matchState : blockState.getBlock() == matchState.getBlock();
    }

    public static BlockStateMatcher fromJson(JsonObject json) {
        BlockState display = null;
        boolean isStrict = GsonHelper.getAsBoolean(json, "strict", false);
        if (json.has("display")) {
            display = blockStateFromJson(json, "display");
        }

        var block = blockStateFromJson(json, "block");
        return new BlockStateMatcher(display != null ? display : block, block, isStrict);
    }

    private static BlockState blockStateFromJson(JsonObject json, String memberName) {
        var jsonString = GsonHelper.getAsString(json, memberName);
        var blockRL = ResourceLocation.tryParse(jsonString);
        if (blockRL != null) {
            var block = Registry.BLOCK.getOptional(blockRL);
            if (block.isPresent()) {
                return block.get().defaultBlockState();
            }
        }
        try {
            return new BlockStateParser(new StringReader(jsonString), true).parse(false).getState();
        } catch (CommandSyntaxException e) {
            throw new IllegalArgumentException("Failed to parse BlockState \"" + memberName + "\" for BlockStateMatcher from json.", e);
        }
    }

    public static BlockStateMatcher fromNetwork(FriendlyByteBuf buffer) {
        try {
            var displayState = new BlockStateParser(new StringReader(buffer.readUtf()), true).parse(false).getState();
            var matcherState = new BlockStateParser(new StringReader(buffer.readUtf()), true).parse(false).getState();
            var strict = buffer.readBoolean();
            return new BlockStateMatcher(displayState, matcherState, strict);
        } catch (CommandSyntaxException e) {
            throw new IllegalArgumentException("Failed to parse BlockStateMatcher from network.", e);
        }
    }

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    public TriPredicate<BlockGetter, BlockPos, BlockState> getStatePredicate() {
        return this.predicate;
    }

    @Override
    public void toNetwork(FriendlyByteBuf buffer) {
        super.toNetwork(buffer);
        buffer.writeUtf(BlockStateParser.serialize(this.matchState));
        buffer.writeBoolean(this.strict);
    }
}
