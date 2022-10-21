package io.github.fisher2911.kingdoms.kingdom;

import io.github.fisher2911.kingdoms.kingdom.permission.PermissionContainer;
import io.github.fisher2911.kingdoms.world.KChunk;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class ClaimedChunk implements Claim {

    private final int claimedBy;
    private final KChunk chunk;
    private final PermissionContainer permissions;

    public ClaimedChunk(int claimedBy, KChunk chunk, PermissionContainer permissions) {
        this.claimedBy = claimedBy;
        this.chunk = chunk;
        this.permissions = permissions;
    }

    public static ClaimedChunk wilderness(KChunk at) {
        return new ClaimedChunk(Kingdom.WILDERNESS_ID, at, PermissionContainer.empty());
    }

    @Override
    public KChunk getChunk() {
        return chunk;
    }

    @Override
    public boolean isWilderness() {
        return this.claimedBy == -1;
    }

    @Override
    public int getOwnedBy() {
        return this.claimedBy;
    }

    @Override
    @Nullable
    // todo
    public <T> T getData(String key) {
        final World world = Bukkit.getWorld(this.chunk.world());
        if (world == null) return null;
        return null;
    }

    @Override
    public UUID getWorld() {
        return this.chunk.world();
    }

    @Override
    public PermissionContainer getPermissions() {
        return permissions;
    }

}
