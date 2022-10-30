package io.github.fisher2911.kingdoms.kingdom;

import io.github.fisher2911.kingdoms.Kingdoms;
import io.github.fisher2911.kingdoms.data.Saveable;
import io.github.fisher2911.kingdoms.kingdom.permission.KPermission;
import io.github.fisher2911.kingdoms.kingdom.permission.PermissionContainer;
import io.github.fisher2911.kingdoms.kingdom.permission.RolePermissionHolder;
import io.github.fisher2911.kingdoms.kingdom.role.Role;
import io.github.fisher2911.kingdoms.world.KChunk;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.UUID;

public class ClaimedChunk implements Claim, RolePermissionHolder, Saveable {

    private final Kingdoms plugin;
    private final int claimedBy;
    private final KChunk chunk;
    private final PermissionContainer permissions;
//    private final Map<RelationType, Relation> relations;
    private boolean dirty;

    public ClaimedChunk(Kingdoms plugin, int claimedBy, KChunk chunk, PermissionContainer permissions/*, Map<RelationType, Relation> relations*/) {
        this.plugin = plugin;
        this.claimedBy = claimedBy;
        this.chunk = chunk;
        this.permissions = permissions;
//        this.relations = relations;
    }

    public static ClaimedChunk wilderness(Kingdoms plugin, KChunk at) {
        return new ClaimedChunk(plugin, Kingdom.WILDERNESS_ID, at, PermissionContainer.empty()/*, new EnumMap<>(RelationType.class)*/);
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

/*    public Map<RelationType, Relation> getRelations() {
        return relations;
    }*/

    public PermissionContainer getPermissions() {
        return permissions;
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
    public boolean hasPermission(Role role, KPermission permission) {
//        final Relation relation = this.relations.get(this.plugin.getRelationManager().fromRole(role.id()));
//        if (relation == null) return this.permissions.hasPermission(role, permission);
//        return relation.hasPermission(role, permission);
        return this.permissions.hasPermission(role, permission);
    }

    @Override
    public void setPermission(Role role, KPermission permission, boolean value) {
//        final Relation relation = this.relations.get(this.plugin.getRelationManager().fromRole(role.id()));
//        if (relation == null) {
            this.permissions.setPermission(role, permission, value);
            this.setDirty(true);
//            return;
//        }
//        relation.setPermission(role, permission, value);
//        this.setDirty(true);
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
