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
import dev.latvian.mods.kubejs.event.EventGroup;
import dev.latvian.mods.kubejs.event.EventHandler;
import dev.latvian.mods.kubejs.event.Extra;
import dev.latvian.mods.kubejs.script.ScriptType;
import net.minecraft.server.level.ServerPlayer;

import java.util.Set;

public class ModonomiconKubeJSPlugin extends dev.latvian.mods.kubejs.KubeJSPlugin {

    public static final EventGroup EVENT_GROUP = EventGroup.of("ModonomiconEvents");

    public static final EventHandler UPDATE_UNLOCKED_CONTENT = EVENT_GROUP.server("updateUnlockedContent", () -> UpdateUnlockedContentEventJS.class).extra(Extra.STRING);

    public static final EventHandler CATEGORY_UNLOCKED = EVENT_GROUP.server("categoryUnlocked", () -> CategoryUnlockedEventJS.class).extra(Extra.STRING);

    public static final EventHandler ENTRY_UNOCKED = EVENT_GROUP.server("entryUnlocked", () -> EntryUnlockedEventJS.class).extra(Extra.STRING);

    public static final EventHandler ENTRY_READ = EVENT_GROUP.server("entryRead", () -> EntryReadEventJS.class).extra(Extra.STRING);

    public static void postNewContentUnlockedEvent(ServerPlayer player, Set<BookConditionContext> newlyUnlockedContent) {
        if(newlyUnlockedContent.isEmpty())
            return;

        //post one event for all unlocked stuff
        UPDATE_UNLOCKED_CONTENT.post(ScriptType.SERVER, new UpdateUnlockedContentEventJS(player, newlyUnlockedContent));

        //then for each unlocked piece of content fire an event
        for (BookConditionContext context : newlyUnlockedContent) {
            if (context instanceof BookConditionCategoryContext category) {
                CATEGORY_UNLOCKED.post(ScriptType.SERVER, new CategoryUnlockedEventJS(player, category));
            } else if (context instanceof BookConditionEntryContext entry) {
                ENTRY_UNOCKED.post(ScriptType.SERVER, new EntryUnlockedEventJS(player, entry));
            }
        }
    }

    public static void postEntryReadEvent(ServerPlayer player, BookEntry entry) {
        ENTRY_READ.post(ScriptType.SERVER, new EntryReadEventJS(player, entry));
    }

    @Override
    public void registerEvents() {
        EVENT_GROUP.register();
    }
}
