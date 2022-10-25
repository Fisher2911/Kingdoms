package io.github.fisher2911.kingdoms.message;

import io.github.fisher2911.kingdoms.Kingdoms;
import io.github.fisher2911.kingdoms.config.Config;
import io.github.fisher2911.kingdoms.kingdom.Kingdom;
import io.github.fisher2911.kingdoms.placeholder.PlaceholderBuilder;
import io.github.fisher2911.kingdoms.user.User;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.yaml.NodeStyle;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import java.io.File;
import java.io.IOException;
import java.util.EnumMap;
import java.util.Map;

public class MessageHandler extends Config {

    public static final MiniMessage MINI_MESSAGE = MiniMessage.miniMessage();
    public static final LegacyComponentSerializer LEGACY_COMPONENT_SERIALIZER = LegacyComponentSerializer.builder()
            .hexColors()
            .useUnusualXRepeatedCharacterHexFormat()
            .build();
    private final Map<Message, String> messages = new EnumMap<>(Message.class);
    private final BukkitAudiences audiences;

    private MessageHandler(Kingdoms plugin, String... path) {
        super(plugin, path);
        this.audiences = BukkitAudiences.create(this.plugin);
    }

    private static final MessageHandler INSTANCE;

    static {
        INSTANCE = new MessageHandler(Kingdoms.getPlugin(Kingdoms.class), "messages.yml");
    }

    public static String serialize(String s) {
        try {
            return LEGACY_COMPONENT_SERIALIZER.serialize(MINI_MESSAGE.deserialize(s));
        } catch (final Exception e) {
            throw new RuntimeException();
        }
    }

    public static void sendMessage(User user, Message message) {
        final String value = INSTANCE.getMessage(message);
        if (value.isBlank()) return;
        sendMessage(user, MINI_MESSAGE.deserialize(value));
    }

    public static void sendMessage(Kingdom kingdom, Message message) {
        final String value = INSTANCE.getMessage(message);
        if (value.isBlank()) return;
        sendToAll(kingdom, MINI_MESSAGE.deserialize(value));
    }

    public static void sendMessage(User user, Message message, Object... placeholders) {
        final String value = INSTANCE.getMessage(message);
        if (value.isBlank()) return;
        sendMessage(user, MINI_MESSAGE.deserialize(PlaceholderBuilder.apply(value, placeholders)));
    }

    public static void sendMessage(Kingdom kingdom, Message message, Object... placeholders) {
        final String value = INSTANCE.getMessage(message);
        if (value.isBlank()) return;
        sendToAll(kingdom, MINI_MESSAGE.deserialize(PlaceholderBuilder.apply(value, placeholders)));
    }

    public static void sendMessage(User user, String message) {
        sendMessage(user, MINI_MESSAGE.deserialize(message));
    }

    public static void sendMessage(Kingdom kingdom, String message, Object... placeholders) {
        sendToAll(kingdom, MINI_MESSAGE.deserialize(PlaceholderBuilder.apply(message, placeholders)));
    }

    public static void sendMessage(Kingdom kingdom, String message) {
        sendToAll(kingdom, MINI_MESSAGE.deserialize(message));
    }

    public static void sendToAll(Kingdom kingdom, Component component) {
        for (User user : kingdom.getMembers()) {
            sendMessage(user, component);
        }
    }

    public static void sendMessage(User user, String message, Object... placeholders) {
        sendMessage(user, MINI_MESSAGE.deserialize(PlaceholderBuilder.apply(message, placeholders)));
    }

    public static void sendMessage(User user, Component component) {
        if (!user.isOnline()) return;
        final Audience audience = INSTANCE.audiences.player(user.getPlayer());
        audience.sendMessage(component);
    }

    public static void sendNotInKingdom(User user) {
        sendMessage(user, Message.NOT_IN_KINGDOM);
    }

    private String getMessage(Message message) {
        return this.messages.get(message);
    }

    public static void reload() {
        MessageHandler.INSTANCE.messages.clear();
        load();
    }

    public static void load() {
        INSTANCE.loadMessages();
    }

    private void loadMessages() {
        final File file = this.path.toFile();
        final boolean exists = file.exists();
        if (!exists) {
            file.getParentFile().mkdirs();
            this.plugin.saveResource(this.path.getFileName().toString(), false);
        }
        final YamlConfigurationLoader loader = YamlConfigurationLoader.
                builder().
                path(this.path).nodeStyle(NodeStyle.BLOCK).
                defaultOptions(opts ->
                        opts.serializers(build -> {
                        }))
                .build();
        try {
            final ConfigurationNode source = loader.load();
            for (Message message : Message.values()) {
                final String messagePath = message.getConfigPath();
                if (!source.hasChild(messagePath)) {
                    source.node(messagePath).set(message.toString());
                    this.messages.put(message, message.toString());
                    continue;
                }
                this.messages.put(message, source.node(messagePath).getString(""));
            }
            loader.save(source);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
