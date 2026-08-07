/*
 * SPDX-FileCopyrightText: 2024 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class ServerConfig {

    private static final ServerConfig instance = new ServerConfig();
    public final ForgeConfigSpec spec;

    private ServerConfig() {
        var builder = new ForgeConfigSpec.Builder();
        this.spec = builder.build();
    }

    public static ServerConfig get() {
        return instance;
    }
}
