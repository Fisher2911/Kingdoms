package io.github.fisher2911.kingdoms.world;

import javax.xml.stream.Location;

public record BlockPosition(int x, int y, int z) {

    BlockPosition fromLocation(Location location) {
        return new BlockPosition(x, y, z);
    }

    public BlockPosition add(int x, int y, int z) {
        return new BlockPosition(this.x + x, this.y + y, this.z + z);
    }

    public BlockPosition subtract(int x, int y, int z) {
        return this.add(-x, -y, -z);
    }

    public BlockPosition multiply(int x, int y, int z) {
        return new BlockPosition(this.x * x, this.y * y, this.z * z);
    }

    public BlockPosition multiply(int scalar) {
        return this.multiply(scalar, scalar, scalar);
    }

    public BlockPosition divide(int scalar) {
        return this.divide(scalar, scalar, scalar);
    }

    public BlockPosition divide(int x, int y, int z) {
        return new BlockPosition(this.x / x, this.y / y, this.z / z);
    }

}
