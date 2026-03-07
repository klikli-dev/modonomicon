/*
 * SPDX-FileCopyrightText: 2023 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.bookstate;

import com.klikli_dev.modonomicon.Modonomicon;
import com.klikli_dev.modonomicon.util.Codecs;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.Object2ObjectMaps;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.resources.Identifier;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.UUID;

public class BookStatesSaveData extends SavedData {
    public static final Identifier ID = Modonomicon.loc( "book_states");

    public static final Codec<BookStatesSaveData> CODEC = RecordCodecBuilder.create((instance) -> instance.group(
            Codec.unboundedMap(Codecs.UUID, BookUnlockStates.CODEC).fieldOf("unlockStates").forGetter((state) -> state.unlockStates),
            Codec.unboundedMap(Codecs.UUID, BookVisualStates.CODEC).fieldOf("visualStates").forGetter((state) -> state.visualStates)
    ).apply(instance, BookStatesSaveData::new));

    public static final SavedDataType<BookStatesSaveData> TYPE = new SavedDataType<>(
            ID,
            BookStatesSaveData::new,
            CODEC, DataFixTypes.PLAYER);


    public Map<UUID, BookUnlockStates> unlockStates;
    public Map<UUID, BookVisualStates> visualStates;

    public BookStatesSaveData() {
        this(Object2ObjectMaps.emptyMap(), Object2ObjectMaps.emptyMap());
    }

    public BookStatesSaveData(Map<UUID, BookUnlockStates> unlockStates, Map<UUID, BookVisualStates> visualStates) {
        this.unlockStates = Object2ObjectMaps.synchronize(new Object2ObjectOpenHashMap<>(unlockStates));
        this.visualStates = Object2ObjectMaps.synchronize(new Object2ObjectOpenHashMap<>(visualStates));

        this.setDirty();
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
}
