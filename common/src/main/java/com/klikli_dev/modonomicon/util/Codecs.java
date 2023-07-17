package com.klikli_dev.modonomicon.util;

import com.mojang.serialization.Codec;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class Codecs {
    public static final Codec<UUID> UUID = Codec.STRING.xmap(java.util.UUID::fromString, java.util.UUID::toString);

    public static <K, V> Codec<Map<K, V>> mutableMap(final Codec<K> keyCodec, final Codec<V> elementCodec) {
        return mutableMapFromMap(Codec.unboundedMap(keyCodec, elementCodec));
    }

    public static <K, V> Codec<Map<K, V>> mutableMapFromMap(Codec<Map<K, V>> mapCodec) {
        return mapCodec.xmap(HashMap::new, HashMap::new);
    }

    public static <K, V> Codec<ConcurrentMap<K, V>> concurrentMap(final Codec<K> keyCodec, final Codec<V> elementCodec) {
        return concurrentMapFromMap(Codec.unboundedMap(keyCodec, elementCodec));
    }

    public static <K, V> Codec<ConcurrentMap<K, V>> concurrentMapFromMap(Codec<Map<K, V>> mapCodec) {
        return mapCodec.xmap(ConcurrentHashMap::new, HashMap::new);
    }

    public static <V> Codec<Set<V>> set(Codec<V> elementCodec) {
        return setFromList(elementCodec.listOf());
    }

    public static <V> Codec<Set<V>> setFromList(Codec<List<V>> listCodec) {
        return listCodec.xmap(HashSet::new, ArrayList::new);
    }

}
