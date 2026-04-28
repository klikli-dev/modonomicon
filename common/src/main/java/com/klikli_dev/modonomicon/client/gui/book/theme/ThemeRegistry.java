/*
 * SPDX-FileCopyrightText: 2026 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.client.gui.book.theme;

import com.klikli_dev.modonomicon.api.ModonomiconConstants;
import com.klikli_dev.modonomicon.client.gui.book.theme.defaults.DefaultBookTheme;
import it.unimi.dsi.fastutil.objects.Object2ObjectMaps;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.resources.Identifier;

import java.util.Map;

public class ThemeRegistry {

    private static final Map<Identifier, BookThemeFactory> THEME_FACTORIES = Object2ObjectMaps.synchronize(new Object2ObjectOpenHashMap<>());

    public static void registerThemes() {
        registerTheme(ModonomiconConstants.Data.Theme.DEFAULT_THEME_TYPE, DefaultBookTheme::new);
    }

    public static void registerTheme(Identifier id, BookThemeFactory factory) {
        THEME_FACTORIES.put(id, factory);
    }

    public static BookThemeFactory getThemeFactory(Identifier id) {
        var factory = THEME_FACTORIES.get(id);
        if (factory == null) {
            throw new IllegalArgumentException("No theme registered for theme type " + id);
        }
        return factory;
    }

    public static BookTheme createTheme(BookThemeData data) {
        return getThemeFactory(data.type()).create(data);
    }
}
