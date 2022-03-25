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
import net.minecraft.network.chat.TranslatableComponent;
import org.commonmark.Extension;
import org.commonmark.internal.renderer.NodeRendererMap;
import org.commonmark.node.Node;
import org.commonmark.renderer.text.TextContentRenderer;

import java.util.ArrayList;
import java.util.List;

public class ComponentRenderer {

    private final List<ComponentNodeRendererFactory> nodeRendererFactories;
    private final List<TranslatableComponent> components;
    private final boolean renderSoftLineBreaks;
    private final boolean replaceSoftLineBreaksWithSpace;
    private final TextColor linkColor;
    private TranslatableComponent currentComponent;
    private Style currentStyle;

    private ComponentRenderer(ComponentRenderer.Builder builder) {
        this.renderSoftLineBreaks = builder.renderSoftLineBreaks;
        this.replaceSoftLineBreaksWithSpace = builder.replaceSoftLineBreaksWithSpace;
        this.linkColor = builder.linkColor;
        this.currentStyle = builder.style;

        this.components = new ArrayList<>();
        this.currentComponent = new TranslatableComponent("");

        this.nodeRendererFactories = new ArrayList<>(builder.nodeRendererFactories.size() + 1);
        this.nodeRendererFactories.addAll(builder.nodeRendererFactories);
        // Add as last. This means clients can override the rendering of core nodes if they want.
        this.nodeRendererFactories.add(CoreComponentNodeRenderer::new);
    }

    /**
     * Create a new builder for configuring an {@link ComponentRenderer}.
     *
     * @return a builder
     */
    public static ComponentRenderer.Builder builder() {
        return new ComponentRenderer.Builder();
    }

    public List<TranslatableComponent> render(Node node) {
        ComponentRenderer.RendererContext context = new ComponentRenderer.RendererContext();
        context.render(node);
        return context.getComponents();
    }

    /**
     * Extension for {@link ComponentRenderer}.
     */
    public interface ComponentRendererExtension extends Extension {
        void extend(ComponentRenderer.Builder rendererBuilder);
    }

    /**
     * Builder for configuring an {@link TextContentRenderer}. See methods for default configuration.
     */
    public static class Builder {

        private boolean renderSoftLineBreaks = false;
        private boolean replaceSoftLineBreaksWithSpace = true;
        private TextColor linkColor = TextColor.fromRgb(0x5555FF);
        private Style style = Style.EMPTY;
        private final List<ComponentNodeRendererFactory> nodeRendererFactories = new ArrayList<>();

        /**
         * @return the configured {@link TextContentRenderer}
         */
        public ComponentRenderer build() {
            return new ComponentRenderer(this);
        }

        /**
         * True to render soft line breaks (deviating from MD spec). Should usually be false.
         */
        public ComponentRenderer.Builder renderSoftLineBreaks(boolean renderSoftLineBreaks) {
            this.renderSoftLineBreaks = renderSoftLineBreaks;
            return this;
        }

        /**
         * True to replace soft line breaks with spaces. Should usually be true, prevents IDE line breaks from causing
         * words to be rendered without spaces inbetween.
         */
        public ComponentRenderer.Builder replaceSoftLineBreaksWithSpace(boolean replaceSoftLineBreaksWithSpace) {
            this.replaceSoftLineBreaksWithSpace = replaceSoftLineBreaksWithSpace;
            return this;
        }

        /**
         * The color to use for http and book page links. Default: Blue: 0x5555FF
         */
        public ComponentRenderer.Builder linkColor(TextColor linkColor) {
            this.linkColor = linkColor;
            return this;
        }

        /**
         * The style to start rendering with. Will be modified by md instructions.
         */
        public ComponentRenderer.Builder style(Style style) {
            this.style = style;
            return this;
        }

        /**
         * Add a factory for instantiating a node renderer (done when rendering). This allows to override the rendering
         * of node types or define rendering for custom node types.
         * <p>
         * If multiple node renderers for the same node type are created, the one from the factory that was added first
         * "wins". (This is how the rendering for core node types can be overridden; the default rendering comes last.)
         *
         * @param nodeRendererFactory the factory for creating a node renderer
         * @return {@code this}
         */
        public ComponentRenderer.Builder nodeRendererFactory(ComponentNodeRendererFactory nodeRendererFactory) {
            this.nodeRendererFactories.add(nodeRendererFactory);
            return this;
        }

        /**
         * @param extensions extensions to use on this text content renderer
         * @return {@code this}
         */
        public ComponentRenderer.Builder extensions(Iterable<? extends Extension> extensions) {
            for (Extension extension : extensions) {
                if (extension instanceof ComponentRenderer.ComponentRendererExtension componentRendererExtension) {
                    componentRendererExtension.extend(this);
                }
            }
            return this;
        }
    }

    private class RendererContext implements ComponentNodeRendererContext {

        private final NodeRendererMap nodeRendererMap = new NodeRendererMap();

        private RendererContext() {


            // The first node renderer for a node type "wins".
            for (int i = ComponentRenderer.this.nodeRendererFactories.size() - 1; i >= 0; i--) {
                var nodeRendererFactory = ComponentRenderer.this.nodeRendererFactories.get(i);
                var nodeRenderer = nodeRendererFactory.create(this);
                this.nodeRendererMap.add(nodeRenderer);
            }
        }

        @Override
        public TranslatableComponent getCurrentComponent() {
            return ComponentRenderer.this.currentComponent;
        }

        @Override
        public void setCurrentComponent(TranslatableComponent component) {
            ComponentRenderer.this.currentComponent = component;
        }

        @Override
        public List<TranslatableComponent> getComponents() {
            return ComponentRenderer.this.components;
        }

        @Override
        public Style getCurrentStyle() {
            return ComponentRenderer.this.currentStyle;
        }

        @Override
        public void setCurrentStyle(Style style) {
            ComponentRenderer.this.currentStyle = style;
        }

        @Override
        public void render(Node node) {
            this.nodeRendererMap.render(node);
        }

        @Override
        public boolean getRenderSoftLineBreaks() {
            return ComponentRenderer.this.renderSoftLineBreaks;
        }

        @Override
        public boolean getReplaceSoftLineBreaksWithSpace() {
            return ComponentRenderer.this.replaceSoftLineBreaksWithSpace;
        }

        @Override
        public TextColor getLinkColor() {
            return ComponentRenderer.this.linkColor;
        }
    }
}
