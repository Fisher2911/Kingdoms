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

package io.github.fisher2911.kingdoms.command.kingdom.invite;

import io.github.fisher2911.fisherlib.command.CommandSenderType;
import io.github.fisher2911.fisherlib.task.TaskChain;
import io.github.fisher2911.kingdoms.Kingdoms;
import io.github.fisher2911.kingdoms.command.KCommand;
import io.github.fisher2911.kingdoms.kingdom.KingdomManager;
import io.github.fisher2911.kingdoms.kingdom.invite.InviteManager;
import io.github.fisher2911.kingdoms.message.KMessage;
import io.github.fisher2911.kingdoms.user.User;
import io.github.fisher2911.kingdoms.user.UserManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class InviteCommand extends KCommand {

    private final KingdomManager kingdomManager;
    private final InviteManager inviteManager;
    private final UserManager userManager;

    public InviteCommand(Kingdoms plugin, @Nullable KCommand parent, Map<String, KCommand> subCommands) {
        super(plugin, parent, "invite", "<name>", null, CommandSenderType.PLAYER, 1, 1, subCommands);
        this.kingdomManager = this.plugin.getKingdomManager();
        this.inviteManager = this.plugin.getInviteManager();
        this.userManager = this.plugin.getUserManager();
    }

    @Override
    public void execute(User user, String[] args, String[] previousArgs) {
        final String invitedName = args[0];
        this.userManager.getUserByName(invitedName, false)
                .ifPresentOrElse(invited -> TaskChain.create(this.plugin)
                                .supplyAsync(() -> this.kingdomManager.getKingdom(user.getKingdomId(), true))
                                .consumeSync(opt -> opt.ifPresentOrElse(kingdom -> this.inviteManager.invite(kingdom, user, invited),
                                        () -> this.messageHandler.sendMessage(user, KMessage.NOT_IN_KINGDOM)
                                ))
                                .execute(),
                        () -> this.messageHandler.sendMessage(user, KMessage.PLAYER_NOT_FOUND));
    }

    @Override
    public @Nullable List<String> getTabs(User user, String[] args, String[] previousArgs, boolean defaultTabIsNull) {
        List<String> tabs = super.getTabs(user, args, previousArgs, true);
        if (tabs == null) tabs = new ArrayList<>();
        if (args.length != 1) return tabs;
        final String arg = args[0];
        for (Player player : Bukkit.getOnlinePlayers()) {
            final String playerName = player.getName();
            if (playerName.startsWith(arg)) tabs.add(playerName);
        }
        return tabs;
    }
}
