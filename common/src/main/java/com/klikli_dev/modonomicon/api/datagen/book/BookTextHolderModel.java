/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.api.datagen.book;

import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.mojang.serialization.JsonOps;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
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

    public JsonElement toJson(HolderLookup.Provider provider) {
        if (this.hasComponent()) {
            //From Component.Serializer.serialize as it is private
            return ComponentSerialization.CODEC.encodeStart(provider.createSerializationContext(JsonOps.INSTANCE), this.component).getOrThrow(JsonParseException::new);
        }
        return new JsonPrimitive(this.string);
    }

    public Component getComponent() {
        return this.component;
    }

    public String getString() {
        return this.string;
    }
}
