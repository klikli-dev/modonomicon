/*
 *
 *  * SPDX-FileCopyrightText: 2022 klikli-dev
 *  *
 *  * SPDX-License-Identifier: MIT
 *
 */

package com.klikli_dev.modonomicon.capability;

import com.klikli_dev.modonomicon.api.ModonimiconConstants.Nbt;
import com.klikli_dev.modonomicon.book.Book;
import com.klikli_dev.modonomicon.book.BookCategory;
import com.klikli_dev.modonomicon.book.BookEntry;
import com.klikli_dev.modonomicon.capability.bookstate.BookState;
import com.klikli_dev.modonomicon.capability.bookstate.CategoryState;
import com.klikli_dev.modonomicon.capability.bookstate.EntryState;
import com.klikli_dev.modonomicon.network.Networking;
import com.klikli_dev.modonomicon.network.messages.SyncBookStateCapabilityMessage;
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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

public class BookStateCapability implements INBTSerializable<CompoundTag> {

    public Map<ResourceLocation, BookState> bookStates = new HashMap<>();


    public static BookState getBookStateFor(Player player, Book book) {
        return player.getCapability(CapabilityRegistry.BOOK_STATE).map(c -> c.getBookState(book)).orElse(null);
    }

    public static CategoryState getCategoryStateFor(Player player, BookCategory category) {
        return player.getCapability(CapabilityRegistry.BOOK_STATE).map(c -> c.getCategoryState(category)).orElse(null);
    }

    public static EntryState getEntryStateFor(Player player, BookEntry entry) {
        return player.getCapability(CapabilityRegistry.BOOK_STATE).map(c -> c.getEntryState(entry)).orElse(null);
    }

    public static void syncFor(ServerPlayer player) {
        player.getCapability(CapabilityRegistry.BOOK_STATE).ifPresent(capability -> {
            capability.sync(player);
        });
    }

    /**
     * Clones the data from an existing instance
     *
     * @param other the existing instance.
     */
    public void clone(BookStateCapability other) {
        this.bookStates = other.bookStates;
    }

    public void sync(ServerPlayer player) {
        Networking.sendTo(player, new SyncBookStateCapabilityMessage(this));
    }

    @Override
    public CompoundTag serializeNBT() {
        var compound = new CompoundTag();

        compound.putString(Nbt.VERSION_TAG, Nbt.CURRENT_VERSION);

        var bookStatesList = new ListTag();
        compound.put("book_states", bookStatesList);
        this.bookStates.forEach((bookId, state) -> {
            var stateCompound = new CompoundTag();
            stateCompound.putString("book_id", bookId.toString());
            stateCompound.put("state", state.serializeNBT());
            bookStatesList.add(stateCompound);
        });

        return compound;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        this.bookStates.clear();

        var bookStatesList = nbt.getList("book_states", Tag.TAG_COMPOUND);
        for (var bookStateEntry : bookStatesList) {
            if (bookStateEntry instanceof CompoundTag bookStateCompound) {
                var bookId = new ResourceLocation(bookStateCompound.getString("book_id"));
                var stateCompound = bookStateCompound.getCompound("state");
                var state = new BookState();
                state.deserializeNBT(stateCompound);
                this.bookStates.put(bookId, state);
            }
        }
    }

    public BookState getBookState(Book book){
        return this.bookStates.getOrDefault(book.getId(), new BookState());
    }

    public CategoryState getCategoryState(BookCategory category){
        return this.getBookState(category.getBook()).categoryStates.getOrDefault(category.getId(), new CategoryState());
    }

    public EntryState getEntryState(BookEntry entry){
        return this.getCategoryState(entry.getCategory()).entryStates.getOrDefault(entry.getId(), new EntryState());
    }

    public static class Dispatcher implements ICapabilitySerializable<CompoundTag> {

        private final LazyOptional<BookStateCapability> bookStateCapability = LazyOptional.of(
                BookStateCapability::new);

        @Nonnull
        @Override
        public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
            if (cap == CapabilityRegistry.BOOK_STATE) {
                return this.bookStateCapability.cast();
            }
            return LazyOptional.empty();
        }

        @Override
        public CompoundTag serializeNBT() {
            return this.bookStateCapability.map(BookStateCapability::serializeNBT).orElse(new CompoundTag());
        }

        @Override
        public void deserializeNBT(CompoundTag nbt) {
            this.bookStateCapability.ifPresent(capability -> capability.deserializeNBT(nbt));
        }
    }
}
