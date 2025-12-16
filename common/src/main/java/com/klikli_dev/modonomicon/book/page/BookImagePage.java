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
import net.minecraft.resources.Identifier;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.Level;

public class BookImagePage extends BookPage {
    protected BookTextHolder title;
    protected BookTextHolder text;
    protected Identifier[] images;
    protected boolean border;
    protected boolean useLegacyRendering;

    public BookImagePage(BookTextHolder title, BookTextHolder text, Identifier[] images, boolean border, boolean useLegacyRendering, String anchor, BookCondition condition) {
        super(anchor, condition);
        this.title = title;
        this.text = text;
        this.images = images;
        this.border = border;
        this.useLegacyRendering = useLegacyRendering;
    }

    public static BookImagePage fromJson(Identifier entryId, JsonObject json, HolderLookup.Provider provider) {
        var title = BookGsonHelper.getAsBookTextHolder(json, "title", BookTextHolder.EMPTY, provider);
        var text = BookGsonHelper.getAsBookTextHolder(json, "text", BookTextHolder.EMPTY, provider);

        var imagesArray = GsonHelper.getAsJsonArray(json, "images");
        var images = new Identifier[imagesArray.size()];
        for (int i = 0; i < imagesArray.size(); i++) {
            images[i] = Identifier.parse(GsonHelper.convertToString(imagesArray.get(i), "images[" + i + "]"));
        }

        var border = GsonHelper.getAsBoolean(json, "border", true);
        var useLegacyRendering = GsonHelper.getAsBoolean(json, "use_legacy_rendering", false);

        var anchor = GsonHelper.getAsString(json, "anchor", "");
        var condition = json.has("condition")
                ? BookCondition.fromJson(entryId, json.getAsJsonObject("condition"), provider)
                : new BookNoneCondition();
        return new BookImagePage(title, text, images, border, useLegacyRendering, anchor, condition);
    }

    public static BookImagePage fromNetwork(RegistryFriendlyByteBuf buffer) {
        var title = BookTextHolder.fromNetwork(buffer);
        var text = BookTextHolder.fromNetwork(buffer);

        var count = buffer.readVarInt();
        var images = new Identifier[count];
        for (int i = 0; i < count; i++) {
            images[i] = Identifier.parse(buffer.readUtf());
        }

        var border = buffer.readBoolean();
        var useLegacyRendering = buffer.readBoolean();

        var anchor = buffer.readUtf();
        var condition = BookCondition.fromNetwork(buffer);
        return new BookImagePage(title, text, images, border, useLegacyRendering, anchor, condition);
    }

    public Identifier[] getImages() {
        return this.images;
    }

    public boolean hasBorder() {
        return this.border;
    }

    public boolean useLegacyRendering() {
        return this.useLegacyRendering;
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
    public Identifier getType() {
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
        buffer.writeBoolean(this.useLegacyRendering);
        super.toNetwork(buffer);
    }

    @Override
    public boolean matchesQuery(String query, Level level) {
        return this.title.getString().toLowerCase().contains(query)
                || this.text.getString().toLowerCase().contains(query);
    }
}
