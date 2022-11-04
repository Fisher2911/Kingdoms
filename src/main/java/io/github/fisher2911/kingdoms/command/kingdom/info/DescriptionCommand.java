package io.github.fisher2911.kingdoms.command.kingdom.info;

import io.github.fisher2911.kingdoms.Kingdoms;
import io.github.fisher2911.kingdoms.command.CommandSenderType;
import io.github.fisher2911.kingdoms.command.KCommand;
import io.github.fisher2911.kingdoms.kingdom.KingdomManager;
import io.github.fisher2911.kingdoms.task.TaskChain;
import io.github.fisher2911.kingdoms.user.User;

import java.util.Map;

public class DescriptionCommand extends KCommand {

    private final KingdomManager kingdomManager;

    public DescriptionCommand(Kingdoms plugin, KCommand parent, Map<String, KCommand> subCommands) {
        super(plugin, parent, "description", null, CommandSenderType.PLAYER, 1, -1, subCommands);
        this.kingdomManager = this.plugin.getKingdomManager();
    }

    @Override
    public void execute(User user, String[] args, String[] previousArgs) {
        if (args.length == 0) {
            TaskChain.create(this.plugin)
                    .runAsync(() -> this.kingdomManager.sendKingdomDescription(user, true))
                    .execute();
            return;
        }
        if (!args[0].equalsIgnoreCase("set")) {
            this.sendHelp(user);
            return;
        }
        if (args.length == 1) {
            this.sendHelp(user);
            return;
        }
        final StringBuilder description = new StringBuilder();
        for (int i = 1; i < args.length; i++) {
            description.append(args[i]).append(" ");
        }
        TaskChain.create(this.plugin)
                .runAsync(() -> this.kingdomManager.trySetDescription(user, description.toString(), true))
                .execute();
    }

//    @Override
//    public void sendHelp(User user, String[] args, String[] previousArgs) {
//        MessageHandler.sendMessage(user, "/k description [set] [description]");
//    }

}
