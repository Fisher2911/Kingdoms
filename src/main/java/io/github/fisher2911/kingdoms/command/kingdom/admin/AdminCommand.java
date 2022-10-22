package io.github.fisher2911.kingdoms.command.kingdom.admin;

import io.github.fisher2911.kingdoms.Kingdoms;
import io.github.fisher2911.kingdoms.command.CommandPermission;
import io.github.fisher2911.kingdoms.command.CommandSenderType;
import io.github.fisher2911.kingdoms.command.KCommand;
import io.github.fisher2911.kingdoms.message.MessageHandler;
import io.github.fisher2911.kingdoms.user.User;

import java.util.HashMap;
import java.util.Map;

public class AdminCommand extends KCommand {

    public AdminCommand(Kingdoms plugin, Map<String, KCommand> subCommands) {
        super(plugin, "admin", CommandPermission.ADMIN_COMMAND, CommandSenderType.ANY, -1, -1, subCommands);
        this.addSubCommand(new ReloadSubCommand(this.plugin, new HashMap<>()));
    }

    @Override
    public void execute(User user, String[] args, String[] previousArgs) {
        this.sendHelp(user, args, previousArgs);
    }

    @Override
    public void sendHelp(User user, String[] args, String[] previousArgs) {
        MessageHandler.sendMessage(user, "/k admin");
    }
}
