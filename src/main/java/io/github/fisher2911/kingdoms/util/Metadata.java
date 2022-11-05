/*
 *     Kingdoms Plugin
 *     Copyright (C) 2022  Fisher2911
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package io.github.fisher2911.kingdoms.util;

import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class Metadata {

    private final Map<Object, Object> metadata;

    public Metadata(final Map<Object, Object> metadata) {
        this.metadata = metadata;
    }

    public Map<Object, Object> get() {
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

    public Metadata copyWith(Metadata metadata, boolean overwrite) {
        final Map<Object, Object> newMap = new HashMap<>(this.metadata);
        if (overwrite) {
            newMap.putAll(metadata.get());
        } else {
            metadata.get().forEach((key, value) -> {
                if (!newMap.containsKey(key)) {
                    newMap.put(key, value);
                }
            });
        }
        newMap.putAll(metadata.get());
        return new Metadata(newMap);
    }

    @Nullable
    public <T> T get(Object key, Class<T> clazz) {
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

    public void set(Object key, Object value, boolean overwrite) {
        if (overwrite) {
            this.metadata.put(key, value);
            return;
        }
        this.metadata.putIfAbsent(key, value);
    }

    public void set(Map<Object, Object> metadata) {
        this.metadata.clear();
        this.metadata.putAll(metadata);
    }

    public void putAll(Map<Object, Object> metadata, boolean overwrite) {
        if (overwrite) {
            this.metadata.putAll(metadata);
        } else {
            metadata.forEach((key, value) -> {
                if (!this.metadata.containsKey(key)) {
                    this.metadata.put(key, value);
                }
            });
        }
    }

    public void putAll(Metadata metadata, boolean overwrite) {
        this.putAll(metadata.get(), overwrite);
    }

    @Override
    public String toString() {
        return "Metadata{" +
                "metadata=" + metadata +
                '}';
    }
}
