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
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.util.Lazy;

//TODO implement
public class PredicateMatcher implements StateMatcher {
    private static final ResourceLocation ID = Modonomicon.loc("predicate");

    private final BlockState displayState;
    private final ResourceLocation predicateId;
    private final Lazy<TriPredicate<BlockGetter, BlockPos, BlockState>> predicate;

    protected PredicateMatcher(BlockState displayState, ResourceLocation predicateId) {
        this.displayState = displayState;
        this.predicateId = predicateId;
        //TODO: read predicate from predicate registry
        this.predicate = Lazy.of(() -> (getter, pos, state) -> true);
    }

    public static PredicateMatcher fromJson(JsonObject json) {
        try {
            var displayState = new BlockStateParser(new StringReader(GsonHelper.getAsString(json, "display")), false).parse(false).getState();
            var predicateId = new ResourceLocation(GsonHelper.getAsString(json, "predicate"));
            return new PredicateMatcher(displayState, predicateId);
        } catch (CommandSyntaxException e) {
            throw new IllegalArgumentException("Failed to parse BlockState from json member \"display\" for PredicateMatcher.", e);
        }
    }

    public static PredicateMatcher fromNetwork(FriendlyByteBuf buffer) {
        try {
            var displayState = new BlockStateParser(new StringReader(buffer.readUtf()), true).parse(false).getState();
            var predicateId = buffer.readResourceLocation();
            return new PredicateMatcher(displayState, predicateId);
        } catch (CommandSyntaxException e) {
            throw new IllegalArgumentException("Failed to parse PredicateMatcher from network.", e);
        }
    }

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    public BlockState getDisplayedState(long ticks) {
        return this.displayState;
    }

    @Override
    public TriPredicate<BlockGetter, BlockPos, BlockState> getStatePredicate() {
        return this.predicate.get();
    }

    @Override
    public void toNetwork(FriendlyByteBuf buffer) {
        buffer.writeUtf(BlockStateParser.serialize(this.displayState));
        buffer.writeUtf(this.predicateId.toString());
    }
}
