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

import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import org.commonmark.internal.renderer.text.BulletListHolder;
import org.commonmark.internal.renderer.text.ListHolder;
import org.commonmark.internal.renderer.text.OrderedListHolder;
import org.commonmark.node.*;

import java.util.ArrayList;
import java.util.List;

public class ComponentMarkdownRenderer extends AbstractVisitor {
    /**
     * Context that contains render settings.
     */
    private final ComponentRendererContext context;
    /**
     * The list of components we already finished rendering. Each hard newline will cause a new component to start,
     * while list items should share a component.
     */
    private final List<TranslatableComponent> components;
    /**
     * The component we are currently rendering to (by appending siblings). In certain well-defined cases it will be
     * replaced with a new component and the old one added to @components
     */
    private TranslatableComponent currentComponent;
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

    public TranslatableComponent getCurrentComponent() {
        return this.currentComponent;
    }

    public List<TranslatableComponent> getComponents() {
        return this.components;
    }

    public List<TranslatableComponent> render(Node node) {
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
        this.currentComponent = this.listHolder == null ? new TranslatableComponent("") : new ListItemComponent(this.listHolder, "");
    }

    /**
     * Checks if the current component is empty and has no siblings.
     */
    private boolean isEmptyComponent() {
        //translation contents have no content, they have a key (which doubles as content).
        return this.currentComponent.getKey().isEmpty() && this.currentComponent.getSiblings().isEmpty();
    }

    @Override
    public void visit(BulletList bulletList) {
        this.listHolder = new BulletListHolder(this.listHolder, bulletList);
        this.visitChildren(bulletList);
        if (this.listHolder.getParent() != null) {
            this.listHolder = this.listHolder.getParent();
        } else {
            this.listHolder = null;
        }
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

        //lists do their own finalization
        if (this.listHolder == null) {
            this.finalizeCurrentComponent();
        }
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
        //while hard newlines don't force a new component in a list, a new list item does.
        this.finalizeCurrentComponent();

        if (this.listHolder != null && this.listHolder instanceof OrderedListHolder orderedListHolder) {
            //List bullets/numbers will not be affected by current style
            this.currentComponent.append(new TranslatableComponent(
                    orderedListHolder.getIndent() + orderedListHolder.getCounter() + orderedListHolder.getDelimiter() + " ")
                    .withStyle(Style.EMPTY));

            this.visitChildren(listItem);
            orderedListHolder.increaseCounter();
        } else if (this.listHolder != null && this.listHolder instanceof BulletListHolder bulletListHolder) {
            //List bullets/numbers will not be affected by current style
            this.currentComponent.append(new TranslatableComponent(
                    bulletListHolder.getIndent() + bulletListHolder.getMarker() + " ")
                    .withStyle(Style.EMPTY));
            this.visitChildren(listItem);
        }
    }

    @Override
    public void visit(OrderedList orderedList) {
        //create a new list holder with our (potential) current holder as parent
        this.listHolder = new OrderedListHolder(this.listHolder, orderedList);
        this.visitChildren(orderedList);

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
        this.visitChildren(text);
    }
}
