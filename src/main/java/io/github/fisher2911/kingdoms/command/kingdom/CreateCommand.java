package io.github.fisher2911.kingdoms.command.kingdom;

import io.github.fisher2911.kingdoms.Kingdoms;
import io.github.fisher2911.kingdoms.command.CommandSenderType;
import io.github.fisher2911.kingdoms.command.KCommand;
import org.jetbrains.annotations.Nullable;
import io.github.fisher2911.kingdoms.kingdom.KingdomManager;
import io.github.fisher2911.kingdoms.message.Message;
import io.github.fisher2911.kingdoms.message.MessageHandler;
import io.github.fisher2911.kingdoms.task.TaskChain;
import io.github.fisher2911.kingdoms.user.User;

import java.util.Map;

public class CreateCommand extends KCommand {

    private final KingdomManager kingdomManager;

    public CreateCommand(Kingdoms plugin, @Nullable KCommand parent, Map<String, KCommand> subCommands) {
        super(
                plugin,
                parent,
                "create",
                null,
                CommandSenderType.PLAYER,
                1,
                1,
                subCommands
        );
        this.kingdomManager = this.plugin.getKingdomManager();
    }

    @Override
    public void execute(User user, String[] args, String[] previous) {
        if (user.hasKingdom()) {
            MessageHandler.sendMessage(user, Message.ALREADY_IN_KINGDOM);
            return;
        }
        final String name = args[0];
        TaskChain.create(this.plugin)
                .runAsync(() -> this.kingdomManager.tryCreate(user, name))
                .execute();
    }

}
