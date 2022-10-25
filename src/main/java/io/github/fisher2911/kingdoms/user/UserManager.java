package io.github.fisher2911.kingdoms.user;

import io.github.fisher2911.kingdoms.Kingdoms;
import io.github.fisher2911.kingdoms.chat.ChatChannel;
import io.github.fisher2911.kingdoms.message.Message;
import io.github.fisher2911.kingdoms.message.MessageHandler;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.UUID;

public class UserManager {

    private final Kingdoms plugin;
    private final Map<UUID, User> userMap;

    public UserManager(Kingdoms plugin, Map<UUID, User> userMap) {
        this.plugin = plugin;
        this.userMap = userMap;
    }

    // todo
    public User wrap(CommandSender sender) {
        if (sender instanceof ConsoleCommandSender) return User.CONSOLE;
        if (sender instanceof final Player player) {
            final UUID uuid = player.getUniqueId();
            return this.userMap.getOrDefault(uuid, new BukkitUser(this.plugin, player.getUniqueId(), player.getName(), player));
        }
        return null;
    }

    public void changeChatChannel(User user, ChatChannel channel) {
        if (!user.hasKingdom()) {
            MessageHandler.sendMessage(user, Message.MUST_BE_IN_KINGDOM_TO_CHANGE_CHAT);
            return;
        }
        user.setChatChannel(channel);
        MessageHandler.sendMessage(user, Message.CHAT_CHANNEL_CHANGED, channel);
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
