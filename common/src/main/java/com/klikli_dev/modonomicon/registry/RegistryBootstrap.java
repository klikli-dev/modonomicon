/*
 * SPDX-FileCopyrightText: 2026 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.registry;

public final class RegistryBootstrap {

    private RegistryBootstrap() {
    }

    public static void bootstrap() {
        ThemeRegistry.bootstrap();
        BookEntryTypeRegistry.bootstrap();
        BookPageTypeRegistry.bootstrap();
        BookConditionTypeRegistry.bootstrap();
        PredicateRegistry.bootstrap();
        StateMatcherTypeRegistry.bootstrap();
        MultiblockTypeRegistry.bootstrap();
        DynamicTextMacroRegistry.bootstrap();
        TriggerTypeRegistry.bootstrap();
    }
}
