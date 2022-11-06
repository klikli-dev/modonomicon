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
import com.klikli_dev.modonomicon.util.EntityUtil;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;

public class BookEntityPage extends BookPage {

    protected BookTextHolder entityName;
    protected BookTextHolder text;
    protected ResourceLocation entityId;
    protected float scale = 1.0f;
    protected float offset = 0f;
    protected boolean rotate = true;
    protected float defaultRotation = -45f;


    public BookEntityPage(BookTextHolder entityName, BookTextHolder text, ResourceLocation entityId, float scale, float offset, boolean rotate, float defaultRotation, String anchor) {
        super(anchor);
        this.entityName = entityName;
        this.text = text;
        this.entityId = entityId;
        this.scale = scale;
        this.offset = offset;
        this.rotate = rotate;
        this.defaultRotation = defaultRotation;
    }

    public static BookEntityPage fromJson(JsonObject json) {
        var entityName = BookGsonHelper.getAsBookTextHolder(json, "name", BookTextHolder.EMPTY);
        var text = BookGsonHelper.getAsBookTextHolder(json, "text", BookTextHolder.EMPTY);
        var entityId = ResourceLocation.tryParse(GsonHelper.getAsString(json, "entity_id"));
        var scale = GsonHelper.getAsFloat(json, "scale", 1.0f);
        var offset = GsonHelper.getAsFloat(json, "offset", 0.0f);
        var rotate = GsonHelper.getAsBoolean(json, "rotate", true);
        var defaultRotation = GsonHelper.getAsFloat(json, "default_rotation", -45.0f);

        var anchor = GsonHelper.getAsString(json, "anchor", "");
        return new BookEntityPage(entityName, text, entityId, scale, offset, rotate, defaultRotation, anchor);
    }

    public static BookEntityPage fromNetwork(FriendlyByteBuf buffer) {
        var entityName = BookTextHolder.fromNetwork(buffer);
        var text = BookTextHolder.fromNetwork(buffer);
        var entityId = buffer.readResourceLocation();
        var scale = buffer.readFloat();
        var offset = buffer.readFloat();
        var rotate = buffer.readBoolean();
        var defaultRotation = buffer.readFloat();
        var anchor = buffer.readUtf();
        return new BookEntityPage(entityName, text, entityId, scale, offset, rotate, defaultRotation, anchor);
    }

    public ResourceLocation getEntityId() {
        return this.entityId;
    }

    public float getScale() {
        return this.scale;
    }

    public float getOffset() {
        return this.offset;
    }

    public boolean doesRotate() {
        return this.rotate;
    }

    public float getDefaultRotation() {
        return this.defaultRotation;
    }

    public BookTextHolder getEntityName() {
        return this.entityName;
    }

    public BookTextHolder getText() {
        return this.text;
    }

    @Override
    public ResourceLocation getType() {
        return Page.ENTITY;
    }

    @Override
    public void prerenderMarkdown(BookTextRenderer textRenderer) {
        super.prerenderMarkdown(textRenderer);

        if (!this.entityName.hasComponent()) {
            this.entityName = new BookTextHolder(new TranslatableComponent(this.entityName.getKey())
                    .withStyle(Style.EMPTY
                            .withBold(true)
                            .withColor(this.getParentEntry().getBook().getDefaultTitleColor())));
        }
        if (!this.text.hasComponent()) {
            this.text = new RenderedBookTextHolder(this.text, textRenderer.render(this.text.getString()));
        }
    }

    @Override
    public void build(BookEntry parentEntry, int pageNum) {
        super.build(parentEntry, pageNum);

        if (this.entityName.isEmpty()) {
            //use entity name if we don't have a custom title
            this.entityName = new BookTextHolder(new TranslatableComponent(EntityUtil.getEntityName(this.entityId))
                    .withStyle(Style.EMPTY
                            .withBold(true)
                            .withColor(this.getParentEntry().getBook().getDefaultTitleColor())
                    ));
        }
    }
    @Override
    public void toNetwork(FriendlyByteBuf buffer) {
        this.entityName.toNetwork(buffer);
        this.text.toNetwork(buffer);
        buffer.writeResourceLocation(this.entityId);
        buffer.writeFloat(this.scale);
        buffer.writeFloat(this.offset);
        buffer.writeBoolean(this.rotate);
        buffer.writeFloat(this.defaultRotation);
        buffer.writeUtf(this.anchor);
    }

    @Override
    public boolean matchesQuery(String query) {
        return this.entityName.getString().toLowerCase().contains(query)
                || this.text.getString().toLowerCase().contains(query);
    }
}
