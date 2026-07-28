/*
 * SPDX-FileCopyrightText: 2026 klikli-dev
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
import com.klikli_dev.modonomicon.research.data.ResearchDataManager;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.IdentifierArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

public class RevokeFactCommand implements com.mojang.brigadier.Command<CommandSourceStack> {

    public static final DynamicCommandExceptionType ERROR_UNKNOWN_FACT = new DynamicCommandExceptionType(
            (message) -> Component.translatable(Command.ERROR_UNKNOWN_FACT, message)
    );

    private static final RevokeFactCommand CMD = new RevokeFactCommand();

    public static ArgumentBuilder<CommandSourceStack, ?> register(CommandDispatcher<CommandSourceStack> dispatcher) {
        return Commands.argument("fact_id", IdentifierArgument.id())
                .suggests(ResearchSuggestions.SUGGEST_FACT)
                .executes(CMD);
    }

    @Override
    public int run(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        var factId = IdentifierArgument.getId(context, "fact_id");

        if (!ResearchDataManager.get().data().factIds().contains(factId)) {
            throw ERROR_UNKNOWN_FACT.create(factId);
        }

        var player = context.getSource().getPlayer();
        var before = BookDataManager.get().getBooks().values().stream()
                .collect(java.util.stream.Collectors.toMap(Book::getId, b -> BookVisibilitySnapshots.collect(player, b)));

        ResearchServices.state().revokeFact(player, factId);
        ResearchServices.state().reevaluate(player);

        for (var book : BookDataManager.get().getBooks().values()) {
            BookVisualStateManager.get().updateVisibilityDrivenUnread(player, book, before.get(book.getId()), BookVisibilitySnapshots.collect(player, book));
        }

        ResearchServices.state().syncFor(player);
        BookVisualStateManager.get().syncFor(player);

        context.getSource().sendSuccess(() -> Component.translatable(Command.SUCCESS_REVOKE_FACT, factId.toString()), true);
        return 1;
    }
}
