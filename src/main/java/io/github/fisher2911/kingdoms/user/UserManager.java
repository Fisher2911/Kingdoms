package io.github.fisher2911.kingdoms.user;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.UUID;

public class UserManager {

    private final Map<UUID, User> userMap;

    public UserManager(Map<UUID, User> userMap) {
        this.userMap = userMap;
    }

    // todo
    public User wrap(CommandSender sender) {
        if (sender instanceof ConsoleUser) return User.CONSOLE;
        if (sender instanceof final Player player) {
            final UUID uuid = player.getUniqueId();
            return this.userMap.getOrDefault(uuid, new BukkitUser(player.getUniqueId(), player.getName(), player));
        }
        return null;
    }

    public User getUserByName(String name) {
        return this.wrap(Bukkit.getPlayer(name));
    }

    public void addUser(User user) {
        this.userMap.put(user.getId(), user);
    }

    @Nullable
    public User removeUser(UUID uuid) {
        return this.userMap.remove(uuid);
    }

}
