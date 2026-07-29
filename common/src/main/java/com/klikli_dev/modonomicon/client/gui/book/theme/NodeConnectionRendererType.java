/*
 * SPDX-FileCopyrightText: 2026 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.client.gui.book.theme;

public enum NodeConnectionRendererType {
    SPRITE,
    DIRECT;

    public String serializedName() {
        return this.name().toLowerCase();
    }

    public static NodeConnectionRendererType fromString(String name) {
        for (var value : values()) {
            if (value.serializedName().equals(name)) {
                return value;
            }
        }
        return SPRITE;
    }
}
