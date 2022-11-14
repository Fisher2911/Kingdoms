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

import io.github.fisher2911.fisherlib.user.CoreConsoleUser;
import io.github.fisher2911.kingdoms.chat.ChatChannel;
import io.github.fisher2911.kingdoms.command.CommandPermission;
import io.github.fisher2911.fisherlib.world.WorldPosition;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.Map;
import java.util.UUID;

public class ConsoleUser implements CoreConsoleUser, User {

    public static final UUID CONSOLE_ID = UUID.randomUUID();

    protected ConsoleUser() {}

    @Override
    public UUID getId() {
        return CONSOLE_ID;
    }

    @Override
    public String getName() {
        return "console";
    }

    @Nullable
    @Override
    public Player getPlayer() {
        return null;
    }

    @Nullable
    @Override
    public OfflinePlayer getOfflinePlayer() {
        return null;
    }

    @Override
    public boolean isOnline() {
        return false;
    }

    @Override
    public void takeMoney(double amount) {

    }

    @Override
    public double getMoney() {
        return -1;
    }

    @Override
    public void addMoney(double amount) {

    }

    @Override
    public int getKingdomId() {
        return -1;
    }

    @Override
    public void setKingdomId(int id) {

    }

    @Override
    public boolean hasPermission(CommandPermission permission) {
        return true;
    }

    @Override
    public Map<Integer, ItemStack> getInventory() {
        return Collections.emptyMap();
    }

    @Override
    public boolean hasKingdom() {
        return false;
    }

    public void sendMessage(String message) {
        Bukkit.getConsoleSender().sendMessage(message);
    }

    @Override
    public ChatChannel getChatChannel() {
        return ChatChannel.GLOBAL;
    }

    @Override
    public void setChatChannel(ChatChannel chatChannel) {

    }

    @Override
    @Nullable
    public WorldPosition getPosition() {
        return null;
    }

    @Override
    public void onJoin(Player player) {

    }

    @Override
    public void onQuit() {

    }

    @Override
    public boolean isDirty() {
        return false;
    }

    @Override
    public void setDirty(boolean dirty) {

    }

    @Override
    public boolean hasPermission(String permission) {
        return true;
    }
}
