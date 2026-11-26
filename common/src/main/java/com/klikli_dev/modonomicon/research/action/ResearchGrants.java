/*
 * SPDX-FileCopyrightText: 2026 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.research.action;

import com.klikli_dev.modonomicon.book.Book;
import com.klikli_dev.modonomicon.bookstate.BookVisualStateManager;
import com.klikli_dev.modonomicon.bookstate.visual.BookVisibilitySnapshots;
import com.klikli_dev.modonomicon.data.BookDataManager;
import com.klikli_dev.modonomicon.research.ResearchServices;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;

import java.util.function.BooleanSupplier;
import java.util.stream.Collectors;

/**
 * Grants research facts and values directly, outside of the actions attached to facts. Used by the grant fact command and by
 * the client request for players with game master permission (see the grant research requirement message).
 * <p>
 * Callers validate the ids and the permission of the player. The mutation runs through the state change pipeline, then the
 * visibility driven unread state of the books and the research and book visual states are synced to the player.
 */
public final class ResearchGrants {

    private ResearchGrants() {
    }

    /**
     * Grants the fact to the player.
     *
     * @return true if the fact was not granted before and is granted now
     */
    public static boolean grantFact(ServerPlayer player, Identifier factId) {
        return apply(player, () -> ResearchServices.state().grantFact(player, factId));
    }

    /**
     * Raises the value of the player to the threshold, incrementing it by the missing amount. Values that are already at or above the threshold stay as they are.
     * Incrementing (instead of setting) reports the change like a hook would, so value toasts and unlocks behave the same.
     *
     * @return true if the value changed
     */
    public static boolean raiseValue(ServerPlayer player, Identifier valueId, int threshold) {
        return apply(player, () -> {
            int missing = threshold - ResearchServices.state().getStateFor(player).getValue(valueId);
            return missing > 0 && ResearchServices.state().incrementValue(player, valueId, missing);
        });
    }

    private static boolean apply(ServerPlayer player, BooleanSupplier mutation) {
        var before = BookDataManager.get().getBooks().values().stream()
                .collect(Collectors.toMap(Book::getId, b -> BookVisibilitySnapshots.collect(player, b)));

        boolean changed = ResearchServices.hooks().applyStateChange(player, mutation);

        for (var book : BookDataManager.get().getBooks().values()) {
            BookVisualStateManager.get().updateVisibilityDrivenUnread(player, book, before.get(book.getId()), BookVisibilitySnapshots.collect(player, book));
        }

        ResearchServices.state().syncFor(player);
        BookVisualStateManager.get().syncFor(player);
        return changed;
    }
}
