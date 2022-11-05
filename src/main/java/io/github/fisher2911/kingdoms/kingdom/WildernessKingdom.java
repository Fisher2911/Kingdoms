package io.github.fisher2911.kingdoms.kingdom;

import io.github.fisher2911.kingdoms.Kingdoms;
import io.github.fisher2911.kingdoms.economy.Bank;
import io.github.fisher2911.kingdoms.economy.Price;
import io.github.fisher2911.kingdoms.kingdom.location.KingdomLocations;
import io.github.fisher2911.kingdoms.kingdom.permission.KPermission;
import io.github.fisher2911.kingdoms.kingdom.permission.PermissionContainer;
import io.github.fisher2911.kingdoms.kingdom.relation.RelationInfo;
import io.github.fisher2911.kingdoms.kingdom.relation.RelationType;
import io.github.fisher2911.kingdoms.kingdom.role.Role;
import io.github.fisher2911.kingdoms.kingdom.upgrade.UpgradeHolder;
import io.github.fisher2911.kingdoms.kingdom.upgrade.Upgrades;
import io.github.fisher2911.kingdoms.user.User;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

public class WildernessKingdom implements Kingdom {

    private final int id = Kingdom.WILDERNESS_ID;
    private final String name = "Wilderness";
    private final PermissionContainer empty = PermissionContainer.empty();
    private final Bank<Kingdom> bank = Bank.createKingdomBank(0);

    private WildernessKingdom() {}

    public static final WildernessKingdom INSTANCE = new WildernessKingdom();

    @Override
    public boolean isDirty() {
        return false;
    }

    @Override
    public void setDirty(boolean dirty) {

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
    public void setName(String name) {

    }

    @Override
    public String getDescription() {
        return "";
    }

    @Override
    public void setDescription(String description) {

    }

    @Override
    public Collection<User> getMembers() {
        return Collections.emptyList();
    }

    @Override
    public int getMaxMembers() {
        return 0;
    }

    @Override
    public Map<UUID, Role> getUserRoles() {
        return Collections.emptyMap();
    }

    @Override
    public void consumeRoles(Consumer<User> consumer, Role... roles) {

    }

    @Override
    public PermissionContainer getPermissions() {
        return this.empty;
    }

    @Override
    public PermissionContainer getDefaultChunkPermissions() {
        return this.empty;
    }

    @Override
    public boolean hasPermission(User user, KPermission permission) {
        return false;
    }

    @Override
    public boolean hasPermission(User user, KPermission permission, ClaimedChunk chunk) {
        return false;
    }

    @Override
    public boolean hasPermission(Role role, KPermission permission) {
        return false;
    }

    @Override
    public boolean hasPermission(Role role, KPermission permission, ClaimedChunk chunk) {
        return false;
    }

    @Override
    public Collection<ClaimedChunk> getClaimedChunks() {
        return Collections.emptyList();
    }

    @Override
    public void addClaimedChunk(ClaimedChunk chunk) {

    }

    @Override
    public void removeClaimedChunk(ClaimedChunk chunk) {

    }

    @Override
    public void setRole(User user, Role role) {

    }

    @Override
    public void addMember(User user) {

    }

    @Override
    public void removeMember(User member) {

    }

    @Override
    public Role getRole(User user) {
        return null;
    }

    @Override
    public boolean isFull() {
        return true;
    }

    @Override
    public int getAvailableChunks() {
        return 0;
    }

    @Override
    public int getTotalPossibleChunks() {
        return 0;
    }

    @Override
    public boolean canKick(User kicker, User toKick) {
        return false;
    }

    @Override
    public void kick(User user) {

    }

    @Override
    public Map<Integer, RelationInfo> getKingdomRelations() {
        return Collections.emptyMap();
    }

    @Override
    public Collection<RelationInfo> getRelations(RelationType type) {
        return Collections.emptyList();
    }

    @Override
    public @Nullable RelationType getKingdomRelation(int kingdomId) {
        return RelationType.NEUTRAL;
    }

    @Override
    public void setRelation(Integer kingdomId, RelationInfo info) {

    }

    @Override
    public void removeRelation(Integer kingdomId) {

    }

    @Override
    public boolean isLeader(User user) {
        return false;
    }

    @Override
    public Bank<Kingdom> getBank() {
        return this.bank;
    }

    @Override
    public double getBankLimit() {
        return 0;
    }

    @Override
    public Map<String, Role> getRoles() {
        return Collections.emptyMap();
    }

    @Override
    public Role getRole(String id) {
        return null;
    }

    @Override
    public KingdomLocations getLocations() {
        return null;
    }

    @Override
    public boolean canBeUnloaded(Kingdoms plugin) {
        return false;
    }

    @Override
    public void setPermission(User user, KPermission permission, boolean value) {

    }

    @Override
    public void setPermission(Role role, KPermission permission, boolean value) {

    }

    @Override
    public UpgradeHolder getUpgradeHolder() {
        return null;
    }

    @Override
    public void setUpgradeLevel(String id, int level) {

    }

    @Override
    public @Nullable Integer getUpgradeLevel(String id) {
        return null;
    }

    @Override
    public Price getUpgradePrice(String id) {
        return null;
    }

    @Override
    public <T> T getUpgradesValue(String id, Class<? extends Upgrades<T>> clazz) {
        return null;
    }

    @Override
    public Map<String, Integer> getUpgradeLevels() {
        return null;
    }
}
