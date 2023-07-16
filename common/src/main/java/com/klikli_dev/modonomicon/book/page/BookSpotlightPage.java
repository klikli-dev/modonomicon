/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.book.page;

import com.google.gson.JsonObject;
import com.klikli_dev.modonomicon.api.ModonomiconConstants.Data.Page;
import com.klikli_dev.modonomicon.book.BookEntry;
import com.klikli_dev.modonomicon.book.BookTextHolder;
import com.klikli_dev.modonomicon.book.RenderedBookTextHolder;
import com.klikli_dev.modonomicon.client.gui.book.markdown.BookTextRenderer;
import com.klikli_dev.modonomicon.util.BookGsonHelper;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;

import java.util.Arrays;

public class BookSpotlightPage extends BookPage {
    protected BookTextHolder title;
    protected BookTextHolder text;
    protected Ingredient item;

    public BookSpotlightPage(BookTextHolder title, BookTextHolder text, Ingredient item, String anchor) {
        super(anchor);
        this.title = title;
        this.text = text;
        this.item = item;
    }

    public static BookSpotlightPage fromJson(JsonObject json) {
        var title = BookGsonHelper.getAsBookTextHolder(json, "title", BookTextHolder.EMPTY);
        var item = Ingredient.fromJson(GsonHelper.getAsJsonObject(json, "item"));
        var text = BookGsonHelper.getAsBookTextHolder(json, "text", BookTextHolder.EMPTY);
        var anchor = GsonHelper.getAsString(json, "anchor", "");
        return new BookSpotlightPage(title, text, item, anchor);
    }

    public static BookSpotlightPage fromNetwork(FriendlyByteBuf buffer) {
        var title = BookTextHolder.fromNetwork(buffer);
        var item = Ingredient.fromNetwork(buffer);
        var text = BookTextHolder.fromNetwork(buffer);
        var anchor = buffer.readUtf();
        return new BookSpotlightPage(title, text, item, anchor);
    }

    public Ingredient getItem() {
        return this.item;
    }

    public BookTextHolder getTitle() {
        return this.title;
    }

    public BookTextHolder getText() {
        return this.text;
    }

    public boolean hasTitle() {
        return !this.title.isEmpty();
    }

    @Override
    public ResourceLocation getType() {
        return Page.SPOTLIGHT;
    }

    @Override
    public void build(Level level, BookEntry parentEntry, int pageNum) {
        super.build(level, parentEntry, pageNum);

        if (this.title.isEmpty()) {
            //use ingredient name if we don't have a custom title
            this.title = new BookTextHolder(((MutableComponent) this.item.getItems()[0].getHoverName())
                    .withStyle(Style.EMPTY
                            .withBold(true)
                            .withColor(this.getParentEntry().getBook().getDefaultTitleColor())
                    ));
        }
    }

    @Override
    public void prerenderMarkdown(BookTextRenderer textRenderer) {
        super.prerenderMarkdown(textRenderer);

        if (!this.title.hasComponent()) {
            this.title = new BookTextHolder(Component.translatable(this.title.getKey())
                    .withStyle(Style.EMPTY
                            .withBold(true)
                            .withColor(this.getParentEntry().getBook().getDefaultTitleColor())));
        }
        if (!this.text.hasComponent()) {
            this.text = new RenderedBookTextHolder(this.text, textRenderer.render(this.text.getString()));
        }
    }

    @Override
    public void toNetwork(FriendlyByteBuf buffer) {
        this.title.toNetwork(buffer);
        this.item.toNetwork(buffer);
        this.text.toNetwork(buffer);
        buffer.writeUtf(this.anchor);
    }

    @Override
    public boolean matchesQuery(String query) {
        return this.title.getString().toLowerCase().contains(query)
                || Arrays.stream(this.item.getItems()).anyMatch(i -> I18n.get(i.getDescriptionId()).toLowerCase().contains(query))
                || this.text.getString().toLowerCase().contains(query);
    }
}
