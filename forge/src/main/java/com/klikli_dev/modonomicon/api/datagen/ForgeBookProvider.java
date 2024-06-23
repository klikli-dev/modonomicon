// SPDX-FileCopyrightText: 2024 klikli-dev
//
// SPDX-License-Identifier: MIT

package com.klikli_dev.modonomicon.api.datagen;

import net.minecraftforge.data.event.GatherDataEvent;

import java.util.List;

public class ForgeBookProvider {
    public static BookProvider of(GatherDataEvent event, BookSubProvider... subProviders) {
        return new BookProvider(event.getGenerator().getPackOutput(), event.getLookupProvider(), event.getModContainer().getModId(), List.of(subProviders));
    }
}
