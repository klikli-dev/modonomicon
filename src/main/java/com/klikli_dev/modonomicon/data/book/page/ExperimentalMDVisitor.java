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
import org.commonmark.internal.renderer.text.ListHolder;
import org.commonmark.internal.renderer.text.OrderedListHolder;
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
    private ListHolder listHolder;

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
        var child = link.getFirstChild();
        if(child instanceof Text t && t.getLiteral().equals("#")){
            this.visitColor(link);
            //do not visit super for color - otherwise link text will be rendered
        } else {
            //TODO: handle normal links
            super.visit(link);
        }
    }

    public void visitColor(Link link){
        if(link.getDestination().isEmpty()){
            this.currentStyle = this.currentStyle.withColor((TextColor)null);
        } else {
            //we use TextColor.parseColor because it fails gracefully as a color reset.
            this.currentStyle = this.currentStyle.withColor(TextColor.parseColor("#" + link.getDestination()));
        }
    }

    @Override
    public void visit(OrderedList orderedList) {

        //TODO implement lists - see CoreTextContentNodeREnderer
        //ListHolder is used to
//        if (listHolder != null) {
//            writeEndOfLine();
//        }
//        listHolder = new OrderedListHolder(listHolder, orderedList);
//        visitChildren(orderedList);
//        writeEndOfLineIfNeeded(orderedList, null);
//        if (listHolder.getParent() != null) {
//            listHolder = listHolder.getParent();
//        } else {
//            listHolder = null;
//        }
    }

    @Override
    public void visit(ListItem listItem) {
        //use list holder to identify our current parent list
    }

    @Override
    public void visit(HardLineBreak hardLineBreak) {
        //note: space-space-newline hard line breaks will usually not happen
        // due to java stripping trailing white spaces from text blocks
        // and data gen will use text blocks 99% of the time
        // one way to still get them is to use \s as the last space at the end of the line in the text block
        this.component.append(new TextComponent("\n"));
        super.visit(hardLineBreak);
    }

    @Override
    public void visit(SoftLineBreak softLineBreak) {
        //TODO: make using/ignoring soft line breaks a book-level option
        this.component.append(new TextComponent("\n"));
        super.visit(softLineBreak);
    }
}
