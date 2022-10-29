package io.github.fisher2911.kingdoms.kingdom.relation;

import io.github.fisher2911.kingdoms.kingdom.ClaimedChunk;
import io.github.fisher2911.kingdoms.kingdom.permission.KPermission;
import io.github.fisher2911.kingdoms.kingdom.permission.RolePermissionHolder;
import io.github.fisher2911.kingdoms.kingdom.role.Role;

import java.util.Map;

public class Relation implements RolePermissionHolder {

    private final RelationType relationType;
    private final Role role;
    private final Map<KPermission, Boolean> permissions;

    public Relation(RelationType relationType, Role role, Map<KPermission, Boolean> permissions) {
        this.relationType = relationType;
        this.role = role;
        this.permissions = permissions;
    }

    @Override
    public boolean hasPermission(Role role, KPermission permission) {
        return role.isAtLeastRank(this.role) && this.permissions.getOrDefault(permission, false);
    }

    public boolean hasPermission(Role role, KPermission permission, ClaimedChunk chunk) {
        return chunk.getRelations().get(this.relationType).hasPermission(role, permission);
    }

    @Override
    public void setPermission(Role role, KPermission permission, boolean value) {
        this.permissions.put(permission, value);
    }

    public RelationType getRelationType() {
        return relationType;
    }

    public Role getRole() {
        return role;
    }

    public Map<KPermission, Boolean> getPermissions() {
        return permissions;
    }
}
