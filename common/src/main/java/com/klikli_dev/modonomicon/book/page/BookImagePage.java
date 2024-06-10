/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.book.page;

import com.google.gson.JsonObject;
import com.klikli_dev.modonomicon.api.ModonomiconConstants.Data.Page;
import com.klikli_dev.modonomicon.book.BookTextHolder;
import com.klikli_dev.modonomicon.book.RenderedBookTextHolder;
import com.klikli_dev.modonomicon.book.conditions.BookCondition;
import com.klikli_dev.modonomicon.book.conditions.BookNoneCondition;
import com.klikli_dev.modonomicon.client.gui.book.markdown.BookTextRenderer;
import com.klikli_dev.modonomicon.util.BookGsonHelper;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;

public class BookImagePage extends BookPage {
    protected BookTextHolder title;
    protected BookTextHolder text;
    protected ResourceLocation[] images;
    protected boolean border;

    public BookImagePage(BookTextHolder title, BookTextHolder text, ResourceLocation[] images, boolean border, String anchor, BookCondition condition) {
        super(anchor, condition);
        this.title = title;
        this.text = text;
        this.images = images;
        this.border = border;
    }

    public static BookImagePage fromJson(JsonObject json, HolderLookup.Provider provider) {
        var title = BookGsonHelper.getAsBookTextHolder(json, "title", BookTextHolder.EMPTY, provider);
        var text = BookGsonHelper.getAsBookTextHolder(json, "text", BookTextHolder.EMPTY, provider);

        var imagesArray = GsonHelper.getAsJsonArray(json, "images");
        var images = new ResourceLocation[imagesArray.size()];
        for (int i = 0; i < imagesArray.size(); i++) {
            images[i] = ResourceLocation.parse(GsonHelper.convertToString(imagesArray.get(i), "images[" + i + "]"));
        }

        var border = GsonHelper.getAsBoolean(json, "border", true);

        var anchor = GsonHelper.getAsString(json, "anchor", "");
        var condition = json.has("condition")
                ? BookCondition.fromJson(json.getAsJsonObject("condition"), provider)
                : new BookNoneCondition();
        return new BookImagePage(title, text, images, border, anchor, condition);
    }

    public static BookImagePage fromNetwork(RegistryFriendlyByteBuf buffer){
        var title = BookTextHolder.fromNetwork(buffer);
        var text = BookTextHolder.fromNetwork(buffer);

        var count = buffer.readVarInt();
        var images = new ResourceLocation[count];
        for (int i = 0; i < count; i++) {
            images[i] = ResourceLocation.parse(buffer.readUtf());
        }

        var border = buffer.readBoolean();

        var anchor = buffer.readUtf();
        var condition = BookCondition.fromNetwork(buffer);
        return new BookImagePage(title, text, images, border, anchor, condition);
    }

    public ResourceLocation[] getImages() {
        return this.images;
    }

    public boolean hasBorder() {
        return this.border;
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
        return Page.IMAGE;
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
    public void toNetwork(RegistryFriendlyByteBuf buffer) {
        this.title.toNetwork(buffer);
        this.text.toNetwork(buffer);

        buffer.writeVarInt(this.images.length);
        for (var image : this.images) {
            buffer.writeUtf(image.toString());
        }

        buffer.writeBoolean(this.border);
        super.toNetwork(buffer);
    }

    @Override
    public boolean matchesQuery(String query) {
        return this.title.getString().toLowerCase().contains(query)
                || this.text.getString().toLowerCase().contains(query);
    }
}
