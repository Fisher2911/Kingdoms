package io.github.fisher2911.kingdoms.command.kingdom.role;

import io.github.fisher2911.kingdoms.Kingdoms;
import io.github.fisher2911.kingdoms.command.CommandSenderType;
import io.github.fisher2911.kingdoms.command.KCommand;
import io.github.fisher2911.kingdoms.kingdom.KingdomManager;
import io.github.fisher2911.kingdoms.kingdom.role.Role;
import io.github.fisher2911.kingdoms.kingdom.role.RoleManager;
import io.github.fisher2911.kingdoms.message.MessageHandler;
import io.github.fisher2911.kingdoms.user.User;
import io.github.fisher2911.kingdoms.user.UserManager;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class SetRoleCommand extends KCommand {

    private final KingdomManager kingdomManager;
    private final RoleManager roleManager;
    private final UserManager userManager;

    public SetRoleCommand(Kingdoms plugin, Map<String, KCommand> subCommands) {
        super(plugin, "setrole", null, CommandSenderType.PLAYER, 2, 2, subCommands);
        this.kingdomManager = this.plugin.getKingdomManager();
        this.roleManager = this.plugin.getRoleManager();
        this.userManager = this.plugin.getUserManager();
    }

    @Override
    public void execute(User user, String[] args, String[] previousArgs) {
        final Role role = this.roleManager.getById(args[0]);
        final User toSet = this.userManager.getUserByName(args[1]);
        this.kingdomManager.trySetRole(user, toSet, role);
    }

    @Override
    public void sendHelp(User user, String[] args, String[] previousArgs) {
        MessageHandler.sendMessage(user, "/k setrole <role> <player>");
    }

    @Override
    public @Nullable List<String> getTabs(User user, String[] args, String[] previousArgs, boolean defaultTabIsNull) {
        if (args.length != 1) return Collections.emptyList();
        final String arg = args[0];
        final List<String> tabs = new ArrayList<>();
        for (String role : this.roleManager.getAllRoleIds()) {
            if (role.startsWith(arg)) tabs.add(role);
        }
        return tabs;
    }
}
