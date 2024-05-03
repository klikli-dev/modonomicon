/*
 * SPDX-FileCopyrightText: 2015, Atlassian Pty Ltd
 *
 * SPDX-License-Identifier: BSD-2-Clause
 */

package com.klikli_dev.modonomicon.client.gui.book.markdown.internal.renderer;

import org.commonmark.node.BulletList;

public class BulletListHolder extends ListHolder {
    private final String marker;

    public BulletListHolder(ListHolder parent, BulletList list) {
        super(parent);
        marker = list.getMarker();
    }

    public String getMarker() {
        return marker;
    }
}
