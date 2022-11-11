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

import org.bukkit.Location;

import java.util.UUID;

public record Position(double x, double y, double z, float yaw, float pitch) {

    public static Position at(double x, double y, double z) {
        return new Position(x, y, z, 0, 0);
    }

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

    public double distanceSq(Position position) {
        return Math.pow(this.x - position.x, 2) + Math.pow(this.y - position.y, 2) + Math.pow(this.z - position.z, 2);
    }

    public double distance(Position position) {
        return Math.sqrt(this.distanceSq(position));
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

    public int getChunkX() {
        return this.getBlockX() >> 4;
    }

    public int getChunkZ() {
        return this.getBlockZ() >> 4;
    }

    public boolean isSameBlock(Position position) {
        return this.getBlockX() == position.getBlockX() && this.getBlockY() == position.getBlockY() && this.getBlockZ() == position.getBlockZ();
    }

}
