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

package io.github.fisher2911.kingdoms.listener;

import io.github.fisher2911.fisherlib.listener.CoreListener;
import io.github.fisher2911.fisherlib.task.TaskChain;
import io.github.fisher2911.kingdoms.Kingdoms;
import io.github.fisher2911.kingdoms.data.DataManager;
import io.github.fisher2911.kingdoms.kingdom.Kingdom;
import io.github.fisher2911.kingdoms.user.User;
import io.github.fisher2911.kingdoms.user.UserManager;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerJoinListener extends CoreListener {

    private final Kingdoms plugin;
    private final UserManager userManager;
    private final DataManager dataManager;

    public PlayerJoinListener(Kingdoms plugin) {
        super(plugin);
        this.plugin = plugin;
        this.userManager = this.plugin.getUserManager();
        this.dataManager = this.plugin.getDataManager();
    }

    @Override
    public void init() {
        this.globalListener.register(PlayerJoinEvent.class, this::onJoin);
        this.globalListener.register(PlayerQuitEvent.class, this::onQuit);
    }

    public void onJoin(PlayerJoinEvent event) {
        final Player player = event.getPlayer();
        this.userManager.loadUser(player);
    }

    public void onQuit(PlayerQuitEvent event) {
        final User user = this.userManager.removeUser(event.getPlayer().getUniqueId());
        if (user == null) return;
        user.onQuit();
        TaskChain.create(this.plugin)
                .supplyAsync(() -> {
                    this.dataManager.saveUser(user);
                    if (
                            user.getKingdomId() != Kingdom.WILDERNESS_ID &&
                                    !this.plugin.getKingdomManager().removeIfCanBeUnloaded(user.getKingdomId())
                    ) return null;
                    return user;
                })
                .consumeSync(u -> {
                    if (u == null) return;
                    this.userManager.removeUser(u.getId());
                })
                .execute();
    }

}
