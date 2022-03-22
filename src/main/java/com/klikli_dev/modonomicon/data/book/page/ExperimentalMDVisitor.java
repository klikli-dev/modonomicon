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

package com.klikli_dev.modonomicon.data.book.page;

import com.klikli_dev.modonomicon.Modonomicon;
import net.minecraft.network.chat.TranslatableComponent;
import org.commonmark.node.AbstractVisitor;
import org.commonmark.node.StrongEmphasis;
import org.commonmark.node.Text;

public class ExperimentalMDVisitor extends AbstractVisitor {

    TranslatableComponent component;

    public ExperimentalMDVisitor(){
        this.component = new TranslatableComponent("");
    }

    @Override
    public void visit(StrongEmphasis strongEmphasis) {
        Modonomicon.LOGGER.debug("strongEmphasis");
        super.visit(strongEmphasis);
    }

    @Override
    public void visit(Text text) {
        Modonomicon.LOGGER.debug("Text: " + text.getLiteral());
        super.visit(text);
    }
}
