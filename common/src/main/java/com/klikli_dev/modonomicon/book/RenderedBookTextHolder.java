/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.book;

import com.mojang.datafixers.util.Either;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

import java.util.List;

public class RenderedBookTextHolder extends BookTextHolder {

    private final BookTextHolder original;
    private final List<MutableComponent> renderedText;

    public RenderedBookTextHolder(BookTextHolder original, List<MutableComponent> renderedText) {
        if (original == null) {
            throw new IllegalArgumentException("original cannot be null");
        }
        if (renderedText == null) {
            throw new IllegalArgumentException("renderedText cannot be null");
        }
        this.original = original;
        this.renderedText = renderedText;
    }

    public List<MutableComponent> getRenderedText() {
        return this.renderedText;
    }

    @Override
    public String getString() {
        return this.original.getString();
    }

    @Override
    public Component getComponent() {
        return this.original.getComponent();
    }

    @Override
    public boolean isEmpty() {
        return this.getRenderedText().isEmpty();
    }

    @Override
    public boolean hasComponent() {
        return this.original.hasComponent();
    }

    @Override
    public void toNetwork(RegistryFriendlyByteBuf buffer) {
        this.original.toNetwork(buffer);
    }

    @Override
    protected Either<String, Component> toSerializableValue() {
        return this.original.toSerializableValue();
    }
}
