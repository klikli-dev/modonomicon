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

public class ResetGraphResearchCommand implements com.mojang.brigadier.Command<CommandSourceStack> {

    public static final DynamicCommandExceptionType ERROR_UNKNOWN_GRAPH = new DynamicCommandExceptionType(
            (message) -> Component.literal("Unknown graph: " + message)
    );

    private static final ResetGraphResearchCommand CMD = new ResetGraphResearchCommand();

    public static ArgumentBuilder<CommandSourceStack, ?> register(CommandDispatcher<CommandSourceStack> dispatcher) {
        return Commands.argument("graph_id", IdentifierArgument.id())
                .suggests(ResearchSuggestions.SUGGEST_GRAPH)
                .executes(CMD);
    }

    @Override
    public int run(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        var graphId = IdentifierArgument.getId(context, "graph_id");
        var data = ResearchDataManager.get().data();

        if (!data.graphNodeIds().containsKey(graphId)) {
            throw ERROR_UNKNOWN_GRAPH.create(graphId);
        }

        var player = context.getSource().getPlayer();
        var before = BookDataManager.get().getBooks().values().stream()
                .collect(java.util.stream.Collectors.toMap(Book::getId, b -> BookVisibilitySnapshots.collect(player, b)));

        var state = ResearchServices.state().getStateFor(player);

        // Lock all nodes in this graph
        for (var nodeId : data.graphNodeIds().getOrDefault(graphId, java.util.Set.of())) {
            state.lockNode(nodeId);
        }

        // Revoke all facts in this graph
        for (var factId : data.graphFactIds().getOrDefault(graphId, java.util.Set.of())) {
            state.revokeFact(factId);
        }

        // Reset all values in this graph
        for (var valueId : data.graphValueIds().getOrDefault(graphId, java.util.Set.of())) {
            state.setValue(valueId, 0);
        }

        ResearchServices.state().reevaluate(player);

        for (var book : BookDataManager.get().getBooks().values()) {
            BookVisualStateManager.get().updateVisibilityDrivenUnread(player, book, before.get(book.getId()), BookVisibilitySnapshots.collect(player, book));
        }

        ResearchServices.state().syncFor(player);
        BookVisualStateManager.get().syncFor(player);

        context.getSource().sendSuccess(() -> Component.translatable(Command.SUCCESS_RESET_BOOK, graphId.toString()), true);
        return 1;
    }
}
