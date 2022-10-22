package io.github.fisher2911.kingdoms.kingdom.permission;

import io.github.fisher2911.kingdoms.kingdom.ClaimedChunk;
import io.github.fisher2911.kingdoms.kingdom.role.Role;
import io.github.fisher2911.kingdoms.user.User;

public interface KPermissionHolder {

    boolean hasPermission(User user, KPermission permission);
    boolean hasPermission(User user, KPermission permission, ClaimedChunk chunk);
    boolean hasPermission(Role role, KPermission permission);
    boolean hasPermission(Role role, KPermission permission, ClaimedChunk chunk);

}
