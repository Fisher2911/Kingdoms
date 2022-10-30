package io.github.fisher2911.kingdoms.util;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

public class MapOfMaps<K, E, V> implements Map<K, Map<E, V>> {

    private final Map<K, Map<E, V>> map;
    private final Supplier<Map<E, V>> mapFactory;

    public static <K, E, V> MapOfMaps<K, E, V> newHashMap() {
        return new MapOfMaps<>(new HashMap<>(), HashMap::new);
    }

    public MapOfMaps(Map<K, Map<E, V>> map, Supplier<Map<E, V>> mapFactory) {
        this.map = map;
        this.mapFactory = mapFactory;
    }

    public MapOfMaps(MapOfMaps<K, E, V> map) {
        this.mapFactory = map.mapFactory;
        this.map = new HashMap<>();
        for (var entry : map.map.entrySet()) {
            final Map<E, V> add = this.mapFactory.get();
            add.putAll(entry.getValue());
            this.map.put(entry.getKey(), add);
        }
    }

    @Nullable
    public V put(K k, E e, V v) {
        return this.map.computeIfAbsent(k, i -> this.mapFactory.get()).put(e, v);
    }

    @Nullable
    public V get(K k, E e) {
        final Map<E, V> current = this.map.get(k);
        if (current == null) return null;
        return current.get(e);
    }

    public V getOrDefault(K k, E e, V def) {
        final Map<E, V> current = this.map.get(k);
        if (current == null) return def;
        return current.getOrDefault(e, def);
    }

    @Nullable
    public V removeMapValue(K k, E e) {
        final Map<E, V> current = this.map.get(k);
        if (current == null) return null;
        return current.remove(e);
    }

    @Override
    public int size() {
        return this.map.size();
    }

    @Override
    public boolean isEmpty() {
        return this.map.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return this.map.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return this.map.containsValue(value);
    }

    @Override
    public Map<E, V> get(Object key) {
        return this.map.computeIfAbsent((K) key, i -> this.mapFactory.get());
    }

    @Nullable
    @Override
    public Map<E, V> put(K key, Map<E, V> value) {
        return this.map.put(key, value);
    }

    @Override
    public Map<E, V> remove(Object key) {
        return this.map.remove(key);
    }

    @Override
    public void putAll(@NotNull Map<? extends K, ? extends Map<E, V>> m) {
        this.map.putAll(m);
    }

    @Override
    public void clear() {
        this.map.clear();
    }

    @NotNull
    @Override
    public Set<K> keySet() {
        return this.map.keySet();
    }

    @NotNull
    @Override
    public Collection<Map<E, V>> values() {
        return this.map.values();
    }

    @NotNull
    @Override
    public Set<Entry<K, Map<E, V>>> entrySet() {
        return this.map.entrySet();
    }

    @Override
    public String toString() {
        return "MapOfMaps{" +
                "map=" + map +
                '}';
    }
}
