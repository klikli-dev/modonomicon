/*
 * SPDX-FileCopyrightText: 2026 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.registry;

import com.klikli_dev.modonomicon.client.gui.book.theme.BookTheme;
import com.klikli_dev.modonomicon.client.gui.book.theme.BookThemeData;
import com.klikli_dev.modonomicon.client.gui.book.theme.BookThemeFactory;
import com.klikli_dev.modonomicon.client.gui.book.theme.defaults.DefaultBookTheme;
import it.unimi.dsi.fastutil.objects.Object2ObjectMaps;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.resources.Identifier;

import java.util.Map;

public final class ThemeRegistry {

    private static final Map<Identifier, BookThemeFactory> THEME_FACTORIES = Object2ObjectMaps.synchronize(new Object2ObjectOpenHashMap<>());
    public static final BookThemeFactory DEFAULT = register(DefaultBookTheme.ID, DefaultBookTheme::new);

    private ThemeRegistry() {
    }

    public static void bootstrap() {
    }

    public static BookThemeFactory register(Identifier id, BookThemeFactory factory) {
        THEME_FACTORIES.put(id, factory);
        return factory;
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
