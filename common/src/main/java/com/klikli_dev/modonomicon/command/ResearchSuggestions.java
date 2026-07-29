/*
 * SPDX-FileCopyrightText: 2026 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.command;

import com.klikli_dev.modonomicon.data.BookDataManager;
import com.klikli_dev.modonomicon.research.data.ResearchDataManager;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;

public class ResearchSuggestions {

    public static final SuggestionProvider<CommandSourceStack> SUGGEST_BOOK = (context, builder) -> {
        var books = BookDataManager.get().getBooks().keySet();
        return SharedSuggestionProvider.suggestResource(books, builder);
    };

    public static final SuggestionProvider<CommandSourceStack> SUGGEST_FACT = (context, builder) -> {
        var facts = ResearchDataManager.get().data().factIds();
        return SharedSuggestionProvider.suggestResource(facts, builder);
    };

    public static final SuggestionProvider<CommandSourceStack> SUGGEST_NODE = (context, builder) -> {
        var nodes = ResearchDataManager.get().data().nodeIds();
        return SharedSuggestionProvider.suggestResource(nodes, builder);
    };

    public static final SuggestionProvider<CommandSourceStack> SUGGEST_VALUE = (context, builder) -> {
        var values = ResearchDataManager.get().data().valueIds();
        return SharedSuggestionProvider.suggestResource(values, builder);
    };

    public static final SuggestionProvider<CommandSourceStack> SUGGEST_GRAPH = (context, builder) -> {
        var graphs = ResearchDataManager.get().data().graphNodeIds().keySet();
        return SharedSuggestionProvider.suggestResource(graphs, builder);
    };
}
