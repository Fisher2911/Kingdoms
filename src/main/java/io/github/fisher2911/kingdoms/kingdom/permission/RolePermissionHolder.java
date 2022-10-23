package io.github.fisher2911.kingdoms.kingdom.permission;

import io.github.fisher2911.kingdoms.kingdom.role.Role;

public interface RolePermissionHolder {

    boolean hasPermission(Role role, KPermission permission);
    void setPermission(Role role, KPermission permission, boolean value);

}
