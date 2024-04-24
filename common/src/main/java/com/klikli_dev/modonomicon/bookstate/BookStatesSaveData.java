/*
 * SPDX-FileCopyrightText: 2023 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.bookstate;

import com.klikli_dev.modonomicon.util.Codecs;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.world.level.saveddata.SavedData;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class BookStatesSaveData extends SavedData {
    public static final Codec<BookStatesSaveData> CODEC = RecordCodecBuilder.create((instance) -> instance.group(
            Codecs.concurrentMap(Codecs.UUID, BookUnlockStates.CODEC).fieldOf("unlockStates").forGetter((state) -> state.unlockStates),
            Codecs.concurrentMap(Codecs.UUID, BookVisualStates.CODEC).fieldOf("visualStates").forGetter((state) -> state.visualStates)
    ).apply(instance, BookStatesSaveData::new));

    public static final String ID = "modonomicon_book_states";

    public ConcurrentMap<UUID, BookUnlockStates> unlockStates;
    public ConcurrentMap<UUID, BookVisualStates> visualStates;

    public BookStatesSaveData() {
        this(new ConcurrentHashMap<>(), new ConcurrentHashMap<>());
    }

    public BookStatesSaveData(ConcurrentMap<UUID, BookUnlockStates> unlockStates, ConcurrentMap<UUID, BookVisualStates> visualStates) {
        this.unlockStates = unlockStates;
        this.visualStates = visualStates;

        this.setDirty();
    }

    public static BookStatesSaveData load(CompoundTag pCompoundTag, HolderLookup.Provider pHolderProvider) {
        return CODEC.parse(NbtOps.INSTANCE, pCompoundTag.get("bookStates")).result().orElseThrow();
    }

    public BookUnlockStates getUnlockStates(UUID playerUUID) {
        return this.unlockStates.computeIfAbsent(playerUUID, (uuid) -> {
            this.setDirty();
            return new BookUnlockStates();
        });
    }

    public BookVisualStates getVisualStates(UUID playerUUID) {
        return this.visualStates.computeIfAbsent(playerUUID, (uuid) -> {
            this.setDirty();
            return new BookVisualStates();
        });
    }

    @Override
    public CompoundTag save(CompoundTag compoundTag, HolderLookup.Provider pHolderProvider) {
        compoundTag.put("bookStates", CODEC.encodeStart(NbtOps.INSTANCE, this).result().orElseThrow());
        return compoundTag;
    }
}
