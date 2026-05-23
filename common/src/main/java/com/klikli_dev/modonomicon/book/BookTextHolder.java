/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.book;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.chat.contents.TranslatableContents;
import org.jetbrains.annotations.NotNull;

public class BookTextHolder {

    public static final BookTextHolder EMPTY = new BookTextHolder("");
    public static final Codec<BookTextHolder> CODEC = Codec.either(Codec.STRING, ComponentSerialization.CODEC)
            .xmap(value -> value.map(BookTextHolder::new, BookTextHolder::new), holder -> holder.hasComponent() ? Either.right(holder.component) : Either.left(holder.string));
    public static final StreamCodec<RegistryFriendlyByteBuf, BookTextHolder> STREAM_CODEC = ByteBufCodecs.fromCodecWithRegistries(CODEC);

    private Component component;
    private String string;

    protected BookTextHolder() {
    }

    public BookTextHolder(Component component) {
        this.component = component;
    }

    public BookTextHolder(@NotNull String string) {
        this.string = string;
    }

    public static BookTextHolder fromNetwork(RegistryFriendlyByteBuf buffer) {
        return STREAM_CODEC.decode(buffer);
    }

    public String getString() {
        return this.hasComponent() ? this.component.getString() : I18n.get(this.string);
    }

    /**
     * Gets the translation key, or null if none
     */
    public String getKey() {
        if (this.hasComponent() && this.component.getContents() instanceof TranslatableContents contents) {
            return contents.getKey();
        }
        return this.string;
    }

    public Component getComponent() {
        return this.component;
    }

    public boolean hasComponent() {
        return this.component != null;
    }

    public boolean isEmpty() {
        //Note: BookTextHolder needs to override this, because string will always be null for it
        return (this.hasComponent() ? this.component.getString() : this.string).isEmpty();
    }

    public void toNetwork(RegistryFriendlyByteBuf buffer) {
        STREAM_CODEC.encode(buffer, this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        var that = (BookTextHolder) o;

        if (this.hasComponent() && that.hasComponent()) {
            return this.component.equals(that.component);
        }

        if (this.string != null)
            return this.string.equals(that.string);

        return false;
    }

    @Override
    public int hashCode() {
        return this.hasComponent() ? this.component.hashCode() :  this.string == null ? 0 : this.string.hashCode();
    }

    public record ScaleCacheKey(BookTextHolder holder, int width, int height){

    }
}
