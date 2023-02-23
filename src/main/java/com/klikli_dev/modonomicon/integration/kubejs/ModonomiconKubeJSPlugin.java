/*
 * SPDX-FileCopyrightText: 2023 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.integration.kubejs;

import com.klikli_dev.modonomicon.book.BookEntry;
import com.klikli_dev.modonomicon.book.conditions.context.BookConditionCategoryContext;
import com.klikli_dev.modonomicon.book.conditions.context.BookConditionContext;
import com.klikli_dev.modonomicon.book.conditions.context.BookConditionEntryContext;
import dev.latvian.mods.kubejs.script.ScriptType;
import net.minecraft.server.level.ServerPlayer;

import java.util.Set;

public class ModonomiconKubeJSPlugin extends dev.latvian.mods.kubejs.KubeJSPlugin {


    public static void postNewContentUnlockedEvent(ServerPlayer player, Set<BookConditionContext> newlyUnlockedContent) {
        if(newlyUnlockedContent.isEmpty())
            return;

        new UpdateUnlockedContentEventJS(player, newlyUnlockedContent).post(ScriptType.SERVER, "modonomicon.update_unlocked_content");

        //then for each unlocked piece of content fire an event
        for (BookConditionContext context : newlyUnlockedContent) {
            if (context instanceof BookConditionCategoryContext category) {
                new CategoryUnlockedEventJS(player, category).post(ScriptType.SERVER, "modonomicon.category_unlocked");
            } else if (context instanceof BookConditionEntryContext entry) {
                new EntryUnlockedEventJS(player, entry).post(ScriptType.SERVER, "modonomicon.entry_unlocked");
            }
        }
    }

    public static void postEntryReadEvent(ServerPlayer player, BookEntry entry) {
        new EntryReadEventJS(player, entry).post(ScriptType.SERVER, "modonomicon.entry_read");
    }
}
