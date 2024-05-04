/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.command;

import com.klikli_dev.modonomicon.api.ModonomiconConstants;
import com.klikli_dev.modonomicon.networking.ReloadResourcesOnClientMessage;
import com.klikli_dev.modonomicon.platform.Services;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;

public class ReloadBooksCommand implements com.mojang.brigadier.Command<CommandSourceStack> {

    private static final ReloadBooksCommand CMD = new ReloadBooksCommand();


    public static ArgumentBuilder<CommandSourceStack, ?> register(CommandDispatcher<CommandSourceStack> dispatcher) {
        return Commands.literal("reload")
                .requires(cs -> cs.hasPermission(2))
                .executes(CMD);
    }


    @Override
    public int run(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        Services.NETWORK.sendTo(context.getSource().getPlayer(), ReloadResourcesOnClientMessage.INSTANCE);
        context.getSource().sendSuccess(() -> Component.translatable(ModonomiconConstants.I18n.Command.RELOAD_REQUESTED), true);
        return 1;
    }
}
