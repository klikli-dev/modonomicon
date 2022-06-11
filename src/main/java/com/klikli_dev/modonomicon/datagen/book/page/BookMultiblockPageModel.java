/*
 * LGPL-3.0
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

package com.klikli_dev.modonomicon.datagen.book.page;

import com.google.gson.JsonObject;
import com.klikli_dev.modonomicon.api.ModonimiconConstants.Data.Page;
import com.klikli_dev.modonomicon.datagen.book.BookTextHolderModel;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class BookMultiblockPageModel extends BookPageModel {
    protected BookTextHolderModel multiblockName = new BookTextHolderModel("");
    protected BookTextHolderModel text = new BookTextHolderModel("");
    protected String multiblockId;
    protected boolean showVisualizeButton = true;

    protected BookMultiblockPageModel(@NotNull String anchor) {
        super(Page.TEXT, anchor);
    }

    public static Builder builder() {
        return new Builder();
    }

    public BookTextHolderModel getMultiblockName() {
        return this.multiblockName;
    }

    public String getMultiblockId() {
        return this.multiblockId;
    }

    public BookTextHolderModel getText() {
        return this.text;
    }

    public boolean showVisualizeButton() {
        return this.showVisualizeButton;
    }

    @Override
    public JsonObject toJson() {
        var json = super.toJson();
        json.add("multiblock_name", this.multiblockName.toJson());
        json.addProperty("multiblock_id", this.multiblockId);
        json.add("text", this.text.toJson());
        json.addProperty("show_visualize_button", this.showVisualizeButton);
        return json;
    }


    public static final class Builder {
        protected String anchor = "";
        protected BookTextHolderModel multiblockName = new BookTextHolderModel("");
        protected String multiblockId = "";
        protected BookTextHolderModel text = new BookTextHolderModel("");
        protected boolean showVisualizeButton = true;

        private Builder() {
        }

        public static Builder aBookTextPageModel() {
            return new Builder();
        }


        public Builder withAnchor(String anchor) {
            this.anchor = anchor;
            return this;
        }

        public Builder withMultiblockName(String title) {
            this.multiblockName = new BookTextHolderModel(title);
            return this;
        }

        public Builder withMultiblockName(Component title) {
            this.multiblockName = new BookTextHolderModel(title);
            return this;
        }

        public Builder withMultiblockId(String multiblockId) {
            this.multiblockId = multiblockId;
            return this;
        }

        public Builder withMultiblockId(ResourceLocation multiblockId) {
            this.multiblockId = multiblockId.toString();
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

        public Builder withVisualizeButton(boolean showVisualizeButton) {
            this.showVisualizeButton = showVisualizeButton;
            return this;
        }

        public BookMultiblockPageModel build() {
            BookMultiblockPageModel bookTextPageModel = new BookMultiblockPageModel(this.anchor);
            bookTextPageModel.multiblockId = this.multiblockId;
            bookTextPageModel.multiblockName = this.multiblockName;
            bookTextPageModel.text = this.text;
            bookTextPageModel.showVisualizeButton = this.showVisualizeButton;
            return bookTextPageModel;
        }
    }
}
