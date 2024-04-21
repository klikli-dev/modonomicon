/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.config;


import net.neoforged.neoforge.common.ModConfigSpec;

import java.util.ArrayList;
import java.util.List;

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

        public final ModConfigSpec.ConfigValue<List<String>> fontFallbackLocales;

        public QoLCategory(ModConfigSpec.Builder builder) {
            builder.comment("Quality of Life Settings").push("qol");
            this.enableSmoothZoom = builder.comment("Enable smooth zoom in book categories")
                    .define("enableSmoothZoom", true);
            this.storeLastOpenPageWhenClosingEntry = builder.comment("Enable keeping the last open page stored when closing an entry. " +
                            "Regardless of this setting it will be stored when closing the entire book with Esc.")
                    .define("storeLastOpenPageWhenClosingEntry", false);

            var fontFallbackLocalesDefault = new ArrayList<>(List.of("zh_cn", "ja_jp", "ko_kr")); //wrap in arraylist because immutable lists cause issues with the config system
            this.fontFallbackLocales = builder.comment("If your locale is not supported by the default Modonomicon font, indicated by the book just rendering blocky shapes instead of characters, add your locale to this list to fall back to the builtin Minecraft font.")
                    .define("fontFallbackLocales", fontFallbackLocalesDefault);

            builder.pop();
        }
    }
}
