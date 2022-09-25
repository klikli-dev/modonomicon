/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.api.datagen.book.page;

import com.google.gson.JsonObject;
import com.klikli_dev.modonomicon.api.ModonimiconConstants.Data.Page;
import com.klikli_dev.modonomicon.api.datagen.book.BookTextHolderModel;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class BookEntityPageModel extends BookPageModel {
    protected BookTextHolderModel entityName = new BookTextHolderModel("");
    protected BookTextHolderModel text = new BookTextHolderModel("");
    protected ResourceLocation entityId;
    protected float scale = 1.0f;
    protected float offset = 0f;
    protected boolean rotate = true;
    protected float defaultRotation = -45f;

    protected BookEntityPageModel(@NotNull String anchor) {
        super(Page.ENTITY, anchor);
    }

    public static Builder builder() {
        return new Builder();
    }

    public BookTextHolderModel getEntityName() {
        return this.entityName;
    }


    public BookTextHolderModel getText() {
        return this.text;
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

    @Override
    public JsonObject toJson() {
        var json = super.toJson();
        json.add("name", this.entityName.toJson());
        json.add("text", this.text.toJson());
        json.addProperty("entity_id", this.entityId.toString());
        json.addProperty("scale", this.scale);
        json.addProperty("offset", this.offset);
        json.addProperty("rotate", this.rotate);
        json.addProperty("default_rotation", this.defaultRotation);

        return json;
    }


    public static final class Builder {
        protected String anchor = "";
        protected BookTextHolderModel entityName = new BookTextHolderModel("");
        protected BookTextHolderModel text = new BookTextHolderModel("");
        protected ResourceLocation entityId;
        protected float scale = 1.0f;
        protected float offset = 0f;
        protected boolean rotate = true;
        protected float defaultRotation = -45f;

        private Builder() {
        }

        public static Builder aBookTextPageModel() {
            return new Builder();
        }


        public Builder withAnchor(String anchor) {
            this.anchor = anchor;
            return this;
        }

        public Builder withEntityName(String name) {
            this.entityName = new BookTextHolderModel(name);
            return this;
        }

        public Builder withEntityName(Component name) {
            this.entityName = new BookTextHolderModel(name);
            return this;
        }

        public Builder withEntityId(ResourceLocation entityId) {
            this.entityId = entityId;
            return this;
        }

        public Builder withEntityId(String entityId) {
            this.entityId = new ResourceLocation(entityId);
            return this;
        }

        public Builder withText(String text) {
            this.text = new BookTextHolderModel(text);
            return this;
        }

        public Builder withText(Component text) {
            this.text = new BookTextHolderModel(text);
            return this;
        }

        public Builder withScale(float scale) {
            this.scale = scale;
            return this;
        }

        public Builder withOffset(float offset) {
            this.offset = offset;
            return this;
        }

        public Builder withRotate(boolean rotate) {
            this.rotate = rotate;
            return this;
        }

        public Builder withDefaultRotation(float defaultRotation) {
            this.defaultRotation = defaultRotation;
            return this;
        }


        public BookEntityPageModel build() {
            BookEntityPageModel model = new BookEntityPageModel(this.anchor);
            model.text = this.text;
            model.entityName = this.entityName;
            model.entityId = this.entityId;
            model.scale = this.scale;
            model.offset = this.offset;
            model.rotate = this.rotate;
            model.defaultRotation = this.defaultRotation;
            return model;
        }
    }
}
