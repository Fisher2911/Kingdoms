package io.github.fisher2911.kingdoms.kingdom;

import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import io.github.fisher2911.kingdoms.Kingdoms;
import io.github.fisher2911.kingdoms.economy.Bank;
import io.github.fisher2911.kingdoms.economy.Price;
import io.github.fisher2911.kingdoms.kingdom.location.KingdomLocations;
import io.github.fisher2911.kingdoms.kingdom.permission.KPermission;
import io.github.fisher2911.kingdoms.kingdom.permission.PermissionContainer;
import io.github.fisher2911.kingdoms.kingdom.relation.RelationInfo;
import io.github.fisher2911.kingdoms.kingdom.relation.RelationType;
import io.github.fisher2911.kingdoms.kingdom.role.Role;
import io.github.fisher2911.kingdoms.kingdom.upgrade.DoubleUpgrades;
import io.github.fisher2911.kingdoms.kingdom.upgrade.IntUpgrades;
import io.github.fisher2911.kingdoms.kingdom.upgrade.UpgradeHolder;
import io.github.fisher2911.kingdoms.kingdom.upgrade.UpgradeId;
import io.github.fisher2911.kingdoms.kingdom.upgrade.Upgrades;
import io.github.fisher2911.kingdoms.user.User;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class KingdomImpl implements Kingdom {

    private final Kingdoms plugin;
    private final int id;
    private String name;
    private String description;
    private final Map<UUID, User> members;
    private final Map<UUID, Role> userRoles;
    private final Multimap<Role, UUID> roleUsers;
    private final PermissionContainer permissions;
    private final Set<ClaimedChunk> claims;
    private final UpgradeHolder upgradeHolder;
    private final Map<String, Integer> upgradeLevels;
    private final Map<Integer, RelationInfo> kingdomRelations;
    private final Bank<Kingdom> bank;
    private final Map<String, Role> roles;
    private final KingdomLocations locations;
    private boolean dirty;

    public KingdomImpl(
            Kingdoms plugin,
            int id,
            String name,
            String description,
            Map<UUID, User> members,
            Map<UUID, Role> userRoles,
            PermissionContainer permissions,
            Set<ClaimedChunk> claims,
            UpgradeHolder upgradeHolder,
            Map<String, Integer> upgradeLevels,
            Map<Integer, RelationInfo> kingdomRelations,
            Bank<Kingdom> bank,
            Map<String, Role> roles,
            KingdomLocations locations
    ) {
        this.plugin = plugin;
        this.id = id;
        this.name = name;
        this.description = description;
        this.members = members;
        this.userRoles = userRoles;
        this.roleUsers = Multimaps.newSetMultimap(new HashMap<>(), HashSet::new);
        for (var entry : this.userRoles.entrySet()) {
            this.roleUsers.put(entry.getValue(), entry.getKey());
        }
        this.permissions = permissions;
        this.claims = claims;
        this.upgradeHolder = upgradeHolder;
        this.upgradeLevels = upgradeLevels;
        this.kingdomRelations = kingdomRelations;
        this.bank = bank;
        this.roles = roles;
        this.locations = locations;
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
    public String getDescription() {
        return description;
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
            for (UUID uuid : this.roleUsers.get(role)) {
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
        return this.permissions.copy();
    }

    @Override
    public boolean hasPermission(User user, KPermission permission) {
        final Role role = this.getRole(user);
//        final Relation relation = this.getRelation(user.getKingdomId());
//        if (relation != null) return relation.hasPermission(role, permission);
        return this.hasPermission(role, permission);
    }

    @Override
    public boolean hasPermission(User user, KPermission permission, ClaimedChunk chunk) {
        final Role role = this.getRole(user);
//        final RelationType relationType = this.getKingdomRelation(user.getKingdomId());
//        Relation relation = chunk.getRelations().get(relationType);
//        if (relation != null && relation.hasPermission(role, permission, chunk)) {
//            return true;
//        }
//        relation = this.getRelation(user.getKingdomId());
//        if (relation != null) return relation.hasPermission(role, permission);
        if (chunk.getPermissions().hasPermission(role, permission, this.plugin.getRoleManager())) {
            return true;
        }
        return this.hasPermission(role, permission);
    }

    @Override
    public void setPermission(User user, KPermission permission, boolean value) {
        this.setPermission(this.getRole(user), permission, value);
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
        this.setDirty(true);
    }


    @Override
    public boolean hasPermission(Role role, KPermission permission) {
//        final RelationType relationType = this.plugin.getRelationManager().fromRole(role.id());
//        final Relation relation = this.relations.get(relationType);
//        if (relation != null) return relation.hasPermission(role, permission);
        return this.permissions.hasPermission(role, permission, this.plugin.getRoleManager());
    }

    @Override
    public boolean hasPermission(Role role, KPermission permission, ClaimedChunk chunk) {
//        final RelationType relationType = this.plugin.getRelationManager().fromRole(role.id());
//        Relation relation = chunk.getRelations().get(relationType);
//        if (relation != null && relation.hasPermission(role, permission, chunk)) {
//            return true;
//        }
//        relation = this.relations.get(relationType);
//        if (relation != null) return relation.hasPermission(role, permission);
        if (chunk.getPermissions().hasPermission(role, permission, this.plugin.getRoleManager())) {
            return true;
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
        this.setDirty(true);
    }

    @Override
    public void removeClaimedChunk(ClaimedChunk chunk) {
        this.claims.remove(chunk);
        this.setDirty(true);
    }


    @Override
    public void setRole(User user, Role role) {
        this.roleUsers.put(role, user.getId());
        this.userRoles.put(user.getId(), role);
        this.setDirty(true);
    }

    @Override
    public void addMember(User user) {
        this.members.put(user.getId(), user);
        user.setKingdomId(this.id);
        this.setRole(user, this.plugin.getRoleManager().getMemberRole(this));
        this.setDirty(true);
    }

    @Override
    public void removeMember(User user) {
        final UUID uuid = user.getId();
        this.members.remove(uuid);
        this.roleUsers.remove(this.getRole(user), uuid);
        this.userRoles.remove(uuid);
        user.setKingdomId(Kingdom.WILDERNESS_ID);
        this.setDirty(true);
    }

    @Override
    public Role getRole(User user) {
        final UUID uuid = user.getId();
        final Role role = this.userRoles.get(uuid);
        if (role != null) return role;
        final RelationInfo relation = this.kingdomRelations.get(user.getKingdomId());
        if (relation == null) return this.plugin.getRoleManager().getNeutralRole(this);
        return relation.relationType().getRole(this, this.plugin.getRoleManager());
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
        if (upgrades.getMaxLevel() < level) return;
        this.upgradeLevels.put(id, level);
    }

    @Override
    @Nullable
    public <T> T getUpgradesValue(String id, Class<? extends Upgrades<T>> clazz) {
        final Upgrades<T> upgrades = this.upgradeHolder.getUpgrades(id, clazz);
        if (upgrades == null) return null;
        final Integer level = upgradeLevels.get(id);
        if (level == null) return null;
        return upgrades.getValueAtLevel(level);
    }

    @Override
    public Map<String, Integer> getUpgradeLevels() {
        return this.upgradeLevels;
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

    @Override
    public boolean canKick(User kicker, User toKick) {
        if (!this.hasPermission(kicker, KPermission.KICK_MEMBER)) return false;
        final Role kickerRole = this.getRole(kicker);
        final Role toKickRole = this.getRole(toKick);
        return kickerRole.isHigherRankedThan(toKickRole);
    }

    @Override
    public void kick(User user) {
        this.removeMember(user);
    }

//    @Override
//    @Nullable
//    public Relation getRelation(int kingdomId) {
//        if (kingdomId == this.id) return null;
//        return this.relations.get(this.getKingdomRelation(kingdomId));
//    }

    @Override
    public Map<Integer, RelationInfo> getKingdomRelations() {
        return this.kingdomRelations;
    }

//    @Override
//    public Map<RelationType, Relation> getRelations() {
//        return this.relations;
//    }

    @Override
    @Nullable
    public RelationType getKingdomRelation(int kingdomId) {
        if (kingdomId == this.id) return null;
        final RelationInfo info = this.kingdomRelations.get(kingdomId);
        if (info == null) return RelationType.NEUTRAL;
        return info.relationType();
    }

    @Override
    public void setRelation(Integer kingdomId, RelationInfo info) {
        if (kingdomId == this.id) return;
        this.kingdomRelations.put(kingdomId, info);
    }

    @Override
    public void removeRelation(Integer kingdomId) {
        if (kingdomId == this.id) return;
        this.kingdomRelations.remove(kingdomId);
    }

//    @Override
//    public void setRelation(RelationType type, Relation relation) {
//        this.relations.put(type, relation);
//    }

    @Override
    public Collection<RelationInfo> getRelations(RelationType type) {
        return this.kingdomRelations.values().stream().filter(info -> info.relationType() == type).collect(Collectors.toSet());
    }

    @Override
    public boolean isLeader(User user) {
        return this.getRole(user).equals(this.plugin.getRoleManager().getLeaderRole(this));
    }

    @Override
    public Bank<Kingdom> getBank() {
        return this.bank;
    }

    @Override
    public double getBankLimit() {
        final DoubleUpgrades bankLimitUpgrades = this.upgradeHolder.getUpgrades(UpgradeId.BANK_LIMIT.toString(), DoubleUpgrades.class);
        if (bankLimitUpgrades == null) return 0;
        final Integer level = this.getUpgradeLevel(UpgradeId.BANK_LIMIT.toString());
        if (level == null) return 0;
        final Double limit = bankLimitUpgrades.getValueAtLevel(level);
        if (limit == null) return 0;
        return limit;
    }

    @Override
    public Map<String, Role> getRoles() {
        return this.roles;
    }

    @Override
    public Role getRole(String id) {
        return this.roles.get(id);
    }

    @Override
    public KingdomLocations getLocations() {
        return this.locations;
    }

    @Override
    public boolean isDirty() {
        return this.locations.isDirty() || this.permissions.isDirty() || this.dirty;
    }

    @Override
    public void setDirty(boolean dirty) {
        this.dirty = dirty;
    }

    @Override
    public boolean canBeUnloaded(Kingdoms plugin) {
        final WorldManager worldManager = plugin.getWorldManager();
        for (User user : this.getMembers()) {
            if (user.isOnline()) return false;
        }
        for (ClaimedChunk chunk : this.claims) {
            if (worldManager.isChunkLoaded(chunk.getChunk())) return false;
        }
        return true;
    }

    @Override
    public void setName(String name) {
        this.name = name;
        this.setDirty(true);
    }

    @Override
    public void setDescription(String description) {
        this.description = description;
        this.setDirty(true);
    }

    @Override
    public String toString() {
        return "KingdomImpl{" +
                "plugin=" + plugin +
                ", id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", members=" + members +
                ", userRoles=" + userRoles +
                ", roleUsers=" + roleUsers +
                ", permissions=" + permissions +
                ", claims=" + claims +
                ", upgradeHolder=" + upgradeHolder +
                ", upgradeLevels=" + upgradeLevels +
                ", kingdomRelations=" + kingdomRelations +
                ", bank=" + bank +
                ", roles=" + roles +
                ", locations=" + locations +
                ", dirty=" + dirty +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final KingdomImpl kingdom = (KingdomImpl) o;
        return getId() == kingdom.getId();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }
}
