/*
 *     Kingdoms Plugin
 *     Copyright (C) 2022  Fisher2911
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package io.github.fisher2911.kingdoms.kingdom;

import io.github.fisher2911.fisherlib.economy.Bank;
import io.github.fisher2911.fisherlib.economy.Price;
import io.github.fisher2911.fisherlib.upgrade.UpgradeHolder;
import io.github.fisher2911.fisherlib.upgrade.Upgrades;
import io.github.fisher2911.fisherlib.util.collections.DirtyMap;
import io.github.fisher2911.kingdoms.Kingdoms;
import io.github.fisher2911.kingdoms.economy.EconomyManager;
import io.github.fisher2911.kingdoms.kingdom.location.KingdomLocations;
import io.github.fisher2911.kingdoms.kingdom.permission.KPermission;
import io.github.fisher2911.kingdoms.kingdom.permission.PermissionContainer;
import io.github.fisher2911.kingdoms.kingdom.relation.RelationInfo;
import io.github.fisher2911.kingdoms.kingdom.relation.RelationType;
import io.github.fisher2911.kingdoms.kingdom.role.Role;
import io.github.fisher2911.kingdoms.user.User;
import org.jetbrains.annotations.Nullable;

import java.time.Instant;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.UUID;
import java.util.function.Consumer;

public class WildernessKingdom implements Kingdom {

    private final int id = Kingdom.WILDERNESS_ID;
    private final String name = "Wilderness";
    private final PermissionContainer empty = PermissionContainer.empty();
    private final Bank<Kingdom> bank = EconomyManager.createKingdomBank(0);
    private final Instant creationDate = Instant.MIN;
    private final DirtyMap<String, Role> roles = new DirtyMap<>(Collections.unmodifiableMap(new HashMap<>()));
    private final DirtyMap<UUID, Role> userRoles = new DirtyMap<>(Collections.unmodifiableMap(new HashMap<>()));
    private final DirtyMap<Integer, RelationInfo> kingdomRelations = new DirtyMap<>(Collections.unmodifiableMap(new HashMap<>()));


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
    public Collection<User> getUsers() {
        return Collections.emptyList();
    }

    @Override
    public int getMaxMembers() {
        return 0;
    }

    @Override
    public DirtyMap<UUID, Role> getUserRoles() {
        return this.userRoles;
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
    public DirtyMap<Integer, RelationInfo> getKingdomRelations() {
        return this.kingdomRelations;
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
    public DirtyMap<String, Role> getRoles() {
        return this.roles;
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
    public UpgradeHolder<Kingdom, User> getUpgradeHolder() {
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
    public DirtyMap<String, Integer> getUpgradeLevels() {
        return null;
    }

    @Override
    public void tryLevelUpUpgrade(User user, Upgrades<?> upgrades) {

    }

    @Override
    public Instant getCreatedAt() {
        return this.creationDate;
    }
}
