/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.api.datagen.book.page;

import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class BookPageModel {

    protected ResourceLocation type;
    protected String anchor;

    protected BookPageModel(ResourceLocation type, @NotNull String anchor) {
        this.type = type;
        this.anchor = anchor;
    }

    public ResourceLocation getType() {
        return this.type;
    }

    public String getAnchor() {
        return this.anchor;
    }

    public JsonObject toJson() {
        JsonObject json = new JsonObject();
        json.addProperty("type", this.type.toString());
        json.addProperty("anchor", this.anchor);
        return json;
    }
}
