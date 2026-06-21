/*
 * SPDX-FileCopyrightText: 2026 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.command;

import com.klikli_dev.modonomicon.research.ResearchServices;
import com.klikli_dev.modonomicon.research.data.ResearchDataManager;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

public class SetStageCommand {

    public static LiteralArgumentBuilder<CommandSourceStack> register(com.mojang.brigadier.CommandDispatcher<CommandSourceStack> dispatcher) {
        return Commands.literal("stage")
                .then(Commands.argument("player", com.mojang.brigadier.arguments.StringArgumentType.word())
                        .suggests((ctx, b) -> net.minecraft.commands.SharedSuggestionProvider.suggest(ctx.getSource().getServer().getPlayerList().getPlayers().stream().map(p -> p.getName().getString()), b))
                        .then(Commands.argument("node", com.mojang.brigadier.arguments.StringArgumentType.word())
                                .suggests(ResearchSuggestions.SUGGEST_NODE)
                                .then(Commands.argument("stageIndex", IntegerArgumentType.integer(0))
                                        .executes(SetStageCommand::run))));
    }

    private static int run(CommandContext<CommandSourceStack> context) {
        var source = context.getSource();
        var playerName = com.mojang.brigadier.arguments.StringArgumentType.getString(context, "player");
        var nodeId = Identifier.parse(com.mojang.brigadier.arguments.StringArgumentType.getString(context, "node"));
        var stageIndex = IntegerArgumentType.getInteger(context, "stageIndex");

        if (!ResearchDataManager.get().data().nodeIds().contains(nodeId)) {
            source.sendFailure(Component.translatable("modonomicon.command.error.unknown_node", nodeId.toString()));
            return 0;
        }

        var player = source.getServer().getPlayerList().getPlayerByName(playerName);
        if (player == null) {
            source.sendFailure(Component.translatable("commands.player.unknown"));
            return 0;
        }

        ResearchServices.state().setNodeStage(player, nodeId, stageIndex);
        ResearchServices.state().reevaluate(player);
        ResearchServices.state().syncFor(player);

        source.sendSuccess(() -> Component.translatable("modonomicon.command.stage.set", playerName, nodeId.toString(), stageIndex), false);
        return 1;
    }
}
