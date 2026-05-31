/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.command;

import com.klikli_dev.modonomicon.api.ModonomiconConstants.I18n.Command;
import com.klikli_dev.modonomicon.book.Book;
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
import net.minecraft.commands.arguments.IdentifierArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.permissions.Permission;
import net.minecraft.server.permissions.PermissionLevel;
import net.minecraft.server.permissions.Permissions;

public class ResetBookUnlocksCommand implements com.mojang.brigadier.Command<CommandSourceStack> {

    public static final DynamicCommandExceptionType ERROR_UNKNOWN_BOOK = new DynamicCommandExceptionType((message) -> {
        return Component.translatable(Command.ERROR_UNKNOWN_BOOK, message);
    });

    public static final SuggestionProvider<CommandSourceStack> SUGGEST_BOOK = (context, builder) -> {
        var books = BookDataManager.get().getBooks().keySet();
        return SharedSuggestionProvider.suggestResource(books, builder);
    };
    private static final ResetBookUnlocksCommand CMD = new ResetBookUnlocksCommand();


    public static ArgumentBuilder<CommandSourceStack, ?> register(CommandDispatcher<CommandSourceStack> dispatcher) {
        return Commands.literal("reset")
                .requires(Commands.hasPermission(Commands.LEVEL_ALL))
                .then(Commands.argument("book", IdentifierArgument.id())
                        .suggests(SUGGEST_BOOK)
                        .executes(CMD));

    }


    public static Book getBook(CommandContext<CommandSourceStack> pContext, String pName) throws CommandSyntaxException {
        var Identifier = IdentifierArgument.getId(pContext, pName);
        var book = BookDataManager.get().getBook(Identifier);
        if (book == null) {
            throw ERROR_UNKNOWN_BOOK.create(Identifier);
        } else {
            return book;
        }
    }

    @Override
    public int run(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        context.getSource().sendFailure(Component.literal("Reset unlocks command removed."));
        return 0;
    }
}
