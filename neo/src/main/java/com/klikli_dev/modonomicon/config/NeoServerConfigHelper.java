/*
 * SPDX-FileCopyrightText: 2024 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.config;

import com.klikli_dev.modonomicon.platform.services.ServerConfigHelper;

public class NeoServerConfigHelper implements ServerConfigHelper {
    @Override
    public boolean disableAdvancementLocking() {
        return ServerConfig.get().unlockCategory.disableAdvancementLocking.get();
    }
}
