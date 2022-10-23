package io.github.fisher2911.kingdoms.command.kingdom.permission;

import io.github.fisher2911.kingdoms.Kingdoms;
import io.github.fisher2911.kingdoms.command.CommandSenderType;
import io.github.fisher2911.kingdoms.command.KCommand;
import io.github.fisher2911.kingdoms.gui.impl.PermissionGui;
import io.github.fisher2911.kingdoms.kingdom.KingdomManager;
import io.github.fisher2911.kingdoms.kingdom.role.Role;
import io.github.fisher2911.kingdoms.kingdom.role.RoleManager;
import io.github.fisher2911.kingdoms.message.Message;
import io.github.fisher2911.kingdoms.message.MessageHandler;
import io.github.fisher2911.kingdoms.user.User;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EditPermissionsCommand extends KCommand {

    private final RoleManager roleManager;
    private final KingdomManager kingdomManager;

    public EditPermissionsCommand(Kingdoms plugin, Map<String, KCommand> subCommands) {
        super(plugin, "edit", null, CommandSenderType.PLAYER, 1, -1, subCommands);
        this.roleManager = this.plugin.getRoleManager();
        this.kingdomManager = this.plugin.getKingdomManager();
        this.addSubCommand(new EditChunkPermissionsCommand(this.plugin, new HashMap<>()));
    }

    @Override
    public void execute(User user, String[] args, String[] previous) {
        if (!user.hasKingdom()) {
            MessageHandler.sendMessage(user, Message.NOT_IN_KINGDOM);
            return;
        }
        final Role role = this.roleManager.getById(args[0]);
        if (role == null) {
            MessageHandler.sendMessage(user, Message.NO_ROLE_FOUND);
            return;
        }
        this.kingdomManager.getKingdom(user.getKingdomId()).ifPresentOrElse(kingdom -> {
            PermissionGui.create(
                    this.plugin,
                    role,
                    kingdom,
                    null
            ).open(user.getPlayer());
        }, () -> MessageHandler.sendMessage(user, "Could not find kingdom"));
    }

    @Override
    public void sendHelp(User user, String[] args, String[] previous) {
        MessageHandler.sendMessage(user, "/k permission edit [role]");
    }

    @Override
    public @Nullable List<String> getTabs(User user, String[] args, String[] previousArgs) {
        List<String> tabs = super.getTabs(user, args, previousArgs);
        if (tabs == null) tabs = new ArrayList<>();
        final String lastArg = previousArgs[previousArgs.length - 1];
        if (!lastArg.equalsIgnoreCase("edit")) return tabs;
        if (args.length == 0) return tabs;
        final String arg = args[0];
        for (String role : this.roleManager.getAllRoleIds()) {
            if (!role.equals(this.roleManager.getLeaderRole().id()) && role.startsWith(arg)) tabs.add(role);
        }
        return tabs;
    }
}
