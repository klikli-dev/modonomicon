/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.client.gui.book.markdown;

import net.minecraft.network.chat.TranslatableComponent;
import org.commonmark.internal.renderer.text.ListHolder;

public class ListItemComponent  extends TranslatableComponent {

    private final ListHolder listHolder;

    public ListItemComponent(ListHolder listHolder, String pKey) {
        super(pKey);
        this.listHolder = listHolder;
    }

    public ListItemComponent(ListHolder listHolder, String pKey, Object... pArgs) {
        super(pKey, pArgs);
        this.listHolder = listHolder;
    }

    public ListHolder getListHolder() {
        return this.listHolder;
    }
}
