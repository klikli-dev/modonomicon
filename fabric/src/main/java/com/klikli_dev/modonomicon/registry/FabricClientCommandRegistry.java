/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.registry;

import com.klikli_dev.modonomicon.Modonomicon;
import com.mojang.brigadier.CommandDispatcher;
import net.fabricmc.fabric.api.client.command.v2.ClientCommands;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;

public class FabricClientCommandRegistry {

    public static void registerClientCommands(CommandDispatcher<FabricClientCommandSource> dispatcher) {

        var modonomiconCommand = dispatcher.register(
                ClientCommands.literal(Modonomicon.MOD_ID + "-client")
        );

        dispatcher.register(ClientCommands.literal(Modonomicon.MOD_ID + "-client").redirect(modonomiconCommand));
    }

}
