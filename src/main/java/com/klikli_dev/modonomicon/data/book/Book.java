/*
 * LGPL-3-0
 *
 * Copyright (C) 2021 klikli-dev
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

package com.klikli_dev.modonomicon.data.book;

import com.google.gson.JsonObject;
import com.klikli_dev.modonomicon.api.ModonimiconConstants.Data;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.crafting.CraftingHelper;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Book {
    protected ResourceLocation id;
    protected String name;
    protected ResourceLocation bookTexture;
    protected Map<ResourceLocation, BookCategory> categories;
    //TODO: further properties for customization, such as book item, ...

    public Book(ResourceLocation id, String name, ResourceLocation bookTexture) {
        this.id = id;
        this.name = name;
        this.bookTexture = bookTexture;
        this.categories = new HashMap<>();
    }

    public ResourceLocation getId() {
        return this.id;
    }

    public Map<ResourceLocation, BookCategory> getCategories() {
        return this.categories;
    }

    public List<BookCategory> getCategoriesSorted(){
        return this.categories.values().stream().sorted(Comparator.comparingInt(BookCategory::getSortNumber)).toList();
    }

    public String getName() {
        return this.name;
    }

    public ResourceLocation getBookTexture() {
        return this.bookTexture;
    }

    public static Book fromJson(ResourceLocation id, JsonObject json) {
        String name = json.get("name").getAsString();
        ResourceLocation bookTexture = new ResourceLocation(GsonHelper.getAsString(json, "book_texture", Data.Book.DEFAULT_BOOK_TEXTURE));
        return new Book(id, name, bookTexture);
    }
}
