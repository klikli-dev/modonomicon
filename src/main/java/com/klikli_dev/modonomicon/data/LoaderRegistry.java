/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.data;

import com.klikli_dev.modonomicon.api.ModonimiconConstants.Data.Condition;
import com.klikli_dev.modonomicon.api.ModonimiconConstants.Data.Page;
import com.klikli_dev.modonomicon.api.multiblock.Multiblock;
import com.klikli_dev.modonomicon.api.multiblock.StateMatcher;
import com.klikli_dev.modonomicon.api.multiblock.TriPredicate;
import com.klikli_dev.modonomicon.book.conditions.*;
import com.klikli_dev.modonomicon.book.page.BookMultiblockPage;
import com.klikli_dev.modonomicon.book.page.BookPage;
import com.klikli_dev.modonomicon.book.page.BookTextPage;
import com.klikli_dev.modonomicon.multiblock.DenseMultiblock;
import com.klikli_dev.modonomicon.multiblock.SparseMultiblock;
import com.klikli_dev.modonomicon.multiblock.matcher.*;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;

import java.util.HashMap;
import java.util.Map;

public class LoaderRegistry {

    private static final Map<ResourceLocation, JsonLoader<? extends BookPage>> pageJsonLoaders = new HashMap<>();
    private static final Map<ResourceLocation, NetworkLoader<? extends BookPage>> pageNetworkLoaders = new HashMap<>();

    private static final Map<ResourceLocation, JsonLoader<? extends BookCondition>> conditionJsonLoaders = new HashMap<>();

    private static final Map<ResourceLocation, NetworkLoader<? extends BookCondition>> conditionNetworkLoaders = new HashMap<>();
    private static final Map<ResourceLocation, JsonLoader<? extends Multiblock>> multiblockJsonLoaders = new HashMap<>();
    private static final Map<ResourceLocation, NetworkLoader<? extends Multiblock>> multiblockNetworkLoaders = new HashMap<>();

    private static final Map<ResourceLocation, JsonLoader<? extends StateMatcher>> stateMatcherJsonLoaders = new HashMap<>();
    private static final Map<ResourceLocation, NetworkLoader<? extends StateMatcher>> stateMatcherNetworkLoaders = new HashMap<>();


    private static final Map<ResourceLocation, TriPredicate<BlockGetter, BlockPos, BlockState>> predicates = new HashMap<>();

    /**
     * Call from common setup
     */
    public static void registerLoaders() {
        registerDefaultPageLoaders();
        registerDefaultConditionLoaders();
        registerDefaultPredicates();
        registerDefaultStateMatcherLoaders();
        registerDefaultMultiblockLoaders();
    }

    private static void registerDefaultPageLoaders() {
        registerPageLoader(Page.TEXT, BookTextPage::fromJson, BookTextPage::fromNetwork);
        registerPageLoader(Page.MULTIBLOCK, BookMultiblockPage::fromJson, BookMultiblockPage::fromNetwork);
    }

    private static void registerDefaultConditionLoaders() {
        registerConditionLoader(Condition.ADVANCEMENT, BookAdvancementCondition::fromJson, BookAdvancementCondition::fromNetwork);
        registerConditionLoader(Condition.OR, BookOrCondition::fromJson, BookOrCondition::fromNetwork);
        registerConditionLoader(Condition.AND, BookAndCondition::fromJson, BookAndCondition::fromNetwork);
        registerConditionLoader(Condition.TRUE, BookTrueCondition::fromJson, BookTrueCondition::fromNetwork);
        registerConditionLoader(Condition.FALSE, BookFalseCondition::fromJson, BookFalseCondition::fromNetwork);
    }

    private static void registerDefaultMultiblockLoaders() {
        registerMultiblockLoader(DenseMultiblock.TYPE, DenseMultiblock::fromJson, DenseMultiblock::fromNetwork);
        registerMultiblockLoader(SparseMultiblock.TYPE, SparseMultiblock::fromJson, SparseMultiblock::fromNetwork);
    }

    private static void registerDefaultStateMatcherLoaders() {
        registerStateMatcherLoader(AnyMatcher.TYPE, AnyMatcher::fromJson, AnyMatcher::fromNetwork);
        registerStateMatcherLoader(BlockMatcher.TYPE, BlockMatcher::fromJson, BlockMatcher::fromNetwork);
        registerStateMatcherLoader(BlockStateMatcher.TYPE, BlockStateMatcher::fromJson, BlockStateMatcher::fromNetwork);
        registerStateMatcherLoader(DisplayOnlyMatcher.TYPE, DisplayOnlyMatcher::fromJson, DisplayOnlyMatcher::fromNetwork);
        registerStateMatcherLoader(PredicateMatcher.TYPE, PredicateMatcher::fromJson, PredicateMatcher::fromNetwork);
        registerStateMatcherLoader(TagMatcher.TYPE, TagMatcher::fromJson, TagMatcher::fromNetwork);
    }

