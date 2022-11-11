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

import io.github.fisher2911.kingdoms.Kingdoms;
import io.github.fisher2911.kingdoms.data.Saveable;
import io.github.fisher2911.kingdoms.economy.Bank;
import io.github.fisher2911.kingdoms.kingdom.location.KingdomLocations;
import io.github.fisher2911.kingdoms.kingdom.permission.KPermissible;
import io.github.fisher2911.kingdoms.kingdom.permission.KPermission;
import io.github.fisher2911.kingdoms.kingdom.permission.KPermissionHolder;
import io.github.fisher2911.kingdoms.kingdom.permission.PermissionContainer;
import io.github.fisher2911.kingdoms.kingdom.relation.RelationInfo;
import io.github.fisher2911.kingdoms.kingdom.relation.RelationType;
import io.github.fisher2911.kingdoms.kingdom.role.Role;
import io.github.fisher2911.kingdoms.kingdom.upgrade.Upgradeable;
import io.github.fisher2911.kingdoms.user.User;
import io.github.fisher2911.kingdoms.util.collections.DirtyMap;
import org.jetbrains.annotations.Nullable;

import java.time.Instant;
import java.util.Collection;
import java.util.UUID;
import java.util.function.Consumer;

public interface Kingdom extends KPermissible, Upgradeable, KPermissionHolder, Saveable {

    int WILDERNESS_ID = -1;

    int getId();
    String getName();
    void setName(String name);
    String getDescription();
    void setDescription(String description);
    Collection<User> getMembers();
    int getMaxMembers();
    DirtyMap<UUID, Role> getUserRoles();
    void consumeRoles(Consumer<User> consumer, Role... roles);
    PermissionContainer getPermissions();
    PermissionContainer getDefaultChunkPermissions();
    boolean hasPermission(User user, KPermission permission);
    boolean hasPermission(User user, KPermission permission, ClaimedChunk chunk);
    boolean hasPermission(Role role, KPermission permission);
    boolean hasPermission(Role role, KPermission permission, ClaimedChunk chunk);
    Collection<ClaimedChunk> getClaimedChunks();
    void addClaimedChunk(ClaimedChunk chunk);
    void removeClaimedChunk(ClaimedChunk chunk);
    void setRole(User user, Role role);
    void addMember(User user);
    void removeMember(User member);
    Role getRole(User user);
    boolean isFull();
    int getAvailableChunks();
    int getTotalPossibleChunks();
    boolean canKick(User kicker, User toKick);
    void kick(User user);
    DirtyMap<Integer, RelationInfo> getKingdomRelations();
    Collection<RelationInfo> getRelations(RelationType type);
    @Nullable // if self
    RelationType getKingdomRelation(int kingdomId);
    void setRelation(Integer kingdomId, RelationInfo info);
    void removeRelation(Integer kingdomId);
    boolean isLeader(User user);
    Bank<Kingdom> getBank();
    double getBankLimit();
    DirtyMap<String, Role> getRoles();
    Role getRole(String id);
    KingdomLocations getLocations();
    boolean canBeUnloaded(Kingdoms plugin);
    Instant getCreatedAt();

}
