// SPDX-FileCopyrightText: 2024 klikli-dev
//
// SPDX-License-Identifier: MIT

package com.klikli_dev.modonomicon.api.events;

import com.klikli_dev.modonomicon.client.gui.book.entry.EntryDisplayState;
import net.minecraft.resources.ResourceLocation;

public class EntryClickedEvent extends ModonomiconEvent {
    protected ResourceLocation bookId;
    protected ResourceLocation entryId;

    protected double mouseX;
    protected double mouseY;
    protected int button;

    protected EntryDisplayState displayState;

    public EntryClickedEvent(ResourceLocation bookId, ResourceLocation entryId, double mouseX, double mouseY, int button, EntryDisplayState displayState) {
        super(true);

        this.bookId = bookId;
        this.entryId = entryId;
        this.mouseX = mouseX;
        this.mouseY = mouseY;
        this.button = button;
        this.displayState = displayState;
    }

    public ResourceLocation getBookId() {
        return this.bookId;
    }

    public ResourceLocation getEntryId() {
        return this.entryId;
    }

    public double getMouseX() {
        return this.mouseX;
    }

    public double getMouseY() {
        return this.mouseY;
    }

    public int getButton() {
        return this.button;
    }

    public EntryDisplayState getDisplayState() {
        return this.displayState;
    }
}