    private static void registerDefaultPredicates() {
        registerPredicate(Matchers.AIR.getPredicateId(), (getter, pos, state) -> state.isAir());
    }


    /**
     * Call from common setup
     */
    public static void registerPageLoader(ResourceLocation id, JsonLoader<? extends BookPage> jsonLoader,
                                          NetworkLoader<? extends BookPage> networkLoader) {
        pageJsonLoaders.put(id, jsonLoader);
        pageNetworkLoaders.put(id, networkLoader);
    }

    /**
     * Call from common setup
     */
    public static void registerConditionLoader(ResourceLocation id, JsonLoader<? extends BookCondition> jsonLoader,
                                          NetworkLoader<? extends BookCondition> networkLoader) {
        conditionJsonLoaders.put(id, jsonLoader);
        conditionNetworkLoaders.put(id, networkLoader);
    }

    /**
     * Call from common setup
     */
    public static void registerMultiblockLoader(ResourceLocation id, JsonLoader<? extends Multiblock> jsonLoader,
                                                NetworkLoader<? extends Multiblock> networkLoader) {
        multiblockJsonLoaders.put(id, jsonLoader);
        multiblockNetworkLoaders.put(id, networkLoader);
    }

    /**
     * Call from common setup
     */
    public static void registerStateMatcherLoader(ResourceLocation id, JsonLoader<? extends StateMatcher> jsonLoader,
                                                  NetworkLoader<? extends StateMatcher> networkLoader) {
        stateMatcherJsonLoaders.put(id, jsonLoader);
        stateMatcherNetworkLoaders.put(id, networkLoader);
    }

    /**
     * Call from common setup, so predicates are available on both sides.
     */
    public static void registerPredicate(ResourceLocation id, TriPredicate<BlockGetter, BlockPos, BlockState> predicate) {
        predicates.put(id, predicate);
    }

    public static JsonLoader<? extends StateMatcher> getStateMatcherJsonLoader(ResourceLocation id) {
        var loader = stateMatcherJsonLoaders.get(id);
        if (loader == null) {
            throw new IllegalArgumentException("No json loader registered for state matcher type " + id);
        }
        return loader;
    }

    public static NetworkLoader<? extends StateMatcher> getStateMatcherNetworkLoader(ResourceLocation id) {
        var loader = stateMatcherNetworkLoaders.get(id);
        if (loader == null) {
            throw new IllegalArgumentException("No network loader registered for state matcher type " + id);
        }
        return loader;
    }

    public static TriPredicate<BlockGetter, BlockPos, BlockState> getPredicate(ResourceLocation id) {
        var predicate = predicates.get(id);
        if (predicate == null) {
            throw new IllegalArgumentException("No predicated registered for id " + id);
        }
        return predicate;
    }

    public static JsonLoader<? extends BookPage> getPageJsonLoader(ResourceLocation id) {
        var loader = pageJsonLoaders.get(id);
        if (loader == null) {
            throw new IllegalArgumentException("No json loader registered for page type " + id);
        }
        return loader;
    }

    public static NetworkLoader<? extends BookPage> getPageNetworkLoader(ResourceLocation id) {
        var loader = pageNetworkLoaders.get(id);
        if (loader == null) {
            throw new IllegalArgumentException("No network loader registered for page type " + id);
        }
        return loader;
    }

    public static JsonLoader<? extends BookCondition> getConditionJsonLoader(ResourceLocation id) {
        var loader = conditionJsonLoaders.get(id);
        if (loader == null) {
            throw new IllegalArgumentException("No json loader registered for condition type " + id);
        }
        return loader;
    }

    public static NetworkLoader<? extends BookCondition> getConditionNetworkLoader(ResourceLocation id) {
        var loader = conditionNetworkLoaders.get(id);
        if (loader == null) {
            throw new IllegalArgumentException("No network loader registered for condition type " + id);
        }
        return loader;
    }

    public static JsonLoader<? extends Multiblock> getMultiblockJsonLoader(ResourceLocation id) {
        var loader = multiblockJsonLoaders.get(id);
        if (loader == null) {
            throw new IllegalArgumentException("No json loader registered for multiblock type " + id);
        }
        return loader;
    }

    public static NetworkLoader<? extends Multiblock> getMultiblockNetworkLoader(ResourceLocation id) {
        var loader = multiblockNetworkLoaders.get(id);
        if (loader == null) {
            throw new IllegalArgumentException("No network loader registered for multiblock type " + id);
        }
        return loader;
    }
}
