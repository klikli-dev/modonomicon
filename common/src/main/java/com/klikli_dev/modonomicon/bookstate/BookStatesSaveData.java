/*
 * SPDX-FileCopyrightText: 2023 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.bookstate;

import com.klikli_dev.modonomicon.Modonomicon;
import com.klikli_dev.modonomicon.book.Book;
import com.klikli_dev.modonomicon.book.BookCategory;
import com.klikli_dev.modonomicon.book.BookCommand;
import com.klikli_dev.modonomicon.book.entries.BookEntry;
import com.klikli_dev.modonomicon.util.Codecs;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.Object2ObjectMaps;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.resources.Identifier;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class BookStatesSaveData extends SavedData {
    public static final Identifier ID = Modonomicon.loc( "book_states");

    public static final Codec<BookStatesSaveData> CODEC = RecordCodecBuilder.create((instance) -> instance.group(
            Codec.unboundedMap(Codecs.UUID, BookVisualStates.CODEC).fieldOf("visualStates").forGetter((state) -> state.visualStates),
            Codec.unboundedMap(Codecs.UUID, Codec.unboundedMap(Identifier.CODEC, Codecs.set(Identifier.CODEC))).optionalFieldOf("readEntries", Object2ObjectMaps.emptyMap()).forGetter((state) -> state.readEntries),
            Codec.unboundedMap(Codecs.UUID, Codec.unboundedMap(Identifier.CODEC, Codecs.set(Identifier.CODEC))).optionalFieldOf("readCategories", Object2ObjectMaps.emptyMap()).forGetter((state) -> state.readCategories),
            Codec.unboundedMap(Codecs.UUID, Codec.unboundedMap(Identifier.CODEC, Codec.unboundedMap(Identifier.CODEC, Codec.INT))).optionalFieldOf("usedCommands", Object2ObjectMaps.emptyMap()).forGetter((state) -> state.usedCommands),
            Codec.unboundedMap(Codecs.UUID, Codec.unboundedMap(Identifier.CODEC, Codec.unboundedMap(Identifier.CODEC, Codec.LONG))).optionalFieldOf("unlockTimestamps", Object2ObjectMaps.emptyMap()).forGetter((state) -> state.unlockTimestamps)
    ).apply(instance, BookStatesSaveData::new));

    public static final SavedDataType<BookStatesSaveData> TYPE = new SavedDataType<>(
            ID,
            BookStatesSaveData::new,
            CODEC, DataFixTypes.PLAYER);


    public Map<UUID, BookVisualStates> visualStates;
    public Map<UUID, Map<Identifier, Set<Identifier>>> readEntries;
    public Map<UUID, Map<Identifier, Set<Identifier>>> readCategories;
    public Map<UUID, Map<Identifier, Map<Identifier, Integer>>> usedCommands;
    public Map<UUID, Map<Identifier, Map<Identifier, Long>>> unlockTimestamps;

    public BookStatesSaveData() {
        this(Object2ObjectMaps.emptyMap(), Object2ObjectMaps.emptyMap(), Object2ObjectMaps.emptyMap(), Object2ObjectMaps.emptyMap(), Object2ObjectMaps.emptyMap());
    }

    public BookStatesSaveData(Map<UUID, BookVisualStates> visualStates) {
        this(visualStates, Object2ObjectMaps.emptyMap(), Object2ObjectMaps.emptyMap(), Object2ObjectMaps.emptyMap(), Object2ObjectMaps.emptyMap());
    }

    public BookStatesSaveData(Map<UUID, BookVisualStates> visualStates,
                              Map<UUID, Map<Identifier, Set<Identifier>>> readEntries,
                              Map<UUID, Map<Identifier, Set<Identifier>>> readCategories,
                              Map<UUID, Map<Identifier, Map<Identifier, Integer>>> usedCommands,
                              Map<UUID, Map<Identifier, Map<Identifier, Long>>> unlockTimestamps) {
        this.visualStates = Object2ObjectMaps.synchronize(new Object2ObjectOpenHashMap<>(visualStates));
        this.readEntries = synchronizeNestedSets(readEntries);
        this.readCategories = synchronizeNestedSets(readCategories);
        this.usedCommands = synchronizeNestedMaps(usedCommands);
        this.unlockTimestamps = synchronizeNestedMaps(unlockTimestamps);

        this.setDirty();
    }

    public BookVisualStates getVisualStates(UUID playerUUID) {
        return this.visualStates.computeIfAbsent(playerUUID, (uuid) -> {
            this.setDirty();
            return new BookVisualStates();
        });
    }

    public boolean isEntryRead(UUID playerUUID, BookEntry entry) {
        return this.readEntries.getOrDefault(playerUUID, Map.of()).getOrDefault(entry.getBook().getId(), Set.of()).contains(entry.getId());
    }

    public boolean markEntryRead(UUID playerUUID, BookEntry entry) {
        var byBook = this.readEntries.computeIfAbsent(playerUUID, uuid -> Object2ObjectMaps.synchronize(new Object2ObjectOpenHashMap<>()));
        var entries = byBook.computeIfAbsent(entry.getBook().getId(), id -> new ObjectOpenHashSet<>());
        if (!entries.add(entry.getId())) {
            return false;
        }
        this.setDirty();
        return true;
    }

    public boolean isCategoryRead(UUID playerUUID, BookCategory category) {
        return this.readCategories.getOrDefault(playerUUID, Map.of()).getOrDefault(category.getBook().getId(), Set.of()).contains(category.getId());
    }

    public boolean markCategoryRead(UUID playerUUID, BookCategory category) {
        var byBook = this.readCategories.computeIfAbsent(playerUUID, uuid -> Object2ObjectMaps.synchronize(new Object2ObjectOpenHashMap<>()));
        var categories = byBook.computeIfAbsent(category.getBook().getId(), id -> new ObjectOpenHashSet<>());
        if (!categories.add(category.getId())) {
            return false;
        }
        this.setDirty();
        return true;
    }

    public boolean canRunCommand(UUID playerUUID, BookCommand command) {
        if (command.getBook() == null) {
            return false;
        }
        if (command.getMaxUses() == -1) {
            return true;
        }
        return this.usedCommands.getOrDefault(playerUUID, Map.of())
                .getOrDefault(command.getBook().getId(), Map.of())
                .getOrDefault(command.getId(), 0) < command.getMaxUses();
    }

    public void incrementCommandUses(UUID playerUUID, BookCommand command) {
        if (command.getBook() == null) {
            return;
        }
        var byBook = this.usedCommands.computeIfAbsent(playerUUID, uuid -> Object2ObjectMaps.synchronize(new Object2ObjectOpenHashMap<>()));
        var commands = byBook.computeIfAbsent(command.getBook().getId(), id -> Object2ObjectMaps.synchronize(new Object2ObjectOpenHashMap<>()));
        commands.put(command.getId(), commands.getOrDefault(command.getId(), 0) + 1);
        this.setDirty();
    }

    public Map<Identifier, Long> getUnlockTimestamps(UUID playerUUID, Book book) {
        return this.unlockTimestamps.getOrDefault(playerUUID, Map.of()).getOrDefault(book.getId(), Map.of());
    }

    public void stampVisibleEntries(UUID playerUUID, Book book, Set<Identifier> visibleEntryIds, long now) {
        var byBook = this.unlockTimestamps.computeIfAbsent(playerUUID, uuid -> Object2ObjectMaps.synchronize(new Object2ObjectOpenHashMap<>()));
        var timestamps = byBook.computeIfAbsent(book.getId(), id -> Object2ObjectMaps.synchronize(new Object2ObjectOpenHashMap<>()));
        boolean changed = false;
        for (var entryId : visibleEntryIds) {
            if (!timestamps.containsKey(entryId)) {
                timestamps.put(entryId, now);
                changed = true;
            }
        }
        if (changed) {
            this.setDirty();
        }
    }

    private static <K1, K2> Map<K1, Map<K2, Set<Identifier>>> synchronizeNestedSets(Map<K1, Map<K2, Set<Identifier>>> source) {
        var outer = Object2ObjectMaps.synchronize(new Object2ObjectOpenHashMap<K1, Map<K2, Set<Identifier>>>());
        source.forEach((outerKey, inner) -> {
            Map<K2, Set<Identifier>> innerMap = Object2ObjectMaps.synchronize(new Object2ObjectOpenHashMap<>());
            inner.forEach((innerKey, values) -> innerMap.put(innerKey, new ObjectOpenHashSet<>(values)));
            outer.put(outerKey, innerMap);
        });
        return outer;
    }

    private static <K1, K2, V> Map<K1, Map<K2, Map<Identifier, V>>> synchronizeNestedMaps(Map<K1, Map<K2, Map<Identifier, V>>> source) {
        var outer = Object2ObjectMaps.synchronize(new Object2ObjectOpenHashMap<K1, Map<K2, Map<Identifier, V>>>());
        source.forEach((outerKey, inner) -> {
            Map<K2, Map<Identifier, V>> innerMap = Object2ObjectMaps.synchronize(new Object2ObjectOpenHashMap<>());
            inner.forEach((innerKey, values) -> innerMap.put(innerKey, Object2ObjectMaps.synchronize(new Object2ObjectOpenHashMap<>(values))));
            outer.put(outerKey, innerMap);
        });
        return outer;
    }
}
