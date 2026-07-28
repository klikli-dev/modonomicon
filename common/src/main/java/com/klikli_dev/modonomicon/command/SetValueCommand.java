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
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.IdentifierArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

public class SetValueCommand implements com.mojang.brigadier.Command<CommandSourceStack> {

    public static final DynamicCommandExceptionType ERROR_UNKNOWN_VALUE = new DynamicCommandExceptionType(
            (message) -> Component.translatable(Command.ERROR_UNKNOWN_VALUE, message)
    );

    private static final SetValueCommand CMD = new SetValueCommand();

    public static ArgumentBuilder<CommandSourceStack, ?> register(CommandDispatcher<CommandSourceStack> dispatcher) {
        return Commands.argument("value_id", IdentifierArgument.id())
                .suggests(ResearchSuggestions.SUGGEST_VALUE)
                .then(Commands.argument("amount", IntegerArgumentType.integer(0))
                        .executes(CMD));
    }

    @Override
    public int run(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        var valueId = IdentifierArgument.getId(context, "value_id");
        var amount = IntegerArgumentType.getInteger(context, "amount");

        if (!ResearchDataManager.get().data().valueIds().contains(valueId)) {
            throw ERROR_UNKNOWN_VALUE.create(valueId);
        }

        var player = context.getSource().getPlayer();
        var before = BookDataManager.get().getBooks().values().stream()
                .collect(java.util.stream.Collectors.toMap(Book::getId, b -> BookVisibilitySnapshots.collect(player, b)));

        ResearchServices.state().setValue(player, valueId, amount);
        ResearchServices.state().reevaluate(player);

        for (var book : BookDataManager.get().getBooks().values()) {
            BookVisualStateManager.get().updateVisibilityDrivenUnread(player, book, before.get(book.getId()), BookVisibilitySnapshots.collect(player, book));
        }

        ResearchServices.state().syncFor(player);
        BookVisualStateManager.get().syncFor(player);

        context.getSource().sendSuccess(() -> Component.translatable(Command.SUCCESS_SET_VALUE, valueId.toString(), amount), true);
        return 1;
    }
}
