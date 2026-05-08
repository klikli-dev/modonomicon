/*
 * SPDX-FileCopyrightText: 2026 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.api.datagen.book;

import com.google.gson.JsonObject;
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

    public JsonObject toJson() {
        JsonObject json = new JsonObject();
        json.addProperty("width", this.width);
        json.addProperty("opacity", this.opacity);
        json.addProperty("brightness", this.brightness);
        json.addProperty("oscillation", this.oscillation);
        json.addProperty("oscillation_amplitude", this.oscillationAmplitude);
        json.addProperty("oscillation_speed", this.oscillationSpeed);
        json.addProperty("connected_color", this.connectedColor);
        json.addProperty("available_color", this.availableColor);
        json.addProperty("discovered_color", this.discoveredColor);
        return json;
    }

    /**
     * Sets the line width used by direct node connections.
     */
    public BookDirectConnectionThemeModel withWidth(float value) {
        this.width = value;
        return this;
    }

    /**
     * Sets the opacity multiplier applied to direct node connections.
     */
    public BookDirectConnectionThemeModel withOpacity(float value) {
        this.opacity = value;
        return this;
    }

    /**
     * Sets the brightness multiplier applied to direct node connections.
     */
    public BookDirectConnectionThemeModel withBrightness(float value) {
        this.brightness = value;
        return this;
    }

    /**
     * Enables or disables oscillation for direct node connections in unsettled states.
     */
    public BookDirectConnectionThemeModel withOscillation(boolean value) {
        this.oscillation = value;
        return this;
    }

    /**
     * Sets the oscillation amplitude used by direct node connections.
     */
    public BookDirectConnectionThemeModel withOscillationAmplitude(float value) {
        this.oscillationAmplitude = value;
        return this;
    }

    /**
     * Sets the oscillation speed used by direct node connections.
     */
    public BookDirectConnectionThemeModel withOscillationSpeed(float value) {
        this.oscillationSpeed = value;
        return this;
    }

    /**
     * Sets the base color used by settled direct node connections.
     */
    public BookDirectConnectionThemeModel withConnectedColor(int value) {
        this.connectedColor = value;
        return this;
    }

    /**
     * Sets the base color used when a parent is available but the child is not yet settled.
     */
    public BookDirectConnectionThemeModel withAvailableColor(int value) {
        this.availableColor = value;
        return this;
    }

    /**
     * Sets the base color used when both ends are visible but not yet settled.
     */
    public BookDirectConnectionThemeModel withDiscoveredColor(int value) {
        this.discoveredColor = value;
        return this;
    }
}
