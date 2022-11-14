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

package io.github.fisher2911.kingdoms.command.kingdom.chat;

import io.github.fisher2911.fisherlib.command.CommandSenderType;
import io.github.fisher2911.fisherlib.util.EnumUtil;
import io.github.fisher2911.kingdoms.Kingdoms;
import io.github.fisher2911.kingdoms.chat.ChatChannel;
import io.github.fisher2911.kingdoms.command.KCommand;
import io.github.fisher2911.kingdoms.message.KMessage;
import io.github.fisher2911.kingdoms.user.User;
import io.github.fisher2911.kingdoms.user.UserManager;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ChatCommand extends KCommand {

    private final UserManager userManager;

    public ChatCommand(Kingdoms plugin, @Nullable KCommand parent, Map<String, KCommand> subCommands) {
        super(plugin, parent, "chat", "<channel>", null, CommandSenderType.PLAYER, 1, 1, subCommands);
        this.userManager = this.plugin.getUserManager();
    }

    @Override
    public void execute(User user, String[] args, String[] previousArgs) {
        final ChatChannel chatChannel = EnumUtil.valueOf(ChatChannel.class, args[0].toUpperCase(Locale.ROOT));
        if (chatChannel == null) {
            this.messageHandler.sendMessage(user, KMessage.CHAT_CHANNEL_NOT_FOUND);
            return;
        }
        this.userManager.changeChatChannel(user, chatChannel);
    }

    @Override
    public @Nullable List<String> getTabs(User user, String[] args, String[] previousArgs, boolean defaultTabIsNull) {
        List<String> tabs = super.getTabs(user, args, previousArgs, defaultTabIsNull);
        if (tabs == null) tabs = new ArrayList<>();
        if (args.length != 1) return tabs;
        final String arg = args[0];
        for (ChatChannel chatChannel : ChatChannel.values()) {
            final String channel = chatChannel.name().toLowerCase(Locale.ROOT);
            if (channel.startsWith(arg)) {
                tabs.add(channel);
            }
        }
        return tabs;
    }
}
