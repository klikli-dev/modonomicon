/*
 * SPDX-FileCopyrightText: 2026 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.client.gui.book.theme;

public interface BookFrameTheme {

    GuiNineSlice frame();

    GuiFrameOverlay topOverlay();

    GuiFrameOverlay bottomOverlay();

    GuiFrameOverlay leftOverlay();

    GuiFrameOverlay rightOverlay();
}
