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

package io.github.fisher2911.kingdoms.util.collections;

import io.github.fisher2911.kingdoms.data.Saveable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

public class DirtyMap<K, V> implements Map<K, V>, Saveable {

    private final Map<K, V> internal;
    private boolean dirty;

    public DirtyMap(Map<K, V> internal) {
        this.internal = internal;
    }

    @Override
    public int size() {
        return this.internal.size();
    }

    @Override
    public boolean isEmpty() {
        return this.internal.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return this.internal.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return this.internal.containsValue(value);
    }

    @Override
    public V get(Object key) {
        return this.internal.get(key);
    }

    @Nullable
    @Override
    public V put(K key, V value) {
        final V previous = this.internal.put(key, value);
        this.setDirty(true);
        return previous;
    }

    @Override
    public V remove(Object key) {
        final V previous = this.internal.remove(key);
        this.setDirty(true);
        return previous;
    }

    @Override
    public void putAll(@NotNull Map<? extends K, ? extends V> m) {
        this.internal.putAll(m);
        this.setDirty(true);
    }

    @Override
    public void clear() {
        this.internal.clear();
        this.setDirty(true);
    }

    @NotNull
    @Override
    public Set<K> keySet() {
        return this.internal.keySet();
    }

    @NotNull
    @Override
    public Collection<V> values() {
        return this.internal.values();
    }

    @NotNull
    @Override
    public Set<Entry<K, V>> entrySet() {
        return this.internal.entrySet();
    }

    @Override
    public boolean isDirty() {
        return this.dirty;
    }

    @Override
    public void setDirty(boolean dirty) {
        this.dirty = dirty;
    }

}
