/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.registry;

import com.klikli_dev.modonomicon.Modonomicon;
import com.klikli_dev.modonomicon.commands.FabricLoadUnlocksCommand;
import com.mojang.brigadier.CommandDispatcher;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;

public class FabricClientCommandRegistry {

    public static void registerClientCommands(CommandDispatcher<FabricClientCommandSource> dispatcher) {

        var modonomiconCommand = dispatcher.register(
                ClientCommandManager.literal(Modonomicon.MOD_ID+"-client")
                        .then(FabricLoadUnlocksCommand.register(dispatcher))
        );

        dispatcher.register(ClientCommandManager.literal(Modonomicon.MOD_ID+"-client").redirect(modonomiconCommand));
    }

}
