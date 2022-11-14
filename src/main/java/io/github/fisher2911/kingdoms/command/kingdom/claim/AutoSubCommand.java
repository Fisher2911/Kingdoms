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
import io.github.fisher2911.fisherlib.message.Message;
import io.github.fisher2911.fisherlib.task.TaskChain;
import io.github.fisher2911.kingdoms.Kingdoms;
import io.github.fisher2911.kingdoms.command.KCommand;
import io.github.fisher2911.kingdoms.kingdom.claim.ClaimManager;
import io.github.fisher2911.kingdoms.kingdom.claim.ClaimMode;
import io.github.fisher2911.kingdoms.message.KMessage;
import io.github.fisher2911.kingdoms.user.User;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.UUID;

public class AutoSubCommand extends KCommand {

    private final ClaimManager claimManager;

    public AutoSubCommand(Kingdoms plugin, @Nullable KCommand parent, Map<String, KCommand> subCommands) {
        super(plugin, parent, "auto", null, CommandSenderType.PLAYER, 0, 1, subCommands);
        this.claimManager = this.plugin.getClaimManager();
    }

    @Override
    public void execute(User user, String[] args, String[] previous) {
        if (!user.hasKingdom()) {
            this.messageHandler.sendMessage(user, KMessage.NOT_IN_KINGDOM);
            return;
        }
        final ClaimMode claimMode = ClaimMode.valueOf(previous[0].toUpperCase());
        final UUID uuid = user.getId();
        final ClaimMode previousClaimMode = this.claimManager.getClaimMode(uuid);
        if (previousClaimMode == claimMode) {
            final Message message = claimMode == ClaimMode.CLAIM ? KMessage.DISABLED_AUTO_CLAIM : KMessage.DISABLED_AUTO_UNCLAIM;
            this.claimManager.removePlayerClaimMode(uuid);
            this.messageHandler.sendMessage(user, message);
            return;
        }
        this.claimManager.setClaimMode(uuid, claimMode);
        final Location location = user.getPlayer().getLocation();
        final Message message = claimMode == ClaimMode.CLAIM ? KMessage.ENABLED_AUTO_CLAIM : KMessage.ENABLED_AUTO_UNCLAIM;
        final Chunk chunk = location.getChunk();
        TaskChain.create(this.plugin)
                .runAsync(() -> {
                    if (claimMode == ClaimMode.CLAIM) {
                        this.claimManager.tryClaim(user, chunk, true);
                    } else if (claimMode == ClaimMode.UNCLAIM) {
                        this.claimManager.tryUnClaim(user, chunk, true);
                    }
                    this.messageHandler.sendMessage(user, message);
                })
                .execute();
    }

}
