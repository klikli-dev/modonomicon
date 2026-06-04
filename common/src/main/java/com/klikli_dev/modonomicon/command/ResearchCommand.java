/*
 * SPDX-FileCopyrightText: 2026 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.ArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

public class ResearchCommand {

    public static ArgumentBuilder<CommandSourceStack, ?> register(CommandDispatcher<CommandSourceStack> dispatcher) {
        var resetCmd = new ResetResearchCommand();
        return Commands.literal("research")
                .requires(Commands.hasPermission(Commands.LEVEL_MODERATORS))
                .then(Commands.literal("reset")
                        .executes(resetCmd)
                        .then(Commands.literal("book")
                                .then(ResetBookResearchCommand.register(dispatcher)))
                        .then(Commands.literal("graph")
                                .then(ResetGraphResearchCommand.register(dispatcher))))
                .then(Commands.literal("grant")
                        .then(Commands.literal("fact")
                                .then(GrantFactCommand.register(dispatcher))))
                .then(Commands.literal("revoke")
                        .then(Commands.literal("fact")
                                .then(RevokeFactCommand.register(dispatcher))))
                .then(Commands.literal("unlock")
                        .then(Commands.literal("node")
                                .then(UnlockNodeCommand.register(dispatcher))))
                .then(Commands.literal("lock")
                        .then(Commands.literal("node")
                                .then(LockNodeCommand.register(dispatcher))))
                .then(Commands.literal("set")
                        .then(Commands.literal("value")
                                .then(SetValueCommand.register(dispatcher)))
                        .then(Commands.literal("stage")
                                .then(SetStageCommand.register(dispatcher))));
    }
}
