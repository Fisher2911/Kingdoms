package io.github.fisher2911.kingdoms.command.kingdom.chat;

import io.github.fisher2911.kingdoms.Kingdoms;
import io.github.fisher2911.kingdoms.chat.ChatChannel;
import io.github.fisher2911.kingdoms.command.CommandSenderType;
import io.github.fisher2911.kingdoms.command.KCommand;
import io.github.fisher2911.kingdoms.message.Message;
import io.github.fisher2911.kingdoms.message.MessageHandler;
import io.github.fisher2911.kingdoms.user.User;
import io.github.fisher2911.kingdoms.user.UserManager;
import io.github.fisher2911.kingdoms.util.EnumUtil;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ChatCommand extends KCommand {

    private final UserManager userManager;

    public ChatCommand(Kingdoms plugin, Map<String, KCommand> subCommands) {
        super(plugin, "chat", null, CommandSenderType.PLAYER, 1, 1, subCommands);
        this.userManager = this.plugin.getUserManager();
    }

    @Override
    public void execute(User user, String[] args, String[] previousArgs) {
        final ChatChannel chatChannel = EnumUtil.valueOf(ChatChannel.class, args[0].toUpperCase(Locale.ROOT));
        if (chatChannel == null) {
            MessageHandler.sendMessage(user, Message.CHAT_CHANNEL_NOT_FOUND);
            return;
        }
        this.userManager.changeChatChannel(user, chatChannel);
    }

    @Override
    public void sendHelp(User user, String[] args, String[] previousArgs) {
        MessageHandler.sendMessage(user, "/k chat <channel>");
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
