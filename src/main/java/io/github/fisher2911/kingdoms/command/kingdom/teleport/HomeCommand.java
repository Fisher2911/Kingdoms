package io.github.fisher2911.kingdoms.command.kingdom.teleport;

import io.github.fisher2911.kingdoms.Kingdoms;
import io.github.fisher2911.kingdoms.command.CommandSenderType;
import io.github.fisher2911.kingdoms.command.KCommand;
import io.github.fisher2911.kingdoms.kingdom.KingdomManager;
import io.github.fisher2911.kingdoms.kingdom.location.KingdomLocations;
import io.github.fisher2911.kingdoms.kingdom.permission.KPermission;
import io.github.fisher2911.kingdoms.message.MessageHandler;
import io.github.fisher2911.kingdoms.user.User;

import java.util.Map;

public class HomeCommand extends KCommand {

    private final KingdomManager kingdomManager;

    public HomeCommand(Kingdoms plugin, Map<String, KCommand> subCommands) {
        super(plugin, "home", null, CommandSenderType.PLAYER, 0, 0, subCommands);
        this.kingdomManager = this.plugin.getKingdomManager();
    }

    @Override
    public void execute(User user, String[] args, String[] previousArgs) {
        this.kingdomManager.tryTeleportTo(
                user,
                KingdomLocations.HOME,
                KPermission.TELEPORT_TO_KINGDOM_HOME
        );
    }

    @Override
    public void sendHelp(User user, String[] args, String[] previousArgs) {
        MessageHandler.sendMessage(user, "/k home");
    }

}
