package io.github.fisher2911.kingdoms.command;

import io.github.fisher2911.kingdoms.Kingdoms;
import io.github.fisher2911.kingdoms.message.Message;
import io.github.fisher2911.kingdoms.message.MessageHandler;
import io.github.fisher2911.kingdoms.user.User;
import io.github.fisher2911.kingdoms.user.UserManager;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class KCommand {

    protected final Kingdoms plugin;
    protected final UserManager userManager;
    protected final String name;
    @Nullable
    protected final CommandPermission permission;
    protected final CommandSenderType senderType;
    private final int minArgs;
    private final int maxArgs;
    protected final Map<String, KCommand> subCommands;

    public KCommand(
            Kingdoms plugin,
            String name,
            @Nullable CommandPermission permission,
            CommandSenderType senderType,
            int minArgs,
            int maxArgs,
            Map<String, KCommand> subCommands
    ) {
        this.plugin = plugin;
        this.userManager = this.plugin.getUserManager();
        this.name = name;
        this.permission = permission;
        this.senderType = senderType;
        this.minArgs = minArgs;
        this.maxArgs = maxArgs;
        this.subCommands = subCommands;
    }

    public void handleArgs(CommandSender sender, String[] args, String[] previousArgs) {
        final User user = this.userManager.forceGet(sender);
        if (user == null) {
            MessageHandler.sendMessage(sender, Message.USER_DATA_LOAD_ERROR);
            return;
        }
        if (!this.senderType.canExecute(sender)) {
            MessageHandler.sendMessage(user, Message.INVALID_COMMAND_EXECUTOR);
            return;
        }
        if (this.permission != null && !user.hasPermission(this.permission)) {
            MessageHandler.sendMessage(user, Message.NO_COMMAND_PERMISSION);
            return;
        }
        final int argsLength = args.length;
        final String[] newPrevious = this.getPreviousArgs(args, previousArgs);
        if ((this.minArgs != -1 && argsLength < this.minArgs) || (this.maxArgs != -1 && argsLength > this.maxArgs)) {
            this.sendHelp(user, args, newPrevious);
            return;
        }
        if (args.length == 0) {
            this.execute(user, args, previousArgs);
            return;
        }
        final String first = args[0];
        final KCommand subCommand = this.subCommands.get(first.toLowerCase());
        if (subCommand != null) {
//            final String[] newArgs = new String[argsLength - 1];
//            System.arraycopy(args, 1, newArgs, 0, newArgs.length);
            final String[] newArgs = this.getNewArgs(args);
            subCommand.handleArgs(sender, newArgs, newPrevious);
            return;
        }
        this.execute(user, args, newPrevious);
    }

    public abstract void execute(User user, String[] args, String[] previousArgs);

    public abstract void sendHelp(User user, String[] args, String[] previousArgs);

    protected void addSubCommand(KCommand command) {
        this.subCommands.put(command.name, command);
    }

    // original: [k, bank, ]
    // old: [bank]
    // new: [ ]

    @Nullable
    public List<String> getTabs(User user, String[] args, String[] previousArgs, boolean defaultTabIsNull) {
        final List<String> tabs = new ArrayList<>();
        if (this.permission != null && !user.hasPermission(this.permission)) {
            MessageHandler.sendMessage(user, Message.NO_COMMAND_PERMISSION);
            if (defaultTabIsNull) return null;
            return tabs;
        }
        final int argsLength = args.length;
        final String[] newPrevious = this.getPreviousArgs(args, previousArgs);
        if ((this.minArgs != -1 && argsLength < this.minArgs) || (this.maxArgs != -1 && argsLength > this.maxArgs)) {
            if (defaultTabIsNull) return null;
            return tabs;
        }
        if (args.length == 0) {
            if (defaultTabIsNull) return null;
            return tabs;
        }
        final String first = args[0];
        final KCommand subCommand = this.subCommands.get(first.toLowerCase());
        if (subCommand != null) {
            final String[] newArgs = this.getNewArgs(args);
            return subCommand.getTabs(user, newArgs, newPrevious, defaultTabIsNull);
        }
        final String previous = previousArgs.length > 0 ? previousArgs[previousArgs.length - 1] : "";
        for (KCommand command : this.subCommands.values()) {
            if (!command.name.equalsIgnoreCase(previous) && command.name.startsWith(first.toLowerCase())) {
                tabs.add(command.name);
            }
        }
        return tabs;
    }

    private String[] getNewArgs(String[] original) {
        final String[] newArgs = new String[original.length - 1];
        System.arraycopy(original, 1, newArgs, 0, newArgs.length);
        return newArgs;
    }

    private String[] getPreviousArgs(String[] original, String[] previous) {
        final String[] newArgs = new String[previous.length + 1];
        System.arraycopy(previous, 0, newArgs, 0, previous.length);
        if (original.length == 0) return newArgs;
        newArgs[newArgs.length - 1] = original[0];
        return newArgs;
    }

}
