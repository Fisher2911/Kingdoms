package io.github.fisher2911.kingdoms.command.kingdom.bank;

import io.github.fisher2911.kingdoms.Kingdoms;
import io.github.fisher2911.kingdoms.command.CommandSenderType;
import io.github.fisher2911.kingdoms.command.KCommand;
import io.github.fisher2911.kingdoms.message.MessageHandler;
import io.github.fisher2911.kingdoms.user.User;

import java.util.HashMap;
import java.util.Map;

public class BankCommand extends KCommand {

    public BankCommand(Kingdoms plugin, Map<String, KCommand> subCommands) {
        super(plugin, "bank", null, CommandSenderType.PLAYER, 1, 3, subCommands);
        this.addSubCommand(new BalanceSubCommand(plugin, new HashMap<>()));
        this.addSubCommand(new DepositSubCommand(plugin, new HashMap<>()));
        this.addSubCommand(new WithdrawSubCommand(plugin, new HashMap<>()));
    }

    @Override
    public void execute(User user, String[] args, String[] previousArgs) {
        this.sendHelp(user, args, previousArgs);
    }

    @Override
    public void sendHelp(User user, String[] args, String[] previousArgs) {
        MessageHandler.sendMessage(user, "/k bank <deposit | withdraw | balance>");
    }

}
