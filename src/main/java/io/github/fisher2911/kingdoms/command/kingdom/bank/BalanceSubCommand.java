package io.github.fisher2911.kingdoms.command.kingdom.bank;

import io.github.fisher2911.kingdoms.Kingdoms;
import io.github.fisher2911.kingdoms.command.CommandSenderType;
import io.github.fisher2911.kingdoms.command.KCommand;
import io.github.fisher2911.kingdoms.economy.EconomyManager;
import io.github.fisher2911.kingdoms.user.User;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class BalanceSubCommand extends KCommand {

    private final EconomyManager economyManager;

    public BalanceSubCommand(Kingdoms plugin, @Nullable KCommand parent, Map<String, KCommand> subCommands) {
        super(plugin, parent, "balance", null, CommandSenderType.PLAYER, 0, 0, subCommands);
        this.economyManager = this.plugin.getEconomyManager();
    }

    @Override
    public void execute(User user, String[] args, String[] previousArgs) {
        this.economyManager.sendKingdomBalance(user);
    }

//    @Override
//    public void sendHelp(User user, String[] args, String[] previousArgs) {
//        MessageHandler.sendMessage(user, "/k bank balance");
//    }

}
