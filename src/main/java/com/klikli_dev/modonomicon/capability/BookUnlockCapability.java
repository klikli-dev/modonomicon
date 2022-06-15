/*
 *
 *  * SPDX-FileCopyrightText: 2022 klikli-dev
 *  *
 *  * SPDX-License-Identifier: MIT
 *
 */

package com.klikli_dev.modonomicon.capability;

import com.klikli_dev.modonomicon.book.BookCategory;
import com.klikli_dev.modonomicon.book.BookEntry;
import com.klikli_dev.modonomicon.book.conditions.context.BookConditionContext;
import com.klikli_dev.modonomicon.data.BookDataManager;
import com.klikli_dev.modonomicon.network.Networking;
import com.klikli_dev.modonomicon.network.messages.SyncBookUnlockCapabilityMessage;
import com.klikli_dev.modonomicon.registry.CapabilityRegistry;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.entity.player.AdvancementEvent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class BookUnlockCapability implements INBTSerializable<CompoundTag> {

    /**
     * Map Book ID to unlocked pages IDs
     */
    public Map<ResourceLocation, Set<ResourceLocation>> unlockedEntries = new HashMap<>();

    /**
     * Map Book ID to unlocked categories IDs
     */
    public Map<ResourceLocation, Set<ResourceLocation>> unlockedCategories = new HashMap<>();

    public static boolean isUnlockedFor(Player player, BookCategory category) {
        return player.getCapability(CapabilityRegistry.BOOK_UNLOCK).map(c -> c.isUnlocked(category)).orElse(false);
    }

    public static boolean isUnlockedFor(Player player, BookEntry entry) {
        return player.getCapability(CapabilityRegistry.BOOK_UNLOCK).map(c -> c.isUnlocked(entry)).orElse(false);
    }

    public static void onAdvancement(final AdvancementEvent event) {
        if (event.getPlayer() instanceof ServerPlayer serverplayer) {
            serverplayer.getCapability(CapabilityRegistry.BOOK_UNLOCK).ifPresent(capability -> {
                capability.update(serverplayer);
                capability.sync(serverplayer);
            });
        }
    }

    /**
     * Clones the data from an existing instance
     *
     * @param other the existing instance.
     */
    public void clone(BookUnlockCapability other) {
        this.unlockedEntries = other.unlockedEntries;
        this.unlockedCategories = other.unlockedCategories;
    }

    /**
     * Always call sync afterwards!
     */
    public void update(ServerPlayer owner) {
        //loop through available books and update unlocked pages and categories
        for (var book : BookDataManager.get().getBooks().values()) {
            for (var category : book.getCategories().values()) {

                if (category.getCondition().test(BookConditionContext.of(book, category), owner))
                    this.unlockedCategories.computeIfAbsent(book.getId(), k -> new HashSet<>()).add(category.getId());

                for (var entry : category.getEntries().values()) {
                    if (entry.getCondition().test(BookConditionContext.of(book, entry), owner))
                        this.unlockedEntries.computeIfAbsent(book.getId(), k -> new HashSet<>()).add(entry.getId());
                }
            }
        }
    }

    public void sync(ServerPlayer player) {
        Networking.sendTo(player, new SyncBookUnlockCapabilityMessage(this));
    }

    public boolean isUnlocked(BookEntry entry) {
        return this.unlockedEntries.getOrDefault(entry.getBook().getId(), new HashSet<>()).contains(entry.getId());
    }

    public boolean isUnlocked(BookCategory category) {
        return this.unlockedCategories.getOrDefault(category.getBook().getId(), new HashSet<>()).contains(category.getId());
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
        compound.put("unlocked_entries", unlockedPagesByBook);
        this.unlockedEntries.forEach((bookId, pageId) -> {
            var bookCompound = new CompoundTag();
            var unlockedPagesList = new ListTag();
            bookCompound.putString("book_id", bookId.toString());
            bookCompound.put("unlocked_entries", unlockedPagesList);

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

        this.unlockedEntries.clear();
        var unlockedEntriesByBook = nbt.getList("unlocked_entries", Tag.TAG_COMPOUND);
        for (var bookEntry : unlockedEntriesByBook) {
            if (bookEntry instanceof CompoundTag bookCompound) {
                var bookId = ResourceLocation.tryParse(bookCompound.getString("book_id"));
                var unlockedPagesList = bookCompound.getList("unlocked_entries", Tag.TAG_COMPOUND);
                var pages = new HashSet<ResourceLocation>();
                for (var pageEntry : unlockedPagesList) {
                    if (pageEntry instanceof CompoundTag pageCompound) {
                        var pageId = ResourceLocation.tryParse(pageCompound.getString("page_id"));
                        pages.add(pageId);
                    }
                }
                this.unlockedEntries.put(bookId, pages);
            }
        }
    }

    public static class Dispatcher implements ICapabilitySerializable<CompoundTag> {

        private final LazyOptional<BookUnlockCapability> bookDataCapability = LazyOptional.of(
                BookUnlockCapability::new);

        @Nonnull
        @Override
        public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
            if (cap == CapabilityRegistry.BOOK_UNLOCK) {
                return this.bookDataCapability.cast();
            }
            return LazyOptional.empty();
        }

        @Override
        public CompoundTag serializeNBT() {
            return this.bookDataCapability.map(BookUnlockCapability::serializeNBT).orElse(new CompoundTag());
        }

        @Override
        public void deserializeNBT(CompoundTag nbt) {
            this.bookDataCapability.ifPresent(capability -> capability.deserializeNBT(nbt));
        }
    }
}
