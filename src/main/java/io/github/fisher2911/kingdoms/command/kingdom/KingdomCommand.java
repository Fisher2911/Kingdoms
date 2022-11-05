/*
 *     Kingdoms Plugin
 *     Copyright (C) 2022  Fisher2911
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

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
import io.github.fisher2911.kingdoms.command.kingdom.help.HelpCommand;
import io.github.fisher2911.kingdoms.command.kingdom.info.InfoCommand;
import io.github.fisher2911.kingdoms.command.kingdom.info.NameCommand;
import io.github.fisher2911.kingdoms.command.kingdom.invite.InviteCommand;
import io.github.fisher2911.kingdoms.command.kingdom.invite.JoinCommand;
import io.github.fisher2911.kingdoms.command.kingdom.kick.KickCommand;
import io.github.fisher2911.kingdoms.command.kingdom.leave.LeaveCommand;
import io.github.fisher2911.kingdoms.command.kingdom.map.MapCommand;
import io.github.fisher2911.kingdoms.command.kingdom.permission.PermissionCommand;
import io.github.fisher2911.kingdoms.command.kingdom.relation.RelationCommand;
import io.github.fisher2911.kingdoms.command.kingdom.role.SetRoleCommand;
import io.github.fisher2911.kingdoms.command.kingdom.teleport.HomeCommand;
import io.github.fisher2911.kingdoms.command.kingdom.teleport.SetHomeCommand;
import io.github.fisher2911.kingdoms.command.kingdom.upgrade.UpgradeCommand;
import io.github.fisher2911.kingdoms.gui.GuiManager;
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

    private final GuiManager guiManager;

    public KingdomCommand(Kingdoms plugin, Map<String, KCommand> subCommands) {
        super(plugin, null, "kingdom", null, CommandSenderType.ANY, -1, -1, subCommands);
        this.guiManager = this.plugin.getGuiManager();
        RelationCommand.createAll(this.plugin, this).forEach(this::addSubCommand);
        this.addSubCommand(new CreateCommand(this.plugin, this, new HashMap<>()));
        this.addSubCommand(new ClaimCommand(this.plugin, this, new HashMap<>()));
        this.addSubCommand(new UnclaimCommand(this.plugin, this, new HashMap<>()));
        this.addSubCommand(new PermissionCommand(this.plugin, this, new HashMap<>()));
        this.addSubCommand(new UpgradeCommand(this.plugin, this, new HashMap<>()));
        this.addSubCommand(new InviteCommand(this.plugin, this, new HashMap<>()));
        this.addSubCommand(new JoinCommand(this.plugin, this, new HashMap<>()));
        this.addSubCommand(new AdminCommand(this.plugin, this, new HashMap<>()));
        this.addSubCommand(new InfoCommand(this.plugin, this, new HashMap<>()));
        this.addSubCommand(new KickCommand(this.plugin, this, new HashMap<>()));
        this.addSubCommand(new SetRoleCommand(this.plugin, this, new HashMap<>()));
        this.addSubCommand(new LeaveCommand(this.plugin, this, new HashMap<>()));
        this.addSubCommand(new DisbandCommand(this.plugin, this, new HashMap<>()));
        this.addSubCommand(new ChatCommand(this.plugin, this, new HashMap<>()));
        this.addSubCommand(new BankCommand(this.plugin, this, new HashMap<>()));
        this.addSubCommand(new HomeCommand(this.plugin, this, new HashMap<>()));
        this.addSubCommand(new SetHomeCommand(this.plugin, this, new HashMap<>()));
        this.addSubCommand(new NameCommand(this.plugin, this, new HashMap<>()));
        this.addSubCommand(new MapCommand(this.plugin, this, new HashMap<>()));
        this.addSubCommand(new HelpCommand(this.plugin, this, new HashMap<>()), true);
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
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        final User user = this.plugin.getUserManager().forceGet(sender);
        if (user == null) return null;
        return this.getTabs(user, args, new String[0], false);
    }

}
