package io.github.fisher2911.kingdoms.command.kingdom.teleport;

import io.github.fisher2911.kingdoms.Kingdoms;
import io.github.fisher2911.kingdoms.command.CommandSenderType;
import io.github.fisher2911.kingdoms.command.KCommand;
import io.github.fisher2911.kingdoms.kingdom.KingdomManager;
import io.github.fisher2911.kingdoms.message.MessageHandler;
import io.github.fisher2911.kingdoms.user.User;

import java.util.Map;

public class SetHomeCommand extends KCommand {

    private final KingdomManager kingdomManager;

    public SetHomeCommand(Kingdoms plugin, Map<String, KCommand> subCommands) {
        super(plugin, "sethome", null, CommandSenderType.PLAYER, 0, 0, subCommands);
        this.kingdomManager = this.plugin.getKingdomManager();
    }

    @Override
    public void execute(User user, String[] args, String[] previousArgs) {
        this.kingdomManager.trySetHome(user);
    }

    @Override
    public void sendHelp(User user, String[] args, String[] previousArgs) {
        MessageHandler.sendMessage(user, "/k sethome");
    }

}
