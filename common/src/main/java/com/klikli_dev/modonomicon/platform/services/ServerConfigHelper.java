/*
 * SPDX-FileCopyrightText: 2024 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.platform.services;

public interface ServerConfigHelper {
    /**
     * If true, advancement-based unlock conditions will always return true,
     * effectively disabling advancement-gated progression in books.
     */
    boolean disableAdvancementLocking();
}
