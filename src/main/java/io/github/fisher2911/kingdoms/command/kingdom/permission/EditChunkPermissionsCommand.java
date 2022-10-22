package io.github.fisher2911.kingdoms.command.kingdom.permission;

import io.github.fisher2911.kingdoms.Kingdoms;
import io.github.fisher2911.kingdoms.command.CommandSenderType;
import io.github.fisher2911.kingdoms.command.KCommand;
import io.github.fisher2911.kingdoms.gui.impl.PermissionGui;
import io.github.fisher2911.kingdoms.kingdom.ClaimedChunk;
import io.github.fisher2911.kingdoms.kingdom.KingdomManager;
import io.github.fisher2911.kingdoms.kingdom.WorldManager;
import io.github.fisher2911.kingdoms.kingdom.role.Role;
import io.github.fisher2911.kingdoms.kingdom.role.RoleManager;
import io.github.fisher2911.kingdoms.message.Message;
import io.github.fisher2911.kingdoms.message.MessageHandler;
import io.github.fisher2911.kingdoms.user.User;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class EditChunkPermissionsCommand extends KCommand {

    private final RoleManager roleManager;
    private final KingdomManager kingdomManager;
    private final WorldManager worldManager;

    public EditChunkPermissionsCommand(Kingdoms plugin, Map<String, KCommand> subCommands) {
        super(plugin, "chunk", null, CommandSenderType.PLAYER, 1, -1, subCommands);
        this.roleManager = this.plugin.getRoleManager();
        this.kingdomManager = this.plugin.getKingdomManager();
        this.worldManager = this.plugin.getWorldManager();
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
        final Player player = user.getPlayer();
        final ClaimedChunk chunk = this.worldManager.getAt(player.getLocation());
        if (chunk.isWilderness()) {
            MessageHandler.sendMessage(user, Message.NOT_CLAIMED_BY_KINGDOM);
            return;
        }
        this.kingdomManager.getKingdom(user.getKingdomId()).ifPresentOrElse(kingdom -> {
            PermissionGui.create(
                    this.plugin,
                    role,
                    kingdom,
                    chunk
            ).open(player);
        }, () -> MessageHandler.sendMessage(user, "Could not find kingdom"));
    }

    @Override
    public void sendHelp(User user, String[] args, String[] previous) {
        MessageHandler.sendMessage(user, "/k permission edit chunk [role]");
    }

    @Override
    public @Nullable List<String> getTabs(String[] args, String[] previousArgs) {
        List<String> tabs = super.getTabs(args, previousArgs);
        if (tabs == null) tabs = new ArrayList<>();
        final String lastArg = previousArgs[previousArgs.length - 1];
        if (!lastArg.equalsIgnoreCase("chunk")) return tabs;
        if (args.length == 0) return tabs;
        final String arg = args[0];
        for (String role : this.roleManager.getAllRoleIds()) {
            if (!role.equals(this.roleManager.getLeader().id()) && role.startsWith(arg)) tabs.add(role);
        }
        return tabs;
    }

}
