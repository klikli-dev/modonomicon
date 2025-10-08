// SPDX-FileCopyrightText: 2024 klikli-dev
//
// SPDX-License-Identifier: MIT

package com.klikli_dev.modonomicon.api.events;

import com.klikli_dev.modonomicon.client.gui.book.entry.EntryDisplayState;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.resources.ResourceLocation;

/**
 * An event that is fired on the client-side when an entry is clicked in the book.
 * If the event is cancelled by a listener, the entry will not be displayed.
 */
public class EntryClickedEvent extends ModonomiconEvent {
    protected ResourceLocation bookId;
    protected ResourceLocation entryId;

    protected MouseButtonEvent mouseButtonEvent;

    protected EntryDisplayState displayState;

    public EntryClickedEvent(ResourceLocation bookId, ResourceLocation entryId, MouseButtonEvent mouseButtonEvent, EntryDisplayState displayState) {
        super(true);

        this.bookId = bookId;
        this.entryId = entryId;
        this.mouseButtonEvent = mouseButtonEvent;
        this.displayState = displayState;
    }

    public ResourceLocation getBookId() {
        return this.bookId;
    }

    public ResourceLocation getEntryId() {
        return this.entryId;
    }

    /**
     * For categories in Index mode this has the X coordinate of the button that was clicked, instead of the mouse cursor that clicked it.
     * For categories in Index mode this has the Y coordinate of the button that was clicked, instead of the mouse cursor that clicked it.
     * For categories in Index mode this always has GLFW_MOUSE_BUTTON_1 (= 0 = left mouse button).
     */
    public MouseButtonEvent getMouseButtonEvent() {
        return this.mouseButtonEvent;
    }

    public EntryDisplayState getDisplayState() {
        return this.displayState;
    }
}
