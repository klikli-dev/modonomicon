/*
 * SPDX-FileCopyrightText: 2023 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.integration.kubejs;

import com.klikli_dev.modonomicon.book.conditions.context.BookConditionContext;
import dev.latvian.mods.kubejs.core.PlayerSelector;
import dev.latvian.mods.kubejs.player.ServerPlayerJS;
import dev.latvian.mods.kubejs.server.ServerEventJS;
import dev.latvian.mods.kubejs.server.ServerJS;
import net.minecraft.server.level.ServerPlayer;

import java.util.Set;

public class UpdateUnlockedContentEventJS extends ServerEventJS {

    ServerPlayer player;
    Set<BookConditionContext> unlockedContent;

    public UpdateUnlockedContentEventJS(ServerPlayer player, Set<BookConditionContext> unlockedContent) {
        this.player = player;
        this.unlockedContent = unlockedContent;
    }

    public ServerPlayerJS getPlayer() {
        return ServerJS.instance.getPlayer(PlayerSelector.mc(this.player));
    }

    public Set<BookConditionContext> getUnlockedContent() {
        return this.unlockedContent;
    }
}
