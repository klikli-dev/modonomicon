/*
 * SPDX-FileCopyrightText: 2023 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.integration;

import com.klikli_dev.modonomicon.book.BookEntry;
import com.klikli_dev.modonomicon.book.conditions.context.BookConditionContext;
import com.klikli_dev.modonomicon.integration.kubejs.ModonomiconKubeJSPlugin;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.fml.ModList;

import java.util.Set;

public class ModonomiconKubeJSIntegration {

    public static boolean isLoaded() {
        return ModList.get().isLoaded("kubejs");
    }

    public static void postNewContentUnlockedEvent(ServerPlayer player, Set<BookConditionContext> newlyUnlockedContent) {
        if (isLoaded()) {
            ModonomiconKubeJSPlugin.postNewContentUnlockedEvent(player, newlyUnlockedContent);
        }
    }

    public static void postEntryReadEvent(ServerPlayer player, BookEntry entry) {
        if (isLoaded()) {
            ModonomiconKubeJSPlugin.postEntryReadEvent(player, entry);
        }
    }

}
