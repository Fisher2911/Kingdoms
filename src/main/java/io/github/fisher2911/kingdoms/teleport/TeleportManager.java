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

package io.github.fisher2911.kingdoms.teleport;

import io.github.fisher2911.kingdoms.Kingdoms;
import io.github.fisher2911.kingdoms.api.event.kingdom.KingdomMemberTeleportEvent;
import io.github.fisher2911.kingdoms.message.Message;
import io.github.fisher2911.kingdoms.message.MessageHandler;
import io.github.fisher2911.kingdoms.user.User;
import io.github.fisher2911.kingdoms.world.WorldPosition;
import org.bukkit.Bukkit;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TeleportManager {

    private final Kingdoms plugin;
    private final Map<UUID, TeleportInfo> teleporting = new HashMap<>();

    public TeleportManager(Kingdoms plugin) {
        this.plugin = plugin;
    }

    public void tryTeleport(TeleportInfo info) {
        final UUID uuid = info.getUser().getId();
        this.teleporting.put(uuid, info);
        this.doTeleportTask(uuid);
    }

    private void doTeleportTask(UUID uuid) {
        Bukkit.getScheduler().runTaskLater(this.plugin, () -> {
            final TeleportInfo info = this.teleporting.get(uuid);
            if (info == null) return;
            final User user = info.getUser();
            if (!user.isOnline()) return;
            if (info.getSecondsLeft() <= 0) {
                final KingdomMemberTeleportEvent event = new KingdomMemberTeleportEvent(
                        user,
                        info.getTo(),
                        info.getPositionId()
                );
                Bukkit.getPluginManager().callEvent(event);
                if (event.isCancelled()) {
                    this.teleporting.remove(uuid);
                    return;
                }
                user.getPlayer().teleport(info.getTo().toLocation());
                this.teleporting.remove(uuid);
                MessageHandler.sendMessage(user, Message.TELEPORT_SUCCESS);
                return;
            }
            final WorldPosition lastPosition = user.getPosition();
            if (lastPosition == null) return;
            if (!lastPosition.isSameBlock(info.getStartPosition())) {
                MessageHandler.sendMessage(user, Message.TELEPORT_CANCELLED_MOVEMENT);
                this.teleporting.remove(uuid);
                return;
            }
            info.decSeconds();
            MessageHandler.sendMessage(user, Message.TELEPORT_COUNTDOWN, info);
            this.doTeleportTask(uuid);
        }, 20);
    }

}
