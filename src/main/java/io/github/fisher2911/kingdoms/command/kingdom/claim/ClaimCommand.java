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

package io.github.fisher2911.kingdoms.command.kingdom.claim;

import io.github.fisher2911.fisherlib.command.CommandSenderType;
import io.github.fisher2911.fisherlib.task.TaskChain;
import io.github.fisher2911.kingdoms.Kingdoms;
import io.github.fisher2911.kingdoms.command.KCommand;
import io.github.fisher2911.kingdoms.kingdom.KingdomManager;
import io.github.fisher2911.kingdoms.kingdom.claim.ClaimManager;
import io.github.fisher2911.kingdoms.message.KMessage;
import io.github.fisher2911.kingdoms.user.User;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class ClaimCommand extends KCommand {

    private final KingdomManager kingdomManager;
    private final ClaimManager claimManager;

    public ClaimCommand(Kingdoms plugin, @Nullable KCommand parent, Map<String, KCommand> subCommands) {
        super(plugin, parent, "claim", null, CommandSenderType.PLAYER, 0, 2, subCommands);
        this.kingdomManager = this.plugin.getKingdomManager();
        this.claimManager = this.plugin.getClaimManager();
        this.addSubCommand(new AutoSubCommand(this.plugin, this, new HashMap<>()), true);
    }

    @Override
    public void execute(User user, String[] args, String[] previous) {
        if (!user.hasKingdom()) {
            this.messageHandler.sendMessage(user, KMessage.NOT_IN_KINGDOM);
            return;
        }
        TaskChain.create(this.plugin)
                .supplyAsync(() -> this.kingdomManager.getKingdom(user.getKingdomId(), true))
                .consumeSync(opt -> opt.ifPresentOrElse(kingdom -> {
                    final Location location = user.getPlayer().getLocation();
                    final Chunk chunk = location.getChunk();
                    this.claimManager.tryClaim(user, kingdom, chunk);
                }, () -> this.messageHandler.sendMessage(user, KMessage.KINGDOM_NOT_FOUND)))
                .execute();
    }

}
