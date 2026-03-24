/*
 * SPDX-FileCopyrightText: 2024 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.config;

import com.klikli_dev.modonomicon.platform.services.ServerConfigHelper;

public class FabricServerConfigHelper implements ServerConfigHelper {
    @Override
    public boolean disableAdvancementLocking() {
        return ServerConfig.disableAdvancementLocking.getValue();
    }
}
