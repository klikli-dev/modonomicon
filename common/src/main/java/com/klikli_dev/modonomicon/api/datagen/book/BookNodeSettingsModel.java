/*
 * SPDX-FileCopyrightText: 2026 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.api.datagen.book;

import com.google.gson.JsonObject;
import com.klikli_dev.modonomicon.client.gui.book.theme.BookNodeSettings;
import com.klikli_dev.modonomicon.client.gui.book.theme.NodeConnectionRendererType;

import java.util.function.Consumer;

public class BookNodeSettingsModel {

    protected NodeConnectionRendererType connectionRenderer = BookNodeSettings.DEFAULT.connectionRenderer();
    protected BookDirectConnectionThemeModel directConnections = new BookDirectConnectionThemeModel();

    public JsonObject toJson() {
        JsonObject json = new JsonObject();
        json.addProperty("connection_renderer", this.connectionRenderer.serializedName());
        if (this.connectionRenderer == NodeConnectionRendererType.DIRECT) {
            json.add("direct_connections", this.directConnections.toJson());
        }
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
}
