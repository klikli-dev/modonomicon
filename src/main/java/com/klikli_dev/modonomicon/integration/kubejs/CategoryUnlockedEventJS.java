/*
 * SPDX-FileCopyrightText: 2023 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.integration.kubejs;

import com.klikli_dev.modonomicon.book.Book;
import com.klikli_dev.modonomicon.book.BookCategory;
import com.klikli_dev.modonomicon.book.conditions.BookCondition;
import com.klikli_dev.modonomicon.book.conditions.context.BookConditionCategoryContext;
import com.klikli_dev.modonomicon.book.conditions.context.BookConditionContext;
import dev.latvian.mods.kubejs.server.ServerEventJS;
import net.minecraft.server.level.ServerPlayer;

import java.util.Set;

public class CategoryUnlockedEventJS extends ServerEventJS {

    ServerPlayer player;
    BookConditionCategoryContext unlockedContent;

    public CategoryUnlockedEventJS(ServerPlayer player, BookConditionCategoryContext unlockedContent) {
        super(player.getServer());
        this.player = player;
        this.unlockedContent = unlockedContent;
    }

    public ServerPlayer getPlayer() {
        return this.player;
    }

    public BookConditionCategoryContext getUnlockedContent() {
        return this.unlockedContent;
    }

    public Book getBook() {
        return this.unlockedContent.getBook();
    }

    public BookCategory getCategory() {
        return this.unlockedContent.getCategory();
    }

    public BookCondition getCondition() {
        return this.unlockedContent.getCategory().getCondition();
    }
}
