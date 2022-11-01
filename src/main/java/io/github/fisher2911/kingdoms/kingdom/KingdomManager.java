package io.github.fisher2911.kingdoms.kingdom;

import io.github.fisher2911.kingdoms.Kingdoms;
import io.github.fisher2911.kingdoms.command.CommandPermission;
import io.github.fisher2911.kingdoms.confirm.Confirmation;
import io.github.fisher2911.kingdoms.confirm.ConfirmationManager;
import io.github.fisher2911.kingdoms.data.DataManager;
import io.github.fisher2911.kingdoms.economy.Price;
import io.github.fisher2911.kingdoms.economy.PriceManager;
import io.github.fisher2911.kingdoms.economy.PriceType;
import io.github.fisher2911.kingdoms.kingdom.location.KingdomLocations;
import io.github.fisher2911.kingdoms.kingdom.permission.KPermission;
import io.github.fisher2911.kingdoms.kingdom.role.Role;
import io.github.fisher2911.kingdoms.kingdom.role.RoleManager;
import io.github.fisher2911.kingdoms.kingdom.upgrade.Upgrades;
import io.github.fisher2911.kingdoms.message.Message;
import io.github.fisher2911.kingdoms.message.MessageHandler;
import io.github.fisher2911.kingdoms.placeholder.wrapper.UpgradesWrapper;
import io.github.fisher2911.kingdoms.task.TaskChain;
import io.github.fisher2911.kingdoms.teleport.TeleportInfo;
import io.github.fisher2911.kingdoms.user.User;
import io.github.fisher2911.kingdoms.world.WorldPosition;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class KingdomManager {

    private final Kingdoms plugin;
    private final PriceManager priceManager;
    private final DataManager dataManager;
    private final Map<Integer, Kingdom> kingdoms;
    private final Map<String, Kingdom> byName;

    public KingdomManager(Kingdoms plugin, Map<Integer, Kingdom> kingdoms) {
        this.plugin = plugin;
        this.priceManager = this.plugin.getPriceManager();
        this.dataManager = this.plugin.getDataManager();
        this.byName = new HashMap<>();
        this.kingdoms = kingdoms;
    }

    public Optional<Kingdom> tryCreate(User user, String name) {
        final Optional<Kingdom> empty = Optional.empty();
        if (user.getKingdomId() != Kingdom.WILDERNESS_ID) {
            MessageHandler.sendMessage(user, Message.ALREADY_IN_KINGDOM);
            return empty;
        }
        if (!user.hasPermission(CommandPermission.CREATE_KINGDOM)) {
            MessageHandler.sendMessage(user, Message.NO_PERMISSION_TO_CREATE_KINGDOM);
            return empty;
        }
        final Optional<Kingdom> kingdomByName = this.getKingdomByName(name, true);
        if (kingdomByName.isPresent()) {
            MessageHandler.sendMessage(user, Message.KINGDOM_ALREADY_EXISTS, kingdomByName.get());
            return empty;
        }
        if (!this.priceManager.getPrice(PriceType.KINGDOM_CREATION, Price.FREE).payIfCanAfford(user)) {
            MessageHandler.sendMessage(user, Message.CANNOT_AFFORD_TO_CREATE_KINGDOM);
            return empty;
        }
        final Kingdom kingdom = this.dataManager.newKingdom(user, name);
        MessageHandler.sendMessage(user, Message.CREATED_KINGDOM, kingdom);
        this.kingdoms.put(kingdom.getId(), kingdom);
        this.byName.put(kingdom.getName(), kingdom);
        user.setKingdomId(kingdom.getId());
        TaskChain.create(this.plugin)
                .runAsync(() -> this.dataManager.saveUser(user))
                .execute();
        return Optional.of(kingdom);
    }

    public Optional<Kingdom> join(User user, int kingdomId, boolean searchDatabase) {
        return this.getKingdom(kingdomId, searchDatabase).flatMap(kingdom -> this.join(user, kingdom));
    }

    public Optional<Kingdom> join(User user, Kingdom kingdom) {
        final Optional<Kingdom> empty = Optional.empty();
        if (user.getKingdomId() != Kingdom.WILDERNESS_ID) {
            MessageHandler.sendMessage(user, Message.ALREADY_IN_KINGDOM);
            return empty;
        }
        if (kingdom.isFull()) {
            MessageHandler.sendMessage(user, Message.OTHER_KINGDOM_FULL, kingdom);
            return empty;
        }
        kingdom.addMember(user);
        MessageHandler.sendMessage(user, Message.JOINED_KINGDOM, kingdom);
        return Optional.of(kingdom);
    }

    public void tryLevelUpUpgrade(Kingdom kingdom, User user, Upgrades<?> upgrades) {
        if (!kingdom.hasPermission(user, KPermission.UPGRADE_KINGDOM)) {
            MessageHandler.sendMessage(user, Message.NO_KINGDOM_PERMISSION);
            return;
        }
        final String upgradesId = upgrades.getId();
        final Integer upgradeLevel = kingdom.getUpgradeLevel(upgradesId);
        if (upgradeLevel == null) {
            MessageHandler.sendMessage(user, Message.UPGRADE_DOES_NOT_EXIST);
            return;
        }
        final UpgradesWrapper wrapper = new UpgradesWrapper(upgrades, upgradeLevel);
        if (upgrades.getMaxLevel() <= upgradeLevel) {
            MessageHandler.sendMessage(user, Message.ALREADY_MAX_UPGRADE_LEVEL, wrapper);
            return;
        }
        final Price price = kingdom.getUpgradePrice(upgradesId);
        if (!price.payIfCanAfford(user)) {
            MessageHandler.sendMessage(user, Message.CANNOT_AFFORD_TO_UPGRADE, wrapper);
            return;
        }
        kingdom.setUpgradeLevel(upgradesId, upgradeLevel + 1);
        MessageHandler.sendMessage(user, Message.LEVEL_UP_UPGRADE_SUCCESSFUL, new UpgradesWrapper(upgrades, upgradeLevel + 1));
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
        });
        return kingdom;
    }

    public Optional<Kingdom> getKingdomByName(String name, boolean searchDatabase) {
        final Kingdom kingdom = this.byName.get(name);
        if (kingdom != null || !searchDatabase) return Optional.ofNullable(kingdom);
        return this.dataManager.loadKingdomByName(name);
    }

    public void sendKingdomInfo(User user, Kingdom kingdom) {
        if ((kingdom.getId() != user.getKingdomId() && !user.hasPermission(CommandPermission.VIEW_OTHER_KINGDOM_INFO)) ||
                (kingdom.getId() == user.getKingdomId() && !user.hasPermission(CommandPermission.VIEW_SELF_KINGDOM_INFO))
        ) {
            MessageHandler.sendMessage(user, Message.NO_COMMAND_PERMISSION);
            return;
        }
        MessageHandler.sendMessage(user, Message.KINGDOM_INFO, kingdom);
    }

    public void sendKingdomInfo(User user, boolean searchDatabase) {
        this.getKingdom(user.getKingdomId(), searchDatabase).ifPresentOrElse(kingdom -> {
            if ((kingdom.getId() != user.getKingdomId() && !user.hasPermission(CommandPermission.VIEW_OTHER_KINGDOM_INFO)) ||
                    (kingdom.getId() == user.getKingdomId() && !user.hasPermission(CommandPermission.VIEW_SELF_KINGDOM_INFO))
            ) {
                MessageHandler.sendMessage(user, Message.NO_COMMAND_PERMISSION);
                return;
            }
            MessageHandler.sendMessage(user, Message.KINGDOM_INFO, kingdom);
        }, () -> MessageHandler.sendNotInKingdom(user));
    }

    public void tryKick(User kicker, User toKick, boolean searchDatabase) {
        this.getKingdom(kicker.getKingdomId(), searchDatabase).ifPresentOrElse(kingdom -> {
            this.tryKick(kingdom, kicker, toKick);
        }, () -> MessageHandler.sendMessage(kicker, Message.NOT_IN_KINGDOM));
    }

    public void tryKick(Kingdom kingdom, User kicker, User toKick) {
        if (!kingdom.canKick(kicker, toKick) || kicker.getId().equals(toKick.getId())) {
            MessageHandler.sendMessage(kicker, Message.NO_KINGDOM_PERMISSION);
            return;
        }
        kingdom.kick(toKick);
        MessageHandler.sendMessage(kicker, Message.KICKED_OTHER, toKick);
        MessageHandler.sendMessage(toKick, Message.KICKED_FROM_KINGDOM, kicker, kingdom);
    }

    public void trySetRole(User user, User toSet, String roleId, boolean searchDatabase) {
        this.getKingdom(user.getKingdomId(), searchDatabase).ifPresentOrElse(kingdom -> {
            this.trySetRole(kingdom, user, toSet, roleId);
        }, () -> MessageHandler.sendNotInKingdom(user));
    }

    public void trySetRole(Kingdom kingdom, User user, User toSet, String roleId) {
        if (toSet.getKingdomId() != user.getKingdomId()) {
            MessageHandler.sendMessage(user, Message.NOT_IN_SAME_KINGDOM, toSet);
            return;
        }
        final Role role = this.plugin.getRoleManager().getRole(roleId, kingdom);
        if (role == null) {
            MessageHandler.sendMessage(user, Message.ROLE_DOES_NOT_EXIST);
            return;
        }
        if (RoleManager.UNSETTABLE_ROLES.contains(roleId)) {
            MessageHandler.sendMessage(user, Message.CANNOT_SET_PERMISSION_ROLE, role);
            return;
        }
        if (user.getId().equals(toSet.getId())) {
            MessageHandler.sendMessage(user, Message.CANNOT_SET_SELF_ROLE);
            return;
        }
        final Role setterRole = kingdom.getRole(user);
        final Role previousRole = kingdom.getRole(toSet);
        if (!kingdom.hasPermission(user, KPermission.SET_MEMBER_ROLE) || previousRole.isHigherRankedThan(setterRole)) {
            MessageHandler.sendMessage(user, Message.NO_KINGDOM_PERMISSION);
            return;
        }
        kingdom.setRole(toSet, role);
        MessageHandler.sendMessage(user, Message.SET_OTHER_ROLE, toSet, role);
        MessageHandler.sendMessage(toSet, Message.OWN_ROLE_SET, user, role);
    }

    public void tryLeave(User user, boolean searchDatabase) {
        this.getKingdom(user.getKingdomId(), searchDatabase).
                ifPresentOrElse(kingdom -> {
                    final Role role = kingdom.getRole(user);
                    if (role.equals(this.plugin.getRoleManager().getLeaderRole(kingdom))) {
                        MessageHandler.sendMessage(user, Message.LEADER_CANNOT_LEAVE_KINGDOM);
                        return;
                    }
                    kingdom.removeMember(user);
                    MessageHandler.sendMessage(kingdom, Message.MEMBER_LEFT_KINGDOM, user);
                    MessageHandler.sendMessage(user, Message.YOU_LEFT_KINGDOM, kingdom);
                }, () -> MessageHandler.sendNotInKingdom(user));
    }

    public void tryDisband(User user, boolean searchDatabase) {
        this.tryDisband(user, false, searchDatabase);
    }

    public void tryDisband(User user, boolean force, boolean searchDatabase) {
        this.getKingdom(user.getKingdomId(), searchDatabase).
                ifPresentOrElse(kingdom -> {
                    if (!kingdom.isLeader(user)) {
                        MessageHandler.sendMessage(user, Message.NO_KINGDOM_PERMISSION);
                        return;
                    }
                    final ConfirmationManager confirmationManager = this.plugin.getConfirmationManager();
                    if (!confirmationManager.hasConfirmation(Confirmation.DISBAND_KINGDOM, user.getId(), true) && !force) {
                        MessageHandler.sendMessage(user, Message.CONFIRM_DISBAND_KINGDOM, kingdom);
                        confirmationManager.addConfirmation(
                                Confirmation.DISBAND_KINGDOM,
                                user.getId(),
                                20 * 10,
                                () -> MessageHandler.sendMessage(user, Message.DISBAND_KINGDOM_CONFIRMATION_EXPIRED)
                        );
                        return;
                    }
                    this.disband(user, kingdom, searchDatabase);
                }, () -> MessageHandler.sendNotInKingdom(user));
    }

    private void disband(User user, Kingdom kingdom, boolean searchDatabase) {
        MessageHandler.sendMessage(kingdom, Message.KINGDOM_DISBANDED, kingdom, user);
        kingdom.getKingdomRelations().keySet().forEach(id ->
                this.getKingdom(id, searchDatabase).ifPresent(k -> this.plugin.getRelationManager().removeRelation(k, kingdom))
        );
        kingdom.getMembers().forEach(member -> member.setKingdomId(Kingdom.WILDERNESS_ID));
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
                MessageHandler.sendMessage(user, Message.NO_KINGDOM_PERMISSION);
                return;
            }
            kingdom.getLocations().setPosition(KingdomLocations.HOME, worldPosition);
            MessageHandler.sendMessage(user, Message.SET_KINGDOM_HOME, kingdom, worldPosition);
        }, () -> MessageHandler.sendNotInKingdom(user));

    }

    public void tryTeleportTo(User user, String id, KPermission requiredPerm) {
        this.getKingdom(user.getKingdomId(), false).ifPresentOrElse(kingdom -> {
            final WorldPosition worldPosition = kingdom.getLocations().getPosition(id);
            if (worldPosition == null) {
                MessageHandler.sendMessage(user, Message.KINGDOM_LOCATION_NOT_SET, id);
                return;
            }
            if (!kingdom.hasPermission(user, requiredPerm)) {
                MessageHandler.sendMessage(user, Message.NO_KINGDOM_PERMISSION);
                return;
            }
            if (!user.isOnline()) return;
            final WorldPosition currentPosition = user.getPosition();
            if (currentPosition == null) return;
            this.plugin.getTeleportManager().tryTeleport(
                    new TeleportInfo(
                            user,
                            worldPosition,
                            this.plugin.getKingdomSettings().getTeleportDelay(),
                            currentPosition
                    )
            );

        }, () -> MessageHandler.sendNotInKingdom(user));
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
        return true;
    }

}
