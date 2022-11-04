package io.github.fisher2911.kingdoms.command.kingdom.kick;

import io.github.fisher2911.kingdoms.Kingdoms;
import io.github.fisher2911.kingdoms.command.CommandSenderType;
import io.github.fisher2911.kingdoms.command.KCommand;
import org.jetbrains.annotations.Nullable;
import io.github.fisher2911.kingdoms.kingdom.KingdomManager;
import io.github.fisher2911.kingdoms.message.Message;
import io.github.fisher2911.kingdoms.message.MessageHandler;
import io.github.fisher2911.kingdoms.task.TaskChain;
import io.github.fisher2911.kingdoms.user.User;
import io.github.fisher2911.kingdoms.user.UserManager;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

public class KickCommand extends KCommand {

    private final UserManager userManager;
    private final KingdomManager kingdomManager;

    public KickCommand(Kingdoms plugin, @Nullable KCommand parent, Map<String, KCommand> subCommands) {
        super(plugin, parent, "kick", null, CommandSenderType.PLAYER, 1, 1, subCommands);
        this.userManager = this.plugin.getUserManager();
        this.kingdomManager = this.plugin.getKingdomManager();
    }

    @Override
    public void execute(User user, String[] args, String[] previousArgs) {
        final String playerName = args[0];
        TaskChain.create(this.plugin).
                supplyAsync(() -> this.userManager.getUserByName(playerName, true))
                .consumeAsync(opt -> opt.ifPresentOrElse(toKick -> this.kingdomManager.tryKick(user, toKick, true),
                        () -> MessageHandler.sendMessage(user, Message.NOT_IN_KINGDOM)))
                .execute();

    }

//    @Override
//    public void sendHelp(User user, String[] args, String[] previousArgs) {
//        MessageHandler.sendMessage(user, "/k kick <player>");
//    }

    @Override
    public @Nullable List<String> getTabs(User user, String[] args, String[] previousArgs, boolean defaultTabIsNull) {
        return super.getTabs(user, args, previousArgs, true);
    }
}
