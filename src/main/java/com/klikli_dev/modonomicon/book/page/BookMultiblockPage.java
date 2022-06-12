/*
 * LGPL-3.0
 *
 * Copyright (C) 2022 Authors of Patchouli, klikli-dev
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

package com.klikli_dev.modonomicon.book.page;

import com.google.gson.JsonObject;
import com.klikli_dev.modonomicon.api.ModonimiconConstants.Data.Page;
import com.klikli_dev.modonomicon.api.multiblock.Multiblock;
import com.klikli_dev.modonomicon.book.BookEntry;
import com.klikli_dev.modonomicon.book.BookTextHolder;
import com.klikli_dev.modonomicon.book.RenderedBookTextHolder;
import com.klikli_dev.modonomicon.client.gui.book.markdown.BookTextRenderer;
import com.klikli_dev.modonomicon.data.MultiblockDataManager;
import com.klikli_dev.modonomicon.util.BookGsonHelper;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;

public class BookMultiblockPage extends BookPage {

    protected BookTextHolder multiblockName;
    protected BookTextHolder text;
    protected boolean showVisualizeButton;
    protected ResourceLocation multiblockId;

    protected Multiblock multiblock;

    public BookMultiblockPage(BookTextHolder multiblockName, BookTextHolder text, ResourceLocation multiblockId, boolean showVisualizeButton, String anchor) {
        super(anchor);
        this.multiblockName = multiblockName;
        this.text = text;
        this.multiblockId = multiblockId;
        this.showVisualizeButton = showVisualizeButton;
    }

    public static BookMultiblockPage fromJson(JsonObject json) {
        var multiblockName = BookGsonHelper.getAsBookTextHolder(json, "multiblock_name", BookTextHolder.EMPTY);
        var multiblockId = ResourceLocation.tryParse(GsonHelper.getAsString(json, "multiblock_id"));
        var text = BookGsonHelper.getAsBookTextHolder(json, "text", BookTextHolder.EMPTY);
        var showVisualizeButton = GsonHelper.getAsBoolean(json, "show_visualize_button", true);
        var anchor = GsonHelper.getAsString(json, "anchor", "");
        return new BookMultiblockPage(multiblockName, text, multiblockId, showVisualizeButton, anchor);
    }

    public static BookMultiblockPage fromNetwork(FriendlyByteBuf buffer) {
        var multiblockName = BookTextHolder.fromNetwork(buffer);
        var multiblockId = buffer.readResourceLocation();
        var text = BookTextHolder.fromNetwork(buffer);
        var showVisualizeButton = buffer.readBoolean();
        var anchor = buffer.readUtf();
        return new BookMultiblockPage(multiblockName, text, multiblockId, showVisualizeButton, anchor);
    }

    public boolean showVisualizeButton() {
        return this.showVisualizeButton;
    }

    public Multiblock getMultiblock() {
        return this.multiblock;
    }

    public BookTextHolder getMultiblockName() {
        return this.multiblockName;
    }

    public BookTextHolder getText() {
        return this.text;
    }

    @Override
    public ResourceLocation getType() {
        return Page.MULTIBLOCK;
    }

    @Override
    public void build(BookEntry parentEntry, int pageNum) {
        super.build(parentEntry, pageNum);

        this.multiblock = MultiblockDataManager.get().getMultiblock(this.multiblockId);

        if (this.multiblock == null) {
            throw new IllegalArgumentException("Invalid multiblock id " + this.multiblockId);
        }
    }

    @Override
    public void prerenderMarkdown(BookTextRenderer textRenderer) {
        super.prerenderMarkdown(textRenderer);

        if (!this.multiblockName.hasComponent()) {
            this.multiblockName = new BookTextHolder(new TranslatableComponent(this.multiblockName.getKey())
                    .withStyle(Style.EMPTY
                            .withBold(true)
                            .withColor(this.getParentEntry().getCategory().getBook().getDefaultTitleColor())));
        }
        if (!this.text.hasComponent()) {
            this.text = new RenderedBookTextHolder(this.text, textRenderer.render(this.text.getString()));
        }
    }

    @Override
    public void toNetwork(FriendlyByteBuf buffer) {
        this.multiblockName.toNetwork(buffer);
        buffer.writeResourceLocation(this.multiblockId);
        this.text.toNetwork(buffer);
        buffer.writeBoolean(this.showVisualizeButton);
        buffer.writeUtf(this.anchor);
    }
}
