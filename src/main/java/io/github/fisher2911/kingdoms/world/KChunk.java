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
