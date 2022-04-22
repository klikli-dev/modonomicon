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
import com.klikli_dev.modonomicon.api.multiblock.StateMatcher;
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
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Matches a block, ignoring the BlockState properties.
 */
public class BlockMatcher implements StateMatcher {
    public static final ResourceLocation ID = Modonomicon.loc("block");
    private final BlockState displayState;
    private final Block block;
    private final TriPredicate<BlockGetter, BlockPos, BlockState> predicate;

    protected BlockMatcher(BlockState displayState, Block block) {
        this.displayState = displayState;
        this.block = block;
        this.predicate = (blockGetter, blockPos, blockState) ->
                blockState.getBlock() == block;
    }

    public static BlockMatcher from(Block block) {
        return new BlockMatcher(null, block);
    }

    public static BlockMatcher from(BlockState displayState, Block block) {
        return new BlockMatcher(displayState, block);
    }

    public static BlockMatcher fromJson(JsonObject json) {
        BlockState displayState = null;
        if (json.has("display")) {
            try {
                displayState = new BlockStateParser(new StringReader(GsonHelper.getAsString(json, "display")), false).parse(false).getState();
            } catch (CommandSyntaxException e) {
                throw new IllegalArgumentException("Failed to parse BlockState from json member \"display\" for BlockMatcher.", e);
            }
        }

        try {
            var blockRL = ResourceLocation.tryParse(GsonHelper.getAsString(json, "block"));
            var block = Registry.BLOCK.getOptional(blockRL).orElseThrow();

            return new BlockMatcher(displayState, block);
        } catch (Exception e) {
            throw new IllegalArgumentException("Failed to parse Block from json member \"block\" for BlockMatcher", e);
        }
    }

    public static BlockMatcher fromNetwork(FriendlyByteBuf buffer) {
        try {
            BlockState displayState = null;
            if (buffer.readBoolean())
                displayState = new BlockStateParser(new StringReader(buffer.readUtf()), false).parse(false).getState();

            var block = Registry.BLOCK.getOptional(buffer.readResourceLocation()).orElseThrow();
            return new BlockMatcher(displayState, block);
        } catch (CommandSyntaxException e) {
            throw new IllegalArgumentException("Failed to parse BlockMatcher from network.", e);
        }
    }

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    public BlockState getDisplayedState(long ticks) {
        return this.displayState == null ? this.block.defaultBlockState() : this.displayState;
    }

    @Override
    public TriPredicate<BlockGetter, BlockPos, BlockState> getStatePredicate() {
        return this.predicate;
    }

    @Override
    public void toNetwork(FriendlyByteBuf buffer) {
        buffer.writeBoolean(this.displayState != null);
        if (this.displayState != null)
            buffer.writeUtf(BlockStateParser.serialize(this.displayState));
        buffer.writeResourceLocation(this.block.getRegistryName());
    }
}
