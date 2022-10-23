package io.github.fisher2911.kingdoms.command.kingdom.permission;

import io.github.fisher2911.kingdoms.Kingdoms;
import io.github.fisher2911.kingdoms.command.CommandSenderType;
import io.github.fisher2911.kingdoms.command.KCommand;
import io.github.fisher2911.kingdoms.message.Message;
import io.github.fisher2911.kingdoms.message.MessageHandler;
import io.github.fisher2911.kingdoms.user.User;

import java.util.HashMap;
import java.util.Map;

public class PermissionCommand extends KCommand {

    public PermissionCommand(Kingdoms plugin, Map<String, KCommand> subCommands) {
        super(plugin, "permissions", null, CommandSenderType.PLAYER, 0, -1, subCommands);
        this.addSubCommand(new EditPermissionsCommand(this.plugin, new HashMap<>()));
    }

    @Override
    public void execute(User user, String[] args, String[] previous) {
        if (!user.hasKingdom()) {
            MessageHandler.sendNotInKingdom(user);
            return;
        }
        this.sendHelp(user, args, previous);
    }

    @Override
    public void sendHelp(User user, String[] args, String[] previous) {
        MessageHandler.sendMessage(user, "/k permission edit [role]");
    }

}
