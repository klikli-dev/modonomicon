/*
 * SPDX-FileCopyrightText: 2022 Authors of Patchouli
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.api.multiblock;

import com.google.gson.JsonObject;
import com.klikli_dev.modonomicon.data.MultiblockType;
import com.klikli_dev.modonomicon.registry.MultiblockTypeRegistry;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Vec3i;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.BlockAndLightGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Rotation;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

/**
 * An instance of a multiblock.
 * <br>
 * <br>
 * WARNING: This interface is provided only for usage with the API. For creating
 * a Multiblock instance use the methods provided in the API main class. Please
 * do not create your own implementation of this, as it'll not be compatible with
 * all the features in the mod.
 */
public interface Multiblock extends BlockAndLightGetter {

    Codec<Multiblock> CODEC = Codec.lazyInitialized(() -> MultiblockTypeRegistry.codec().dispatch(
            "type",
            Multiblock::type,
            type -> (MapCodec<? extends Multiblock>) type.codec()
    ));

    @SuppressWarnings("unchecked")
    StreamCodec<RegistryFriendlyByteBuf, Multiblock> STREAM_CODEC = StreamCodec.recursive(codec ->
            (StreamCodec<RegistryFriendlyByteBuf, Multiblock>) (StreamCodec<?, ?>) MultiblockTypeRegistry.streamCodec()
                    .dispatch(
                            Multiblock::type,
                            type -> (StreamCodec<? super RegistryFriendlyByteBuf, ? extends Multiblock>) type.streamCodec()
                    ));

    static Multiblock fromJson(JsonObject json, HolderLookup.Provider provider) {
        return CODEC.parse(provider.createSerializationContext(JsonOps.INSTANCE), json)
                .getOrThrow(error -> new IllegalArgumentException("Failed to decode multiblock: " + error));
    }

    static Multiblock fromNetwork(RegistryFriendlyByteBuf buffer) {
        return STREAM_CODEC.decode(buffer);
    }

    static void toNetwork(Multiblock multiblock, RegistryFriendlyByteBuf buffer) {
        STREAM_CODEC.encode(buffer, multiblock);
    }

    // ================================================================================================
    // Builder methods
    // ================================================================================================

    /**
     * Offsets the position of the multiblock by the amount specified.
     * Works for both placement, validation, and rendering.
     */
    Multiblock offset(int x, int y, int z);

    /**
     * Offsets the view of the multiblock by the amount specified.
     * Matters only for where the multiblock renders.
     */
    Multiblock offsetView(int x, int y, int z);

    /**
     * Gets if this multiblock is symmetrical.
     *
     * @see Multiblock#setSymmetrical
     */
    boolean isSymmetrical();

    /**
     * Sets the multiblock's symmetrical value. Symmetrical multiblocks
     * check only in one rotation, not all 4. If your multiblock is symmetrical
     * around the center axis, set this to true to prevent needless cycles.
     */
    Multiblock setSymmetrical(boolean symmetrical);

    // ================================================================================================
    // Getters
    // ================================================================================================

    MultiblockType<?> type();

    Identifier getId();

    /**
     * Sets the multiblock's ID. Not something you need to
     * call yourself as the register method in the main API class does it for you.
     */
    Multiblock setId(Identifier res);

    /**
     * The multiblock type id for serialization.
     */
    default Identifier getType() {
        return this.type().id();
    }

    /**
     * Sets the level the multiblock should use for e.g registry access
     */
    void setLevel(Level level);

    /**
     * Places the multiblock at the given position with the given rotation.
     */
    void place(Level world, BlockPos pos, Rotation rotation);

    /**
     * If this multiblock were anchored at world position {@code anchor} with rotation {@code rotation}, then
     * return a pair whose first element is the final center position (after rotation and {@link #offset}),
     * and whose second element describes each position of the multiblock.
     * This is intended to be highly general, most of the other methods below are implemented in terms of this one.
     * See the main Modonomicon code to see what can be done with this.
     */
    Pair<BlockPos, Collection<SimulateResult>> simulate(Level world, BlockPos anchor, Rotation rotation, boolean forView, boolean disableOffset);

    /**
     * Validates if the multiblock exists at the given position. Will check all 4
     * rotations if the multiblock is not symmetrical.
     *
     * @return The rotation that worked, null if no match
     */
    @Nullable
    Rotation validate(Level world, BlockPos pos);

    /**
     * Validates the multiblock for a specific rotation
     */
    boolean validate(Level world, BlockPos pos, Rotation rotation);

    /**
     * Fine-grained check for whether any one given block of the multiblock exists at the given position
     * with the given rotation.
     *
     * @param start The anchor position. The multiblock's {@link #offset} is not applied to this.
     */
    boolean test(Level world, BlockPos start, int x, int y, int z, Rotation rotation);

    /**
     * Gets the size of this multiblock
     *
     * @return The size of the multiblock
     */
    Vec3i getSize();

    Vec3i getOffset();

    Vec3i getViewOffset();

    /**
     * Serializes multiblock to the given buffer.
     */
    default void toNetwork(FriendlyByteBuf buffer) {
        Multiblock.toNetwork(this, (RegistryFriendlyByteBuf) buffer);
    }

    interface SimulateResult {
        /**
         * Final world position this block will be matched or placed at
         */
        BlockPos worldPosition();

        /**
         * The matcher used at this position
         */
        StateMatcher stateMatcher();

        /**
         * The character used to express the state matcher, if this is a dense multiblock.
         */
        @Nullable
        Character character();

        /**
         * @return Whether the multiblock is fulfilled at this position
         */
        boolean test(Level world, Rotation rotation);
    }

}
