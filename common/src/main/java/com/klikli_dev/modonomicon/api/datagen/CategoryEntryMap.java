/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.api.datagen;

import com.mojang.logging.LogUtils;
import net.minecraft.world.phys.Vec2;
import org.slf4j.Logger;

import java.util.Arrays;

/**
 * A helper class to visualize entry positions within a category.
 * Allows to map characters to positions in a 2D grid which will be used as coordinates for entries within the category for rendering.
 */
public class CategoryEntryMap {

    public static final Logger LOGGER = LogUtils.getLogger();

    protected String[] map;

    protected Vec2 offset;

    public CategoryEntryMap() {

    }

    public void setMap(String... map) {
        //we remove whitespaces, including tabs
        this.map = Arrays.stream(map).map(s -> s.replaceAll("\\s", "")).toArray(String[]::new);

        this.offset = new Vec2(-(int) (map[0].length() / 2.0f), -(int) (map.length / 2.0f));
    }

    public void setOffset(Vec2 offset) {
        this.offset = offset;
    }

    /**
     * Matches a single character in the map as a symbol to represent an entry.
     */
    public Vec2 get(Character symbol) {
        int y = 0;
        for (var line : this.map) {
            int x = 0;

            //get x coordinate in line. Each char is one position, but all characters within ( and ) count only as one
            boolean inBracket = false;
            for (var c : line.toCharArray()) {
                //do not match a char symbol within brackets, because everything in there is part of a larger string symbol
                if (c == symbol && !inBracket) {
                    return new Vec2(x, y).add(this.offset);
                } else if (c == '(') {
                    inBracket = true;
                } else if (c == ')') {
                    inBracket = false;
                    x++;
                } else if (!inBracket) {
                    x++;
                }
            }
            y++;
        }
        LOGGER.warn("Symbol '{}' not found in map", symbol);
        return Vec2.ZERO;
    }

    /**
     * Matches a string in the map as a symbol wrapped in ( and ) to represent an entry.
     * The string must be surrounded by ( and ) in the map and may not contain any whitespace characters.
     * You can hand over the symbol with or without brackets to this method.
     */
    public Vec2 get(String symbol) {

        symbol = symbol.replace("(", "");
        symbol = symbol.replace(")", "");

        int y = 0;
        for (var line : this.map) {
            int x = 0;
            //get x coordinate in line. Each char is one position, but all characters within ( and ) count only as one

            boolean inBracket = false;
            String currentSymbol = "";
            for (var c : line.toCharArray()) {
                if (c == '(') {
                    inBracket = true;
                } else if (c == ')') {
                    inBracket = false;
                    if (currentSymbol.equals(symbol)) {
                        return new Vec2(x, y).add(this.offset);
                    }
                    x++;
                    currentSymbol = "";
                } else if (inBracket) {
                    currentSymbol += c;
                } else {
                    x++;
                }
            }
            y++;
        }
        LOGGER.warn("Symbol '({})' not found in map", symbol);
        return Vec2.ZERO;
    }
}
