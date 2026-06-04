/*
 * SPDX-FileCopyrightText: 2026 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.command;

import com.klikli_dev.modonomicon.api.ModonomiconConstants.I18n.Command;
import com.klikli_dev.modonomicon.book.Book;
import com.klikli_dev.modonomicon.book.conditions.BookAndCondition;
import com.klikli_dev.modonomicon.book.conditions.BookCondition;
import com.klikli_dev.modonomicon.book.conditions.BookOrCondition;
import com.klikli_dev.modonomicon.book.conditions.BookResearchNodeUnlockedCondition;
import com.klikli_dev.modonomicon.book.conditions.BookResearchStageCompletedCondition;
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

import java.util.HashSet;
import java.util.Set;

public class ResetBookResearchCommand implements com.mojang.brigadier.Command<CommandSourceStack> {

    public static final DynamicCommandExceptionType ERROR_UNKNOWN_BOOK = new DynamicCommandExceptionType(
            (message) -> Component.translatable(Command.ERROR_UNKNOWN_BOOK, message)
    );

    private static final ResetBookResearchCommand CMD = new ResetBookResearchCommand();

    public static ArgumentBuilder<CommandSourceStack, ?> register(CommandDispatcher<CommandSourceStack> dispatcher) {
        return Commands.argument("book_id", IdentifierArgument.id())
                .suggests(ResearchSuggestions.SUGGEST_BOOK)
                .executes(CMD);
    }

    @Override
    public int run(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        var bookId = IdentifierArgument.getId(context, "book_id");
        var book = BookDataManager.get().getBook(bookId);
        if (book == null) {
            throw ERROR_UNKNOWN_BOOK.create(bookId);
        }

        var player = context.getSource().getPlayer();
        var before = BookDataManager.get().getBooks().values().stream()
                .collect(java.util.stream.Collectors.toMap(Book::getId, b -> BookVisibilitySnapshots.collect(player, b)));

        var nodeIdsToReset = collectNodeIdsFromBook(book);
        var state = ResearchServices.state().getStateFor(player);

        for (var nodeId : nodeIdsToReset) {
            state.lockNode(nodeId);
            state.setNodeStageIndex(nodeId, 0);

            // Find the node rule to revoke related facts and reset values
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
        }

        ResearchServices.state().reevaluate(player);

        for (var b : BookDataManager.get().getBooks().values()) {
            BookVisualStateManager.get().updateVisibilityDrivenUnread(player, b, before.get(b.getId()), BookVisibilitySnapshots.collect(player, b));
        }

        ResearchServices.state().syncFor(player);
        BookVisualStateManager.get().syncFor(player);

        context.getSource().sendSuccess(() -> Component.translatable(Command.SUCCESS_RESET_BOOK, bookId.toString()), true);
        return 1;
    }

    private Set<Identifier> collectNodeIdsFromBook(Book book) {
        var nodeIds = new HashSet<Identifier>();
        for (var entry : book.getEntries().values()) {
            collectNodeIdsFromCondition(entry.getCondition(), nodeIds);
        }
        return nodeIds;
    }

    private void collectNodeIdsFromCondition(BookCondition condition, Set<Identifier> nodeIds) {
        if (condition instanceof BookResearchNodeUnlockedCondition researchCondition) {
            nodeIds.add(researchCondition.nodeId());
        } else if (condition instanceof BookResearchStageCompletedCondition stageCondition) {
            nodeIds.add(stageCondition.nodeId());
        } else if (condition instanceof BookAndCondition andCondition) {
            for (var child : andCondition.children()) {
                collectNodeIdsFromCondition(child, nodeIds);
            }
        } else if (condition instanceof BookOrCondition orCondition) {
            for (var child : orCondition.children()) {
                collectNodeIdsFromCondition(child, nodeIds);
            }
        }
    }
}
