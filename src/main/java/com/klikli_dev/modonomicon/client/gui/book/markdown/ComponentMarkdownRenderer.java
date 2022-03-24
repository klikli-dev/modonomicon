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

package com.klikli_dev.modonomicon.client.gui.book.markdown;

import net.minecraft.network.chat.*;
import org.commonmark.internal.renderer.text.ListHolder;
import org.commonmark.internal.renderer.text.OrderedListHolder;
import org.commonmark.node.*;

import java.util.ArrayList;
import java.util.List;

public class ComponentMarkdownRenderer extends AbstractVisitor {
    //        - visitor should default to soft newline off
//        - visitor has multiple output components in list
//        - keep current component to add siblings, when conditions are met create new comp and add old to list
//                - on hard newline new comp
//                - per list item new comp
//                - when rendering list item comps, wrap with indent as in ComponentRenderUtils
//        - can comps keep Metadata to know which list?
//                - or wrapper that has component + references (could be child of Translate component, or just implementing interface, more work)

    /**
     * Context that contains render settings.
     */
    private final ComponentRendererContext context;
    /**
     * The list of components we already finished rendering. Each hard newline will cause a new component to start,
     * while list items should share a component.
     */
    private final List<MutableComponent> components;
    /**
     * The component we are currently rendering to (by appending siblings). In certain well-defined cases it will be
     * replaced with a new component and the old one added to @components
     */
    private MutableComponent currentComponent;
    /**
     * The style applied to the next sibling. Each markdown styling instruction will replace this with a new immutable
     * style option.
     */
    private Style currentStyle = Style.EMPTY;
    /**
     * List holder is used to keep track of the current markdown (ordered or unordered) list we are rendering.
     */
    private ListHolder listHolder;

    public ComponentMarkdownRenderer(ComponentRendererContext context) {
        this.context = context;
        this.currentComponent = new TranslatableComponent("");
        this.components = new ArrayList<>();
    }

    public MutableComponent getCurrentComponent() {
        return this.currentComponent;
    }

    public List<MutableComponent> getComponents() {
        return this.components;
    }

    public List<MutableComponent> render(Node node) {
        node.accept(this);
        if (!this.isEmptyComponent()) {
            this.finalizeCurrentComponent();
        }
        return this.getComponents();
    }

    public void visitColor(Link link) {
        if (link.getDestination().isEmpty()) {
            this.currentStyle = this.currentStyle.withColor((TextColor) null);
        } else {
            //we use TextColor.parseColor because it fails gracefully as a color reset.
            this.currentStyle = this.currentStyle.withColor(TextColor.parseColor("#" + link.getDestination()));
        }
    }

    /**
     * Archives our current component on the list of components
     */
    private void finalizeCurrentComponent() {
        this.components.add(this.currentComponent);
        this.currentComponent = new TranslatableComponent("");
    }

    /**
     * Checks if the current component is empty and has no siblings.
     */
    private boolean isEmptyComponent() {
        return this.currentComponent.getContents().isEmpty() && this.currentComponent.getSiblings().isEmpty();
    }

    @Override
    public void visit(Emphasis emphasis) {
        this.currentStyle = this.currentStyle.withItalic(true);
        this.visitChildren(emphasis);
        this.currentStyle = this.currentStyle.withItalic(null);
    }

    @Override
    public void visit(HardLineBreak hardLineBreak) {
        //note: space-space-newline hard line breaks will usually not happen
        // due to java stripping trailing white spaces from text blocks
        // and data gen will use text blocks 99% of the time
        // one way to still get them is to use \s as the last space at the end of the line in the text block
        this.currentComponent.append(new TextComponent("\n"));
        this.finalizeCurrentComponent();
        this.visitChildren(hardLineBreak);
    }

    @Override
    public void visit(Link link) {
        var child = link.getFirstChild();
        if (child instanceof Text t && t.getLiteral().equals("#")) {
            this.visitColor(link);
            //do not visit super for color - otherwise link text will be rendered
        } else {
            //TODO: handle normal links
            super.visit(link);
        }
    }

    @Override
    public void visit(ListItem listItem) {
        //use list holder to identify our current parent list
    }

    @Override
    public void visit(OrderedList orderedList) {
        if (this.listHolder != null) {
            //TODO: write newline?
        }
        //create a new list holder with our (potential) current holder as parent
        this.listHolder = new OrderedListHolder(this.listHolder, orderedList);
        this.visitChildren(orderedList);
        //TODO: newline?

        //Now, if we have a parent, set it as current to handle nested lists
        if (this.listHolder.getParent() != null) {
            this.listHolder = this.listHolder.getParent();
        } else {
            this.listHolder = null;
        }
    }

    @Override
    public void visit(SoftLineBreak softLineBreak) {
        if (this.context.renderSoftLineBreaks()) {
            this.currentComponent.append(new TextComponent("\n"));
        }
        this.visitChildren(softLineBreak);
    }

    @Override
    public void visit(StrongEmphasis strongEmphasis) {
        this.currentStyle = this.currentStyle.withBold(true);
        this.visitChildren(strongEmphasis);
        this.currentStyle = this.currentStyle.withBold(null);
    }

    @Override
    public void visit(Text text) {
        this.currentComponent.append(new TranslatableComponent(text.getLiteral()).withStyle(this.currentStyle));
        super.visit(text);
    }
}
