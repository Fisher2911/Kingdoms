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

import io.github.fisher2911.kingdoms.chat.ChatChannel;
import io.github.fisher2911.kingdoms.command.CommandPermission;
import io.github.fisher2911.kingdoms.data.Saveable;
import io.github.fisher2911.kingdoms.world.WorldPosition;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.UUID;

public interface User extends Saveable {

    User CONSOLE = new ConsoleUser();

    UUID getId();
    String getName();
    @Nullable
    Player getPlayer();
    @Nullable
    OfflinePlayer getOfflinePlayer();
    boolean isOnline();
    void takeMoney(double amount);
    void addMoney(double amount);
    double getMoney();
    int getKingdomId();
    void setKingdomId(int id);
    boolean hasPermission(CommandPermission permission);
    boolean hasPermission(String permission);
    Map<Integer, ItemStack> getInventory();
    boolean hasKingdom();
    ChatChannel getChatChannel();
    void setChatChannel(ChatChannel chatChannel);
    @Nullable
    WorldPosition getPosition();
    void onJoin(Player player);
    void onQuit();

}
