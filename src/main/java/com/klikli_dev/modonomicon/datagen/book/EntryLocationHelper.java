/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.datagen.book;

import com.klikli_dev.modonomicon.Modonomicon;
import net.minecraft.world.phys.Vec2;

public class EntryLocationHelper {

    protected String[] map;

    protected Vec2 offset;

    public void setMap(String... map) {
        this.map = map;
        this.offset = new Vec2(-(int)(map[0].length() / 2.0f), -(int)(map.length / 2.0f));
    }

    public void setOffset(Vec2 offset) {
        this.offset = offset;
    }

    public Vec2 get(Character symbol) {
        int y = 0;
        for (var line : this.map) {
            var x = line.indexOf(symbol);
            if (x != -1) {
                return new Vec2(x, y).add(this.offset);
            }
            y++;
        }
        Modonomicon.LOGGER.warn("Symbol '{}' not found in map", symbol);
        return Vec2.ZERO;
    }
}
