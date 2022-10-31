package io.github.fisher2911.kingdoms.util;

import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class Metadata {

    private final Map<Object, Object> metadata;

    public Metadata(final Map<Object, Object> metadata) {
        this.metadata = metadata;
    }

    public Map<Object, Object> getMetadata() {
        return metadata;
    }

    public static Metadata of(Map<Object, Object> metadata) {
        return new Metadata(metadata);
    }

    public static Metadata empty() {
        return new Metadata(new HashMap<>());
    }

    public Metadata copy() {
        return new Metadata(new HashMap<>(this.metadata));
    }

    public Metadata copyWith(Metadata metadata) {
        final Map<Object, Object> newMap = new HashMap<>(this.metadata);
        newMap.putAll(metadata.getMetadata());
        return new Metadata(newMap);
    }

    @Nullable
    public <T> T getMetadata(Object key, Class<T> clazz) {
        final Object o = this.metadata.get(key);
        if (o == null) return null;
        if (!clazz.isInstance(o)) return null;
        return clazz.cast(o);
    }

    @Nullable
    public Object get(Object key) {
        return this.metadata.get(key);
    }

    public void set(Object key, Object value) {
        this.metadata.put(key, value);
    }

    public void set(Map<Object, Object> metadata) {
        this.metadata.clear();
        this.metadata.putAll(metadata);
    }

    public void putAll(Map<Object, Object> metadata) {
        this.metadata.putAll(metadata);
    }

    public void putAll(Metadata metadata) {
        this.metadata.putAll(metadata.getMetadata());
    }

    @Override
    public String toString() {
        return "Metadata{" +
                "metadata=" + metadata +
                '}';
    }
}
