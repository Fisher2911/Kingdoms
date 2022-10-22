package io.github.fisher2911.kingdoms.kingdom;

import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import io.github.fisher2911.kingdoms.Kingdoms;
import io.github.fisher2911.kingdoms.economy.Price;
import io.github.fisher2911.kingdoms.kingdom.permission.KPermission;
import io.github.fisher2911.kingdoms.kingdom.permission.PermissionContainer;
import io.github.fisher2911.kingdoms.kingdom.role.Role;
import io.github.fisher2911.kingdoms.kingdom.upgrade.IntUpgrades;
import io.github.fisher2911.kingdoms.kingdom.upgrade.UpgradeHolder;
import io.github.fisher2911.kingdoms.kingdom.upgrade.UpgradeId;
import io.github.fisher2911.kingdoms.kingdom.upgrade.Upgrades;
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
    private final UpgradeHolder upgradeHolder;
    private final Map<String, Integer> upgradeLevels;

    public KingdomImpl(
            Kingdoms plugin,
            int id,
            String name,
            Map<UUID, User> members,
            Map<UUID, Role> userRoles,
            PermissionContainer permissions,
            PermissionContainer defaultPermissions,
            Set<ClaimedChunk> claims,
            UpgradeHolder upgradeHolder,
            Map<String, Integer> upgradeLevels
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
        this.upgradeHolder = upgradeHolder;
        this.upgradeLevels = upgradeLevels;
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
    public int getMaxMembers() {
        final IntUpgrades maxMembers = this.upgradeHolder.getUpgrades(UpgradeId.MAX_MEMBERS.toString(), IntUpgrades.class);
        if (maxMembers == null) return 0;
        final Integer level = this.getUpgradeLevel(maxMembers.getId());
        if (level == null) return 0;
        final Integer value = maxMembers.getValueAtLevel(level);
        if (value == null) return 0;
        return value;
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
        return this.defaultChunkPermissions.copy();
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
        if (chunk.getPermissions().containsPermission(role, permission)) {
            return chunk.getPermissions().hasPermission(role, permission);
        }
        return this.hasPermission(role, permission);
    }

    @Override
    public boolean hasPermission(Role role, KPermission permission) {
        return this.permissions.hasPermission(role, permission);
    }

    @Override
    public boolean hasPermission(Role role, KPermission permission, ClaimedChunk chunk) {
        if (chunk.getPermissions().containsPermission(role, permission)) {
            return chunk.getPermissions().hasPermission(role, permission);
        }
        return this.hasPermission(role, permission);
    }

    @Override
    public Collection<ClaimedChunk> getClaimedChunks() {
        return this.claims;
    }

    @Override
    public void addClaimedChunk(ClaimedChunk chunk) {
        this.claims.add(chunk);
    }

    @Override
    public void removeClaimedChunk(ClaimedChunk chunk) {
        this.claims.remove(chunk);
    }


    @Override
    public void setRole(User user, Role role) {
        this.roles.put(role, user.getId());
        this.userRoles.put(user.getId(), role);
    }

    @Override
    public void addMember(User user) {

    }

    @Override
    public void removeMember(User member) {
        this.members.remove(member.getId());
    }

    @Override
    public Role getRole(UUID uuid) {
        return this.userRoles.getOrDefault(uuid, this.plugin.getRoleManager().getNonMember());
    }

    @Override
    public boolean isFull() {
        return this.getMaxMembers() <= this.getMembers().size();
    }

    @Override
    public UpgradeHolder getUpgradeHolder() {
        return this.upgradeHolder;
    }

    @Override
    public Integer getUpgradeLevel(String id) {
        return this.upgradeLevels.computeIfAbsent(id, i -> 1);
    }

    @Override
    public Price getUpgradePrice(String id) {
        final Integer level = this.getUpgradeLevel(id);
        if (level == null) return Price.IMPOSSIBLE;
        final Upgrades<?> upgrades = this.upgradeHolder.getUpgrades(id);
        if (upgrades == null) return Price.IMPOSSIBLE;
        return upgrades.getPriceAtLevel(level);
    }

    @Override
    public void setUpgradeLevel(String id, int level) {
        final Upgrades<?> upgrades = this.upgradeHolder.getUpgrades(id);
        if (upgrades == null) return;
        if (upgrades.getMaxLevel() <= level) return;
        this.upgradeLevels.put(id, level);
    }

    @Override
    public int getAvailableChunks() {
        return this.getTotalPossibleChunks() - this.claims.size();
    }

    @Override
    public int getTotalPossibleChunks() {
        final String id = UpgradeId.MAX_CLAIMS.toString();
        final Integer upgradeLevel = this.getUpgradeLevel(id);
        if (upgradeLevel == null) return 0;
        final Integer maxClaims = this.upgradeHolder.getValueAtLevel(
                id,
                IntUpgrades.class,
                upgradeLevel
        );
        if (maxClaims == null) return 0;
        return maxClaims;
    }
}
