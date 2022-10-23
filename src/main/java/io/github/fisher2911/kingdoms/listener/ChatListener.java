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
        final User user = this.userManager.wrap(event.getPlayer());
        final ChatChannel chatChannel = user.getChatChannel();
        this.kingdomManager.getKingdom(user.getKingdomId()).
                ifPresent(kingdom -> event.getRecipients().
                        removeIf(player -> !chatChannel.canSeeChat(kingdom, this.userManager.wrap(player)))
                );
    }
}
