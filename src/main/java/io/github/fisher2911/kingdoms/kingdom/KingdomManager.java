package io.github.fisher2911.kingdoms.kingdom;

import io.github.fisher2911.kingdoms.Kingdoms;
import io.github.fisher2911.kingdoms.command.CommandPermission;
import io.github.fisher2911.kingdoms.confirm.Confirmation;
import io.github.fisher2911.kingdoms.confirm.ConfirmationManager;
import io.github.fisher2911.kingdoms.data.DataManager;
import io.github.fisher2911.kingdoms.economy.Price;
import io.github.fisher2911.kingdoms.economy.PriceManager;
import io.github.fisher2911.kingdoms.economy.PriceType;
import io.github.fisher2911.kingdoms.kingdom.permission.KPermission;
import io.github.fisher2911.kingdoms.kingdom.role.Role;
import io.github.fisher2911.kingdoms.kingdom.upgrade.Upgrades;
import io.github.fisher2911.kingdoms.message.Message;
import io.github.fisher2911.kingdoms.message.MessageHandler;
import io.github.fisher2911.kingdoms.placeholder.wrapper.UpgradesWrapper;
import io.github.fisher2911.kingdoms.user.User;

import java.util.Map;
import java.util.Optional;

public class KingdomManager {

    private final Kingdoms plugin;
    private final PriceManager priceManager;
    private final DataManager dataManager;
    private final Map<Integer, Kingdom> kingdoms;

    public KingdomManager(Kingdoms plugin, Map<Integer, Kingdom> kingdoms) {
        this.plugin = plugin;
        this.priceManager = this.plugin.getPriceManager();
        this.dataManager = this.plugin.getDataManager();
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
        final Optional<Kingdom> kingdomByName = this.getKingdomByName(name);
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
        user.setKingdomId(kingdom.getId());
        return Optional.of(kingdom);
    }

    public Optional<Kingdom> join(User user, int kingdomId) {
        return this.getKingdom(kingdomId).flatMap(kingdom -> this.join(user, kingdom));
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

    public Optional<Kingdom> getKingdom(int id) {
        return Optional.ofNullable(this.kingdoms.get(id));
    }

    public Optional<Kingdom> getKingdomByName(String name) {
        return this.kingdoms.values().stream().filter(k -> k.getName().equalsIgnoreCase(name)).findAny();
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

    public void sendKingdomInfo(User user) {
        this.getKingdom(user.getKingdomId()).ifPresentOrElse(kingdom -> {
            if ((kingdom.getId() != user.getKingdomId() && !user.hasPermission(CommandPermission.VIEW_OTHER_KINGDOM_INFO)) ||
                    (kingdom.getId() == user.getKingdomId() && !user.hasPermission(CommandPermission.VIEW_SELF_KINGDOM_INFO))
            ) {
                MessageHandler.sendMessage(user, Message.NO_COMMAND_PERMISSION);
                return;
            }
            MessageHandler.sendMessage(user, Message.KINGDOM_INFO, kingdom);
        }, () -> MessageHandler.sendNotInKingdom(user));
    }

    public void tryKick(User kicker, User toKick) {
        this.getKingdom(kicker.getKingdomId()).ifPresentOrElse(kingdom -> {
            if (!kingdom.canKick(kicker, toKick)) {
                MessageHandler.sendMessage(kicker, Message.NO_KINGDOM_PERMISSION);
                return;
            }
            kingdom.kick(toKick);
            MessageHandler.sendMessage(kicker, Message.KICKED_OTHER, toKick);
            MessageHandler.sendMessage(toKick, Message.KICKED_FROM_KINGDOM, kicker, kingdom);
        }, () -> MessageHandler.sendMessage(kicker, Message.NOT_IN_KINGDOM));
    }

    public void trySetRole(User user, User toSet, String roleId) {
        this.getKingdom(user.getKingdomId()).ifPresentOrElse(kingdom -> {
            if (toSet.getKingdomId() != user.getKingdomId()) {
                MessageHandler.sendMessage(user, Message.NOT_IN_SAME_KINGDOM, toSet);
                return;
            }
            final Role role = this.plugin.getRoleManager().getRole(roleId, kingdom);
            final Role setterRole = kingdom.getRole(user);
            final Role previousRole = kingdom.getRole(toSet);
            if (!kingdom.hasPermission(user, KPermission.SET_MEMBER_ROLE) || previousRole.isHigherRankedThan(setterRole)) {
                MessageHandler.sendMessage(user, Message.NO_KINGDOM_PERMISSION);
                return;
            }
            kingdom.setRole(toSet, role);
            MessageHandler.sendMessage(user, Message.SET_OTHER_ROLE, toSet, role);
            MessageHandler.sendMessage(user, Message.OWN_ROLE_SET, user, role);
        }, () -> MessageHandler.sendNotInKingdom(user));
    }

    public void tryLeave(User user) {
        this.getKingdom(user.getKingdomId()).
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

    public void tryDisband(User user) {
        this.tryDisband(user, false);
    }

    public void tryDisband(User user, boolean force) {
        this.getKingdom(user.getKingdomId()).
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
                    this.disband(user, kingdom);
                }, () -> MessageHandler.sendNotInKingdom(user));
    }

    private void disband(User user, Kingdom kingdom) {
        MessageHandler.sendMessage(kingdom, Message.KINGDOM_DISBANDED, kingdom, user);
        kingdom.getKingdomRelations().keySet().forEach(id ->
                this.getKingdom(id).ifPresent(k -> this.plugin.getRelationManager().removeRelation(k, kingdom))
        );
        kingdom.getMembers().forEach(member -> member.setKingdomId(Kingdom.WILDERNESS_ID));
        this.kingdoms.remove(kingdom.getId());
    }

    public int countKingdoms() {
        return this.kingdoms.size();
    }


}
