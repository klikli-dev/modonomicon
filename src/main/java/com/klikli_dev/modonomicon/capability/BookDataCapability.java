/*
 *
 *  * SPDX-FileCopyrightText: 2022 klikli-dev
 *  *
 *  * SPDX-License-Identifier: MIT
 *
 */

package com.klikli_dev.modonomicon.capability;

import com.klikli_dev.modonomicon.registry.CapabilityRegistry;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class BookDataCapability implements INBTSerializable<CompoundTag> {

    /**
     * Map Book ID to unlocked pages IDs
     */
    public Map<ResourceLocation, Set<ResourceLocation>> unlockedPages = new HashMap<>();

    /**
     * Map Book ID to unlocked categories IDs
     */
    public Map<ResourceLocation, Set<ResourceLocation>> unlockedCategories = new HashMap<>();

    /**
     * Clones the data from an existing instance
     *
     * @param other the existing instance.
     */
    public void clone(BookDataCapability other) {
        this.unlockedPages = other.unlockedPages;
        this.unlockedCategories = other.unlockedCategories;
    }

    @Override
    public CompoundTag serializeNBT() {
        var compound = new CompoundTag();
        var unlockedCategoriesByBook = new ListTag();
        compound.put("unlocked_categories", unlockedCategoriesByBook);
        this.unlockedCategories.forEach((bookId, categories) -> {
            var bookCompound = new CompoundTag();
            var unlockedCategoriesList = new ListTag();
            bookCompound.putString("book_id", bookId.toString());
            bookCompound.put("unlocked_categories", unlockedCategoriesList);

            categories.forEach(categoryId -> {
                var categoryCompound = new CompoundTag();
                categoryCompound.putString("category_id", categoryId.toString());
                unlockedCategoriesList.add(categoryCompound);
            });

            unlockedCategoriesByBook.add(bookCompound);
        });

        var unlockedPagesByBook = new ListTag();
        compound.put("unlocked_pages", unlockedPagesByBook);
        this.unlockedPages.forEach((bookId, pageId) -> {
            var bookCompound = new CompoundTag();
            var unlockedPagesList = new ListTag();
            bookCompound.putString("book_id", bookId.toString());
            bookCompound.put("unlocked_pages", unlockedPagesList);

            pageId.forEach(categoryId -> {
                var categoryCompound = new CompoundTag();
                categoryCompound.putString("page_id", categoryId.toString());
                unlockedPagesList.add(categoryCompound);
            });

            unlockedPagesByBook.add(bookCompound);
        });

        return compound;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        this.unlockedCategories.clear();

        var unlockedCategoriesByBook = nbt.getList("unlocked_categories", Tag.TAG_COMPOUND);
        for (var bookEntry : unlockedCategoriesByBook) {
            if (bookEntry instanceof CompoundTag bookCompound) {
                var bookId = ResourceLocation.tryParse(bookCompound.getString("book_id"));
                var unlockedCategoriesList = bookCompound.getList("unlocked_categories", Tag.TAG_COMPOUND);
                var categories = new HashSet<ResourceLocation>();
                for (var categoryEntry : unlockedCategoriesList) {
                    if (categoryEntry instanceof CompoundTag categoryCompound) {
                        var categoryId = ResourceLocation.tryParse(categoryCompound.getString("category_id"));
                        categories.add(categoryId);
                    }
                }
                this.unlockedCategories.put(bookId, categories);
            }
        }

        this.unlockedPages.clear();
        var unlockedPagesByBook = nbt.getList("unlocked_pages", Tag.TAG_COMPOUND);
        for (var bookEntry : unlockedPagesByBook) {
            if (bookEntry instanceof CompoundTag bookCompound) {
                var bookId = ResourceLocation.tryParse(bookCompound.getString("book_id"));
                var unlockedPagesList = bookCompound.getList("unlocked_pages", Tag.TAG_COMPOUND);
                var pages = new HashSet<ResourceLocation>();
                for (var pageEntry : unlockedPagesList) {
                    if (pageEntry instanceof CompoundTag pageCompound) {
                        var pageId = ResourceLocation.tryParse(pageCompound.getString("page_id"));
                        pages.add(pageId);
                    }
                }
                this.unlockedPages.put(bookId, pages);
            }
        }
    }

    public static class Dispatcher implements ICapabilitySerializable<CompoundTag> {

        private final LazyOptional<BookDataCapability> bookDataCapability = LazyOptional.of(
                BookDataCapability::new);

        @Nonnull
        @Override
        public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
            if (cap == CapabilityRegistry.BOOK_DATA) {
                return this.bookDataCapability.cast();
            }
            return LazyOptional.empty();
        }

        @Override
        public CompoundTag serializeNBT() {
            return this.bookDataCapability.map(BookDataCapability::serializeNBT).orElse(new CompoundTag());
        }

        @Override
        public void deserializeNBT(CompoundTag nbt) {
            this.bookDataCapability.ifPresent(capability -> capability.deserializeNBT(nbt));
        }
    }
}
