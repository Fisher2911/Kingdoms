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

import io.github.fisher2911.kingdoms.Kingdoms;
import io.github.fisher2911.kingdoms.chat.ChatChannel;
import io.github.fisher2911.kingdoms.kingdom.KingdomManager;
import io.github.fisher2911.kingdoms.user.User;
import io.github.fisher2911.kingdoms.user.UserManager;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class ChatListener extends KListener {

    private final Kingdoms plugin;
    private final KingdomManager kingdomManager;
    private final UserManager userManager;

    public ChatListener(Kingdoms plugin) {
        super(plugin.getGlobalListener());
        this.plugin = plugin;
        this.kingdomManager = plugin.getKingdomManager();
        this.userManager = this.plugin.getUserManager();
    }

    @Override
    public void init() {
        this.globalListener.register(AsyncPlayerChatEvent.class, this::onChat);
    }

    public void onChat(AsyncPlayerChatEvent event) {
        final User user = this.userManager.forceGet(event.getPlayer());
        if (user == null) return;
        final ChatChannel chatChannel = user.getChatChannel();
        this.kingdomManager.getKingdom(user.getKingdomId(), false).
                ifPresent(kingdom -> event.getRecipients().
                        removeIf(player -> {
                            final User viewer = this.userManager.forceGet(player);
                            if (viewer == null) return false;
                            return !chatChannel.canSeeChat(kingdom, viewer);
                        })
                );
    }
}
