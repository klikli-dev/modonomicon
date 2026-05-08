/*
 * SPDX-FileCopyrightText: 2026 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.api.datagen.book;

import com.klikli_dev.modonomicon.client.gui.book.theme.BookDirectConnectionTheme;

public class BookDirectConnectionThemeModel {
    protected float width = BookDirectConnectionTheme.DEFAULT.width();
    protected float opacity = BookDirectConnectionTheme.DEFAULT.opacity();
    protected float brightness = BookDirectConnectionTheme.DEFAULT.brightness();
    protected boolean oscillation = BookDirectConnectionTheme.DEFAULT.oscillation();
    protected float oscillationAmplitude = BookDirectConnectionTheme.DEFAULT.oscillationAmplitude();
    protected float oscillationSpeed = BookDirectConnectionTheme.DEFAULT.oscillationSpeed();
    protected int connectedColor = BookDirectConnectionTheme.DEFAULT.connectedColor();
    protected int availableColor = BookDirectConnectionTheme.DEFAULT.availableColor();
    protected int discoveredColor = BookDirectConnectionTheme.DEFAULT.discoveredColor();

    public BookDirectConnectionTheme toData() {
        return new BookDirectConnectionTheme(
                this.width,
                this.opacity,
                this.brightness,
                this.oscillation,
                this.oscillationAmplitude,
                this.oscillationSpeed,
                this.connectedColor,
                this.availableColor,
                this.discoveredColor
        );
    }

    public BookDirectConnectionThemeModel withWidth(float value) {
        this.width = value;
        return this;
    }

    public BookDirectConnectionThemeModel withOpacity(float value) {
        this.opacity = value;
        return this;
    }

    public BookDirectConnectionThemeModel withBrightness(float value) {
        this.brightness = value;
        return this;
    }

    public BookDirectConnectionThemeModel withOscillation(boolean value) {
        this.oscillation = value;
        return this;
    }

    public BookDirectConnectionThemeModel withOscillationAmplitude(float value) {
        this.oscillationAmplitude = value;
        return this;
    }

    public BookDirectConnectionThemeModel withOscillationSpeed(float value) {
        this.oscillationSpeed = value;
        return this;
    }

    public BookDirectConnectionThemeModel withConnectedColor(int value) {
        this.connectedColor = value;
        return this;
    }

    public BookDirectConnectionThemeModel withAvailableColor(int value) {
        this.availableColor = value;
        return this;
    }

    public BookDirectConnectionThemeModel withDiscoveredColor(int value) {
        this.discoveredColor = value;
        return this;
    }
}
