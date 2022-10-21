package io.github.fisher2911.kingdoms.kingdom;

import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import io.github.fisher2911.kingdoms.Kingdoms;
import io.github.fisher2911.kingdoms.kingdom.permission.KPermission;
import io.github.fisher2911.kingdoms.kingdom.permission.PermissionContainer;
import io.github.fisher2911.kingdoms.kingdom.role.Role;
import io.github.fisher2911.kingdoms.user.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;

public class KingdomImpl implements Kingdom {

    private final Kingdoms plugin;
    private final int id;
    private final String name;
    private final Map<UUID, User> members;
    private final Map<UUID, Role> userRoles;
    private final Multimap<Role, UUID> roles;
    private final PermissionContainer permissions;
    private final PermissionContainer defaultChunkPermissions;
    private final Set<ClaimedChunk> claims;

    public KingdomImpl(
            Kingdoms plugin,
            int id,
            String name,
            Map<UUID, User> members,
            Map<UUID, Role> userRoles,
            PermissionContainer permissions,
            PermissionContainer defaultPermissions,
            Set<ClaimedChunk> claims
    ) {
        this.plugin = plugin;
        this.id = id;
        this.name = name;
        this.members = members;
        this.userRoles = userRoles;
        this.roles = Multimaps.newSetMultimap(new HashMap<>(), HashSet::new);
        for (var entry : this.userRoles.entrySet()) {
            this.roles.put(entry.getValue(), entry.getKey());
        }
        this.permissions = permissions;
        this.defaultChunkPermissions = defaultPermissions;
        this.claims = claims;
    }

    @Override
    public int getId() {
        return this.id;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public Collection<User> getMembers() {
        return this.members.values();
    }

    @Override
    public Map<UUID, Role> getUserRoles() {
        return this.userRoles;
    }

    @Override
    public void consumeRoles(Consumer<User> consumer, Role... roles) {
        for (Role role : roles) {
            for (UUID uuid : this.roles.get(role)) {
                final User user = this.members.get(uuid);
                if (user == null) continue;
                consumer.accept(user);
            }
        }
    }

    @Override
    public PermissionContainer getPermissions() {
        return this.permissions;
    }

    @Override
    public PermissionContainer getDefaultChunkPermissions() {
        return this.defaultChunkPermissions;
    }

    @Override
    public boolean hasPermission(User user, KPermission permission) {
        final Role role = this.getRole(user.getId());
        if (role == null) return false;
        return this.hasPermission(role, permission);
    }

    @Override
    public boolean hasPermission(User user, KPermission permission, ClaimedChunk chunk) {
        final Role role = this.getRole(user.getId());
        return chunk.getPermissions().hasPermission(role, permission) || this.hasPermission(role, permission);
    }

    @Override
    public boolean hasPermission(Role role, KPermission permission) {
        return this.permissions.hasPermission(role, permission);
    }

    @Override
    public boolean hasPermission(Role role, KPermission permission, ClaimedChunk chunk) {
        return chunk.getPermissions().hasPermission(role, permission) || this.hasPermission(role, permission);
    }

    @Override
    public Collection<ClaimedChunk> getClaimedChunks() {
        return this.claims;
    }

    @Override
    public void setRole(User user, Role role) {
        this.roles.put(role, user.getId());
        this.userRoles.put(user.getId(), role);
    }

    @Override
    public void removeMember(User member) {
        this.members.remove(member.getId());
    }

    @Override
    public Role getRole(UUID uuid) {
        return this.userRoles.getOrDefault(uuid, this.plugin.getRoleManager().getNonMember());
    }
}
