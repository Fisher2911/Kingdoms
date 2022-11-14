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

package io.github.fisher2911.kingdoms.user;

import io.github.fisher2911.fisherlib.message.MessageHandler;
import io.github.fisher2911.fisherlib.task.TaskChain;
import io.github.fisher2911.fisherlib.user.CoreUserManager;
import io.github.fisher2911.kingdoms.Kingdoms;
import io.github.fisher2911.kingdoms.api.event.user.UserLoadEvent;
import io.github.fisher2911.kingdoms.chat.ChatChannel;
import io.github.fisher2911.kingdoms.data.DataManager;
import io.github.fisher2911.kingdoms.message.KMessage;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class UserManager implements CoreUserManager<User> {

    private final Kingdoms plugin;
    private final MessageHandler messageHandler;
    private final DataManager dataManager;
    private final Map<UUID, User> userMap;
    private final Map<String, User> byName;

    public UserManager(Kingdoms plugin, Map<UUID, User> userMap) {
        this.plugin = plugin;
        this.messageHandler = plugin.getMessageHandler();
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
                    Bukkit.getPluginManager().callEvent(new UserLoadEvent(user));
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
            this.messageHandler.sendMessage(user, KMessage.MUST_BE_IN_KINGDOM_TO_CHANGE_CHAT);
            return;
        }
        user.setChatChannel(channel);
        this.messageHandler.sendMessage(user, KMessage.CHAT_CHANNEL_CHANGED, channel);
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
        TaskChain.create(this.plugin)
                .runAsync(() -> this.dataManager.saveUser(user))
                .execute();
    }

    @Nullable
    public User removeUser(UUID uuid) {
        final User user = this.userMap.remove(uuid);
        if (user == null) return null;
        Bukkit.getPluginManager().callEvent(new UserLoadEvent(user));
        this.byName.remove(user.getName());
        return user;
    }

    public void saveDirty() {
        this.userMap.values().stream()
                .filter(User::isDirty)
                .forEach(this.dataManager::saveUser);
    }

}
