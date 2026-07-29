/*
 * SPDX-FileCopyrightText: 2026 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.registry;

import com.klikli_dev.modonomicon.Modonomicon;
import com.klikli_dev.modonomicon.api.multiblock.TriPredicate;
import com.klikli_dev.modonomicon.data.PredicateType;
import com.klikli_dev.modonomicon.multiblock.matcher.Matchers;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMaps;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Map;

public final class PredicateRegistry {

    private static final Map<Identifier, PredicateType> TYPES = Object2ObjectMaps.synchronize(new Object2ObjectArrayMap<>());

    public static final PredicateType AIR = register(Matchers.AIR.getPredicateId(), (getter, pos, state) -> state.isAir());
    public static final PredicateType NON_SOLID = register(Modonomicon.loc("non_solid"), (getter, pos, state) -> !state.isSolid());

    private PredicateRegistry() {
    }

    public static void bootstrap() {
    }

    public static PredicateType register(Identifier id, TriPredicate<BlockGetter, BlockPos, BlockState> predicate) {
        var type = new PredicateType(id, predicate);
        var previous = TYPES.putIfAbsent(id, type);
        if (previous != null) {
            throw new IllegalArgumentException("Duplicate registration for predicate " + id);
        }
        return type;
    }

    public static TriPredicate<BlockGetter, BlockPos, BlockState> get(Identifier id) {
        var predicate = TYPES.get(id);
        if (predicate == null) {
            throw new IllegalArgumentException("No predicated registered for id " + id);
        }
        return predicate.predicate();
    }

    public static PredicateType getType(Identifier id) {
        var predicate = TYPES.get(id);
        if (predicate == null) {
            throw new IllegalArgumentException("No predicate registered for id " + id);
        }
        return predicate;
    }
}
