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

    boolean shouldShowResearchToasts();

    boolean pauseGameWhenOpen();

    boolean debugOverlay();

    boolean showRecipeLookupHints();

    /**
     * How long (in milliseconds) the open key must be held while hovering an
     * associated item before the linked book entry/page opens.
     */
    int associatedItemsHoldDurationMs();
}
