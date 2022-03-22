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
import net.minecraft.network.chat.*;
import org.commonmark.node.*;

import java.util.List;

public class ExperimentalMDVisitor extends AbstractVisitor {

    //keep stack of mc styles
    //on visit add style
    //on visit text, create component and apply styles, append component to root component
    // after visiting children = before exiting visit remove style

    //TODO: Colors, could be:
    //  - modified links?
    //      - [#](hex) starts color change, [#]() ends it
    //TODO: also look at textcomponenttagvisitor


    MutableComponent component;
    Style currentStyle = Style.EMPTY;

    public ExperimentalMDVisitor(){
        this.component = new TranslatableComponent("");
    }

    public MutableComponent getComponent(){
        return this.component;
    }

    @Override
    public void visit(StrongEmphasis strongEmphasis) {
        this.currentStyle = this.currentStyle.withBold(true);
        super.visit(strongEmphasis); //visit children
        this.currentStyle = this.currentStyle.withBold(null);
    }

    @Override
    public void visit(Emphasis Emphasis) {
        this.currentStyle = this.currentStyle.withItalic(true);
        super.visit(Emphasis); //visit children
        this.currentStyle = this.currentStyle.withItalic(null);
    }

    @Override
    public void visit(Text text) {
        this.component.append(new TranslatableComponent(text.getLiteral()).withStyle(this.currentStyle));
        super.visit(text);
    }

    @Override
    public void visit(Link link) {
        if(link.getTitle().equals("#")){
            this.visitColor(link);
        }
        //TODO: handle normal links
        super.visit(link);
    }

    public void visitColor(Link link){
        if(link.getDestination().isEmpty()){
            this.currentStyle = this.currentStyle.withColor((TextColor)null);
        } else {
            //we use TextColor.parseColor because it fails gracefully as a color reset.
            this.currentStyle = this.currentStyle.withColor(TextColor.parseColor("#" + link.getDestination()));
        }
    }

}
