/*
 * SPDX-FileCopyrightText: 2026 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.api.datagen.book;

import com.klikli_dev.modonomicon.client.gui.book.theme.BookDirectConnectionTheme;
import com.klikli_dev.modonomicon.client.gui.book.theme.BookNodeSettings;
import com.klikli_dev.modonomicon.client.gui.book.theme.NodeConnectionRendererType;

class BookNodeSettingsModel {
    protected NodeConnectionRendererType connectionRenderer = BookNodeSettings.DEFAULT.connectionRenderer();
    protected float directConnectionWidth = BookDirectConnectionTheme.DEFAULT.width();
    protected float directConnectionOpacity = BookDirectConnectionTheme.DEFAULT.opacity();
    protected float directConnectionBrightness = BookDirectConnectionTheme.DEFAULT.brightness();
    protected boolean directConnectionOscillation = BookDirectConnectionTheme.DEFAULT.oscillation();
    protected float directConnectionOscillationAmplitude = BookDirectConnectionTheme.DEFAULT.oscillationAmplitude();
    protected float directConnectionOscillationSpeed = BookDirectConnectionTheme.DEFAULT.oscillationSpeed();
    protected int directConnectionConnectedColor = BookDirectConnectionTheme.DEFAULT.connectedColor();
    protected int directConnectionAvailableColor = BookDirectConnectionTheme.DEFAULT.availableColor();
    protected int directConnectionDiscoveredColor = BookDirectConnectionTheme.DEFAULT.discoveredColor();

    public BookNodeSettings toData() {
        return new BookNodeSettings(
                this.connectionRenderer,
                new BookDirectConnectionTheme(
                        this.directConnectionWidth,
                        this.directConnectionOpacity,
                        this.directConnectionBrightness,
                        this.directConnectionOscillation,
                        this.directConnectionOscillationAmplitude,
                        this.directConnectionOscillationSpeed,
                        this.directConnectionConnectedColor,
                        this.directConnectionAvailableColor,
                        this.directConnectionDiscoveredColor
                )
        );
    }
}
