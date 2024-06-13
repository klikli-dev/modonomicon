/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.command;

import com.klikli_dev.modonomicon.api.ModonomiconConstants.I18n.Command;
import com.klikli_dev.modonomicon.book.Book;
import com.klikli_dev.modonomicon.bookstate.BookUnlockStateManager;
import com.klikli_dev.modonomicon.data.BookDataManager;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.network.chat.Component;

public class ResetBookUnlocksCommand implements com.mojang.brigadier.Command<CommandSourceStack> {

    public static final DynamicCommandExceptionType ERROR_UNKNOWN_BOOK = new DynamicCommandExceptionType((message) -> {
        return Component.translatable(Command.ERROR_UNKNOWN_BOOK, message);
    });

    public static final SuggestionProvider<CommandSourceStack> SUGGEST_BOOK = (context, builder) -> {
        var books = BookUnlockStateManager.get().getBooksFor(context.getSource().getPlayer());
        return SharedSuggestionProvider.suggestResource(books, builder);
    };
    private static final ResetBookUnlocksCommand CMD = new ResetBookUnlocksCommand();


    public static ArgumentBuilder<CommandSourceStack, ?> register(CommandDispatcher<CommandSourceStack> dispatcher) {
        return Commands.literal("reset")
                .requires(cs -> cs.hasPermission(1))
                .then(Commands.argument("book", ResourceLocationArgument.id())
                        .suggests(SUGGEST_BOOK)
                        .executes(CMD));

    }


    public static Book getBook(CommandContext<CommandSourceStack> pContext, String pName) throws CommandSyntaxException {
        var resourcelocation = ResourceLocationArgument.getId(pContext, pName);
        var book = BookDataManager.get().getBook(resourcelocation);
        if (book == null) {
            throw ERROR_UNKNOWN_BOOK.create(resourcelocation);
        } else {
            return book;
        }
    }

    @Override
    public int run(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        var book = getBook(context, "book");
        BookUnlockStateManager.get().resetFor(context.getSource().getPlayer(), book);
        BookUnlockStateManager.get().updateAndSyncFor(context.getSource().getPlayer());
        context.getSource().sendSuccess(() -> Component.translatable(Command.SUCCESS_RESET_BOOK, Component.translatable(book.getName())), true);
        return 1;
    }
}
