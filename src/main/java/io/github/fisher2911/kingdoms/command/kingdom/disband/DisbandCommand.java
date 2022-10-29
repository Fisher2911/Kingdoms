package io.github.fisher2911.kingdoms.command.kingdom.disband;

import io.github.fisher2911.kingdoms.Kingdoms;
import io.github.fisher2911.kingdoms.command.CommandSenderType;
import io.github.fisher2911.kingdoms.command.KCommand;
import io.github.fisher2911.kingdoms.kingdom.KingdomManager;
import io.github.fisher2911.kingdoms.task.TaskChain;
import io.github.fisher2911.kingdoms.user.User;

import java.util.Map;

public class DisbandCommand extends KCommand {

    private final KingdomManager kingdomManager;

    public DisbandCommand(Kingdoms plugin, Map<String, KCommand> subCommands) {
        super(plugin, "disband", null, CommandSenderType.PLAYER, 0, 0, subCommands);
        this.kingdomManager = this.plugin.getKingdomManager();
    }

    @Override
    public void execute(User user, String[] args, String[] previousArgs) {
        TaskChain.create(this.plugin)
                .runAsync(() -> this.kingdomManager.tryDisband(user, true))
                .execute();
    }

    @Override
    public void sendHelp(User user, String[] args, String[] previousArgs) {

    }
}
