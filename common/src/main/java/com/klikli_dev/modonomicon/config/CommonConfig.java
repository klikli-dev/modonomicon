/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class CommonConfig {

    private static final CommonConfig instance = new CommonConfig();

    public final ForgeConfigSpec spec;

    private CommonConfig() {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
        this.spec = builder.build();
    }

    public static CommonConfig get() {
        return instance;
    }
}
