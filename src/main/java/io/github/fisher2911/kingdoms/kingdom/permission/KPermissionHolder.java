package io.github.fisher2911.kingdoms.kingdom.permission;

import io.github.fisher2911.kingdoms.user.User;

public interface KPermissionHolder extends RolePermissionHolder {

    boolean hasPermission(User user, KPermission permission);
    void setPermission(User user, KPermission permission, boolean value);

}
