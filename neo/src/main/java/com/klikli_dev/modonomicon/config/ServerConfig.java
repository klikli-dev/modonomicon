/*
 * SPDX-FileCopyrightText: 2024 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.config;


import net.neoforged.neoforge.common.ModConfigSpec;

public class ServerConfig {

    private static final ServerConfig instance = new ServerConfig();

    public final UnlockCategory unlockCategory;
    public final ModConfigSpec spec;

    private ServerConfig() {
        var builder = new ModConfigSpec.Builder();
        this.unlockCategory = new UnlockCategory(builder);
        this.spec = builder.build();
    }

    public static ServerConfig get() {
        return instance;
    }

    public static class UnlockCategory {
        public final ModConfigSpec.BooleanValue disableAdvancementLocking;

        public UnlockCategory(ModConfigSpec.Builder builder) {
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
