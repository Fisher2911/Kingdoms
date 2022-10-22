package io.github.fisher2911.kingdoms.kingdom;

import io.github.fisher2911.kingdoms.kingdom.permission.KPermissible;
import io.github.fisher2911.kingdoms.kingdom.permission.KPermission;
import io.github.fisher2911.kingdoms.kingdom.permission.PermissionContainer;
import io.github.fisher2911.kingdoms.kingdom.role.Role;
import io.github.fisher2911.kingdoms.kingdom.upgrade.Upgradeable;
import io.github.fisher2911.kingdoms.user.User;

import java.util.Collection;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

public interface Kingdom extends KPermissible, Upgradeable {

    int WILDERNESS_ID = -1;

    int getId();
    String getName();
    Collection<User> getMembers();
    Map<UUID, Role> getUserRoles();
    void consumeRoles(Consumer<User> consumer, Role... roles);
    PermissionContainer getPermissions();
    PermissionContainer getDefaultChunkPermissions();
    boolean hasPermission(User user, KPermission permission);
    boolean hasPermission(User user, KPermission permission, ClaimedChunk chunk);
    boolean hasPermission(Role role, KPermission permission);
    boolean hasPermission(Role role, KPermission permission, ClaimedChunk chunk);
    Collection<ClaimedChunk> getClaimedChunks();
    void setRole(User user, Role role);
    void addMember(User user);
    void removeMember(User member);
    Role getRole(UUID uuid);
    boolean isFull();
    int getAvailableChunks();
    int getTotalPossibleChunks();

}
