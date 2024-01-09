// SPDX-FileCopyrightText: 2024 klikli-dev
//
// SPDX-License-Identifier: MIT

package com.klikli_dev.modonomicon.api.events;

public abstract class ModonomiconEvent {
    private final boolean isCancelable;
    private boolean isCanceled = false;

    public ModonomiconEvent() {
        this(true);
    }

    public ModonomiconEvent(boolean isCancelable) {
        this.isCancelable = isCancelable;
    }

    public boolean isCancelable() {
        return this.isCancelable;
    }

    public boolean isCanceled() {
        return this.isCanceled;
    }

    public void setCanceled(boolean cancel) {
        if (!this.isCancelable()) {
            throw new UnsupportedOperationException(
                    "Attempted to call ModonomiconEvent#setCanceled() on a non-cancelable event of type: "
                            + this.getClass().getCanonicalName()
            );
        }
        this.isCanceled = cancel;
    }

}
