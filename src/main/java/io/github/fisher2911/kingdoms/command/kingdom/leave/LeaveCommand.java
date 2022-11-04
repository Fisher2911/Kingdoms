package io.github.fisher2911.kingdoms.command.kingdom.leave;

import io.github.fisher2911.kingdoms.Kingdoms;
import io.github.fisher2911.kingdoms.command.CommandSenderType;
import io.github.fisher2911.kingdoms.command.KCommand;
import org.jetbrains.annotations.Nullable;
import io.github.fisher2911.kingdoms.kingdom.KingdomManager;
import io.github.fisher2911.kingdoms.task.TaskChain;
import io.github.fisher2911.kingdoms.user.User;

import java.util.Map;

public class LeaveCommand extends KCommand {

    private final KingdomManager kingdomManager;

    public LeaveCommand(Kingdoms plugin, @Nullable KCommand parent, Map<String, KCommand> subCommands) {
        super(plugin, parent, "leave", null, CommandSenderType.PLAYER, 0, 0, subCommands);
        this.kingdomManager = this.plugin.getKingdomManager();
    }

    @Override
    public void execute(User user, String[] args, String[] previousArgs) {
        TaskChain.create(this.plugin)
                .runAsync(() -> this.kingdomManager.tryLeave(user, true))
                .execute();
    }

//    @Override
//    public void sendHelp(User user, String[] args, String[] previousArgs) {
//        MessageHandler.sendMessage(user, "/k leave");
//    }
}
