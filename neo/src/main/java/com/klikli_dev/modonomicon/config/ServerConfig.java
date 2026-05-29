/*
 * SPDX-FileCopyrightText: 2024 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.config;


import net.neoforged.neoforge.common.ModConfigSpec;

public class ServerConfig {

    private static final ServerConfig instance = new ServerConfig();
    public final ModConfigSpec spec;

    private ServerConfig() {
        var builder = new ModConfigSpec.Builder();
        this.spec = builder.build();
    }

    public static ServerConfig get() {
        return instance;
    }

}
