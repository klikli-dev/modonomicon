/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.client.render.page;

public interface PageWithTextRenderer {
    /**
     * Gets the x-coordinate where text starts on this page. Use this to handle shifting down text below other content
     * such as title.
     */
    int getTextY();
}
