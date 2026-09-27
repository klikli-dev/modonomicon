/*
 * SPDX-FileCopyrightText: 2026 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.api.datagen.book;

import com.google.gson.JsonObject;
import com.klikli_dev.modonomicon.client.gui.book.theme.BookNodeSettings;
import com.klikli_dev.modonomicon.client.gui.book.theme.GuiSprite;
import com.klikli_dev.modonomicon.client.gui.book.theme.NodeConnectionRendererType;

import java.util.function.Consumer;

public class BookNodeSettingsModel {

    protected NodeConnectionRendererType connectionRenderer = BookNodeSettings.DEFAULT.connectionRenderer();
    protected BookDirectConnectionThemeModel directConnections = new BookDirectConnectionThemeModel();
    protected GuiSprite entryNameBackground = BookNodeSettings.DEFAULT.entryNameBackground();
    protected int entryNameBackgroundTint = BookNodeSettings.DEFAULT.entryNameBackgroundTint();
    protected int entryNameTextColor = BookNodeSettings.DEFAULT.entryNameTextColor();

    public JsonObject toJson() {
        JsonObject json = new JsonObject();
        json.addProperty("connection_renderer", this.connectionRenderer.serializedName());
        if (this.connectionRenderer == NodeConnectionRendererType.DIRECT) {
            json.add("direct_connections", this.directConnections.toJson());
        }
        json.add("entry_name_background", this.entryNameBackground.toJson());
        json.addProperty("entry_name_background_tint", this.entryNameBackgroundTint);
        json.addProperty("entry_name_text_color", this.entryNameTextColor);
        return json;
    }

    /**
     * Sets which node connection renderer should be used by this theme.
     */
    public BookNodeSettingsModel withConnectionRenderer(NodeConnectionRendererType value) {
        this.connectionRenderer = value;
        return this;
    }

    /**
     * Configures the direct line renderer used for node-screen connections.
     */
    public BookNodeSettingsModel withDirectConnections(Consumer<BookDirectConnectionThemeModel> consumer) {
        consumer.accept(this.directConnections);
        return this;
    }

    /**
     * Sets the default background sprite for permanently rendered entry names (see {@code render_name}).
     * Can be overridden per entry via {@code name_background}.
     */
    public BookNodeSettingsModel withEntryNameBackground(GuiSprite value) {
        this.entryNameBackground = value;
        return this;
    }

    /**
     * Sets the default ARGB tint for the entry name background.
     * Multiplied with the per-entry {@code name_background_color}.
     */
    public BookNodeSettingsModel withEntryNameBackgroundTint(int value) {
        this.entryNameBackgroundTint = value;
        return this;
    }

    /**
     * Sets the default ARGB color of the entry name text.
     * Multiplied with the per-entry {@code name_text_color}.
     */
    public BookNodeSettingsModel withEntryNameTextColor(int value) {
        this.entryNameTextColor = value;
        return this;
    }
}
