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

import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.util.UUID;

public record WorldPosition(UUID world, Position position) {

    public static WorldPosition at(UUID world, double x, double y, double z) {
        return new WorldPosition(world, Position.at(x, y, z));
    }

    public static WorldPosition fromLocation(Location location) {
        return new WorldPosition(location.getWorld().getUID(), Position.fromLocation(location));
    }

    public Location toLocation() {
        return new Location(Bukkit.getWorld(this.world), this.position.x(), this.position.y(), this.position.z(), this.position.yaw(), this.position.pitch());
    }

    public boolean isSameBlock(WorldPosition other) {
        return this.world.equals(other.world) && this.position.isSameBlock(other.position);
    }

}
