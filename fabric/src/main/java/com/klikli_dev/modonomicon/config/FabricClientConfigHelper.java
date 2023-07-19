/*
 * SPDX-FileCopyrightText: 2023 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.config;

import com.klikli_dev.modonomicon.platform.services.ClientConfigHelper;

public class FabricClientConfigHelper implements ClientConfigHelper {
    @Override
    public boolean enableSmoothZoom() {
        return ClientConfig.enableSmoothZoom.getValue();
    }

    @Override
    public boolean storeLastOpenPageWhenClosingEntry() {
        return ClientConfig.storeLastOpenPageWhenClosingEntry.getValue();
    }
}
