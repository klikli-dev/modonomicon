/*
 * LGPL-3.0
 *
 * Copyright (C) 2022 Authors of Patchouli
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

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.klikli_dev.modonomicon.api.multiblock.Multiblock;
import com.klikli_dev.modonomicon.api.multiblock.StateMatcher;
import com.klikli_dev.modonomicon.api.multiblock.TriPredicate;
import com.klikli_dev.modonomicon.data.LoaderRegistry;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.core.Vec3i;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.*;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.lighting.LevelLightEngine;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public abstract class AbstractMultiblock implements Multiblock, BlockAndTintGetter {
    private final transient Map<BlockPos, BlockEntity> teCache = new HashMap<>();
    public ResourceLocation id;
    protected int offX, offY, offZ;
    protected int viewOffX, viewOffY, viewOffZ;
    Level world;
    protected boolean symmetrical;

    public static Map<Character, StateMatcher> mappingFromJson(JsonObject jsonMapping) {
        var mapping = new HashMap<Character, StateMatcher>();
        for (Entry<String, JsonElement> entry : jsonMapping.entrySet()) {
            if (entry.getKey().length() != 1)
                throw new JsonSyntaxException("Mapping key needs to be only 1 character");
            char key = entry.getKey().charAt(0);
            var value = entry.getValue().getAsJsonObject();
            var stateMatcherType = ResourceLocation.tryParse(GsonHelper.getAsString(value, "type"));
            var stateMatcher = LoaderRegistry.getStateMatcherJsonLoader(stateMatcherType).fromJson(value);
            mapping.put(key, stateMatcher);
        }

        return mapping;
    }

    public static <T extends AbstractMultiblock> T additionalPropertiesFromJson(T multiblock, JsonObject json) {
        if (json.has("symmetrical")) {
            multiblock.symmetrical = GsonHelper.getAsBoolean(json, "symmetrical");
        }
        if(json.has("offset")) {
            var jsonOffset = GsonHelper.getAsJsonArray(json, "offset");
            if(jsonOffset.size() != 3) {
                throw new JsonSyntaxException("Offset needs to be an array of 3 integers");
            }
            multiblock.offset(jsonOffset.get(0).getAsInt(), jsonOffset.get(1).getAsInt(), jsonOffset.get(2).getAsInt());
        }

        return multiblock;
    }

    public static Rotation fixHorizontal(Rotation rot) {
        return switch (rot) {
            case CLOCKWISE_90 -> Rotation.COUNTERCLOCKWISE_90;
            case COUNTERCLOCKWISE_90 -> Rotation.CLOCKWISE_90;
            default -> rot;
        };
    }

    public Multiblock setOffset(int x, int y, int z) {
		this.offX = x;
		this.offY = y;
		this.offZ = z;
        return this.setViewOffset(x, y, z);
    }

    public Multiblock setViewOffset(int x, int y, int z) {
		this.viewOffX = x;
		this.viewOffY = y;
		this.viewOffZ = z;
        return this;
    }

    public void setWorld(Level world) {
        this.world = world;
    }

    @Override
    public Multiblock offset(int x, int y, int z) {
        return this.setOffset(this.offX + x, this.offY + y, this.offZ + z);
    }

    @Override
    public Multiblock offsetView(int x, int y, int z) {
        return this.setViewOffset(this.viewOffX + x, this.viewOffY + y, this.viewOffZ + z);
    }

    @Override
    public Multiblock setSymmetrical(boolean symmetrical) {
        this.symmetrical = symmetrical;
        return this;
    }

    @Override
    public Multiblock setId(ResourceLocation res) {
        this.id = res;
        return this;
    }

    @Override
    public boolean isSymmetrical() {
        return this.symmetrical;
    }

    @Override
    public ResourceLocation getId() {
        return this.id;
    }

    @Override
    public void place(Level world, BlockPos pos, Rotation rotation) {
		this.setWorld(world);
		this.simulate(world, pos, rotation, false).getSecond().forEach(r -> {
            BlockPos placePos = r.getWorldPosition();
            BlockState targetState = r.getStateMatcher().getDisplayedState(world.getGameTime()).rotate(rotation);

            if (!targetState.isAir() && targetState.canSurvive(world, placePos) && world.getBlockState(placePos).getMaterial().isReplaceable()) {
                world.setBlockAndUpdate(placePos, targetState);
            }
        });
    }

    @Override
    public Rotation validate(Level world, BlockPos pos) {
        if (this.isSymmetrical() && this.validate(world, pos, Rotation.NONE)) {
            return Rotation.NONE;
        } else {
            for (Rotation rot : Rotation.values()) {
                if (this.validate(world, pos, rot)) {
                    return rot;
                }
            }
        }
        return null;
    }

    @Override
    public boolean validate(Level world, BlockPos pos, Rotation rotation) {
		this.setWorld(world);
        Pair<BlockPos, Collection<SimulateResult>> sim = this.simulate(world, pos, rotation, false);

        return sim.getSecond().stream().allMatch(r -> {
            BlockPos checkPos = r.getWorldPosition();
            TriPredicate<BlockGetter, BlockPos, BlockState> pred = r.getStateMatcher().getStatePredicate();
            BlockState state = world.getBlockState(checkPos).rotate(fixHorizontal(rotation));

            return pred.test(world, checkPos, state);
        });
    }

    @Override
    public abstract Vec3i getSize();

    @Override
    @Nullable
    public BlockEntity getBlockEntity(BlockPos pos) {
        BlockState state = this.getBlockState(pos);
        if (state.getBlock() instanceof EntityBlock) {
            return this.teCache.computeIfAbsent(pos.immutable(), p -> ((EntityBlock) state.getBlock()).newBlockEntity(pos, state));
        }
        return null;
    }

    @Override
    public FluidState getFluidState(BlockPos pos) {
        return Fluids.EMPTY.defaultFluidState();
    }

    @Override
    public float getShade(Direction direction, boolean shaded) {
        return 1.0F;
    }

    @Override
    public LevelLightEngine getLightEngine() {
        return null;
    }

    @Override
    public int getBlockTint(BlockPos pos, ColorResolver color) {
        var plains = this.world.registryAccess().registryOrThrow(Registry.BIOME_REGISTRY)
                .getOrThrow(Biomes.PLAINS);
        return color.getColor(plains, pos.getX(), pos.getZ());
    }

    @Override
    public int getBrightness(LightLayer type, BlockPos pos) {
        return 15;
    }

    @Override
    public int getRawBrightness(BlockPos pos, int ambientDarkening) {
        return 15 - ambientDarkening;
    }

    // These heights were assumed based being derivative of old behavior, but it may be ideal to change
    @Override
    public int getHeight() {
        return 255;
    }

    @Override
    public int getMinBuildHeight() {
        return 0;
    }

    void setViewOffset() {
		this.setViewOffset(this.offX, this.offY, this.offZ);
    }
}
