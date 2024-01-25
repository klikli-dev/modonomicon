/*
 * SPDX-FileCopyrightText: 2023 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.platform.services;

import java.util.List;

public interface ClientConfigHelper {
    boolean enableSmoothZoom();

    boolean storeLastOpenPageWhenClosingEntry();

    List<String> fontFallbackLocales();
}
