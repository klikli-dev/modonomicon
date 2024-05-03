/*
 * SPDX-FileCopyrightText: 2022 Authors of Patchouli
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.book.page;

import com.google.gson.JsonObject;
import com.klikli_dev.modonomicon.api.ModonomiconConstants.Data.Page;
import com.klikli_dev.modonomicon.api.multiblock.Multiblock;
import com.klikli_dev.modonomicon.book.entries.ContentBookEntry;
import com.klikli_dev.modonomicon.book.BookTextHolder;
import com.klikli_dev.modonomicon.book.RenderedBookTextHolder;
import com.klikli_dev.modonomicon.book.conditions.BookCondition;
import com.klikli_dev.modonomicon.book.conditions.BookNoneCondition;
import com.klikli_dev.modonomicon.client.gui.book.markdown.BookTextRenderer;
import com.klikli_dev.modonomicon.data.MultiblockDataManager;
import com.klikli_dev.modonomicon.util.BookGsonHelper;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.Level;

public class BookMultiblockPage extends BookPage {

    protected BookTextHolder multiblockName;
    protected BookTextHolder text;
    protected boolean showVisualizeButton;
    protected ResourceLocation multiblockId;

    protected Multiblock multiblock;

    public BookMultiblockPage(BookTextHolder multiblockName, BookTextHolder text, ResourceLocation multiblockId, boolean showVisualizeButton, String anchor, BookCondition condition) {
        super(anchor, condition);
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
        var condition = json.has("condition")
                ? BookCondition.fromJson(json.getAsJsonObject("condition"))
                : new BookNoneCondition();
        return new BookMultiblockPage(multiblockName, text, multiblockId, showVisualizeButton, anchor, condition);
    }

    public static BookMultiblockPage fromNetwork(FriendlyByteBuf buffer) {
        var multiblockName = BookTextHolder.fromNetwork(buffer);
        var multiblockId = buffer.readResourceLocation();
        var text = BookTextHolder.fromNetwork(buffer);
        var showVisualizeButton = buffer.readBoolean();
        var anchor = buffer.readUtf();
        var condition = BookCondition.fromNetwork(buffer);
        return new BookMultiblockPage(multiblockName, text, multiblockId, showVisualizeButton, anchor, condition);
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
    public void build(Level level, ContentBookEntry parentEntry, int pageNum) {
        super.build(level, parentEntry, pageNum);

        this.multiblock = MultiblockDataManager.get().getMultiblock(this.multiblockId);

        if (this.multiblock == null) {
            throw new IllegalArgumentException("Invalid multiblock id " + this.multiblockId);
        }
    }

    @Override
    public void prerenderMarkdown(BookTextRenderer textRenderer) {
        super.prerenderMarkdown(textRenderer);

        if (!this.multiblockName.hasComponent()) {
            this.multiblockName = new BookTextHolder(Component.translatable(this.multiblockName.getKey())
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
        super.toNetwork(buffer);
    }

    @Override
    public boolean matchesQuery(String query) {
        return this.multiblockName.getString().toLowerCase().contains(query)
                || this.text.getString().toLowerCase().contains(query);
    }
}
