/*
 * SPDX-FileCopyrightText: 2026 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.gametest;

import com.klikli_dev.modonomicon.Modonomicon;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.FunctionGameTestInstance;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.gametest.framework.TestData;
import net.minecraft.resources.ResourceKey;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.event.RegisterGameTestsEvent;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Consumer;

/**
 * Registers the common {@link GameTestCatalog} with NeoForge. The tests only run for the namespaces listed in
 * {@code neoforge.enabledGameTestNamespaces}, see the {@code gameTestServer} run in neo/build.gradle.
 */
public class GameTestRegistry {

    public static final DeferredRegister<Consumer<GameTestHelper>> TEST_FUNCTIONS =
            DeferredRegister.create(BuiltInRegistries.TEST_FUNCTION, Modonomicon.MOD_ID);

    static {
        for (var group : GameTestCatalog.GROUPS) {
            for (var test : group.tests()) {
                TEST_FUNCTIONS.register(testPath(group, test), () -> test.function());
            }
        }
    }

    public static void register(IEventBus modEventBus) {
        TEST_FUNCTIONS.register(modEventBus);
        modEventBus.addListener(GameTestRegistry::onRegisterGameTests);
    }

    public static void onRegisterGameTests(RegisterGameTestsEvent event) {
        for (var group : GameTestCatalog.GROUPS) {
            var environment = event.registerEnvironment(Modonomicon.loc(group.name()));
            for (var test : group.tests()) {
                var function = ResourceKey.create(Registries.TEST_FUNCTION, Modonomicon.loc(testPath(group, test)));
                var testData = new TestData<>(environment, GameTestCatalog.STRUCTURE, 40, 0, true);
                event.registerTest(function.identifier(), new FunctionGameTestInstance(function, testData));
            }
        }
    }

    private static String testPath(GameTestCatalog.Group group, GameTestCatalog.GameTest test) {
        return group.name() + "/" + test.name();
    }
}
