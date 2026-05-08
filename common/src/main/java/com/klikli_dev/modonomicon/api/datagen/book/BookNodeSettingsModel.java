/*
 * SPDX-FileCopyrightText: 2026 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.api.datagen.book;

import com.klikli_dev.modonomicon.client.gui.book.theme.BookNodeSettings;
import com.klikli_dev.modonomicon.client.gui.book.theme.NodeConnectionRendererType;

import java.util.function.Consumer;

public class BookNodeSettingsModel {
    protected NodeConnectionRendererType connectionRenderer = BookNodeSettings.DEFAULT.connectionRenderer();
    protected BookDirectConnectionThemeModel directConnections = new BookDirectConnectionThemeModel();

    public BookNodeSettings toData() {
        return new BookNodeSettings(this.connectionRenderer, this.directConnections.toData());
    }

    public BookNodeSettingsModel withConnectionRenderer(NodeConnectionRendererType value) {
        this.connectionRenderer = value;
        return this;
    }

    public BookNodeSettingsModel withDirectConnections(Consumer<BookDirectConnectionThemeModel> consumer) {
        consumer.accept(this.directConnections);
        return this;
    }
}
