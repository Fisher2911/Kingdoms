package io.github.fisher2911.kingdoms.kingdom;

import io.github.fisher2911.kingdoms.Kingdoms;
import io.github.fisher2911.kingdoms.command.CommandPermission;
import io.github.fisher2911.kingdoms.data.DataManager;
import io.github.fisher2911.kingdoms.economy.Price;
import io.github.fisher2911.kingdoms.economy.PriceManager;
import io.github.fisher2911.kingdoms.economy.PriceType;
import io.github.fisher2911.kingdoms.kingdom.permission.KPermission;
import io.github.fisher2911.kingdoms.kingdom.upgrade.Upgrades;
import io.github.fisher2911.kingdoms.message.Message;
import io.github.fisher2911.kingdoms.message.MessageHandler;
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
        if (this.dataManager.getKingdomByName(name) != null) {
            MessageHandler.sendMessage(user, Message.KINGDOM_ALREADY_EXISTS);
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
            MessageHandler.sendMessage(user, Message.KINGDOM_FULL);
            return empty;
        }
        kingdom.addMember(user);
        MessageHandler.sendMessage(user, Message.JOINED_KINGDOM);
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
        if (upgrades.getMaxLevel() <= upgradeLevel) {
            MessageHandler.sendMessage(user, Message.ALREADY_MAX_UPGRADE_LEVEL);
            return;
        }
        final Price price = kingdom.getUpgradePrice(upgradesId);
        if (!price.payIfCanAfford(user)) {
            MessageHandler.sendMessage(user, Message.CANNOT_AFFORD_TO_UPGRADE);
            MessageHandler.sendMessage(user, price.getDisplay());
            return;
        }
        kingdom.setUpgradeLevel(upgradesId, upgradeLevel + 1);
        MessageHandler.sendMessage(user, Message.LEVEL_UP_UPGRADE_SUCCESSFUL);
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
        }, () -> MessageHandler.sendMessage(user, Message.NOT_IN_KINGDOM));
    }


    public int countKingdoms() {
        return this.kingdoms.size();
    }
}
