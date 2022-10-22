package io.github.fisher2911.kingdoms.command.kingdom.upgrade;

import io.github.fisher2911.kingdoms.Kingdoms;
import io.github.fisher2911.kingdoms.command.CommandSenderType;
import io.github.fisher2911.kingdoms.command.KCommand;
import io.github.fisher2911.kingdoms.gui.impl.UpgradeGui;
import io.github.fisher2911.kingdoms.kingdom.KingdomManager;
import io.github.fisher2911.kingdoms.message.Message;
import io.github.fisher2911.kingdoms.message.MessageHandler;
import io.github.fisher2911.kingdoms.user.User;

import java.util.Map;

public class UpgradeCommand extends KCommand {

    private final KingdomManager kingdomManager;

    public UpgradeCommand(Kingdoms plugin, Map<String, KCommand> subCommands) {
        super(plugin, "upgrades", null, CommandSenderType.PLAYER, 0, 1, subCommands);
        this.kingdomManager = this.plugin.getKingdomManager();
    }

    @Override
    public void execute(User user, String[] args, String[] previousArgs) {
        this.kingdomManager.getKingdom(user.getKingdomId()).
                ifPresentOrElse(kingdom -> {
                    UpgradeGui.create(
                            this.plugin,
                            kingdom
                    ).open(user.getPlayer());
                }, () -> MessageHandler.sendMessage(user, Message.NOT_IN_KINGDOM));
    }

    @Override
    public void sendHelp(User user, String[] args, String[] previousArgs) {
        MessageHandler.sendMessage(user, "/k upgrades");
    }
}
