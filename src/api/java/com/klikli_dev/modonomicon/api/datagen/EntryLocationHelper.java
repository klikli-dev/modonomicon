/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.api.datagen;

import com.mojang.logging.LogUtils;
import net.minecraft.world.phys.Vec2;
import org.slf4j.Logger;

public class EntryLocationHelper {

    public static final Logger LOGGER = LogUtils.getLogger();

    protected String[] map;

    protected Vec2 offset;

    public EntryLocationHelper(){

    }

    public void setMap(String... map) {
        this.map = map;
        this.offset = new Vec2(-(int) (map[0].length() / 2.0f), -(int) (map.length / 2.0f));
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
        LOGGER.warn("Symbol '{}' not found in map", symbol);
        return Vec2.ZERO;
    }
}
