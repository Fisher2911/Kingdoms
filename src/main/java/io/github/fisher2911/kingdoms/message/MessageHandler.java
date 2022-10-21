package io.github.fisher2911.kingdoms.message;

import io.github.fisher2911.kingdoms.user.User;
import org.bukkit.Bukkit;

import java.util.EnumMap;
import java.util.Map;

public class MessageHandler {

    private final Map<Message, String> messages = new EnumMap<>(Message.class);

    public static void sendMessage(User user, Message message) {
        // todo
        Bukkit.broadcastMessage(user.getName() + " -> " + message);
    }

    public static void sendMessage(User user, String message) {
        // todo
        Bukkit.broadcastMessage(user.getName() + " -> " + message);
    }

}
