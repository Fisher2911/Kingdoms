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

import io.github.fisher2911.fisherlib.economy.Price;
import io.github.fisher2911.fisherlib.economy.PriceManager;
import io.github.fisher2911.fisherlib.message.MessageHandler;
import io.github.fisher2911.fisherlib.upgrade.Upgrades;
import io.github.fisher2911.fisherlib.world.WorldPosition;
import io.github.fisher2911.kingdoms.Kingdoms;
import io.github.fisher2911.kingdoms.api.event.kingdom.KingdomAttemptCreateEvent;
import io.github.fisher2911.kingdoms.api.event.kingdom.KingdomCreateEvent;
import io.github.fisher2911.kingdoms.api.event.kingdom.KingdomDisbandEvent;
import io.github.fisher2911.kingdoms.api.event.kingdom.KingdomJoinEvent;
import io.github.fisher2911.kingdoms.api.event.kingdom.KingdomLoadEvent;
import io.github.fisher2911.kingdoms.api.event.kingdom.KingdomMemberKickEvent;
import io.github.fisher2911.kingdoms.api.event.kingdom.KingdomMemberLeaveEvent;
import io.github.fisher2911.kingdoms.api.event.kingdom.KingdomMemberStartTeleportEvent;
import io.github.fisher2911.kingdoms.api.event.kingdom.KingdomSetDescriptionEvent;
import io.github.fisher2911.kingdoms.api.event.kingdom.KingdomSetHomeEvent;
import io.github.fisher2911.kingdoms.api.event.kingdom.KingdomSetMemberRoleEvent;
import io.github.fisher2911.kingdoms.api.event.kingdom.KingdomSetNameEvent;
import io.github.fisher2911.kingdoms.api.event.kingdom.KingdomUnloadEvent;
import io.github.fisher2911.kingdoms.api.event.kingdom.KingdomUpgradeEvent;
import io.github.fisher2911.kingdoms.command.CommandPermission;
import io.github.fisher2911.kingdoms.config.KingdomsSettings;
import io.github.fisher2911.kingdoms.confirm.Confirmation;
import io.github.fisher2911.kingdoms.confirm.ConfirmationManager;
import io.github.fisher2911.kingdoms.data.DataManager;
import io.github.fisher2911.kingdoms.economy.PriceType;
import io.github.fisher2911.kingdoms.kingdom.location.KingdomLocations;
import io.github.fisher2911.kingdoms.kingdom.permission.KPermission;
import io.github.fisher2911.kingdoms.kingdom.role.Role;
import io.github.fisher2911.kingdoms.kingdom.role.RoleManager;
import io.github.fisher2911.kingdoms.message.KMessage;
import io.github.fisher2911.kingdoms.placeholder.wrapper.UpgradesWrapper;
import io.github.fisher2911.kingdoms.teleport.TeleportInfo;
import io.github.fisher2911.kingdoms.user.User;
import org.bukkit.Bukkit;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class KingdomManager {

    private final Kingdoms plugin;
    private final MessageHandler messageHandler;
    private final KingdomsSettings settings;
    private final PriceManager priceManager;
    private final DataManager dataManager;
    private final Map<Integer, Kingdom> kingdoms;
    private final Map<String, Kingdom> byName;

    public KingdomManager(Kingdoms plugin, Map<Integer, Kingdom> kingdoms) {
        this.plugin = plugin;
        this.messageHandler = plugin.getMessageHandler();
        this.settings = this.plugin.getKingdomSettings();
        this.priceManager = this.plugin.getPriceManager();
        this.dataManager = this.plugin.getDataManager();
        this.byName = new HashMap<>();
        this.kingdoms = kingdoms;
    }

    public Optional<Kingdom> tryCreate(User user, String name) {
        final Optional<Kingdom> empty = Optional.empty();
        if (user.getKingdomId() != Kingdom.WILDERNESS_ID) {
            this.messageHandler.sendMessage(user, KMessage.ALREADY_IN_KINGDOM);
            return empty;
        }
        if (!user.hasPermission(CommandPermission.CREATE_KINGDOM.getValue())) {
            this.messageHandler.sendMessage(user, KMessage.NO_PERMISSION_TO_CREATE_KINGDOM);
            return empty;
        }
        if (!this.settings.isProperNameLength(name)) {
            this.messageHandler.sendMessage(user, KMessage.INVALID_KINGDOM_NAME_LENGTH);
            return empty;
        }
        final Optional<Kingdom> kingdomByName = this.getKingdomByName(name, true);
        if (kingdomByName.isPresent()) {
            this.messageHandler.sendMessage(user, KMessage.KINGDOM_ALREADY_EXISTS, kingdomByName.get());
            return empty;
        }
        if (!this.priceManager.getPrice(PriceType.KINGDOM_CREATION, Price.FREE).payIfCanAfford(user)) {
            this.messageHandler.sendMessage(user, KMessage.CANNOT_AFFORD_TO_CREATE_KINGDOM);
            return empty;
        }
        name = MessageHandler.removeAllTags(name);
        final KingdomAttemptCreateEvent attemptCreateEvent = new KingdomAttemptCreateEvent(user, name);
        Bukkit.getPluginManager().callEvent(attemptCreateEvent);
        if (attemptCreateEvent.isCancelled()) return empty;
        final Kingdom kingdom = this.dataManager.newKingdom(user, attemptCreateEvent.getName());
        this.messageHandler.sendMessage(user, KMessage.CREATED_KINGDOM, kingdom);
        this.kingdoms.put(kingdom.getId(), kingdom);
        this.byName.put(kingdom.getName(), kingdom);
        Bukkit.getPluginManager().callEvent(new KingdomCreateEvent(kingdom, user));
        return Optional.of(kingdom);
    }

    public Optional<Kingdom> join(User user, int kingdomId, boolean searchDatabase) {
        return this.getKingdom(kingdomId, searchDatabase).flatMap(kingdom -> this.join(user, kingdom));
    }

    public Optional<Kingdom> join(User user, Kingdom kingdom) {
        final Optional<Kingdom> empty = Optional.empty();
        if (user.getKingdomId() != Kingdom.WILDERNESS_ID) {
            this.messageHandler.sendMessage(user, KMessage.ALREADY_IN_KINGDOM);
            return empty;
        }
        if (kingdom.isFull()) {
            this.messageHandler.sendMessage(user, KMessage.OTHER_KINGDOM_FULL, kingdom);
            return empty;
        }
        final KingdomJoinEvent kingdomJoinEvent = new KingdomJoinEvent(kingdom, user);
        Bukkit.getPluginManager().callEvent(kingdomJoinEvent);
        if (kingdomJoinEvent.isCancelled()) return empty;
        kingdom.addMember(user);
        this.messageHandler.sendMessage(user, KMessage.JOINED_KINGDOM, kingdom);
        return Optional.of(kingdom);
    }

    public void tryLevelUpUpgrade(Kingdom kingdom, User user, Upgrades<?> upgrades) {
        if (!kingdom.hasPermission(user, KPermission.UPGRADE_KINGDOM)) {
            this.messageHandler.sendMessage(user, KMessage.NO_KINGDOM_PERMISSION);
            return;
        }
        final String upgradesId = upgrades.getId();
        final Integer upgradeLevel = kingdom.getUpgradeLevel(upgradesId);
        if (upgradeLevel == null) {
            this.messageHandler.sendMessage(user, KMessage.UPGRADE_DOES_NOT_EXIST);
            return;
        }
        final UpgradesWrapper wrapper = new UpgradesWrapper(upgrades, upgradeLevel);
        if (upgrades.getMaxLevel() <= upgradeLevel) {
            this.messageHandler.sendMessage(user, KMessage.ALREADY_MAX_UPGRADE_LEVEL, wrapper);
            return;
        }
        final Price price = kingdom.getUpgradePrice(upgradesId);
        if (!price.payIfCanAfford(user)) {
            this.messageHandler.sendMessage(user, KMessage.CANNOT_AFFORD_TO_UPGRADE, wrapper);
            return;
        }
        final KingdomUpgradeEvent kingdomUpgradeEvent = new KingdomUpgradeEvent(kingdom, user, upgrades, upgradeLevel + 1);
        Bukkit.getPluginManager().callEvent(kingdomUpgradeEvent);
        if (kingdomUpgradeEvent.isCancelled()) return;
        kingdom.setUpgradeLevel(upgradesId, kingdomUpgradeEvent.getNewLevel());
        this.messageHandler.sendMessage(user, KMessage.LEVEL_UP_UPGRADE_SUCCESSFUL, new UpgradesWrapper(upgrades, upgradeLevel + 1));
    }

    public Optional<Kingdom> getKingdom(int id, boolean searchDatabase) {
        if (id == Kingdom.WILDERNESS_ID) return Optional.empty();
        final Kingdom kingdom = this.kingdoms.get(id);
        if (kingdom != null || !searchDatabase) return Optional.ofNullable(kingdom);
        return this.loadKingdom(id);
    }

    public Optional<Kingdom> loadKingdom(int id) {
        final Optional<Kingdom> kingdom = this.dataManager.loadKingdom(id);
        kingdom.ifPresent(k -> {
            this.kingdoms.put(k.getId(), k);
            this.byName.put(k.getName(), k);
            Bukkit.getPluginManager().callEvent(new KingdomLoadEvent(k));
        });
        return kingdom;
    }

    public Optional<Kingdom> getKingdomByName(String name, boolean searchDatabase) {
        final Kingdom kingdom = this.byName.get(name);
        if (kingdom != null || !searchDatabase) return Optional.ofNullable(kingdom);
        return this.dataManager.loadKingdomByName(name);
    }

    public void sendKingdomInfo(User user, Kingdom kingdom) {
        if ((kingdom.getId() != user.getKingdomId() && !user.hasPermission(CommandPermission.VIEW_OTHER_KINGDOM_INFO.getValue())) ||
                (kingdom.getId() == user.getKingdomId() && !user.hasPermission(CommandPermission.VIEW_SELF_KINGDOM_INFO.getValue()))
        ) {
            this.messageHandler.sendMessage(user, KMessage.NO_COMMAND_PERMISSION);
            return;
        }
        this.messageHandler.sendMessage(user, KMessage.KINGDOM_INFO, kingdom);
    }

    public void sendKingdomInfo(User user, boolean searchDatabase) {
        this.getKingdom(user.getKingdomId(), searchDatabase).ifPresentOrElse(kingdom -> {
            if ((kingdom.getId() != user.getKingdomId() && !user.hasPermission(CommandPermission.VIEW_OTHER_KINGDOM_INFO.getValue())) ||
                    (kingdom.getId() == user.getKingdomId() && !user.hasPermission(CommandPermission.VIEW_SELF_KINGDOM_INFO.getValue()))
            ) {
                this.messageHandler.sendMessage(user, KMessage.NO_COMMAND_PERMISSION);
                return;
            }
            this.messageHandler.sendMessage(user, KMessage.KINGDOM_INFO, kingdom);
        }, () -> this.messageHandler.sendMessage(user, KMessage.NOT_IN_KINGDOM));
    }

    public void sendKingdomDescription(User user, Kingdom kingdom) {
        if ((kingdom.getId() != user.getKingdomId() && !user.hasPermission(CommandPermission.VIEW_OTHER_KINGDOM_DESCRIPTION.getValue())) ||
                (kingdom.getId() == user.getKingdomId() && !user.hasPermission(CommandPermission.VIEW_SELF_KINGDOM_DESCRIPTION.getValue()))
        ) {
            this.messageHandler.sendMessage(user, KMessage.NO_COMMAND_PERMISSION);
            return;
        }
        this.messageHandler.sendMessage(user, KMessage.KINGDOM_DESCRIPTION, kingdom);
    }

    public void sendKingdomDescription(User user, boolean searchDatabase) {
        this.getKingdom(user.getKingdomId(), searchDatabase).ifPresentOrElse(kingdom -> {
            this.sendKingdomDescription(user, kingdom);
        }, () -> this.messageHandler.sendMessage(user, KMessage.NOT_IN_KINGDOM));
    }

    public void trySetDescription(User user, String description, boolean searchDatabase) {
        if (!this.settings.isProperDescriptionLength(description)) {
            this.messageHandler.sendMessage(user, KMessage.INVALID_KINGDOM_DESCRIPTION_LENGTH);
            return;
        }
        this.getKingdom(user.getKingdomId(), searchDatabase).ifPresentOrElse(kingdom -> {
            this.trySetDescription(user, description, kingdom);
        }, () -> this.messageHandler.sendMessage(user, KMessage.NOT_IN_KINGDOM));
    }

    public void trySetDescription(User user, String description, Kingdom kingdom) {
        if (!this.settings.isProperDescriptionLength(description)) {
            this.messageHandler.sendMessage(user, KMessage.INVALID_KINGDOM_DESCRIPTION_LENGTH);
            return;
        }
        if (!kingdom.hasPermission(user, KPermission.SET_KINGDOM_DESCRIPTION)) {
            this.messageHandler.sendMessage(user, KMessage.NO_KINGDOM_PERMISSION);
            return;
        }
        description = MessageHandler.removeUnsafeTags(description);
        final KingdomSetDescriptionEvent kingdomSetDescriptionEvent = new KingdomSetDescriptionEvent(kingdom, user, description);
        Bukkit.getPluginManager().callEvent(kingdomSetDescriptionEvent);
        if (kingdomSetDescriptionEvent.isCancelled()) return;
        kingdom.setDescription(kingdomSetDescriptionEvent.getDescription());
        this.messageHandler.sendMessage(user, KMessage.CHANGED_KINGDOM_DESCRIPTION, kingdom);
    }

    public void trySetName(User user, String name) {
        if (!this.settings.isProperNameLength(name)) {
            this.messageHandler.sendMessage(user, KMessage.INVALID_KINGDOM_NAME_LENGTH);
            return;
        }
        this.getKingdom(user.getKingdomId(), true).ifPresentOrElse(kingdom -> {
            this.trySetName(user, name, kingdom);
        }, () -> this.messageHandler.sendMessage(user, KMessage.NOT_IN_KINGDOM));
    }

    public void trySetName(User user, String name, Kingdom kingdom) {
        if (!this.settings.isProperNameLength(name)) {
            this.messageHandler.sendMessage(user, KMessage.INVALID_KINGDOM_NAME_LENGTH);
            return;
        }
        if (!kingdom.hasPermission(user, KPermission.SET_KINGDOM_NAME)) {
            this.messageHandler.sendMessage(user, KMessage.NO_KINGDOM_PERMISSION);
            return;
        }
        final Optional<Kingdom> otherKingdom = this.getKingdomByName(name, true);
        if (otherKingdom.isPresent()) {
            this.messageHandler.sendMessage(user, KMessage.KINGDOM_ALREADY_EXISTS, otherKingdom.get());
            return;
        }
        name = MessageHandler.removeAllTags(name);
        final KingdomSetNameEvent kingdomSetNameEvent = new KingdomSetNameEvent(kingdom, user, name);
        Bukkit.getPluginManager().callEvent(kingdomSetNameEvent);
        if (kingdomSetNameEvent.isCancelled()) return;
        kingdom.setName(kingdomSetNameEvent.getName());
        this.messageHandler.sendMessage(user, KMessage.CHANGED_KINGDOM_NAME, kingdom);
    }

    public void tryKick(User kicker, User toKick, boolean searchDatabase) {
        this.getKingdom(kicker.getKingdomId(), searchDatabase).ifPresentOrElse(kingdom -> {
            this.tryKick(kingdom, kicker, toKick);
        }, () -> this.messageHandler.sendMessage(kicker, KMessage.NOT_IN_KINGDOM));
    }

    public void tryKick(Kingdom kingdom, User kicker, User toKick) {
        if (!kingdom.canKick(kicker, toKick) || kicker.getId().equals(toKick.getId())) {
            this.messageHandler.sendMessage(kicker, KMessage.NO_KINGDOM_PERMISSION);
            return;
        }
        final KingdomMemberKickEvent kingdomKickEvent = new KingdomMemberKickEvent(kingdom, kicker, toKick);
        Bukkit.getPluginManager().callEvent(kingdomKickEvent);
        if (kingdomKickEvent.isCancelled()) return;
        kingdom.kick(toKick);
        this.messageHandler.sendMessage(kicker, KMessage.KICKED_OTHER, toKick);
        this.messageHandler.sendMessage(toKick, KMessage.KICKED_FROM_KINGDOM, kicker, kingdom);
    }

    public void trySetRole(User user, User toSet, String roleId, boolean searchDatabase) {
        this.getKingdom(user.getKingdomId(), searchDatabase).ifPresentOrElse(kingdom -> {
            this.trySetRole(kingdom, user, toSet, roleId);
        }, () -> this.messageHandler.sendMessage(user, KMessage.NOT_IN_KINGDOM));
    }

    public void trySetRole(Kingdom kingdom, User user, User toSet, String roleId) {
        if (toSet.getKingdomId() != user.getKingdomId()) {
            this.messageHandler.sendMessage(user, KMessage.NOT_IN_SAME_KINGDOM, toSet);
            return;
        }
        final Role role = this.plugin.getRoleManager().getRole(roleId, kingdom);
        if (role == null) {
            this.messageHandler.sendMessage(user, KMessage.ROLE_DOES_NOT_EXIST);
            return;
        }
        if (RoleManager.UNSETTABLE_ROLES.contains(roleId)) {
            this.messageHandler.sendMessage(user, KMessage.CANNOT_SET_PERMISSION_ROLE, role);
            return;
        }
        if (user.getId().equals(toSet.getId())) {
            this.messageHandler.sendMessage(user, KMessage.CANNOT_SET_SELF_ROLE);
            return;
        }
        final Role setterRole = kingdom.getRole(user);
        final Role previousRole = kingdom.getRole(toSet);
        if (!kingdom.hasPermission(user, KPermission.SET_MEMBER_ROLE) || previousRole.isHigherRankedThan(setterRole)) {
            this.messageHandler.sendMessage(user, KMessage.NO_KINGDOM_PERMISSION);
            return;
        }
        final KingdomSetMemberRoleEvent kingdomMemberSetRoleEvent = new KingdomSetMemberRoleEvent(kingdom, user, toSet, role);
        Bukkit.getPluginManager().callEvent(kingdomMemberSetRoleEvent);
        if (kingdomMemberSetRoleEvent.isCancelled()) return;
        kingdom.setRole(toSet, kingdomMemberSetRoleEvent.getRole());
        this.messageHandler.sendMessage(user, KMessage.SET_OTHER_ROLE, toSet, role);
        this.messageHandler.sendMessage(toSet, KMessage.OWN_ROLE_SET, user, role);
    }

    public void tryLeave(User user, boolean searchDatabase) {
        this.getKingdom(user.getKingdomId(), searchDatabase).
                ifPresentOrElse(kingdom -> {
                    final Role role = kingdom.getRole(user);
                    if (role.equals(this.plugin.getRoleManager().getLeaderRole(kingdom))) {
                        this.messageHandler.sendMessage(user, KMessage.LEADER_CANNOT_LEAVE_KINGDOM);
                        return;
                    }
                    final KingdomMemberLeaveEvent kingdomMemberLeaveEvent = new KingdomMemberLeaveEvent(kingdom, user);
                    Bukkit.getPluginManager().callEvent(kingdomMemberLeaveEvent);
                    if (kingdomMemberLeaveEvent.isCancelled()) return;
                    kingdom.removeMember(user);
                    this.messageHandler.sendMessage(kingdom, KMessage.MEMBER_LEFT_KINGDOM, user);
                    this.messageHandler.sendMessage(user, KMessage.YOU_LEFT_KINGDOM, kingdom);
                }, () -> this.messageHandler.sendMessage(user, KMessage.NOT_IN_KINGDOM));
    }

    public void tryDisband(User user, boolean searchDatabase) {
        this.tryDisband(user, false, searchDatabase);
    }

    public void tryDisband(User user, boolean force, boolean searchDatabase) {
        this.getKingdom(user.getKingdomId(), searchDatabase).
                ifPresentOrElse(kingdom -> {
                    if (!kingdom.isLeader(user)) {
                        this.messageHandler.sendMessage(user, KMessage.NO_KINGDOM_PERMISSION);
                        return;
                    }
                    final ConfirmationManager confirmationManager = this.plugin.getConfirmationManager();
                    if (!confirmationManager.hasConfirmation(Confirmation.DISBAND_KINGDOM, user.getId(), true) && !force) {
                        this.messageHandler.sendMessage(user, KMessage.CONFIRM_DISBAND_KINGDOM, kingdom);
                        confirmationManager.addConfirmation(
                                Confirmation.DISBAND_KINGDOM,
                                user.getId(),
                                20 * 10,
                                () -> this.messageHandler.sendMessage(user, KMessage.DISBAND_KINGDOM_CONFIRMATION_EXPIRED)
                        );
                        return;
                    }
                    final KingdomDisbandEvent kingdomDisbandEvent = new KingdomDisbandEvent(kingdom, user);
                    Bukkit.getPluginManager().callEvent(kingdomDisbandEvent);
                    if (kingdomDisbandEvent.isCancelled()) return;
                    this.disband(user, kingdom, searchDatabase);
                }, () -> this.messageHandler.sendMessage(user, KMessage.NOT_IN_KINGDOM));
    }

    private void disband(User user, Kingdom kingdom, boolean searchDatabase) {
        this.messageHandler.sendMessage(kingdom, KMessage.KINGDOM_DISBANDED, kingdom, user);
        kingdom.getKingdomRelations().keySet().forEach(id ->
                this.getKingdom(id, searchDatabase).ifPresent(k -> this.plugin.getRelationManager().removeRelation(k, kingdom))
        );
        kingdom.getUsers().forEach(member -> member.setKingdomId(Kingdom.WILDERNESS_ID));
        this.kingdoms.remove(kingdom.getId());
        this.byName.remove(kingdom.getName());
        this.dataManager.deleteKingdom(kingdom.getId());
        final WorldManager worldManager = this.plugin.getWorldManager();
        for (ClaimedChunk chunk : kingdom.getClaimedChunks()) {
            worldManager.remove(chunk);
        }
    }

    public void trySetHome(User user, WorldPosition worldPosition, boolean searchDatabase) {
        if (!user.isOnline()) return;
        this.getKingdom(user.getKingdomId(), searchDatabase).ifPresentOrElse(kingdom -> {
            if (!kingdom.hasPermission(user, KPermission.SET_KINGDOM_HOME)) {
                this.messageHandler.sendMessage(user, KMessage.NO_KINGDOM_PERMISSION);
                return;
            }
            final KingdomSetHomeEvent kingdomSetHomeEvent = new KingdomSetHomeEvent(kingdom, user, worldPosition);
            Bukkit.getPluginManager().callEvent(kingdomSetHomeEvent);
            if (kingdomSetHomeEvent.isCancelled()) return;
            kingdom.getLocations().setPosition(KingdomLocations.HOME, kingdomSetHomeEvent.getPosition());
            this.messageHandler.sendMessage(user, KMessage.SET_KINGDOM_HOME, kingdom, kingdomSetHomeEvent.getPosition());
        }, () -> this.messageHandler.sendMessage(user, KMessage.NOT_IN_KINGDOM));

    }

    public void tryTeleportTo(User user, String id, KPermission requiredPerm) {
        this.getKingdom(user.getKingdomId(), false).ifPresentOrElse(kingdom -> {
            final WorldPosition worldPosition = kingdom.getLocations().getPosition(id);
            if (worldPosition == null) {
                this.messageHandler.sendMessage(user, KMessage.KINGDOM_LOCATION_NOT_SET, id);
                return;
            }
            if (!kingdom.hasPermission(user, requiredPerm)) {
                this.messageHandler.sendMessage(user, KMessage.NO_KINGDOM_PERMISSION);
                return;
            }
            if (!user.isOnline()) return;
            final WorldPosition currentPosition = user.getPosition();
            if (currentPosition == null) return;
            final KingdomMemberStartTeleportEvent kingdomMemberStartTeleportEvent = new KingdomMemberStartTeleportEvent(
                    user,
                    worldPosition,
                    id,
                    this.plugin.getKingdomSettings().getTeleportDelay()
            );
            Bukkit.getPluginManager().callEvent(kingdomMemberStartTeleportEvent);
            if (kingdomMemberStartTeleportEvent.isCancelled()) return;
            this.plugin.getTeleportManager().tryTeleport(
                    new TeleportInfo(
                            user,
                            kingdomMemberStartTeleportEvent.getTo(),
                            kingdomMemberStartTeleportEvent.getDelay(),
                            currentPosition,
                            kingdomMemberStartTeleportEvent.getPositionId()
                    )
            );

        }, () -> this.messageHandler.sendMessage(user, KMessage.NOT_IN_KINGDOM));
    }

    public void saveDirty() {
        this.kingdoms.values()
                .stream()
                .filter(Kingdom::isDirty)
                .forEach(this.dataManager::saveKingdom);
    }

    // return true if removed
    public boolean removeIfCanBeUnloaded(int kingdomId) {
        return this.getKingdom(kingdomId, false)
                .map(this::removeIfCanBeUnloaded)
                .orElse(false);
    }

    public boolean removeIfCanBeUnloaded(Kingdom kingdom) {
        if (!kingdom.canBeUnloaded(this.plugin)) return false;
        this.dataManager.saveKingdom(kingdom);
        kingdom.getClaimedChunks().forEach(this.plugin.getWorldManager()::remove);
        this.kingdoms.remove(kingdom.getId());
        this.byName.remove(kingdom.getName());
        Bukkit.getPluginManager().callEvent(new KingdomUnloadEvent(kingdom));
        return true;
    }

}
