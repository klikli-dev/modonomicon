/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.command;

import com.klikli_dev.modonomicon.api.ModonomiconConstants.I18n.Command;
import com.klikli_dev.modonomicon.bookstate.BookUnlockStateManager;
import com.klikli_dev.modonomicon.networking.SendUnlockCodeToClientMessage;
import com.klikli_dev.modonomicon.platform.Services;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.network.chat.Component;

public class SaveUnlocksCommand implements com.mojang.brigadier.Command<CommandSourceStack> {

    private static final SaveUnlocksCommand CMD = new SaveUnlocksCommand();


    public static ArgumentBuilder<CommandSourceStack, ?> register(CommandDispatcher<CommandSourceStack> dispatcher) {

        return Commands.literal("save_progress")
                .requires(cs -> cs.hasPermission(1))
                .then(Commands.argument("book", ResourceLocationArgument.id())
                        .suggests(ResetBookUnlocksCommand.SUGGEST_BOOK)
                        .executes(CMD));

    }


    @Override
    public int run(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        var book = ResetBookUnlocksCommand.getBook(context, "book");

        var code = BookUnlockStateManager.get().getUnlockCodeFor(context.getSource().getPlayer(), book);

        Services.NETWORK.sendToSplit(context.getSource().getPlayer(), new SendUnlockCodeToClientMessage(code));

        context.getSource().sendSuccess(() -> Component.translatable(Command.SUCCESS_SAVE_PROGRESS, Component.translatable(book.getName())), true);
        return 1;
    }
}
