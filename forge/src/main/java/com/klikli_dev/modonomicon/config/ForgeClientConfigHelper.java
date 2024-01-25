/*
 * SPDX-FileCopyrightText: 2023 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.config;

import com.klikli_dev.modonomicon.platform.services.ClientConfigHelper;

import java.util.List;

public class ForgeClientConfigHelper implements ClientConfigHelper {
    @Override
    public boolean enableSmoothZoom() {
        return ClientConfig.get().qolCategory.enableSmoothZoom.get();
    }

    @Override
    public boolean storeLastOpenPageWhenClosingEntry() {
        return ClientConfig.get().qolCategory.storeLastOpenPageWhenClosingEntry.get();
    }

    @Override
    public List<String> fontFallbackLocales() {
        return ClientConfig.get().qolCategory.fontFallbackLocales.get();
    }


}
