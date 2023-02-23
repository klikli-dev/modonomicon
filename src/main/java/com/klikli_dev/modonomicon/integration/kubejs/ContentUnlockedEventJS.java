/*
 * SPDX-FileCopyrightText: 2023 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.integration.kubejs;

import com.klikli_dev.modonomicon.book.conditions.context.BookConditionContext;
import dev.latvian.mods.kubejs.server.ServerEventJS;
import net.minecraft.server.level.ServerPlayer;

import java.util.Set;

public class ContentUnlockedEventJS extends ServerEventJS {

    ServerPlayer player;
    Set<BookConditionContext> unlockedContent;

    public ContentUnlockedEventJS(ServerPlayer player, Set<BookConditionContext> unlockedContent) {
        super(player.getServer());
        this.player = player;
        this.unlockedContent = unlockedContent;
    }

    public ServerPlayer getPlayer() {
        return this.player;
    }

    public Set<BookConditionContext> getUnlockedContent() {
        return this.unlockedContent;
    }
}
