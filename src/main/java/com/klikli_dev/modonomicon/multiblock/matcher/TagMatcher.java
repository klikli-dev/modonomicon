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

import com.google.common.collect.ImmutableList;
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
import net.minecraft.tags.TagKey;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;

import java.util.Map;
import java.util.Map.Entry;

/**
 * Matches against the given tag, and optionally checks for the given BlockState properties.
 */
public class TagMatcher implements StateMatcher {
    public static final ResourceLocation ID = Modonomicon.loc("tag");
    private final BlockState displayState;
    private final TagKey<Block> tag;
    private final Map<String, String> props;
    private final TriPredicate<BlockGetter, BlockPos, BlockState> predicate;

    protected TagMatcher(TagKey<Block> tag, Map<String, String> props) {
        this(null, tag, props);
    }

    protected TagMatcher(BlockState displayState, TagKey<Block> tag, Map<String, String> props) {
        this.displayState = displayState;
        this.tag = tag;
        this.props = props;
        this.predicate = (blockGetter, blockPos, blockState) -> blockState.is(this.tag) && this.checkProps(blockState);
    }

    public static TagMatcher fromJson(JsonObject json) {
        BlockState displayState = null;
        if (json.has("display")) {
            try {
                displayState = new BlockStateParser(new StringReader(GsonHelper.getAsString(json, "display")), false).parse(false).getState();
            } catch (CommandSyntaxException e) {
                throw new IllegalArgumentException("Failed to parse BlockState from json member \"display\" for TagStateMatcher.", e);
            }
        }

        try {
            //testing=true enables tag parsing
            var parser = new BlockStateParser(new StringReader(GsonHelper.getAsString(json, "tag")), true).parse(false);
            var tag = parser.getTag();
            var props = parser.getVagueProperties();
            return new TagMatcher(displayState, tag, props);
        } catch (CommandSyntaxException e) {
            throw new IllegalArgumentException("Failed to parse Tag and BlockState properties from json member \"tag\" for TagMatcher.", e);
        }
    }

    public static TagMatcher fromNetwork(FriendlyByteBuf buffer) {
        try {
            BlockState displayState = null;
            if (buffer.readBoolean()) {
                displayState = new BlockStateParser(new StringReader(buffer.readUtf()), true).parse(false).getState();
            }

            var tag = TagKey.create(Registry.BLOCK_REGISTRY, buffer.readResourceLocation());
            var props = buffer.readMap(FriendlyByteBuf::readUtf, FriendlyByteBuf::readUtf);

            return new TagMatcher(displayState, tag, props);
        } catch (CommandSyntaxException e) {
            throw new IllegalArgumentException("Failed to parse TagMatcher from network.", e);
        }
    }

    private boolean checkProps(BlockState state) {
        for (Entry<String, String> entry : this.props.entrySet()) {
            Property<?> prop = state.getBlock().getStateDefinition().getProperty(entry.getKey());
            if (prop == null) {
                return false;
            }

            Comparable<?> value = prop.getValue(entry.getValue()).orElse(null);
            if (value == null) {
                return false;
            }

            if (!state.getValue(prop).equals(value)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    public BlockState getDisplayedState(long ticks) {
        if (this.displayState != null) {
            return this.displayState;
        } else {
            var all = ImmutableList.copyOf(Registry.BLOCK.getTagOrEmpty(this.tag));
            if (all.isEmpty()) {
                return Blocks.BEDROCK.defaultBlockState(); // show something impossible
            } else {
                int idx = (int) ((ticks / 20) % all.size());
                return all.get(idx).value().defaultBlockState();
            }
        }
    }

    @Override
    public TriPredicate<BlockGetter, BlockPos, BlockState> getStatePredicate() {
        return this.predicate;
    }

    @Override
    public void toNetwork(FriendlyByteBuf buffer) {
        buffer.writeBoolean(this.displayState != null);
        if (this.displayState != null) {
            buffer.writeUtf(BlockStateParser.serialize(this.displayState));
        }
        buffer.writeResourceLocation(this.tag.location());
        buffer.writeMap(this.props, FriendlyByteBuf::writeUtf, FriendlyByteBuf::writeUtf);
    }
}
