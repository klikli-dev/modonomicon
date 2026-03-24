/*
 * SPDX-FileCopyrightText: 2024 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.config;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.BooleanValue;

public class ServerConfig {

    private static final ServerConfig instance = new ServerConfig();

    public final UnlockCategory unlockCategory;
    public final ForgeConfigSpec spec;

    private ServerConfig() {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
        this.unlockCategory = new UnlockCategory(builder);
        this.spec = builder.build();
    }

    public static ServerConfig get() {
        return instance;
    }

    public static class UnlockCategory {
        public final BooleanValue disableAdvancementLocking;

        public UnlockCategory(ForgeConfigSpec.Builder builder) {
            builder.comment("Unlock Settings").push("unlock");
            this.disableAdvancementLocking = builder.comment(
                            "If true, advancement-based unlock conditions will always return true, " +
                                    "effectively disabling advancement-gated progression in all books. " +
                                    "Other unlock conditions (e.g. entry read, mod loaded) are not affected.")
                    .define("disableAdvancementLocking", false);
            builder.pop();
        }
    }
}
