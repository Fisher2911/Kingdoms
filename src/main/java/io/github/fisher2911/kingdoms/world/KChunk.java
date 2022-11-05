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

package io.github.fisher2911.kingdoms.world;

import java.util.Objects;
import java.util.UUID;

public record KChunk(UUID world, int x, int z) {

    public static KChunk at(UUID world, int x, int z) {
        return new KChunk(world, x, z);
    }

    public long getChunkKey() {
        return chunkKeyAt(this.x, this.z);
    }

    public static long chunkKeyAt(int x, int z) {
        return (long) x & 0xffffffffL | ((long) z & 0xffffffffL) << 32;
    }

    public WorldPosition getCenter() {
        return WorldPosition.at(this.world, this.x * 16 + 8, 0, this.z * 16 + 8);
    }

    @Override
    public String toString() {
        return "KChunk{" +
                "world=" + world +
                ", x=" + x +
                ", z=" + z +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final KChunk kChunk = (KChunk) o;
        return x == kChunk.x && z == kChunk.z && Objects.equals(world, kChunk.world);
    }

    @Override
    public int hashCode() {
        return Objects.hash(world, x, z);
    }
}
