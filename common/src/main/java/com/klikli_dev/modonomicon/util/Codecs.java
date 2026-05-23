/*
 * SPDX-FileCopyrightText: 2023 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.util;

import com.mojang.serialization.DataResult;
import com.mojang.serialization.Codec;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.resources.Identifier;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class Codecs {
    public static final Codec<UUID> UUID = Codec.STRING.xmap(java.util.UUID::fromString, java.util.UUID::toString);
    public static final Codec<Identifier> STRICT_IDENTIFIER = Codec.STRING.comapFlatMap(Codecs::strictIdentifierResult, Identifier::toString);

    public static DataResult<Identifier> strictIdentifierResult(String value) {
        if (!value.contains(":")) {
            return DataResult.error(() -> "Expected fully qualified identifier, got '" + value + "'");
        }

        var id = Identifier.tryParse(value);
        if (id == null) {
            return DataResult.error(() -> "Invalid identifier '" + value + "'");
        }

        return DataResult.success(id);
    }

    public static Identifier parseStrictIdentifier(String value) {
        return strictIdentifierResult(value)
                .getOrThrow(error -> new IllegalArgumentException(error));
    }

    public static Identifier tryParseStrictIdentifier(String value) {
        if (!value.contains(":")) {
            return null;
        }

        return Identifier.tryParse(value);
    }

    public static <V> Codec<Set<V>> set(Codec<V> elementCodec) {
        return setFromList(elementCodec.listOf());
    }

    public static <V> Codec<Set<V>> setFromList(Codec<List<V>> listCodec) {
        return listCodec.xmap(ObjectOpenHashSet::new, ArrayList::new);
    }

}
