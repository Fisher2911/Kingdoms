package io.github.fisher2911.kingdoms.placeholder.wrapper;

import io.github.fisher2911.kingdoms.kingdom.permission.KPermission;

public record PermissionWrapper(KPermission permission, boolean value)  {

    public static PermissionWrapper wrap(KPermission permission, boolean value) {
        return new PermissionWrapper(permission, value);
    }

}
