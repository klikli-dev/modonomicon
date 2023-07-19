/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.commands;

import com.klikli_dev.modonomicon.api.ModonomiconConstants.I18n.Command;
import com.klikli_dev.modonomicon.networking.SendUnlockCodeToServerMessage;
import com.klikli_dev.modonomicon.platform.Services;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;

import java.util.Base64;

public class FabricLoadUnlocksCommand implements com.mojang.brigadier.Command<FabricClientCommandSource> {

    private static final FabricLoadUnlocksCommand CMD = new FabricLoadUnlocksCommand();


    public static ArgumentBuilder<FabricClientCommandSource, ?> register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        return ClientCommandManager.literal("load_progress")
                .requires(cs -> cs.hasPermission(1))
                .executes(CMD);
    }

    @Override
    public int run(CommandContext<FabricClientCommandSource> context) throws CommandSyntaxException {
        var code = Minecraft.getInstance().keyboardHandler.getClipboard();

        try {
            if (code.isEmpty())
                throw new IllegalArgumentException("No code in clipboard.");

            var decoded = Base64.getDecoder().decode(code);

            if (decoded.length == 0)
                throw new IllegalArgumentException("Decoded code is zero-length.");

            Services.NETWORK.sendToServer(new SendUnlockCodeToServerMessage(code));

            return 1;
        } catch (Exception e) {
            context.getSource().sendError(Component.translatable(Command.ERROR_LOAD_PROGRESS_CLIENT, code));
            return 0;
        }
    }
}
