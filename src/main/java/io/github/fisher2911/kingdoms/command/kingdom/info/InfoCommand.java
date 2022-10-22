package io.github.fisher2911.kingdoms.command.kingdom.info;

import io.github.fisher2911.kingdoms.Kingdoms;
import io.github.fisher2911.kingdoms.command.CommandSenderType;
import io.github.fisher2911.kingdoms.command.KCommand;
import io.github.fisher2911.kingdoms.kingdom.KingdomManager;
import io.github.fisher2911.kingdoms.message.Message;
import io.github.fisher2911.kingdoms.message.MessageHandler;
import io.github.fisher2911.kingdoms.user.User;

import java.util.Map;

public class InfoCommand extends KCommand {

    private final KingdomManager kingdomManager;

    public InfoCommand(Kingdoms plugin, Map<String, KCommand> subCommands) {
        super(plugin, "info", null, CommandSenderType.ANY, 0, 1, subCommands);
        this.kingdomManager = this.plugin.getKingdomManager();
    }

    @Override
    public void execute(User user, String[] args, String[] previousArgs) {
        if (args.length == 0) {
            this.kingdomManager.sendKingdomInfo(user);
            return;
        }
        final String kingdomName = args[0];
        this.kingdomManager.getKingdomByName(kingdomName).ifPresentOrElse(k ->
                        this.kingdomManager.sendKingdomInfo(user, k),
                () -> MessageHandler.sendMessage(user, Message.KINGDOM_NOT_FOUND)
        );
    }

    @Override
    public void sendHelp(User user, String[] args, String[] previousArgs) {

    }
}
