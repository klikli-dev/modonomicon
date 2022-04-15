/*
 * LGPL-3.0
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

package com.klikli_dev.modonomicon.client.gui.book.markdown;

import org.commonmark.node.Link;
import org.commonmark.node.Node;

import java.util.function.Consumer;

public interface LinkRenderer {
    /**
     * Renders a link node - used for custom functionality
     *
     * @param Link          the link node
     * @param visitChildren callback to visit children (if link text should be rendered)
     * @param context       the renderer context
     * @return true if handled, false if next link renderer (or default if none) should be called.
     */
    boolean visit(Link Link, Consumer<Node> visitChildren, ComponentNodeRendererContext context);
}
