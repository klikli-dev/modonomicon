/*
 * LGPL-3-0
 *
 * Copyright (C) 2022 klikli-dev
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

package com.klikli_dev.modonomicon.client.gui.book.markdown.ext;

import com.klikli_dev.modonomicon.client.gui.book.markdown.ComponentNodeRendererContext;
import org.commonmark.ext.gfm.strikethrough.Strikethrough;
import org.commonmark.ext.ins.Ins;
import org.commonmark.node.Node;
import org.commonmark.renderer.NodeRenderer;

import java.util.Collections;
import java.util.Set;

public class UnderlineComponentNodeRenderer implements NodeRenderer {

    private final ComponentNodeRendererContext context;

    public UnderlineComponentNodeRenderer(ComponentNodeRendererContext context) {
        this.context = context;
    }

    /**
     * Copied from StrikethroughHtmlNodeRenderer
     */
    protected void visitChildren(Node parent) {
        Node node = parent.getFirstChild();
        while (node != null) {
            Node next = node.getNext();
            this.context.render(node);
            node = next;
        }
    }

    @Override
    public Set<Class<? extends Node>> getNodeTypes() {
        return Collections.singleton(Ins.class);
    }

    @Override
    public void render(Node node) {
        var underline = this.context.getCurrentStyle().isUnderlined();
        this.context.setCurrentStyle(this.context.getCurrentStyle().withUnderlined(true));
        this.visitChildren(node);
        this.context.setCurrentStyle(this.context.getCurrentStyle().withUnderlined(underline));
    }
}
