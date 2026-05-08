/*
 * SPDX-FileCopyrightText: 2026 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.api.datagen.book;

import com.google.gson.JsonObject;
import com.klikli_dev.modonomicon.api.ModonomiconConstants;
import com.klikli_dev.modonomicon.client.gui.book.theme.BookThemeData;
import com.klikli_dev.modonomicon.client.gui.book.theme.NodeConnectionRendererType;
import net.minecraft.resources.Identifier;

import java.util.function.Consumer;

public class BookThemeModel {

    protected Identifier id = ModonomiconConstants.Data.Theme.DEFAULT_THEME_ID;
    protected Identifier type = ModonomiconConstants.Data.Theme.DEFAULT_THEME_TYPE;
    protected BookThemeLayoutModel layout = new BookThemeLayoutModel();
    protected BookThemePaletteModel palette = new BookThemePaletteModel();
    protected BookNodeSettingsModel node = new BookNodeSettingsModel();
    protected boolean generateJson;

    public BookThemeData toData() {
        return new BookThemeData(this.id, this.type, this.layout.toData(), this.palette.toData(), this.node.toData());
    }

    public JsonObject toJson() {
        return this.toData().toJson();
    }

    public boolean shouldGenerateJson() {
        return this.generateJson;
    }

    /**
     * Sets the generated theme id.
     */
    public BookThemeModel withId(Identifier id) {
        this.id = id;
        this.generateJson = true;
        return this;
    }

    /**
     * Sets the registered theme type used to construct the runtime theme.
     */
    public BookThemeModel withType(Identifier type) {
        this.type = type;
        this.generateJson = true;
        return this;
    }

    /**
     * Configures theme layout offsets and scaling.
     */
    public BookThemeModel withLayout(Consumer<BookThemeLayoutModel> consumer) {
        consumer.accept(this.layout);
        this.generateJson = true;
        return this;
    }

    /**
     * Configures theme palette colors.
     */
    public BookThemeModel withPalette(Consumer<BookThemePaletteModel> consumer) {
        consumer.accept(this.palette);
        this.generateJson = true;
        return this;
    }

    /**
     * Sets which node connection renderer should be used by this theme.
     */
    public BookThemeModel withConnectionRenderer(NodeConnectionRendererType value) {
        this.node.connectionRenderer = value;
        this.generateJson = true;
        return this;
    }

    /**
     * Sets the line width used by direct node connections.
     */
    public BookThemeModel withDirectConnectionWidth(float value) {
        this.node.directConnectionWidth = value;
        this.generateJson = true;
        return this;
    }

    /**
     * Sets the opacity multiplier applied to direct node connections.
     */
    public BookThemeModel withDirectConnectionOpacity(float value) {
        this.node.directConnectionOpacity = value;
        this.generateJson = true;
        return this;
    }

    /**
     * Sets the brightness multiplier applied to direct node connections.
     */
    public BookThemeModel withDirectConnectionBrightness(float value) {
        this.node.directConnectionBrightness = value;
        this.generateJson = true;
        return this;
    }

    /**
     * Enables or disables oscillation for direct node connections in unsettled states.
     */
    public BookThemeModel withDirectConnectionOscillation(boolean value) {
        this.node.directConnectionOscillation = value;
        this.generateJson = true;
        return this;
    }

    /**
     * Sets the oscillation amplitude used by direct node connections.
     */
    public BookThemeModel withDirectConnectionOscillationAmplitude(float value) {
        this.node.directConnectionOscillationAmplitude = value;
        this.generateJson = true;
        return this;
    }

    /**
     * Sets the oscillation speed used by direct node connections.
     */
    public BookThemeModel withDirectConnectionOscillationSpeed(float value) {
        this.node.directConnectionOscillationSpeed = value;
        this.generateJson = true;
        return this;
    }

    /**
     * Sets the base color used by settled direct node connections.
     */
    public BookThemeModel withDirectConnectionConnectedColor(int value) {
        this.node.directConnectionConnectedColor = value;
        this.generateJson = true;
        return this;
    }

    /**
     * Sets the base color used when a parent is available but the child is not yet settled.
     */
    public BookThemeModel withDirectConnectionAvailableColor(int value) {
        this.node.directConnectionAvailableColor = value;
        this.generateJson = true;
        return this;
    }

    /**
     * Sets the base color used when both ends are visible but not yet settled.
     */
    public BookThemeModel withDirectConnectionDiscoveredColor(int value) {
        this.node.directConnectionDiscoveredColor = value;
        this.generateJson = true;
        return this;
    }
}
