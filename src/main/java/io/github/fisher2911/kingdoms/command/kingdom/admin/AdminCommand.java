package io.github.fisher2911.kingdoms.command.kingdom.admin;

import io.github.fisher2911.kingdoms.Kingdoms;
import io.github.fisher2911.kingdoms.command.CommandPermission;
import io.github.fisher2911.kingdoms.command.CommandSenderType;
import io.github.fisher2911.kingdoms.command.KCommand;
import io.github.fisher2911.kingdoms.user.User;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class AdminCommand extends KCommand {

    public AdminCommand(Kingdoms plugin, @Nullable KCommand parent, Map<String, KCommand> subCommands) {
        super(
                plugin,
                parent,
                "admin",
                CommandPermission.ADMIN_COMMAND,
                CommandSenderType.ANY,
                -1,
                -1,
                subCommands
        );
        this.addSubCommand(new ReloadSubCommand(this.plugin, this, new HashMap<>()));
    }

    @Override
    public void execute(User user, String[] args, String[] previousArgs) {
    }

}
