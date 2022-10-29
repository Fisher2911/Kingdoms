package io.github.fisher2911.kingdoms.command.kingdom;

import io.github.fisher2911.kingdoms.Kingdoms;
import io.github.fisher2911.kingdoms.command.CommandSenderType;
import io.github.fisher2911.kingdoms.command.KCommand;
import io.github.fisher2911.kingdoms.command.kingdom.admin.AdminCommand;
import io.github.fisher2911.kingdoms.command.kingdom.bank.BankCommand;
import io.github.fisher2911.kingdoms.command.kingdom.chat.ChatCommand;
import io.github.fisher2911.kingdoms.command.kingdom.claim.ClaimCommand;
import io.github.fisher2911.kingdoms.command.kingdom.claim.UnclaimCommand;
import io.github.fisher2911.kingdoms.command.kingdom.disband.DisbandCommand;
import io.github.fisher2911.kingdoms.command.kingdom.info.InfoCommand;
import io.github.fisher2911.kingdoms.command.kingdom.invite.InviteCommand;
import io.github.fisher2911.kingdoms.command.kingdom.invite.JoinCommand;
import io.github.fisher2911.kingdoms.command.kingdom.kick.KickCommand;
import io.github.fisher2911.kingdoms.command.kingdom.leave.LeaveCommand;
import io.github.fisher2911.kingdoms.command.kingdom.permission.PermissionCommand;
import io.github.fisher2911.kingdoms.command.kingdom.relation.RelationCommand;
import io.github.fisher2911.kingdoms.command.kingdom.role.SetRoleCommand;
import io.github.fisher2911.kingdoms.command.kingdom.teleport.HomeCommand;
import io.github.fisher2911.kingdoms.command.kingdom.teleport.SetHomeCommand;
import io.github.fisher2911.kingdoms.command.kingdom.upgrade.UpgradeCommand;
import io.github.fisher2911.kingdoms.gui.GuiManager;
import io.github.fisher2911.kingdoms.kingdom.KingdomManager;
import io.github.fisher2911.kingdoms.message.MessageHandler;
import io.github.fisher2911.kingdoms.user.User;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.command.TabExecutor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class KingdomCommand extends KCommand implements TabExecutor, TabCompleter {

    private final KingdomManager kingdomManager;
    private final GuiManager guiManager;

    public KingdomCommand(Kingdoms plugin, Map<String, KCommand> subCommands) {
        super(plugin, "kingdom", null, CommandSenderType.ANY, -1, -1, subCommands);
        this.kingdomManager = plugin.getKingdomManager();
        this.guiManager = this.plugin.getGuiManager();
        this.addSubCommand(new CreateCommand(this.plugin, new HashMap<>()));
        this.addSubCommand(new ClaimCommand(this.plugin, new HashMap<>()));
        this.addSubCommand(new UnclaimCommand(this.plugin, new HashMap<>()));
        this.addSubCommand(new PermissionCommand(this.plugin, new HashMap<>()));
        this.addSubCommand(new UpgradeCommand(this.plugin, new HashMap<>()));
        this.addSubCommand(new InviteCommand(this.plugin, new HashMap<>()));
        this.addSubCommand(new JoinCommand(this.plugin, new HashMap<>()));
        this.addSubCommand(new AdminCommand(this.plugin, new HashMap<>()));
        this.addSubCommand(new InfoCommand(this.plugin, new HashMap<>()));
        this.addSubCommand(new KickCommand(this.plugin, new HashMap<>()));
        this.addSubCommand(new SetRoleCommand(this.plugin, new HashMap<>()));
        this.addSubCommand(new LeaveCommand(this.plugin, new HashMap<>()));
        this.addSubCommand(new DisbandCommand(this.plugin, new HashMap<>()));
        this.addSubCommand(new ChatCommand(this.plugin, new HashMap<>()));
        this.addSubCommand(new BankCommand(this.plugin, new HashMap<>()));
        this.addSubCommand(new HomeCommand(this.plugin, new HashMap<>()));
        this.addSubCommand(new SetHomeCommand(this.plugin, new HashMap<>()));
        RelationCommand.createAll(this.plugin).forEach(this::addSubCommand);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        this.handleArgs(sender, args, new String[0]);
        return true;
    }

    @Override
    public void execute(User user, String[] args, String[] previous) {
        try {
            this.guiManager.open(GuiManager.MAIN_GUI, user);
        } catch (Exception e) {
            e.printStackTrace();
        }
//        this.sendHelp(user, args, previous);
    }

    @Override
    public void sendHelp(User user, String[] args, String[] previous) {
        MessageHandler.sendMessage(user, "/k");
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        final User user = this.plugin.getUserManager().forceGet(sender);
        if (user == null) return null;
        return this.getTabs(user, args, new String[0], false);
    }

}
