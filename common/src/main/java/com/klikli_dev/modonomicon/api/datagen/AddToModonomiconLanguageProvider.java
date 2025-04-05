// SPDX-FileCopyrightText: 2024 klikli-dev
//
// SPDX-License-Identifier: MIT

package com.klikli_dev.modonomicon.api.datagen;

import net.minecraft.data.PackOutput;
import org.jetbrains.annotations.NotNull;

public class AddToModonomiconLanguageProvider extends AbstractModonomiconLanguageProvider {

    /**
     * Creates a new language provider.
     *
     * @param output         the pack output to write to.
     * @param targetModId    the mod id to generate a language file for.
     * @param locale         the locale this provider should generate.
     * @param cachedProvider the cached provider - its contents will be written into this provider.
     */
    public AddToModonomiconLanguageProvider(PackOutput output, String targetModId, String locale, ModonomiconLanguageProvider cachedProvider) {
        super(output, targetModId, locale, cachedProvider);
    }

    public AddToModonomiconLanguageProvider(PackOutput output, String targetModId, String locale) {
        super(output, targetModId, locale);
    }

    @Override
    protected void addTranslations() {
        //usually translations are added in the AddTo providers for book, category, entry.
    }

    @Override
    public @NotNull String getName() {
        return "Languages: " + this.locale + " for " + this.modId;
    }

}
