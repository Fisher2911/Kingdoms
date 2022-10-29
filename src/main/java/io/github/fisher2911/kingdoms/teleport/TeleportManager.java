package io.github.fisher2911.kingdoms.teleport;

import io.github.fisher2911.kingdoms.Kingdoms;
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
