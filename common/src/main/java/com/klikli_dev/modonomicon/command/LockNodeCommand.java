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

public class LockNodeCommand implements com.mojang.brigadier.Command<CommandSourceStack> {

    public static final DynamicCommandExceptionType ERROR_UNKNOWN_NODE = new DynamicCommandExceptionType(
            (message) -> Component.translatable(Command.ERROR_UNKNOWN_NODE, message)
    );

    private static final LockNodeCommand CMD = new LockNodeCommand();

    public static ArgumentBuilder<CommandSourceStack, ?> register(CommandDispatcher<CommandSourceStack> dispatcher) {
        return Commands.argument("node_id", IdentifierArgument.id())
                .suggests(ResearchSuggestions.SUGGEST_NODE)
                .executes(CMD);
    }

    @Override
    public int run(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        var nodeId = IdentifierArgument.getId(context, "node_id");

        if (!ResearchDataManager.get().data().nodeIds().contains(nodeId)) {
            throw ERROR_UNKNOWN_NODE.create(nodeId);
        }

        var player = context.getSource().getPlayer();
        var before = BookDataManager.get().getBooks().values().stream()
                .collect(java.util.stream.Collectors.toMap(Book::getId, b -> BookVisibilitySnapshots.collect(player, b)));

        var state = ResearchServices.state().getStateFor(player);
        state.lockNode(nodeId);

        // Revoke required facts and reset values so the node stays locked
        for (var rule : ResearchDataManager.get().data().nodeRules()) {
            if (rule.nodeId().equals(nodeId)) {
                for (var factId : rule.requiredFactIds()) {
                    state.revokeFact(factId);
                }
                for (var valueReq : rule.requiredValueRequirements()) {
                    state.setValue(valueReq.valueId(), 0);
                }
            }
        }

        ResearchServices.state().reevaluate(player);

        for (var book : BookDataManager.get().getBooks().values()) {
            BookVisualStateManager.get().updateVisibilityDrivenUnread(player, book, before.get(book.getId()), BookVisibilitySnapshots.collect(player, book));
        }

        ResearchServices.state().syncFor(player);
        BookVisualStateManager.get().syncFor(player);

        context.getSource().sendSuccess(() -> Component.translatable(Command.SUCCESS_LOCK_NODE, nodeId.toString()), true);
        return 1;
    }
}
