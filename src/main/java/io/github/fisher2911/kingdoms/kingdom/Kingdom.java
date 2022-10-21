package io.github.fisher2911.kingdoms.kingdom;

import io.github.fisher2911.kingdoms.kingdom.permission.KPermissible;
import io.github.fisher2911.kingdoms.kingdom.permission.KPermission;
import io.github.fisher2911.kingdoms.kingdom.permission.PermissionContainer;
import io.github.fisher2911.kingdoms.kingdom.role.Role;
import io.github.fisher2911.kingdoms.user.User;

import java.util.Collection;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

public interface Kingdom extends KPermissible {

    int WILDERNESS_ID = -1;

    int getId();
    String getName();
    Collection<User> getMembers();
    Map<UUID, Role> getUserRoles();
    void consumeRoles(Consumer<User> consumer, Role... roles);
    PermissionContainer getPermissions();
    PermissionContainer getDefaultChunkPermissions();
    boolean hasPermission(User user, KPermission permission);
    Collection<ClaimedChunk> getClaimedChunks();
    void setRole(User user, Role role);
    void removeMember(User member);
    Role getRole(UUID uuid);

}
