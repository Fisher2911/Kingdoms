package io.github.fisher2911.kingdoms.user;

import io.github.fisher2911.kingdoms.Kingdoms;
import io.github.fisher2911.kingdoms.chat.ChatChannel;
import io.github.fisher2911.kingdoms.data.DataManager;
import io.github.fisher2911.kingdoms.message.Message;
import io.github.fisher2911.kingdoms.message.MessageHandler;
import io.github.fisher2911.kingdoms.task.TaskChain;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class UserManager {

    private final Kingdoms plugin;
    private final DataManager dataManager;
    private final Map<UUID, User> userMap;
    private final Map<String, User> byName;

    public UserManager(Kingdoms plugin, Map<UUID, User> userMap) {
        this.plugin = plugin;
        this.dataManager = this.plugin.getDataManager();
        this.userMap = userMap;
        this.byName = new HashMap<>();
    }

    public Optional<User> get(UUID uuid) {
        return Optional.ofNullable(this.userMap.get(uuid));
    }

    @Nullable
    public User forceGet(CommandSender sender) {
        if (sender instanceof ConsoleCommandSender) return User.CONSOLE;
        if (sender instanceof final Player player) {
            final UUID uuid = player.getUniqueId();
            return this.forceGet(uuid);
        }
        return null;
    }

    @Nullable
    public User forceGet(UUID uuid) {
        return this.userMap.get(uuid);
    }

    public void loadUser(Player player) {
        TaskChain.create(this.plugin)
                .supplyAsync(() -> {
                    final User current = this.userMap.get(player.getUniqueId());
                    if (current != null) return current;
                    final Optional<User> user = this.dataManager.loadUser(player.getUniqueId());
                    if (user.isPresent()) {
                        return user.get();
                    }
                    final User newUser = this.createUser(player);
                    this.dataManager.saveUser(newUser);
                    return newUser;
                })
                .sync(user -> {
                    Bukkit.broadcastMessage("Adding user: " + user);
                    this.addUser(user);
                    user.onJoin(player);
                    return user.getKingdomId();
                })
                .consumeAsync(kingdomId -> this.plugin.getKingdomManager().getKingdom(kingdomId, true))
                .execute();
    }


    private User createUser(Player player) {
        return new BukkitUser(this.plugin, player.getUniqueId(), player.getName(), player);
    }

    public void changeChatChannel(User user, ChatChannel channel) {
        if (!user.hasKingdom()) {
            MessageHandler.sendMessage(user, Message.MUST_BE_IN_KINGDOM_TO_CHANGE_CHAT);
            return;
        }
        user.setChatChannel(channel);
        MessageHandler.sendMessage(user, Message.CHAT_CHANNEL_CHANGED, channel);
    }

    public Optional<User> getUserByName(String name, boolean searchDatabase) {
        final User user = this.byName.get(name);
        if (user != null || !searchDatabase) return Optional.ofNullable(user);
        return this.dataManager.loadUserByName(name);
    }

    public void addUser(User user) {
        this.userMap.put(user.getId(), user);
        this.byName.put(user.getName(), user);
    }

    public void saveAndRemove(UUID uuid) {
        final User user = this.removeUser(uuid);
        if (user == null) return;
        this.byName.remove(user.getName());
        TaskChain.create(this.plugin)
                .runAsync(() -> this.dataManager.saveUser(user))
                .execute();
    }

    @Nullable
    public User removeUser(UUID uuid) {
        final User user = this.userMap.remove(uuid);
        if (user == null) return null;
        this.byName.remove(user.getName());
        return user;
    }

    public void saveDirty() {
        this.userMap.values().stream()
                .filter(User::isDirty)
                .forEach(this.dataManager::saveUser);
    }

}
