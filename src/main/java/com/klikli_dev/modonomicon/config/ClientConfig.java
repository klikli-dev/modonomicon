/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.config;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.BooleanValue;

public class ClientConfig {

    private static final ClientConfig instance = new ClientConfig();

    public final QoLCategory qolCategory;
    public final ForgeConfigSpec spec;

    private ClientConfig() {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
        this.qolCategory = new QoLCategory(builder);
        this.spec = builder.build();
    }

    public static ClientConfig get() {
        return instance;
    }

    public static class QoLCategory {
        public final BooleanValue enableSmoothZoom;
        public final BooleanValue storeLastOpenPageWhenClosingEntry;

        public QoLCategory(ForgeConfigSpec.Builder builder) {
            builder.comment("Quality of Life Settings").push("qol");
            this.enableSmoothZoom = builder.comment("Enable smooth zoom in book categories")
                    .define("enableSmoothZoom", true);
            this.storeLastOpenPageWhenClosingEntry = builder.comment("Enable keeping the last open page stored when closing an entry. " +
                            "Regardless of this setting it will be stored when closing the entire book with Esc.")
                    .define("storeLastOpenPageWhenClosingEntry", false);
            builder.pop();
        }
    }
}
