package io.github.fisher2911.kingdoms.world;

import org.bukkit.Location;

import java.util.UUID;

public record Position(double x, double y, double z, float yaw, float pitch) {

    public static Position fromLocation(Location location) {
        return new Position(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
    }

    public Position(double x, double y, double z) {
        this(x, y, z, 0, 0);
    }

    public WorldPosition toWorldPosition(UUID world) {
        return new WorldPosition(world, this);
    }

    public Position add(double x, double y, double z) {
        return new Position(this.x + x, this.y + y, this.z + z);
    }

    public Position subtract(double x, double y, double z) {
        return this.add(-x, -y, -z);
    }

    public Position multiply(double x, double y, double z) {
        return new Position(this.x * x, this.y * y, this.z * z);
    }

    public Position multiply(double scalar) {
        return this.multiply(scalar, scalar, scalar);
    }

    public Position divide(double scalar) {
        return this.divide(scalar, scalar, scalar);
    }

    public Position divide(double x, double y, double z) {
        return new Position(this.x / x, this.y / y, this.z / z);
    }

    public BlockPosition toBlock() {
        return new BlockPosition((int) this.x, (int) this.y, (int) this.z);
    }

    public int getBlockX() {
        return (int) this.x;
    }

    public int getBlockY() {
        return (int) this.y;
    }

    public int getBlockZ() {
        return (int) this.z;
    }

    public boolean isSameBlock(Position position) {
        return this.getBlockX() == position.getBlockX() && this.getBlockY() == position.getBlockY() && this.getBlockZ() == position.getBlockZ();
    }

}
