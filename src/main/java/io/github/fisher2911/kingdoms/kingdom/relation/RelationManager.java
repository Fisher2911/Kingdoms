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

package io.github.fisher2911.kingdoms.kingdom.relation;

import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import io.github.fisher2911.fisherlib.message.MessageHandler;
import io.github.fisher2911.fisherlib.upgrade.IntUpgrades;
import io.github.fisher2911.kingdoms.Kingdoms;
import io.github.fisher2911.kingdoms.kingdom.Kingdom;
import io.github.fisher2911.kingdoms.kingdom.KingdomManager;
import io.github.fisher2911.kingdoms.kingdom.permission.KPermission;
import io.github.fisher2911.kingdoms.kingdom.role.RoleManager;
import io.github.fisher2911.kingdoms.kingdom.upgrade.UpgradeId;
import io.github.fisher2911.kingdoms.message.KMessage;
import io.github.fisher2911.kingdoms.user.User;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.Nullable;

import java.time.Instant;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class RelationManager {

    private final Kingdoms plugin;
    private final MessageHandler messageHandler;
    private final KingdomManager kingdomManager;
    private final Map<RelationType, Map<KPermission, Boolean>> defaultRelationPermissions = new EnumMap<>(RelationType.class);
    private Map<String, RelationType> roleRelationMap;
    private final Multimap<Integer, RelationInvite> invitedRelations;

    public RelationManager(Kingdoms plugin) {
        this.plugin = plugin;
        this.messageHandler = plugin.getMessageHandler();
        this.kingdomManager = this.plugin.getKingdomManager();
        this.invitedRelations = Multimaps.newSetMultimap(new HashMap<>(), HashSet::new);
    }

    public void tryAddRelation(User user, String toRelate, RelationType type, boolean searchDatabase) {
        this.kingdomManager.getKingdomByName(toRelate, searchDatabase).ifPresentOrElse(kingdom ->
                        this.tryAddRelation(user, kingdom, type, searchDatabase),
                () -> this.messageHandler.sendMessage(user, KMessage.KINGDOM_NOT_FOUND)
        );
    }

    public void tryAddRelation(User user, Kingdom toRelate, RelationType type, boolean searchDatabase) {
        this.kingdomManager.getKingdom(user.getKingdomId(), searchDatabase).
                ifPresentOrElse(kingdom -> this.tryAddRelation(kingdom, user, toRelate, type),
                        () -> this.messageHandler.sendMessage(user, KMessage.NOT_IN_KINGDOM)
                );
    }

    public void tryAddRelation(Kingdom kingdom, User user, Kingdom toRelate, RelationType type) {
        if (kingdom.getId() == toRelate.getId()) {
            this.messageHandler.sendMessage(user, KMessage.CANNOT_RELATE_TO_SELF_KINGDOM);
            return;
        }
        final RelationType currentRelation = kingdom.getKingdomRelation(toRelate.getId());
        final boolean addingRelation = type != currentRelation;

        if (this.getMaxRelations(kingdom, type, 0) <= kingdom.getRelations(type).size()) {
            this.messageHandler.sendMessage(user, KMessage.MAX_RELATIONS_REACHED, type);
            return;
        }

        final KPermission permission = addingRelation ? type.getAddPermission() : type.getRemovePermission();
        if (!kingdom.hasPermission(user, permission)) {
            this.messageHandler.sendMessage(user, KMessage.NO_KINGDOM_PERMISSION);
            return;
        }
        final boolean canBeOneWay = type.isLowerThan(currentRelation) || type.canBeOneWay();
        if (addingRelation) {
            if (type == RelationType.ENEMY) {
                this.addRelation(kingdom, toRelate, type, canBeOneWay);
                return;
            }
            for (RelationInvite invite : this.invitedRelations.get(toRelate.getId())) {
                if (invite.invited().getId() != kingdom.getId()) continue;
                this.addRelation(kingdom, toRelate, type, canBeOneWay);
                this.invitedRelations.remove(toRelate.getId(), invite);
                return;
            }
            final RelationInvite invite = new RelationInvite(
                    kingdom,
                    user,
                    toRelate,
                    Instant.now()
            );
            this.invitedRelations.put(kingdom.getId(), invite);
            this.messageHandler.sendMessage(kingdom, KMessage.SENT_RELATION_REQUEST, toRelate, type);
            this.messageHandler.sendMessage(toRelate, KMessage.RECEIVED_RELATION_REQUEST, kingdom, type);
            Bukkit.getScheduler().runTaskLater(this.plugin, () -> {
                if (!this.invitedRelations.get(kingdom.getId()).contains(invite)) return;
                this.messageHandler.sendMessage(kingdom, KMessage.SENT_RELATION_REQUEST_EXPIRED, toRelate, type);
                this.messageHandler.sendMessage(toRelate, KMessage.RECEIVED_RELATION_REQUEST_EXPIRED, kingdom, type);
                this.invitedRelations.remove(kingdom.getId(), invite);
            }, 20 * 60);
            return;
        }
        this.removeRelation(kingdom, toRelate, type, canBeOneWay);
    }

    private void addRelation(Kingdom kingdom, Kingdom toRelate, RelationType type, boolean oneWay) {
        if (!oneWay) {
            toRelate.setRelation(kingdom.getId(), new RelationInfo(kingdom.getId(), kingdom.getName(), type));
        }
        kingdom.setRelation(toRelate.getId(), new RelationInfo(toRelate.getId(), toRelate.getName(), type));
        this.messageHandler.sendMessage(kingdom, KMessage.ACCEPTED_RELATION_REQUEST, toRelate, type);
        this.messageHandler.sendMessage(toRelate, KMessage.RELATION_REQUEST_ACCEPTED, kingdom, type);
    }

    private void removeRelation(Kingdom kingdom, Kingdom toRelate, RelationType type, boolean oneWay) {
        if (!oneWay) {
            toRelate.removeRelation(kingdom.getId());
        }
        kingdom.removeRelation(toRelate.getId());
        this.messageHandler.sendMessage(kingdom, KMessage.REMOVED_RELATION_TO, toRelate, type);
        this.messageHandler.sendMessage(toRelate, KMessage.RELATION_REMOVED_FROM, kingdom, type);
    }

    public void removeRelation(Kingdom kingdom, Kingdom toRelate) {
        final RelationType type = kingdom.getKingdomRelation(toRelate.getId());
        if (type == RelationType.NEUTRAL) return;
        kingdom.removeRelation(toRelate.getId());
        this.messageHandler.sendMessage(kingdom, KMessage.REMOVED_RELATION_TO, toRelate, type);
    }

    public int getMaxRelations(Kingdom kingdom, RelationType type, int def) {
        final String upgradeId = switch (type) {
            case ALLY -> UpgradeId.MAX_ALLIES.toString();
            case TRUCE -> UpgradeId.MAX_TRUCES.toString();
            case ENEMY -> UpgradeId.MAX_ENEMIES.toString();
            default -> null;
        };
        if (upgradeId == null) return def;
        final Integer value = kingdom.getUpgradesValue(upgradeId, IntUpgrades.class);
        if (value == null) return def;
        return value;
    }

    @Nullable
    public RelationType fromRole(String roleId) {
        return this.roleRelationMap.get(roleId);
    }

    public void load() {
        final RoleManager roleManager = this.plugin.getRoleManager();
        final String enemyRoleId = roleManager.getEnemyRoleId();
        final String neutralRoleId = roleManager.getNeutralRoleId();
        final String truceRoleId = roleManager.getTruceRoleId();
        final String allyRoleId = roleManager.getAllyRoleId();
        this.roleRelationMap = Map.of(
                enemyRoleId, RelationType.ENEMY,
                neutralRoleId, RelationType.NEUTRAL,
                truceRoleId, RelationType.TRUCE,
                allyRoleId, RelationType.ALLY
        );

        this.defaultRelationPermissions.put(this.fromRole(allyRoleId), roleManager.getDefaultRolePermissions().getPermissions().get(allyRoleId));
        this.defaultRelationPermissions.put(this.fromRole(truceRoleId), roleManager.getDefaultRolePermissions().getPermissions().get(truceRoleId));
        this.defaultRelationPermissions.put(this.fromRole(enemyRoleId), roleManager.getDefaultRolePermissions().getPermissions().get(enemyRoleId));
        this.defaultRelationPermissions.put(this.fromRole(neutralRoleId), roleManager.getDefaultRolePermissions().getPermissions().get(neutralRoleId));

    }
}
