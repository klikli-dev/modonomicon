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

        public QoLCategory(ForgeConfigSpec.Builder builder) {
            builder.comment("Quality of Life Settings").push("qol");
            this.enableSmoothZoom = builder.comment("Enable smooth zoom in book categories")
                    .define("enableSmoothZoom", true);
            builder.pop();
        }
    }
}
