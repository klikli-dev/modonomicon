/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.registry;

import com.klikli_dev.modonomicon.Modonomicon;
import com.klikli_dev.modonomicon.command.LoadUnlocksCommand;
import com.klikli_dev.modonomicon.command.ReloadBooksCommand;
import com.klikli_dev.modonomicon.command.ResetBookUnlocksCommand;
import com.klikli_dev.modonomicon.command.SaveUnlocksCommand;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

public class CommandRegistry {

    public static void registerCommands(CommandDispatcher<CommandSourceStack> dispatcher) {
        LiteralCommandNode<CommandSourceStack> modonomiconCommand = dispatcher.register(
                Commands.literal(Modonomicon.MOD_ID)
                        .then(ResetBookUnlocksCommand.register(dispatcher))
                        .then(SaveUnlocksCommand.register(dispatcher))
                        .then(ReloadBooksCommand.register(dispatcher))
        );

        dispatcher.register(Commands.literal(Modonomicon.MOD_ID).redirect(modonomiconCommand));
    }

    public static void registerClientCommands(CommandDispatcher<CommandSourceStack> dispatcher) {

        LiteralCommandNode<CommandSourceStack> modonomiconCommand = dispatcher.register(
                Commands.literal(Modonomicon.MOD_ID)
                        .then(LoadUnlocksCommand.register(dispatcher))
        );

        dispatcher.register(Commands.literal(Modonomicon.MOD_ID).redirect(modonomiconCommand));
    }

}
