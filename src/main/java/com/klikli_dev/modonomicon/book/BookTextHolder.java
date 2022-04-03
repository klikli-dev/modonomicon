/*
 * LGPL-3-0
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

package com.klikli_dev.modonomicon.book;

import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import org.jetbrains.annotations.NotNull;

public class BookTextHolder {

    public static final BookTextHolder EMPTY = new BookTextHolder("");

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

    public static BookTextHolder fromNetwork(FriendlyByteBuf buffer) {
        if (buffer.readBoolean()) {
            return new BookTextHolder(buffer.readComponent());
        } else {
            return new BookTextHolder(buffer.readUtf());
        }
    }

    public String getString() {
        return this.hasComponent() ? this.component.getString() : I18n.get(this.string);
    }

    /**
     * Gets the translation key, or null if none
     */
    public String getKey() {
        if (this.hasComponent() && this.component instanceof TranslatableComponent translatableComponent) {
            return translatableComponent.getKey();
        }
        return this.string;
    }

    public Component getComponent() {
        return this.component;
    }

    public boolean hasComponent() {
        return this.component != null;
    }

    public void toNetwork(FriendlyByteBuf buffer) {
        buffer.writeBoolean(this.hasComponent());
        if (this.hasComponent()) {
            buffer.writeComponent(this.component);
        } else {
            buffer.writeUtf(this.string);
        }
    }
}
