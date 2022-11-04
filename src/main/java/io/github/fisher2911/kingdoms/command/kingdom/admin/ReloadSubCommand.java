package io.github.fisher2911.kingdoms.command.kingdom.admin;

import io.github.fisher2911.kingdoms.Kingdoms;
import io.github.fisher2911.kingdoms.command.CommandPermission;
import io.github.fisher2911.kingdoms.command.CommandSenderType;
import io.github.fisher2911.kingdoms.command.KCommand;
import io.github.fisher2911.kingdoms.message.Message;
import io.github.fisher2911.kingdoms.message.MessageHandler;
import io.github.fisher2911.kingdoms.user.User;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class ReloadSubCommand extends KCommand {

    public ReloadSubCommand(Kingdoms plugin, @Nullable KCommand parent, Map<String, KCommand> subCommands) {
        super(
                plugin,
                parent,
                "reload",
                CommandPermission.RELOAD_COMMAND,
                CommandSenderType.ANY,
                -1,
                -1,
                subCommands
        );
    }

    @Override
    public void execute(User user, String[] args, String[] previousArgs) {
        this.plugin.reload();
        MessageHandler.sendMessage(user, Message.SUCCESSFUL_RELOAD);
    }

}
