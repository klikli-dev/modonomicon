/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.client.gui.book.markdown;

import com.klikli_dev.modonomicon.client.gui.book.markdown.internal.renderer.ListHolder;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.contents.TranslatableContents;
import org.jetbrains.annotations.Nullable;

public class ListItemContents extends TranslatableContents {

    private final ListHolder listHolder;

    /**
     * The number/bullet that precedes the list item text. It is kept out of the wrapped text so that it does not
     * consume the first line's text width, and is prepended to the first line when wrapping instead.
     */
    @Nullable
    private Component prefix;

    public ListItemContents(ListHolder listHolder, String pKey) {
        this(listHolder, pKey, TranslatableContents.NO_ARGS);
    }

    public ListItemContents(ListHolder listHolder, String pKey, Object... pArgs) {
        super(pKey, null, pArgs);
        this.listHolder = listHolder;
    }

    public ListHolder getListHolder() {
        return this.listHolder;
    }

    @Nullable
    public Component getPrefix() {
        return this.prefix;
    }

    public void setPrefix(Component prefix) {
        this.prefix = prefix;
    }
}
