package io.github.fisher2911.kingdoms.command.kingdom.role;

import io.github.fisher2911.kingdoms.Kingdoms;
import io.github.fisher2911.kingdoms.command.CommandSenderType;
import io.github.fisher2911.kingdoms.command.KCommand;
import io.github.fisher2911.kingdoms.kingdom.KingdomManager;
import io.github.fisher2911.kingdoms.kingdom.role.RoleManager;
import io.github.fisher2911.kingdoms.message.Message;
import io.github.fisher2911.kingdoms.message.MessageHandler;
import io.github.fisher2911.kingdoms.task.TaskChain;
import io.github.fisher2911.kingdoms.user.User;
import io.github.fisher2911.kingdoms.user.UserManager;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SetRoleCommand extends KCommand {

    private final KingdomManager kingdomManager;
    private final RoleManager roleManager;
    private final UserManager userManager;

    public SetRoleCommand(Kingdoms plugin, @Nullable KCommand parent, Map<String, KCommand> subCommands) {
        super(plugin, parent, "setrole", "<role> <player>", null, CommandSenderType.PLAYER, 2, 2, subCommands);
        this.kingdomManager = this.plugin.getKingdomManager();
        this.roleManager = this.plugin.getRoleManager();
        this.userManager = this.plugin.getUserManager();
    }

    @Override
    public void execute(User user, String[] args, String[] previousArgs) {
        final String roleId = args[0];
        final String name = args[1];
        TaskChain.create(this.plugin).
                supplyAsync(() -> this.userManager.getUserByName(name, true))
                .consumeAsync(opt -> opt.ifPresentOrElse(toSet -> this.kingdomManager.trySetRole(user, toSet, roleId, true),
                        () -> MessageHandler.sendMessage(user, Message.NOT_IN_KINGDOM)))
                .execute();
    }

    @Override
    public @Nullable List<String> getTabs(User user, String[] args, String[] previousArgs, boolean defaultTabIsNull) {
        if (args.length != 1) return Collections.emptyList();
        final String arg = args[0];
        final List<String> tabs = new ArrayList<>();
        return this.kingdomManager.getKingdom(user.getKingdomId(), false)
                .map(kingdom -> kingdom.getRoles().keySet()
                        .stream()
                        .filter(role -> !RoleManager.UNSETTABLE_ROLES.contains(role))
                        .filter(roleId -> roleId.startsWith(arg))
                        .collect(Collectors.toList())).
                orElse(tabs);
    }

}
