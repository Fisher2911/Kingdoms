package io.github.fisher2911.kingdoms.kingdom;

import io.github.fisher2911.kingdoms.economy.Bank;
import io.github.fisher2911.kingdoms.kingdom.location.KingdomLocations;
import io.github.fisher2911.kingdoms.kingdom.permission.KPermissible;
import io.github.fisher2911.kingdoms.kingdom.permission.KPermission;
import io.github.fisher2911.kingdoms.kingdom.permission.KPermissionHolder;
import io.github.fisher2911.kingdoms.kingdom.permission.PermissionContainer;
import io.github.fisher2911.kingdoms.kingdom.relation.Relation;
import io.github.fisher2911.kingdoms.kingdom.relation.RelationInfo;
import io.github.fisher2911.kingdoms.kingdom.relation.RelationType;
import io.github.fisher2911.kingdoms.kingdom.role.Role;
import io.github.fisher2911.kingdoms.kingdom.upgrade.Upgradeable;
import io.github.fisher2911.kingdoms.user.User;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

public interface Kingdom extends KPermissible, Upgradeable, KPermissionHolder {

    int WILDERNESS_ID = -1;

    int getId();
    String getName();
    String getDescription();
    Collection<User> getMembers();
    int getMaxMembers();
    Map<UUID, Role> getUserRoles();
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
    Map<Integer, RelationInfo> getKingdomRelations();
    Map<RelationType, Relation> getRelations();
    Collection<RelationInfo> getRelations(RelationType type);
    @Nullable // if self
    RelationType getKingdomRelation(int kingdomId);
    @Nullable // if self
    Relation getRelation(int kingdomId);
    void setRelation(Integer kingdomId, RelationInfo info);
    void removeRelation(Integer kingdomId);
    void setRelation(RelationType type, Relation relation);
    boolean isLeader(User user);
    Bank<Kingdom> getBank();
    double getBankLimit();
    Map<String, Role> getRoles();
    Role getRole(String id);
    KingdomLocations getLocations();

}
