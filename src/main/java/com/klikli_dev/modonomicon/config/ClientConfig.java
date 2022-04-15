/*
 * LGPL-3.0
 *
 * Copyright (C) 2021 klikli-dev
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

package com.klikli_dev.modonomicon.config;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.BooleanValue;

public class ClientConfig  {

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
