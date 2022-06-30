/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.registry;

import com.klikli_dev.modonomicon.Modonomicon;
import com.klikli_dev.modonomicon.command.LoadUnlocksCommand;
import com.klikli_dev.modonomicon.command.ResetBookUnlocksCommand;
import com.klikli_dev.modonomicon.command.SaveUnlocksCommand;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraftforge.client.event.RegisterClientCommandsEvent;
import net.minecraftforge.event.RegisterCommandsEvent;

public class CommandRegistry {

    public static void registerCommands(RegisterCommandsEvent event) {
        LiteralCommandNode<CommandSourceStack> modonomiconCommand = event.getDispatcher().register(
                Commands.literal(Modonomicon.MODID)
                        .then(ResetBookUnlocksCommand.register(event.getDispatcher()))
                        .then(SaveUnlocksCommand.register(event.getDispatcher()))
        );

        event.getDispatcher().register(Commands.literal("modonomicon").redirect(modonomiconCommand));
    }

    public static void registerClientCommands(RegisterClientCommandsEvent event) {

        LiteralCommandNode<CommandSourceStack> modonomiconCommand = event.getDispatcher().register(
                Commands.literal(Modonomicon.MODID)
                        .then(LoadUnlocksCommand.register(event.getDispatcher()))
        );

        event.getDispatcher().register(Commands.literal("modonomicon").redirect(modonomiconCommand));
    }

}
