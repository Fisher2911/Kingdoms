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

import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class SortedList<E> implements List<E> {

    private final List<E> list;
    private final Comparator<E> comparator;

    public SortedList(List<E> list, Comparator<E> comparator) {
        this.list = list;
        this.comparator = comparator;
        this.sort();
    }

    @Override
    public int size() {
        return this.list.size();
    }

    @Override
    public boolean isEmpty() {
        return this.list.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return this.list.contains(o);
    }

    @NotNull
    @Override
    public Iterator<E> iterator() {
        return this.list.iterator();
    }

    @NotNull
    @Override
    public Object[] toArray() {
        return this.list.toArray();
    }

    @NotNull
    @Override
    public <T> T[] toArray(@NotNull T[] a) {
        return this.list.toArray(a);
    }

    @Override
    public boolean add(E e) {
        final boolean added = this.list.add(e);
        this.list.sort(this.comparator);
        return added;
    }

    @Override
    public boolean remove(Object o) {
        final boolean removed = this.list.remove(o);
        this.list.sort(this.comparator);
        return removed;
    }

    @Override
    public boolean containsAll(@NotNull Collection<?> c) {
        return this.list.contains(c);
    }

    @Override
    public boolean addAll(@NotNull Collection<? extends E> c) {
        final boolean added = this.list.addAll(c);
        this.sort();
        return added;
    }

    @Override
    public boolean addAll(int index, @NotNull Collection<? extends E> c) {
        final boolean added = this.list.addAll(index, c);
        this.sort();
        return added;
    }

    @Override
    public boolean removeAll(@NotNull Collection<?> c) {
        final boolean removed = this.list.removeAll(c);
        this.sort();
        return removed;
    }

    @Override
    public boolean retainAll(@NotNull Collection<?> c) {
        final boolean retained = this.list.retainAll(c);
        this.sort();
        return retained;
    }

    @Override
    public void clear() {
        this.list.clear();
    }

    @Override
    public E get(int index) {
        return this.list.get(index);
    }

    @Override
    public E set(int index, E element) {
        final E set = this.list.set(index, element);
        this.sort();
        return set;
    }

    @Override
    public void add(int index, E element) {
        this.list.add(index, element);
        this.sort();
    }

    @Override
    public E remove(int index) {
        final E removed = this.list.remove(index);
        this.sort();
        return removed;
    }

    @Override
    public int indexOf(Object o) {
        return this.list.indexOf(o);
    }

    @Override
    public int lastIndexOf(Object o) {
        return this.list.lastIndexOf(o);
    }

    @NotNull
    @Override
    public ListIterator<E> listIterator() {
        return this.list.listIterator();
    }

    @NotNull
    @Override
    public ListIterator<E> listIterator(int index) {
        return this.list.listIterator(index);
    }

    @NotNull
    @Override
    public List<E> subList(int fromIndex, int toIndex) {
        return this.list.subList(fromIndex, toIndex);
    }

    public void sort() {
        this.list.sort(this.comparator);
    }
}
