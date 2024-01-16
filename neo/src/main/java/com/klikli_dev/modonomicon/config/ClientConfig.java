/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.config;


import net.neoforged.neoforge.common.ModConfigSpec;

public class ClientConfig {

    private static final ClientConfig instance = new ClientConfig();

    public final QoLCategory qolCategory;
    public final ModConfigSpec spec;

    private ClientConfig() {
        var builder = new ModConfigSpec.Builder();
        this.qolCategory = new QoLCategory(builder);
        this.spec = builder.build();
    }

    public static ClientConfig get() {
        return instance;
    }

    public static class QoLCategory {
        public final ModConfigSpec.BooleanValue enableSmoothZoom;
        public final ModConfigSpec.BooleanValue storeLastOpenPageWhenClosingEntry;

        public QoLCategory(ModConfigSpec.Builder builder) {
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
