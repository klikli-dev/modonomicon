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
import com.klikli_dev.modonomicon.commands.FabricLoadUnlocksCommand;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

public class FabricClientCommandRegistry {

    public static void registerClientCommands(CommandDispatcher<FabricClientCommandSource> dispatcher) {

        var modonomiconCommand = dispatcher.register(
                ClientCommandManager.literal(Modonomicon.MOD_ID)
                        .then(FabricLoadUnlocksCommand.register(dispatcher))
        );

        dispatcher.register(ClientCommandManager.literal("modonomicon").redirect(modonomiconCommand));
    }

}
