package io.github.fisher2911.kingdoms.world;

import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.util.UUID;

public record WorldPosition(UUID world, Position position) {

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
