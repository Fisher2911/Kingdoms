package io.github.fisher2911.kingdoms.command.kingdom.teleport;

import io.github.fisher2911.kingdoms.Kingdoms;
import io.github.fisher2911.kingdoms.command.CommandSenderType;
import io.github.fisher2911.kingdoms.command.KCommand;
import org.jetbrains.annotations.Nullable;
import io.github.fisher2911.kingdoms.kingdom.KingdomManager;
import io.github.fisher2911.kingdoms.message.Message;
import io.github.fisher2911.kingdoms.message.MessageHandler;
import io.github.fisher2911.kingdoms.task.TaskChain;
import io.github.fisher2911.kingdoms.user.User;
import io.github.fisher2911.kingdoms.world.WorldPosition;

import java.util.Map;

public class SetHomeCommand extends KCommand {

    private final KingdomManager kingdomManager;

    public SetHomeCommand(Kingdoms plugin, @Nullable KCommand parent, Map<String, KCommand> subCommands) {
        super(plugin, parent, "sethome", null, CommandSenderType.PLAYER, 0, 0, subCommands);
        this.kingdomManager = this.plugin.getKingdomManager();
    }

    @Override
    public void execute(User user, String[] args, String[] previousArgs) {
        final WorldPosition position = user.getPosition();
        if (position == null) {
            MessageHandler.sendMessage(user, Message.INVALID_POSITION);
            return;
        }
        TaskChain.create(this.plugin)
                .runAsync(() -> this.kingdomManager.trySetHome(user, position, true))
                .execute();
    }

//    @Override
//    public void sendHelp(User user, String[] args, String[] previousArgs) {
//        MessageHandler.sendMessage(user, "/k sethome");
//    }

}
