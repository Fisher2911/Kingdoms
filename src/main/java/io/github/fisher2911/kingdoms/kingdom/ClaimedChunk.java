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

package io.github.fisher2911.kingdoms.kingdom;

import io.github.fisher2911.kingdoms.Kingdoms;
import io.github.fisher2911.kingdoms.api.metadata.MetadataKey;
import io.github.fisher2911.kingdoms.data.Saveable;
import io.github.fisher2911.kingdoms.kingdom.permission.KPermission;
import io.github.fisher2911.kingdoms.kingdom.permission.PermissionContainer;
import io.github.fisher2911.kingdoms.kingdom.permission.RolePermissionHolder;
import io.github.fisher2911.kingdoms.kingdom.role.Role;
import io.github.fisher2911.kingdoms.util.Metadata;
import io.github.fisher2911.kingdoms.world.KChunk;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;

public class ClaimedChunk implements Claim, RolePermissionHolder, Saveable {

    private final Kingdoms plugin;
    private final int claimedBy;
    private final KChunk chunk;
    private final PermissionContainer permissions;
    /**
     * The metadata for this chunk, not persistent so make sure to add the data on chunk load
     */
    private final Metadata metadata;
    private boolean dirty;

    public ClaimedChunk(Kingdoms plugin, int claimedBy, KChunk chunk, PermissionContainer permissions, Metadata metadata) {
        this.plugin = plugin;
        this.claimedBy = claimedBy;
        this.chunk = chunk;
        this.permissions = permissions;
        this.metadata = metadata;
    }

    public ClaimedChunk(Kingdoms plugin, int claimedBy, KChunk chunk, PermissionContainer permissions) {
        this(plugin, claimedBy, chunk, permissions, new Metadata(new HashMap<>()));
    }

    public static ClaimedChunk wilderness(Kingdoms plugin, KChunk at) {
        return new ClaimedChunk(plugin, Kingdom.WILDERNESS_ID, at, PermissionContainer.empty());
    }

    @Override
    public KChunk getChunk() {
        return chunk;
    }

    @Override
    public boolean isWilderness() {
        return this.claimedBy == Kingdom.WILDERNESS_ID;
    }

    @Override
    public int getKingdomId() {
        return this.claimedBy;
    }

    public PermissionContainer getPermissions() {
        return permissions;
    }

    @Override
    public UUID getWorld() {
        return this.chunk.world();
    }

    @Override
    public boolean hasPermission(Role role, KPermission permission) {
        return this.permissions.hasPermission(role, permission, this.plugin.getRoleManager());
    }

    @Override
    public void setPermission(Role role, KPermission permission, boolean value) {
            this.permissions.setPermission(role, permission, value);
            this.setDirty(true);
    }

    @Override
    public <T> @Nullable T getData(MetadataKey key, Class<T> clazz) {
        return this.metadata.get(key, clazz);
    }

    @Override
    public void setData(MetadataKey key, Object value) {
        this.metadata.set(key, value);
    }

    @Override
    public boolean isDirty() {
        return this.dirty;
    }

    @Override
    public void setDirty(boolean dirty) {
        this.dirty = dirty;
    }

    @Override
    public String toString() {
        return "ClaimedChunk{" +
                "claimedBy=" + claimedBy +
                ", chunk=" + chunk +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final ClaimedChunk that = (ClaimedChunk) o;
        return Objects.equals(getChunk(), that.getChunk());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getChunk());
    }
}
