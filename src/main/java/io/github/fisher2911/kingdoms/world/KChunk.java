package io.github.fisher2911.kingdoms.world;

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

}
