/*
 * SPDX-FileCopyrightText: 2026 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.registry;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import com.klikli_dev.modonomicon.Modonomicon;
import com.klikli_dev.modonomicon.data.BookDynamicTextMacroLoader;
import com.klikli_dev.modonomicon.data.DynamicTextMacroType;
import net.minecraft.resources.Identifier;

import java.util.Collection;
import java.util.Map;
import java.util.Random;

public final class DynamicTextMacroRegistry {

    private static final Multimap<Identifier, DynamicTextMacroType> TYPES = Multimaps.synchronizedListMultimap(ArrayListMultimap.create());

    public static final DynamicTextMacroType DEMO = register(Modonomicon.loc("demo"), () -> Map.of("my.test.macro", String.valueOf(new Random().nextDouble())));

    private DynamicTextMacroRegistry() {
    }

    public static void bootstrap() {
    }

    public static DynamicTextMacroType register(Identifier forBookId, BookDynamicTextMacroLoader loader) {
        DynamicTextMacroType type = new DynamicTextMacroType(forBookId, loader);
        TYPES.put(forBookId, type);
        return type;
    }

    public static Collection<BookDynamicTextMacroLoader> getLoaders(Identifier bookId) {
        return TYPES.get(bookId).stream().map(DynamicTextMacroType::loader).toList();
    }

    public static Collection<DynamicTextMacroType> getTypes(Identifier bookId) {
        return TYPES.get(bookId);
    }
}
