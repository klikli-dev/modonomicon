/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.datagen.book;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

public class BookTextHolderModel {
    protected Component component;
    protected String string;

    public BookTextHolderModel(Component component) {
        this.component = component;
    }

    public BookTextHolderModel(@NotNull String string) {
        this.string = string;
    }

    public boolean hasComponent() {
        return this.component != null;
    }

    public JsonElement toJson() {
        if (this.hasComponent()) {
            return Component.Serializer.toJsonTree(this.component);
        }
        return new JsonPrimitive(this.string);
    }
}
