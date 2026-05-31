/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.command;

import com.klikli_dev.modonomicon.api.ModonomiconConstants.I18n.Command;
import com.klikli_dev.modonomicon.book.Book;
import com.klikli_dev.modonomicon.bookstate.BookVisualStateManager;
import com.klikli_dev.modonomicon.bookstate.visual.BookVisibilitySnapshots;
import com.klikli_dev.modonomicon.data.BookDataManager;
import com.klikli_dev.modonomicon.research.ResearchServices;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;

public class ResetResearchCommand implements com.mojang.brigadier.Command<CommandSourceStack> {

    private static final ResetResearchCommand CMD = new ResetResearchCommand();

    public static ArgumentBuilder<CommandSourceStack, ?> register(CommandDispatcher<CommandSourceStack> dispatcher) {
        return Commands.literal("reset")
                .requires(Commands.hasPermission(Commands.LEVEL_ALL))
                .executes(CMD);
    }

    @Override
    public int run(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        var player = context.getSource().getPlayer();
        var before = BookDataManager.get().getBooks().values().stream().collect(java.util.stream.Collectors.toMap(Book::getId, book -> BookVisibilitySnapshots.collect(player, book)));
        ResearchServices.state().resetFor(player);
        for (var book : BookDataManager.get().getBooks().values()) {
            BookVisualStateManager.get().updateVisibilityDrivenUnread(player, book, before.get(book.getId()), BookVisibilitySnapshots.collect(player, book));
        }
        ResearchServices.state().syncFor(player);
        BookVisualStateManager.get().syncFor(player);
        context.getSource().sendSuccess(() -> Component.translatable(Command.SUCCESS_RESET_RESEARCH), true);
        return 1;
    }
}
