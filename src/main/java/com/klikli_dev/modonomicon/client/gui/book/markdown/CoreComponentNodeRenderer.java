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
import net.minecraft.network.chat.ClickEvent.Action;
import org.commonmark.internal.renderer.text.BulletListHolder;
import org.commonmark.internal.renderer.text.ListHolder;
import org.commonmark.internal.renderer.text.OrderedListHolder;
import org.commonmark.node.*;
import org.commonmark.renderer.NodeRenderer;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class CoreComponentNodeRenderer extends AbstractVisitor implements NodeRenderer {

    private final ComponentNodeRendererContext context;

    /**
     * List holder is used to keep track of the current markdown (ordered or unordered) list we are rendering.
     */
    private ListHolder listHolder;


    public CoreComponentNodeRenderer(ComponentNodeRendererContext context) {
        this.context = context;
    }

    public void visitColor(Link link) {
        if (link.getDestination().isEmpty()) {
            this.context.setCurrentStyle(this.context.getCurrentStyle().withColor((TextColor) null));
        } else {
            //we use TextColor.parseColor because it fails gracefully as a color reset.
            this.context.setCurrentStyle(this.context.getCurrentStyle()
                    .withColor(TextColor.parseColor("#" + link.getDestination())));
        }
    }

    public void visitHttpLink(Link link) {

        var currentColor = this.context.getCurrentStyle().getColor();

        //if we have a color we use it, otherwise we use link default.
        this.context.setCurrentStyle(this.context.getCurrentStyle()
                .withColor(currentColor == null ? this.context.getLinkColor() : currentColor)
                .withClickEvent(new ClickEvent(Action.OPEN_URL, link.getDestination()))
        );

        this.visitChildren(link);

        //at the end of the link we reset to our previous color.
        this.context.setCurrentStyle(this.context.getCurrentStyle()
                .withColor(currentColor)
                .withClickEvent(null)
        );
    }

    public void visitBookLink(Link link) {
        var currentColor = this.context.getCurrentStyle().getColor();

        //if we have a color we use it, otherwise we use link default.
        this.context.setCurrentStyle(this.context.getCurrentStyle()
                .withColor(currentColor == null ? this.context.getLinkColor() : currentColor)
                .withClickEvent(new ClickEvent(Action.CHANGE_PAGE, link.getDestination()))
        );

        this.visitChildren(link);

        //links are not style instructions, so we reset to our previous color.
        this.context.setCurrentStyle(this.context.getCurrentStyle()
                .withColor(currentColor)
                .withClickEvent(null)
        );
    }

    /**
     * Archives our current component on the list of components
     */
    private void finalizeCurrentComponent() {
        this.context.getComponents().add(this.context.getCurrentComponent());
        this.context.setCurrentComponent(this.listHolder == null ?
                new TranslatableComponent("") : new ListItemComponent(this.listHolder, ""));
    }

    /**
     * Checks if the current component is empty and has no siblings.
     */
    private boolean isEmptyComponent() {
        //translation contents have no content, they have a key (which doubles as content).
        return this.context.getCurrentComponent().getKey().isEmpty() && this.context.getCurrentComponent().getSiblings().isEmpty();
    }

    @Override
    public Set<Class<? extends Node>> getNodeTypes() {
        return new HashSet<>(Arrays.asList(
                Heading.class,
                BulletList.class,
                Link.class,
                ListItem.class,
                OrderedList.class,
                Emphasis.class,
                StrongEmphasis.class,
                Text.class,
                SoftLineBreak.class,
                HardLineBreak.class
        ));
    }

    public void render(Node node) {
        node.accept(this);
        if (!this.isEmptyComponent()) {
            this.finalizeCurrentComponent();
        }
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

        this.finalizeCurrentComponent();
    }

    @Override
    public void visit(Emphasis emphasis) {
        var italic = this.context.getCurrentStyle().isItalic();
        this.context.setCurrentStyle(this.context.getCurrentStyle().withItalic(true));
        this.visitChildren(emphasis);
        this.context.setCurrentStyle(this.context.getCurrentStyle().withItalic(italic));
    }

    @Override
    public void visit(HardLineBreak hardLineBreak) {
        //note: space-space-newline hard line breaks will usually not happen
        // due to java stripping trailing white spaces from text blocks
        // and data gen will use text blocks 99% of the time
        // one way to still get them is to use \s as the last space at the end of the line in the text block
        this.context.getCurrentComponent().append(new TextComponent("\n"));

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
            //do not visit children for color - otherwise link text will be rendered
        } else {
            //normal links
            if (link.getDestination().startsWith("http://") || link.getDestination().startsWith("https://")) {
                this.visitHttpLink(link);
            }
            //book links
            if (link.getDestination().startsWith("book://")) {
                this.visitBookLink(link);
            }
        }

        //TODO: other special links such as items
    }

    @Override
    public void visit(ListItem listItem) {
        //while hard newlines don't force a new component in a list, a new list item does.
        this.finalizeCurrentComponent();

        if (this.listHolder != null && this.listHolder instanceof OrderedListHolder orderedListHolder) {
            //List bullets/numbers will not be affected by current style
            this.context.getCurrentComponent().append(new TranslatableComponent(
                    orderedListHolder.getIndent() + orderedListHolder.getCounter() + orderedListHolder.getDelimiter() + " ")
                    .withStyle(Style.EMPTY));

            this.visitChildren(listItem);
            orderedListHolder.increaseCounter();
        } else if (this.listHolder != null && this.listHolder instanceof BulletListHolder bulletListHolder) {
            //List bullets/numbers will not be affected by current style
            this.context.getCurrentComponent().append(new TranslatableComponent(
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

        this.finalizeCurrentComponent();
    }

    @Override
    public void visit(SoftLineBreak softLineBreak) {
        if (this.context.getRenderSoftLineBreaks()) {
            this.context.getCurrentComponent().append(new TextComponent("\n"));
        } else if (this.context.getReplaceSoftLineBreaksWithSpace()) {
            this.context.getCurrentComponent().append(new TextComponent(" "));
        }
        this.visitChildren(softLineBreak);
    }

    @Override
    public void visit(StrongEmphasis strongEmphasis) {
        var emphasis = this.context.getCurrentStyle().isBold();
        this.context.setCurrentStyle(this.context.getCurrentStyle().withBold(true));
        this.visitChildren(strongEmphasis);
        this.context.setCurrentStyle(this.context.getCurrentStyle().withBold(emphasis));

    }

    @Override
    public void visit(Text text) {
        this.context.getCurrentComponent().append(
                new TranslatableComponent(text.getLiteral()).withStyle(this.context.getCurrentStyle()));
        this.visitChildren(text);
    }
}
